package services;

import java.io.RandomAccessFile;
import java.math.BigInteger;

public class RSA {

    static String fileEncrypted = "src/output/billionairesEncryptedRSA.db";
    static String fileDecrypted = "src/output/billionairesDecryptedRSA.db";

    public static void encrypt(int p, int q, String file) {

        if (!isPrime(p) || !isPrime(q)) {
            System.out.println("ERRO: As chaves deverão ser números primos!");
            return;
        }

        // n = p * q
        int n = p * q;

        if (n < 255) {
            System.out.println(
                    "ERRO: Números inválidos, a multiplicação dos dois deverá ser maior do que o range de caracteres do banco!");
            return;
        }

        // z = (p - 1) * (q - 1)
        int z = (p - 1) * (q - 1);
        // d = primo em relação à z
        int d = nearPrime(z);
        // (e * d) % z = 1
        int e = getE(d, z);

        try {

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            RandomAccessFile randomAccessFileEncrypt = new RandomAccessFile(fileEncrypted, "rw");

            System.out.println("Criptografando " + randomAccessFile.length() + " tokens, aguarde...");

            for (int i = 0; i < randomAccessFile.length(); i++) {

                BigInteger P = BigInteger.valueOf(randomAccessFile.read());

                BigInteger big = P.pow(e); // C = P^e

                big = big.mod(BigInteger.valueOf(n)); // C = (P ^ e) % n

                // Armazena no formato Char para não acontecer overflow
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

    public static void decrypt(int p, int q) {

        if (!isPrime(p) || !isPrime(q)) {
            System.out.println("ERRO: As chaves deverão ser números primos!");
            return;
        }

        // n = p * q
        int n = p * q;

        if (n < 255) {
            System.out.println(
                    "ERRO: Números inválidos, a multiplicação dos dois deverá ser maior do que o range de caracteres do banco!");
            return;
        }

        // z = (p - 1) * (q - 1)
        int z = (p - 1) * (q - 1);
        // d = primo em relação à z
        int d = nearPrime(z);
        // int e = getE(d, z);

        try {

            RandomAccessFile randomAccessFileEncrypt = new RandomAccessFile(fileEncrypted, "rw");
            RandomAccessFile randomAccessFileDecrypt = new RandomAccessFile(fileDecrypted, "rw");

            System.out.println("Descriptografando " + randomAccessFileEncrypt.length() + " tokens, aguarde...");
            // Avança 2 bytes por vez, pois os elementos criptografados foram armazenados em char
            for (int i = 0; i < randomAccessFileEncrypt.length() / 2; i++) {

                BigInteger C = BigInteger.valueOf(randomAccessFileEncrypt.readChar());

                BigInteger big = C.pow(d); // P = C^d

                int P = big.mod(BigInteger.valueOf(n)).intValue(); // P = (C ^ d) % n

                randomAccessFileDecrypt.write(P);

            }

            randomAccessFileDecrypt.close();
            randomAccessFileEncrypt.close();

        } catch (Exception exception) {
            System.err.println("ERRO: " + exception);
        }

    }
}
