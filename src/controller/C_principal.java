/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import Framework.Funcoes;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import model.Lauda;
import model.NodeTree;
import model.Table;
import model.Tree;
import view.Principal;
import view.Tbl_news;
import Listener.Tempo_listener;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import model.Tema;

/**
 *
 * @author Z D K
 */
public class C_principal implements Tempo_listener {

    Principal view;
    Tbl_news tbl_news;
    List<JLabel> atalhos = new ArrayList<>();
    public Tree tree;

    public File back_file;

    boolean jInternal = false;
    private boolean treeListenerAdicionado = false;

    public static Properties config = Funcoes.init_properties();

    public Tema tema;

    public C_principal(Principal view) {
        this.view = view;
    }

    public void init() {
        def_labels_final();
        setTree();

        UIManager.put("Tree.drawsFocusBorderAroundIcon", false);
        UIManager.put("Tree.drawDashedFocusIndicator", false);

        hora_atual();

        tema = new Tema(view.Desktop, view.pn_superior_1, view.pn_lateral_esquerdo, view.pn_inferior_1, view.pn_inferior_2, view.lbl_hora_atual, view.lbl_frame_open, view.lbl_close, view.lbl_in_jorn, view.lbl_in_jornal_tempo, view.lbl_tempo_producao, view.lbl_tempo_producao_tempo, view.lbl_out_jorn, view.lbl_out_jornal_tempo, view.lbl_encerramento, view.lbl_encerramento_tempo, view.lbl_stts_jornal, view.lbl_stts_jornal_tempo, view.tree_produto, view.mn_superior);

        for (String chave : config.stringPropertyNames()) {
            String valor = config.getProperty(chave);
            if (chave.equals("Tema")) {
                if (valor.equals("Default")) {
                    tema.Desktop_default(config);
                    view.mn_item_modo_tema.setText("Mudar para o Modo Escuro");
                } else if (valor.equals("Dark")) {
                    tema.Desktop_dark(config);
                    view.mn_item_modo_tema.setText("Mudar para o Modo Claro");

                }
            }

            if (chave.equals("last_file_open")) {
                if (!valor.equals("")) {
                    File last_file_open = new File(valor);
                    if (last_file_open.exists()) {
                        init_frame(last_file_open);
                    }
                }
            }

        }

    }

    void def_labels_final() {
        view.lbl_close.setVisible(false);

        view.lbl_tempo_producao_tempo.setVisible(false);
        view.lbl_tempo_producao.setVisible(false);

        view.lbl_in_jornal_tempo.setVisible(false);
        view.lbl_in_jorn.setVisible(false);

        view.lbl_out_jornal_tempo.setVisible(false);
        view.lbl_out_jorn.setVisible(false);

        view.lbl_encerramento.setVisible(false);
        view.lbl_encerramento_tempo.setVisible(false);

        view.lbl_stts_jornal.setVisible(false);
        view.lbl_stts_jornal_tempo.setVisible(false);
    }

    public void tema_dark() {
        if (tbl_news != null) {
            tema.JTable_dark(tbl_news.tbl_news);
        }
        tema.Desktop_dark(config);
        view.mn_item_modo_tema.setText("Mudar para o Modo Claro");
    }

    public void tema_default() {
        if (tbl_news != null) {
            tema.JTable_default(tbl_news.tbl_news);
        }
        tema.Desktop_default(config);
        view.mn_item_modo_tema.setText("Mudar para o Modo Escuro");
    }

    public void choose_tema() {
        System.out.println("Tema IN: " + Tema.modelo_tema);
        if (Tema.modelo_tema.equals("Default")) {
            tema_dark();
        } else if (Tema.modelo_tema.equals("Dark")) {
            tema_default();
        }
        System.out.println("Tema OUT: " + Tema.modelo_tema);
    }

    public void def_tema_table() {
        if (Tema.modelo_tema.equals("Default")) {
            Tema.JTable_default(tbl_news.tbl_news);
        } else if (Tema.modelo_tema.equals("Dark")) {
            Tema.JTable_dark(tbl_news.tbl_news);
        }
    }

    /**
     *
     * @param tempo
     */
    @Override
    public void onTempoEntradaAtualizado(String tempo) {
        view.lbl_in_jornal_tempo.setText(tempo);
    }

