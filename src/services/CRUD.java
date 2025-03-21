package services;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.RandomAccessFile;

import DAO.DAO;
import models.Billionaire;

public class CRUD {
    public static int createAll() {

        String line = "";

        String file = "src/database/billionaires.db";
        String fileCSV = "src/database/forbes_billionaires.csv";
        // String fileCSV = "src/database/BillionairesCSV.csv"; (Database com 10.000
        // linhas inseridas pelo ChatGPT)

        int id = -1;

        BufferedReader reader = null;

        try {

            FileOutputStream fileOutputStream = new FileOutputStream(file); // Arquivo de banco de dados a inserir

            reader = new BufferedReader(new FileReader(fileCSV));

            reader.readLine(); // Ler primeira linha

            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            dataOutputStream.writeInt(0);

            while ((line = reader.readLine()) != null) {

                String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                DAO.create(row, fileOutputStream);

                id = Integer.parseInt(row[0]);

            }

            // RandomAccessFile aponta para 1° posição e escreve o último ID inserido
            randomAccessFile.seek(0);
            randomAccessFile.writeInt(id);

            // .close()
            randomAccessFile.close();
            reader.close();
            fileOutputStream.close();

            System.out.println("CSV convertido para Database!");

        } catch (Exception e) {
            System.err.println("Erro ReadCSV.createAll: " + e);
        }

        return id;
    }

    public static void create(String file) {
        try {

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            byte[] bt;

            randomAccessFile.seek(0);

            int lastId = randomAccessFile.readInt() + 1;

            randomAccessFile.seek(0);

            randomAccessFile.writeInt(lastId);

            Billionaire newBillionaire = BillionaireService.newBillionaire(lastId);

            randomAccessFile.seek(randomAccessFile.length()); // Move ponteiro para fim do arquivo

            // Inserir newBillionaire

            bt = newBillionaire.toByteArray();
            randomAccessFile.write(bt);

            randomAccessFile.close();

        } catch (Exception e) {
            System.out.println("Erro CREATE: " + e);
        }

    }

    public static Billionaire get(String key, String file) {

        boolean found = false;

        try {

            FileInputStream fileInputStream = new FileInputStream(file);

            DataInputStream dataInputStream = new DataInputStream(fileInputStream);

            Billionaire billionaireTmp;

            // Lê primeiros 4 BYTES (Ultimo ID inserido)
            dataInputStream.readInt();

            while (dataInputStream.available() > 0) {

                billionaireTmp = DAO.read(fileInputStream, dataInputStream);

                // Verifica se é o ID procurado (Só possivel conferir o ID se o Objeto estiver
                // ativo)

                if (key.charAt(0) >= '0' && key.charAt(0) <= '9') {

                    if ((billionaireTmp != null && billionaireTmp.getId() == Integer.parseInt(key))) {
                        found = true;
                        System.out.println(billionaireTmp);
                        return billionaireTmp;
                    }

                } else {

                    if ((billionaireTmp != null && billionaireTmp.getName().equals(key))) {
                        found = true;
                        System.out.println(billionaireTmp);
                        // Podem Haver Billionários com o mesmo nome
                    }
                }

            }

            if (!found) {
                System.out.println("Bilionário não encontrado");
                return null;
            }

        } catch (Exception e) {
            System.err.println("Erro Read: " + e);
        }
        return null;
    }

    public static void update(String key, String file) {

        Billionaire billionaire;
        if (key.charAt(0) >= '0' && key.charAt(0) <= '9') {
            billionaire = get(key, file);
        } else {
            System.out.println("Só é aceito update inserindo a chave 'ID'!");
            return;
        }

        int id = Integer.parseInt(key);

        if (billionaire == null) {
            System.out.println("Bilinário Indisponível!");
        } else {
            Billionaire newBillionaire = BillionaireService.updateBillionaire(billionaire);

            try {

                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                byte[] bt;

                if (newBillionaire.getByteSize() > billionaire.getByteSize()) {

                    DAO.delete(id, file); // Insere Lapide no billionaire

                    randomAccessFile.seek(randomAccessFile.length()); // Move ponteiro para fim do arquivo

                    // Inserir newBillionaire

                    bt = newBillionaire.toByteArray();
                    randomAccessFile.write(bt);

                } else {

                    long filePointer = BillionaireService.findBillionaireByte(id, file);

                    randomAccessFile.seek(filePointer);

                    bt = newBillionaire.toByteArrayUpdate(billionaire, file);
                    randomAccessFile.write(bt);

                    // Add newBillionaire no lugar do billionaire
                }

                randomAccessFile.close();

            } catch (Exception e) {
                System.err.println(e);
            }

        }

    }

    public static void delete(int id, String file) {

        boolean isDeleted = DAO.delete(id, file);

        if (isDeleted == true) {
            System.out.println("Bilionário Deletado!");
        } else {
            System.out.println("Bilionário não deletado!");
        }

    }

}
