/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Z D K
 */
// Classe responsável pelo Gerenciamento de Arquivos e Pasta do JTree
public class NodeTree {

    private String nome;
    private boolean isArquivo;
    private List<NodeTree> filhos;
    private Arquivo arquivo;

    public NodeTree(String nome, boolean isArquivo) {
        this.nome = nome;
        this.isArquivo = isArquivo;

        if (!isArquivo) {
            this.filhos = new ArrayList<>();
        } else {
            this.filhos = null;
        }
    }

    @Override
    public String toString() {
        return "\n\tNode"
                + "\nNome: " + nome
                + "\nÉ arquivo: " + isArquivo
                + "\nFilhos: " + filhos
                + arquivo;
    }

    public NodeTree(String nome, boolean isArquivo, Arquivo arquivo) {
        this.nome = nome;
        this.isArquivo = isArquivo;
        this.arquivo = arquivo;
        if (!isArquivo) {
            filhos = new ArrayList<>();
        }
        if (arquivo != null) {
            arquivo.setPath_file(arquivo.getFile().getPath());
        }
    }

    public void adicionar_filho(NodeTree filho) {
        try {
            if (!isArquivo()) {
                getFilhos().add(filho);
            }
        } catch (Exception e) {
            System.err.println("Erro ao adicionar Filho: " + e);
        }

    }

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

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

}
