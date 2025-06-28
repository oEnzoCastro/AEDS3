package services;

import java.io.RandomAccessFile;

public class Vigenere {

    static String fileEncrypted = "src/output/billionairesEncrypted.db";
    static String fileDecrypted = "src/output/billionairesDecrypted.db";

    public static void encrypt(String key, String file) {

        try {

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            RandomAccessFile randomAccessFileEncrypt = new RandomAccessFile(fileEncrypted, "rw");

            // System.out.println(randomAccessFile.read());

            for (int i = 0; i < randomAccessFile.length(); i++) {

                randomAccessFileEncrypt.write(randomAccessFile.read() + key.charAt(i%key.length()));

            }


            randomAccessFile.close();
            randomAccessFileEncrypt.close();

        } catch (Exception e) {
            System.err.println("ERRO: " + e);
        }

    }

    public static void decrypt(String key) {

        try {

            RandomAccessFile randomAccessFileEncrypt = new RandomAccessFile(fileEncrypted, "rw");
            RandomAccessFile randomAccessFileDecrypt = new RandomAccessFile(fileDecrypted, "rw");


            for (int i = 0; i < randomAccessFileEncrypt.length(); i++) {

                randomAccessFileDecrypt.write(randomAccessFileEncrypt.read() - key.charAt(i%key.length()));

            }


            randomAccessFileEncrypt.close();
            randomAccessFileDecrypt.close();

        } catch (Exception e) {
            System.err.println("ERRO: " + e);
        }

    }
}
