package DAO;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.util.ArrayList;
import models.Billionaire;
import services.CRUD_Hash;

public class DAO_Hash {
    public static void create(String[] row, RandomAccessFile raf) {

        try {
            // Parsing dos dados
            int id = Integer.parseInt(row[0]);
            String name = row[1];
            float netWorth = Float.parseFloat(row[2]);
            String country = row[3];

            row[4] = row[4].replace("\"", "");
            String[] sourceArray = row[4].split(",");
            ArrayList<String> source = new ArrayList<>();
            for (String i : sourceArray) {
                source.add(i.trim());
            }

            int rank = Integer.parseInt(row[5]);
            int age = Integer.parseInt(row[6]);
            String residence = row[7];
            String citizenship = row[8];
            String status = row[9];
            int children = Integer.parseInt(row[10]);

            row[11] = row[11].replace("\"", "");
            String[] educationArray = row[11].split(",");
            ArrayList<String> education = new ArrayList<>();
            for (String i : educationArray) {
                education.add(i.trim());
            }

            Boolean self_made = Boolean.parseBoolean(row[12]);
            LocalDate birthdate = LocalDate.parse(row[13]);

            // Criação do objeto
            Billionaire billionaire = new Billionaire(id, name, netWorth, country, source, rank, age, residence,
                    citizenship, status, children, education, self_made, birthdate);

            // Adiciona nas 2 listas invertidas
            DAO_InvertedList.addIL(billionaire, 1);
            DAO_InvertedList.addIL(billionaire, 2);

            // Escrita binária direta com RandomAccessFile
            raf.write(billionaire.toByteArray());

        } catch (Exception e) {
            System.err.println("Error -> DAO.create: " + e);
        }
    }

