package capstone.sdd.gui;

import capstone.sdd.parser.ParserFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
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
        new DetailWorker(type, data, detailed_dict).execute();
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
    class DetailWorker extends SwingWorker<Void, Void> {

        private String type;
        private String data;
        private List<List<String>> detailed_dict;

        public DetailWorker(String type, String data, List<List<String>> detailed_dict) {
            this.type = type;
            this.data = data;
            this.detailed_dict = detailed_dict;
        }

        @Override
        protected Void doInBackground() throws Exception {

            DetailPanel.this.removeAll();

            DetailPanel.this.setLayout(new BorderLayout());
            DetailPanel.this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(Color.white);

            JLabel title = new JLabel(type + " : " + data);
            title.setFont(new Font("Serif", Font.BOLD, 15));
            title.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel correct_btm = new JLabel(getImage("correct.png"));
            correct_btm.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    title.setForeground(Color.GREEN);
                }
            });

            JLabel wrong_btm = new JLabel(getImage("wrong.png"));
            wrong_btm.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    title.setForeground(Color.RED);
                }
            });


            buttonPanel.add(title);
            buttonPanel.add(correct_btm);
            buttonPanel.add(wrong_btm);


            DetailPanel.this.add(buttonPanel, BorderLayout.NORTH);

            // Add content
            JTextPane textPane = new JTextPane();
            textPane.setEditable(false);
            StyledDocument doc = textPane.getStyledDocument();
            DetailPanel.this.highlighter = (DefaultHighlighter) textPane.getHighlighter();

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
            DetailPanel.this.add(scrollPane, BorderLayout.CENTER);

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

            DetailPanel.this.removeAll();

            DetailPanel.this.setLayout(new BorderLayout());
            DetailPanel.this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

            JPanel buttonPanel = new JPanel();

            JLabel title = new JLabel(file.getName());
            title.setFont(new Font("Serif", Font.BOLD, 15));
            title.setHorizontalAlignment(SwingConstants.CENTER);

            buttonPanel.setBackground(Color.white);
            buttonPanel.add(title);
            buttonPanel.add(new JButton("Set Correct"));

            DetailPanel.this.add(buttonPanel, BorderLayout.NORTH);

            // Add content
            JTextPane textPane = new JTextPane();
            textPane.setEditable(false);
            StyledDocument doc = textPane.getStyledDocument();
            DetailPanel.this.highlighter = (DefaultHighlighter) textPane.getHighlighter();

            try {

                String content = ParserFactory.getContent(file);

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
            BufferedImage bufferedImage = ImageIO.read(new File(MainFrame.class.getClassLoader().getResource(name).getPath()));
            imageIcon = new ImageIcon(bufferedImage);

        } catch(Exception e) {
            e.printStackTrace();
        }

        return imageIcon;
    }

}



