package services;

import DAO.DAO;
import DAO.DAO_BTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import models.Billionaire;

public class CRUD_BTree {
    public static int createAll() {

        String line;

        String file = "src/database/billionaires.db";
        String fileCSV = "src/database/forbes_billionaires.csv";
        String indexFile = "src/database/indexTree.db";

        int id = -1;

        BufferedReader reader;

        // Deleta os arquivos antigos
        new File(file).delete();
        new File(indexFile).delete();

        // Cria os arquivos novos
        try {

            reader = new BufferedReader(new FileReader(fileCSV));
            reader.readLine(); // pula o cabeçalho

            RandomAccessFile raf = new RandomAccessFile(file, "rw");

            // Reserva espaço para o último ID
            raf.writeInt(0);

            long posicao;

            // Cria base para Arvore

            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");

            int maxPagina = 5; // Maximo de elementos por pagina
            long raiz = 8; // Endereço da raiz

            rafIndex.writeLong(raiz); // Aponta para Raiz            
            rafIndex.writeInt(0); // Tamanho da Raiz

            for (int i = 0; i < maxPagina; i++) {
                rafIndex.writeLong(-1); // Ponteiro
                rafIndex.writeInt(0); // Valor
                rafIndex.writeLong(-1); // Endereço
            }
            rafIndex.writeLong(-1); // Ponteiro

            while ((line = reader.readLine()) != null) {

                String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                posicao = raf.getFilePointer(); // posição antes da escrita

                DAO.create(row, raf);

                id = Integer.parseInt(row[0]);

                insertTree(id, posicao, indexFile, maxPagina, raiz); // Adiciona Elemento no arquivo de indice
            }

            // Volta ao início e grava o último ID inserido
            raf.seek(0);
            raf.writeInt(id);

            rafIndex.close();
            raf.close();
            reader.close();
            System.out.println("CSV convertido para Database!");

        } catch (Exception e) {
            System.err.println("Erro ReadCSV.createAll: " + e);
        }

        return id;
    }

    public static void insertTree(int id, long posicao, String indexFile, int maxPagina, long pagina) {

        try {

            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");

            rafIndex.seek(pagina);

            int tamanhoPagina = rafIndex.readInt();

            if (tamanhoPagina == 0) {
                rafIndex.readLong();
                rafIndex.writeInt(id);
                rafIndex.writeLong(posicao);

                rafIndex.seek(pagina);

                rafIndex.writeInt(tamanhoPagina + 1);

                return;

            }

            if (tamanhoPagina >= maxPagina) {

                // Ordenar

                ArrayList<Integer> arrId = new ArrayList<>();
                ArrayList<Long> arrPosicao = new ArrayList<>();

                for (int i = 0; i < tamanhoPagina; i++) {
                    rafIndex.readLong();
                    arrId.add(rafIndex.readInt());
                    arrPosicao.add(rafIndex.readLong());
                }

                for (int i = 0; i < arrId.size(); i++) {

                    int menor = i;

                    for (int j = i + 1; j < arrId.size(); j++) {

                        if (arrId.get(j) < arrId.get(menor)) {
                            menor = j;
                        }

                        if (arrId.get(j) < arrId.get(menor)) {
                            menor = j;
                        }

                    }

                    int tmpMenorId = arrId.get(i);
                    long tmpMenorPos = arrPosicao.get(i);

                    arrId.set(i, arrId.get(menor));
                    arrPosicao.set(i, arrPosicao.get(menor));

                    arrId.set(menor, tmpMenorId);
                    arrPosicao.set(menor, tmpMenorPos);

                }

                System.out.println();

                // Quebrar
                if (pagina == 8) { // Se o pai for a raiz

                    rafIndex.seek(pagina);

                    rafIndex.writeInt(1);

                    boolean isFolha = true;

                    if (rafIndex.readLong() != -1) {
                        isFolha = false;
                    }
                    rafIndex.seek(rafIndex.getFilePointer() - 8);
                    long esq = 0;
                    if (isFolha) {
                        esq = createPagina(indexFile, pagina + (4 + (20 * maxPagina) + 8), maxPagina);

                    } else {
                        esq = pagina + (4 + (20 * maxPagina) + 8);
                    }
                    for (int k = 0; k < (maxPagina / 2); k++) {
                        insertTree(arrId.get(k), arrPosicao.get(k), indexFile, maxPagina, esq);
                    }
                    rafIndex.writeLong(esq);

                    rafIndex.writeInt(arrId.get(maxPagina / 2));

                    rafIndex.writeLong(arrPosicao.get(maxPagina / 2));

                    long dir = 0;
                    if (isFolha) {
                        dir = createPagina(indexFile, pagina + ((4 + (20 * maxPagina) + 8) * 2), maxPagina);

                    } else {
                        dir = pagina + ((4 + (20 * maxPagina) + 8) * 2);
                    }
                    for (int k = (maxPagina / 2) + 1; k < maxPagina; k++) {
                        insertTree(arrId.get(k), arrPosicao.get(k), indexFile, maxPagina, dir);
                    }
                    rafIndex.writeLong(dir);

                    for (int i = 1; i < maxPagina; i++) {
                        rafIndex.writeInt(0);
                        rafIndex.writeLong(-1);
                        rafIndex.writeLong(-1);
                    }

                    System.out.println();

                } else {

                }

            }

            rafIndex.seek(pagina + 4);

            boolean isFolha = true;

            for (int i = 0; i <= tamanhoPagina; i++) {

                long esq = rafIndex.readLong();
                int elemento = rafIndex.readInt();

                if (esq != -1) {
                    isFolha = false;
                }

                if (id < elemento && isFolha == false) {
                    insertTree(id, posicao, indexFile, maxPagina, esq);
                    return;
                } else if (elemento == 0 && isFolha == false) {
                    insertTree(id, posicao, indexFile, maxPagina, esq);
                    return;
                }

                if (elemento == 0) {

                    rafIndex.seek(rafIndex.getFilePointer() - 4);
                    rafIndex.writeInt(id);
                    rafIndex.writeLong(posicao);

                    rafIndex.seek(pagina);

                    rafIndex.writeInt(tamanhoPagina + 1);

                    return;

                }

                rafIndex.readLong(); // Ler Endereco

            }

            long dir = rafIndex.readLong();

            insertTree(id, posicao, indexFile, maxPagina, dir);
            return;

        } catch (

        Exception e) {
            System.err.println("Erro ao adicionar na Arvore: " + e);
        }

    }

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

