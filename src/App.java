import java.io.IOException;
import java.util.Scanner;

import services.CRUD;
import services.Sorting;

public class App {

    public static void main(String[] args) throws IOException {

        // boolean isRunning = true;
        // Scanner scan = new Scanner(System.in);
        // String file = "src/database/billionaires.db";

        // // MENU
        // while (isRunning == true) {
        //     System.out.println("----------------------------------");
        //     System.out.println("Select your options:");
        //     System.out.println("----------------------------------");
        //     System.out.println("1. Create from CSV");
        //     System.out.println("2. Create");
        //     System.out.println("3. Read");
        //     System.out.println("4. Update");
        //     System.out.println("5. Delete");
        //     System.out.println("6. Exit");
        //     System.out.println("----------------------------------");

        //     int option = -1;

        //     option = scan.nextInt();

        //     int id = -1;
        //     switch (option) {
        //         case 1:
        //             // Create from CSV
        //             clearScreen(); // Clear terminal
        //             CRUD.createAll();
        //             break;

        //         case 2:
        //             // Create
        //             break;

        //         case 3:
        //             // Read
        //             System.out.print("ID: ");
        //             id = scan.nextInt();
        //             System.out.println();

        //             clearScreen(); // Clear terminal
        //             CRUD.get(id, file);
        //             break;

        //         case 4:
        //             // Update
        //             System.out.print("ID: ");
        //             id = scan.nextInt();
        //             System.out.println();

        //             clearScreen(); // Clear terminal
        //             CRUD.update(id, file);
        //             break;

        //         case 5:
        //             // Delete
        //             System.out.print("ID: ");
        //             id = scan.nextInt();
        //             System.out.println();

        //             clearScreen(); // Clear terminal
        //             CRUD.delete(id, file);
        //             break;

        //         case 6:
        //             // Exit
        //             clearScreen(); // Clear terminal
        //             System.out.println("Programa Encerrado!");
        //             isRunning = false;
        //             break;

        //         default:
        //             // Opção Inválida
        //             clearScreen(); // Clear terminal
        //             System.err.println("Opção Inválida");
        //             break;
        //     }

        // }

        // scan.close();

        Sorting.sort();
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");  
        System.out.flush(); 
    }

}
