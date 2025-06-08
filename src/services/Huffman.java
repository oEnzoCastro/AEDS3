package services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import models.NoHuffman;

public class Huffman {
    public static Map<Byte, Integer> contarBytes (String file) throws IOException{
        // Lê byte a byte e adiciona ao hash junto a sua frequência somando 1
        Map<Byte, Integer> freq = new HashMap<>();
        try(FileInputStream fis = new FileInputStream(file)){
            int leitura;
            while((leitura = fis.read()) != -1){
                byte byteLido = (byte) leitura;
                freq.put(byteLido, freq.getOrDefault(byteLido, 0) + 1);
            }
        }
        return freq;
    }

    public static NoHuffman construirArvore(Map<Byte, Integer> freq){
        PriorityQueue<NoHuffman> fila = new PriorityQueue<>(Comparator.comparingInt(NoHuffman::getNum));
        
        // Adiciona na fila os valores (em ordem por ser priority)
        for(Map.Entry<Byte, Integer> entry : freq.entrySet()){
            fila.add(new NoHuffman(entry.getValue(), entry.getKey()));
        }
        while (fila.size() > 1){
            // Pega os 2 primeiros nós menores
            NoHuffman no1 = fila.poll();
            NoHuffman no2 = fila.poll();
            // Cria o pai com o valor da soma dos menores e o simbolo = 0
            NoHuffman pai = new NoHuffman(no1.getNum() + no2.getNum(), (byte) 0);
            // Faz o pai apontar para os nós 
            pai.setEsq(no1);
            pai.setDir(no2);
            // Adiciona o pai a fila
            fila.add(pai);
        }
        return fila.poll(); // retorna a raiz 
    }
    
