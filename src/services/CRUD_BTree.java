package services;

import DAO.DAO;
import DAO.DAO_BTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import models.Billionaire;

public class CRUD_BTree {
    public static int createAll() {

        String line;
    
        String file = "src/database/billionaires.db";
        String fileCSV = "src/database/forbes_billionaires.csv";
        String indexFile ="src/database/indexTree.db";

        int id = -1;
    
        BufferedReader reader;

        // Deleta os arquivos antigos
        new File(file).delete();
        new File(indexFile).delete();

        // Cria os arquivos novos
        try {
    
            reader = new BufferedReader(new FileReader(fileCSV));
            reader.readLine(); // pula o cabeçalho
    
            RandomAccessFile raf = new RandomAccessFile(file, "rw");

            // Reserva espaço para o último ID
            raf.writeInt(0);
    
            long posicao;
    
            while ((line = reader.readLine()) != null) {
    
                String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    
                posicao = raf.getFilePointer(); // posição antes da escrita
    
                DAO.create(row, raf);
    
                id = Integer.parseInt(row[0]);
                
                DAO_BTree.createIndex(id, posicao, indexFile); // Adiciona Elemento no arquivo de indice
            }
    
            // Volta ao início e grava o último ID inserido
            raf.seek(0);
            raf.writeInt(id);
    
            raf.close();
            reader.close();
            System.out.println("CSV convertido para Database!");
    
        } catch (Exception e) {
            System.err.println("Erro ReadCSV.createAll: " + e);
        }
    
        return id;
    }    

    public static void create(String file) {
        try {
            String indexFile ="src/database/index.db";
            String bucketFile = "src/database/bucketFile.db";

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");
            byte[] bt;

            randomAccessFile.seek(0);

            int lastId = randomAccessFile.readInt() + 1;

            randomAccessFile.seek(0);

            randomAccessFile.writeInt(lastId);

            Billionaire newBillionaire = BillionaireService.newBillionaire(lastId);

            randomAccessFile.seek(randomAccessFile.length()); // Move ponteiro para fim do arquivo

            long posicao = randomAccessFile.getFilePointer();
            rafIndex.seek(rafIndex.length()); // Move para o fim do arquivo index
            DAO_BTree.createIndex(lastId, posicao, indexFile); 

            // Inserir newBillionaire no arquivo original

            bt = newBillionaire.toByteArray();
            randomAccessFile.write(bt);

            randomAccessFile.close();
            rafIndex.close();

        } catch (Exception e) {
            System.out.println("Erro CREATE: " + e);
        }

    }

    public static Billionaire getIndex(int key){

        String file = "src/database/billionaires.db";
        String indexFile ="src/database/index.db";
        String bucketFile = "src/database/bucketFile.db";

        try{


            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");
            RandomAccessFile rafBucket = new RandomAccessFile(bucketFile, "rw");

            int pG = rafIndex.readInt();

            int hash = ((key % pG) * 12) + 4; // Multiplicar por 8 que é o tamanho de Long (+ 4 para pular pG)

            rafIndex.seek(hash); // Ir para a posição no arquivo de Indice

            int pL = rafIndex.readInt(); // pL = Profundidade Local
            long posicaoBucket = rafIndex.readLong(); // PosicaoBucket = Valor da posição no Indice

            int bitsBucket = (4 * 12) + 4;
            
            posicaoBucket = (posicaoBucket * bitsBucket); // PosicaoBucket = Endereço do bucket

            rafBucket.seek(posicaoBucket); // Aponta para o Bucket a ser adicionado

            int numBucket = rafBucket.readInt();

            for (int i = 0; i < numBucket; i++) {
                
                int id = rafBucket.readInt();
                long posicao = rafBucket.readLong();
                System.out.println();

                if (key == id) {

                    Billionaire billionaire = DAO_BTree.read(file, posicao);

                    System.out.println(billionaire);

                    return billionaire;

                }

            }

            rafIndex.close();

        }catch(Exception e){
            System.err.println("Erro na leitura: " + e);
        }
        System.out.println("Bilionário não encontrado");
        return null;
    }

    public static long getBucketPosition(int key){

        String file = "src/database/billionaires.db";
        String indexFile ="src/database/index.db";
        String bucketFile = "src/database/bucketFile.db";

        try{

            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");
            RandomAccessFile rafBucket = new RandomAccessFile(bucketFile, "rw");

            int pG = rafIndex.readInt();

            int hash = ((key % pG) * 12) + 4; // Multiplicar por 8 que é o tamanho de Long (+ 4 para pular pG)

            rafIndex.seek(hash); // Ir para a posição no arquivo de Indice

            int pL = rafIndex.readInt(); // pL = Profundidade Local
            long posicaoBucket = rafIndex.readLong(); // PosicaoBucket = Valor da posição no Indice

            int bitsBucket = (4 * 12) + 4;
            
            posicaoBucket = (posicaoBucket * bitsBucket); // PosicaoBucket = Endereço do bucket

            rafIndex.close();
            
            return posicaoBucket;

        }catch(Exception e){
            System.err.println("Erro na leitura: " + e);
        }
        System.out.println("Bilionário não encontrado");
        return -1;
    }

