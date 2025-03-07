import java.io.IOException;
import java.util.Scanner;

import services.CRUD;

public class App {

    public static void main(String[] args) throws IOException {

        boolean isRunning = true;
        Scanner scan = new Scanner(System.in);
        String file = "src/database/billionaires.db";

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
            System.out.println("----------------------------------");

            int option = -1;

            option = scan.nextInt();

            switch (option) {
                case 1:
                    // Create from CSV
                    CRUD.createAll();
                    break;

                case 2:
                    // Create
                    break;

                case 3:
                    // Read
                    System.out.print("ID: ");
                    int id = scan.nextInt();
                    System.out.println();

                    CRUD.get(id, file);
                    break;

                case 4:
                    // Update
                    break;

                case 5:
                    // Delete
                    break;

                case 6:
                    // Exit
                    System.out.println("Programa Encerrado!");
                    isRunning = false;
                    break;

                default:
                    // Opção Inválida
                    System.err.println("Opção Inválida");
                    break;
            }

        }

        scan.close();

    }

}
