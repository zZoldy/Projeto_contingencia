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

    private Color corFundo;
    private Color corTexto;
    private Color corFundoSelecionado;
    private Color corTextoSelecionado;

    public CustomTreeRenderer(Color corFundo, Color corTexto, Color corFundoSelecionado, Color corTextoSelecionado) {
        this.corFundo = corFundo;
        this.corTexto = corTexto;
        this.corFundoSelecionado = corFundoSelecionado;
        this.corTextoSelecionado = corTextoSelecionado;
    }

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
        setBackground(corFundo); // Cor de fundo padrão
        setForeground(corTexto); // Texto padrão

        if (sel) {
            setBackground(corFundoSelecionado);   // Fundo de seleção
            setForeground(corTextoSelecionado);  // Texto de seleção
        }

        setOpaque(true); // necessário para a cor de fundo aparecer
        setBorder(null);
        return this;
    }
}
