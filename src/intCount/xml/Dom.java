package intCount.xml;

import intCount.security.EncryptedTextToFile;


import javafx.application.Platform;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.Random;

public class Dom {

    public int count;
    public int CountMinus;
    public int customer;
    public long experitionEpochTime;
    byte[] IV;
    byte[] bytes;
    EncryptedTextToFile encryptedTextToFile = new EncryptedTextToFile();
    String key = "Mary has one 123";

    FileOutputStream output = null;
    FileInputStream input = null;
    FileInputStream fileInput = null;

    public Dom() {

        this.count = getCount();
        this.customer = getCustomer();
        this.CountMinus = getCountminus();
        this.experitionEpochTime = getExperitionEpochTime();

    }

    public int getCount() {
        return count;
    }

    public int getCountminus() {
        return CountMinus;
    }

    public int getCustomer() {
        return customer;
    }

    public long getExperitionEpochTime() {
        return experitionEpochTime;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setCountminus(int Minus) {
        this.CountMinus = Minus;
    }

    public void setCustomer(int customer) {
        this.customer = customer;
    }

    public void setExperitionEpochTime(long experitionEpochTime) {
        this.experitionEpochTime = experitionEpochTime;
    }

    // Main method
    public void writeDatFile() {

        GenerateExtraLines();

    }

    public void readDatFile() {

        String encr = encryptedTextToFile.ReadEncryptedTextFromFile("[5;'x)7F|(M^.168HC|4#$,;'c1J.$~d+",
                System.getProperty("user.dir").concat("/resources").concat("/intCount").concat("/Datfile.dat"));

        String[] lines = encr.split("count");
        String[] nest = lines[6].split("-");

        System.out.println(nest[1] + nest[3] + nest[5] + nest[7] + nest[9]);

        // Close our input stream fileInput.close();
        setCount(Integer.valueOf(nest[1]));
        setCountminus(Integer.valueOf(nest[3]));
        setCustomer(Integer.valueOf(nest[5]));
        setExperitionEpochTime(Long.valueOf(nest[7]));

    }

    public String readNaroto() {

        String encr = encryptedTextToFile.ReadEncryptedTextFromFile("[5;'x)7F|(M^.168HC|4#$,;'c1J.$~d+",
                System.getProperty("user.dir").concat("/resources").concat("/intCount").concat("/Datfile.dat"));
        String[] lines = encr.split("count");
        String[] nest = lines[6].split("-");

        return nest[9];

    }

    public String readDFile() {

        String encr = encryptedTextToFile.ReadEncryptedTextFromFile(">3)@C0~6~;[y@,p!gkI~6,1#)6E1|n#",
                System.getProperty("user.dir").concat("/resources").concat("/intCount").concat("/Dfile.dat"));

        //System.out.println(encr);
        String[] lines = encr.split("/");
        //System.out.println(lines[1]);
        //System.out.println(lines[2]);


        return lines[1];

    }

    public String readNFile() {

        String encr = encryptedTextToFile.ReadEncryptedTextFromFile(">3)@C0~6~;[y@,p!gkI~6,1#)6E1|n#",
                System.getProperty("user.dir").concat("/resources").concat("/intCount").concat("/Dfile.dat"));

        String[] lines = encr.split("/");

        return lines[0];

    }

    public String[] parse(String line, String s1) {

        String[] rslt = line.split(s1);

        return rslt;
    }

    public void Check() {

        String[] sarout = {"ckbkl4ZqriiFpJKccVe7LFokB30UcriX", "YdNlFuxFQTaSpnkkuVBwXmF0JQbqMmXa"};
        String nextSarout = null;

        if (count >= 30) {

            for (int x = 0; x < sarout.length; x++) {

                if (readNaroto().equals(sarout[x])) {
                    nextSarout = sarout[x + 1];
                }
                //System.out.println(nextSarout);
            }

            if (readDFile().equals(nextSarout) && readNFile().equals("intCount")) {

                setCount(0);
                setCountminus(0);
                write();
                Platform.exit();

            } else {
                //System.out.println("readFile" + readDFile());
                Platform.exit();
            }
        }

    }

    public void GenerateExtraLines() {

        // Print a line of Text
        encryptedTextToFile.WriteEncryptedTextToFile("count" + "-" + "?" + "-" + generateJavaCode(3) + "Minus" + "-"
                        + "?" + "-" + generateJavaCode(3) + "customer" + "-" + "?" + "-" + "experitionEpochTime" + "-" + "?"
                        + "-" + "Naroto" + "-" + "?" + "-" + "\r\n" + "count" + "-" + "?" + "-" + generateJavaCode(3) + "Minus"
                        + "-" + "?" + "-" + generateJavaCode(3) + "customer" + "-" + "?" + "-" + "experitionEpochTime" + "-"
                        + "?" + "-" + "Naroto" + "-" + "?" + "-" + "\r\n" + "count" + "-" + "?" + "-" + generateJavaCode(3)
                        + "Minus" + "-" + "?" + "-" + generateJavaCode(3) + "customer" + "-" + "?" + "-" + "experitionEpochTime"
                        + "-" + "?" + "-" + "Naroto" + "-" + "?" + "-" + "\r\n" + "count" + "-" + "?" + "-"
                        + generateJavaCode(3) + "Minus" + "-" + "?" + "-" + generateJavaCode(3) + "customer" + "-" + "?" + "-"
                        + "experitionEpochTime" + "-" + "?" + "-" + "Naroto" + "-" + "?" + "-" + "\r\n" + "count" + "-" + "?"
                        + "-" + generateJavaCode(3) + "Minus" + "-" + "?" + "-" + generateJavaCode(3) + "customer" + "-" + "?"
                        + "-" + "experitionEpochTime" + "-" + "?" + "-" + "Naroto" + "-" + "?" + "-" + "\r\n" + "count" + "-"
                        + getCount() + "-" + generateJavaCode(3) + "Minus" + "-" + getCountminus() + "-" + generateJavaCode(3)
                        + "customer" + "-" + getCustomer() + "-" + "experitionEpochTime" + "-" + getExperitionEpochTime() + "-"
                        + "Naroto" + "-" + readDFile() + "-" + "\r\n" + "count" + "-" + "?" + "-" + generateJavaCode(3)
                        + "Minus" + "-" + "?" + "-" + generateJavaCode(3) + "customer" + "-" + "?" + "-" + "experitionEpochTime"
                        + "-" + "?" + "-" + "Naroto" + "-" + "?" + "-" + "\r\n" + "count" + "-" + "?" + "-"
                        + generateJavaCode(3) + "Minus" + "-" + "?" + "-" + generateJavaCode(3) + "customer" + "-" + "?" + "-"
                        + "experitionEpochTime" + "-" + "?" + "-" + "Naroto" + "-" + "?" + "-" + "\r\n" + "count" + "-" + "?"
                        + "-" + generateJavaCode(3) + "Minus" + "-" + "?" + "-" + generateJavaCode(3) + "customer" + "-" + "?"
                        + "-" + "experitionEpochTime" + "-" + "?" + "-" + "Naroto" + "-" + "?" + "-" + "\r\n" + "count" + "-"
                        + "?" + "-" + generateJavaCode(3) + "Minus" + "-" + "?" + "-" + generateJavaCode(3) + "customer" + "-"
                        + "?" + "-" + "experitionEpochTime" + "-" + "?" + "-" + "Naroto" + "-" + "?" + "-" + "\r\n" + "count"
                        + "-" + "?" + "-" + generateJavaCode(3) + "Minus" + "-" + "?" + "-" + generateJavaCode(3) + "customer"
                        + "-" + "?" + "-" + "experitionEpochTime" + "-" + "?" + "-" + "Naroto" + "-" + "?" + "-" + "\r\n",
                "[5;'x)7F|(M^.168HC|4#$,;'c1J.$~d+", System.getProperty("user.dir").concat("/resources").concat("/intCount").concat("/Datfile.dat"));

    }


    public void Generate() {

        encryptedTextToFile.WriteEncryptedTextToFile("Name:" + "\r\n" + "Number:", ">3)@C0~6~;[y@,p!gkI~6,1#)6E1|n#",
                System.getProperty("user.dir").concat("/resources").concat("/intCount").concat("/Dfile.dat"));
    }

    public String generateJavaCode(int n) {

        // length is bounded by 256 Character
        byte[] array = new byte[256];
        new Random().nextBytes(array);

        String randomString = new String(array, Charset.forName("UTF-8"));

        // Create a StringBuffer to store the result
        StringBuffer r = new StringBuffer();

        // remove all spacial char
        String AlphaNumericString = randomString.replaceAll("[^A-Za-z0-9--]", "");

        // Append first 20 alphanumeric characters
        // from the generated random String into the result
        for (int k = 0; k < AlphaNumericString.length(); k++) {

            if (Character.isLetter(AlphaNumericString.charAt(k)) && (n > 0)
                    || Character.isDigit(AlphaNumericString.charAt(k)) && (n > 0)) {

                r.append(AlphaNumericString.charAt(k));
                n--;
            }
        }

        // return the resultant string
        return r.toString();
    }

    // Runtime rt = Runtime.getRuntime();
    // Process pr = rt.exec("jar uf Facturation.jar" + filepath);

    public void write() {

        writeDatFile();
    }
}
