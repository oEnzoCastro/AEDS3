package services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import models.Billionaire;

public class BillionaireService {

    // Retorna um objeto Billionaire a partir de input de usuário
    public static Billionaire createBillionaire() {

        Scanner scan = new Scanner(System.in);

        int id = scan.nextInt();
        String name = scan.nextLine();
        float netWorth = scan.nextFloat();
        String country = scan.nextLine();
        ArrayList<String> source = null;
        int rank = -1; // Calcular a partir dos outros ranks
        int age = scan.nextInt();
        String residence = scan.nextLine();
        String citizenship = scan.nextLine();
        String status = scan.nextLine();
        int children = scan.nextInt();
        ArrayList<String> education = null;
        Boolean selfMade = scan.nextBoolean();
        LocalDate birthdate = null;

        return null;
    }
}
