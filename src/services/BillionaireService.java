package services;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import models.Billionaire;

public class BillionaireService {

    public static Billionaire read(String file, long posicao) throws IOException {

        Billionaire billionaireTmp = new Billionaire();

        RandomAccessFile raf = new RandomAccessFile(file, "rw");

        raf.seek(posicao);

        byte[] bt;
        int len;
        char lapide;

        lapide = raf.readChar(); // Ler Lapide
        len = raf.readInt(); // Ler Tamanho Obj

        bt = new byte[len];
        raf.read(bt);

        billionaireTmp.fromByteArray(bt);

        // Confere se o objeto está inativo, se sim, retornar null
        if (lapide == '*') {
            raf.close();
            return null;
        }

        raf.close();
        return billionaireTmp;
    }


    // Retorna um objeto Billionaire a partir de input de usuário
    public static Billionaire updateBillionaire(Billionaire billionaire) {

        @SuppressWarnings("resource")
        Scanner scan = new Scanner(System.in);

        System.out.println("----------------------------------");
        System.out.println("Insira os dados para o ID: (" + billionaire.getId() + ")");
        System.out.println("----------------------------------");

        // Name
        String name = billionaire.getName();
        System.out.print("Name: " + name);
        System.out.println();
        // NetWorth
        System.out.print("NetWorth: ");
        float netWorth = Float.parseFloat(scan.nextLine());
        System.out.println();
        // Country
        String country = billionaire.getCountry();
        System.out.println("Country: " + country);
        // Source
        ArrayList<String> source = billionaire.getSource();
        System.out.print("Sources: ");
        for (String string : source) {
            System.out.print(string + " ");
        }
        boolean addList;
        System.out.println();
        // Rank
        int rank = getRank(netWorth); // Calcular a partir dos outros ranks
        System.out.print("Rank: " + rank);
        System.out.println();
        // Age
        System.out.print("Age: ");
        int age = scan.nextInt();
        scan.nextLine(); // Clear Buffer
        System.out.println();
        // Residence
        System.out.print("Residence: ");
        String residence = scan.nextLine();
        System.out.println();
        // Citizenship
        System.out.print("Citizenship: ");
        String citizenship = scan.nextLine();
        System.out.println();
        // Status
        System.out.print("Status: ");
        String status = scan.nextLine();
        System.out.println();
        // Children
        System.out.print("Children: ");
        int children = scan.nextInt();
        scan.nextLine(); // Clear Buffer
        System.out.println();
        // Education
        System.out.println("- Education -");
        addList = true;
        ArrayList<String> education = new ArrayList<String>();
        while (addList == true) {
            System.out.print("Digite o valor de Education ou 0 para continuar: ");

            String educationString = scan.nextLine();

            if (educationString.equals("0")) {
                addList = false;
            } else {
                education.add(educationString);
            }

            System.out.println();

        }
        // SelfMade
        Boolean selfMade = billionaire.getSelfMade();
        System.out.print("Selfmade (True/False): " + selfMade);

        System.out.println();
        // Birthdate (YYYY-MM-DD)
        System.out.print("Birthdate (YYYY-MM-DD): ");
        LocalDate birthdate = billionaire.getBirthdate();
        System.out.println();

        Billionaire billionaireTmp = new Billionaire(billionaire.getId(), name, netWorth, country, source, rank, age,
                residence, citizenship, status, children, education, selfMade, birthdate);

        System.out.println(billionaireTmp); // TODO Delete

        return billionaireTmp;
    }

