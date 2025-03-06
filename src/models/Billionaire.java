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
    private ArrayList<String> education;
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
                "\nEducation: " + education.toString() +
                "\nSelfmade: " + selfMade +
                "\nBirthdate: " + birthdate

        ;
    }

    public Billionaire(int id, String name, float netWorth, String country, ArrayList<String> source, int rank, int age,
            String residence, String citizenship, String status, int children, ArrayList<String> education,
            Boolean selfMade, LocalDate birthdate) {

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
        this.education = new ArrayList<String>();
        this.selfMade = false;
        this.birthdate = null;
    }

    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        dataOutputStream.writeChar('*');
        // Print Size Object
        dataOutputStream.writeInt(getByteSize());
        // int id;
        dataOutputStream.writeInt(id);
        // String name;
        dataOutputStream.writeUTF(name);
        // float netWorth;
        dataOutputStream.writeFloat(netWorth);
        // String country;
        dataOutputStream.writeUTF(country);
        // ArrayList<String> source;
        dataOutputStream.writeInt(source.size());
        for (String sourceString : source) {
            dataOutputStream.writeUTF(sourceString);
        }
        // int rank;
        dataOutputStream.writeInt(rank);
        // int age;
        dataOutputStream.writeInt(age);
        // String residence;
        dataOutputStream.writeUTF(residence);
        // String citizenship;
        dataOutputStream.writeUTF(citizenship);
        // String status;
        dataOutputStream.writeUTF(status);
        // int children;
        dataOutputStream.writeInt(children);
        // ArrayList<String> education;
        dataOutputStream.writeInt(education.size());
        for (String educationString : education) {
            dataOutputStream.writeUTF(educationString);
        }
        // Boolean selfMade;
        dataOutputStream.writeBoolean(selfMade);
        // LocalDate birthdate;
        dataOutputStream.writeLong(birthdate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond());

        return byteArrayOutputStream.toByteArray();
    }

    public int getByteSize() {

        int size = 0;

        // int id;
        size += Integer.BYTES;
        // String name;
        size += getUTFSize(name);
        // float netWorth;
        size += Float.BYTES;
        // String country;
        size += getUTFSize(country);
        // ArrayList<String> source;
        size += getUTFArraySize(source);
        // int rank;
        size += Integer.BYTES;
        // int age;
        size += Integer.BYTES;
        // String residence;
        size += getUTFSize(residence);
        // String citizenship;
        size += getUTFSize(citizenship);
        // String status;
        size += getUTFSize(status);
        // int children;
        size += Integer.BYTES;
        // ArrayList<String> education;
        size += getUTFArraySize(education);
        // Boolean selfMade;
        size += 1; // Boolean = 1 BYTE
        // LocalDate birthdate;
        size += Long.BYTES;

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

        // Lapide - Char
        // Tamanho Objeto - Int
        // Id - Int
        id = dataInputStream.readInt();
        // Name - 2 Bytes Size + String
        name = dataInputStream.readUTF();
        // NetWorth - Float (43 31 00 00 = 177)
        netWorth = dataInputStream.readFloat();
        // Country - 2 Bytes Size + String
        country = dataInputStream.readUTF();
        // Tamanho Source - Int
        int tamanhoSource = dataInputStream.readInt();
        // Source Element - 2 Bytes Size + String !!!
        for (int i = 0; i < tamanhoSource; i++) {
            source.add(dataInputStream.readUTF());
        }
        // Rank - Int
        rank = dataInputStream.readInt();
        // Age - Int
        age = dataInputStream.readInt();
        // Residence - 2 Bytes Size + String
        residence = dataInputStream.readUTF();
        // Citizenship - 2 Bytes Size + String
        citizenship = dataInputStream.readUTF();
        // Status - 2 Bytes Size + String
        status = dataInputStream.readUTF();
        // Children - Int
        children = dataInputStream.readInt();
        // Tamanho Education - Int
        int tamanhoEducation = dataInputStream.readInt();
        // Education - 2 Bytes Size + String !!!
        for (int i = 0; i < tamanhoEducation; i++) {
            education.add(dataInputStream.readUTF());
        }
        // Self Made - Boolean (1 Byte)
        selfMade = dataInputStream.readBoolean();
        // Birthdate - Long (8 Bytes)
        birthdate = Instant.ofEpochSecond(dataInputStream.readLong()).atZone(ZoneId.systemDefault()).toLocalDate();

    }

}
