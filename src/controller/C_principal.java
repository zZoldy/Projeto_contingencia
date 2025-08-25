/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import framework.Config;
import framework.Funcoes;
import framework.Log;
import Listener.Tabela_to_principal;
import conexao.ConexaoAtiva;
import conexao.NetUtils;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import model.Arvore_local;
import model.NodeTree;
import model.Tabela;
import model.Tema;
import view.Principal;
import view.jInternal_tabela;

/**
 *
 * @author Z D K
 */
public class C_principal implements Tabela_to_principal {

    Principal view;
    Arvore_local arvore;

    public ConexaoAtiva host_in;
    public List<JMenuItem> temasMenuItems;

    // Referencia no label para debug, PUBLIC
    public Config config;
    public String posicao;
    public boolean jInternal = false;
    public File backup_file;
    public jInternal_tabela tabela;

    public C_principal(Principal view) {
        this.arvore = new Arvore_local();
        this.config = new Config();
        this.view = view;

    }

    public void init_controller() {
        Funcoes.relogio(view.lbl_horario);

        arvore.treeModel(view.tree_produto);

        // Disparo de Backup
        /*
        Funcoes.iniciar_disparo(300, () -> {
            String timestamp = new java.text.SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new java.util.Date());
            File destino = new File("Backup", "Dados_" + timestamp);
            Funcoes.backup_dados(arvore.getProcess_tree().getArquivo(), destino);
        });
         */
        String coluna = config.get("coluna_style");
        if (coluna.equals("LAST_COLUMN")) {
            view.mn_coluna_style.setText("Soltar colunas");

        } else if (coluna.equals("OFF")) {
            view.mn_coluna_style.setText("Mostrar todas colunas na tela");
        }

        apply_tema(config.get("Tema"), view.tree_produto);

        temasMenuItems = Arrays.asList(view.menu_tema_default, view.menu_tema_dark, view.menu_tema_star_light);

        switch (config.get("Tema")) {
            case "Default" ->
                atualizarMenuTemas(view.menu_tema_default, temasMenuItems);

            case "Dark" ->
                atualizarMenuTemas(view.menu_tema_dark, temasMenuItems);

            case "Star Light" ->
                atualizarMenuTemas(view.menu_tema_star_light, temasMenuItems);

            default ->
                atualizarMenuTemas(view.menu_tema_default, temasMenuItems);
        }
    }

    public void in_operador() {
        while (true) {
            posicao = Funcoes.message_in(view, "Posição:", "Identificação do Operador");

            if (posicao == null) {
                System.exit(0);
            }

            if (posicao.isBlank()) {
                continue;
            }
            break;
        }

        Optional<ConexaoAtiva> conn = NetUtils.infoConexaoAtual();

        if (conn.isPresent()) {
            ConexaoAtiva c = conn.get();
            c.log_interface(posicao);
        } else {
            Log.registrarErro_noEx("Tentativa de Entrada do Usuário: " + posicao + " - Sem conexão");
            posicao = "N/I";
        }

        view.lbl_posicao.setText(posicao);
        last_file_open();
    }

    public void show_pn_lateral() {
        if (view.pn_lateral.isVisible()) {
            view.pn_lateral.setVisible(false);
            view.lbl_close_pn_lateral.setText(">");
        } else {
            view.pn_lateral.setVisible(true);
            view.lbl_close_pn_lateral.setText("<");
        }
    }

    public void last_file_open() {
        String lastPath = config.get("last_file_open");

        System.out.println("Last: " + lastPath);
        if (lastPath != null && !lastPath.equals("No file")) {
            File file = new File(lastPath);
            if (file.exists()) {
                rescue_file_open(file);
            }
        }
    }

    public void atualizarMenuTemas(JMenuItem selecionado, List<JMenuItem> todosItens) {
        for (JMenuItem item : todosItens) {
            item.setEnabled(true);
        }
        selecionado.setEnabled(false);
    }

