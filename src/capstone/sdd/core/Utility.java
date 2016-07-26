package capstone.sdd.core;

import capstone.sdd.main.Main;
import capstone.sdd.parser.Parser;
import capstone.sdd.parser.ParserFactory;

import javax.swing.*;
import java.io.*;
import java.net.URL;

/**
 * Created by lieyongzou on 7/23/16.
 */
public class Utility {

    /**
     * A method to get the extension of file
     * @param name the name of file
     * @return the extension of file
     */
    public static String getExtension(String name) {

        if (!name.contains(".")) {
            return "";
        }

        // Get the extension of file
        int index = name.lastIndexOf('.');
        String suffix = name.substring(index + 1);

        return suffix.toLowerCase();
    }

    /**
     * A method to remove all the unrelated char, like apace, dash
     * @param data the data
     * @return the pure data
     */
    public static String removeNonNumeric(String data) {
        StringBuilder res = new StringBuilder();

        for (char c : data.toCharArray()) {
            if (Character.isDigit(c)) {
                res.append(c);
            }
        }

        return res.toString();
    }


    /**
     * A method to sum up all digits in a number
     * @param num the number
     * @return the sum
     */
    public static int sumAllDigit(int num) {
        int res = 0;

        while (num != 0) {
            res += num % 10;
            num /= 10;
        }

        return res;
    }

    /**
     * A method to get the content from file
     * @param file the file
     * @return the content
     */
    public static String getContent(File file) {

        // Get the parser from factory
        Parser parser = ParserFactory.getParser(getExtension(file.getName()));
        return " " + parser.parse(file) + " ";
    }


    /**
     * A method to check whether the workid has been set
     * @return the workid
     */
    public static String getWorkId() {

        // Read the workid from file
//        File file = new File(Main.class.getClassLoader().getResource("workid.txt").getPath());

        File file = new File("./workid.txt");
        if (!file.exists() || file.isDirectory()) {
            return null;
        }

        String workid = "";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            workid = reader.readLine();

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return workid;
    }

    /**
     * A method to set the work id and write to the file
     * @param workId the work id
     * @return true: the work id has been set
     *         false: the work id has not been set
     */
    public static boolean setWorkId(String workId) {

//        File file = new File(Main.class.getClassLoader().getResource("workid.txt").getPath());
        File file = new File("./workid.txt");

        // Write the workid to file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(workId);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    public static ImageIcon getImage(String name) {
        ImageIcon imageIcon = null;
        try {
            imageIcon = new ImageIcon(Utility.class.getClassLoader().getResource(name));

        } catch(Exception e) {
            e.printStackTrace();
        }

        return imageIcon;
    }
}
