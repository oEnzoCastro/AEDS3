package services;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoyerMoore {

    public static void pesquisar(String pattern, String  file) {

        try (RandomAccessFile fileReader = new RandomAccessFile(file, "r")) {

            long fileSize = fileReader.length();
            byte[] data = new byte[(int) fileSize];

            fileReader.readFully(data);
            String text = new String(data);
            List<Integer> result = findOccurrences(text, pattern);

            if (result.isEmpty()) {

                System.out.println("Padrão não encontrado!");

            } else {

                System.out.println("Padrão encontrado " + result.size() + " vezes!");
                System.out.println("Endereços: " + result);

            }

        } catch (Exception e) {

            System.err.println("ERRO: " + e);

        }

    }

    public static ArrayList<Integer> findOccurrences(String text, String padrao) {

        int tamanhoTexto = text.length();
        int tamanhoPadrao = padrao.length();

        ArrayList<Integer> indicesOcorrencias = new ArrayList<>();

        if (tamanhoTexto < tamanhoPadrao || tamanhoPadrao == 0) {

            return indicesOcorrencias;

        }

        // Pré-processamento do padrão

        Map<Character, Integer> caractereRuimArray = caractereRuim(padrao);

        int[] sufixoBom = sufixoBom(padrao);
        int indiceTexto = tamanhoPadrao - 1;

        while (indiceTexto < tamanhoTexto) {

            int indicePadrao = tamanhoPadrao - 1;
            int indiceTextoAtual = indiceTexto;

            // Compara o padrão com o texto da direita para a esquerda

            while (indicePadrao >= 0 && padrao.charAt(indicePadrao) == text.charAt(indiceTextoAtual)) {

                indicePadrao--;
                indiceTextoAtual--;

            }

            // Se o padrão foi encontrado

            if (indicePadrao < 0) {

                indicesOcorrencias.add(indiceTexto - tamanhoPadrao + 1);
                indiceTexto += (indiceTexto + tamanhoPadrao < tamanhoTexto) ? tamanhoPadrao : 1;

            } else {

                char caractereRuim = text.charAt(indiceTextoAtual);
                int saltoCaracterRuim = indicePadrao - caractereRuimArray.getOrDefault(caractereRuim, -1);
                int saltoSufixoBom = sufixoBom[indicePadrao];

                indiceTexto += Math.max(saltoCaracterRuim, saltoSufixoBom);

            }

        }

        return indicesOcorrencias;

    }

    /**
    
    * Sufixo Bom
    
    */

    private static int[] sufixoBom(String pattern) {

        int m = pattern.length();
        int[] goodSuffixShift = new int[m];
        int[] suffixes = new int[m];

        suffixes[m - 1] = m;

        for (int i = m - 2; i >= 0; i--) {

            int j = i;

            while (j >= 0 && pattern.charAt(j) == pattern.charAt(m - 1 - i + j)) {

                j--;

            }

            suffixes[i] = i - j;

        }

        for (int i = 0; i < m; i++) {

            goodSuffixShift[i] = m;

        }

        int j = 0;

        for (int i = m - 1; i >= 0; i--) {

            if (i + 1 == suffixes[i]) {

                for (; j < m - 1 - i; j++) {

                    if (goodSuffixShift[j] == m) {

                        goodSuffixShift[j] = m - 1 - i;

                    }

                }

            }

        }

        for (int i = 0; i <= m - 2; i++) {

            goodSuffixShift[m - 1 - suffixes[i]] = m - 1 - i;

        }

        return goodSuffixShift;

    }

    /**
    
    * Caractere Ruim
    
    */

    private static Map<Character, Integer> caractereRuim(String pattern) {

        Map<Character, Integer> badChar = new HashMap<>();
        int patternLength = pattern.length();

        for (int i = 0; i < patternLength - 1; i++) {

            badChar.put(pattern.charAt(i), i);

        }

        return badChar;

    }
}