    @Override
    public void onAttTempo() {
        if (tbl_news.info.get(1).equals("Prelim")) {
            tbl_news.controller.tempo_prelim_bo(tbl_news.tbl_news);
        } else if (tbl_news.info.get(1).equals("Final")) {
            tbl_news.controller.tempo_final(tbl_news.tbl_news);
        } else if (tbl_news.info.get(1).equals("BOLETIM_CTL1")) {
            tbl_news.controller.tempo_prelim_bo(tbl_news.tbl_news);
        } else if (tbl_news.info.get(1).equals("BOLETIM_CTL2")) {
            tbl_news.controller.tempo_prelim_bo(tbl_news.tbl_news);
        }

        view.lbl_tempo_producao_tempo.setText(tbl_news.controller.getTempoProducao());
    }

    @Override
    public void attSaidaJornal() {
        view.lbl_out_jornal_tempo.setText(tbl_news.controller.getTempoSaida());
    }

    @Override
    public void attInTempos(String produto, String arquivo) {
        tempoEncerramento(produto, arquivo, view.lbl_stts_jornal_tempo);
    }

    public void tempoEncerramento(String produto, String arquivo, JLabel label) {
        LocalTime encerramento = LocalTime.parse(view.lbl_encerramento_tempo.getText());
        LocalTime saida = LocalTime.parse(view.lbl_out_jornal_tempo.getText());

        Duration diferenca = Duration.between(encerramento, saida).abs();

        String mensagem;

        Color cor;

        if (encerramento.isAfter(saida)) {
            mensagem = "Buraco " + Funcoes.formatarDuracao(diferenca);
            cor = new Color(255, 204, 0); // Amarelo
        } else if (encerramento.isBefore(saida)) {
            mensagem = "Estouro " + Funcoes.formatarDuracao(diferenca);
            cor = new Color(255, 51, 51); // Vermelho
        } else {
            mensagem = "OK";
            cor = new Color(0, 153, 0); // Verde
        }

        try {
            String chave = "Tempo_encerramento_" + produto + "_" + arquivo;
            String valor = view.lbl_encerramento_tempo.getText();
            Funcoes.salvarConfiguracao(config, chave, valor);

            label.setText(mensagem);
            label.setForeground(cor);
        } catch (Exception e) {
            System.err.println("\tErro ao carregar Tempo_encerramento na memória\n");
            e.printStackTrace();
        }
    }

    public void info_produto_arquivo(String produto, String arquivo) {
        if (produto.equals("") || produto.isBlank()) {
            if (tbl_news != null) {
                produto = tbl_news.info.get(0);
                System.out.println("P: " + produto);
            }
        }

        if (arquivo.equals("") || arquivo.isBlank()) {
            if (tbl_news != null) {
                arquivo = tbl_news.info.get(1);
                System.out.println("Arquivo: " + arquivo);
            }
        }
        tempoEncerramento(produto, arquivo, view.lbl_stts_jornal_tempo);
    }

    public void info_variaveis() {
        // System.out.println(tree);
        Component[] componentes = view.pn_superior_1.getComponents();
        System.out.println("Componentes no Painel Superior:");

        int cont = 0;
        for (Component c : componentes) {
            System.out.println("- " + cont + " | Visible: " + c.isVisible());
            cont++;
        }

        System.out.println("Quantidade Total: " + cont);
        System.out.println("\n\tConfig");
        System.out.println(config);

        System.out.println("Boolean jInternal: " + jInternal);
        System.out.println("File Backup: " + back_file);

        System.out.println("\n\tTree");
        System.out.println("Tree Name - " + tree.getProcess_tree().getNome());
        System.out.println("File Name - " + tree.getProcess_tree().getArquivo().getFile());
        System.out.println("Tree:" + tree);
        System.out.println("Node: " + tree.getProcess_tree());

        System.out.println("Infomações de Memória");
        System.out.println("Estrutura Tree: " + tree.getProcess_tree().getArquivo().getFile());
        System.out.println("Arquivo em Memória: " + this.back_file);
        System.out.println("Referência JInternalFrame: " + ((tbl_news == null) ? "Nulo" : "Referenciado"));
        String estadoTabela = (tbl_news == null || tbl_news.tbl_news == null) ? "Nulo" : "Referenciado";
        System.out.println("Referência JTable: " + estadoTabela);
    }

    public String memoria_app() {
        System.gc(); // Sugere ao GC que rode
        try {
            Thread.sleep(500); // Dá tempo pro GC processar
        } catch (InterruptedException ex) {
            Logger.getLogger(C_principal.class.getName()).log(Level.SEVERE, null, ex);
        }
        long memoriaUsada = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        double memoria = memoriaUsada / 1024;
        return String.valueOf(memoria);
    }

    public void setTree() {
        tree = new Tree();
        tree.treeModel(view.tree_produto);
    }

