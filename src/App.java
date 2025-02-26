import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import util.readCSV;

public class App {

    public static void main(String[] args) throws IOException {

        String file= "teste.db";

        FileOutputStream fileOutputStream = new FileOutputStream(file); // Arquivo de banco de dados a inserir
        DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

        dataOutputStream.writeInt(0);

        int lastId = readCSV.getAll(fileOutputStream);

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw"); // Atualiza cabeçalho último ID
        randomAccessFile.seek(0);
        randomAccessFile.writeInt(lastId);
        randomAccessFile.close();

        fileOutputStream.close(); // Salva BD apenas no fim do programa

        readCSV.get();

    }
}
