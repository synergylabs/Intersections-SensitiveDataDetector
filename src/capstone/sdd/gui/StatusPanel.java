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

    public StatusPanel() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

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

        this.setVisible(false);
    }


    public void updateFileCount(String type, int count) {
        labelMap.get(type).setText(count + "");
    }
}
