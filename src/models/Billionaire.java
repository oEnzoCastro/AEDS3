package models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

public class Billionaire {
    private int id;
    private String name;
    private float netWorth;
    private String country;
    private ArrayList<String> source;
    private int rank;
    private int age;
    private String residence;
    private String citizenship;
    private String status;
    private int children;
    private String education;
    private Boolean selfMade;
    private LocalDate birthdate;

    public String toString() {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return "\nId:" + id +
                "\nNome:" + name +
                "\nNetWorth:" + df.format(netWorth) +
                "\nCountry:" + country +
                "\nSource: " + source.toString() +
                "\nRank: " + rank +
                "\nAge: " + age +
                "\nResidence: " + residence +
                "\nCitizenship: " + citizenship +
                "\nStatus: " + status +
                "\nChildren: " + children +
                "\nEducation: " + education +
                "\nSelfmade: " + selfMade +
                "\nBirthdate: " + birthdate

        ;
    }

    public Billionaire(int id, String name, float netWorth, String country, ArrayList<String> source, int rank,
            int age,
            String residence, String citizenship, String status, int children, String education, Boolean selfMade,
            LocalDate birthdate) {

        this.id = id;
        this.name = name;
        this.netWorth = netWorth;
        this.country = country;
        this.source = source;
        this.rank = rank;
        this.age = age;
        this.residence = residence;
        this.citizenship = citizenship;
        this.status = status;
        this.children = children;
        this.education = education;
        this.selfMade = selfMade;
        this.birthdate = birthdate;
    }

    public Billionaire() {

        this.id = -1;
        this.name = "";
        this.netWorth = 0;
        this.country = "";
        this.source = new ArrayList<String>();
        this.rank = -1;
        this.age = 0;
        this.residence = "";
        this.citizenship = "";
        this.status = "";
        this.children = 0;
        this.education = "";
        this.selfMade = false;
        this.birthdate = null;
    }

    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        dataOutputStream.writeInt(getByteSize()); // Chama função que retorna tamanho total do objeto

        dataOutputStream.writeInt(id);
        dataOutputStream.writeUTF(name);
        dataOutputStream.writeFloat(netWorth);

        String countryTmp = country;
        
        while (countryTmp.length() < 20) {
            countryTmp += " ";
        }

        dataOutputStream.writeUTF(countryTmp.substring(0, 20));
        dataOutputStream.writeInt(source.size()); // Tamanho do array (Quantidade, não bits)
        for (int i = 0; i < source.size(); i++) {
            dataOutputStream.writeUTF(source.get(i)); // adiciona cada elemento do array
        }
        dataOutputStream.writeInt(rank);
        dataOutputStream.writeInt(age);
        dataOutputStream.writeUTF(residence);
        dataOutputStream.writeUTF(citizenship);
        dataOutputStream.writeUTF(status);
        dataOutputStream.writeInt(children);
        dataOutputStream.writeUTF(education);
        dataOutputStream.writeBoolean(selfMade);
        dataOutputStream.writeLong(birthdate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond());

        return byteArrayOutputStream.toByteArray();
    }

    public int getByteSize() {

        int size = 0;

        size += Integer.BYTES; // Id (INT)

        size += getUTFSize(name); // Name (UTF8)

        size += Float.BYTES; // NetWorth (FLOAT)

        size += getUTFSize(country); // Country (UTF8)

        size += Integer.BYTES; // Source (Array [UTF8])

        size += Integer.BYTES; // Rank (INT)

        size += Integer.BYTES; // Age (INT)

        size += getUTFSize(residence); // Residence (UTF8)

        size += getUTFSize(citizenship); // Citizenship (UTF8)

        size += getUTFSize(status); // Status (UTF8)

        size += Integer.BYTES; // Children (INT)

        size += getUTFSize(education); // Education (UTF8)

        size += Byte.BYTES; // SelfMade (BOOLEAN)

        size += Long.BYTES; // Birthdate (UNIX TIMESTAMP / LONG)


        System.out.println(size);

        return size;
    }

    public int getUTFArraySize(ArrayList<String> array) {

        int utfArraySize = Integer.BYTES;

        for (String i : array) {
            utfArraySize += getUTFSize(i);
        }

        return utfArraySize;
    }

    public int getUTFSize(String text) {

        int utfSize = text.length();

        return utfSize + 2;
    }

    public void fromByteArray(byte[] bt) throws IOException {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bt);

        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        dataInputStream.readInt(); // Lapide

        id = dataInputStream.readInt();
        name = dataInputStream.readUTF();

        netWorth = dataInputStream.readFloat();
        country = dataInputStream.readUTF();
        int arraySize = dataInputStream.readInt();
        source.clear();
        for (int i = 0; i < arraySize; i++) {
            source.add(dataInputStream.readUTF());
        }
        rank = dataInputStream.readInt();
        age = dataInputStream.readInt();
        residence = dataInputStream.readUTF();
        citizenship = dataInputStream.readUTF();
        status = dataInputStream.readUTF();
        children = dataInputStream.readInt();
        education = dataInputStream.readUTF();
        selfMade = dataInputStream.readBoolean();
        birthdate = Instant.ofEpochSecond(dataInputStream.readLong()).atZone(ZoneId.systemDefault()).toLocalDate();

    }

}
