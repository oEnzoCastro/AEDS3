package DAO;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

import models.Billionaire;
import services.CRUD;

public class DAO_InvertedList {
    private static final String file1 = "src/database/invertedListSource.db";
    private static final String file2 = "src/database/invertedListCountry.db";

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
                CRUD.getIndex(id); // Procura o billionario no arquivo original
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
        ArrayList<byte[]> entradasValidas = new ArrayList<>(); // Lista de palavras e IDs válidas
    
        try {
            raf.seek(0); // Começa a ler desde o início do arquivo
    
            while (raf.getFilePointer() < raf.length()) { // Enquanto não chegar no fim do arquivo
                long inicioPalavra = raf.getFilePointer(); // Marca onde começa a palavra
                String palavra = raf.readUTF(); // Lê a palavra
                int quantidadeIds = raf.readInt(); // Lê qntd IDs
    
                ArrayList<Integer> ids = new ArrayList<>();
                for (int i = 0; i < quantidadeIds; i++) {
                    ids.add(raf.readInt()); // Lê cada ID e adiciona na lista
                }
    
                if (ids.contains(id)) {
                    ids.remove(Integer.valueOf(id)); // Remove o ID a ser deletado
                }
    
                if (!ids.isEmpty()) { 
                    // Se ainda sobraram IDs associados à palavra
                    try (RandomAccessFile temp = new RandomAccessFile("temp.db", "rw")) {
                        temp.seek(0);
                        temp.writeUTF(palavra); // Escreve a palavra
                        temp.writeInt(ids.size()); // Escreve nova quantidade de IDs
                        for (Integer novoId : ids) {
                            temp.writeInt(novoId); // Escreve cada ID novo
                        }
                        temp.seek(0);
                        byte[] buffer = new byte[(int) temp.length()];
                        temp.readFully(buffer); // Cria um buffer com a palavra e os ids
                        entradasValidas.add(buffer); // Guarda para depois regravar
                    }
                }
            }
    
            // Depois de varrer o arquivo
            raf.setLength(0); // Limpa o arquivo original
            for (byte[] entrada : entradasValidas) {
                raf.write(entrada); // Escreve as entradas novamente
            }
    
        } finally {
            raf.close(); // Fecha o arquivo
        }
    }    
}
