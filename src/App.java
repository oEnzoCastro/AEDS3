import java.io.FileOutputStream;
import java.io.IOException;

import util.readCSV;

public class App {

    public static void main(String[] args) throws IOException {

        FileOutputStream fileOutputStream = null;

        fileOutputStream = new FileOutputStream("teste.db"); // Arquivo de banco de dados a inserir

        readCSV.read(fileOutputStream);

        fileOutputStream.close(); // Salva BD apenas no fim do programa

        /*
         * 
         * Test test = new Test(5, "Enzo", 1.23);
         * Test test2 = new Test(20, "Dudinha", 4.56);
         * Test testTmp = new Test();
         * 
         * FileOutputStream fileOutputStream;
         * DataOutputStream dataOutputStream;
         * 
         * //
         * 
         * FileInputStream fileInputStream;
         * DataInputStream dataInputStream;
         * 
         * byte[] bt;
         * int len;
         * 
         * try {
         * 
         * // Write
         * 
         * fileOutputStream = new FileOutputStream("teste.db");
         * dataOutputStream = new DataOutputStream(fileOutputStream);
         * 
         * bt = test.toByteArray();
         * dataOutputStream.writeInt(bt.length); // Byte para guardar tamanho do Objeto
         * dataOutputStream.write(bt); // Insere objeto
         * 
         * bt = test2.toByteArray();
         * dataOutputStream.writeInt(bt.length); // Byte para guardar tamanho do Objeto
         * dataOutputStream.write(bt); // Insere objeto
         * 
         * fileOutputStream.close();
         * 
         * // Read
         * 
         * fileInputStream = new FileInputStream("teste.db");
         * dataInputStream = new DataInputStream(fileInputStream);
         * 
         * while ((len = dataInputStream.readInt()) > 0) {
         * 
         * // len = dataInputStream.readInt(); // Lê tamanho do primeiro Objeto
         * bt = new byte[len]; // Byte do tamanho do Objeto
         * dataInputStream.read(bt);
         * testTmp.fromByteArray(bt);
         * System.out.println(testTmp);
         * 
         * }
         * 
         * } catch (Exception e) {
         * System.err.println(e.getMessage());
         * }
         */
    }
}
