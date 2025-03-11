package services;

import java.io.*;
import java.util.*;

import models.Billionaire;

public class Sorting {

    private static int max_Memory = 50;

    public static void sort(){
        String file = "src/database/billionaires.db";
        String temp1 = "src/database/temp1.db";
        String temp2 = "src/database/temp2.db";
        String temp3 = "src/database/temp3.db";
        String temp4 = "src/database/temp4.db";
        
        distribuicao();
        int lastwrite = intercalacao();
        try {
            // Lê o ultimo id inserido no arquivo original e salva
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(0);
            int ultimoId = randomAccessFile.readInt();
            randomAccessFile.close();

            new File(file).delete(); // Apaga o arquivo original desordenado
            
            String finalFile = lastwrite == 1 ? temp1 : temp3; // Encontra o arquivo com os dados ordenados (ulitmo arquivo escrito)

            FileInputStream finalInputStream = new FileInputStream(finalFile);
            DataInputStream finalDataInputStream = new DataInputStream(finalInputStream);

            FileOutputStream finalOutputStream = new FileOutputStream(file);
            DataOutputStream finalDataOutputStream = new DataOutputStream(finalOutputStream);

            finalDataOutputStream.writeInt(ultimoId); // Escreve o cabeçalho

            // Transfere os dados do arquivo temporario para o final
            while (finalDataInputStream.available() > 0) {
                char lapide = finalDataInputStream.readChar();
                int len = finalDataInputStream.readInt();
                byte[] bt = new byte[len];
                finalDataInputStream.read(bt);
                Billionaire b = new Billionaire();
                b.fromByteArray(bt);
                byte[] tmp = b.toByteArray();
                if (lapide != '*') {
                    finalDataOutputStream.write(tmp);
                }
            }
    
            finalDataInputStream.close();
            finalDataOutputStream.close();
    
            // Deletando arquivos temporários
            new File(temp1).delete();
            new File(temp2).delete();
            new File(temp3).delete();
            new File(temp4).delete();
    
            System.out.println("Ordenação concluída! Arquivo salvo em: " + file);
        } catch (Exception e) {
            System.err.println("Erro ao salvar arquivo final: " + e);
        }
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

            while (dataInputStream.available() > 0) {
                billionaires.clear();

                for (int i = 0; i < max_Memory; i++) { // Lê até 50 registros
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

    private static int intercalacao() {
        String temp1 = "src/database/temp1.db";
        String temp2 = "src/database/temp2.db";
        String temp3 = "src/database/temp3.db";
        String temp4 = "src/database/temp4.db";
    
        boolean inicialRead = true; // Para saber em quais arquivos ler e em quais escrever
        int segmento = max_Memory; // Tamanho inicial do segmento ordenado
        boolean hasData;
        int lastwrite = -1; // Para saber qual o ulitmo arquivo escrito para saber onde está o arquivo ordenado final

        do {
            hasData = false; // Controle se ainda existe dados nos arquivos
            boolean wroteData = false; // Controle se algum dado foi escrito
    
            try {
                // Seleção dos arquivos
                FileInputStream fileInputStream1 = new FileInputStream(inicialRead ? temp1 : temp3);
                DataInputStream dataInputStream1 = new DataInputStream(fileInputStream1);
    
                FileInputStream fileInputStream2 = new FileInputStream(inicialRead ? temp2 : temp4);
                DataInputStream dataInputStream2 = new DataInputStream(fileInputStream2);
    
                FileOutputStream fileOutputStream1 = new FileOutputStream(inicialRead ? temp3 : temp1);
                DataOutputStream dataOutputStream1 = new DataOutputStream(fileOutputStream1);
    
                FileOutputStream fileOutputStream2 = new FileOutputStream(inicialRead ? temp4 : temp2);
                DataOutputStream dataOutputStream2 = new DataOutputStream(fileOutputStream2);
                
                // Em qual arquivo começar escrevendo
                boolean writeToFirst = true;
    
                Billionaire b1 = null, b2 = null;
                boolean hasB1 = false, hasB2 = false;
    
                while (dataInputStream1.available() > 0 || dataInputStream2.available() > 0) { // Enquanto ainda existir dados em um dos arquivos de input
                    DataOutputStream outputStream = writeToFirst ? dataOutputStream1 : dataOutputStream2;
                    int count = 0;
                    int countB1 = 0, countB2 = 0;
                    
                    while (count < segmento * 2) { // Ainda existem números no segmento de um dos arquivos
                        if (!hasB1 && countB1 < segmento && dataInputStream1.available() > 0) { // Se não tem um registro do arquivo 1 salvo e ainda tem registros no segmento ordenado do arquivo 1 e ele não acabou
                            // Ler e salvar registro do arquivo 1
                            char lapide = dataInputStream1.readChar();
                            int len = dataInputStream1.readInt();
                            byte[] bt = new byte[len];
                            dataInputStream1.read(bt);
                            if (lapide != '*') {
                                b1 = new Billionaire();
                                b1.fromByteArray(bt);
                                hasB1 = true;
                                countB1++;
                            }
                        }
    
                        if (!hasB2 && countB2 < segmento && dataInputStream2.available() > 0) { // Se não tem um registro do arquivo 2 salvo e ainda tem registros no segmento ordenado do arquivo2 e ele não acabou
                            // Ler e salvar registro do arquivo 2
                            char lapide = dataInputStream2.readChar();
                            int len = dataInputStream2.readInt();
                            byte[] bt = new byte[len];
                            dataInputStream2.read(bt);
                            if (lapide != '*') {
                                b2 = new Billionaire();
                                b2.fromByteArray(bt);
                                hasB2 = true;
                                countB2++;
                            }
                        }
    
                        if (hasB1 && (!hasB2 || b1.getId() < b2.getId())) { // Se existe o registro do arquivo 1 e ou não existe do arquivo 2 ou o id de 1 é menor que o de 2
                            // Escreve no próximo arquivo o registro do arquivo 1
                            byte[] tmp = b1.toByteArray();
                            outputStream.write(tmp);
                            hasB1 = false; // Avisa que b1 foi usado
                            wroteData = true; // Avisa que algum dado foi escrito
                            lastwrite = inicialRead ? 3 : 1; // Diz qual o ultimo arquivo que foi escrito
                        } else if (hasB2) {
                            byte[] tmp = b2.toByteArray();
                            outputStream.write(tmp);
                            hasB2 = false; // Avisa que b2 foi usado
                            wroteData = true; // Avisa que algum dado foi escrito
                            lastwrite = inicialRead ? 3 : 1; // Diz qual o ultimo arquivo que foi escrito
                        } else { // Se b1 e b2 não existem
                            break;
                        } 

                    }

                    if(dataInputStream1.available() == 0 && dataInputStream2.available() == 0){ // Se os arquivos não tem mais dados disponiveis
                        break;
                    }

                    hasData = hasData || wroteData; // Se ainda há dados ou se dados foram escritos nessa iteração
                    
                    writeToFirst = !writeToFirst; // Inverte o próximo arquivo a ser escrito
                }

                dataInputStream1.close();
                dataInputStream2.close();
                dataOutputStream1.close();
                dataOutputStream2.close();
    
                inicialRead = !inicialRead; // Inverte o conjunto de arquivos a serem lidos e escritos
                segmento *= 2; // Dobra o tamanho do segmento em cada iteração
                
            } catch (Exception e) {
                System.err.println("Erro em Sorting.intercalacao: " + e);
                break;
            }
            if(!wroteData){
                break;
            }
        } while (hasData); // Enquanto ainda existe dados

        return lastwrite; // Retorna o ultimo arquivo escrito (que sera o ordenado)
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
