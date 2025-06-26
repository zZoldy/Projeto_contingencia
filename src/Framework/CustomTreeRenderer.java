/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Framework;

import model.NodeTree;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Z D K
 */
public class CustomTreeRenderer extends DefaultTreeCellRenderer {

    private Icon pastaIcon = UIManager.getIcon("FileView.directoryIcon");
    private Icon arquivoIcon = UIManager.getIcon("FileView.fileIcon");

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (value instanceof DefaultMutableTreeNode) {
            Object obj = ((DefaultMutableTreeNode) value).getUserObject();
            if (obj instanceof NodeTree) {
                NodeTree node = (NodeTree) obj;
                if (node.isArquivo()) {
                    setIcon(arquivoIcon); // ícone de arquivo
                    setText(node.getNome()); // emoji de arquivo
                } else {
                    setIcon(pastaIcon); // ícone de pasta
                    setText(node.getNome()); // emoji de pasta
                }
            }
        }

        return this;
    }
}
