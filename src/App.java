import DAO.DAO_InvertedList;
import java.io.IOException;
import java.util.Scanner;
import services.CRUD_BTree;
import services.CRUD_Hash;
import services.Sorting;

public class App {

    public static void main(String[] args) throws IOException {

        boolean isRunning = true;
        Scanner scan = new Scanner(System.in);
        String fileTree = "src/database/billionairesTree.db";
        String fileHash = "src/database/billionairesHash.db";

        while (isRunning) {
            printMenu();
            int option = scan.nextInt();
            int id;
            int algoritmo;
            String key;

            switch (option) {
                case 1: // Create All
                    algoritmo = selectAlgorithm(scan);
                    if (algoritmo == 1) {
                        CRUD_BTree.createAll();
                    } else if (algoritmo == 2) {
                        DAO_InvertedList.limparLista();
                        CRUD_Hash.createAll();
                    }
                    clearScreen();
                    break;

                case 2: // Create
                    algoritmo = selectAlgorithm(scan);
                    if (algoritmo == 1) {
                        CRUD_BTree.create(fileTree);
                    } else if (algoritmo == 2) {
                        CRUD_Hash.create(fileHash);
                    }
                    break;

                case 3: // Read
                    algoritmo = selectAlgorithm(scan);
                    System.out.print("Chave: ");
                    clearInputBuffer(scan);
                    id = scan.nextInt();
                    clearScreen();
                    if (algoritmo == 1) {
                        CRUD_BTree.getIndex(id);
                    } else if (algoritmo == 2) {
                        CRUD_Hash.getIndex(id);
                    }
                    break;

                case 4: // Update
                    algoritmo = selectAlgorithm(scan);
                    System.out.print("Chave: ");
                    clearInputBuffer(scan);
                    key = scan.nextLine();
                    clearScreen();
                    if (algoritmo == 1) {
                        CRUD_BTree.update(key, fileTree);
                    } else if (algoritmo == 2) {
                        CRUD_Hash.update(key, fileHash);
                    }
                    break;

                case 5: // Delete

                    algoritmo = selectAlgorithm(scan);
                    System.out.print("ID: ");
                    clearInputBuffer(scan);
                    id = scan.nextInt();
                    clearScreen();
                    if (algoritmo == 1)
                        CRUD_BTree.delete(id, fileTree);
                    else if (algoritmo == 2) {
                        CRUD_Hash.delete(id, fileHash);
                        DAO_InvertedList.deleteIL(id);
                        DAO_InvertedList.deleteIL(id);
                    }

                    break;

                case 6: // Sort
                    clearScreen();
                    System.out.print("Registros: ");
                    int registros = scan.nextInt();
                    clearInputBuffer(scan);
                    System.out.print("Caminhos: ");
                    int caminhos = scan.nextInt();
                    Sorting.sort(fileTree, registros, caminhos);
                    Sorting.sort(fileHash, registros, caminhos);
                    break;

                case 7: // Lista Invertida
                    clearScreen();
                    scan.nextLine();
                    System.out.print("Palavra a ser pesquisada: ");
                    String palavra = scan.nextLine();
                    System.out.println("Escolha a lista a ser pesquisada:");
                    System.out.println("1. Para coluna Source");
                    System.out.println("2. Para coluna Country");
                    int code = scan.nextInt();
                    DAO_InvertedList.searchIL(palavra, code);
                    break;

                case 8:
                    clearScreen();
                    System.out.println("Programa Encerrado!");
                    isRunning = false;
                    break;

                default:
                    clearScreen();
                    System.err.println("Opção Inválida");
                    break;
            }
        }

        scan.close();
    }

    public static void printMenu() {
        System.out.println("----------------------------------");
        System.out.println("Select your options:");
        System.out.println("----------------------------------");
        System.out.println("1. Create from CSV");
        System.out.println("2. Create");
        System.out.println("3. Read");
        System.out.println("4. Update");
        System.out.println("5. Delete");
        System.out.println("6. Sort");
        System.out.println("7. Procurar Palavra nas Listas Invertidas");
        System.out.println("8. Exit");
        System.out.println("----------------------------------");
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

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
