
// Java
import java.util.Scanner;

import DAO.DAO_InvertedList;
import services.CRUD_BTree;
// Services
import services.CRUD_Hash;
import services.Sorting;

/**
 * 
 */
public class App {

    public static void main(String[] args) {

        // *.new()
        boolean isRunning = true;
        Scanner scanner = new Scanner(System.in);
        String fileTree = "src/database/billionairesTree.db";
        String fileHash = "src/database/billionairesHash.db";
        // Start

        while (isRunning) {

            int id;
            int algoritmo;
            String key;

            switch (printMenu(scanner)) {
                case 0: // Exit

                    System.out.println("Exiting the application...");
                    isRunning = false;
                    break;

                case 1: // Create All

                    algoritmo = selectAlgorithm(scanner);
                    if (algoritmo == 1) {
                        CRUD_BTree.createAll();
                    } else if (algoritmo == 2) {
                        DAO_InvertedList.limparLista();
                        CRUD_Hash.createAll();
                    }
                    break;

                case 2: // Create One
                    algoritmo = selectAlgorithm(scanner);
                    if (algoritmo == 1) {
                        CRUD_BTree.create(fileTree);
                    } else if (algoritmo == 2) {
                        CRUD_Hash.create(fileHash);
                    }
                    break;
                case 3: // Read
                    algoritmo = selectAlgorithm(scanner);
                    System.out.print("Chave: ");
                    clearInputBuffer(scanner);
                    id = scanner.nextInt();
                    if (algoritmo == 1) {
                        CRUD_BTree.getIndex(id);
                    } else if (algoritmo == 2) {
                        CRUD_Hash.getIndex(id);
                    }
                    break;

                case 4: // Update
                    algoritmo = selectAlgorithm(scanner);
                    System.out.print("Chave: ");
                    clearInputBuffer(scanner);
                    key = scanner.nextLine();
                    if (algoritmo == 1) {
                        CRUD_BTree.update(key, fileTree);
                    } else if (algoritmo == 2) {
                        CRUD_Hash.update(key, fileHash);
                    }
                    break;

                case 5: // Delete

                    algoritmo = selectAlgorithm(scanner);
                    System.out.print("ID: ");
                    clearInputBuffer(scanner);
                    id = scanner.nextInt();
                    if (algoritmo == 1)
                        CRUD_BTree.delete(id, fileTree);
                    else if (algoritmo == 2) {
                        CRUD_Hash.delete(id, fileHash);
                        DAO_InvertedList.deleteIL(id);
                        DAO_InvertedList.deleteIL(id);
                    }

                    break;

                case 6: // Sort
                    System.out.print("Registros: ");
                    int registros = scanner.nextInt();
                    clearInputBuffer(scanner);
                    System.out.print("Caminhos: ");
                    int caminhos = scanner.nextInt();
                    Sorting.sort(fileTree, registros, caminhos);
                    Sorting.sort(fileHash, registros, caminhos);
                    break;

                case 7: // Lista Invertida
                    scanner.nextLine();
                    System.out.print("Palavra a ser pesquisada: ");
                    String palavra = scanner.nextLine();
                    System.out.println("Escolha a lista a ser pesquisada:");
                    System.out.println("1. Para coluna Source");
                    System.out.println("2. Para coluna Country");
                    int code = scanner.nextInt();
                    DAO_InvertedList.searchIL(palavra, code);
                    break;

                default:
                    break;
            }

        }
        // End

        // *.close()

    }

    public static int printMenu(Scanner scanner) {

        System.out.println("|————————————————————————————————————————————|");
        System.out.println("|            Select your option:             |");
        System.out.println("|———|————————————————————————————————————————|");
        System.out.println("| 1 | Create from CSV                        |");
        System.out.println("|———|————————————————————————————————————————|");
        System.out.println("| 2 | Create                                 |");
        System.out.println("|———|————————————————————————————————————————|");
        System.out.println("| 3 | Read                                   |");
        System.out.println("|———|————————————————————————————————————————|");
        System.out.println("| 4 | Update                                 |");
        System.out.println("|———|————————————————————————————————————————|");
        System.out.println("| 5 | Delete                                 |");
        System.out.println("|———|————————————————————————————————————————|");
        System.out.println("| 6 | Sort                                   |");
        System.out.println("|———|————————————————————————————————————————|");
        System.out.println("| 7 | Procurar Palavra nas Listas Invertidas |");
        System.out.println("|———|————————————————————————————————————————|");
        System.out.println("| 8 | Exit                                   |");
        System.out.println("|———|————————————————————————————————————————|");
        System.out.print("| Opção: ");
        int res = scanner.nextInt();
        System.out.println("|————————————————————————————————————————————|");
        // scanner.close();

        return res;

    }

    public static int selectAlgorithm(Scanner scan) {
        System.out.println("----------------------------------");
        System.out.println("Escolha qual algoritmo: ");
        System.out.println("1 -> Árvore B");
        System.out.println("2 -> Tabela Hash");
        int choice = scan.nextInt();
        switch (choice) {
            case 1:
                return choice;
            case 2:
                return choice;

            default:
                System.out.println("----------------------------------");
                System.out.println("Opção Inválida!");
                return selectAlgorithm(scan);
        }
    }

    public static void clearInputBuffer(Scanner scan) {
        scan.nextLine(); // Clear buffer
    }

}
