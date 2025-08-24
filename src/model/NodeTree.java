package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Representa um nó de árvore que pode ser um diretório (com filhos) ou um
 * arquivo (sem filhos).
 *
 * Utilizado na construção de estruturas de árvore visuais (ex: JTree). Pode ser
 * convertido para {@link DefaultMutableTreeNode} para exibição em UI Swing.
 *
 * Autor: Z D K
 */
public class NodeTree {

    private String nome;
    private boolean isArquivo;
    private List<NodeTree> filhos;
    private File arquivo;

    /**
     * Construtor padrão de nó.
     *
     * @param nome Nome do nó
     * @param isArquivo Indica se é um arquivo (true) ou pasta (false)
     */
    public NodeTree(String nome, boolean isArquivo) {
        this.nome = nome;
        this.isArquivo = isArquivo;

        if (!isArquivo) {
            this.filhos = new ArrayList<>();
        } else {
            this.filhos = null;
        }
    }

    /**
     * Construtor que também recebe um {@link File} associado ao nó.
     *
     * @param nome Nome do nó
     * @param isArquivo Indica se é um arquivo (true) ou diretório (false)
     * @param arquivo Objeto File correspondente
     */
    public NodeTree(String nome, boolean isArquivo, File arquivo) {
        this.nome = nome;
        this.isArquivo = isArquivo;
        this.arquivo = arquivo;

        if (!isArquivo) {
            this.filhos = new ArrayList<>();
        }
    }

    /**
     * Adiciona um filho ao nó, caso ele não seja um arquivo.
     *
     * @param filho O nó filho a ser adicionado
     */
    public void adicionar_filho(NodeTree filho) {
        try {
            if (!isArquivo()) {
                getFilhos().add(filho);
            }
        } catch (Exception e) {
            System.err.println("Erro ao adicionar Filho: " + e);
        }
    }

    /**
     * Converte o nó atual (e seus filhos) para um
     * {@link DefaultMutableTreeNode}, para ser usado em uma árvore Swing (como
     * JTree).
     *
     * @return Nó convertido como {@link DefaultMutableTreeNode}
     */
    public DefaultMutableTreeNode toTreeNode() {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(this);

        try {
            if (!isArquivo && filhos != null) {
                for (NodeTree filho : filhos) {
                    treeNode.add(filho.toTreeNode());
                }
            }
        } catch (Exception e) {
            System.err.println("\tErro toTreeNode()\n" + e);
        }

        return treeNode;
    }

    @Override
    public String toString() {
        return "\n\tNode"
                + "\nNome: " + nome
                + "\nArquivo: " + (arquivo != null ? arquivo.getName() : "null")
                + "\nÉ arquivo: " + isArquivo
                + "\nFilhos: " + filhos;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isArquivo() {
        return isArquivo;
    }

    public void setIsArquivo(boolean isArquivo) {
        this.isArquivo = isArquivo;
    }

    public List<NodeTree> getFilhos() {
        return filhos;
    }

    public void setFilhos(List<NodeTree> filhos) {
        this.filhos = filhos;
    }

    public File getArquivo() {
        return arquivo;
    }

    public void setArquivo(File arquivo) {
        this.arquivo = arquivo;
    }
}
