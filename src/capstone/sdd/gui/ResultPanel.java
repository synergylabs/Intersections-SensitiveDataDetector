package capstone.sdd.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.List;

/**
 * Created by lieyongzou on 6/26/16.
 */
public class ResultPanel extends JDialog {

    private String data;
    private List<List<String>> detailed_dict;

    // Define the highlight style
    private DefaultHighlighter highlighter;
    private DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);

    public ResultPanel(String type, String data, List<List<String>> detailed_dict) {
        this.data = data;
        this.detailed_dict = detailed_dict;

//        this.setTitle(data);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(400, 500));
        ((JPanel) this.getContentPane()).setBorder(new EmptyBorder(10, 5, 10, 5));

        JLabel title = new JLabel(type + " : " + data);
        title.setFont(new Font("Serif", Font.BOLD, 15));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(title, BorderLayout.NORTH);

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

            // Highlight the data
            String content = doc.getText(0, doc.getLength());
            int dataLength = data.length();

            int index = 0;
            while ((index = content.indexOf(data, index)) != -1) {
                this.highlighter.addHighlight(index, index + dataLength, this.painter);
                index += dataLength;
            }

        } catch (BadLocationException e) {
            e.printStackTrace();
        }


        JScrollPane scrollPane = new JScrollPane(textPane);
        this.add(scrollPane, BorderLayout.CENTER);

        pack();

    }
}
