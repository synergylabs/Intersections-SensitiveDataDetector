package capstone.sdd.gui;

import capstone.sdd.core.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by lieyongzou on 6/16/16.
 * A class of main frame containing the buttons, and results
 */
public class MainFrame {

    private final static String TITLE = "TScanner";
    private final static int WINDOW_WIDTH = 450;
    private final static int WINDOW_HEIGHT = 500;

    private String id = "TestCMU";

    private JFrame frame;
    private GuiListener listener;
    private Settings settings = Settings.getInstance();

    // A task map contains the type of file and a set of files
    private Map<String, Set<File>> tasks;

    // A map contains <Type of PII> -- <The corresponding results panel>
    private Map<String, ResultTree> results;

    // A map contains file and its sensitive data
    private Map<File, Set<String>> fileMap;

    // Executor pool
    private CompletionExecutor pool;

    private JPanel resultPanel = new JPanel();
    private JPanel buttonPanel = new JPanel();
    private DetailPanel detailPanel;
    private StatusPanel statusPanel = new StatusPanel();


    JButton scan_btm = new JButton("Scan");
    JButton stop_btm = new JButton("Stop");
    JButton add_btm = new JButton("Settings");
    JButton report_btm = new JButton("Report");

    // The time of some specific operation
    long scanStartTime = 0;
    long scanStopTime = 0;
    long matchStopTime = 0;
    long evalStartTime = 0;
    long evalStopTime = 0;


    public MainFrame() {

        // Ask for the random code from MTurk


        // init task map
        listener = new GuiListener(this);

        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);

        detailPanel = new DetailPanel(frame, listener);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        // Add button panel to frame
        buttonPanel.setLayout(new GridLayout(1, 4));
        buttonPanel.setBackground(Color.WHITE);

        scan_btm.setFocusPainted(false);
        scan_btm.setIcon(getImage("start.png"));
        scan_btm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Remove the old status panel and add new panel
                frame.remove(statusPanel);
                statusPanel = new StatusPanel();
                frame.add(statusPanel, BorderLayout.SOUTH);

                statusPanel.setVisible(true);
                detailPanel.setVisible(false);
                resultPanel.removeAll();

                buttonPanel.remove(report_btm);
                add_btm.setEnabled(false);
                scan_btm.setEnabled(false);

                // init all data set
                tasks = new ConcurrentHashMap<>();
                results = new ConcurrentHashMap<>();
                fileMap = new ConcurrentHashMap<>();

                // Set start time
                scanStartTime = System.currentTimeMillis();

                for (String type : settings.getSupported_file()) {
                    tasks.put(type, new HashSet<File>());
                }

                frame.pack();

                pool = new CompletionExecutor(listener);
                pool.setMode(0);

