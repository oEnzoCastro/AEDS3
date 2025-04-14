package services;

import DAO.DAO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import models.Billionaire;

public class CRUD {
    public static int createAll() {

        String line;
    
        String file = "src/database/billionaires.db";
        String fileCSV = "src/database/forbes_billionaires.csv";
        String indexFile ="src/database/index.db";

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
            
            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");

            // Reserva espaço para o último ID
            raf.writeInt(0);
    
            long posicao;
    
            while ((line = reader.readLine()) != null) {
    
                String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    
                posicao = raf.getFilePointer(); // posição antes da escrita
    
                DAO.create(row, raf);
    
                id = Integer.parseInt(row[0]);
                
                DAO.createIndex(id, posicao, rafIndex);
            }
    
            // Volta ao início e grava o último ID inserido
            raf.seek(0);
            raf.writeInt(id);
    
            raf.close();
            reader.close();
            rafIndex.close();
            System.out.println("CSV convertido para Database!");
    
        } catch (Exception e) {
            System.err.println("Erro ReadCSV.createAll: " + e);
        }
    
        return id;
    }    

    public static void create(String file) {
        try {
            String indexFile ="src/database/index.db";

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
            DAO.createIndex(lastId, posicao, rafIndex); // Insere no arquivo index

            // Inserir newBillionaire no arquivo original

            bt = newBillionaire.toByteArray();
            randomAccessFile.write(bt);

            randomAccessFile.close();
            rafIndex.close();

        } catch (Exception e) {
            System.out.println("Erro CREATE: " + e);
        }

    }

    public static Billionaire getIndex(int key){
        String file = "src/database/billionaires.db";
        String indexFile ="src/database/index.db";
        try{
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");

            while (rafIndex.getFilePointer() < rafIndex.length()) {
                int id = rafIndex.readInt();       // lê o id
                long posicao = rafIndex.readLong(); // lê a posição
                if (id == key) { // encontrou o id procurado
                    raf.seek(posicao); // vai até a posição no arquivo original

                    Billionaire billionaireTmp = new Billionaire();

                    byte[] bt;
                    int len;
                    char lapide;
            
                    lapide = raf.readChar(); // Ler Lapide
                    len = raf.readInt(); // Ler Tamanho Obj
            
                    bt = new byte[len];
                    raf.read(bt); // ler obj
            
                    billionaireTmp.fromByteArray(bt);
                    // Confere se o objeto está inativo, se sim, retornar null
                    if (lapide == '*') {
                        System.out.println("Bilionário não encontrado");
                        raf.close();
                        rafIndex.close();
                        return null;
                    }

                    System.out.println(billionaireTmp);
                    raf.close();
                    rafIndex.close();
                    return billionaireTmp;
                }
            }

            raf.close();
            rafIndex.close();

        }catch(Exception e){
            System.err.println("Erro na leitura: " + e);
        }
        System.out.println("Bilionário não encontrado");
        return null;
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
            System.out.println("Bilinário Indisponível!");
        } else {
            Billionaire newBillionaire = BillionaireService.updateBillionaire(billionaire);

            try {
                String indexFile ="src/database/index.db";

                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");

                byte[] bt;

                if (newBillionaire.getByteSize() > billionaire.getByteSize()) {

                    DAO.deleteIndex(id); // Insere Lapide no billionaire

                    randomAccessFile.seek(randomAccessFile.length()); // Move ponteiro para fim do arquivo

                    // Salva nova posiçao para por no arquivo index
                    long posicao = randomAccessFile.getFilePointer();
                    
                    //insere o novo local no arquivo indice
                    File tempFile = new File("src/database/temp_index.db");
                    RandomAccessFile tempIndex = new RandomAccessFile(tempFile, "rw");

                    tempIndex.seek(tempIndex.length()) ;
                    tempIndex.writeInt(id);
                    tempIndex.writeLong(posicao);

                    // Inserir newBillionaire

                    bt = newBillionaire.toByteArray();
                    randomAccessFile.write(bt);

                    tempIndex.close();
                    randomAccessFile.close();
                    rafIndex.close();

                    File original = new File(indexFile);
                    if (original.delete()) {
                        tempFile.renameTo(original);
                    }
                } else {

                    long filePointer = BillionaireService.findBillionaireByte(id, file);

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

        boolean isDeleted = DAO.deleteIndex(id);

        if (isDeleted == true) {
            System.out.println("Bilionário Deletado!");
        } else {
            System.out.println("Bilionário não deletado!");
        }

    }

}