            return pagina;

        } catch (Exception e) {
            System.err.println("Erro na criação de pagina: " + e);
        }
        return -1;
    }

    //

    public static void create(String file) {
        try {
            String indexFile = "src/database/index.db";
            String bucketFile = "src/database/bucketFile.db";

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");
            byte[] bt;

            randomAccessFile.seek(0);

            int lastId = randomAccessFile.readInt() + 1;

            randomAccessFile.seek(0);

            randomAccessFile.writeInt(lastId);

            Billionaire newBillionaire = BillionaireService.newBillionaire(lastId);

            randomAccessFile.seek(randomAccessFile.length()); // Move ponteiro para fim do arquivo

            long posicao = randomAccessFile.getFilePointer();
            rafIndex.seek(rafIndex.length()); // Move para o fim do arquivo index
            DAO_BTree.createIndex(lastId, posicao, indexFile);

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

        String file = "src/database/billionaires.db";
        String indexFile = "src/database/index.db";
        String bucketFile = "src/database/bucketFile.db";

        try {

            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");
            RandomAccessFile rafBucket = new RandomAccessFile(bucketFile, "rw");

            int pG = rafIndex.readInt();

            int hash = ((key % pG) * 12) + 4; // Multiplicar por 8 que é o tamanho de Long (+ 4 para pular pG)

            rafIndex.seek(hash); // Ir para a posição no arquivo de Indice

            int pL = rafIndex.readInt(); // pL = Profundidade Local
            long posicaoBucket = rafIndex.readLong(); // PosicaoBucket = Valor da posição no Indice

            int bitsBucket = (4 * 12) + 4;

            posicaoBucket = (posicaoBucket * bitsBucket); // PosicaoBucket = Endereço do bucket

            rafBucket.seek(posicaoBucket); // Aponta para o Bucket a ser adicionado

            int numBucket = rafBucket.readInt();

            for (int i = 0; i < numBucket; i++) {

                int id = rafBucket.readInt();
                long posicao = rafBucket.readLong();
                System.out.println();

                if (key == id) {

                    Billionaire billionaire = DAO_BTree.read(file, posicao);

                    System.out.println(billionaire);

                    return billionaire;

                }

            }

            rafIndex.close();

        } catch (Exception e) {
            System.err.println("Erro na leitura: " + e);
        }
        System.out.println("Bilionário não encontrado");
        return null;
    }

    public static long getBucketPosition(int key) {

        String file = "src/database/billionaires.db";
        String indexFile = "src/database/index.db";
        String bucketFile = "src/database/bucketFile.db";

        try {

            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");
            RandomAccessFile rafBucket = new RandomAccessFile(bucketFile, "rw");

            int pG = rafIndex.readInt();

            int hash = ((key % pG) * 12) + 4; // Multiplicar por 8 que é o tamanho de Long (+ 4 para pular pG)

            rafIndex.seek(hash); // Ir para a posição no arquivo de Indice

            int pL = rafIndex.readInt(); // pL = Profundidade Local
            long posicaoBucket = rafIndex.readLong(); // PosicaoBucket = Valor da posição no Indice

            int bitsBucket = (4 * 12) + 4;

            posicaoBucket = (posicaoBucket * bitsBucket); // PosicaoBucket = Endereço do bucket

            rafIndex.close();

            return posicaoBucket;

        } catch (Exception e) {
            System.err.println("Erro na leitura: " + e);
        }
        System.out.println("Bilionário não encontrado");
        return -1;
    }

    public static long getBucketElementPosition(int key) {

        String file = "src/database/billionaires.db";
        String indexFile = "src/database/index.db";
        String bucketFile = "src/database/bucketFile.db";

        try {

            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");
            RandomAccessFile rafBucket = new RandomAccessFile(bucketFile, "rw");

            int pG = rafIndex.readInt();

            int hash = ((key % pG) * 12) + 4; // Multiplicar por 8 que é o tamanho de Long (+ 4 para pular pG)

            rafIndex.seek(hash); // Ir para a posição no arquivo de Indice

            int pL = rafIndex.readInt(); // pL = Profundidade Local
            long posicaoBucket = rafIndex.readLong(); // PosicaoBucket = Valor da posição no Indice

            int bitsBucket = (4 * 12) + 4;

            posicaoBucket = (posicaoBucket * bitsBucket); // PosicaoBucket = Endereço do bucket

            rafBucket.seek(posicaoBucket); // Aponta para o Bucket a ser adicionado

            int numBucket = rafBucket.readInt();

            for (int i = 0; i < numBucket; i++) {

                int id = rafBucket.readInt();
                long posicao = rafBucket.readLong();
                System.out.println();

                if (key == id) {

                    rafBucket.seek(rafBucket.getFilePointer() - 8); // Voltar para posição do bucket

                    return rafBucket.getFilePointer();

                }

            }

            rafIndex.close();

        } catch (Exception e) {
            System.err.println("Erro na leitura: " + e);
        }
        System.out.println("Bilionário não encontrado");
        return -1;
    }

    public static long getBillionairePosition(int key) {

        String file = "src/database/billionaires.db";
        String indexFile = "src/database/index.db";
        String bucketFile = "src/database/bucketFile.db";

        try {

            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");
            RandomAccessFile rafBucket = new RandomAccessFile(bucketFile, "rw");

            int pG = rafIndex.readInt();

            int hash = ((key % pG) * 12) + 4; // Multiplicar por 8 que é o tamanho de Long (+ 4 para pular pG)

            rafIndex.seek(hash); // Ir para a posição no arquivo de Indice

            int pL = rafIndex.readInt(); // pL = Profundidade Local
            long posicaoBucket = rafIndex.readLong(); // PosicaoBucket = Valor da posição no Indice

            int bitsBucket = (4 * 12) + 4;

            posicaoBucket = (posicaoBucket * bitsBucket); // PosicaoBucket = Endereço do bucket

            rafBucket.seek(posicaoBucket); // Aponta para o Bucket a ser adicionado

            int numBucket = rafBucket.readInt();

            for (int i = 0; i < numBucket; i++) {

                int id = rafBucket.readInt();
                long posicao = rafBucket.readLong();
                System.out.println();

                if (key == id) {

                    return posicao;

                }

            }

            rafIndex.close();

        } catch (Exception e) {
            System.err.println("Erro na leitura: " + e);
        }
        System.out.println("Bilionário não encontrado");
        return -1;
    }

    public static void update(String key, String file) {

        Billionaire billionaire;
        int id = Integer.parseInt(key);
        if (key.charAt(0) >= '0' && key.charAt(0) <= '9') {
            billionaire = getIndex(id);
        } else {
            System.out.println("Só é aceito update inserindo a chave 'ID'!");
            return;
        }

        if (billionaire == null) {
            System.out.println("Bilionário Indisponível!");
        } else {
            Billionaire newBillionaire = BillionaireService.updateBillionaire(billionaire);

            try {
                String indexFile = "src/database/index.db";
                String bucketFile = "src/database/bucketFile.db";

                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");
                RandomAccessFile rafBucket = new RandomAccessFile(bucketFile, "rw");

                byte[] bt;

                if (newBillionaire.getByteSize() > billionaire.getByteSize()) {

                    long posicaoBilionario = getBillionairePosition(id);

                    randomAccessFile.seek(posicaoBilionario);

                    randomAccessFile.writeChar('*'); // Adiciona Lapide

                    randomAccessFile.seek(randomAccessFile.length()); // Move ponteiro para fim do arquivo

                    long posicaoBucket = getBucketElementPosition(id);

                    rafBucket.seek(posicaoBucket);

                    rafBucket.writeLong(randomAccessFile.getFilePointer());

                    // Inserir newBillionaire

                    bt = newBillionaire.toByteArray();
                    randomAccessFile.write(bt);

                    randomAccessFile.close();
                    rafIndex.close();

                } else {

                    long filePointer = getBillionairePosition(id);

                    randomAccessFile.seek(filePointer);

                    bt = newBillionaire.toByteArrayUpdate(billionaire, file);
                    randomAccessFile.write(bt);

                    randomAccessFile.close();
                    rafIndex.close();
                    // Add newBillionaire no lugar do billionaire
                }

                randomAccessFile.close();
                rafIndex.close();
            } catch (Exception e) {
                System.err.println(e);
            }

        }

    }

    public static void delete(int id, String file) {

        boolean isDeleted = DAO_BTree.deleteIndex(id);

        if (isDeleted == true) {
            System.out.println("Bilionário Deletado!");
        } else {
            System.out.println("Bilionário não deletado!");
        }

    }

}
