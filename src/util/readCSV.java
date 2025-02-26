package util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;

import DAO.DAO;

public class readCSV {
    public static void read(FileOutputStream fileOutputStream) {

        String file = "src/database/forbes_billionaires.csv";
        String line = "";

        BufferedReader reader = null;

        try {

            reader = new BufferedReader(new FileReader(file));

            reader.readLine(); // Ler primeira linha

            while ((line = reader.readLine()) != null) {

                String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                DAO.insert(row, fileOutputStream);

                // for (String i : row) {
                // System.out.print(i + " - ");
                // }
                // System.out.println();

            }

            reader.close();

        } catch (Exception e) {

        }
    }
}
