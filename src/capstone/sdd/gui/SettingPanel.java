package capstone.sdd.gui;

import capstone.sdd.core.Settings;
import capstone.sdd.core.Utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lieyongzou on 7/15/16.
 */
public class SettingPanel extends JDialog{

    private static final String CellPattern = "<html>%s<br>%s</html>";
    private static final String CellRetrievePattern = "<html>(.*?)<br>(.*?)</html>";


    private DefaultListModel<String> model = new DefaultListModel<>();
    private JList<String> jList = new JList<>(model);

    private JTextField nameInput = new JTextField(20);
    private JTextField patternInput = new JTextField(20);

    private GuiListener listener;
    private Settings settings = Settings.getInstance();

    public SettingPanel(GuiListener listener) {
        this.listener = listener;
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // The panal to add or remove customized pattern
        JPanel patternPanel = new JPanel();
        patternPanel.setLayout(new FlowLayout());

        JPanel listPanel = new JPanel();

        // Add customized patterns to list
        for (Map.Entry<String, Set<String>> entry : Settings.getInstance().getCustomizedPatterns().entrySet()) {
            for (String pattern : entry.getValue()) {
                model.addElement(String.format(CellPattern, entry.getKey(), pattern));
            }
        }

        JScrollPane scrollPane = new JScrollPane(jList);
        scrollPane.setPreferredSize(new Dimension(200, 200));
        listPanel.add(scrollPane);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));

        JButton addButton = new JButton();
        addButton.setIcon(Utility.getImage("add.png"));
        addButton.setFocusPainted(false);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameInput.getText();
                String pattern = patternInput.getText();

                model.addElement(String.format(CellPattern, name, pattern));
                nameInput.setText("");
                patternInput.setText("");

                // Add the pattern to settings
                listener.addPattern(name, pattern);
            }
        });

        JButton removeButton = new JButton();
        removeButton.setIcon(Utility.getImage("remove.png"));
        removeButton.setFocusPainted(false);

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (jList.getSelectedIndex() >= 0) {

                    // Retrieve the name and pattern from content
                    String content = jList.getSelectedValue();
                    Pattern pattern = Pattern.compile(CellRetrievePattern);
                    Matcher m = pattern.matcher(content);

                    if (m.find()) {
                        listener.removePattern(m.group(1), m.group(2));
                    }

                    model.removeElementAt(jList.getSelectedIndex());
                }
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);


        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        inputPanel.add(new JLabel("Name:"), c);

        c.gridx = 0;
        c.gridy = 1;
        inputPanel.add(nameInput, c);

        c.gridx = 0;
        c.gridy = 2;
        inputPanel.add(new JLabel("Data Pattern:"), c);

        c.gridx = 0;
        c.gridy = 3;
        inputPanel.add(patternInput, c);

        JLabel label = new JLabel("Please use space as delimiter.");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.ITALIC, 13));

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        inputPanel.add(label, c);

        patternPanel.add(listPanel);
        patternPanel.add(buttonPanel);
        patternPanel.add(inputPanel);

        tabbedPane.add("Patterns", patternPanel);

        // the panel to set the file size to filter
        JPanel sizePanel = new JPanel();
        sizePanel.setLayout(new GridBagLayout());

        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.gridwidth = 3;

        label = new JLabel("Please choose the file size, above which the files will be filtered out:");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        sizePanel.add(label, c);

        JRadioButton radio1 = new JRadioButton("5 MB");
        JRadioButton radio2 = new JRadioButton("10 MB");
        JRadioButton radio3 = new JRadioButton("15 MB");

        int currentFileSize = (int)settings.getFileSizeLimit() / (1024 * 1024);
        switch (currentFileSize) {
            case 5:
                radio1.setSelected(true);
                break;

            case 10:
                radio2.setSelected(true);
                break;

            case 15:
                radio3.setSelected(true);
                break;

        }

        radio1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settings.setFilesizelimit(5);
            }
        });

        radio2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settings.setFilesizelimit(10);
            }
        });

        radio3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settings.setFilesizelimit(15);
            }
        });

        ButtonGroup fileSizeGroup = new ButtonGroup();
        fileSizeGroup.add(radio1);
        fileSizeGroup.add(radio2);
        fileSizeGroup.add(radio3);

        sizePanel.add(radio1, c);
        sizePanel.add(radio2, c);
        sizePanel.add(radio3, c);

        tabbedPane.add("File Size", sizePanel);

        // The page to modify the worker id
        JPanel workerPanel = new JPanel();
        workerPanel.setLayout(new GridBagLayout());

        workerPanel.add(new JLabel("Worker ID:"));

        JTextField input = new JTextField(Utility.getWorkId(), 10);
        workerPanel.add(input);

        JButton workerButton = new JButton("OK");
        workerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String workid = input.getText();

                if (Utility.setWorkId(workid)) {
                    JOptionPane.showMessageDialog(SettingPanel.this, "The worker id has been set to \"" + workid + "\"");
                } else {
                    JOptionPane.showMessageDialog(SettingPanel.this, "The worker id is illegal");
                }
            }
        });

        workerPanel.add(workerButton);

        tabbedPane.add("worker id", workerPanel);

        this.add(tabbedPane);
        pack();

    }

}
