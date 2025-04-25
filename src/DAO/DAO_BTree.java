package DAO;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.util.ArrayList;
import models.Billionaire;

public class DAO_BTree {
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

    public static Billionaire read(String file, long posicao) throws IOException {

        Billionaire billionaireTmp = new Billionaire();

        RandomAccessFile raf = new RandomAccessFile(file, "rw");

        raf.seek(posicao);

        byte[] bt;
        int len;
        char lapide;

        lapide = raf.readChar(); // Ler Lapide
        len = raf.readInt(); // Ler Tamanho Obj

        bt = new byte[len];
        raf.read(bt);

        billionaireTmp.fromByteArray(bt);

        // Confere se o objeto está inativo, se sim, retornar null
        if (lapide == '*') {
            return null;
        }

        return billionaireTmp;
    }

    public static void update(Billionaire newBillionaire) {

        

    }

    // Novo delete com arquivo index
    public static boolean deleteIndex(int key) {
        String file = "src/database/billionaires.db";
        String indexFile = "src/database/index.db";
        String bucketFile = "src/database/bucketFile.db";
        try {
            


        } catch (Exception e) {
            System.err.println("Erro na leitura: " + e);
        }
        System.out.println("Bilionário não encontrado");
        return false;
    }

    // Escreve no arquivo index
    public static void createIndex(int id, long posicao, String indexFile) {
        try {



        } catch (Exception e) {
            System.err.println("Erro ao inserir no arquivo index: " + e);
        }
    }

}
