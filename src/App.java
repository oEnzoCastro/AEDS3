import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import services.CRUD;

public class App {

    public static void main(String[] args) throws IOException {

        boolean isRunning = true;

        // MENU
        while (isRunning == true) {
            System.out.println("----------------------------------");
            System.out.println("Select your options:");
            System.out.println("----------------------------------");
            System.out.println("1. Create from CSV");
            System.out.println("2. Create");
            System.out.println("3. Read");
            System.out.println("4. Update");
            System.out.println("5. Delete");
            System.out.println("6. Exit");

            int option = -1;



        }

        /* --- Read CSV --- */

        String file = "src/database/billlionaines.db";

        FileOutputStream fileOutputStream = new FileOutputStream(file); // Arquivo de banco de dados a inserir

        // dataOutputStream.writeInt(0);

        int lastId = CRUD.createAll(fileOutputStream); // Read CSV -> Write BD

        System.out.println("Last ID: " + lastId);

        fileOutputStream.close(); // Salva BD apenas no fim do programa para não reescrever

        /* --- Read BD --- */

        try {
            int id = 2755;

            FileInputStream fileInputStream = new FileInputStream(file);

            CRUD.get(fileInputStream, id);

        } catch (Exception e) {
            System.err.println("Erro na Main: " + e);
        }

    }

}
