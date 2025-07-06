/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Framework;

import java.awt.Color;
import model.NodeTree;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Z D K
 */
public class CustomTreeRenderer extends DefaultTreeCellRenderer {

    private final Icon pastaIcon = new ImageIcon(getClass().getResource("/icon/pasta.png"));
    private final Icon arquivoIcon = new ImageIcon(getClass().getResource("/icon/arquivo.png"));

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (pastaIcon == null || arquivoIcon == null) {
            System.err.println("Erro ao carregar os ícones!");
        }

        if (value instanceof DefaultMutableTreeNode defaultMutableTreeNode) {
            Object obj = defaultMutableTreeNode.getUserObject();
            if (obj instanceof NodeTree node) {
                if (node.isArquivo()) {
                    setIcon(arquivoIcon); // ícone de arquivo
                    setText(node.getNome()); // emoji de arquivo
                } else {
                    setIcon(pastaIcon); // ícone de pasta
                    setText(node.getNome()); // emoji de pasta
                }
            }
        }

        // Configurações de cor
        setBackground(new Color(30, 30, 30)); // Cor de fundo padrão
        setForeground(new Color(221, 221, 211)); // Texto padrão

        if (sel) {
            setBackground(new Color(30, 144, 255));   // Fundo de seleção
            setForeground(new Color(255, 255, 255));  // Texto de seleção
        }

        setOpaque(true); // necessário para a cor de fundo aparecer
        return this;
    }
}
