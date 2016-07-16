package capstone.sdd.gui;

import capstone.sdd.core.Settings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lieyongzou on 7/15/16.
 */
public class PatternPanel extends JDialog{

    private static final String CellPattern = "<html>%s<br>%s</html>";
    private static final String CellRetrievePattern = "<html>(.*?)<br>(.*?)</html>";


    private DefaultListModel<String> model = new DefaultListModel<>();
    private JList<String> jList = new JList<>(model);

    private JTextField nameInput = new JTextField(20);
    private JTextField patternInput = new JTextField(20);

    private GuiListener listener;

    public PatternPanel(GuiListener listener) {
        this.listener = listener;

        this.setLayout(new FlowLayout());
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

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
        addButton.setIcon(getImage("add.png"));
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
        removeButton.setIcon(getImage("remove.png"));
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
        inputPanel.add(new JLabel("Patterns:"), c);

        c.gridx = 0;
        c.gridy = 3;
        inputPanel.add(patternInput, c);

        add(listPanel);
        add(buttonPanel);
        add(inputPanel);

        pack();

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
