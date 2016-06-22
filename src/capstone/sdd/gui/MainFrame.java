package capstone.sdd.gui;

import capstone.sdd.core.ScanWorker;
import capstone.sdd.core.Settings;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lieyongzou on 6/16/16.
 * A class of main frame containing the buttons, and results
 */
public class MainFrame {

    private final static String TITLE = "Sensitive Data Detector";

    private JFrame frame;
    private GuiListener listener;
    private Settings settings = Settings.getInstance();

    // A map contains <Type of PII> -- <The corresponding results panel>
    private Map<String, ResultTree> results = new HashMap<>();

    // Executor pool
    private final int POOL_SIZE = Runtime.getRuntime().availableProcessors();
    ExecutorService pool;

    private JPanel resultPanel = new JPanel();

    public MainFrame() {
        listener = new GuiListener(this);

        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);

        // Add button panel to frame
        JPanel button_panel = new JPanel();
        button_panel.setLayout(new FlowLayout());

        // Button to start scan
        JButton scan_btm = new JButton("Scan");
        scan_btm.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pool = Executors.newFixedThreadPool(POOL_SIZE);
                pool.submit(new ScanWorker(settings.getStart_folder(), pool));
            }
        });

        // Button to stop scan
        JButton stop_btm = new JButton("Stop");
        stop_btm.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pool.shutdownNow();
            }
        });

        // Add two buttons to panel
        button_panel.add(scan_btm);
        button_panel.add(stop_btm);

        // The panel to display results
        Border border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                "Result", TitledBorder.CENTER, TitledBorder.TOP);
        resultPanel.setBorder(border);
        resultPanel.setBackground(Color.white);
//        resultPanel.setPreferredSize(new Dimension(400, 400));
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));

//        for (int i = 0; i < 100; i++) {
//            resultPanel.add(new JLabel(i + ""));
//        }
//
        JScrollPane scrollPane = new JScrollPane(resultPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Add button_panel to frame
        frame.add(button_panel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setPreferredSize(new Dimension(500, 600));
        frame.pack();
        frame.setVisible(true);
    }


    /**
     * A method to get the corresponding result panel for listener to add results
     * @param type the type of data
     * @return the result panel
     */
    public ResultTree getResultTree(String type) {

        // If no type existed in the frame, add it to results map
        if (!results.containsKey(type)) {
            ResultTree tree = new ResultTree(type);
            results.put(type, tree);
            resultPanel.add(tree.getTree());
//            resultPanel.add(new JSeparator());
        }

        return results.get(type);
    }



}
