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
    private Double netWorth;
    private String country;
    private ArrayList<String> source;
    private int rank;
    private Double age;
    private String residence;
    private String citizenship;
    private String status;
    private Double children;
    private String education;
    private Boolean selfMade;
    private LocalDate birthdate;

    public String toString() {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return "\nID:" + id +
                "\nNome:" + name +
                "\nNetWorth:" + df.format(netWorth) +
                "\nCountry:" + country +
                "\nSource: " + source.toString() +
                "\nRank: " + rank +
                "\nAge: " + df.format(age) +
                "\nResidence: " + residence +
                "\nCitizenship: " + citizenship +
                "\nStatus: " + status +
                "\nChildren: " + df.format(children) +
                "\nEducation: " + education +
                "\nSelfmade: " + selfMade +
                "\nBirthdate: " + birthdate
                
                ;
    }

    public Billionaire(int id, String name, Double netWorth, String country, ArrayList<String> source, int rank,
            Double age,
            String residence, String citizenship, String status, Double children, String education, Boolean selfMade,
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
        this.netWorth = 0.0;
        this.country = "";
        this.source = new ArrayList<String>();
        this.rank = -1;
        this.age = 0.0;
        this.residence = "";
        this.citizenship = "";
        this.status = "";
        this.children = 0.0;
        this.education = "";
        this.selfMade = false;
        this.birthdate = null;
    }

    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);


        dataOutputStream.writeInt(id);
        dataOutputStream.writeUTF(name);
        dataOutputStream.writeDouble(netWorth);
        dataOutputStream.writeUTF(country);
        dataOutputStream.writeInt(source.size()); // Tamanho do array (Quantidade, não bits !Mudar para BITS!)
        for (int i = 0; i < source.size(); i++) {
            dataOutputStream.writeUTF(source.get(i)); // adiciona cada elemento do array
        }
        dataOutputStream.writeInt(rank);
        dataOutputStream.writeDouble(age);
        dataOutputStream.writeUTF(residence);
        dataOutputStream.writeUTF(citizenship);
        dataOutputStream.writeUTF(status);
        dataOutputStream.writeDouble(children);
        dataOutputStream.writeUTF(education);
        dataOutputStream.writeBoolean(selfMade);
        dataOutputStream.writeLong(birthdate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond());

        // LocalDate timestampToLocalDate =
        // Instant.ofEpochSecond(birthdate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()).atZone(ZoneId.systemDefault()).toLocalDate();

        return byteArrayOutputStream.toByteArray();
    }

    public void fromByteArray(byte[] bt) throws IOException {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bt);

        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        dataInputStream.readInt(); // Lapide

        id = dataInputStream.readInt();
        name = dataInputStream.readUTF();

        netWorth = dataInputStream.readDouble();
        country = dataInputStream.readUTF();
        int arraySize = dataInputStream.readInt();
        source.clear();
        for (int i = 0; i < arraySize; i++) {
            source.add(dataInputStream.readUTF());
        }
        rank = dataInputStream.readInt();
        age = dataInputStream.readDouble();
        residence = dataInputStream.readUTF();
        citizenship = dataInputStream.readUTF();
        status = dataInputStream.readUTF();
        children = dataInputStream.readDouble();
        education = dataInputStream.readUTF();
        selfMade = dataInputStream.readBoolean();
        birthdate = Instant.ofEpochSecond(dataInputStream.readLong()).atZone(ZoneId.systemDefault()).toLocalDate();

    }

}
