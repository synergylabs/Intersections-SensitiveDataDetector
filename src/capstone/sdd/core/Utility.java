package capstone.sdd.core;

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
}