    public static void update(Billionaire newBillionaire) {

        try {
            newBillionaire.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Novo delete com arquivo index
    public static boolean deleteIndex(int key) {
        String file = "src/database/billionairesHash.db";
        String indexFile = "src/database/index.db";
        String bucketFile = "src/database/bucketFile.db";
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");
            RandomAccessFile rafBucket = new RandomAccessFile(bucketFile, "rw");

            long billionairePosition = CRUD_Hash.getBillionairePosition(key);

            if (billionairePosition < 0) {
                raf.close();
                rafIndex.close();
                rafBucket.close();
                return false;
            }

            raf.seek(billionairePosition);

            raf.writeChar('*');

            long enderecoBucket = CRUD_Hash.getBucketPosition(key);

            rafBucket.seek(enderecoBucket);

            int tamanhoBucket = rafBucket.readInt();
            tamanhoBucket = tamanhoBucket - 1;

            rafBucket.seek(enderecoBucket);

            rafBucket.writeInt(tamanhoBucket);

            for (int i = 0; i < tamanhoBucket; i++) {

                int id = rafBucket.readInt();
                rafBucket.readLong();
                System.out.println();

                if (key == id) {

                    rafBucket.seek(rafBucket.getFilePointer() - 12); // Voltar para posição do bucket

                    rafBucket.writeInt(0);
                    rafBucket.writeLong(0);

                    if (i + 1 >= tamanhoBucket) {

                    } else {

                        while (i < tamanhoBucket) {

                            int updateId = rafBucket.readInt();
                            long updatePosicao = rafBucket.readLong();

                            rafBucket.seek(rafBucket.getFilePointer() - 24);

                            rafBucket.writeInt(updateId);
                            rafBucket.writeLong(updatePosicao);
                            rafBucket.writeInt(0);
                            rafBucket.writeLong(0);

                            i++;

                            System.out.println();

                        }
                    }

                }
            }

            raf.close();
            rafIndex.close();
            rafBucket.close();
            return true;

        } catch (Exception e) {
            System.err.println("Erro na leitura: " + e);
        }
        return true;
    }

    // Escreve no arquivo index
    public static void createIndex(int id, long posicao, String indexFile, String bucketFile, RandomAccessFile rafIndex,
            RandomAccessFile rafBucket) {
        try {
            // Deverá ter no maximo 138 buckets (5% de 2755)
            // Para testes vou fazer com tamanho 4
            int tamanhoBucket = 4;
            int bitsBucket = (4 * 12) + 4;

            rafIndex.seek(0);
            rafBucket.seek(0);

            if (rafIndex.length() == 0) { // Confere se existem indices, se não, criar formato do indice
                rafIndex.writeInt(2); // pG (Profundidade Global) = 2

                // Criar forma dos buckets
                for (int i = 0; i < 2; i++) {

                    // Inserir no indexFile
                    rafIndex.writeInt(2); // Primeira posição do fileBucket
                    rafIndex.writeLong(i); // Primeira posição do fileBucket

                    // Inserir no bucketFile
                    rafBucket.writeInt(0); // Tamanho do Bucket

                    for (int j = 0; j < tamanhoBucket; j++) {
                        rafBucket.writeInt(0); // Id
                        rafBucket.writeLong(0); // Pos
                    }

                }

            }

            // Inserir

            rafIndex.seek(0); // Index Posição 0

            int pG = rafIndex.readInt(); // pG = Profundidade Global
            int hash = ((id % pG) * 12) + 4; // Multiplicar por 8 que é o tamanho de Long (+ 4 para pular pG)

            rafIndex.seek(hash); // Ir para a posição no arquivo de Indice
            int pL = rafIndex.readInt(); // pL = Profundidade Local
            long posicaoBucket = rafIndex.readLong(); // PosicaoBucket = Valor da posição no Indice

            posicaoBucket = (posicaoBucket * bitsBucket); // PosicaoBucket = Endereço do bucket

            rafBucket.seek(posicaoBucket); // Aponta para o Bucket a ser adicionado

            int numBucket = rafBucket.readInt(); // Lê quantos valores estão armazenados no Bucket

            // Se o Bucket estourar
            if (numBucket == tamanhoBucket - 1) {

                rafIndex.seek(hash); // Ir para a posição no arquivo de Indice

                pL = pL * 2;

                rafIndex.writeInt(pL);

                if (pL > pG) {

                    rafIndex.seek(rafIndex.length());

                    for (int i = pG; i < pG * 2; i++) {

                        rafIndex.writeInt(pG * 2);
                        rafIndex.writeLong(i);

                        rafBucket.seek(rafBucket.length());

                        rafBucket.writeInt(0); // Tamanho dos Buckets novos

                        for (int j = 0; j < tamanhoBucket; j++) {
                            rafBucket.writeInt(0); // Id
                            rafBucket.writeLong(0); // Pos
                        }

                    }

                    pG = pG * 2;

                }

                hash = ((id % pG) * 12) + 4;

                rafIndex.seek(0);
                rafIndex.writeInt(pG);

                // Passa RAF, profundidadeGlobal, Posicao do bucket antigo e o Tamanho maximo do
                // bucket
                rebalancearBucket(rafIndex, rafBucket, pG, posicaoBucket, tamanhoBucket, bitsBucket);

            }

            hash = ((id % pG) * 12) + 4; // Multiplicar por 8 que é o tamanho de Long (+ 4 para pular pG)

            rafIndex.seek(hash); // Ir para a posição no arquivo de Indice

            pL = rafIndex.readInt(); // pL = Profundidade Local

            posicaoBucket = rafIndex.readLong(); // PosicaoBucket = Valor da posição no Indice

            posicaoBucket = (posicaoBucket * bitsBucket); // PosicaoBucket = Endereço do bucket

            rafBucket.seek(posicaoBucket); // Aponta para o Bucket a ser adicionado

            numBucket = rafBucket.readInt(); // Lê quantos valores estão armazenados no Bucket

            long ponteiroBucket = (posicaoBucket + (numBucket * 12) + 4);
            rafBucket.seek(posicaoBucket);

            rafBucket.writeInt(numBucket + 1);

            rafBucket.seek(ponteiroBucket);
            // Escrever elemento
            rafBucket.writeInt(id);
            rafBucket.writeLong(posicao);

            // rafIndex.writeInt(id);
            // rafIndex.writeLong(posicao);

        } catch (Exception e) {
            System.err.println("Erro ao inserir no arquivo index: " + e);
        }
    }

    public static void rebalancearBucket(RandomAccessFile rafIndex, RandomAccessFile rafBucket, int pG,
            long posicaoBucket, int tamanhoBucket, int bitsBucket) {

        try {

            int[] elementos = new int[tamanhoBucket - 1];
            long[] elementosPosicao = new long[tamanhoBucket - 1];

            rafBucket.seek(posicaoBucket);

            rafBucket.readInt();

            for (int i = 0; i < tamanhoBucket - 1; i++) {

                elementos[i] = rafBucket.readInt();
                elementosPosicao[i] = rafBucket.readLong();

            }

            rafBucket.seek(posicaoBucket);

            rafBucket.writeInt(0);

            for (int i = 0; i < tamanhoBucket - 1; i++) {

                rafBucket.writeInt(0);
                rafBucket.writeLong(0);

            }

            for (int i = 0; i < tamanhoBucket - 1; i++) {

                int hash = ((elementos[i] % pG) * 12) + 4; // Multiplicar por 8 que é o tamanho de Long (+ 4 para pular
                                                           // pG)

                rafIndex.seek(hash);

                rafIndex.readInt(); // pL = Profundidade Local

                posicaoBucket = rafIndex.readLong(); // PosicaoBucket = Valor da posição no Indice

                posicaoBucket = (posicaoBucket * bitsBucket); // PosicaoBucket = Endereço do bucket

                rafBucket.seek(posicaoBucket); // Aponta para o Bucket a ser adicionado

                int numBucket = rafBucket.readInt(); // Lê quantos valores estão armazenados no Bucket

                long ponteiroBucket = (posicaoBucket + (numBucket * 12) + 4);

                rafBucket.seek(posicaoBucket);

                rafBucket.writeInt(numBucket + 1);

                rafBucket.seek(ponteiroBucket);
                // Escrever elemento
                rafBucket.writeInt(elementos[i]);
                rafBucket.writeLong(elementosPosicao[i]);

            }

        } catch (Exception e) {
            System.err.println("Erro ao rebalancear Bucket: " + e);
        }

    }

}
