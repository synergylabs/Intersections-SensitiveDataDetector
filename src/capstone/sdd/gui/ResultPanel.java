package capstone.sdd.gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class ResultPanel extends JPanel {

    // Name - root node of results
    private Map<String, DefaultMutableTreeNode> categories = new HashMap<>();

    private GridBagConstraints c = new GridBagConstraints();

    public ResultPanel() {

        Border border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                "Result", TitledBorder.CENTER, TitledBorder.TOP);
        this.setBorder(border);
        this.setBackground(Color.white);
        this.setPreferredSize(new Dimension(400, 400));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//        this.setLayout(new FlowLayout(FlowLayout.LEFT));


        DefaultMutableTreeNode root = new DefaultMutableTreeNode("The Java Series");
        for (int i = 0; i < 5; i++) {
            DefaultMutableTreeNode vegetableNode = new DefaultMutableTreeNode("Vegetables");
            DefaultMutableTreeNode fruitNode = new DefaultMutableTreeNode("Fruits");

            //add the child nodes to the root node
            root.add(vegetableNode);
            root.add(fruitNode);
        }

        JTree tree = new JTree(root);
        tree.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        this.add(tree);
        this.add(new JSeparator());

//        this.add(new JTree(new DefaultMutableTreeNode("hello")));
    }

    /**
     * A method to add a category of sensitive data in result panel
     * @param catagory the category, eg: ssn
     */
    public void addCategory(String catagory) {

        if (!categories.containsKey(catagory)) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(catagory);
            JTree tree = new JTree(root);
            categories.put(catagory, root);
            this.add(tree);
        }
    }


}
