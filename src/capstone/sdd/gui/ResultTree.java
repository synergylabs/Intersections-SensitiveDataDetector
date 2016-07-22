package capstone.sdd.gui;

import com.sun.deploy.security.ValidationState;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lieyongzou on 6/19/16.
 * A panel contains all the result with specific sensitive data.
 * Results are categorized by type of PII and grouped by the content of PII
 * For example:
 * - SSN
 * -- 123-45-678
 * ----/user/ssn.txt
 * ----/user/ssn.pdf
 */
public class ResultTree {

    private static final String RIGHT_TEMPLATE = "<html><font color=green><b>%s</b></font></html>";
    private static final String WRONG_TEMPLATE = "<html><font color=red><b>%s</b></font></html>";

    private static final String TYPE_PATTERN = "%s (0 / 0)";
    private static final String TOTAL_PATTERN = "(.*?/ )\\d+";
    private static final String COMPLETE_PATTERN = "(.*?\\()\\d+( /.*?)$";

    // Name - root node of results
    private Map<String, DefaultMutableTreeNode> result_node_dict = new HashMap<>();

    // data - [context, filepath]
    private Map<String, List<List<String>>> detailed_result_dict = new HashMap<>();



    // The dataset contains all the data needs to be evaluate
    private Set<String> target_dataset = new HashSet<>();

    // The dataset contains all the correct data
    private Set<String> correct_dataset = new HashSet<>();


    private JTree tree;
    private DefaultTreeModel model;
    private String type;


    public ResultTree(String type, GuiListener listener) {
        this.type = type;

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(String.format(TYPE_PATTERN, type));
        tree = new JTree(root);
        tree.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        tree.setCellRenderer(new CustomizedRenderer());

        model = (DefaultTreeModel)tree.getModel();

        tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                TreePath treePath = tree.getPathForLocation(e.getX(), e.getY());

                // If user double click the sensitive data, shows the detailed information
                if (e.getClickCount() == 1) {
                    Object[] nodes = treePath.getPath();

                    if (nodes.length == 2) {

                        String data = "";
                        for (Map.Entry<String, DefaultMutableTreeNode> entry : result_node_dict.entrySet()) {
                            if (nodes[1] == entry.getValue()) {
                                data = entry.getKey();
                            }
                        }

                        listener.displayDataInfo(type, data, detailed_result_dict.get(data));
                    }

                    else if (nodes.length == 3) {
                        String path = nodes[2].toString();
                        listener.displayFileInfo(new File(path));
                    }

                }
            }
        });
    }

    /**
     * A method to test whether all the data has been evaluated
     * @return the number of data without being evaluated
     *
     */
    public int getRestDataNumber() {
        return target_dataset.size();
    }

    public Set<String> getCorrectDataset() {
        return correct_dataset;
    }

    public Map<String, List<List<String>>> getDetailedResults() {
        return detailed_result_dict;
    }

    public String getType() {
        return type;
    }

    /**
     * A method to add a category of sensitive data in result panel
     * @param data the data itself
     * @param context the context around data
     * @param file the path to the file
     */
    public void addResult(String data, String context, File file) {

        if (!result_node_dict.containsKey(data)) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(data);
            result_node_dict.put(data, node);
            ((DefaultMutableTreeNode)model.getRoot()).add(node);

            detailed_result_dict.put(data, new ArrayList<>(2));
        }

        DefaultMutableTreeNode node = result_node_dict.get(data);
        DefaultMutableTreeNode new_node = new DefaultMutableTreeNode(file.getAbsolutePath());
        node.add(new_node);
        model.reload();

        List<String> list = new ArrayList<>();
        list.add(context);
        list.add(file.getAbsolutePath());
        detailed_result_dict.get(data).add(list);

        // Add the data to the target set
        target_dataset.add(data);
        addTotalTag();

    }


    public JTree getTree() {
        return tree;
    }


    /**
     * A method to change the color of text according to the correctness of data
     * @param data the data
     * @param isCorrect true the data is correct
     *             false the data is wrong
     */
    public void setCorrectness(String data, boolean isCorrect) {
        DefaultMutableTreeNode node = result_node_dict.get(data);
        String text = isCorrect ? String.format(RIGHT_TEMPLATE, data) : String.format(WRONG_TEMPLATE, data);
        node.setUserObject(text);
        model.reload();

        // The data has been evaluated, remove it from target set
        target_dataset.remove(data);
        addCompleteTag();

        if (isCorrect) {
            correct_dataset.add(data);
        } else if (correct_dataset.contains(data)) {
            correct_dataset.remove(data);
        }
    }


    /**
     * A method to increase the number of total number of sensitive data in this type
     */
    private void addTotalTag() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        String text = root.getUserObject().toString();

        text = text.replaceAll(TOTAL_PATTERN, "$1" + result_node_dict.size());
        root.setUserObject(text);
        model.reload();
    }

    /**
     * A method to increase the number of complete number of sensitive data in this type
     */
    private void addCompleteTag() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        String text = root.getUserObject().toString();

        text = text.replaceAll(COMPLETE_PATTERN, "$1" + (result_node_dict.size() - target_dataset.size()) + "$2");
        root.setUserObject(text);
        model.reload();
    }


    class CustomizedRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            if (hasFocus) {
                this.setFont(getFont().deriveFont(Font.BOLD + Font.ITALIC));
                this.setBackgroundSelectionColor(null);
                this.setBorderSelectionColor(null);
            } else {
                this.setFont(getFont().deriveFont(Font.PLAIN));
            }

            if (!leaf) {
                this.setIcon(getImage("data.png"));
            }
            return this;
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
