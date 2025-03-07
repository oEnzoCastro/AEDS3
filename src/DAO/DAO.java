package DAO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.util.ArrayList;

import models.Billionaire;

public class DAO {
    public static void create(String[] row, FileOutputStream fileOutputStream) {

        try {

            // Format
            int id = Integer.parseInt(row[0]);
            String name = row[1];
            float netWorth = Float.parseFloat(row[2]); // String -> Double
            String country = row[3];
            row[4] = row[4].replace("\"", ""); // Remove "" do array
            String[] sourceArray = row[4].split(","); // Splita o Array
            ArrayList<String> source = new ArrayList<String>();
            for (String i : sourceArray) {
                source.add(i);
            }
            int rank = Integer.parseInt(row[5]); // String -> Int
            int age = Integer.parseInt(row[6]); // String(Float) -> Double !!!
            String residence = row[7];
            String citizenship = row[8];
            String status = row[9];
            int children = Integer.parseInt(row[10]); // String(Float) -> Double !!!

            row[11] = row[11].replace("\"", ""); // Remove "" do array
            String[] educationArray = row[11].split(","); // Splita o Array
            ArrayList<String> education = new ArrayList<String>();
            for (String i : educationArray) {
                education.add(i);
            }
            Boolean self_made = Boolean.parseBoolean(row[12]); // String -> Boolean
            LocalDate birthdate = LocalDate.parse(row[13]); // String -> LocalDate !!!

            // New Object
            Billionaire billionaire = new Billionaire(id, name, netWorth, country, source, rank, age, residence,
                    citizenship, status, children, education, self_made, birthdate);

            // Write

            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            dataOutputStream.write(billionaire.toByteArray()); // Insere objeto

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

    public static boolean delete(int id, String file) {
        try {

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            randomAccessFile.seek(Integer.BYTES);

            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {

                char lapide = randomAccessFile.readChar(); // Lapide
                int size = randomAccessFile.readInt(); // Object Size

                // Id (User Input) == Id (Database)
                if (id == randomAccessFile.readInt()) {
                    // Caso o objeto já tenha sido removido
                    if (lapide == '*') {
                        System.err.println("Objeto já foi removido!");
                        randomAccessFile.close();
                        return false;
                    }

                    // Pega posição atual e volta os Bytes que foram lidos para alterar a lapide
                    randomAccessFile.seek(randomAccessFile.getFilePointer() - Integer.BYTES - Integer.BYTES - Character.BYTES);
                    randomAccessFile.writeChar('*');

                    randomAccessFile.close();
                    return true;
                }

                randomAccessFile.skipBytes(size - Integer.BYTES);

            }

            randomAccessFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Algum erro aconteceu
    }

}
