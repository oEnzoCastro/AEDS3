package models;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class Billionaire {
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

    public Billionaire(String name, Double netWorth, String country, ArrayList<String> source, int rank, Double age,
            String residence, String citizenship, String status, Double children, String education, Boolean selfMade,
            LocalDate birthdate) {

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

        this.name = "";
        this.netWorth = 0.0;
        this.country = "";
        this.source = null;
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
        dataOutputStream.writeUTF(""); // !!!

        return byteArrayOutputStream.toByteArray();
    }

}
