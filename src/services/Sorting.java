package services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import models.Billionaire;

public class Sorting {

    public static void sort(String file) {
        int registros = 4;
        int caminhos = 2;

        String[] tmpFiles = new String[caminhos * 2];

        for (int i = 0; i < caminhos * 2; i++) {
            tmpFiles[i] = "src/database/tmp" + i + ".db";
        }

        distribuicao(file, tmpFiles, registros);
        intercalacao(tmpFiles, registros, caminhos);

    }

    private static void distribuicao(String file, String[] tmpFiles, int registros) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);

            dataInputStream.readInt(); // Pula Cabeçalho

            FileOutputStream[] fileOutputStreams = new FileOutputStream[tmpFiles.length];
            DataOutputStream[] dataOutputStreams = new DataOutputStream[tmpFiles.length];

            // Cria Arquivos tmp de acordo com o tamanho dos caminhos
            for (int i = 0; i < fileOutputStreams.length; i++) {
                fileOutputStreams[i] = new FileOutputStream(tmpFiles[i]);
                dataOutputStreams[i] = new DataOutputStream(fileOutputStreams[i]);

            }

            int objectDistribute = 0;
            int fileDistribute = 0;

            ArrayList<Billionaire> billionaires = new ArrayList<>();
            while (dataInputStream.available() > 0) {

                char lapide = dataInputStream.readChar();
                if (lapide != '*') {
                    Billionaire billionaire = new Billionaire();
                    int objectSize = dataInputStream.readInt();
                    byte[] bt = new byte[objectSize];
                    dataInputStream.read(bt);
                    billionaire.fromByteArray(bt);

                    // billionaires.add(fileDistribute, billionaire);
                    billionaires.add(billionaire);

                    // Conta o registro para distribuir do tamanho que foi pedido
                    objectDistribute++;

                    if (objectDistribute >= registros || dataInputStream.available() <= 0) {

                        Collections.sort(billionaires, (b1, b2) -> {
                            return b1.getId() - b2.getId();
                        });

                        for (int i = 0; i < objectDistribute; i++) {
                            dataOutputStreams[fileDistribute].write(billionaires.get(i).toByteArray()); // Insere objeto
                        }

                        fileDistribute++;
                        // Alterna em qual arquivo será armazenado
                        if (fileDistribute >= tmpFiles.length / 2) {
                            billionaires.clear();
                            fileDistribute = 0;

                        }
                        billionaires.clear();

                        objectDistribute = 0;
                    }

                } else {
                    Billionaire billionaire = new Billionaire();
                    int objectSize = dataInputStream.readInt();
                    byte[] bt = new byte[objectSize];
                    dataInputStream.read(bt);
                    billionaire.jumpElement(bt);
                }

            }

            dataInputStream.close();
        } catch (Exception e) {
            System.out.println("ERRO: " + e);
        }
    }

    private static void intercalacao(String[] tmpFiles, int firstRegistros, int caminhos) {

        int registros = firstRegistros;

        boolean isSorted = false;
        boolean switchFiles = false;

        int contador = 0;

        System.out.println("Registros: " + registros);

        try {

            int inputStart = 0;
            int outputStart = 0; // Lembrar que ele não começa em 0, ele começa do tamanho do inputStart

            if (switchFiles == false) {
                inputStart = 0;
                outputStart = caminhos;

            } else {
                inputStart = caminhos;
                outputStart = 0;
            }

            // O QUE FAZER: MUDAR O OUTPUT / SALVAR A CADA ITERAÇÃO EM CADA ARQUIVO OUTPUT / COLOCAR LIMITE NO FOR PARA EVITAR EOF

            FileOutputStream[] fileOutputStreams = new FileOutputStream[caminhos];
            DataOutputStream[] dataOutputStreams = new DataOutputStream[caminhos];

            for (int i = 0; i < caminhos; i++) {
                fileOutputStreams[i] = new FileOutputStream(tmpFiles[i + outputStart]);
                dataOutputStreams[i] = new DataOutputStream(fileOutputStreams[i]);
            }

            int[] readPointer = new int[caminhos];
            Billionaire[] billionaires = new Billionaire[caminhos];

            for (int i = 0; i < billionaires.length; i++) {
                billionaires[i] = new Billionaire();
            }

            int outputPointer = 0;

            while (isSorted == false) {
                for (int i = 0; i < registros * caminhos; i++) {

                    FileInputStream[] fileInputStreams = new FileInputStream[caminhos];
                    DataInputStream[] dataInputStreams = new DataInputStream[caminhos];

                    for (int j = 0; j < caminhos; j++) {
                        // System.out.println(i);
                        fileInputStreams[j] = new FileInputStream(tmpFiles[j + inputStart]);
                        dataInputStreams[j] = new DataInputStream(fileInputStreams[j]);
                    }

                    for (int j = 0; j < caminhos; j++) {
                        for (int k = 0; k < readPointer[j]; k++) {
                            if (readPointer[j] < registros) {

                                Billionaire billionaireTmp = new Billionaire();

                                dataInputStreams[j].readChar();
                                int objectSize = dataInputStreams[j].readInt();
                                byte[] bt = new byte[objectSize];
                                dataInputStreams[j].read(bt);
                                // billionaires[j].jumpElement(bt);
                                billionaireTmp.fromByteArray(bt);

                                System.out.println(billionaireTmp.getName());
                                
                            }
                        }
                    }
                    System.out.println("-");

                    for (int j = 0; j < caminhos; j++) {
                        dataInputStreams[j].readChar();
                        int objectSize = dataInputStreams[j].readInt();
                        byte[] bt = new byte[objectSize];
                        dataInputStreams[j].read(bt);
                        billionaires[j].fromByteArray(bt);
                    }

                    int menor = 0;
                    for (int j = 1; j < caminhos; j++) {
                        if (billionaires[j].getId() < billionaires[j - 1].getId()) {
                            menor = j;
                        }
                    }

                    dataOutputStreams[outputPointer].write(billionaires[menor].toByteArray()); // Insere objeto

                    readPointer[menor]++;

                }

                // 

                // if (switchFiles == true) {
                //     switchFiles = false;

                // } else {
                //     switchFiles = true;
                // }

                // registros = registros * (tmpFiles.length / 2);

                // contador++;
                // if (contador == 4) {
                //     isSorted = true;
                // }
            }
        } catch (Exception e) {
            System.out.println("ERRO: " + e);
        }
    }

    private static ArrayList<Billionaire> insertionSort(ArrayList<Billionaire> billionaires) {
        for (int i = 1; i < billionaires.size(); i++) {

            for (int j = i; j < billionaires.size(); j++) {

                if (billionaires.get(j).getId() < billionaires.get(j - 1).getId()) {

                    //

                    j--;

                }

            }
        }

        return billionaires;

    }
}
