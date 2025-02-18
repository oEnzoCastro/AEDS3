import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;

public class App {

    public static void main(String[] args) {

      ArrayList<String> genre = new ArrayList<String>();

      genre.add("Science Fition");
      genre.add("Space");
      ArrayList<String> stars = new ArrayList<String>();

      stars.add("Chris Wood");
      stars.add("Mark Hamill");

      LocalDate date = LocalDate.of(2004, 5, 13);

      // "Interstellar", 2004, genre, 6.5, "The war for Eternia begins again in what may be the final battle between He-Man and Skeletor. A new...", stars, 21062, 121

      Movie movie = new Movie("Interstellar", date, genre, 5.3, "The war for Eternia begins again in what may be the final battle between He-Man and Skeletor. A new...", stars, 21.026, 125);

        FileOutputStream fileOutputStream;
        DataOutputStream dataOutputStream;
        byte[] bt;

         try {
            fileOutputStream = new FileOutputStream("teste.db");
            dataOutputStream = new DataOutputStream(fileOutputStream);

            bt = movie.toByteArray();

            dataOutputStream.writeInt(bt.length);
            dataOutputStream.write(bt);

            fileOutputStream.close();
         } catch (Exception e) {
            System.err.println(e);
         }


    }
}
