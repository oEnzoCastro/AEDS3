import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {

        FileInputStream fileInputStream;
        DataInputStream dataInputStream;

        try {
            
            fileInputStream = new FileInputStream("src/database/forbes_billionaires.csv");
            dataInputStream = new DataInputStream(fileInputStream);

            System.out.println(dataInputStream.readUTF());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }





/*
        Test test = new Test(5, "Enzo", 1.23);

        FileOutputStream fileOutputStream;
        DataOutputStream dataOutputStream;

        // 

        FileInputStream fileInputStream;
        DataInputStream dataInputStream;

        byte[] bt;

        try {
            fileOutputStream = new FileOutputStream("teste.db");
            dataOutputStream = new DataOutputStream(fileOutputStream);

            bt = test.toByteArray();

            dataOutputStream.writeInt(bt.length); // Byte para guardar tamanho do Objeto
            dataOutputStream.write(bt); // Insere objeto

            fileOutputStream.close();

            //

            fileInputStream = new FileInputStream("teste.db");
            dataInputStream = new DataInputStream(fileInputStream);

            int len = dataInputStream.readInt(); // Lê tamanho do primeiro Objeto

            bt = new byte[len]; // Byte do tamanho do Objeto

            dataInputStream.read(bt);

            Test testTmp = new Test();

            testTmp.fromByteArray(bt);

            System.out.println(testTmp.num);
            
        } catch (Exception e) {
            
        }
*/
    }
}
