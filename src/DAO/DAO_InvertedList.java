package DAO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import models.Billionaire;
import services.CRUD_Hash;

public class DAO_InvertedList {
    private static final String file1 = "src/database/invertedListSource.db";
    private static final String file2 = "src/database/invertedListCountry.db";

    public static void limparLista() {
        try {
            new FileWriter(file1, false).close(); // Sobrescreve com conteúdo vazio
            new FileWriter(file2, false).close();
        } catch (IOException e) {
            System.err.println("Erro ao limpar os arquivos: " + e.getMessage());
        }
    }

    public static void addIL(Billionaire b, int code) {
        int id = b.getId();
        if (code == 1) { // Arquivo da coluna Source
            ArrayList<String> sources = b.getSource();
            try (RandomAccessFile raf = new RandomAccessFile(file1, "rw")) {
                for (String source : sources) {
                    source = source.trim();
                    if (source.isEmpty()) continue;

                    long posicao = read(raf, source);
                    if (posicao == -1) { // Palavra ainda não existe
                        // Palavra nova
                        raf.seek(raf.length());
                        raf.writeUTF(source);
                        raf.writeInt(1);
                        raf.writeInt(id);
                    } else {
                        // Palavra já existe
                        raf.seek(posicao);
                        raf.readUTF(); // Ignora palavra
                        long posicaoNumIds = raf.getFilePointer();
                        int num = raf.readInt();

                        // Cria lista com os IDs 
                        ArrayList<Integer> ids = new ArrayList<>();
                        for (int i = 0; i < num; i++) {
                            ids.add(raf.readInt());
                        }

                        if (!ids.contains(id)) {
                            // Atualiza somente se não existir
                            ids.add(id);
                            ids.sort(null); // Mantém os IDs ordenados
                            rewriteList(raf, posicaoNumIds, ids);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro DAO_InvertedList.addIL (source): " + e.getMessage());
            }
        } else if (code == 2) { // Mesmo codigo para a coluna country
            String country = b.getCountry();
            ArrayList<String> palavras = new ArrayList<>(Arrays.asList(country.split(" ")));

            try (RandomAccessFile raf = new RandomAccessFile(file2, "rw")) {
                for (String palavra : palavras) {
                    palavra = palavra.trim();
                    if (palavra.isEmpty()) continue;

                    long posicao = read(raf, palavra);
                    if (posicao == -1) {
                        raf.seek(raf.length());
                        raf.writeUTF(palavra);
                        raf.writeInt(1);
                        raf.writeInt(id);
                    } else {
                        raf.seek(posicao);
                        raf.readUTF();
                        long posicaoNumIds = raf.getFilePointer();
                        int num = raf.readInt();

                        ArrayList<Integer> ids = new ArrayList<>();
                        for (int i = 0; i < num; i++) {
                            ids.add(raf.readInt());
                        }

                        if (!ids.contains(id)) {
                            ids.add(id);
                            ids.sort(null);
                            rewriteList(raf, posicaoNumIds, ids);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro DAO_InvertedList.addIL (country): " + e.getMessage());
            }
        }
    }

    // Método para reescrever a lista de ids na posição correta    
    private static void rewriteList(RandomAccessFile raf, long posicaoNumIds, ArrayList<Integer> ids) throws IOException {
        long posInicioProximaPalavra = raf.getFilePointer();

        byte[] buffer = null;
        // Cria um buffer com o restante do arquivo na proxima palavra
        if (posInicioProximaPalavra < raf.length()) {
            buffer = new byte[(int) (raf.length() - posInicioProximaPalavra)];
            raf.readFully(buffer);
        }

        // reescreve os IDs com o novo ID
        raf.seek(posicaoNumIds);
        raf.writeInt(ids.size());
        for (Integer id : ids) {
            raf.writeInt(id);
        }

        // reescreve o restante do arquivo
        if (buffer != null) {
            raf.write(buffer);
        }
        raf.setLength(raf.getFilePointer());
    }

    public static long read(RandomAccessFile raf, String palavraProcurada) throws IOException {
        raf.seek(0);

        while (raf.getFilePointer() < raf.length()) {
            long posicaoAtual = raf.getFilePointer(); // Guarda a posição da palavra atual
            String palavra = raf.readUTF(); // Lê a palavra 
            int quantidadeIds = raf.readInt(); // Lê o numero de IDs

            if (quantidadeIds == 0) {
                // Palavra "fantasma", ignorar
                raf.seek(raf.getFilePointer() + (quantidadeIds * 4));
                continue;
            }

            if (palavra.equalsIgnoreCase(palavraProcurada)) { // Achou a Palavra
                return posicaoAtual;
            }

            raf.seek(raf.getFilePointer() + (quantidadeIds * 4)); // Ignora os IDs
        }

        return -1;
    }

    public static void searchIL(String palavra, int code) {
        String file = (code == 1) ? file1 : file2; // escolhe o arquivo a ser usado
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long posicao = read(raf, palavra.trim()); // Chama o read para achar a palavra

            if (posicao == -1) { // Palavra não existe
                System.out.println("Palavra não encontrada: " + palavra);
                return;
            }

            raf.seek(posicao); // Vai até a posição da palavra
            raf.readUTF(); // Lê a palavra
            int quantidadeIds = raf.readInt(); // Lê a quantidade de IDs

            if (quantidadeIds == 0) {
                System.out.println("Nenhum ID associado a: " + palavra);
                return;
            }

            for (int i = 0; i < quantidadeIds; i++) { // Pra cada ID da palavra
                int id = raf.readInt();
                CRUD_Hash.getIndex(id); // Procura o billionario no arquivo original
            }
        } catch (Exception e) {
            System.err.println("Erro DAO_InvertedList.searchIL: " + e.getMessage());
        }
    }

    public static void deleteIL(int id) {
        try {
            // Deleta de ambos os arquivos de lista invertida  
            deleteFromFile(file1, id);
            deleteFromFile(file2, id);
        } catch (Exception e) {
            e.printStackTrace(); // Mais informativo do que apenas e.getMessage()
        }
    }
    
    private static void deleteFromFile(String fileName, int id) throws IOException {
        File originalFile = new File(fileName);
        File tempFile = new File(fileName + ".tmp");

        try (
            // Abre os arquivos original e temporário
            RandomAccessFile rafRead = new RandomAccessFile(originalFile, "r");
            RandomAccessFile rafWrite = new RandomAccessFile(tempFile, "rw")
        ) {
            while (rafRead.getFilePointer() < rafRead.length()) {
                // Lê a palavra e a quantidade de IDs
                String palavra = rafRead.readUTF();
                int quantidadeIds = rafRead.readInt();

                // Lê todos os IDs
                ArrayList<Integer> ids = new ArrayList<>();
                for (int i = 0; i < quantidadeIds; i++) {
                    ids.add(rafRead.readInt());
                }

                // Remove o ID se estiver presente
                ids.removeIf(x -> x == id);

                // Se ainda houver IDs, reescreve a entrada no arquivo temporário
                if (!ids.isEmpty()) {
                    rafWrite.writeUTF(palavra);
                    rafWrite.writeInt(ids.size());
                    for (int i : ids) {
                        rafWrite.writeInt(i);
                    }
                }
            }
        }

        // Substitui o arquivo original pelo temporário
        if (!originalFile.delete()) {
            throw new IOException("Não foi possível deletar o arquivo original.");
        }
        if (!tempFile.renameTo(originalFile)) {
            throw new IOException("Não foi possível renomear o arquivo temporário.");
        }
    }

}