package capstone.sdd.gui;

import java.io.File;
import java.util.List;

/**
 * A class to add result in GUI, called by components in Scanner
 * Created by lieyongzou on 6/22/16.
 */
public class GuiListener {

    // The main interface of software
    private MainFrame mainFrame;

    public GuiListener(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void changeStatus(String msg) {
        mainFrame.changeStatus(msg);
    }

    /**
     * A method to add result to GUI
     * @param type the type of sensitive data
     * @param data the data itself
     * @param context the context of data
     * @param file the file
     */
    public void addResult(String type, String data, String context, File file) {
        mainFrame.getResultTree(type, data, context, file);
    }


    /**
     * A method to display details of selected data
     * @param type the type of data
     * @param data the data
     * @param detailed_dict the detailed information of specific data
     */
    public void displayDataInfo(String type, String data, List<List<String>> detailed_dict) {
        mainFrame.getDetailPanel().displayDetails(type, data, detailed_dict);
    }

}
