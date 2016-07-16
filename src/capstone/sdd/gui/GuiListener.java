package capstone.sdd.gui;

import capstone.sdd.core.Settings;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * A class to add result in GUI, called by components in Scanner
 * Created by lieyongzou on 6/22/16.
 */
public class GuiListener {

    // The main interface of software
    private MainFrame mainFrame;
    private Settings settings = Settings.getInstance();

    public GuiListener(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }


    /**
     * A method to display details of selected data
     * @param type the type of data
     * @param data the data
     * @param detailed_dict the detailed information of specific data
     */
    public void displayDataInfo(String type, String data, List<List<String>> detailed_dict) {
        mainFrame.getDetailPanel().displayDetails(type, data, detailed_dict);
        mainFrame.getStatusPanel().resizeProgressBar();
    }


    /**
     * A method to display the preview of file on the panel
     * @param file the file
     */
    public void displayFileInfo(File file) {
        Set<String> dataset = mainFrame.getDataInFile(file);
        mainFrame.getDetailPanel().displayFile(file, dataset);
        mainFrame.getStatusPanel().resizeProgressBar();
    }

    /**
     * A method to add scanned file for the match worker to match
     * @param file the file
     */
    public void addTask(File file) {
        mainFrame.addTask(file);
    }


    /**
     * A method to add result to GUI
     * @param type the type of sensitive data
     * @param data the data itself
     * @param context the context of data
     * @param file the file
     */
    public void addResult(String type, String data, String context, File file) {
        ResultTree tree = mainFrame.getResultTree(type);

        tree.addResult(data, context, file);
        mainFrame.addDataInFile(data, file);
    }


    /**
     * A method to add pattern to settings
     * @param name the name
     * @param pattern the pattern
     */
    public void addPattern(String name, String pattern) {
        settings.addPattern(name.toUpperCase(), pattern);
    }

    /**
     * A method to remove pattern from settings
     * @param name the name
     * @param pattern the pattern
     */
    public void removePattern(String name, String pattern) {
        settings.removePattern(name, pattern);
    }

    public void finishScanTasks() {
        mainFrame.onScanFinished();
    }

    public void increaseProgress(String type) {
        mainFrame.getStatusPanel().incrementProgress(type);
    }

    public void finishMatchTasks() {
        mainFrame.onMatchFinished();
        JOptionPane.showMessageDialog(null, "Match Finished");
    }

    /**
     * A method to set the data correct or wrong
     * @param type the type of data
     * @param data the data
     * @param isCorrect true correct
     *                  false wrong
     */
    public void setCorrectness(String type, String data, boolean isCorrect) {
        ResultTree tree = mainFrame.getResultTree(type);
        tree.setCorrectness(data, isCorrect);
    }



}