    public void apply_tema(String tema, JTree tree) {
        if (tema.equals("Default")) {
            Tema.Default(view.pn_frame, tree);
            if (tabela != null) {
                Tema.jTable_default(tabela.inews);
            }
        } else if (tema.equals("Dark")) {
            Tema.Dark(view.pn_frame, tree, "Dark");
            if (tabela != null) {
                Tema.jTabel_dark(tabela.inews);
            }
        } else if (tema.equals("Star Light")) {
            Tema.Dark(view.pn_frame, tree, "Star Light");
            if (tabela != null) {
                Tema.jTable_star_light(tabela.inews);
            }
        } else {
            config.set("Tema", "Default");
            apply_tema(config.get("Tema"), view.tree_produto);
            Log.registrarErro_noEx("Tema indefinido");
            return;
        }

        if (tabela != null) {
            tabela.inews.requestFocusInWindow();
        }
        // Salvando no config
        config.set("Tema", tema);
    }

    public void refresh_collumn() {
        if (tabela != null) {
            String coluna_style = config.get("coluna_style");

            if (coluna_style.equals("LAST_COLUMN")) {
                view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        Thread.sleep(1000);
                        return null;
                    }

                    @Override
                    protected void done() {
                        // Após carregamento, reverter o cursor
                        try {
                            tabela.inews.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                            view.mn_coluna_style.setText("Mostrar todas colunas na tela");

                            view.setCursor(Cursor.getDefaultCursor());
                            Log.registrarErro_noEx("Coluna - AUTO RESIZE OFF");
                        } finally {
                            config.set("coluna_style", "OFF");
                        }

                    }
                }.execute();

            } else if (coluna_style.equals("OFF")) {

                view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        Thread.sleep(1000);
                        return null;
                    }

