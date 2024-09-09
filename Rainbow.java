package CSCI262A1;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Rainbow {

    private static Scanner input;
    private static Formatter output;

    private static void openFileInput(String filename){
        try {
            input = new Scanner(Paths.get(filename));
        }catch (IOException io) {
            System.out.println("Error while opening the file");
            // Exit the program
            System.exit(1);
        }
    }

    private static void readFileInput(List<Entry> entries, String filename) {
        try{
            while (input.hasNext()) {
                // Get each line of passwords
                String pwd = input.nextLine();
                // Add into list
                entries.add(new Entry(pwd, "", false));
            }
        }catch (NoSuchElementException ns){
            System.out.println(filename + " was not created");
            // Exit the program
            System.exit(1);
        }catch (IllegalStateException ise){
            System.out.println("Error while reading the file");
            // Exit the program
            System.exit(1);
        }

    }

    private static void openFileOutput(String filename){
        try{
            // Open file
            output = new Formatter(filename);
        }catch(SecurityException se){
            System.out.println("Write permission is denied");
            // Exit the program
            System.exit(1);
        }catch (FileNotFoundException fe){
            System.out.println("Error in opening the file");
            // Exit the program
            System.exit(1);
        }


    }

    private static void createFile(List<Entry> entries, String fileName) {
        for (Entry entry : entries){
            try {
                output.format("%s%n", entry.toString());
                output.flush();
            }catch (FormatterClosedException fc){
                System.out.println("Error in writing to file");
                // Exit the program
                System.exit(1);
            }
        }
    }

    private static void closeFile(String filename){
        if (input != null){
            // Close file
            input.close();
        }
    }

    private static String getMD5(String input)
    {
        try {
            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            // of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static int reduction(String hash, int size){
        BigInteger bi = new BigInteger(hash, 16);
        // r = md5(hash) % size
        BigInteger reduction = bi.mod(BigInteger.valueOf(size));

        return reduction.intValue();
    }



    private static void generateRainbowTable(List<Entry> entries, List<Entry> rainbow_table){
        String file = "Passwords.txt";

        // Make a list of Entry,
        // where each entry contain:
        // Password, Hashed Password,
        // and boolean the password has been marked
        openFileInput(file);
        // Read the list of possible passwords
        readFileInput(entries, file);
        closeFile(file);


        for (Entry en : entries){
            // For each unused word W
            if (!en.getHasUsed()){
                String[][] hash_list = new String[7][2];

                // Mark as used
                en.setHasUsed(true);

                // Apply hash function H to the word W to produce hash value H(W)
                String current_pwd = en.getPassword();
                String current_hash = getMD5(current_pwd);
                // Set hash value into the current entry
                en.setHashPassword(current_hash);

                // Put into hashlist
                hash_list[0][0] = current_pwd;
                hash_list[0][1] = current_hash;



                // Apply 6 reduction function times
                // 1 (from first step num 2a & 2b) + 5 more times
                for (int i = 1; i < hash_list.length; i++){
                    // Apply reduction function to the current hash
                    int reduction = reduction(current_hash, entries.size());

                    // Get the entry from the result of reduction
                    // Mark as used
                    entries.get(reduction).setHasUsed(true);

                    // Hash current password
                    current_pwd = entries.get(reduction).getPassword();
                    current_hash = getMD5(current_pwd);
                    // Set hashed password
                    entries.get(reduction).setHashPassword(current_hash);

                    // Put the result into hash list
                    hash_list[i][0] = current_pwd;
                    hash_list[i][1] = current_hash;

                }



                // Add to rainbow table
                rainbow_table.add(new Entry(hash_list[0][0], hash_list[6][1], false));
            }

        }

        // Sort based on hash values
        Comparator<Entry> compare = Comparator.comparing(Entry::getHashPassword);
        rainbow_table.sort(compare);


        // Store into text file
        file = "Rainbow.txt";

        openFileOutput(file);
        createFile(rainbow_table, file);
        closeFile(file);


    }

    private static boolean checkInRainbow(String hash, List<Entry> rainbow_table, List<String> sameHash){
        boolean inRainbow = false;
        for (Entry r : rainbow_table){
            if (r.getHashPassword().equals(hash)){
                inRainbow = true;
                sameHash.add(r.getPassword());
            }
        }

        return inRainbow;
    }


    private static String findPreImage(String pwdHash, List<Entry> entries, List<Entry> rainbow_table){
        boolean inRainbow = false;
        boolean isFound = false;
        int reduceCounter = 0;
        String tempHash = pwdHash;
        String found_hash;
        String pwd;
        int found_ent;
        int ent;
        List<String> sameHash = new ArrayList<>();

        // Keep looping until a match is found
        // or until all possible passwords is checked but no match
        while (!inRainbow) {
            // Check whether the hash password in rainbow table
            inRainbow = checkInRainbow(tempHash, rainbow_table, sameHash);

            // If yes,
            // Apply reduction function to the password of the matching hash value in the rainbow table
            if (inRainbow){

                for (String s : sameHash) {
                    // Check for each password in the rainbow table
                    // that has the same hash in rainbow table

                    pwd=s;
                    found_hash = getMD5(pwd);


                    // from r = 1 to r = 7
                    // first iteration is to check previous "found_hash"
                    for (int r = 1; r <= 7; r++) {

                        // If the hash value matches the user input
                        // Password is found
                        if (found_hash.equals(pwdHash)) {
                            return pwd;
                        }

                        // Apply reduction function 6 times
                        // ( same as when generating the rainbow table)
                        // If not, apply reduction function again
                        found_ent = reduction(found_hash, entries.size());
                        pwd = entries.get(found_ent).getPassword();
                        entries.get(found_ent).setHashPassword(getMD5(pwd));
                        found_hash = entries.get(found_ent).getHashPassword();
                    }

                    // If not found, check for the other password
                    // that has the same hash value in the rainbow table


                }

                // Result still not found
                // Apply reduction function again (go back)
                if (!isFound){
                    sameHash.clear();
                    inRainbow = false;
                }

            }


            // If not,
            // Apply reduction function and get the reduction value
            // Then, look for the password from the reduction value
            ent = reduction(tempHash, entries.size());
            // increase reduction counter
            reduceCounter++;

            // If still not found and has reach more than the size of rainbow table,
            // Stop searching in rainbow table
            if (reduceCounter > entries.size()) {
                System.out.println("Could not find password pre-image");
                return "";
            }


            // Then, hash the password
            entries.get(ent).setHashPassword(getMD5(entries.get(ent).getPassword()));
            tempHash = entries.get(ent).getHashPassword();


        }
        //If code reaches this line,
        //meaning that the pre-image is not found
        System.out.println("Pre image not found");
        return "";

}







    public static void main (String[] args){
        List<Entry> entries = new ArrayList<>();
        List<Entry> rainbow_table = new ArrayList<>();

        // Generate Rainbow table
        generateRainbowTable(entries, rainbow_table);

        // Report on the number of words read in
        System.out.printf("The number of words read in is %d%n", entries.size());

        // Display number of passwords in rainbow table
        System.out.printf("The number of password in rainbow table is %d%n", rainbow_table.size());

        input = new Scanner(System.in);
        System.out.print("Enter password hash: ");
        String pwdHash = input.nextLine().trim();
        if (pwdHash.length() != 32){
            System.out.println("Please enter an appropriate hash value!");
        }else{
            String result = findPreImage(pwdHash, entries, rainbow_table);

            if (!result.equals("")){
                System.out.println("The pre image is " + result);
            }


        }




    }

}

class Entry{
    private String password;
    private String hashPassword;
    private boolean hasUsed;

    public Entry(){
        this.password ="";
        this.hashPassword ="";
        this.hasUsed = false;
    }
    public Entry(String password, String hashPassword, boolean hasUsed){
        this.password = password;
        this.hashPassword = hashPassword;
        this.hasUsed = hasUsed;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }
    public void setHasUsed(boolean hasUsed) {
        this.hasUsed = hasUsed;
    }

    public String getPassword() {
        return password;
    }
    public String getHashPassword() {
        return hashPassword;
    }
    public boolean getHasUsed(){
        return hasUsed;
    }

    @Override
    public String toString(){
        return String.format("%-20s %s", getPassword(), getHashPassword());
    }
}
