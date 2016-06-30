package capstone.sdd.gui;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Created by lieyongzou on 6/26/16.
 */
public class DetailPanel extends JPanel {

    private final static int WINDOW_WIDTH = 400;
    private final static int WINDOW_HEIGHT = 500;

    JFrame mainFrame;


    // Define the highlight style
    private DefaultHighlighter highlighter;
    private DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);

    public DetailPanel(JFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.setBackground(Color.WHITE);
    }


    /**
     * A method to display details of selected data
     * @param type the type of data
     * @param data the data
     * @param detailed_dict the detailed information of specific data
     */
    public void displayDetails(String type, String data, List<List<String>> detailed_dict) {
        this.removeAll();

        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        JPanel buttonPanel = new JPanel();

        JLabel title = new JLabel(type + " : " + data);
        title.setFont(new Font("Serif", Font.BOLD, 15));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        buttonPanel.setBackground(Color.white);
        buttonPanel.add(title);
        buttonPanel.add(new JButton("Set Correct"));

        this.add(buttonPanel, BorderLayout.NORTH);

        // Add content
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        StyledDocument doc = textPane.getStyledDocument();
        this.highlighter = (DefaultHighlighter) textPane.getHighlighter();

        try {

            for (List<String> pair : detailed_dict) {
                doc.insertString(doc.getLength(), pair.get(0) + "\n", null);
                doc.insertString(doc.getLength(), pair.get(1) + "\n\n", null);
            }


            highlightText(doc, data);

        } catch (BadLocationException e) {
            e.printStackTrace();
        }


        JScrollPane scrollPane = new JScrollPane(textPane);
        this.add(scrollPane, BorderLayout.CENTER);

        mainFrame.pack();
    }

    /**
     * A method to desplay file content in the detail panel
     * @param file the file
     * @param dataset the set of data
     */
    public void displayFile(File file, Set<String> dataset) {
        this.removeAll();

        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        JPanel buttonPanel = new JPanel();

        JLabel title = new JLabel(file.getName());
        title.setFont(new Font("Serif", Font.BOLD, 15));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        buttonPanel.setBackground(Color.white);
        buttonPanel.add(title);
        buttonPanel.add(new JButton("Set Correct"));

        this.add(buttonPanel, BorderLayout.NORTH);

        // Add content
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        StyledDocument doc = textPane.getStyledDocument();
        this.highlighter = (DefaultHighlighter) textPane.getHighlighter();

        try {

            for (String data : dataset) {
                doc.insertString(doc.getLength(), data + "\n", null);
            }

        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(textPane);
        this.add(scrollPane, BorderLayout.CENTER);

        mainFrame.pack();
    }


    /**
     * A method to highlight the target data in document
     * @param doc the document in TextPane
     * @param data the target data
     */
    public void highlightText(StyledDocument doc, String data) {
        try {

            // Highlight the data
            String content = doc.getText(0, doc.getLength());
            int dataLength = data.length();

            int index = 0;
            while ((index = content.indexOf(data, index)) != -1) {
                this.highlighter.addHighlight(index, index + dataLength, this.painter);
                index += dataLength;
            }

        } catch(BadLocationException e) {
            e.printStackTrace();
        }
    }
}
