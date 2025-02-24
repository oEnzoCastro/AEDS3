import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
    
    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        dataOutputStream.writeDouble(num);

        return byteArrayOutputStream.toByteArray();
    }

    public void fromByteArray(byte[] bt) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bt);

        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        num = dataInputStream.readDouble();
    }

}
