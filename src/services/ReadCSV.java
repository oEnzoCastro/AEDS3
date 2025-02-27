package services;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;

import DAO.DAO;

public class ReadCSV {
    public static int createAll(FileOutputStream fileOutputStream) {

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
                
                break;

            }

            reader.close();

        } catch (Exception e) {
            System.err.println("Erro ReadCSV.createAll: " + e);
        }

        return id;
    }

    public static void get() {

    }

}
