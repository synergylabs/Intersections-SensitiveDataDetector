package capstone.sdd.core;

/**
 * Created by lieyongzou on 7/23/16.
 */
public class Utility {

    public static String getExtension(String name) {

        // Get the extension of file
        int index = name.lastIndexOf('.');
        String suffix = name.substring(index + 1);

        return suffix.toLowerCase();
    }
}
