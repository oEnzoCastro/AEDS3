package DAO;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.util.ArrayList;
import models.Billionaire;

public class DAO {
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

            // Escrita binária direta com RandomAccessFile
            raf.write(billionaire.toByteArray());

        } catch (Exception e) {
            System.err.println("Error -> DAO.create: " + e);
        }
    }

    public static Billionaire read(FileInputStream fileInputStream, DataInputStream dataInputStream)
            throws IOException {

        Billionaire billionaireTmp = new Billionaire();

        byte[] bt;
        int len;
        char lapide;

        lapide = dataInputStream.readChar(); // Ler Lapide
        len = dataInputStream.readInt(); // Ler Tamanho Obj

        bt = new byte[len];
        dataInputStream.read(bt);

        billionaireTmp.fromByteArray(bt);

        // Confere se o objeto está inativo, se sim, retornar null
        if (lapide == '*') {
            return null;
        }

        return billionaireTmp;
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
        String file = "src/database/billionaires.db";
        String indexFile = "src/database/index.db";
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");

            while (rafIndex.getFilePointer() < rafIndex.length()) {
                int id = rafIndex.readInt(); // lê o id
                long posicao = rafIndex.readLong(); // lê a posição`
                if (id == key) { // encontrou o id procurado
                    raf.seek(posicao); // vai até a posição no arquivo original
                    char lapide;
                    lapide = raf.readChar(); // Ler Lapide

                    // Confere se o objeto já está inativo
                    if (lapide == '*') {

                    } else {
                        raf.seek(posicao); //volta para o inicio do billionario a ser deletado
                        raf.writeChar('*');
                    }

                    //Deletar do arquivo index

                    File tempFile = new File("src/database/temp_index.db");
                    RandomAccessFile tempIndex = new RandomAccessFile(tempFile, "rw");

                    rafIndex.seek(0);

                    //Reescreve no arquivo tmp sem o id deletado
                    while (rafIndex.getFilePointer() < rafIndex.length()) {
                        int currentId = rafIndex.readInt();
                        long currentPos = rafIndex.readLong();

                        if (currentId != id) {
                            tempIndex.writeInt(currentId);
                            tempIndex.writeLong(currentPos);
                        }
                    }

                    rafIndex.close();
                    raf.close();
                    tempIndex.close();

                    File original = new File(indexFile);
                    if (original.delete()) {
                        tempFile.renameTo(original);
                    }

                    return true;
                }
            }
            raf.close();
            rafIndex.close();

        } catch (Exception e) {
            System.err.println("Erro na leitura: " + e);
        }
        System.out.println("Bilionário não encontrado");
        return false;
    }

    // Escreve no arquivo index
    public static void createIndex(int id, long posicao, String indexFile, String bucketFile) {
        try {
            // Deverá ter no maximo 138 buckets (5% de 2755)
            // Para testes vou fazer com tamanho 4
            int tamanhoBucket = 4;
            int bitsBucket = (4 * 12) + 4;

            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");
            RandomAccessFile rafBucket = new RandomAccessFile(bucketFile, "rw");

            if (rafIndex.length() == 0) { // Confere se existem indices, se não, criar formato do indice
                rafIndex.writeInt(2); // pG (Profundidade Global) = 2

                // Criar forma dos buckets
                for (int i = 0; i < 2; i++) {

                    // Inserir no indexFile
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
            int hash = ((id % pG) * 8) + 4; // Multiplicar por 8 que é o tamanho de Long (+ 4 para pular pG)

            rafIndex.seek(hash); // Ir para a posição no arquivo de Indice
            long posicaoBucket = rafIndex.readLong(); // PosicaoBucket = Valor da posição no Indice

            posicaoBucket = (posicaoBucket * bitsBucket); // PosicaoBucket = Endereço do bucket

            rafBucket.seek(posicaoBucket); // Aponta para o Bucket a ser adicionado

            int numBucket = rafBucket.readInt(); // Lê quantos valores estão armazenados no Bucket
            if (numBucket == tamanhoBucket - 1) { // Se o Bucket estourar

                rafIndex.seek(rafIndex.length());

                for (int i = pG; i < pG * 2; i++) {

                    rafIndex.writeLong(i);

                    rafBucket.seek(rafBucket.length());

                    rafBucket.writeInt(0); // Tamanho dos Buckets novos

                    for (int j = 0; j < tamanhoBucket; j++) {
                        rafBucket.writeInt(0); // Id
                        rafBucket.writeLong(0); // Pos
                    }

                }

                pG = pG * 2;

                hash = ((id % pG) * 8) + 4;

                rafIndex.seek(0);
                rafIndex.writeInt(pG);

                // Passa RAF, profundidadeGlobal, Posicao do bucket antigo e o Tamanho maximo do bucket
                rebalancearBucket(rafIndex, rafBucket, pG, posicaoBucket, tamanhoBucket, bitsBucket);

            }

            long ponteiroBucket = (posicaoBucket + (numBucket * 12) + 4);
            rafBucket.seek(posicaoBucket);

            rafBucket.writeInt(numBucket + 1);

            rafBucket.seek(ponteiroBucket);
            // Escrever elemento
            rafBucket.writeInt(id);
            rafBucket.writeLong(posicao);

            System.out.println();

            // rafIndex.writeInt(id);
            // rafIndex.writeLong(posicao);

            rafIndex.close();
            rafBucket.close();

        } catch (Exception e) {
            System.err.println("Erro ao inserir no arquivo index: " + e);
        }
    }

    public static void rebalancearBucket(RandomAccessFile rafIndex, RandomAccessFile rafBucket, int pG,
            long posicaoBucket, int tamanhoBucket, int bitsBucket) {

        try {
            
            

        } catch (Exception e) {
            System.err.println("Erro ao rebalancear Bucket: " + e);
        }

    }

}
