package DAO;

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
            System.err.println("Erro DAO_InvertedList.deleteIL: " + e.getMessage());
        }
    }
    
    private static void deleteFromFile(String fileName, int id) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");

        try {
            raf.seek(0);

            while (raf.getFilePointer() < raf.length()) {
                long posPalavra = raf.getFilePointer(); // Marca o início da palavra
                String palavra = raf.readUTF(); // Lê a palavra

                long posNumIds = raf.getFilePointer(); // Marca onde começa a quantidade de IDs
                int quantidadeIds = raf.readInt(); // Lê quantidade de IDs

                ArrayList<Integer> ids = new ArrayList<>();
                for (int i = 0; i < quantidadeIds; i++) {
                    ids.add(raf.readInt());
                }

                if (ids.contains(id)) {
                    ids.remove(Integer.valueOf(id));

                    raf.seek(posPalavra); // Volta para reescrever desde a palavra

                    if (ids.isEmpty()) {
                        // Se não há mais IDs, remove completamente a entrada (sobrescrevendo com o restante do arquivo)
                        long posDepoisEntrada = raf.getFilePointer(); // Onde terminaria a entrada
                        byte[] restante = new byte[(int) (raf.length() - posDepoisEntrada)];
                        raf.readFully(restante);

                        raf.seek(posPalavra);
                        raf.write(restante);
                        raf.setLength(raf.getFilePointer());

                        // Volta para o início para continuar o loop corretamente
                        raf.seek(posPalavra);
                    } else {
                        // Apenas atualiza a lista de IDs
                        rewriteList(raf, posNumIds, ids);
                        raf.seek(posPalavra + getEntrySize(palavra, ids.size())); // Pula para próxima entrada
                    }
                }
            }
        } finally {
            raf.close();
        }
    }

    // Método auxiliar para calcular o tamanho de uma entrada no arquivo
    private static int getEntrySize(String palavra, int numIds) {
        int tamanhoPalavra = 2 + palavra.length() * 2; // UTF: 2 bytes para length + 2 bytes por caractere
        return tamanhoPalavra + 4 + (4 * numIds); // + 4 bytes do int quantidade, + 4 por ID
    }
}