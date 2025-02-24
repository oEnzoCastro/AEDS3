import java.time.LocalDate;
import java.util.ArrayList;

public class Billionare {
    private String name;
    private Double netWorth;
    private String country;
    private ArrayList<String> source;
    private int rank;
    private int age;
    private String residence;
    private String status;
    private int children;
    private String education;
    private Boolean selfMade;
    private LocalDate birthdate;
    
    public Billionare(String name, Double netWorth, String country, ArrayList<String> source, int rank, int age,
            String residence, String status, int children, String education, Boolean selfMade, LocalDate birthdate) {
        this.name = name;
        this.netWorth = netWorth;
        this.country = country;
        this.source = source;
        this.rank = rank;
        this.age = age;
        this.residence = residence;
        this.status = status;
        this.children = children;
        this.education = education;
        this.selfMade = selfMade;
        this.birthdate = birthdate;
    }
}
