package services;

import DAO.DAO_Hash;
import DAO.DAO_BTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import models.ArvoreBElemento;
import models.Billionaire;

public class CRUD_BTree {
    public static int createAll() {

        String line;

        String file = "src/database/billionairesTree.db";
        String fileCSV = "src/database/forbes_billionaires.csv";
        String indexFile = "src/database/indexTree.db";

        int id = -1;

        BufferedReader reader;

        // Deleta os arquivos antigos
        new File(file).delete();
        new File(indexFile).delete();

        BufferedReader countLines;
        // Cria os arquivos novos
        try {
            countLines = new BufferedReader(new FileReader(fileCSV));
            int csvLines = (int) countLines.lines().count() - 1;
            countLines.close();

            reader = new BufferedReader(new FileReader(fileCSV));
            reader.readLine(); // pula o cabeçalho
            int currentLine = 0;
            int percent = 0;

            RandomAccessFile raf = new RandomAccessFile(file, "rw");

            // Reserva espaço para o último ID
            raf.writeInt(0);

            long posicao;

            // Cria base para Arvore

            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");

            int maxPagina = 5; // Maximo de elementos por pagina
            long raiz = 8; // Endereço da raiz

            rafIndex.writeLong(raiz); // Aponta para Raiz

            createPagina(indexFile, raiz, maxPagina);

            System.out.println("Criando Database... Aguarde");
            System.out.println("Adicionando " + csvLines + " elementos:");

            while ((line = reader.readLine()) != null) {

                rafIndex.seek(0);
                raiz = rafIndex.readLong();

                String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                posicao = raf.getFilePointer(); // posição antes da escrita

                DAO_Hash.create(row, raf);

                id = Integer.parseInt(row[0]);

                insertTree(id, posicao, indexFile, maxPagina, raiz, raiz); // Adiciona Elemento no arquivo de indice

                // Loading Start
                if (percent != Math.round((float) currentLine / csvLines * 100) || currentLine == 0) {
                    percent = Math.round((float) currentLine / csvLines * 100);
                    System.out.print("\r[");
                    for (int i = 0; i < percent; i += 2)
                        System.out.print("█");
                    for (int i = percent; i <= 100; i += 2)
                        System.out.print(" ");
                    System.out.print("][ " + percent + "% ]");
                }
                currentLine++;
                // Loading End

            }

            // Volta ao início e grava o último ID inserido
            raf.seek(0);
            raf.writeInt(id);

            rafIndex.close();
            raf.close();
            reader.close();
            System.out.println("CSV convertido para Database!");

        } catch (

        Exception e) {
            System.err.println("Erro ReadCSV.createAll: " + e);
        }

        return id;
    }