    public static long getBucketElementPosition(int key){

        String file = "src/database/billionaires.db";
        String indexFile ="src/database/index.db";
        String bucketFile = "src/database/bucketFile.db";

        try{


            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");
            RandomAccessFile rafBucket = new RandomAccessFile(bucketFile, "rw");

            int pG = rafIndex.readInt();

            int hash = ((key % pG) * 12) + 4; // Multiplicar por 8 que é o tamanho de Long (+ 4 para pular pG)

            rafIndex.seek(hash); // Ir para a posição no arquivo de Indice

            int pL = rafIndex.readInt(); // pL = Profundidade Local
            long posicaoBucket = rafIndex.readLong(); // PosicaoBucket = Valor da posição no Indice

            int bitsBucket = (4 * 12) + 4;
            
            posicaoBucket = (posicaoBucket * bitsBucket); // PosicaoBucket = Endereço do bucket

            rafBucket.seek(posicaoBucket); // Aponta para o Bucket a ser adicionado

            int numBucket = rafBucket.readInt();

            for (int i = 0; i < numBucket; i++) {
                
                int id = rafBucket.readInt();
                long posicao = rafBucket.readLong();
                System.out.println();

                if (key == id) {

                    rafBucket.seek(rafBucket.getFilePointer() - 8); // Voltar para posição do bucket

                    return rafBucket.getFilePointer();

                }

            }

            rafIndex.close();

        }catch(Exception e){
            System.err.println("Erro na leitura: " + e);
        }
        System.out.println("Bilionário não encontrado");
        return -1;
    }

    public static long getBillionairePosition(int key){

        String file = "src/database/billionaires.db";
        String indexFile ="src/database/index.db";
        String bucketFile = "src/database/bucketFile.db";

        try{


            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");
            RandomAccessFile rafBucket = new RandomAccessFile(bucketFile, "rw");

            int pG = rafIndex.readInt();

            int hash = ((key % pG) * 12) + 4; // Multiplicar por 8 que é o tamanho de Long (+ 4 para pular pG)

            rafIndex.seek(hash); // Ir para a posição no arquivo de Indice

            int pL = rafIndex.readInt(); // pL = Profundidade Local
            long posicaoBucket = rafIndex.readLong(); // PosicaoBucket = Valor da posição no Indice

            int bitsBucket = (4 * 12) + 4;
            
            posicaoBucket = (posicaoBucket * bitsBucket); // PosicaoBucket = Endereço do bucket

            rafBucket.seek(posicaoBucket); // Aponta para o Bucket a ser adicionado

            int numBucket = rafBucket.readInt();

            for (int i = 0; i < numBucket; i++) {
                
                int id = rafBucket.readInt();
                long posicao = rafBucket.readLong();
                System.out.println();

                if (key == id) {

                    return posicao;

                }

            }

            rafIndex.close();

        }catch(Exception e){
            System.err.println("Erro na leitura: " + e);
        }
        System.out.println("Bilionário não encontrado");
        return -1;
    }

    public static void update(String key, String file) {

        Billionaire billionaire;
        int id = Integer.parseInt(key);
        if (key.charAt(0) >= '0' && key.charAt(0) <= '9') {
            billionaire = getIndex(id);
        } else {
            System.out.println("Só é aceito update inserindo a chave 'ID'!");
            return;
        }

        if (billionaire == null) {
            System.out.println("Bilionário Indisponível!");
        } else {
            Billionaire newBillionaire = BillionaireService.updateBillionaire(billionaire);

            try {
                String indexFile ="src/database/index.db";
                String bucketFile = "src/database/bucketFile.db";

                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");
                RandomAccessFile rafBucket = new RandomAccessFile(bucketFile, "rw");

                byte[] bt;

                if (newBillionaire.getByteSize() > billionaire.getByteSize()) {

                    long posicaoBilionario = getBillionairePosition(id);

                    randomAccessFile.seek(posicaoBilionario); 

                    randomAccessFile.writeChar('*'); // Adiciona Lapide

                    randomAccessFile.seek(randomAccessFile.length()); // Move ponteiro para fim do arquivo

                    long posicaoBucket = getBucketElementPosition(id);

                    rafBucket.seek(posicaoBucket);

                    rafBucket.writeLong(randomAccessFile.getFilePointer());
                    

                    // Inserir newBillionaire

                    bt = newBillionaire.toByteArray();
                    randomAccessFile.write(bt);

                    randomAccessFile.close();
                    rafIndex.close();

                } else {

                    long filePointer = getBillionairePosition(id);

                    randomAccessFile.seek(filePointer);

                    bt = newBillionaire.toByteArrayUpdate(billionaire, file);
                    randomAccessFile.write(bt);

                    randomAccessFile.close();
                    rafIndex.close();
                    // Add newBillionaire no lugar do billionaire
                }

                randomAccessFile.close();
                rafIndex.close();
            } catch (Exception e) {
                System.err.println(e);
            }

        }

    }

    public static void delete(int id, String file) {

        boolean isDeleted = DAO_BTree.deleteIndex(id);

        if (isDeleted == true) {
            System.out.println("Bilionário Deletado!");
        } else {
            System.out.println("Bilionário não deletado!");
        }

    }

}
