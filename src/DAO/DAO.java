package DAO;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;

import models.Billionaire;

public class DAO {
    public static void insert(String[] row, FileOutputStream fileOutputStream) {

        try {

            // Format

            String name = row[0];
            Double netWorth = Double.parseDouble(row[1]); // String -> Double
            String country = row[2];
            row[3] = row[3].replace("\"", ""); // Remove "" do array
            row[3] = row[3].replace(" ", ""); // Remove espaços em branco para evitar [0]=Tesla, [1]= SpaceX (Com espaço
                                              // antes)
            String[] sourceArray = row[3].split(","); // Splita o Array
            ArrayList<String> source = new ArrayList<String>();
            for (String i : sourceArray) {
                source.add(i);
            }

            int rank = Integer.parseInt(row[4]); // String -> Int
            Double age = Double.parseDouble(row[5]); // String(Float) -> Double !!!
            String residence = row[6];
            String citizenship = row[7];
            String status = row[8];
            Double children = Double.parseDouble(row[9]); // String(Float) -> Double !!!
            String education = row[10];
            Boolean self_made = Boolean.parseBoolean(row[11]); // String -> Boolean
            LocalDate birthdate = LocalDate.parse(row[12]); // String -> LocalDate !!!

            // Jeff Bezos,177.0,United States,Amazon,1,57.0,"Seattle, Washington",United
            // States,In Relationship,4.0,"Bachelor of Arts/Science, Princeton
            // University",True,1968-01-01

            Billionaire billionaire = new Billionaire(name, netWorth, country, source, rank, age, residence,
                    citizenship, status, children, education, self_made, birthdate);

            DataOutputStream dataOutputStream;

            byte[] bt;

            // Write

            dataOutputStream = new DataOutputStream(fileOutputStream);

            bt = billionaire.toByteArray();
            dataOutputStream.writeChar(0); // Lápide do objeto
            dataOutputStream.writeInt(bt.length); // Byte para guardar tamanho do objeto
            dataOutputStream.write(bt); // Insere objeto

        } catch (Exception e) {
            System.err.println("Erro: " + e);
        }

    }

    public static void getAll() {

    }

}