    public static ArvoreBElemento insertTree(int id, long posicao, String indexFile, int maxPagina, long pagina,
            long raiz) {

        try {

            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");

            rafIndex.seek(pagina);

            int tamanhoPagina = rafIndex.readInt();

            // Quebrar

            if (tamanhoPagina >= maxPagina) {

                /**
                 *  Tranforma a pagina já existente no filho esquerdo, cria uma nova pagina (filho direito), e escreve 
                 * 
                 *  Se a pagina atual for a raiz:
                 *  Tranforma a pagina já existente em raiz, mantêm o elemento que vai subir, rebalanceia os elementos filhos
                 */

                // Ordenar

                ArrayList<ArvoreBElemento> arrElemento = new ArrayList<>();

                for (int i = 0; i < tamanhoPagina; i++) {
                    ArvoreBElemento arvoreBElemento = new ArvoreBElemento();

                    arvoreBElemento.setEsq(rafIndex.readLong());
                    arvoreBElemento.setId(rafIndex.readInt());
                    arvoreBElemento.setPosicao(rafIndex.readLong());
                    arvoreBElemento.setDir(rafIndex.readLong());

                    arrElemento.add(arvoreBElemento);

                    rafIndex.seek(rafIndex.getFilePointer() - 8);
                }

                for (int i = 0; i < arrElemento.size(); i++) {

                    int menor = i;

                    for (int j = i + 1; j < arrElemento.size(); j++) {

                        if (arrElemento.get(j).getId() < arrElemento.get(menor).getId()) {
                            menor = j;
                        }

                    }

                    ArvoreBElemento arvoreBElementoTmp = arrElemento.get(i);

                    arrElemento.set(i, arrElemento.get(menor));

                    arrElemento.set(menor, arvoreBElementoTmp);

                }

                //

                rafIndex.seek(pagina);

                rafIndex.writeInt(maxPagina / 2);
                for (int i = 0; i < maxPagina; i++) {

                    if (i <= maxPagina / 2) {

                        rafIndex.writeLong(arrElemento.get(i).getEsq());
                        rafIndex.writeInt(arrElemento.get(i).getId());
                        rafIndex.writeLong(arrElemento.get(i).getPosicao());
                        rafIndex.writeLong(arrElemento.get(i).getDir());
                        rafIndex.seek(rafIndex.getFilePointer() - 8);

                    } else {

                        rafIndex.writeLong(-1);
                        rafIndex.writeInt(0);
                        rafIndex.writeLong(-1);
                        rafIndex.writeLong(-1);
                        rafIndex.seek(rafIndex.getFilePointer() - 8);

                    }

                }

                long newPagina = createPagina(indexFile, rafIndex.length(), maxPagina);

                rafIndex.seek(newPagina);

                rafIndex.writeInt(maxPagina / 2);
                for (int i = (maxPagina / 2) + 1; i < maxPagina; i++) {

                    rafIndex.writeLong(arrElemento.get(i).getEsq());
                    rafIndex.writeInt(arrElemento.get(i).getId());
                    rafIndex.writeLong(arrElemento.get(i).getPosicao());
                    rafIndex.writeLong(arrElemento.get(i).getDir());
                    rafIndex.seek(rafIndex.getFilePointer() - 8);

                }

                if (pagina == raiz) {
                    // Se a pagina cheia for a raiz

                    long newRaiz = createPagina(indexFile, rafIndex.length(), maxPagina);

                    rafIndex.seek(newRaiz);
                    rafIndex.writeInt(1);
                    rafIndex.writeLong(pagina);
                    rafIndex.writeInt(arrElemento.get(maxPagina / 2).getId());
                    rafIndex.writeLong(arrElemento.get(maxPagina / 2).getPosicao());
                    rafIndex.writeLong(newPagina);

                    rafIndex.seek(0);
                    rafIndex.writeLong(newRaiz);

                    raiz = newRaiz;

                } else {

                    rafIndex.seek(raiz);

                    int tamanhoRaiz = rafIndex.readInt();

                    if (tamanhoRaiz >= maxPagina) {

                    } else {
                        tamanhoRaiz = tamanhoRaiz + 1;

                        rafIndex.seek(raiz);
                        rafIndex.writeInt(tamanhoRaiz);

                        rafIndex.seek(raiz + 4 + ((tamanhoRaiz - 1) * 20) + 8);

                        rafIndex.writeInt(arrElemento.get(maxPagina / 2).getId());
                        rafIndex.writeLong(arrElemento.get(maxPagina / 2).getPosicao());
                        rafIndex.writeLong(newPagina);

                    }

                }

                rafIndex.close();
                return insertTree(id, posicao, indexFile, maxPagina, raiz, raiz);

            }

            boolean isFolha = false;

            for (int i = 0; i <= tamanhoPagina; i++) {

                long esq = rafIndex.readLong(); // Esq
                if (esq == -1) {
                    isFolha = true;
                }
                int elemento = rafIndex.readInt(); // Elemento
                rafIndex.readLong(); // Posicao Elemento

                if (isFolha) { // Se for Folha

                    if (elemento == 0) { // Se o elemento da pagina for vazio = Inserir

                        rafIndex.seek(rafIndex.getFilePointer() - 12);
                        rafIndex.writeInt(id);
                        rafIndex.writeLong(posicao);
                        // Insere elemento na pagina

                        tamanhoPagina++;

                        rafIndex.seek(pagina);
                        rafIndex.writeInt(tamanhoPagina); // Adicionar no tamanho da pagina

                        rafIndex.close();
                        return null; // -1 = Escreveu

                    }

                } else {

                    // Caminhar Arvore
                    if (id < elemento) {
                        // ID é menor = Filho Esquerdo
                        rafIndex.close();
                        return insertTree(id, posicao, indexFile, maxPagina, esq, raiz);
                    } else if (elemento == 0) {
                        // ID é maior que o anterior e o atual não existe = Filho Esquerdo (Direito do ponto de vista do anterior)
                        rafIndex.close();
                        return insertTree(id, posicao, indexFile, maxPagina, esq, raiz);
                    }

                }

            }

            long dir = rafIndex.readLong(); // Dir

            rafIndex.close();

            // Caminhar arvore se for o ultimo elemento da pagina
            // ID é maior = Filho Direito
            return insertTree(id, posicao, indexFile, maxPagina, dir, raiz);

        } catch (

        Exception e) {
            System.err.println("Erro ao adicionar na Arvore: " + e);
        }

        return null;

    }

    // Funções da Arvore

    public static long createPagina(String indexFile, long pagina, int maxPagina) {

        try {

            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");

            rafIndex.seek(pagina);

            rafIndex.writeInt(0); // Tamanho da pagina

            for (int i = 0; i < maxPagina; i++) {
                rafIndex.writeLong(-1); // Ponteiro
                rafIndex.writeInt(0); // Valor
                rafIndex.writeLong(-1); // Endereço
            }
            rafIndex.writeLong(-1); // Ponteiro

            rafIndex.close();
            return pagina;

        } catch (Exception e) {
            System.err.println("Erro na criação de pagina: " + e);
        }
        return -1;
    }

