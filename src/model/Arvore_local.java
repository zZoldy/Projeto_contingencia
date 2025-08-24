package model;

import framework.Funcoes;
import framework.Log;
import file_csv.Boletim_ctl1;
import file_csv.Boletim_ctl2;
import file_csv.Final;
import file_csv.Formato;
import file_csv.Prelim;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import model.NodeTree;
import view.Principal;
import view.jInternal_tabela;

/**
 * Classe responsável por construir, inicializar e manipular uma estrutura de
 * árvore de diretórios e arquivos associados aos produtos do sistema.
 *
 * Utiliza {@link NodeTree} como base lógica e {@link JTree} como exibição
 * Swing.
 *
 * Autor: Z D K
 */
public class Arvore_local {

    private NodeTree process_tree; // Raiz lógica da árvore
    private JTree tree;            // Componente gráfico da árvore

    private boolean treeListener_mouseCliked = false;
    private final AtomicBoolean travaAbrirFrame = new AtomicBoolean(true);

    private static final Set<String> PASTAS_IGNORADAS = Set.of("Lauda_Final", "Lauda_Prelim", "Lauda_BO_CTL1", "Lauda_BO_CTL2", "Backup");

    public Arvore_local() {

    }

    /**
     * Cria a árvore de diretórios e arquivos baseada na estrutura de pastas da
     * aplicação.
     *
     * Cria a raiz da árvore "Produtos" e suas subpastas (DF1, GE, etc.) caso
     * não existam. Também cria arquivos CSV obrigatórios para cada produto.
     *
     * @return Raiz lógica da árvore montada (NodeTree)
     */
    public NodeTree nodeTreeModel() {
        try {
            String raiz = System.getProperty("user.dir");
            File raizProdutos = new File(raiz + File.separator + "exportacoes" + File.separator + "Produtos");

            try {
                ver_path_produtos(raizProdutos); // Cria as pastas de produtos
            } catch (Exception e) {
                Log.registrarErro("Falha na verificação da Árvore Produtos", e);
            }

            try {
                ver_path_csvs(raizProdutos);     // Cria os arquivos CSV padrão
            } catch (Exception e) {
                Log.registrarErro("Falha na verificação dos Arquivos CSV da Pasta Produto", e);
            }

            try {
                process_tree = files_tree(raizProdutos); // Monta estrutura de árvore
            } catch (Exception e) {
                Log.registrarErro("Falha ao Monta a estrutura da Árvore", e);
            }

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

    /**
     * Recupera o {@link NodeTree} correspondente ao nó clicado na JTree com o
     * mouse.
     *
     * @param tree JTree com os nós exibidos
     * @param evt Evento de clique do mouse
     * @return O nó lógico ({@link NodeTree}) selecionado ou null se inválido
     */
    public NodeTree node_select(JTree tree, MouseEvent evt) {
        int row = tree.getRowForLocation(evt.getX(), evt.getY());
        if (row < 0) {
            return null;
        }

        tree.setSelectionRow(row);
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            return null;
        }

        Object selectedNode = path.getLastPathComponent();
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

    /**
     * Monta recursivamente a árvore lógica de {@link NodeTree} a partir de um
     * diretório base. Ignora as pastas "Lauda_Final", "Lauda_Prelim" e "Backup
     * Prelim".
     *
     * @param pasta Diretório base
     * @return Nó lógico correspondente
     */
    private NodeTree files_tree(File pasta) {
        NodeTree raizNode = new NodeTree(pasta.getName(), false, pasta);

        File[] arquivos = pasta.listFiles();
        if (arquivos != null) {
            for (File file : arquivos) {
                if (file.isDirectory()) {
                    String nome = file.getName();
                    if (PASTAS_IGNORADAS.contains(nome)) {
                        continue;
                    }
                    NodeTree subNode = files_tree(file);
                    raizNode.adicionar_filho(subNode);
                } else {
                    String nomeSemExtensao = file.getName().replaceFirst("[.][^.]+$", "");
                    raizNode.adicionar_filho(new NodeTree(nomeSemExtensao, true, file));
                }
            }
        }

        return raizNode;
    }

    /**
     * Verifica se as pastas dos produtos existem. Se não existirem, as cria.
     *
     * @param file Diretório "Produtos"
     */
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
                System.out.println("Pasta do " + produtos.getName() + " criada - Path: " + produtos.getPath());
            }
        }
    }

    /**
     * Cria os arquivos CSV padrão nas pastas de cada produto, se não existirem.
     * Inicializa os modelos correspondentes após a criação.
     *
     * @param produtos Diretório raiz de produtos
     */
    private void ver_path_csvs(File produtos) {
        List<String> arquivosCsv = Arrays.asList("Prelim.csv", "Final.csv", "Formato.csv");
        List<String> boletim_csv = Arrays.asList("BO_CTL1.csv", "BO_CTL2.csv");
        File[] subpastas = produtos.listFiles(File::isDirectory);

        if (subpastas == null) {
            return;
        }

        for (File pasta : subpastas) {
            // Criação dos arquivos principais (Formato, Prelim, Final)
            for (String nomeArquivo : arquivosCsv) {
                File csv = new File(pasta, nomeArquivo);
                if (!csv.exists()) {
                    try {
                        boolean criado = csv.createNewFile();
                        if (criado) {
                            String nomeBase = nomeArquivo.replaceFirst("[.][^.]+$", "");
                            switch (nomeBase) {
                                case "Formato" ->
                                    new Formato(csv.getPath(), pasta.getName(), nomeBase);
                                case "Prelim" ->
                                    new Prelim(csv.getPath(), pasta.getName(), nomeBase);
                                case "Final" ->
                                    new Final(csv.getPath(), pasta.getName(), nomeBase);
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Erro ao criar arquivo: " + csv.getPath());
                        e.printStackTrace();
                    }
                }
            }

            // Criação dos arquivos de boletim, mas só se a pasta for "DF2"
            if (pasta.getName().equals("DF2")) {
                for (String nome_bo : boletim_csv) {
                    File bo_csv = new File(pasta, nome_bo);
                    if (!bo_csv.exists()) {
                        try {
                            boolean criado = bo_csv.createNewFile();
                            if (criado) {
                                String nomeBase = nome_bo.replaceFirst("[.][^.]+$", "");
                                switch (nomeBase) {
                                    case "BO_CTL1" ->
                                        new Boletim_ctl1(bo_csv.getPath(), pasta.getName(), nomeBase);
                                    case "BO_CTL2" ->
                                        new Boletim_ctl2(bo_csv.getPath(), pasta.getName(), nomeBase);
                                }
                            }
                        } catch (IOException e) {
                            System.err.println("Erro ao criar arquivo: " + bo_csv.getPath());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Inicializa o modelo da {@link JTree} com base na estrutura de diretórios
     * atual.
     *
     * @param in_tree JTree a ser configurada
     * @return Modelo de árvore associado
     */
    public TreeModel treeModel(JTree in_tree) {
        this.tree = in_tree;
        NodeTree nodeTree = nodeTreeModel();

        DefaultMutableTreeNode rootNode = nodeTree.toTreeNode();

        tree.setModel(new DefaultTreeModel(rootNode));

        tree.setScrollsOnExpand(false); // evita pulos ao expandir
        tree.setLargeModel(true);       // melhor desempenho para grandes árvores

        tree.addTreeSelectionListener(e -> {
            tree.repaint(); // Força atualização da interface visual
        });

        return tree.getModel();
    }

    /**
     * Atualiza a referência do nó raiz com a estrutura mais recente.
     *
     * @param node Ignorado (substituído internamente)
     */
    public void attNode(NodeTree node) {
        this.process_tree = nodeTreeModel();
    }

    public void click_jTree(JTree tree, Principal view) {
        if (treeListener_mouseCliked) {
            return; // já foi adicionado
        }

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
                    NodeTree node_select = node_select(tree, evt);

                    if (node_select != null) {
                        if (node_select.isArquivo()) {
                            // --- TRAVA PARA EVITAR ABRIR VÁRIOS FRAMES ---
                            if (!travaAbrirFrame.get()) {
                                // Se está travado, ignora o clique extra!
                                System.out.println("Ação de abrir frame já em andamento!");
                                return;
                            }
                            travaAbrirFrame.set(false);

                            // Se já tem um frame aberto, fecha e só DEPOIS abre novo
                            if (view.controller.jInternal) {
                                view.controller.click_close_frame(() -> {
                                    abrirFrameComTrava(node_select, view);
                                });
                                return;
                            }

                            abrirFrameComTrava(node_select, view);
                        }

                    }

                }
            }
        });
        treeListener_mouseCliked = true;
    }

// Função auxiliar para reusar o código de abertura e destravar no fim:
    private void abrirFrameComTrava(NodeTree node_select, Principal view) {
        if (node_select != null && node_select.isArquivo()) {
            view.setCursor(Cursor.WAIT_CURSOR);
            view.setEnabled(false);

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    // Carregue ou crie o frame aqui:
                    open_file(node_select.getArquivo(), view);
                    Thread.sleep(1000); // Simule processamento pesado se necessário
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        view.setCursor(Cursor.getDefaultCursor());
                        view.setEnabled(true);
                    } finally {
                        travaAbrirFrame.set(true); // Libera a trava SEMPRE!
                    }
                }
            }.execute();
        } else {
            travaAbrirFrame.set(true); // Libera se não for arquivo válido
        }
    }

    public void open_file(File file, Principal view) {
        view.controller.tabela = new jInternal_tabela(file, view.controller.config);
        view.controller.tabela.controller.setListener(view.controller);

        try {
            Funcoes.processar_arquivo(file, view.controller.tabela.inews);
            Funcoes.init_lines_erro(file, view.controller.config);
        } catch (Exception e) {
            Funcoes.message_error("Erro ao processar arquivo");
            Log.registrarErro("Erro ao processar Arquivo", e);
        }

        view.controller.backup_file = file;

        view.controller.salvarUltimoArquivoAberto(file);

        SwingUtilities.invokeLater(() -> {
            view.controller.add_internal(file);

            Tabela.ajustarLarguraColuna(view.controller.tabela.inews);

            String tempo_entrada = (String) view.controller.tabela.inews.getValueAt(0, 13);
            view.controller.tempo_entrada(tempo_entrada);

            switch (file.getName()) {
                case "Prelim.csv", "BO_CTL1.csv", "BO_CTL2.csv" ->
                    Tabela.tempo_prelim_bo(view.controller.tabela.inews);
                case "Final.csv" ->
                    Tabela.tempo_final(view.controller.tabela.inews);
            }

            view.controller.tempo_producao();
            view.controller.tempo_saida();
            String chave = "Tempo_encerramento_" + view.controller.tabela.controller.produto_info + "_" + view.controller.tabela.controller.arquivo_info;
            view.lbl_out_jornal_tempo.setText(view.controller.config.get(chave));
            view.controller.stts_jornal();

        });
    }

    // Getters e Setters
    public NodeTree getProcess_tree() {
        return process_tree;
    }

    public void setProcess_tree(NodeTree process_tree) {
        this.process_tree = process_tree;
    }

    public JTree getTree() {
        return tree;
    }

}
