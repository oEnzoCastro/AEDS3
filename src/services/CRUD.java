package services;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.RandomAccessFile;

import DAO.DAO;
import models.Billionaire;

public class CRUD {
    public static int createAll() {

        String line = "";

        String file = "src/database/billionaires.db";
        String fileCSV = "src/database/forbes_billionaires.csv";

        int id = -1;

        BufferedReader reader = null;

        try {

            FileOutputStream fileOutputStream = new FileOutputStream(file); // Arquivo de banco de dados a inserir

            reader = new BufferedReader(new FileReader(fileCSV));

            reader.readLine(); // Ler primeira linha

            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            dataOutputStream.writeInt(0);

            while ((line = reader.readLine()) != null) {

                String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                DAO.create(row, fileOutputStream);

                id = Integer.parseInt(row[0]);

            }

            // RandomAccessFile aponta para 1° posição e escreve o último ID inserido
            randomAccessFile.seek(0);
            randomAccessFile.writeInt(id);

            // .close()
            randomAccessFile.close();
            reader.close();
            fileOutputStream.close();

        } catch (Exception e) {
            System.err.println("Erro ReadCSV.createAll: " + e);
        }

        return id;
    }

    public static void get(int id, String file) {

        boolean found = false;

        try {

            FileInputStream fileInputStream = new FileInputStream(file);

            DataInputStream dataInputStream = new DataInputStream(fileInputStream);

            Billionaire billionaireTmp;

            // Lê primeiros 4 BYTES (Ultimo ID inserido)
            dataInputStream.readInt();

            while (dataInputStream.available() > 0) {

                billionaireTmp = DAO.read(fileInputStream, dataInputStream);

                // Verifica se é o ID procurado
                if (billionaireTmp.getId() == id) {
                    System.out.println(billionaireTmp);
                    found = true;
                }

            }

        } catch (Exception e) {
            System.err.println("Erro Read: " + e);
        }
        if (!found) {
            System.out.println("Objeto não encontrado");
        }

    }

    public static void delete(int id) {

    }

}