                for (File folder : settings.getStart_folder()) {
                    pool.submit(new ScanWorker(folder, pool, listener));
                }

            }
        });



        stop_btm.setFocusPainted(false);
        stop_btm.setIcon(getImage("stop.png"));
        stop_btm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pool.shutdownNow();
                add_btm.setEnabled(true);
                scan_btm.setEnabled(true);
            }
        });

        add_btm.setFocusPainted(false);
        add_btm.setIcon(getImage("setting.png"));
        add_btm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingPanel panel = new SettingPanel(listener);
                panel.setVisible(true);
            }
        });

        report_btm.setIcon(getImage("report.png"));
        report_btm.setFocusPainted(false);
        report_btm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GroupWorker().execute();

            }
        });

        // Add two buttons to panel
        buttonPanel.add(scan_btm);
        buttonPanel.add(stop_btm);
        buttonPanel.add(add_btm);

        // The panel to display results
        resultPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        resultPanel.setBackground(Color.white);
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.PAGE_AXIS));


        JScrollPane scrollPane = new JScrollPane(resultPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);


        frame.add(mainPanel, BorderLayout.LINE_START);
        frame.add(detailPanel, BorderLayout.LINE_END);
        frame.add(statusPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }


    /**
     * A method to get the corresponding result panel for listener to add results
     * @param type the type of data
     */
    public synchronized ResultTree getResultTree(String type) {

        // If no type existed in the frame, add it to results map
        if (!results.containsKey(type)) {
            System.out.println(type);
            ResultTree tree = new ResultTree(type, listener);
            results.put(type, tree);
            resultPanel.add(tree.getTree());
        }

        return results.get(type);
    }

    /**
     * A method to add sensitive data to file map
     * @param data the data
     * @param file the file
     */
    public void addDataInFile(String data, File file) {

        // Add data to the file information
        fileMap.putIfAbsent(file, new HashSet<>());
        fileMap.get(file).add(data);
    }


    /**
     * A method to add task to the task map
     * @param file the task
     */
    public void addTask(File file) {
        // Get the extension of file
        int index = file.getName().lastIndexOf('.');
        String suffix = file.getName().substring(index + 1);

        tasks.get(suffix).add(file);
        statusPanel.updateFileCount(suffix, tasks.get(suffix).size());
    }


    public DetailPanel getDetailPanel() {
        return detailPanel;
    }

    public StatusPanel getStatusPanel() {
        return statusPanel;
    }


    /**
     * A method to get the set of sensitive data data in the file
     * @param file the file
     * @return the set of sensitive data
     */
    public Set<String> getDataInFile(File file) {
        return fileMap.get(file);
    }


    public void onScanFinished() {
        scanStopTime = System.currentTimeMillis();
        new MatchSwingWorker().execute();
    }

    /**
     * A method called when all the files have been matched
     */
    public void onMatchFinished() {

        // Get the time when matching all files
        matchStopTime = System.currentTimeMillis();

        buttonPanel.add(report_btm);
        buttonPanel.revalidate();

        scan_btm.setEnabled(true);
        add_btm.setEnabled(true);
    }

    /**
     * A method called when evaluating data, but start time will only be set when first called
     */
    public void onEvalStarted() {
        if (evalStartTime == 0) {
            evalStartTime = System.currentTimeMillis();
        }
    }

    /**
     * A method to get image icon from file
     * @param name the name of image
     * @return the image icon
     */
    private ImageIcon getImage(String name) {
        ImageIcon imageIcon = null;
        try {
            imageIcon = new ImageIcon(this.getClass().getClassLoader().getResource(name));

        } catch(Exception e) {
            e.printStackTrace();
        }

        return imageIcon;
    }

    class MatchSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {

            int totalTask = 0;
            for (Set<File> files : tasks.values()) {
                totalTask += files.size();
            }

            statusPanel.startMatch(totalTask);
            pool.setMode(1);

            for (Set<File> files : tasks.values()) {
                for (File file : files) {
                    pool.submit(new MatchWorker(file, listener));
                }
            }

            return null;
        }
    }


    class GroupWorker extends SwingWorker<Void, Void> {

        // The path to store the report

        @Override
        protected Void doInBackground() throws Exception {

            Map<String, Map<String, List<List<String>>>> detailedDatasetMap = new HashMap<>();
            Map<String, Set<String>> correctDatasetMap = new HashMap<>();

            for (ResultTree tree : results.values()) {
                int num = tree.getRestDataNumber();
                if (num == 0) {
                    String datatype = tree.getType();
                    detailedDatasetMap.put(datatype, tree.getDetailedResults());
                    correctDatasetMap.put(datatype, tree.getCorrectDataset());
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Sorry, there are still " + num + " data in " + tree.getType() + " needs to be evaluated.");
                    return null;
                }

            }

            String path = "";
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showSaveDialog(frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                path = fc.getSelectedFile().getAbsolutePath();


                MainFrame.this.evalStopTime = System.currentTimeMillis();

                pool.setMode(2);
                pool.submit(new ReportGenerator(fileMap, tasks, correctDatasetMap, detailedDatasetMap, path, listener, MainFrame.this.id,
                        MainFrame.this.scanStartTime, MainFrame.this.scanStopTime, MainFrame.this.matchStopTime, MainFrame.this.evalStartTime, MainFrame.this.evalStopTime));

            }

            System.out.println("Scanned and Matching takes " + ((MainFrame.this.matchStopTime - MainFrame.this.scanStartTime) / 1000) + " seconds.");
            System.out.println("Evalatiion of all data takes " + ((MainFrame.this.evalStopTime - MainFrame.this.evalStartTime) / 1000) + " seconds");

            return null;
        }
    }
}