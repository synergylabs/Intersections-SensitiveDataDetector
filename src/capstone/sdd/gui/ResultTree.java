package capstone.sdd.gui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.HashMap;
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
public class ResultTree {

    // Name - root node of results
    private Map<String, DefaultMutableTreeNode> categories = new HashMap<>();

    private JTree tree;

    public ResultTree(String type) {

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(type);
        for (int i = 0; i < 5; i++) {
            DefaultMutableTreeNode vegetableNode = new DefaultMutableTreeNode("Vegetables");
            DefaultMutableTreeNode fruitNode = new DefaultMutableTreeNode("Fruits");

            //add the child nodes to the root node
            root.add(vegetableNode);
            root.add(fruitNode);
        }

        tree = new JTree(root);
        tree.setAlignmentX(JComponent.LEFT_ALIGNMENT);

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
        }
    }

    public JTree getTree() {
        return tree;
    }


}
