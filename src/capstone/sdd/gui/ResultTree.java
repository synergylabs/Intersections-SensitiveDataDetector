package capstone.sdd.gui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
public class ResultTree {

    // Name - root node of results
    private Map<String, DefaultMutableTreeNode> result_node_dict = new HashMap<>();
    private Map<String, List<List<String>>> detailed_result_dict = new HashMap<>();

    private JTree tree;
    private DefaultTreeModel model;

    public ResultTree(String type) {

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(type);
        tree = new JTree(root);
        tree.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        model = (DefaultTreeModel)tree.getModel();

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                TreePath path = tree.getPathForLocation(e.getX(), e.getY());

                // If user double click the sensitive data, shows the detailed information
                if (e.getClickCount() == 2) {
                    Object[] nodes = path.getPath();

                    if (nodes.length == 2) {
                        String data = nodes[1].toString();
                        System.out.println(detailed_result_dict.get(data));
                        new ResultPanel(type, data, detailed_result_dict.get(data)).setVisible(true);
                    }

                }
            }
        });

    }

    /**
     * A method to add a category of sensitive data in result panel
     * @param data the data itself
     * @param context the context around data
     * @param path the path to the file
     */
    public void addResult(String data, String context, String path) {

        if (!result_node_dict.containsKey(data)) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(data);
            result_node_dict.put(data, node);
            ((DefaultMutableTreeNode)model.getRoot()).add(node);

            detailed_result_dict.put(data, new ArrayList<>(2));
        }

        DefaultMutableTreeNode node = result_node_dict.get(data);
        DefaultMutableTreeNode new_node = new DefaultMutableTreeNode(path);
        node.add(new_node);
        model.reload();

        List<String> list = new ArrayList<>();
        list.add(context);
        list.add(path);
        detailed_result_dict.get(data).add(list);

    }


    public JTree getTree() {
        return tree;
    }


}
