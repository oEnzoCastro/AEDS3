package models;

import java.time.LocalDate;
import java.util.ArrayList;

public class billionaire {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getNetWorth() {
        return netWorth;
    }

    public void setNetWorth(float netWorth) {
        this.netWorth = netWorth;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public ArrayList<String> getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source.add(source);
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public ArrayList<String> getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education.add(education);
    }

    public Boolean getSelfMade() {
        return selfMade;
    }

    public void setSelfMade(Boolean selfMade) {
        this.selfMade = selfMade;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

}
