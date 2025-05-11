import java.util.Scanner;

public class App {

    public static void main(String[] args) {

        // *.new()

        // Start

        switch (printMenu()) {
            case 1: // Create All

                break;

            case 2: // Create One

                break;

            default:
                break;
        }

        // End

        // *.close()

    }

    public static int printMenu() {

        Scanner scanner = new Scanner(System.in);

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
        scanner.close();

        return res;

    }
}