                    @Override
                    protected void done() {
                        // Após carregamento, reverter o cursor
                        try {
                            tabela.inews.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

                            view.mn_coluna_style.setText("Soltar colunas");

                            view.setCursor(Cursor.getDefaultCursor());
                            Log.registrarErro_noEx("Coluna - AUTO RESIZE LAST_COLUMN");
                        } finally {
                            config.set("coluna_style", "LAST_COLUMN");
                        }
                    }
                }.execute();

            }

            att_coluna_mode();

        }
    }

    public String memoria_app() {
        System.gc(); // Sugere ao GC que rode
        try {
            Thread.sleep(500); // Dá tempo pro GC processar
        } catch (InterruptedException ex) {
            Logger.getLogger(C_principal.class.getName()).log(Level.SEVERE, null, ex);
        }
        long memoriaUsada = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        double memoriaMB = memoriaUsada / (1024.0 * 1024.0);
        return String.format("%.2f MB", memoriaMB);
    }

    public void show_popup_jtree(MouseEvent evt) {
        JPopupMenu popUp = new JPopupMenu();
        int row = view.tree_produto.getRowForLocation(evt.getX(), evt.getY());

        if (row <= 0) {
            return;
        }

        NodeTree node_select = arvore.node_select(view.tree_produto, evt);

        if (node_select != null && node_select.getNome().equals("Formato")) {
            JMenuItem popPrelim = new JMenuItem("Enviar para Prelim");
            popPrelim.addActionListener(e -> {
                boolean confirm = Funcoes.message_confirm(null, "Deseja copiar Formato > Prelim ?", "Copy: Formato > Prelim");
                if (confirm) {
                    transfer_formato(node_select.getArquivo(), "Prelim.csv");
                }
            });

            File parent = new File(node_select.getArquivo().getParent());
            if (parent.getName().equals("DF2")) {
                JMenuItem popBoletim1 = new JMenuItem("Enviar para BO_CTL1");
                popBoletim1.addActionListener(e -> {
                    boolean confirm = Funcoes.message_confirm(null, "Deseja copiar Formato > BO_CTL1 ?", "Copy: Formato > BO_CTL1");
                    if (confirm) {
                        transfer_formato(node_select.getArquivo(), "BO_CTL1.csv");
                    }
                });

                JMenuItem popBoletim2 = new JMenuItem("Enviar para BO_CTL2");
                popBoletim2.addActionListener(e -> {
                    boolean confirm = Funcoes.message_confirm(null, "Deseja copiar Formato > BO_CTL2 ?", "Copy: Formato > BO_CTL2");
                    if (confirm) {
                        transfer_formato(node_select.getArquivo(), "BO_CTL2.csv");
                    }
                });
                popUp.add(popBoletim1);
                popUp.add(popBoletim2);
            }

            popUp.add(popPrelim);

            popUp.show(evt.getComponent(), evt.getX(), evt.getY());
        } else if (node_select != null && node_select.getNome().equals("Prelim")) {
            JMenuItem popPrelim = new JMenuItem("Enviar para Final");
            popPrelim.addActionListener(e -> {
                boolean confirm = Funcoes.message_confirm(null, "Deseja copiar Prelim > Final?", "Copy: Prelim > Final");
                if (confirm) {
                    transfer_formato(node_select.getArquivo(), "Final.csv");
                }
            });
            popUp.add(popPrelim);

            popUp.show(evt.getComponent(), evt.getX(), evt.getY());
        }

    }

    void transfer_formato(File formato, String destino) {
        try {
            File parent_formato = new File(formato.getParent());
            File arquivo = new File(parent_formato, destino);
            Funcoes.copy_file(formato, arquivo);
            view.setCursor(Cursor.WAIT_CURSOR);
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    String arquivo_csv = tabela.controller.arquivo_info + ".csv";
                    if (tabela.controller.produto_info.equals(parent_formato.getName()) && arquivo_csv.equals(destino)) {
                        kill_internal();
                        Funcoes.copy_file(formato, arquivo);
                        Thread.sleep(2000);
                        SwingUtilities.invokeLater(() -> {
                            arvore.open_file(arquivo, view);

                        });
                    }
                    return null;
                }

                @Override
                protected void done() {
                    // Após carregamento, reverter o cursor
                    view.setCursor(Cursor.DEFAULT_CURSOR);
                    JOptionPane.showMessageDialog(null, "Item Copiado com sucesso");

                }
            }.execute();

        } catch (Exception ex) {
            System.err.println("\tErro ao copiar Formato/Prelim\n" + ex);
        }
    }

    public void tree_click_arquivo() {
        arvore.click_jTree(arvore.getTree(), view);
    }

    public void click_close_frame(Runnable depoisDeFechar) {
        view.setCursor(Cursor.WAIT_CURSOR);
        view.tree_produto.setEnabled(false);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                kill_internal();
                Thread.sleep(2000);
                return null;
            }

            @Override
            protected void done() {

                if (depoisDeFechar != null) {
                    depoisDeFechar.run();
                }

                // Após carregamento, reverter o cursor
                Tabela.linhasComErroDeTempo.clear();
                view.setCursor(Cursor.getDefaultCursor());
                view.tree_produto.setEnabled(true);
            }
        }.execute();

    }

    void rescue_file_open(File file) {
        view.setCursor(Cursor.WAIT_CURSOR);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Init frame
                Thread.sleep(2000);
                arvore.open_file(file, view);
                // Aqui você pode carregar dados pesados, abrir arquivos, etc
                return null;
            }

            @Override
            protected void done() {
                // Após carregamento, reverter o cursor
                view.setCursor(Cursor.getDefaultCursor());
            }
        }.execute();
    }

    public void add_internal(File file) {
        view.pn_inferior_1.setVisible(true);
        view.pn_inferior_2.setVisible(true);
        view.pn_superior_desktop.setVisible(true);

        view.Desktop.setVisible(true);

        view.Desktop.add(tabela);

        try {
            tabela.setMaximum(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        tabela.toFront();
        tabela.requestFocusInWindow();

        apply_tema(config.get("Tema"), view.tree_produto);

        String produto_info = new File(file.getParent()).getName();
        String arquivo_info = file.getName().replaceFirst("[.][^.]+$", "");

        view.lbl_arquivo.setText(produto_info + "  -  " + arquivo_info);

        SwingUtilities.invokeLater(() -> {
            tabela.setSize(view.Desktop.getSize());
            tabela.setVisible(true);
        });

        jInternal = true;
    }

    public void kill_internal() {
        if (tabela.controller.lauda.ver_stts_lauda()) {
            tabela.controller.lauda.fechar_editor_lauda(tabela.inews, tabela.pn_fundo);
        }
        view.pn_inferior_1.setVisible(false);
        view.pn_inferior_2.setVisible(false);
        view.pn_superior_desktop.setVisible(false);

        tabela.controller.close_pop_up();

        Funcoes.acao_save_backup(tabela.inews, tabela.file, tabela.controller);

        view.Desktop.setVisible(false);
        tabela.setVisible(false);
        tabela.dispose();
        tabela = null;

        jInternal = false;
        config.set("last_file_open", "No file");

    }

    public void salvarUltimoArquivoAberto(File file) {
        config.set("last_file_open", file.getAbsolutePath());
    }

    public void att_coluna_mode() {
        SwingUtilities.invokeLater(() -> {

            view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Thread.sleep(1000);
                    return null;
                }

                @Override
                protected void done() {
                    // Após carregamento, reverter o cursor
                    try {
                        kill_internal();
                        view.setCursor(Cursor.getDefaultCursor());
                        Log.registrarErro_noEx("Frame Reiniciado");
                    } finally {
                        arvore.open_file(backup_file, view);
                    }
                }
            }.execute();

        });
    }

    // Listeneres
    @Override
    public void tempo_entrada(String valor) {
        view.lbl_in_jornal_tempo.setText(valor);
    }

    @Override
    public void tempo_producao() {
        int r = tabela.inews.getRowCount() - 1;
        String acumulado = "00:00:00";
        for (int c = 1; c < r; c++) {
            String valor_tMat_1 = (String) tabela.inews.getValueAt(c, 10);

            valor_tMat_1 = Funcoes.format_ms_to_hms(valor_tMat_1);
            acumulado = Funcoes.soma_tempo(acumulado, valor_tMat_1);
        }

        view.lbl_tempo_producao_tempo.setText(acumulado);
    }

    @Override
    public void tempo_saida() {
        view.lbl_saida_jornal_tempo.setText(Funcoes.soma_tempo(view.lbl_in_jornal_tempo.getText(), view.lbl_tempo_producao_tempo.getText()));
    }

    /**
     *
     * @param table
     */
    @Override
    public void att_tempo(JTable table) {
        switch (tabela.controller.arquivo_info) {
            case "Prelim" ->
                Tabela.tempo_prelim_bo(tabela.inews);
            case "Final" ->
                Tabela.tempo_final(tabela.inews);
            case "BO_CTL1", "BO_CTL2" ->
                Tabela.tempo_prelim_bo(tabela.inews);
        }
    }

    @Override
    public void stts_jornal() {
        LocalTime encerramento = LocalTime.parse(view.lbl_out_jornal_tempo.getText());
        LocalTime saida = LocalTime.parse(view.lbl_saida_jornal_tempo.getText());

        Duration diferenca = Duration.between(encerramento, saida).abs();

        String mensagem;

        Color cor;

        if (encerramento.isAfter(saida)) {
            mensagem = "Buraco " + Funcoes.formatarDuracao(diferenca);
            cor = new Color(0, 0, 255); // Azul
        } else if (encerramento.isBefore(saida)) {
            mensagem = "Estouro " + Funcoes.formatarDuracao(diferenca);
            cor = new Color(255, 51, 51); // Vermelho
        } else {
            mensagem = "OK";
            cor = new Color(0, 153, 0); // Verde
        }

        try {
            String chave = "Tempo_encerramento_" + tabela.controller.produto_info + "_" + tabela.controller.arquivo_info;
            String valor = view.lbl_out_jornal_tempo.getText();
            config.set(chave, valor);

            Font font = new Font("Arial", Font.BOLD, 16);
            view.lbl_stts_jornal_tempo.setText(mensagem);
            view.lbl_stts_jornal_tempo.setForeground(cor);
            view.lbl_stts_jornal_tempo.setFont(font);
        } catch (Exception e) {
            System.err.println("\tErro ao carregar Tempo_encerramento na memória\n");
            e.printStackTrace();
        }
    }

}
