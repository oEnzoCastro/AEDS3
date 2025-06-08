package services;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import models.Billionaire;

public class KMP {
    private static int[] vetorFalha(String padrao){
        int tam = padrao.length();
        int[] prefixos = new int[tam];
        int tamPrefixoAnt = 0;

        // i começa em 1 pois o primeiro prefixo é sempre 0
        int i = 1; 
        prefixos[0] = 0;

        while (i < tam){
            // Encontrou ponto para retornar no padrão  
            if(padrao.charAt(i) == padrao.charAt(tamPrefixoAnt)){
                tamPrefixoAnt++;
                prefixos[i] = tamPrefixoAnt;
                i++;
            } else {
                // Não encontrou mas o anterior ainda pode encontrar
                if (tamPrefixoAnt != 0) {
                    tamPrefixoAnt = prefixos [tamPrefixoAnt -1];
                } 
                // Não encontrou prefixo na posição, então = 0
                else {
                    prefixos[i] = 0;
                    i++;
                }
            }
        }
        return prefixos;
    }

    public static String gerarTextoArquivo(String file) throws FileNotFoundException, IOException{
        StringBuilder sb = new StringBuilder();
        DataInputStream dis = new DataInputStream(new FileInputStream(file));

        // Descarta o primeiro int (ultimo id)
        dis.readInt();

        // Lê todo o arquivo e salva todos os nomes dos billionarios
        while (dis.available() > 0){
            Billionaire billionarioTmp = new Billionaire();
            byte[] bt;
            int len;
            char lapide;

            lapide = dis.readChar();
            len = dis.readInt();
            bt = new byte[len];
            dis.read(bt);
            billionarioTmp.fromByteArray(bt);
            if(lapide == '*')
                continue;
            // Adiciona o nome de cada billionario a string
            sb.append(billionarioTmp.getName().toLowerCase());
        }

        dis.close();
        return sb.toString();
    }

    public static int buscarKMP(String padrao, String file) throws FileNotFoundException, IOException{
        int[] vetorFalha = vetorFalha(padrao);
        int encontrados = 0;
        int i = 0, j = 0;
        String texto = gerarTextoArquivo(file);
        padrao = padrao.toLowerCase();
        
        // i caminha no arquivo, j no padrão
        while(i < texto.length()){
            // Se ambos forem iguais caminha os dois 1 casa
            if(texto.charAt(i) == padrao.charAt(j)){
                i++;
                j++;
            }
            // Se o j tiver percorrido todo o padrão, padrão encontrado
            if(j == padrao.length()){
                // Adiciona a encontrados e continua procurando usando o vetor de falha
                encontrados++;
                j = vetorFalha[j-1];
            }
            // Se não testa se ainda a texto para percorrer e se os valores do texto e do padrão são diferentes 
            else if (i < texto.length() && texto.charAt(i) != padrao.charAt(j)){
                // Se o erro não tiver acontecido no primeiro caracter do padrão
                if (j != 0)
                    j = vetorFalha[j-1]; // Usa o vetor de falha para caminhar no padrão
                else // Se tiver acontecido no primeiro caracter do padrão apenas caminha no texto
                i++;
            }
        }
        return encontrados;
    }
}
