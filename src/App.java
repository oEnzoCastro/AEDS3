import java.io.IOException;
import java.util.Scanner;
import services.CRUD;
import services.CRUD_BTree;
import services.Sorting;

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
            System.out.println("6. Sort");
            System.out.println("7. Exit");
            System.out.println("----------------------------------");

            int option = -1;

            option = scan.nextInt();

            int id = -1;
            int algoritmo = -1;
            @SuppressWarnings("unused")
            String key = "";
            switch (option) {
                case 1:

                    System.out.println("----------------------------------");
                    System.out.println("Escolha qual algoritmo: ");
                    System.out.println("1 -> Árvore B");
                    System.out.println("2 -> Tabela Hash");
                    scan.nextLine(); // Clear Buffer
                    algoritmo = scan.nextInt();
                    System.out.println();

                    switch (algoritmo) {
                        case 1:
                            // CRUD Árvore B
                            CRUD_BTree.createAll();
                            break;
                        case 2:
                            // CRUD Tabela Hash
                            CRUD.createAll();
                            break;

                        default:
                            break;
                    }
                    // Create from CSV
                    clearScreen(); // Clear terminal
                    break;

                case 2:

                    System.out.println("----------------------------------");
                    System.out.println("Escolha qual algoritmo: ");
                    System.out.println("1 -> Árvore B");
                    System.out.println("2 -> Tabela Hash");
                    scan.nextLine(); // Clear Buffer
                    algoritmo = scan.nextInt();
                    System.out.println();

                    switch (algoritmo) {
                        case 1:
                            // CRUD Árvore B
                            CRUD_BTree.create(file);
                            break;
                        case 2:
                            // CRUD Tabela Hash
                            CRUD.create(file);
                            break;

                        default:
                            break;
                    }
                    break;

                case 3:

                    System.out.println("----------------------------------");
                    System.out.println("Escolha qual algoritmo: ");
                    System.out.println("1 -> Árvore B");
                    System.out.println("2 -> Tabela Hash");
                    scan.nextLine(); // Clear Buffer
                    algoritmo = scan.nextInt();
                    System.out.println();

                    switch (algoritmo) {
                        case 1:
                            // Read
                            System.out.print("Chave: ");
                            scan.nextLine(); // Clear Buffer
                            id = scan.nextInt();
                            System.out.println();

                            clearScreen(); // Clear terminal
                            CRUD_BTree.getIndex(id);
                            break;
                        case 2:
                            // Read
                            System.out.print("Chave: ");
                            scan.nextLine(); // Clear Buffer
                            id = scan.nextInt();
                            System.out.println();

                            clearScreen(); // Clear terminal
                            CRUD.getIndex(id);
                            break;

                        default:
                            break;
                        }
                        break;
                        
                case 4:
                    // Update
                    System.out.print("ID: ");
                    scan.nextLine(); // Clear Buffer
                    key = scan.nextLine();
                    System.out.println();

                    clearScreen(); // Clear terminal
                    break;

                case 5:
                    // Delete
                    System.out.print("ID: ");
                    id = scan.nextInt();
                    System.out.println();

                    clearScreen(); // Clear terminal
                    CRUD_BTree.delete(id, file);
                    break;

                case 6:
                    // Sort
                    clearScreen(); // Clear terminal
                    System.out.print("Registros: ");
                    int registros = scan.nextInt();
                    System.out.println();
                    System.out.print("Caminhos: ");
                    int caminhos = scan.nextInt();
                    System.out.println();
                    Sorting.sort(file, registros, caminhos); // Sort Function
                    break;

                case 7:
                    // Exit
                    clearScreen(); // Clear terminal
                    System.out.println("Programa Encerrado!");
                    isRunning = false;
                    break;

                default:
                    // Opção Inválida
                    clearScreen(); // Clear terminal
                    System.err.println("Opção Inválida");
                    break;
            }

        }

        scan.close();

    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

}
