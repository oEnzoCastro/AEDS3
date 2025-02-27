package DAO;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;

import models.Billionaire;

public class DAO {
    public static void create(String[] row, FileOutputStream fileOutputStream) {

        try {

            int id = Integer.parseInt(row[0]);
            String name = row[1];
            float netWorth = Float.parseFloat(row[2]); // String -> Double
            String country = row[3];
            row[4] = row[4].replace("\"", ""); // Remove "" do array
            row[4] = row[4].replace(" ", ""); // Remove espaços em branco para evitar [0]=Tesla, [1]= SpaceX (Com espaço// antes)
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
            String education = row[11];
            Boolean self_made = Boolean.parseBoolean(row[12]); // String -> Boolean
            LocalDate birthdate = LocalDate.parse(row[13]); // String -> LocalDate !!!

            Billionaire billionaire = new Billionaire(id, name, netWorth, country, source, rank, age, residence,
                    citizenship, status, children, education, self_made, birthdate);

            byte[] bt;

            // Write

            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

            bt = billionaire.toByteArray();
            dataOutputStream.writeChar('|');
            dataOutputStream.write(bt); // Insere objeto

        } catch (Exception e) {
            System.err.println("Error -> DAO.create: " + e);
        }

    }

    public static void read() {

    }

}
