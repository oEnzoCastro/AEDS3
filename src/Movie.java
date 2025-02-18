import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class Movie {
    private String name;
    private LocalDate year;
    private ArrayList<String> genre;
    private Double rating;
    private String description;
    private ArrayList<String> stars;
    private Double votes;
    private int duration;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getYear() {
        return year;
    }

    public void setYear(LocalDate year) {
        this.year = year;
    }

    public ArrayList<String> getGenre() {
        return genre;
    }

    public void setGenre(ArrayList<String> genre) {
        this.genre = genre;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getStars() {
        return stars;
    }

    public void setStars(ArrayList<String> stars) {
        this.stars = stars;
    }

    public Double getVotes() {
        return votes;
    }

    public void setVotes(Double votes) {
        this.votes = votes;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Movie(String name, LocalDate year, ArrayList<String> genre, Double rating, String description, ArrayList<String> stars, Double votes,
            int duration) {
        this.name = name;
        this.year = year;
        this.genre = genre;
        this.rating = rating;
        this.description = description;
        this.stars = stars;
        this.votes = votes;
        this.duration = duration;
    } 


    public byte[] toByteArray() {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF(name);
            dataOutputStream.writeUTF(year.toString());
            dataOutputStream.writeUTF(genre.toString());
            dataOutputStream.writeDouble(rating);
            dataOutputStream.writeUTF(description);
            dataOutputStream.writeUTF(stars.toString());
            dataOutputStream.writeDouble(votes);
            dataOutputStream.writeInt(duration);

            return byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
