package services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;

import models.Billionaire;

public class Sorting {

    public static void sort(String file) {
        int registros = 6;
        int caminhos = 3;

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

        boolean switchFiles = false;
        
        int contador = 0;
        
        System.out.println("Registros: " + registros);
        
        try {
            
            while (contador < 2) {

                boolean isSorted = false;

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
                RandomAccessFile[] randomAccessFiles = new RandomAccessFile[caminhos];

                int[] readPointer = new int[caminhos];
                Billionaire[] billionaires = new Billionaire[caminhos];

                for (int i = 0; i < caminhos; i++) {
                    billionaires[i] = new Billionaire();
                }

                int outputPointer = 0;

                for (int j = 0; j < caminhos; j++) {
                    // System.out.println(i);
                    randomAccessFiles[j] = new RandomAccessFile(tmpFiles[j + inputStart], "rw");

                }

                boolean[] isEOF = new boolean[caminhos];

                for (int j = 0; j < caminhos; j++) {

                    if (randomAccessFiles[j].getFilePointer() < randomAccessFiles[j].length()) {

                        randomAccessFiles[j].readChar();
                        int objectSize = randomAccessFiles[j].readInt();
                        byte[] bt = new byte[objectSize];
                        randomAccessFiles[j].read(bt);
                        billionaires[j].fromByteArray(bt);

                    } else {
                        isEOF[j] = true;
                    }
                }

                while (isSorted == false) {
                    for (int i = 0; i < registros * caminhos; i++) {

                        int menor = 0;
                        // boolean isFinished = true;
                        for (int j = 1; j < caminhos; j++) {
                            if (isEOF[j] == false) {
                                if (billionaires[j].getId() < billionaires[menor].getId()) {
                                    menor = j;
                                    // isFinished = false;
                                }
                            }
                        }

                        boolean isFinished = true;
                        for (int j = 0; j < caminhos; j++) {
                            if (isEOF[j] == false) {
                                isFinished = false;
                            }
                        }

                        if (isFinished == true) {
                            System.out.println("FIM");
                            isSorted = true;
                            break;
                        } else {
                            dataOutputStreams[outputPointer].write(billionaires[menor].toByteArray()); // Insere objeto
                        }

                        readPointer[menor]++;

                        // Anda ponteiro
                        for (int j = 0; j < caminhos; j++) {

                            if (j == menor) {

                                if (randomAccessFiles[j].getFilePointer() < randomAccessFiles[j].length()) {

                                    randomAccessFiles[j].readChar();
                                    int objectSize = randomAccessFiles[j].readInt();
                                    byte[] bt = new byte[objectSize];
                                    randomAccessFiles[j].read(bt);
                                    billionaires[j].fromByteArray(bt);

                                } else {
                                    // dataOutputStreams[outputPointer].write(billionaires[menor].toByteArray()); // Insere objeto
                                    isEOF[j] = true;
                                }
                            }
                        }

                    }

                    outputPointer++;

                    registros = registros * caminhos;

                    if (outputPointer >= caminhos) {
                        outputPointer = 0;
                    }
                }

                if (switchFiles == true) {
                    switchFiles = false;

                } else {
                    switchFiles = true;
                }

                contador++;

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
