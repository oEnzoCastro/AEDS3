package services;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import models.Billionaire;

public class BillionaireService {

    // Retorna um objeto Billionaire a partir de input de usuário
    public static Billionaire updateBillionaire(Billionaire billionaire) {

        @SuppressWarnings("resource")
        Scanner scan = new Scanner(System.in);

        System.out.println("----------------------------------");
        System.out.println("Insira os dados para o ID: (" + billionaire.getId() + ")");
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
        System.out.print("Source: ");
        ArrayList<String> source = new ArrayList<String>();
            source.add("source");
        System.out.println();
        // Rank
        System.out.print("Rank: ");
        int rank = scan.nextInt(); // Calcular a partir dos outros ranks
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
        System.out.print("Education: ");
        ArrayList<String> education = new ArrayList<String>();
            education.add("education");
        System.out.println();
        // SelfMade
        System.out.print("Selfmade (True/False): ");
        Boolean selfMade = scan.nextBoolean();
        System.out.println();
        // Birthdate (YYYY-MM-DD)
        System.out.print("Birthdate (YYYY-MM-DD): ");
        LocalDate birthdate = LocalDate.now();
        System.out.println();

        Billionaire billionaireTmp = new Billionaire(billionaire.getId(), name, netWorth, country, source, rank, age, residence, citizenship, status, children, education, selfMade, birthdate);

        System.out.println(billionaireTmp);

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
                        randomAccessFile.seek(randomAccessFile.getFilePointer() - Integer.BYTES - Integer.BYTES - Character.BYTES);
                        
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

}
