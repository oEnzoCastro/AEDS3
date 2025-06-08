
// Java
import java.util.Scanner;

import DAO.DAO_InvertedList;
import services.BoyerMoore;
import services.CRUD_BTree;
// Services
import services.CRUD_Hash;
import services.LZW;
import services.Huffman;
import services.KMP;
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
        String fileTreeCompressed = "src/database/billionairesTreeCompressed.db";
        String fileHashCompressed = "src/database/billionairesHashCompressed.db";

        // Start

        while (isRunning) {

            int id;
            int algoritmo;
            String key;
            String padrao;

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

                case 8: // Compactar Huffman
                    algoritmo = selectAlgorithm(scanner);

                    String fileCompactada;
                    try {
                        if (algoritmo == 1) {
                            fileCompactada = Huffman.comprimir(fileTree);
                            Huffman.taxaCompressao(fileTree, fileCompactada);
                        } else if (algoritmo == 2) {
                            fileCompactada = Huffman.comprimir(fileHash);
                            Huffman.taxaCompressao(fileHash, fileCompactada);
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                    break;

                case 9: // Decompactar Huffman
                    algoritmo = selectAlgorithm(scanner);
                    String fileDescompactada;
                    try {
                        if (algoritmo == 1) {
                            fileDescompactada = Huffman.descomprimir(fileTree);
                            Huffman.compararRecuperado(fileTree, fileDescompactada);
                        } else if (algoritmo == 2) {
                            fileDescompactada = Huffman.descomprimir(fileHash);
                            Huffman.compararRecuperado(fileHash, fileDescompactada);
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                    break;

                case 10: // Compactar LZW

                    algoritmo = selectAlgorithm(scanner);

                    if (algoritmo == 1) {
                        LZW.compactar(fileTree, fileTreeCompressed);
                    } else if (algoritmo == 2) {
                        LZW.compactar(fileHash, fileHashCompressed);
                    }

                    break;
                case 11: // Descompactar LZW

                    algoritmo = selectAlgorithm(scanner);

                    if (algoritmo == 1) {
                        LZW.extrair(fileTree, fileTreeCompressed);
                    } else if (algoritmo == 2) {
                        LZW.extrair(fileHash, fileHashCompressed);
                    }

                    break;

                case 12: // Busca KMP
                    int resp = 0;
                    algoritmo = selectAlgorithm(scanner);
                    System.out.println("Digite o padrão a ser procurado: ");
                    scanner.nextLine();
                    padrao = scanner.nextLine();
                    try {

                        if (algoritmo == 1) {
                            resp = KMP.buscarKMP(padrao, fileTree);
                        } else if (algoritmo == 2) {
                            resp = KMP.buscarKMP(padrao, fileHash);
                        }

                    } catch (Exception e) {
                       System.err.println("ERRO: " + e);
                    }

                    if (resp != 0) {
                        System.out.println("Padrão encontrado " + resp + " vez(es)!");
                    } else {
                        System.out.println("Padrão não encontrado");
                    }
                    break;

                case 13: // Busca Boyer-Moore

                    algoritmo = selectAlgorithm(scanner);

                    System.out.println("Digite o padrão a ser procurado: ");
                    scanner.nextLine();
                    padrao = scanner.nextLine();

                    if (algoritmo == 1) {
                        BoyerMoore.pesquisar(padrao, fileTree);
                    } else if (algoritmo == 2) {
                        BoyerMoore.pesquisar(padrao, fileHash);

                    }
                    break;

                default:
                    break;
            }

        }
        // End

        // *.close()

    }

    public static int printMenu(Scanner scanner) {

        System.out.println("|---------------------------------------------|");
        System.out.println("|            Select your option:              |");
        System.out.println("|----|----------------------------------------|");
        System.out.println("| 1  | Create from CSV                        |");
        System.out.println("|----|----------------------------------------|");
        System.out.println("| 2  | Create                                 |");
        System.out.println("|----|----------------------------------------|");
        System.out.println("| 3  | Read                                   |");
        System.out.println("|----|----------------------------------------|");
        System.out.println("| 4  | Update                                 |");
        System.out.println("|----|----------------------------------------|");
        System.out.println("| 5  | Delete                                 |");
        System.out.println("|----|----------------------------------------|");
        System.out.println("| 6  | Sort                                   |");
        System.out.println("|----|----------------------------------------|");
        System.out.println("| 7  | Procurar Palavra nas Listas Invertidas |");
        System.out.println("|----|----------------------------------------|");
        System.out.println("| 8  | Compactar Huffman                      |");
        System.out.println("|----|----------------------------------------|");
        System.out.println("| 9  | Descompactar Huffman                   |");
        System.out.println("|----|----------------------------------------|");
        System.out.println("| 10 | Compactar LZW                          |");
        System.out.println("|----|----------------------------------------|");
        System.out.println("| 11 | Descompactar LZW                       |");
        System.out.println("|----|----------------------------------------|");
        System.out.println("| 12 | Busca KMP                              |");
        System.out.println("|----|----------------------------------------|");
        System.out.println("| 13 | Busca Boyer-Moore                      |");
        System.out.println("|----|----------------------------------------|");
        System.out.println("| 0  | Exit                                   |");
        System.out.println("|----|----------------------------------------|");
        System.out.print("| Opção: ");
        int res = scanner.nextInt();
        System.out.println("|---------------------------------------------|");
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