    // --

    public static void create(String file) {

        String indexFile = "src/database/indexTree.db";

        try {

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");

            long raiz = rafIndex.readLong(); // Pega ponteiro da Raiz

            byte[] bt;

            randomAccessFile.seek(0);

            int lastId = randomAccessFile.readInt() + 1; // Lê ultimo ID (Cabeçalho)

            randomAccessFile.seek(0);

            randomAccessFile.writeInt(lastId); // Adiciona ID a ser adicionado

            Billionaire newBillionaire = BillionaireService.newBillionaire(lastId);

            randomAccessFile.seek(randomAccessFile.length()); // Move ponteiro para fim do arquivo

            long posicao = randomAccessFile.getFilePointer();
            insertTree(lastId, posicao, indexFile, 5, raiz, raiz); // Adiciona Elemento no arquivo de indice

            // Inserir newBillionaire no arquivo original

            bt = newBillionaire.toByteArray();
            randomAccessFile.write(bt);

            randomAccessFile.close();
            rafIndex.close();

        } catch (Exception e) {
            System.out.println("Erro CREATE: " + e);
        }

    }

    public static Billionaire getIndex(int key) {

        String file = "src/database/billionairesTree.db";
        String indexFile = "src/database/indexTree.db";

        try {

            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");
            RandomAccessFile rafBilionario = new RandomAccessFile(file, "rw");

            rafIndex.seek(0);
            long raiz = rafIndex.readLong(); // Pega ponteiro da Raiz

            long indexBilionario = pesquisarArvore(key, indexFile, raiz); // Pega ponteiro do Billionario

            Billionaire billionaire = BillionaireService.read(file, indexBilionario); // Cria Bilionario

            System.out.println(billionaire);

            rafIndex.close();
            rafBilionario.close();
            return billionaire;

        } catch (Exception e) {
            System.err.println("Erro na leitura: " + e);
        }
        System.out.println("Bilionário não encontrado");
        return null;
    }

    public static long pesquisarArvore(int key, String indexFile, long pagina) {

        try {

            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");

            rafIndex.seek(pagina);
            int tamanhoPagina = rafIndex.readInt();

            for (int i = 0; i < tamanhoPagina; i++) {

                long esq = rafIndex.readLong();
                int id = rafIndex.readInt();
                long posicao = rafIndex.readLong();

                if (key == id) {
                    rafIndex.close();
                    return posicao; // Se achar o elemento na arvore, retornar posição dele no arquivo de Bilionarios
                } else if (key < id) {
                    rafIndex.close();
                    return pesquisarArvore(key, indexFile, esq);
                }

            }

            long dir = rafIndex.readLong();
            if (dir == -1) {
                rafIndex.close();
                return -1;
            } else {

                rafIndex.close();
                return pesquisarArvore(key, indexFile, dir);

            }

        } catch (Exception e) {
            System.err.println("Erro na pesquisa: " + e);
        }

        return -1;
    }

    public static long pesquisarPonteiroArvore(int key, String indexFile, long pagina) {

        try {

            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");

            rafIndex.seek(pagina);
            int tamanhoPagina = rafIndex.readInt();

            for (int i = 0; i < tamanhoPagina; i++) {

                long esq = rafIndex.readLong();
                int id = rafIndex.readInt();
                long posicao = rafIndex.getFilePointer();
                rafIndex.readLong();

                if (key == id) {
                    rafIndex.close();
                    return posicao; // Se achar o elemento na arvore, retornar posição do ponteiro dele na arvore
                } else if (key < id) {
                    rafIndex.close();
                    return pesquisarPonteiroArvore(key, indexFile, esq);
                }

            }

            long dir = rafIndex.readLong();
            if (dir == -1) {
                rafIndex.close();
                return -1;
            } else {

                rafIndex.close();
                return pesquisarPonteiroArvore(key, indexFile, dir);

            }

        } catch (Exception e) {
            System.err.println("Erro na pesquisa: " + e);
        }

        return -1;
    }

    // Update

    public static void update(String id, String file) {

        int key = Integer.parseInt(id);

        Billionaire billionaire = getIndex(key);

        if (billionaire == null) {
            System.out.println("Bilionário não encontrado!");
            return;
        }

        Billionaire newBillionaire = BillionaireService.updateBillionaire(billionaire);

        DAO_BTree.update(newBillionaire, billionaire, key);

    }

    //

    public static void delete(int id, String file) {

        boolean isDeleted = DAO_BTree.deleteIndex(id);

        if (isDeleted == true) {
            System.out.println("Bilionário Deletado!");
        } else {
            System.out.println("Bilionário não deletado!");
        }

    }

}
