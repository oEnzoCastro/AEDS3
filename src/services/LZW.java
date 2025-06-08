package services;

import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LZW {

    public static final int BITS_PER_CODE = 12;
    private static final int MAX_DICT_SIZE = 1 << BITS_PER_CODE;

    public static void extrair(String file, String fileCompressed) {
        try {

            RandomAccessFile randomAccessFileDecode = new RandomAccessFile(fileCompressed, "rw");

            long fileSizeDecode = randomAccessFileDecode.length();

            byte[] data2 = new byte[(int) fileSizeDecode]; // Create a byte array to hold the data
            randomAccessFileDecode.readFully(data2); // Read all bytes from the file

            // Decodificação - Cria uma nova string
            byte[] msgDecodeBytes = decode(data2);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            randomAccessFile.write(msgDecodeBytes);

            randomAccessFile.close();
            randomAccessFileDecode.close();

        } catch (Exception e) {
            System.err.println("ERRO: " + e);
        }
    }

    public static void compactar(String file, String fileCompressed) {
      try {

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            long fileSize = randomAccessFile.length();

            byte[] data = new byte[(int) fileSize]; // Create a byte array to hold the data
            randomAccessFile.readFully(data); // Read all bytes from the file

            byte[] msgCodificada = encode(data); // Vetor de bits que contém os índices
            RandomAccessFile randomAccessFileCompressed = new RandomAccessFile(fileCompressed, "rw");

            randomAccessFileCompressed.write(msgCodificada);

            randomAccessFileCompressed.close();
            randomAccessFile.close();

        } catch (Exception e) {
            System.err.println("ERRO: " + e);
        }
    }

    public static byte[] encode(byte[] inputBytes) {
        int dictSize = 256;
        Map<String, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(String.valueOf((char) i), i);
        }

        String currentSequence = "";
        List<Integer> outputCodes = new ArrayList<>();

        for (byte b : inputBytes) {
            char nextChar = (char) (b & 0xFF);
            String sequenceWithNext = currentSequence + nextChar;

            if (dictionary.containsKey(sequenceWithNext)) {
                currentSequence = sequenceWithNext;
            } else {
                outputCodes.add(dictionary.get(currentSequence));
                if (dictSize < MAX_DICT_SIZE) {
                    dictionary.put(sequenceWithNext, dictSize++);
                }
                currentSequence = String.valueOf(nextChar);
            }
        }

        if (!currentSequence.isEmpty()) {
            outputCodes.add(dictionary.get(currentSequence));
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int buffer = 0;
        int bitsInBuffer = 0;

        for (int code : outputCodes) {
            buffer = (buffer << BITS_PER_CODE) | code;
            bitsInBuffer += BITS_PER_CODE;
            while (bitsInBuffer >= 8) {
                bitsInBuffer -= 8;
                int byteToWrite = (buffer >> bitsInBuffer);
                baos.write(byteToWrite);
                buffer &= (1 << bitsInBuffer) - 1;
            }
        }

        if (bitsInBuffer > 0) {
            buffer <<= (8 - bitsInBuffer);
            baos.write(buffer);
        }

        return baos.toByteArray();
    }

    public static byte[] decode(byte[] compressedBytes) throws Exception {
        List<Integer> compressedCodes = new ArrayList<>();
        int buffer = 0;
        int bitsInBuffer = 0;
        for (byte b : compressedBytes) {
            buffer = (buffer << 8) | (b & 0xFF);
            bitsInBuffer += 8;
            while (bitsInBuffer >= BITS_PER_CODE) {
                bitsInBuffer -= BITS_PER_CODE;
                int code = buffer >> bitsInBuffer;
                compressedCodes.add(code);
                buffer &= (1 << bitsInBuffer) - 1;
            }
        }

        int dictSize = 256;
        Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(i, String.valueOf((char) i));
        }

        String previousSequence = dictionary.get(compressedCodes.remove(0));
        ByteArrayOutputStream decodedStream = new ByteArrayOutputStream();
        for (char c : previousSequence.toCharArray()) {
            decodedStream.write((byte) c);
        }

        for (int code : compressedCodes) {
            String currentEntry;
            if (dictionary.containsKey(code)) {
                currentEntry = dictionary.get(code);
            } else if (code == dictSize) {
                currentEntry = previousSequence + previousSequence.charAt(0);
            } else {
                throw new Exception("Invalid code in LZW stream: " + code);
            }

            for (char c : currentEntry.toCharArray()) {
                decodedStream.write((byte) c);
            }

            if (dictSize < MAX_DICT_SIZE) {
                dictionary.put(dictSize++, previousSequence + currentEntry.charAt(0));
            }
            previousSequence = currentEntry;
        }

        return decodedStream.toByteArray();
    }
}