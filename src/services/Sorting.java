package services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.Billionaire;

public class Sorting {

    public static void sort(String file, int registros, int caminhos) {

        String[] tmpFiles = new String[caminhos * 2];

        for (int i = 0; i < caminhos * 2; i++) {
            tmpFiles[i] = "src/database/tmp" + i + ".db";
        }

        distribuicao(file, tmpFiles, registros);
        String resultFile = intercalacao(tmpFiles, registros, caminhos);

        // Deleta arquivos temporarios e manda dados ordenados para arquivo principal

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(0);
            int ultimoId = randomAccessFile.readInt();
            randomAccessFile.close();

            new File(file).delete();

            FileInputStream finalInputStream = new FileInputStream(resultFile);
            DataInputStream finalDataInputStream = new DataInputStream(finalInputStream);

            FileOutputStream finalOutputStream = new FileOutputStream(file);
            DataOutputStream finalDataOutputStream = new DataOutputStream(finalOutputStream);

            finalDataOutputStream.writeInt(ultimoId);

            while (finalDataInputStream.available() > 0) {
                char lapide = finalDataInputStream.readChar();
                int len = finalDataInputStream.readInt();
                byte[] bt = new byte[len];
                finalDataInputStream.read(bt);
                finalDataOutputStream.writeChar(lapide);
                finalDataOutputStream.writeInt(len);
                finalDataOutputStream.write(bt);
            }

            finalDataInputStream.close();
            finalDataOutputStream.close();

            // Deletar tmpFiles
            for (String tempFile : tmpFiles) {
                 new File(tempFile).delete();
            }

            System.out.println("Ordenação concluída! Arquivo salvo em: " + file);

        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

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

                        quickSort(billionaires, 0, billionaires.size() - 1);

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
            for (int i = 0; i < tmpFiles.length; i++) {
                fileOutputStreams[i].close();
                dataOutputStreams[i].close();
            }
        } catch (Exception e) {
            System.out.println("ERRO: " + e);
        }
    }

    private static String intercalacao(String[] tmpFiles, int firstRegistros, int caminhos) {

        boolean switchFiles = false;

        boolean endSorting = false;

        int registros = firstRegistros;

        while (endSorting == false) {

            // Inicio Declaração de I/O

            int inputStart;
            int outputStart;

            if (switchFiles == false) {
                inputStart = 0;
                outputStart = caminhos;
            } else {
                inputStart = caminhos;
                outputStart = 0;
            }

            // In
            RandomAccessFile[] randomAccessFile = new RandomAccessFile[caminhos];
            // Out
            FileOutputStream[] fileOutputStream = new FileOutputStream[caminhos];
            DataOutputStream[] dataOutputStream = new DataOutputStream[caminhos];

            // Setar endereço dos arquivos
            try {
                for (int i = 0; i < caminhos; i++) {
                    randomAccessFile[i] = new RandomAccessFile(tmpFiles[i + inputStart], "rw");
                }

                for (int i = 0; i < caminhos; i++) {
                    fileOutputStream[i] = new FileOutputStream(tmpFiles[i + outputStart]);
                    dataOutputStream[i] = new DataOutputStream(fileOutputStream[i]);
                }

                if (randomAccessFile[1].length() <= 0) {
                    // Retorna o arquivo que está ordenado
                    for (int i = 0; i < caminhos; i++) {
                        randomAccessFile[i].close();
                        fileOutputStream[i].close();
                        dataOutputStream[i].close();
                    }
                    return tmpFiles[0 + inputStart];
                }

            } catch (Exception e) {
                System.out.println("ERROR: " + e);
            }

            // Fim Declaração de I/O
            // Inicio Intercalação

            Billionaire[] billionaires = new Billionaire[caminhos];
            try {

                // Bool para conferir se os registros totais já foram lidos
                boolean[] isOff = new boolean[caminhos];

                // Pega o input de cada caminho e inicializa os Bilionários
                for (int i = 0; i < caminhos; i++) {

                    billionaires[i] = new Billionaire();

                    // Ler se houver Bilionário, se não, Billionario = NULL
                    if (randomAccessFile[i].getFilePointer() < randomAccessFile[i].length()) {

                        randomAccessFile[i].readChar();
                        int objectSize = randomAccessFile[i].readInt();
                        byte[] bt = new byte[objectSize];
                        randomAccessFile[i].read(bt);
                        billionaires[i].fromByteArray(bt);

                    } else {
                        isOff[i] = true;
                    }
                }

                int[] billionairePointer = new int[caminhos];
                int outputPointer = 0;

                Boolean isEOF = false;

                while (isEOF == false) { // Loop Infinito de Teste

                    int menor = 0;

                    if (isOff[0] == true) {
                        for (int i = 0; i < caminhos; i++) {
                            if (isOff[i] == false) {
                                menor = i;
                                i = caminhos; // Break    
                            }
                        }
                    }

                    // Comparar ID de cada caminho
                    for (int i = 0; i < caminhos; i++) {
                        if (isOff[i] == false) {
                            if (billionaires[i].getId() < billionaires[menor].getId()) {
                                menor = i;
                            }
                        }
                    }

                    // System.out.println(outputPointer + " - " + billionaires[menor].getId());

                    if (randomAccessFile[menor].getFilePointer() < randomAccessFile[menor].length()) {
                        // Insere o Bilionário no arquivo correspondente ao Output
                        fileOutputStream[outputPointer].write(billionaires[menor].toByteArray());
                        // Lê o proximo Bilionário
                        randomAccessFile[menor].readChar();
                        int objectSize = randomAccessFile[menor].readInt();
                        byte[] bt = new byte[objectSize];
                        randomAccessFile[menor].read(bt);
                        billionaires[menor].fromByteArray(bt);
                        // Adiciona no contador que o menor bilionário foi movimentado
                        billionairePointer[menor]++;
                        if (billionairePointer[menor] >= registros) {
                            isOff[menor] = true;
                        }
                    } else {
                        isOff[menor] = true;
                    }

                    boolean isAllOff = true;
                    isEOF = true;

                    for (int i = 0; i < caminhos; i++) {
                        if (isOff[i] == false) {
                            isAllOff = false;
                        }
                        if (randomAccessFile[i].getFilePointer() < randomAccessFile[i].length()) {
                            isEOF = false;
                        }
                    }

                    // Se todos tiverem acabado o registro, ir para o proximo arquivo
                    if (isAllOff == true) {
                        for (int i = 0; i < isOff.length; i++) {
                            if (randomAccessFile[i].getFilePointer() < randomAccessFile[i].length()) {
                                isOff[i] = false;
                            }

                            billionairePointer[i] = 0;
                        }
                        outputPointer++;
                        if (outputPointer >= caminhos) {
                            outputPointer = 0;
                        }
                    }

                    // Insere os Bilionários que faltaram e muda o arquivo de output e input
                    if (isEOF == true) {

                        // Insere o Bilionário no arquivo correspondente ao Output
                        for (int i = 0; i < caminhos; i++) {

                            menor = 0;

                            if (isOff[0] == true) {
                                for (int j = 0; j < caminhos; j++) {
                                    if (isOff[j] == false) {
                                        menor = j;
                                        j = caminhos; // Break    
                                    }
                                }
                            }

                            // Comparar ID de cada caminho
                            for (int j = 0; j < caminhos; j++) {
                                if (isOff[j] == false) {
                                    if (billionaires[j].getId() < billionaires[menor].getId()) {
                                        menor = j;
                                    }
                                }
                            }

                            if (isOff[menor] == false) {
                                // System.out.println(outputPointer + " - " + billionaires[menor].getId());

                                fileOutputStream[outputPointer].write(billionaires[menor].toByteArray());
                            }

                            isOff[menor] = true;

                        }

                        // Altera o tamanho dos registros de acordo com a Ordenação Externa
                        registros = registros * caminhos;

                        if (switchFiles == false) {
                            switchFiles = true;
                        } else {
                            switchFiles = false;
                        }

                    }

                }

                for (int i = 0; i < caminhos; i++) {
                    randomAccessFile[i].close();
                    fileOutputStream[i].close();
                    dataOutputStream[i].close();
                }

            } catch (Exception e) {
                System.out.println("ERROR: " + e);
            }

        }
        // Return que estiver fora do Try/Catch
        return "";
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
