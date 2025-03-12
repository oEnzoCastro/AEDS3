package services;

import java.io.*;
import java.util.*;

import models.Billionaire;

public class Sorting {

    private static int max_Memory = 20; // Tamanho de mémoria padrão
    private static int max_Paths = 2; // Número de caminhos padrão

    public static void sort(){
        Scanner scan = new Scanner(System.in);

        System.out.println("Digite o número de Registros");
        max_Memory = scan.nextInt();
        System.out.println("Digite o número de Caminhos");
        max_Paths = scan.nextInt();

        List<String> tempFiles = new ArrayList<>();
        for (int i = 1; i <= max_Paths * 2; i++) {
            tempFiles.add("src/database/temp" + i + ".db");
        }

        distribuicao(tempFiles);
        int lastWrite = intercalacao(tempFiles);

        try {
            String file = "src/database/billionaires.db";
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(0);
            int ultimoId = randomAccessFile.readInt();
            randomAccessFile.close();

            new File(file).delete();
            String finalFile = tempFiles.get(lastWrite - 1);

            FileInputStream finalInputStream = new FileInputStream(finalFile);
            DataInputStream finalDataInputStream = new DataInputStream(finalInputStream);

            FileOutputStream finalOutputStream = new FileOutputStream(file);
            DataOutputStream finalDataOutputStream = new DataOutputStream(finalOutputStream);

            finalDataOutputStream.writeInt(ultimoId);

            while (finalDataInputStream.available() > 0) {
                char lapide = finalDataInputStream.readChar();
                int len = finalDataInputStream.readInt();
                byte[] bt = new byte[len];
                finalDataInputStream.read(bt);
                if (lapide != '*') {
                    finalDataOutputStream.write(bt);
                }
            }

            finalDataInputStream.close();
            finalDataOutputStream.close();

            for (String tempFile : tempFiles) {
                 new File(tempFile).delete();
            }

            System.out.println("Ordenação concluída! Arquivo salvo em: " + file);
        } catch (Exception e) {
            System.err.println("Erro ao salvar arquivo final: " + e);
        }
    }

    private static void distribuicao(List<String> tempFiles) {
        String file = "src/database/billionaires.db";
        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
            List<DataOutputStream> outputStreams = new ArrayList<>();
            for (int i = 0; i < max_Paths; i++) {
                outputStreams.add(new DataOutputStream(new FileOutputStream(tempFiles.get(i))));
            }
            dataInputStream.readInt(); // Ignorar cabeçalho

            List<Billionaire> billionaires = new ArrayList<>();
            int fileIndex = 0;

            while (dataInputStream.available() > 0) {
                billionaires.clear();
                for (int i = 0; i < max_Memory; i++) {
                    if (dataInputStream.available() <= 0) break;
                    char lapide = dataInputStream.readChar();
                    int len = dataInputStream.readInt();
                    byte[] bt = new byte[len];
                    dataInputStream.read(bt);
                    if (lapide != '*') {
                        Billionaire b = new Billionaire();
                        b.fromByteArray(bt);
                        billionaires.add(b);
                    }
                }
                quickSort(billionaires, 0, billionaires.size() - 1);
                DataOutputStream outputStream = outputStreams.get(fileIndex);
                for (Billionaire b : billionaires) {
                    outputStream.write(b.toByteArray());
                }
                fileIndex = (fileIndex + 1) % max_Paths;
            }
            dataInputStream.close();
            for (DataOutputStream dos : outputStreams) dos.close();
        } catch (Exception e) {
            System.err.println("Erro na distribuição: " + e);
        }
    }

    private static int intercalacao(List<String> tempFiles) {
        boolean hasData;
        int lastwrite = -1;
        boolean readFromFirstHalf = true;

        do {
            hasData = false;
            
            try {
                List<DataInputStream> inputStreams = new ArrayList<>();
                List<DataOutputStream> outputStreams = new ArrayList<>();
                
                for (int i = 0; i < max_Paths; i++) {
                    inputStreams.add(new DataInputStream(new FileInputStream(tempFiles.get(readFromFirstHalf ? i : i + max_Paths))));
                    outputStreams.add(new DataOutputStream(new FileOutputStream(tempFiles.get(readFromFirstHalf ? i + max_Paths : i))));
                }
                
                boolean[] hasRecord = new boolean[max_Paths];
                Billionaire[] currentRecords = new Billionaire[max_Paths];
                
                while (inputStreams.stream().anyMatch(in -> {
                    try { return in.available() > 0; } catch (IOException e) { return false; }
                })) {
                    DataOutputStream output = outputStreams.get(0);
                    
                    for (int i = 0; i < max_Paths; i++) {
                        if (!hasRecord[i] && inputStreams.get(i).available() > 0) {
                            char lapide = inputStreams.get(i).readChar();
                            int len = inputStreams.get(i).readInt();
                            byte[] bt = new byte[len];
                            inputStreams.get(i).read(bt);
                            if (lapide != '*') {
                                currentRecords[i] = new Billionaire();
                                currentRecords[i].fromByteArray(bt);
                                hasRecord[i] = true;
                            }
                        }
                    }
                    
                    int minIndex = -1;
                    for (int i = 0; i < max_Paths; i++) {
                        if (hasRecord[i] && (minIndex == -1 || currentRecords[i].getId() < currentRecords[minIndex].getId())) {
                            minIndex = i;
                        }
                    }
                    
                    if (minIndex != -1) {
                        output.writeChar(' ');
                        byte[] tmp = currentRecords[minIndex].toByteArray();
                        output.writeInt(tmp.length);
                        output.write(tmp);
                        hasRecord[minIndex] = false;
                        lastwrite = readFromFirstHalf ? max_Paths + 1 : 1;
                    }
                    
                }
                
                for (DataInputStream in : inputStreams) {
                    in.close();
                }
                for (DataOutputStream out : outputStreams) {
                    out.close();
                }
                
                readFromFirstHalf = !readFromFirstHalf;
            } catch (Exception e) {
                System.err.println("Erro em Sorting.intercalacao: " + e);
                break;
            }
        } while (hasData);

        return lastwrite;
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
