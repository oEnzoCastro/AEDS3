package util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

import DAO.DAO;
import models.Billionaire;

public class readCSV {
    public static int getAll(FileOutputStream fileOutputStream) {

        String file = "src/database/forbes_billionaires.csv";
        String line = "";

        int id = -1;

        BufferedReader reader = null;

        try {

            reader = new BufferedReader(new FileReader(file));

            reader.readLine(); // Ler primeira linha

            while ((line = reader.readLine()) != null) {

                String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                DAO.create(row, fileOutputStream);

                id = Integer.parseInt(row[0]);

            }

            reader.close();

        } catch (Exception e) {
            System.err.println(e);
        }

        return id;
    }

    public static void get() {

        String file = "teste.db";
        int len;
        byte[] bt;

        Billionaire billionaireTmp = new Billionaire();

        FileInputStream fileInputStream;
        DataInputStream dataInputStream;

        try {

            fileInputStream = new FileInputStream(file);
            dataInputStream = new DataInputStream(fileInputStream);

            len = dataInputStream.readInt(); // Lê tamanho do primeiro Objeto
            bt = new byte[len]; // Byte do tamanho do Objeto
            dataInputStream.read(bt);
            billionaireTmp.fromByteArray(bt);
            System.out.println("Billionaire = " + billionaireTmp);

            fileInputStream.close();

        } catch (Exception e) {
            System.err.println("Erro: " + e);
        }

    }

}
