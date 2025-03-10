package services;

import java.io.*;
import java.util.*;

import models.Billionaire;

public class Sorting {
    public static void sort(){
        distribuicao();
        intercalacao();
    }

    private static void distribuicao() {
        String file = "src/database/billionaires.db";
        String temp1 = "src/database/temp1.db";
        String temp2 = "src/database/temp2.db";

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);

            FileOutputStream fileOutputStream1 = new FileOutputStream(temp1);
            DataOutputStream dataOutputStream1 = new DataOutputStream(fileOutputStream1);

            FileOutputStream fileOutputStream2 = new FileOutputStream(temp2);
            DataOutputStream dataOutputStream2 = new DataOutputStream(fileOutputStream2);

            dataInputStream.readInt(); // Ignorar o primeiro int

            List<Billionaire> billionaires = new ArrayList<>();
            boolean writeTemp1 = true;

            int num = 50; // Número de registros lidos

            while (dataInputStream.available() > 0) {
                billionaires.clear();

                for (int i = 0; i < num; i++) { // Lê até 50 registros
                    if (dataInputStream.available() <= 0) break; // Evita ler além do arquivo se ele tiver acabado

                    char lapide = dataInputStream.readChar(); // Ler Lapide
                    int len = dataInputStream.readInt(); // Ler Tamanho do Obj

                    byte[] bt = new byte[len];
                    dataInputStream.read(bt);

                    if (lapide != '*') { // Ignora registros com lapide 
                        Billionaire b = new Billionaire();
                        b.fromByteArray(bt);
                        billionaires.add(b);
                    }
                }

                quickSort(billionaires, 0, billionaires.size() - 1); // Ordenação crescente por ID

                DataOutputStream outputStream = writeTemp1 ? dataOutputStream1 : dataOutputStream2; // Testa em qual arquivo vai inserir

                for (Billionaire b : billionaires) {
                    byte[] tmp = b.toByteArray();
                    outputStream.write(tmp);
                }

                writeTemp1 = !writeTemp1; // Alterna os arquivos de saída
            }

            dataInputStream.close();
            dataOutputStream1.close();
            dataOutputStream2.close();
        } catch (Exception e) {
            System.err.println("Erro Sorting.distribuicao: " + e);
        }
    }

    private static void intercalacao() {
        String temp1 = "src/database/temp1.db";
        String temp2 = "src/database/temp2.db";
        String temp3 = "src/database/temp3.db";
        String temp4 = "src/database/temp4.db";
    
        try {
            FileInputStream fileInputStream1 = new FileInputStream(temp1);
            DataInputStream dataInputStream1 = new DataInputStream(fileInputStream1);
    
            FileInputStream fileInputStream2 = new FileInputStream(temp2);
            DataInputStream dataInputStream2 = new DataInputStream(fileInputStream2);
    
            FileOutputStream fileOutputStream1 = new FileOutputStream(temp3);
            DataOutputStream dataOutputStream1 = new DataOutputStream(fileOutputStream1);
    
            FileOutputStream fileOutputStream2 = new FileOutputStream(temp4);
            DataOutputStream dataOutputStream2 = new DataOutputStream(fileOutputStream2);
    
            boolean writeTemp3 = true; // Alterna entre temp3 e temp4
            List<Billionaire> list1 = new ArrayList<>();
            List<Billionaire> list2 = new ArrayList<>();
    
            while (dataInputStream1.available() > 0 || dataInputStream2.available() > 0) {
                list1.clear();
                list2.clear();
    
                // Lê até 50 registros do temp1
                for (int i = 0; i < 50 && dataInputStream1.available() > 0; i++) {
                    char lapide = dataInputStream1.readChar();
                    int len = dataInputStream1.readInt();
                    byte[] bt = new byte[len];
                    dataInputStream1.read(bt);
    
                    if (lapide != '*') {
                        Billionaire b = new Billionaire();
                        b.fromByteArray(bt);
                        list1.add(b);
                    }
                }
    
                // Lê até 50 registros do temp2
                for (int i = 0; i < 50 && dataInputStream2.available() > 0; i++) {
                    char lapide = dataInputStream2.readChar();
                    int len = dataInputStream2.readInt();
                    byte[] bt = new byte[len];
                    dataInputStream2.read(bt);
    
                    if (lapide != '*') {
                        Billionaire b = new Billionaire();
                        b.fromByteArray(bt);
                        list2.add(b);
                    }
                }
    
                // Lista para armazenar os 100 registros ordenados
                List<Billionaire> mergedList = new ArrayList<>();
                int i = 0, j = 0;
    
                // Intercalação dos registros de list1 e list2
                while (i < list1.size() && j < list2.size()) {
                    if (list1.get(i).getId() < list2.get(j).getId()) {
                        mergedList.add(list1.get(i++));
                    } else {
                        mergedList.add(list2.get(j++));
                    }
                }
    
                // Adiciona os registros restantes de list1
                while (i < list1.size()) {
                    mergedList.add(list1.get(i++));
                }
    
                // Adiciona os registros restantes de list2
                while (j < list2.size()) {
                    mergedList.add(list2.get(j++));
                }
    
                // Escreve os 100 registros ordenados em um dos arquivos temp3 ou temp4
                DataOutputStream outputStream = writeTemp3 ? dataOutputStream1 : dataOutputStream2;
                for (Billionaire b : mergedList) {
                    byte[] tmp = b.toByteArray();
                    outputStream.writeChar(' '); // Marca lápide válida
                    outputStream.writeInt(tmp.length);
                    outputStream.write(tmp);
                }
    
                writeTemp3 = !writeTemp3; // Alterna os arquivos de saída
            }
    
            dataInputStream1.close();
            dataInputStream2.close();
            dataOutputStream1.close();
            dataOutputStream2.close();
    
        } catch (Exception e) {
            System.err.println("Erro Sorting.intercalacao: " + e);
        }
    }    

    private static void quickSort(List<Billionaire> list, int low, int high) {
        if (low < high) {
            int pi = partition(list, low, high);
            quickSort(list, low, pi - 1);
            quickSort(list, pi + 1, high);
        }
    }

    private static int partition(List<Billionaire> list, int low, int high) {
        Billionaire pivot = list.get(high);
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (list.get(j).getId() < pivot.getId()) { // Ordem crescente por ID
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }
}
