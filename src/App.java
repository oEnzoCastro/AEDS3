import java.io.FileOutputStream;
import java.io.IOException;

import services.ReadCSV;

public class App {

    public static void main(String[] args) throws IOException {

        /* --- Read CSV --- */

        String file= "src/database/billlionaines.db";

        FileOutputStream fileOutputStream = new FileOutputStream(file); // Arquivo de banco de dados a inserir
        
        // dataOutputStream.writeInt(0);
        
        int lastId = ReadCSV.createAll(fileOutputStream); // Read CSV -> Write BD

        System.out.println("Last ID: " + lastId);

        fileOutputStream.close(); // Salva BD apenas no fim do programa para não reescrever

        /* --- Read BD --- */

        ReadCSV.get();

    }
}
