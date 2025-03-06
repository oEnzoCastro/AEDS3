import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import services.CSVtoDB;

public class App {

    public static void main(String[] args) throws IOException {

        /* --- Read CSV --- */

        String file = "src/database/billlionaines.db";

        FileOutputStream fileOutputStream = new FileOutputStream(file); // Arquivo de banco de dados a inserir

        // dataOutputStream.writeInt(0);

        int lastId = CSVtoDB.createAll(fileOutputStream); // Read CSV -> Write BD

        System.out.println("Last ID: " + lastId);

        fileOutputStream.close(); // Salva BD apenas no fim do programa para não reescrever

        /* --- Read BD --- */

        // try {

        // FileInputStream fileInputStream = new FileInputStream(file);

        // CSVtoDB.get(fileInputStream);

        // } catch (Exception e) {
        // System.err.println("Erro na Main: " + e);
        // }

    }
}
