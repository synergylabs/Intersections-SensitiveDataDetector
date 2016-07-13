package capstone.sdd.gui;

import capstone.sdd.core.Settings;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lieyongzou on 7/6/16.
 */
public class StatusPanel extends JPanel {

    private static final int PB_WIDTH = 390;

    private Settings settings = Settings.getInstance();

    // A map contains the type and two labels, one for total, one for finished
    private Map<String, JLabel[]> labelMap = new HashMap<>();

    // A map contains the type of file and its processed file number
    private Map<String, AtomicInteger> countMap = new HashMap<>();

    private GridBagConstraints c = new GridBagConstraints();
    private JProgressBar progressBar = new JProgressBar();
    private JLabel workLabel = new JLabel();

    public StatusPanel() {
        this.setLayout(new GridBagLayout());

        workLabel.setText("Collecting...");
        workLabel.setFont(new Font("Serif", Font.BOLD, 15));


        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(0, 10, 0, 0);
        this.add(workLabel, c);

        JLabel label = new JLabel("Total:");
        label.setFont(new Font("Serif", Font.BOLD, 15));
        c.gridx = 1;
        this.add(label, c);

        label = new JLabel("Finish:");
        label.setFont(new Font("Serif", Font.BOLD, 15));
        c.gridx = 2;
        this.add(label, c);



        for (String type : settings.getSupported_file()) {

            countMap.put(type, new AtomicInteger(0));

            JLabel totalLabel = new JLabel("0");
            JLabel finishLabel = new JLabel("0");
            labelMap.put(type, new JLabel[]{totalLabel, finishLabel});

            c.gridy += 1;
            c.gridx = 0;
            this.add(new JLabel(type + ":"), c);

            c.gridx = 1;
            this.add(totalLabel, c);

            c.gridx = 2;
            this.add(finishLabel, c);
        }

        c.gridy += 1;
        c.gridx = 0;
        c.gridwidth = 3;
        this.add(progressBar, c);
        progressBar.setStringPainted(true);

        Dimension prefSize = progressBar.getPreferredSize();
        prefSize.width = PB_WIDTH;
        progressBar.setPreferredSize(prefSize);
        progressBar.setVisible(false);


        this.setVisible(false);
    }

    /**
     * A method to add progress bar in the status panel
     */
    public void startMatch(int maxTasks) {
        workLabel.setText("Matching...");


        progressBar.setVisible(true);
        progressBar.setMaximum(maxTasks);
        progressBar.setValue(0);
    }

    /**
     * A method to increase the progress
     */
    public void incrementProgress(String type) {
        progressBar.setValue(progressBar.getValue() + 1);

        int current = countMap.get(type).incrementAndGet();
        labelMap.get(type)[1].setText(current + "");
    }


    /**
     * A method to update the number of files scanned
     * @param type the type of file
     * @param count the new number of files
     */
    public void updateFileCount(String type, int count) {
        labelMap.get(type)[0].setText(count + "");
    }


    public void resizeProgressBar() {
        Dimension dimension = progressBar.getPreferredSize();
        dimension.width = PB_WIDTH * 2;
        progressBar.setPreferredSize(dimension);
    }
}
