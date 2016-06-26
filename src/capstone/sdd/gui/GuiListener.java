package capstone.sdd.gui;

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
     * @param path the path to the file
     */
    public void addResult(String type, String data, String context, String path) {
        ResultTree tree = mainFrame.getResultTree(type);

        tree.addResult(data, context, path);
    }


}
