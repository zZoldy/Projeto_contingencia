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
import java.time.Duration;

/**
 *
 * @author Z D K
 */
public class C_principal implements Tempo_listener {

    Principal view;
    Tbl_news tbl_news;
    Tree tree;

    boolean jInternal = false;

    public static Properties config = Funcoes.init_properties();

    public C_principal(Principal view) {
        this.view = view;
    }

    public void init() {
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
        
        setTree();
        hora_atual();

        for (String chave : config.stringPropertyNames()) {
            String valor = config.getProperty(chave);
            if (chave.equals("last_file_open")) {
                if (!valor.equals("")) {
                    File last_file_open = new File(valor);
                    if (last_file_open.exists()) {
                        recover_file_open(last_file_open);
                    }
                }
            }
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
            tbl_news.controller.tempo_prelim(tbl_news.tbl_news);
        } else if (tbl_news.info.get(1).equals("Final")) {
            tbl_news.controller.tempo_final(tbl_news.tbl_news);
        }
    
        
        
        view.lbl_tempo_producao_tempo.setText(tbl_news.controller.getTempoProducao());
    
    }

    @Override
    public void attSaidaJornal() {
        view.lbl_out_jornal_tempo.setText(tbl_news.controller.getTempoSaida());
    }

    public void tempoEncerramento() {
        
        
        
        LocalTime encerramento = LocalTime.parse(view.lbl_encerramento_tempo.getText());
        LocalTime saida = LocalTime.parse(view.lbl_out_jornal_tempo.getText());

        Duration diferenca = Duration.between(encerramento, saida);

        String mensagem;

        if (encerramento.isAfter(saida)) {
            mensagem = "Estouro " + formatarDuracao(diferenca.abs());
        } else if (encerramento.isBefore(saida)) {
            mensagem = "Cabeça " + formatarDuracao(diferenca.abs());
        } else {
            mensagem = "OK";
        }

        System.out.println(mensagem);
        view.lbl_stts_jornal_tempo.setText(mensagem);
    }

    private static String formatarDuracao(Duration d) {
        long horas = d.toHours();
        long minutos = d.toMinutesPart();
        long segundos = d.toSecondsPart();

        StringBuilder sb = new StringBuilder();

        if (horas > 0) {
            sb.append(horas).append(horas == 1 ? " h" : " hrs");
        }
        if (horas > 0 && minutos > 0) {
            sb.append(", ");
        }
        if (minutos > 0) {
            sb.append(minutos).append(" mins");
        }
        if ((horas > 0 || minutos > 0) && segundos > 0) {
            sb.append(" e ");
        }
        if (segundos > 0) {
            sb.append(segundos).append(" secs");
        }

        if (horas == 0 && minutos == 0 && segundos == 0) {
            return "0 secs";
        }

        return sb.toString();
    }

    public void info_variaveis() {
        // System.out.println(tree);
        Component[] componentes = view.Desktop.getComponents();
        System.out.println("Componentes no DesktopPane:");
        for (Component c : componentes) {
            System.out.println("- " + c.getClass().getName() + " | Visible: " + c.isVisible());
            System.out.println("Dimension: " + c.getSize());
        }

        System.out.println("PROD: " + tbl_news.controller.tempo_producao);
        System.out.println("IN: " + tbl_news.controller.tempo_entrada);

    }

    public void setTree() {
        tree = new Tree();
        tree.treeModel(view.tree_produto);
    }

    public void process_init_table(MouseEvent evt) {
        NodeTree node_select = tree.node_select(view.tree_produto, evt);

        if (node_select != null) {
            if (!jInternal) {
                tree.setProcess_tree(node_select);
                if (tree.getProcess_tree().isArquivo()) {
                    init_frame();
                }
            }
        } else {
            System.err.println("Click Fora");
        }
    }

