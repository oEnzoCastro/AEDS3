import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class Test {
    protected int id;
    protected String text;
    protected Double num;

    public Test(int id, String text, Double num) {
        this.id = id;
        this.text = text;
        this.num = num;
    }

    public Test() {
        this.id = -1;
        this.text = "";
        this.num = 0.0;
    }

    public String toString(){
        DecimalFormat df= new DecimalFormat("#,##0.00");
        return "ID:"+ id  +
                "\nText:"+ text  +
                "\nNum:"+ df.format(num) + "\n";
    }
    
    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        dataOutputStream.writeInt(id);
        dataOutputStream.writeUTF(text);
        dataOutputStream.writeDouble(num);

        return byteArrayOutputStream.toByteArray();
    }

    public void fromByteArray(byte[] bt) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bt);

        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        id = dataInputStream.readInt();
        text = dataInputStream.readUTF();
        num = dataInputStream.readDouble();
    }

}
