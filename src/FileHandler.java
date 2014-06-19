

/**
 * FileHandling - This is a class that creates a security layer that prevents the user from manually changing 
 * his/her high score without the program noticing. This is done by storing the hash of the high score 
 * in a separate file called hash.txt. Every time the program wants to update the highScore it verifies
 * that the stored hash is equivalent to the current high score hash. 
 *
 * @Author William Fiset, Micah Stairs
 * March 13, 2014
 **/

import java.io.*;
import java.util.*;
import java.security.*;

public class FileHandler{

    final static String hashAlgorithm = "MD5", encoding = "UTF-8";
    static int filesCreated = 0;

    // highScoreFile --> keeps track of the high score
    String highScoreFileString = "highScore";
    
    // hashFile --> stores the hash for the high score
    String hashFileString = "hash";
    
    /** Creates two new files **/
    public FileHandler() {

        highScoreFileString += Integer.toString(filesCreated) + ".txt";
        hashFileString += Integer.toString(filesCreated) + ".txt" ;

        File highScoreFile = new File(highScoreFileString);
        File hashFile = new File(hashFileString);

        if (!highScoreFile.exists())
        	writeToFile(highScoreFileString, "0");

        if (!hashFile.exists())
        	writeToFile(hashFileString, getHash(getFirstLine(highScoreFileString)));

        filesCreated++;
    }

    /** Update the high score given a new score and make sure the user hasn't tried to change the score manually **/
    public void updateHighScore(String newScore){

        // Give cheaters zero!
        if (fileHasBeenManipulated())
            writeToFile(highScoreFileString, "0");
        else
            writeToFile(highScoreFileString, newScore); 

        updateHash();

    }

    /** Gets the high score stored in the file **/
    public String getHighScore(){

        return getFirstLine(highScoreFileString);

    }

    /** Checks to see if the user tried to manually change to score **/
    public boolean fileHasBeenManipulated(){

        String highScoreText = getFirstLine(highScoreFileString),
        		highScoreTextHash = getHash(highScoreText),
        		md5FileText = getFirstLine(hashFileString);
        
        boolean hasBeenManipulated = !highScoreTextHash.equals(md5FileText);

        if (hasBeenManipulated) {
        	writeToFile(highScoreFileString, "0");
        	updateHash();
        }
        
        return hasBeenManipulated;
        
    }

    /** Writes a string to a file **/
    protected void writeToFile(String fileName, String str){

        try {
        	
            // Overrides the current file
            PrintWriter writer = new PrintWriter(fileName, encoding);
            writer.println(str);
            writer.close();

        } 
        catch (FileNotFoundException e){} 
        catch (UnsupportedEncodingException e){}

    }

    /** Updates hash.txt with the newest high score **/
    protected void updateHash() {

        String line =  getFirstLine(highScoreFileString);
        String byteHash = getHash(line);

        // Writes the hash of the high score into hash.txt
        writeToFile(hashFileString, byteHash);

    }

    /** Generates the byte hash associated with a String **/
    protected String getHash(String strLine){

        String md5byteHash = "";

        try{
            byte[] bytesOfMessage = strLine.getBytes(encoding);
            MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
            byte[] thedigest = md.digest(bytesOfMessage);

            // Turn the bytes to a String
            for (byte b : thedigest){ md5byteHash += Byte.toString(b); }
        }
        catch (UnsupportedEncodingException e){}
        catch(NoSuchAlgorithmException e){}

        return md5byteHash;
    }

    /** Gets the first line in a file **/
    protected String getFirstLine(String fileName){

        String line = null;

        try{
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            line = reader.readLine();
            reader.close();
        }
        catch (IOException e){}
        
        if(line == null)
        	line = "0";
        
        return line;
    }

}
