package capstone.sdd.gui;

import capstone.sdd.core.ScanWorker;
import capstone.sdd.core.Settings;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    // A map contains <Type of PII> -- <The corresponding results panel>
    private Map<String, ResultTree> results = new HashMap<>();

    // A map contains file and its sensitive data
    private Map<File, Set<String>> fileMap = new HashMap<>();

    // Executor pool
    private final int POOL_SIZE = Runtime.getRuntime().availableProcessors();
    ExecutorService pool;

    private JPanel resultPanel = new JPanel();
    private DetailPanel detailPanel;
    private JPanel statusPanel = new JPanel();

    private JLabel workStatus = new JLabel();
    private JLabel progressStatus = new JLabel();

    // The number of files scanned
    private AtomicInteger progressNumber = new AtomicInteger(0);

    public MainFrame() {
        listener = new GuiListener(this);

        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);

        detailPanel = new DetailPanel(frame);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        // Add button panel to frame
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);


        JLabel scan_btm = new JLabel("Start", getImage("start.png"), JLabel.CENTER);
        scan_btm.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Clear the result Panel
                statusPanel.setVisible(true);
                resultPanel.removeAll();
                frame.pack();

                pool = Executors.newFixedThreadPool(POOL_SIZE);
                pool.submit(new ScanWorker(settings.getStart_folder(), pool, listener));
            }
        });


        JLabel stop_btm = new JLabel("Stop", getImage("stop.png"), JLabel.CENTER);
        stop_btm.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                pool.shutdownNow();
                statusPanel.setVisible(false);
                progressNumber = new AtomicInteger(0);
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
        scrollPane.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // The panel to display status
        statusPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JLabel workLabel = new JLabel("Current Folder:");
        workLabel.setFont(new Font("Serif", Font.BOLD, 15));

        JLabel progressLabel = new JLabel("Scanned Files:");
        progressLabel.setFont(new Font("Serif", Font.BOLD, 15));


        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(0, 10, 0, 0);
        statusPanel.add(workLabel, c);

        c.gridy = 1;
        statusPanel.add(progressLabel, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = 2;
        statusPanel.add(progressStatus, c);

        c.gridy = 0;
        statusPanel.add(workStatus, c);

        statusPanel.setVisible(false);

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
    public void getResultTree(String type, String data, String context, File file) {

        // If no type existed in the frame, add it to results map
        if (!results.containsKey(type)) {
            ResultTree tree = new ResultTree(type, listener);
            results.put(type, tree);
            resultPanel.add(tree.getTree());
        }

        // Add result to the result tree
        results.get(type).addResult(data, context, file);

        // Add data to the file infomation
        if (!fileMap.containsKey(file)) {
            fileMap.put(file, new HashSet<>());
        }

        fileMap.get(file).add(data);
    }


    /**
     * A method to change the status label on the frame
     * @param msg the content of msg
     */
    public void changeStatus(String msg) {

        // Adjust the length of status by the width of window
        int width = frame.getWidth();
        if (width < 500) {
            if (msg.length() >= 30) {
                msg = "..." + msg.substring(msg.length() - 30);
            }
        } else {
            if (msg.length() >= 80) {
                msg = "..." + msg.substring(msg.length() - 80);
            }
        }
        workStatus.setText(msg);
    }

    /**
     * A method to add one to the number of scanned files
     */
    public void incrementFile() {
        int current = progressNumber.incrementAndGet();
        progressStatus.setText(current + "");
    }



    public DetailPanel getDetailPanel() {
        return detailPanel;
    }


    /**
     * A method to get the set of sensitive data data in the file
     * @param file the file
     * @return the set of sensitive data
     */
    public Set<String> getDataInFile(File file) {
        return fileMap.get(file);
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


}