    public void process_init_table_jtree() {
        if (treeListenerAdicionado) {
            return; // já foi adicionado
        }
        view.tree_produto.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
                    NodeTree node_select = tree.node_select(view.tree_produto, evt);

                    if (node_select != null && node_select.isArquivo()) {
                        init_frame(node_select.getArquivo().getFile());
                    }
                }
            }
        });

        treeListenerAdicionado = true;
    }

    public void init_frame(File file) {
        System.out.println(">>> CHAMOU init_frame | jInternal = " + jInternal);

        if (!jInternal) {
            open_file(file);
        } else if (jInternal) {
            boolean confirm = Funcoes.message_confirm(null, "Deseja fechar a tabela atual?", "Arquivo em instância");
            if (confirm) {
                process_close_table();
                open_file(file);
            }
        }
    }

    void open_file(File file) {
        tbl_news = new Tbl_news(file);
        tbl_news.controller.configurarAjusteAutomatico(tbl_news.tbl_news);
        tbl_news.controller.setListener(this);

        SwingUtilities.invokeLater(() -> {
            tbl_news.setSize(view.Desktop.getSize());
            view.Desktop.add(tbl_news);
        });

        try {
            Funcoes.processar_arquivo(file, tbl_news.tbl_news);
            tbl_news.controller.configurarAjusteAutomatico(tbl_news.tbl_news);

            Funcoes.init_lines_erro(file, config);
        } catch (Exception e) {
            System.err.println("\tErro ao processar arquivo: \n" + e);
            return;
        }

        this.jInternal = true;
        this.back_file = file;

        info_file_init_frame(file);

        onAttTempo();

        Funcoes.salvarConfiguracao(config, "last_file_open", file.getPath());

    }

    void info_file_init_frame(File file) {
        String produto_info = new File(file.getParent()).getName();
        String arquivo_info = file.getName().replaceFirst("[.][^.]+$", "");

        view.lbl_frame_open.setText(produto_info + "-" + arquivo_info);
        tbl_news.controller.lauda = new Lauda(produto_info, arquivo_info);

        view.lbl_close.setVisible(true);
        view.lbl_close.setText("Clique para fechar");

        view.lbl_in_jornal_tempo.setVisible(true);
        view.lbl_in_jorn.setVisible(true);

        view.lbl_out_jornal_tempo.setVisible(true);
        view.lbl_out_jorn.setVisible(true);

        view.lbl_tempo_producao_tempo.setVisible(true);
        view.lbl_tempo_producao.setVisible(true);

        if (arquivo_info.equals("Final")) {
            view.lbl_encerramento.setVisible(true);
            view.lbl_encerramento_tempo.setVisible(true);

            view.lbl_stts_jornal.setVisible(true);
            view.lbl_stts_jornal_tempo.setVisible(true);
        }

        view.lbl_in_jornal_tempo.setText(tbl_news.controller.getTempoEntrada());
        view.lbl_tempo_producao_tempo.setText(tbl_news.controller.getTempoProducao());
        view.lbl_out_jornal_tempo.setText(tbl_news.controller.getTempoSaida());
        view.lbl_encerramento_tempo.setText(rescue_time_encerramento(produto_info, arquivo_info));
        tempoEncerramento(produto_info, arquivo_info, view.lbl_stts_jornal_tempo);

        def_tema_table();

        tbl_news.setVisible(true);
    }

    public void close_frame(File file) {
        if (jInternal) {
            try {
                Funcoes.save_file(tbl_news.tbl_news, file);
                if (tbl_news.controller.lauda.ver_stts_lauda()) {
                    System.out.println("Lauda salva pae");
                }
            } catch (Exception e) {
                System.err.println("\nErro ao salvar Arquivo\n" + e);
            }
        }
    }

    public void process_close_table() {
        if (jInternal) {
            tbl_news.controller.close_pop_up();
            try {
                Funcoes.save_file(tbl_news.tbl_news, back_file);
            } catch (Exception e) {
                System.err.println("\nErro ao salvar Arquivo\n" + e);
            }

            tbl_news.controller.lauda = new Lauda("", "");

            view.lbl_tempo_producao_tempo.setVisible(false);
            view.lbl_tempo_producao.setVisible(false);

            view.lbl_in_jornal_tempo.setVisible(false);
            view.lbl_in_jorn.setVisible(false);

            view.lbl_out_jornal_tempo.setVisible(false);
            view.lbl_out_jorn.setVisible(false);

            view.lbl_encerramento.setVisible(false);
            view.lbl_encerramento_tempo.setVisible(false);

            view.lbl_stts_jornal.setVisible(false);
            view.lbl_stts_jornal_tempo.setVisible(false);

            view.lbl_close.setVisible(false);
            view.lbl_frame_open.setText("");

            this.jInternal = false;

            this.back_file = null;

            tree.attNode(tree.getProcess_tree());

            Funcoes.salvarConfiguracao(config, "last_file_open", "");
            Table.linhasComErroDeTempo.clear();

            tbl_news.tbl_news.setModel(new DefaultTableModel()); // limpa conteúdo
            tbl_news.tbl_news = null; // remove referência
            tbl_news.dispose();       // fecha janela
            tbl_news = null;          // remove frame
        }
    }

    public void show_popup_jtree(MouseEvent evt) {
        JPopupMenu popUp = new JPopupMenu();
        int row = view.tree_produto.getRowForLocation(evt.getX(), evt.getY());

        if (row <= 0) {
            return;
        }

        NodeTree node_select = tree.node_select(view.tree_produto, evt);
        if (node_select != null) {

            if (!node_select.isArquivo()) {
                JMenuItem pop = new JMenuItem("Pasta ");
                popUp.add(pop);
            } else {
                JMenuItem pop = null;

                if (view.pn_superior_1.isVisible()) {
                    JMenuItem criarAtalho = new JMenuItem("Criar Atalho no Painel");
                    criarAtalho.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JLabel label_atalho = Funcoes.addAtalho_inTree_soutPanel(node_select, view.pn_superior_1, atalhos, label -> Funcoes.jlabel_arrastado(atalhos, label, view.pn_superior_1), tema.modelo_tema);
                            if (label_atalho != null) {
                                label_atalho.addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        if (SwingUtilities.isLeftMouseButton(e)) {
                                            File f = (File) label_atalho.getClientProperty("file");
                                            init_frame(f);
                                        }
                                    }
                                });
                            }

                        }
                    });
                    popUp.add(criarAtalho);
                }

                if (node_select.getNome().equals("Formato")) {
                    pop = new JMenuItem("Enviar para Prelim");
                    pop.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            File parent_formato = new File(node_select.getArquivo().getFile().getParent());
                            File prelim = new File(parent_formato, "Prelim.csv");

                            try {
                                Funcoes.copy_file(node_select.getArquivo().getFile(), prelim);
                                backup_prelim(prelim);
                                ver_table_open(back_file);
                            } catch (Exception ex) {
                                System.err.println("\tErro ao copiar Formato/Prelim\n" + ex);
                            }

                            JOptionPane.showMessageDialog(null, "Item Copiado com sucesso");
                        }
                    });
                }

                if (pop != null) {
                    popUp.add(pop);
                }
            }

            popUp.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }

    void ver_table_open(File file) {
        if (file != null) {
            process_close_table();
        }
    }

    public void tbl_responsiva() {
        if (tbl_news != null) {
            tbl_news.setSize(view.Desktop.getSize());
        }
    }

    public void hora_atual() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
        scheduler.scheduleAtFixedRate(() -> {
            LocalTime agora = LocalTime.now();
            String horario_formatado = agora.format(formato);
            view.lbl_horario.setText(horario_formatado);
        }, 0, 1, TimeUnit.SECONDS);
    }

    public String rescue_time_encerramento(String produto, String arquivo) {
        String nome_chave = "Tempo_encerramento_" + produto + "_" + arquivo;
        for (String chave : config.stringPropertyNames()) {
            String valor = config.getProperty(chave);
            if (chave.equals(nome_chave)) {
                if (!valor.equals("00:00:00")) {
                    System.out.println("Valor: " + valor);
                    return valor;
                }
            }
        }
        return "00:00:00";
    }

    public File backup_prelim(File file) {
        File path_backup = new File(file.getParent(), "Backup Prelim");
        if (!path_backup.exists() || !path_backup.isDirectory()) {
            path_backup.mkdirs();
            JOptionPane.showMessageDialog(null, "Diretório de Produto criado com sucesso: " + file.getPath());
        }



        return path_backup;
    }

    public void format_produto() {
        boolean reset_prod = Funcoes.message_confirm(null, "Deseja resetar a pasta Produto?", "Formatar Produto");

        if (reset_prod) {
            process_close_table();

            try {
                if (!deleteRecursively(tree.getProcess_tree().getArquivo().getFile())) {
                    System.err.println("Erro: não foi possível excluir o arquivo Produto.");
                }
            } catch (Exception e) {
                System.err.println("\nErro ao excluir Produtos - \n" + e);
            }

            tree.attNode(tree.getProcess_tree());

        }

    }

    public boolean deleteRecursively(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursively(child);
            }
        }
        return file.delete();
    }

}
