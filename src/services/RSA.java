package services;

import java.io.RandomAccessFile;
import java.math.BigInteger;

public class RSA {

    static String fileEncrypted = "src/output/billionairesEncryptedRSA.db";
    static String fileDecrypted = "src/output/billionairesDecryptedRSA.db";

    public static void encrypt(String key, String file) {
        int p = 23;
        int q = 53;

        if (!isPrime(p) || !isPrime(q)) {
            System.out.println("ERRO");
            return;
        }

        int n = p * q;

        if (n < 255) {
            System.out.println("ERRO");
            return;
        }

        int z = (p - 1) * (q - 1);
        int d = nearPrime(z);
        int e = getE(d, z);

        try {

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            RandomAccessFile randomAccessFileEncrypt = new RandomAccessFile(fileEncrypted, "rw");

            System.out.println("Criptografando " + randomAccessFile.length() + " tokens, aguarde...");


            for (int i = 0; i < randomAccessFile.length(); i++) {

                BigInteger P = BigInteger.valueOf(randomAccessFile.read());

                BigInteger big = P.pow(e);

                big = big.mod(BigInteger.valueOf(n));

                randomAccessFileEncrypt.writeChar(big.intValue());

            }

            randomAccessFile.close();
            randomAccessFileEncrypt.close();

        } catch (Exception exception) {
            System.err.println("ERRO: " + exception);
        }

    }

    private static int nearPrime(int z) {

        int lastPrime = -1;

        for (int i = z; i > 0; i--) {

            if (isPrime(i)) {
                if (z % i == 0) {

                    if (lastPrime == -1) {
                        return i;
                    } else {
                        return lastPrime;
                    }

                }
                lastPrime = i;
            }

        }

        return -1;

    }

    private static boolean isPrime(int num) {
        if (num <= 1) {
            return false;
        }

        for (int i = 2; i * i <= num; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    private static int getE(int d, int z) {

        for (int i = 1; i <= z; i++) {

            int e = (i * d) % z;

            if (e == 1) {
                return i;
            }

        }

        return -1;
    }

    public static void decrypt(String key) {

        int p = 23;
        int q = 53;

        if (!isPrime(p) || !isPrime(q)) {
            System.out.println("ERRO");
            return;
        }

        int n = p * q;

        if (n < 255) {
            System.out.println("ERRO");
            return;
        }

        int z = (p - 1) * (q - 1);
        int d = nearPrime(z);
        // int e = getE(d, z);

        try {

            RandomAccessFile randomAccessFileEncrypt = new RandomAccessFile(fileEncrypted, "rw");
            RandomAccessFile randomAccessFileDecrypt = new RandomAccessFile(fileDecrypted, "rw");

            System.out.println("Descriptografando " + randomAccessFileEncrypt.length() + " tokens, aguarde...");
            for (int i = 0; i < randomAccessFileEncrypt.length()/2; i++) {

                BigInteger C = BigInteger.valueOf(randomAccessFileEncrypt.readChar());

                BigInteger big = C.pow(d);
                
                int P = big.mod(BigInteger.valueOf(n)).intValue();

                randomAccessFileDecrypt.write(P);

            }

            randomAccessFileDecrypt.close();
            randomAccessFileEncrypt.close();

        } catch (Exception exception) {
            System.err.println("ERRO: " + exception);
        }

    }
}
