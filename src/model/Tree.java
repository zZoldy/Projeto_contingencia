/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import tables.Boletim_ctl1;
import tables.Boletim_ctl2;
import tables.Final;
import tables.Formato;
import tables.Prelim;

/**
 *
 * @author Z D K
 */
public class Tree {

    private NodeTree process_tree;
    private JTree tree;

    public Tree() {
    }

    // Verificação da existência das pastas dos produtos, junto com seu Home(Exportação)
    // Retorna processTree
    public NodeTree nodeTreeModel() {
        try {
            String raiz = System.getProperty("user.dir");
            File raizProdutos = new File(raiz + File.separator + "exportacoes" + File.separator + "Produtos");

            ver_path_produtos(raizProdutos);
            ver_path_csvs(raizProdutos);

            process_tree = files_tree(raizProdutos);
            return process_tree;

        } catch (Exception e) {
            System.err.println("\tErro\n" + e);
        }
        return null;
    }

    @Override
    public String toString() {
        return "\n\tTree"
                + process_tree
                + "\nTreeName: " + tree.getName() + " - TreeModel: " + tree;
    }

    // Arquivo (nó) selecionado da JTREE
    public NodeTree node_select(JTree tree, MouseEvent evt) {
        int row = tree.getRowForLocation(evt.getX(), evt.getY());
        if (row < 0) {
            return null;
        }

        tree.setSelectionRow(row);
        TreePath path = tree.getSelectionPath();
        // Imprimi [Pasta 1, Pasta 2, Pasta 3]
        if (path == null) {
            return null;
        }

        Object selectedNode = path.getLastPathComponent();
        // Imprimi [Pasta Selecionada]
        if (!(selectedNode instanceof DefaultMutableTreeNode)) {
            return null;
        }

        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selectedNode;
        Object userObject = treeNode.getUserObject();

        if (userObject instanceof NodeTree node) {
            return node;
        }
        return null;
    }

    private NodeTree files_tree(File pasta) {
        Arquivo arq = new Arquivo(pasta.getPath(), pasta);

        // Ignorando os arquivos na JTree
        if (pasta.getName().equals("Lauda_Final") || pasta.getName().equals("Lauda_Prelim") || pasta.getName().equals("Backup Prelim")) {
            return null;
        }
        NodeTree raizNode = new NodeTree(arq.getFile().getName(), false, arq);
        File[] arquivos = pasta.listFiles();
        if (arquivos != null) {
            for (File file : arquivos) {
                if (file.isDirectory()) {
                    // System.out.println("Pasta: " + file.getName());
                    NodeTree subNode = files_tree(file);
                    raizNode.adicionar_filho(subNode);

                } else {
                    // System.out.println("Arquivo: " + file.getName());
                    Arquivo arq_file = new Arquivo(file.getPath(), file);

                    String nomeSemExtensao = file.getName().replaceFirst("[.][^.]+$", "");
                    raizNode.adicionar_filho(new NodeTree(nomeSemExtensao, true, arq_file));

                }
            }
        }
        return raizNode;
    }

    private void ver_path_produtos(File file) {
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
            JOptionPane.showMessageDialog(null, "Diretório de Produto criado com sucesso: " + file.getPath());
        }

        List<String> pastas = Arrays.asList("BDBR", "BDDF", "DF1", "GE", "DF2", "GCO");
        for (String nome : pastas) {
            File produtos = new File(file, nome);
            if (!produtos.exists() || !produtos.isDirectory()) {
                produtos.mkdirs();
                System.out.println("Pasta do " + produtos.getName() + "Criada - Path: " + produtos.getPath());
            }
        }
    }

    private void ver_path_csvs(File produtos) {
        // Lista com os nomes dos arquivos .csv que serão criados
        List<String> arquivosCsv = Arrays.asList("Prelim.csv", "Final.csv", "Formato.csv", "BOLETIM_CTL1.csv", "BOLETIM_CTL2.csv");

        // Para cada subpasta dentro da pasta "Produtos"
        File[] subpastas = produtos.listFiles(File::isDirectory);
        if (subpastas == null) {
            return;
        }

        for (File pasta : subpastas) {
            System.out.println("PASTA: " + pasta.getName());
            for (String nomeArquivo : arquivosCsv) {
                File csv = new File(pasta, nomeArquivo);
                if (!csv.exists()) {
                    try {
                        boolean criado = csv.createNewFile();
                        if (criado) {
                            System.out.println("Arquivo criado: " + csv.getPath());
                            if (nomeArquivo.equals("Formato.csv")) {
                                Formato format = new Formato(csv.getPath(), pasta.getName(), nomeArquivo.replaceFirst("[.][^.]+$", ""));

                            } else if (nomeArquivo.equals("Prelim.csv")) {
                                Prelim prelim_final = new Prelim(csv.getPath(), pasta.getName(), nomeArquivo.replaceFirst("[.][^.]+$", ""));

                            } else if (nomeArquivo.equals("Final.csv")) {
                                Final final_modelo = new Final(csv.getPath(), pasta.getName(), nomeArquivo.replaceFirst("[.][^.]+$", ""));

                            } else if (nomeArquivo.equals("BOLETIM_CTL1.csv")) {
                                Boletim_ctl1 b_c1 = new Boletim_ctl1(csv.getPath(), pasta.getName(), nomeArquivo.replaceFirst("[.][^.]+$", ""));

                            } else if (nomeArquivo.equals("BOLETIM_CTL2.csv")) {
                                Boletim_ctl2 b_c2 = new Boletim_ctl2(csv.getPath(), pasta.getName(), nomeArquivo.replaceFirst("[.][^.]+$", ""));

                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Erro ao criar arquivo: " + csv.getPath());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public TreeModel treeModel(JTree in_tree) {
        this.tree = in_tree;
        NodeTree nodeTree = nodeTreeModel();

        DefaultMutableTreeNode rootNode = nodeTree.toTreeNode();

        tree.setModel(new DefaultTreeModel(rootNode));
        tree.setBorder(null);
        return tree.getModel();
    }

    public void attNode(NodeTree node) {
        node = nodeTreeModel();
        System.out.println("Node: " + node);
    }

    public NodeTree getProcess_tree() {
        return process_tree;
    }

    public void setProcess_tree(NodeTree process_tree) {
        this.process_tree = process_tree;
    }
}
