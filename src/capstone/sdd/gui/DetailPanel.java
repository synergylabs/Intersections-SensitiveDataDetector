package capstone.sdd.gui;

import capstone.sdd.core.Utility;
import capstone.sdd.parser.ParserFactory;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Created by lieyongzou on 6/26/16.
 */
public class DetailPanel extends JPanel {

    private final static int WINDOWWIDTH = 400;
    private final static int WINDOWHEIGHT = 500;
    private final static int TITLEHEIGHT = 37;

    JFrame mainFrame;
    private GuiListener listener;


    // Define the highlight style
    private DefaultHighlighter highlighter;
    private DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);

    public DetailPanel(JFrame mainFrame, GuiListener listener) {
        this.mainFrame = mainFrame;
        this.listener = listener;
        this.setBackground(Color.WHITE);
    }


    /**
     * A method to display details of selected data
     * @param type the type of data
     * @param data the data
     * @param detailed_dict the detailed information of specific data
     */
    public void displayDetails(String type, String data, List<List<String>> detailed_dict) {
        new DataWorker(type, data, detailed_dict).execute();
    }

    /**
     * A method to desplay file content in the detail panel
     * @param file the file
     * @param dataset the set of data
     */
    public void displayFile(File file, Set<String> dataset) {
        new FileWorker(file, dataset).execute();
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


    /**
     * A worker thread to display details information
     */
    class DataWorker extends SwingWorker<Void, Void> {

        private String type;
        private String data;
        private List<List<String>> detailed_dict;

        public DataWorker(String type, String data, List<List<String>> detailed_dict) {
            this.type = type;
            this.data = data;
            this.detailed_dict = detailed_dict;
        }

        @Override
        protected Void doInBackground() throws Exception {

            DetailPanel.this.setVisible(true);

            DetailPanel.this.removeAll();

            DetailPanel.this.setLayout(new BorderLayout());
            DetailPanel.this.setPreferredSize(new Dimension(WINDOWWIDTH, WINDOWHEIGHT));

            JPanel titlePanel = new JPanel();
            titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            titlePanel.setBackground(Color.white);
            titlePanel.setPreferredSize(new Dimension(WINDOWWIDTH, TITLEHEIGHT));

            JLabel title = new JLabel(type + " : " + data);
            title.setFont(new Font("Serif", Font.BOLD, 15));
            title.setHorizontalAlignment(SwingConstants.CENTER);

            titlePanel.add(title);

            DetailPanel.this.add(titlePanel, BorderLayout.NORTH);

            // Add content
            JTextPane textPane = new JTextPane();
            textPane.setEditable(false);
            StyledDocument doc = textPane.getStyledDocument();
            DetailPanel.this.highlighter = (DefaultHighlighter) textPane.getHighlighter();

            SimpleAttributeSet attributeSet = new SimpleAttributeSet();
            attributeSet.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);

            try {

                for (List<String> pair : detailed_dict) {
                    doc.insertString(doc.getLength(), "File: \n", attributeSet);
                    doc.insertString(doc.getLength(), pair.get(1) + "\n\n", null);

                    doc.insertString(doc.getLength(), "Context: \n", attributeSet);
                    doc.insertString(doc.getLength(), pair.get(0) + "\n", null);
                    doc.insertString(doc.getLength(), "______________________________________________________\n\n", null);
                }


                highlightText(doc, data);

            } catch (BadLocationException e) {
                e.printStackTrace();
            }


            JScrollPane scrollPane = new JScrollPane(textPane);
            DetailPanel.this.add(scrollPane, BorderLayout.CENTER);

            // Button to set the correctness of data
            JPanel questionPanel = new JPanel();
            questionPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black),
                    BorderFactory.createEmptyBorder(10, 0, 5, 0))
            );
            questionPanel.setLayout(new BorderLayout());
            questionPanel.setBackground(Color.white);

            JLabel questionLabel = new JLabel("Is this a " + type + "?");
            questionLabel.setFont(new Font("Serif", Font.BOLD, 15));
            questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
            questionPanel.add(questionLabel, BorderLayout.NORTH);

            JButton rightButton = new JButton("Yes");
            rightButton.setFocusPainted(false);
            rightButton.setIcon(getImage("correct.png"));
            rightButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.setCorrectness(type, data, true);
                }
            });

            JButton wrongButton = new JButton("No");
            wrongButton.setFocusPainted(false);
            wrongButton.setIcon(getImage("wrong.png"));
            wrongButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.setCorrectness(type, data, false);
                }
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(Color.white);
            buttonPanel.setLayout(new GridLayout(1, 2));
            buttonPanel.add(rightButton);
            buttonPanel.add(wrongButton);

            questionPanel.add(buttonPanel, BorderLayout.SOUTH);

            DetailPanel.this.add(questionPanel, BorderLayout.SOUTH);

            mainFrame.pack();

            return null;
        }
    }


    /**
     * A worker thread to read file and display on the panel
     */
    class FileWorker extends SwingWorker<Void, Void> {

        private File file;
        private Set<String> dataset;

        public FileWorker(File file, Set<String> dataset) {
            this.file = file;
            this.dataset = dataset;
        }


        @Override
        protected Void doInBackground() throws Exception {

            DetailPanel.this.setVisible(true);

            DetailPanel.this.removeAll();

            DetailPanel.this.setLayout(new BorderLayout());
            DetailPanel.this.setPreferredSize(new Dimension(WINDOWWIDTH, WINDOWHEIGHT));

            JPanel titlePanel = new JPanel();
            titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            titlePanel.setPreferredSize(new Dimension(WINDOWWIDTH, TITLEHEIGHT));

            JLabel title = new JLabel(file.getName());
            title.setFont(new Font("Serif", Font.BOLD, 15));
            title.setHorizontalAlignment(SwingConstants.CENTER);

            titlePanel.setBackground(Color.white);
            titlePanel.add(title);

            DetailPanel.this.add(titlePanel, BorderLayout.NORTH);

            // Add content
            JTextPane textPane = new JTextPane();
            textPane.setEditable(false);
            StyledDocument doc = textPane.getStyledDocument();
            DetailPanel.this.highlighter = (DefaultHighlighter) textPane.getHighlighter();

            try {

                String content = Utility.getContent(file);

                doc.insertString(doc.getLength(), content, null);

                // Highlight the data
                for (String data : dataset) {
                    highlightText(doc, data);
                }


            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            JScrollPane scrollPane = new JScrollPane(textPane);
            DetailPanel.this.add(scrollPane, BorderLayout.CENTER);

            mainFrame.pack();

            return null;
        }
    }

    /**
     * A method to get image icon from file
     * @param name the name of image
     * @return the image icon
     */
    private ImageIcon getImage(String name) {
        ImageIcon imageIcon = null;
        try {
            imageIcon = new ImageIcon(this.getClass().getClassLoader().getResource(name));

        } catch(Exception e) {
            e.printStackTrace();
        }

        return imageIcon;
    }

}