    public void init_lines_erro(File file) {
        String produto = new File(file.getParent()).getName();
        String arquivo = file.getName().replaceFirst("[.][^.]+$", "");

        String chave_lines_erro = "LinhasComErro_" + produto + "_" + arquivo;

        for (String chave : config.stringPropertyNames()) {
            if (chave.equals(chave_lines_erro)) {
                String valor = config.getProperty(chave);
                if (valor != null && !valor.trim().isEmpty()) {
                    String[] lines = valor.split(",");
                    for (String line : lines) {
                        try {
                            Table.linhasComErroDeTempo.add(Integer.valueOf(line.trim()));
                        } catch (NumberFormatException ex) {
                            System.err.println("Erro ao converter linha de erro: " + line);
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void recover_file_open(File recover) {
        tbl_news = new Tbl_news();
        tbl_news.controller.setListener(this);

        SwingUtilities.invokeLater(() -> {
            tbl_news.setSize(view.Desktop.getSize());
            view.Desktop.add(tbl_news);
            tbl_news.setVisible(true);
        });

        try {
            Funcoes.processar_arquivo(recover, tbl_news.tbl_news);
            init_lines_erro(recover);
        } catch (Exception e) {
            System.err.println("\tErro ao recuperar arquivo: \n" + e);
            return;
        }

        tree.getProcess_tree().getArquivo().setFile(recover);

        info_file_init_frame();
    }

    void info_file_init_frame() {

        List<String> info_table = new ArrayList<>();

        String produto_info = new File(tree.getProcess_tree().getArquivo().getFile().getParent()).getName();
        String arquivo_info = tree.getProcess_tree().getArquivo().getFile().getName().replaceFirst("[.][^.]+$", "");
        view.lbl_frame_open.setText(produto_info + "-" + arquivo_info);
        tbl_news.controller.lauda = new Lauda(produto_info, arquivo_info);

        info_table.add(produto_info);
        info_table.add(arquivo_info);

        tbl_news.info = info_table;

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

        tbl_news.setVisible(true);
        jInternal = true;
    }

    public void init_frame() {

        if (!jInternal) {
            tbl_news = new Tbl_news();
            tbl_news.controller.setListener(this);

            tbl_news.setSize(view.Desktop.getSize());

            try {
                Funcoes.processar_arquivo(tree.getProcess_tree().getArquivo().getFile(), tbl_news.tbl_news);
                init_lines_erro(tree.getProcess_tree().getArquivo().getFile());
            } catch (Exception e) {
                System.err.println("\tErro ao processar arquivo: \n" + e);
                return;
            }

            view.Desktop.add(tbl_news);
            info_file_init_frame();

            config = Funcoes.salvarConfiguracao("last_file_open", tree.getProcess_tree().getArquivo().getFile().getPath());

        }
    }

    public void close_frame() {
        if (jInternal) {
            try {
                Funcoes.save_file(tbl_news.tbl_news, tree.getProcess_tree().getArquivo().getFile());
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
                Funcoes.save_file(tbl_news.tbl_news, tree.getProcess_tree().getArquivo().getFile());
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
            view.lbl_frame_open.setText("Nenhum arquivo em uso");

            jInternal = false;

            tree.attNode(tree.getProcess_tree());

            config = Funcoes.salvarConfiguracao("last_file_open", "");
            Table.linhasComErroDeTempo.clear();

            tbl_news.dispose();
            tbl_news = null;
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
                System.out.println("Click: " + node_select.getNome());
                popUp.add(pop);
            } else {
                JMenuItem pop = null;
                System.out.println("Click: " + node_select.getNome());

                if (node_select.getNome().equals("Formato")) {
                    pop = new JMenuItem("Enviar para Prelim");
                    pop.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            ver_table_open(tree.getProcess_tree().getArquivo().getFile());

                            File parent_formato = new File(node_select.getArquivo().getFile().getParent());
                            File prelim = new File(parent_formato, "Prelim.csv");

                            try {
                                Funcoes.copy_file(node_select.getArquivo().getFile(), prelim);
                                tree.getProcess_tree().getArquivo().setFile(prelim);
                                init_frame();
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

}
