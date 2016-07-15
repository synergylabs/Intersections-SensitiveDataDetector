package capstone.sdd.gui;

import capstone.sdd.core.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lieyongzou on 6/16/16.
 * A class of main frame containing the buttons, and results
 */
public class MainFrame {

    private final static String TITLE = "Sensitive Data Detector";
    private final static int WINDOW_WIDTH = 400;
    private final static int WINDOW_HEIGHT = 500;

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

    public MainFrame() {

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
        buttonPanel.setLayout(new GridLayout(1, 3));
        buttonPanel.setBackground(Color.WHITE);


        JButton scan_btm = new JButton("Start");
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

                // init all data set
                tasks = new HashMap<>();
                results = new ConcurrentHashMap<>();
                fileMap = new HashMap<>();

                for (String type : settings.getSupported_file()) {
                    tasks.put(type, new HashSet<File>());
                }

                frame.pack();

                pool = new CompletionExecutor(listener);
                pool.setMode(0);
                pool.submit(new ScanWorker(settings.getStart_folder(), pool, listener));
            }
        });


        JButton stop_btm = new JButton("Stop");
        stop_btm.setFocusPainted(false);
        stop_btm.setIcon(getImage("stop.png"));
        stop_btm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pool.shutdownNow();
            }
        });

        // Add two buttons to panel
        buttonPanel.add(scan_btm);
        buttonPanel.add(stop_btm);

        // The panel to display results
        Border border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                "Result", TitledBorder.CENTER, TitledBorder.TOP);
        resultPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        resultPanel.setBackground(Color.white);
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.PAGE_AXIS));


        JScrollPane scrollPane = new JScrollPane(resultPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // The panel to display status


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
    public ResultTree getResultTree(String type) {

        // If no type existed in the frame, add it to results map
        if (!results.containsKey(type)) {
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
        if (!fileMap.containsKey(file)) {
            fileMap.put(file, new HashSet<>());
        }

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
        new MatchSwingWorker().execute();
    }

    public void onMatchFinished() {
        JButton report_btm = new JButton("Generate Report");
        report_btm.setIcon(getImage("report.png"));
        report_btm.setFocusPainted(false);
        report_btm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GroupWorker().execute();
            }
        });

        buttonPanel.add(report_btm);
        buttonPanel.revalidate();
    }

    /**
     * A method to get image icon from file
     * @param name the name of image
     * @return the image icon
     */
    private ImageIcon getImage(String name) {
        ImageIcon imageIcon = null;

        try {
            BufferedImage bufferedImage = ImageIO.read(new File(MainFrame.class.getClassLoader().getResource(name).getPath()));
            imageIcon = new ImageIcon(bufferedImage);

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

            pool.setMode(2);
            pool.submit(new ReportGenerator(fileMap, tasks, correctDatasetMap, detailedDatasetMap));
            return null;
        }
    }
}
