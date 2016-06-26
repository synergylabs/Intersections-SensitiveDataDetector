package capstone.sdd.gui;

import javax.swing.*;
import java.util.List;

/**
 * Created by lieyongzou on 6/26/16.
 */
public class ResultPanel extends JDialog {

    private String data;
    private List<List<String>> detailed_dict;

    public ResultPanel(String data, List<List<String>> detailed_dict) {
        this.data = data;
        this.detailed_dict = detailed_dict;

        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        for (List<String> pair : detailed_dict) {
            this.add(new JLabel(pair.get(0)));
            this.add(new JLabel(pair.get(1)));
        }

        pack();

    }
}
