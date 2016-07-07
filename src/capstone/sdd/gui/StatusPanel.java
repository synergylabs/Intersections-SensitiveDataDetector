package capstone.sdd.gui;

import capstone.sdd.core.Settings;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lieyongzou on 7/6/16.
 */
public class StatusPanel extends JPanel {

    private Settings settings = Settings.getInstance();

    private Map<String, JLabel> labelMap = new HashMap<>();

    private GridBagConstraints c = new GridBagConstraints();
    private JProgressBar progressBar = new JProgressBar();

    public StatusPanel() {
        this.setLayout(new GridBagLayout());

        JLabel workLabel = new JLabel("Collecting...");
        workLabel.setFont(new Font("Serif", Font.BOLD, 15));


        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(0, 10, 0, 0);
        this.add(workLabel, c);

        for (String type : settings.getSupported_file()) {

            JLabel label = new JLabel("0");
            labelMap.put(type, label);

            c.gridy += 1;
            c.gridx = 0;
            this.add(new JLabel(type + ":"), c);

            c.gridx = 1;
            this.add(label, c);
        }

        c.gridy += 1;
        c.gridx = 0;
        c.gridwidth = 2;
        this.add(progressBar, c);
        progressBar.setStringPainted(true);

        Dimension prefSize = progressBar.getPreferredSize();
        prefSize.width = 380;
        progressBar.setPreferredSize(prefSize);
        progressBar.setVisible(false);


        this.setVisible(false);
    }

    /**
     * A method to add progress bar in the status panel
     */
    public void showProgressBar(int maxTasks) {
        progressBar.setVisible(true);
        progressBar.setMaximum(maxTasks);
        progressBar.setValue(0);
    }

    /**
     * A method to increase the progress
     */
    public void incrementProgress() {
        progressBar.setValue(progressBar.getValue() + 1);
    }


    public void updateFileCount(String type, int count) {
        labelMap.get(type).setText(count + "");
    }
}
