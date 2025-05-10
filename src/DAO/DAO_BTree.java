package DAO;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.util.ArrayList;
import models.Billionaire;
import services.CRUD_BTree;

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
            raf.close();

            return null;
        }

        raf.close();

        return billionaireTmp;
    }

    public static boolean update(Billionaire newBillionaire, Billionaire billionaire, int key) {

        String file = "src/database/billionairesTree.db";
        String indexFile = "src/database/indexTree.db";

        try {
            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");
            RandomAccessFile rafBilionario = new RandomAccessFile(file, "rw");

            rafIndex.seek(0);
            long raiz = rafIndex.readLong();

            long pointer = CRUD_BTree.pesquisarArvore(key, indexFile, raiz);

            rafBilionario.seek(pointer);
            if (rafBilionario.readChar() == '*') {
                rafIndex.close();
                rafBilionario.close();
                System.out.println("Bilionário não encontrado!");
                return false;
            }

            if (newBillionaire.getByteSize() <= billionaire.getByteSize()) {
                
                rafBilionario.seek(pointer);
    
                rafBilionario.write(newBillionaire.toByteArray());
            
            } else {

                rafBilionario.seek(pointer);
    
                rafBilionario.writeChar('*');

                rafBilionario.seek(rafBilionario.length());

                rafIndex.seek(CRUD_BTree.pesquisarPonteiroArvore(key, indexFile, raiz));

                rafIndex.writeLong(rafBilionario.getFilePointer());

                rafBilionario.write(newBillionaire.toByteArray());


            }

            rafIndex.close();
            rafBilionario.close();

            
        } catch (Exception e) {
            System.err.println("Erro ao atualizar: " + e);
        }

        return true;

    }

    // Novo delete com arquivo index
    public static boolean deleteIndex(int key) {

        String file = "src/database/billionairesTree.db";
        String indexFile = "src/database/indexTree.db";
        
        try {

            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");
            RandomAccessFile rafBilionario = new RandomAccessFile(file, "rw");

            rafIndex.seek(0);
            long raiz = rafIndex.readLong();

            long pointer = CRUD_BTree.pesquisarArvore(key, indexFile, raiz);

            rafBilionario.seek(pointer);
            if (rafBilionario.readChar() == '*') {
                rafIndex.close();
                rafBilionario.close();
                System.out.println("Bilionário não encontrado!");
                return false;
            }

            rafBilionario.seek(pointer);

            rafBilionario.writeChar('*');
            
            System.out.println();

            rafIndex.close();
            rafBilionario.close();
        } catch (Exception e) {
            System.err.println("Erro na leitura: " + e);
        }
        return true;
    }

}