    public static void salvarArvore(NoHuffman raiz, String file) throws IOException{
        // Salva o objeto da raiz que como o nó é serializavel salva todos os nós que ela aponta
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))){
            oos.writeObject(raiz);
        }
    }

    public static NoHuffman carregarArvore (String file) throws IOException, ClassNotFoundException {
        // Lê o objeto e retorna a raiz
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            return (NoHuffman) ois.readObject();
        }
    }

    // Gera os códigos do dicionário lendo a ávore
    public static Map<Byte, String> gerarCodigos(NoHuffman raiz) {
        Map<Byte, String> codigos = new HashMap<>();
        gerarCodigosRec(raiz, "", codigos);
        return codigos;
    }

    private static void gerarCodigosRec(NoHuffman no, String file, Map<Byte, String> codigos) {
        // Se for folha escreve no codigo o simbolo e o binário correspondente 
        if (no.isFolha()) {
            codigos.put(no.getSymbol(), file);
            return;
        }
        // Se for para a esquerda da árvore adiciona 0, se for para a direita adiciona 1
        gerarCodigosRec(no.getEsq(), file + "0", codigos);
        gerarCodigosRec(no.getDir(), file + "1", codigos);
    }

    public static void compactarArquivo(String arqOriginal, String arqCompactado, Map<Byte, String> codigos) throws IOException {
        StringBuilder bits = new StringBuilder();

        // Lê o arquivo original e transforma em códigos através do dicionario
        try (FileInputStream fis = new FileInputStream(arqOriginal)) {
            int b;
            while ((b = fis.read()) != -1) {
                bits.append(codigos.get((byte) b));
            }
        }

        // Escreve os bits como bytes em arquivo compactado
        try (FileOutputStream fos = new FileOutputStream(arqCompactado)) {
            int i = 0;
            while (i < bits.length()) {
                int byteVal = 0;
                for (int bit = 0; bit < 8 && i < bits.length(); bit++, i++) {
                    if (bits.charAt(i) == '1') {
                        byteVal |= (1 << (7 - bit));
                    }
                }
                fos.write(byteVal);
            }

            // Escreve o número de bits válidos no último byte
            int resto = bits.length() % 8;
            fos.write(resto == 0 ? 8 : resto); 
        }
    }

    public static void descompactarArquivo(String arqCompactado, String arqSaida, NoHuffman raiz) throws IOException {
        byte[] dados = Files.readAllBytes(Paths.get(arqCompactado));
        if (dados.length < 2) return;

        // Lê e depois ignora o ultimo byte que diz o número de bits validos do ultimo byye
        int bitsValidosUltimoByte = dados[dados.length - 1];
        int limite = dados.length - 1;

        // Escreve os bytes em bits
        StringBuilder bits = new StringBuilder();
        for (int i = 0; i < limite; i++) {
            for (int j = 7; j >= 0; j--) {
                bits.append((dados[i] >> j) & 1);
            }
        }

        // Remove bits extras se necessário
        if (bitsValidosUltimoByte < 8) {
            bits.setLength(bits.length() - (8 - bitsValidosUltimoByte));
        }

        // Decodifica usando a árvore
        try (FileOutputStream fos = new FileOutputStream(arqSaida)) {
            NoHuffman atual = raiz;
            for (int i = 0; i < bits.length(); i++) {
                // Se for bit for zero vai para a esquerda da árvore se for 1 para a direita
                atual = (bits.charAt(i) == '0') ? atual.getEsq() : atual.getDir();
                // Se for folha escreve o simbolo no arquivo descompactado
                if (atual.isFolha()) {
                    fos.write(atual.getSymbol());
                    atual = raiz;
                }
            }
        }
    }

    public static String comprimir (String file) throws IOException, ClassNotFoundException{
        // Escolhe se usará o arquivo de hash ou árvore
        String compactado = file.equals("src/database/billionairesHash.db") ? "src/database/billionairesHashHuffmanCompactada.db" : "src/database/billionairesTreeHuffmanCompactada.db";
        String arvoreDB = file.equals("src/database/billionairesHash.db") ? "src/database/arvoreHuffmanHash.db" : "src/database/arvoreHuffmanTree.db";

        Map<Byte, Integer> freq = contarBytes(file); // Conta os bytes e sua frequência
        NoHuffman raiz = construirArvore(freq); // Constroi a árvore
        salvarArvore(raiz, arvoreDB); // Salva a árvore no arquivo selecionado

        Map<Byte, String> codigos = gerarCodigos(raiz); // Gera os códigos do dicionário
        compactarArquivo(file, compactado, codigos); // Gera a compactação no arquivo selecionado

        return compactado;
    }

    public static String descomprimir (String file) throws IOException, ClassNotFoundException {
        // Escolhe se usará o arquivo de hash ou árvore
        String compactado = file.equals("src/database/billionairesHash.db") ? "src/database/billionairesHashHuffmanCompactada.db" : "src/database/billionairesTreeHuffmanCompactada.db";
        String descompactado = file.equals("src/database/billionairesHash.db") ? "src/database/billionairesHashHuffman_recuperado.db" : "src/database/billionairesTreeHufman_recuperado.db";
        String arvoreDB = file.equals("src/database/billionairesHash.db") ? "src/database/arvoreHuffmanHash.db" : "src/database/arvoreHuffmanTree.db";

        NoHuffman arvoreLida = carregarArvore(arvoreDB); // Pega a árvore do arquivo na memória
        descompactarArquivo(compactado, descompactado, arvoreLida); // Descompacta o arquivo selecionado através da árvore
        return descompactado;
    }

    public static void taxaCompressao(String arqOriginal, String arqCompactado) throws IOException {
        // Pega o tamanho do arquivo original e do compactado e consegue a taxa de compressão
        long tamanhoOriginal = Files.size(Paths.get(arqOriginal));
        long tamanhoCompactado = Files.size(Paths.get(arqCompactado));
    
        double economia = tamanhoOriginal - tamanhoCompactado;
        double percentual = (economia / tamanhoOriginal) * 100;
    
        System.out.println("Tamanho original: " + tamanhoOriginal + " bytes (" + String.format("%.2f", tamanhoOriginal / 1024.0) + " KB)");
        System.out.println("Tamanho compactado: " + tamanhoCompactado + " bytes (" + String.format("%.2f", tamanhoCompactado / 1024.0) + " KB)");
        System.out.println("Economia: " + String.format("%.0f", economia) + " bytes (" + String.format("%.2f", economia / 1024.0) + " KB) -> " + String.format("%.2f", percentual) + "%");
    }

    public static void compararRecuperado(String arqOriginal, String arqRecuperado) throws IOException {
        // Compara byte a byte o arquivo original e o arquivo recuperado para ver se funcionou perfeitamente
        byte[] original = Files.readAllBytes(Paths.get(arqOriginal));
        byte[] recuperado = Files.readAllBytes(Paths.get(arqRecuperado));
    
        int tamanhoMin = Math.min(original.length, recuperado.length);
        int iguais = 0;
    
        for (int i = 0; i < tamanhoMin; i++) {
            if (original[i] == recuperado[i]) {
                iguais++;
            }
        }
    
        double percentual = (iguais / (double) original.length) * 100;
    
        System.out.println("Bytes idênticos: " + iguais + " de " + original.length + " (" + String.format("%.2f", percentual) + "%)");
    
        if (original.length != recuperado.length) {
            System.out.println("Erro: tamanhos diferentes (" + original.length + " vs " + recuperado.length + " bytes)");
        }
    }    
}