    public static Billionaire newBillionaire(int id) {

        @SuppressWarnings("resource")
        Scanner scan = new Scanner(System.in);

        System.out.println("----------------------------------");
        System.out.println("Insira os dados para o Novo Bilionário:");
        System.out.println("----------------------------------");

        // Name
        System.out.print("Name: ");
        String name = scan.nextLine();
        System.out.println();
        // NetWorth
        System.out.print("NetWorth: ");
        float netWorth = Float.parseFloat(scan.nextLine());
        System.out.println();
        // Country
        System.out.print("Country: ");
        String country = scan.nextLine();
        System.out.println();
        // Source
        System.out.println("- Source -");
        boolean addList = true;
        ArrayList<String> source = new ArrayList<String>();
        while (addList == true) {
            System.out.print("Digite o valor de Source ou 0 para continuar: ");

            String sourceString = scan.nextLine();

            if (sourceString.equals("0")) {
                addList = false;
            } else {
                source.add(sourceString);
            }

            System.out.println();

        }
        // Rank
        System.out.print("Rank: ");
        // int rank = getRank(netWorth); // Calcular a partir dos outros ranks
        int rank = scan.nextInt(); // Calcular a partir dos outros ranks
        scan.nextLine(); // Clear Buffer
        System.out.println();
        // Age
        System.out.print("Age: ");
        int age = scan.nextInt();
        scan.nextLine(); // Clear Buffer
        System.out.println();
        // Residence
        System.out.print("Residence: ");
        String residence = scan.nextLine();
        System.out.println();
        // Citizenship
        System.out.print("Citizenship: ");
        String citizenship = scan.nextLine();
        System.out.println();
        // Status
        System.out.print("Status: ");
        String status = scan.nextLine();
        System.out.println();
        // Children
        System.out.print("Children: ");
        int children = scan.nextInt();
        scan.nextLine(); // Clear Buffer
        System.out.println();
        // Education
        System.out.println("- Education -");
        addList = true;
        ArrayList<String> education = new ArrayList<String>();
        while (addList == true) {
            System.out.print("Digite o valor de Education ou 0 para continuar: ");

            String educationString = scan.nextLine();

            if (educationString.equals("0")) {
                addList = false;
            } else {
                education.add(educationString);
            }

            System.out.println();

        }
        // SelfMade
        System.out.print("Selfmade (True/False): ");
        Boolean selfMade = scan.nextBoolean();
        System.out.println();
        // Birthdate (YYYY-MM-DD)
        System.out.print("Birthdate (YYYY-MM-DD): ");
        scan.nextLine();
        String dateString = scan.nextLine();
        LocalDate birthdate = LocalDate.parse(dateString);
        System.out.println();

        Billionaire billionaireTmp = new Billionaire(id, name, netWorth, country, source, rank, age,
                residence, citizenship, status, children, education, selfMade, birthdate);

        return billionaireTmp;
    }

    public static long findBillionaireByte(int id, String file) {

        try {

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            randomAccessFile.seek(Integer.BYTES);

            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {

                char lapide = randomAccessFile.readChar(); // Lapide
                int size = randomAccessFile.readInt(); // Object Size

                // Id (User Input) == Id (Database)
                if (id == randomAccessFile.readInt()) {
                    // Caso o objeto já tenha sido removido
                    if (lapide == '*') {

                    } else {

                        // Pega posição atual e volta os Bytes que foram lidos para alterar a lapide
                        randomAccessFile.seek(
                                randomAccessFile.getFilePointer() - Integer.BYTES - Integer.BYTES - Character.BYTES);

                        long filePointer = randomAccessFile.getFilePointer();

                        randomAccessFile.close();

                        return filePointer;
                    }
                }

                randomAccessFile.skipBytes(size - Integer.BYTES);

            }

            randomAccessFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int getBillionaireSize(int id, String file) {
        try {

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            randomAccessFile.seek(Integer.BYTES);

            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {

                char lapide = randomAccessFile.readChar(); // Lapide
                int size = randomAccessFile.readInt(); // Object Size

                // Id (User Input) == Id (Database)
                if (id == randomAccessFile.readInt()) {
                    // Caso o objeto já tenha sido removido
                    if (lapide == '*') {

                    } else {

                        randomAccessFile.close();

                        return size;
                    }
                }

                randomAccessFile.skipBytes(size - Integer.BYTES);

            }

            randomAccessFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;

    }

    public static int getRank(float NetWorth) {
        return 100;
    }
}
