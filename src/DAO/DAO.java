package DAO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

            byte[] bt;

            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

            bt = billionaire.toByteArray();
            dataOutputStream.write(bt); // Insere objeto

        } catch (Exception e) {
            System.err.println("Error -> DAO.create: " + e);
        }

    }

    public static void read(FileInputStream fileInputStream) {

        byte[] bt;
        int len;
        char lapide;

        Billionaire billionaireTmp = new Billionaire();

        try {

            DataInputStream dataInputStream = new DataInputStream(fileInputStream);

            for (int i = 0; i < 2755; i++) {
                lapide = dataInputStream.readChar(); // Ler Lapide
                if (lapide == '*') {

                } else {

                }
                len = dataInputStream.readInt(); // Ler Tamanho Obj
                bt = new byte[len];
                dataInputStream.read(bt);
                billionaireTmp.fromByteArray(bt);
                System.out.println(billionaireTmp);
            }

        } catch (Exception e) {
            System.err.println("Erro Read: " + e);
        }

    }

}
