package services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

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
        // intercalacao(tmpFiles, registros); TODO

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

            
            Billionaire[] billionaires = new Billionaire[registros];
            while (dataInputStream.available() > 0) {

                char lapide = dataInputStream.readChar();
                if (lapide != '*') {
                    Billionaire billionaire = new Billionaire();
                    int objectSize = dataInputStream.readInt();
                    byte[] bt = new byte[objectSize];
                    dataInputStream.read(bt);
                    billionaire.fromByteArray(bt);

                    billionaires[objectDistribute] = billionaire;
                    
                    // Conta o registro para distribuir do tamanho que foi pedido
                    objectDistribute++;

                    if (objectDistribute >= registros || dataInputStream.available() <= 0) {

                        // billionaires = insertionSort(billionaires);

                        for (int i = 0; i < objectDistribute; i++) {
                            dataOutputStreams[fileDistribute].write(billionaires[i].toByteArray()); // Insere objeto
                        }

                        fileDistribute++;
                        // Alterna em qual arquivo será armazenado
                        if (fileDistribute >= tmpFiles.length / 2) {
                            fileDistribute = 0;
                        }

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

    private static void intercalacao(String[] tmpFiles, int registros) {

        boolean isSorted = false;
        boolean switchFiles = false;

        while (isSorted == false) {

            try {

                int inputSize;
                int outputSize; // Lembrar que ele não começa em 0, ele começa do tamanho do inputSize

                if (switchFiles == false) {
                    inputSize = tmpFiles.length / 2;
                    outputSize = tmpFiles.length;
                } else {
                    inputSize = tmpFiles.length;
                    outputSize = tmpFiles.length / 2;
                }

                FileInputStream[] fileInputStreams = new FileInputStream[inputSize];
                DataInputStream[] dataInputStreams = new DataInputStream[inputSize];

                for (int i = 0; i < fileInputStreams.length; i++) {
                    fileInputStreams[i] = new FileInputStream(tmpFiles[i]);
                    dataInputStreams[i] = new DataInputStream(fileInputStreams[i]);
                }

                FileOutputStream[] fileOutputStreams = new FileOutputStream[outputSize];
                DataOutputStream[] dataOutputStreams = new DataOutputStream[outputSize];

                for (int i = inputSize; i < fileOutputStreams.length; i++) {
                    fileOutputStreams[i] = new FileOutputStream(tmpFiles[i]);
                    dataOutputStreams[i] = new DataOutputStream(fileOutputStreams[i]);

                }

                System.out.println("A");

                isSorted = true;

            } catch (Exception e) {

            }
        }
    }

    private static Billionaire[] insertionSort(Billionaire[] billionaires) {
        for (int i = 1; i < billionaires.length; i++) {

            for (int j = i; j < billionaires.length; j++) {

                if (billionaires[j].getId() < billionaires[j - 1].getId()) {

                    Billionaire billionaireTmp = billionaires[j];
                    billionaires[j] = billionaires[j - 1];
                    billionaires[j - 1] = billionaireTmp;

                    j--;

                }

            }
        }

        return billionaires;

    }
}
