package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;

/**
 * 
 */
public class CRUD_Hash {

    /**
     * Path to the hash database
     */
    private static String hashDBPath = "src/database/hash/hashDB.db";

    /**
     * Path to the bucket database
     */
    private static String bucketDBPath = "src/database/hash/bucketDB.db";

    /**
     * Path to the billionaire database
     */
    private static String billionairesDBPath = "src/database/hash/billionairesDB.db";

    /**
     * Path to the CSV file
     */
    private static String billionairesCSVPath = "src/database/csv/billionaires.csv";

    /**
     * Function to send all the CSV data to the hash database
     */
    public static void createAll() {

        // Delete the old files (Reset the database)
        new File(hashDBPath).delete();
        new File(bucketDBPath).delete();
        new File(billionairesDBPath).delete();

        try {

            // Create the hash database (hashDB.db + bucketDB.db)
            RandomAccessFile hashDB = new RandomAccessFile(hashDBPath, "rw");
            RandomAccessFile bucketDB = new RandomAccessFile(bucketDBPath, "rw");

            // Create reader for the CSV file
            BufferedReader bufferedReader = new BufferedReader(new FileReader(billionairesCSVPath));

            // Read the csv file size
            int lineCount = bufferedReader.lines().toArray().length;

            // Return to the beginning of the csv file
            bufferedReader.close();
            bufferedReader = new BufferedReader(new FileReader(billionairesCSVPath));

            // Skip the header
            String line = bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                // Split the line by comma
                String[] data = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                

            }

        } catch (Exception e) {
            System.err.println("Error: " + e);
        }
    }

}
