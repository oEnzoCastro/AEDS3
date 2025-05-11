package services;

// Java
import java.io.RandomAccessFile;

/**
 * 
 */
public class CRUD_Hash {

    public static void createAll() {
        try {

            RandomAccessFile randomAccessFile = new RandomAccessFile("./database/hash/hashDB.db", "rw");

        } catch (Exception e) {
            System.err.println("Error: " + e);
        }
    }

    /**
    * 
    */
    public static void create() {
        try {

            

        } catch (Exception e) {
            System.err.println("Error: " + e);
        }
    }
}
