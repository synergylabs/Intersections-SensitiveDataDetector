package capstone.sdd.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by lieyongzou on 7/2/16.
 */
public class ResultPanel extends JPanel {

    // The type of results
    private String type;

    // The constraints for the data item
    private GridBagConstraints dataConstraints = new GridBagConstraints();

    // The constraints for the file item
    private GridBagConstraints fileConstraints = new GridBagConstraints();

    public ResultPanel(String type) {

        this.setLayout(new GridBagLayout());

        JLabel dataType = new JLabel(type);
        dataType.setFont(new Font("Serif", Font.BOLD, 10));

        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        this.add(dataType, c);

//        c.insets = new Insets(0, 20, 0, 0);
        c.gridy += 1;
        this.add(new JLabel("12345"), c);

    }

}
