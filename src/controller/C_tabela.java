/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import framework.Config;
import framework.Funcoes;
import framework.Log;
import Listener.Tabela_to_principal;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import model.Lauda;
import model.Tabela;
import view.jInternal_tabela;

/**
 *
 * @author Z D K
 */
public class C_tabela {

    public Lauda lauda;
    jInternal_tabela view;

    Config config;
    public String produto_info;
    public String arquivo_info;
    public Tabela_to_principal listener;

    Popup popupAtual = null;

    public C_tabela(jInternal_tabela view, Config config) {
        this.view = view;
        this.config = config;
        produto_info = new File(view.file.getParent()).getName();
        arquivo_info = view.file.getName().replaceFirst("[.][^.]+$", "");

        lauda = new Lauda(produto_info, arquivo_info);
    }

    public void setListener(Tabela_to_principal listener) {
        this.listener = listener;
    }

    // Operação nas Linhas 
    public void key_add_line_insert(JTable table, JPanel externo, AtomicBoolean create) {
        Funcoes.acao_com_trava(() -> {
            table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Thread.sleep(300);
                    return null;
                }

                @Override
                protected void done() {
                    // Após carregamento, reverter o cursor
                    try {
                        add_linha_selected(table, externo);
                        in_tMat(table);
                        table.setCursor(Cursor.getDefaultCursor());
                        table.repaint();

                        Log.registrarErro_noEx("Linha criada com sucesso");
                    } finally {
                        Funcoes.save_file(view.inews, view.file);
                        create.set(true);
                    }

                }
            }.execute();
        }, create);
    }

    public void add_linha_selected(JTable table, JPanel externo) {
        if (lauda.lauda_aberta) {
            lauda.ver_stts_lauda();
            lauda.fechar_editor_lauda(table, externo);
        }

        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        DefaultTableModel modelo_tabela = (DefaultTableModel) table.getModel();

        int linha_selecionada = table.getSelectedRow();

        // Se nenhuma linha estiver selecionada, não faz nada
        if (linha_selecionada == -1) {
            System.out.println("Nenhuma linha selecionada.");
            return;
        }

        int indexInsercao = linha_selecionada + 1;
        int num_pg = indexInsercao;

        List<File> laudas = lauda.list_laudas();

        try {
            File arquivosTxt = lauda.lauda_select(laudas, linha_selecionada);
            System.out.println("Arquivo TXT Selecionado - " + arquivosTxt.getName());

            modelo_tabela.insertRow(indexInsercao, new Object[]{
                num_pg, "", "", "", "", "", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""
            });
            lauda.rename_laudas_add_line(linha_selecionada + 1);

        } catch (Exception e) {
            System.err.println("Nenhum arquivo selecionado");
        }

        att_linha_created(modelo_tabela, linha_selecionada);

        if (listener != null) {
            listener.att_tempo(table);
        }
    }

    public static void add_linha_last(JTable table) {
        DefaultTableModel modelo_tabela = (DefaultTableModel) table.getModel();

        int num_pg = modelo_tabela.getRowCount();

        modelo_tabela.addRow(new Object[]{
            num_pg, "", "", "", "", "", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""
        });
    }

    public void att_linha_created(DefaultTableModel modelo, int linha_selecionada) {
        // Seleciona a nova linha inserida
        if (linha_selecionada >= 0 && linha_selecionada < modelo.getRowCount()) {
            view.inews.setRowSelectionInterval(linha_selecionada, linha_selecionada);
        }

        // Atualiza a numeração da coluna 0
        for (int i = 0; i < modelo.getRowCount(); i++) {
            modelo.setValueAt(i, i, 0);
        }
    }

    public void key_delete_line(JTable table, JPanel externo, AtomicBoolean delete) {
        int linha_selecionada = table.getSelectedRow();
        if (linha_selecionada == 0) {
            return;
        }

        Funcoes.acao_com_trava(() -> {
            table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Thread.sleep(300);
                    return null;
                }

                @Override
                protected void done() {
                    // Após carregamento, reverter o cursor
                    try {
                        excluir_linha(table, externo);
                        in_tMat(table);
                        table.setCursor(Cursor.getDefaultCursor());
                        table.repaint();
                        Log.registrarErro_noEx("Linha Excluída com Sucesso");
                    } finally {
                        Funcoes.save_file(view.inews, view.file);
                        delete.set(true);
                    }

                }
            }.execute();
        }, delete);

    }

    public void excluir_linha(JTable table, JPanel externo) {
        int linha_selecionada = table.getSelectedRow();

        if (lauda.lauda_aberta) {
            lauda.ver_stts_lauda();
            lauda.fechar_editor_lauda(table, externo);
        }

        // Garante que a edição pare antes de excluir
        if (table.isEditing()) {
            TableCellEditor editor = table.getCellEditor();
            if (editor != null) {
                editor.stopCellEditing();
            }
        }

        DefaultTableModel modelo = (DefaultTableModel) table.getModel();

        // Remove a linha apenas se ela ainda existir
        if (linha_selecionada >= 0 && linha_selecionada < modelo.getRowCount()) {
            modelo.removeRow(linha_selecionada);

            att_linha_delected(modelo, linha_selecionada);
        } else {
            System.err.println("Índice inválido para remoção: " + linha_selecionada);
        }

        List<File> laudas = lauda.list_laudas();

        try {
            File arquivosTxt = lauda.lauda_select(laudas, linha_selecionada);
            System.out.println("Arquivo TXT Selecionado - " + arquivosTxt.getName());

            if (!arquivosTxt.getName().equals("Não existe")) {
                lauda.excluir_lauda(arquivosTxt, linha_selecionada);
            } else {
                lauda.rename_laudas_excluir_line(linha_selecionada);
            }

        } catch (Exception e) {
            System.err.println("Nenhum arquivo selecionado");
        }

        Set<Integer> novasLinhasErro = new TreeSet<>();
        for (Integer linhaErro : Tabela.linhasComErroDeTempo) {
            if (linhaErro == linha_selecionada) {
                // Linha removida, não adiciona
                continue;
            } else if (linhaErro > linha_selecionada) {
                // Todas as linhas abaixo da excluída sobem uma posição
                novasLinhasErro.add(linhaErro - 1);
            } else {
                // Linhas acima permanecem iguais
                novasLinhasErro.add(linhaErro);
            }
        }

        Tabela.linhasComErroDeTempo = novasLinhasErro;
        String line_erro_file = "LinhasComErro_" + view.file.getParent() + "_" + view.file.getName();
        StringBuilder lines = builder_linhas_erro();
        config.set(line_erro_file, lines.toString());

        if (listener != null) {
            listener.att_tempo(table);
            listener.stts_jornal();
        }

    }

    public void att_linha_delected(DefaultTableModel modelo, int linha_selecionada) {
        int totalLinhas = modelo.getRowCount();
        int novaSelecao = -1;

        if (totalLinhas > 0) {
            if (linha_selecionada < totalLinhas) {
                // Ainda existe uma linha na mesma posição: seleciona ela (a "próxima" que ocupou o índice)
                novaSelecao = linha_selecionada;
            } else if (linha_selecionada - 1 >= 0) {
                // Era a última linha, seleciona a anterior
                novaSelecao = linha_selecionada - 1;
            }
        }

        if (novaSelecao >= 0 && novaSelecao < totalLinhas) {
            view.inews.setRowSelectionInterval(novaSelecao, novaSelecao);
        }

        // Atualiza a numeração da coluna 0 (começando do 1)
        for (int c = 0; c < totalLinhas; c++) {
            modelo.setValueAt(c, c, 0);
        }
    }

    StringBuilder builder_linhas_erro() {
        List<Integer> linhasOrdenadas = new ArrayList<>(Tabela.linhasComErroDeTempo);
        Collections.sort(linhasOrdenadas);

        StringBuilder linhas = new StringBuilder();
        for (Integer linha : linhasOrdenadas) {
            if (linhas.length() > 0) {
                linhas.append(",");
            }
            linhas.append(linha);
        }

        return linhas;
    }

    // Metodos Tempos
    public void in_tMat(JTable table) {
        int line = table.getSelectedRow();
        System.out.println("Linha Selecionada: " + line);
        int column = table.getSelectedColumn();

        if (line == -1 || line == 0 || !(column == 8 || column == 9)) {
            return;
        }

        Object tCab = table.getValueAt(line, 8);
        Object tVt = table.getValueAt(line, 9);

        int[] cab = parseMinSec(tCab);
        int[] vt = parseMinSec(tVt);

        int totalSeg = (cab[0] * 60 + cab[1]) + (vt[0] * 60 + vt[1]);

        String tMat;
        if (totalSeg > 3599) { // maior que 59:59
            tMat = "00:00";
            Tabela.linhasComErroDeTempo.add(line);
        } else {
            int min = totalSeg / 60;
            int sec = totalSeg % 60;
            tMat = String.format("%02d:%02d", min, sec);
            Tabela.linhasComErroDeTempo.remove(line);
        }

        String line_erro_file = "LinhasComErro_" + produto_info + "_" + arquivo_info;

        List<Integer> linhasOrdenadas = new ArrayList<>(Tabela.linhasComErroDeTempo);
        Collections.sort(linhasOrdenadas);

        StringBuilder lines = builder_linhas_erro();

        config.set(line_erro_file, lines.toString());

        table.setValueAt(tMat, line, 10);

        if (listener != null) {
            listener.att_tempo(table);
            listener.tempo_producao();
            listener.tempo_saida();
            listener.stts_jornal();

        }

    }

    public void prelim_to_final(JTable table_origem, File file_origem) {
        int row = table_origem.getSelectedRow();
        if (row == -1) {
            Funcoes.message_error(view, "Selecione uma linha para enviar ao Final.");
            return;
        }

        String path_final = file_origem.getParent();
        File file_final = new File(path_final, "Final.csv");
        System.out.println("FIle: " + file_final.getPath());

        if (!file_final.exists()) {
            Funcoes.message_error(view, "Arquivo 'Final' não encontrado: " + file_final.getPath());
            return;
        }

        // Cria tabela temporária e carrega conteúdo do arquivo Final
        JTable tabela_final = new JTable(Tabela.modelos("Final"));
        Funcoes.processar_arquivo(file_final, tabela_final);

        int numero = proximoNumeroMarcador(table_origem, row);
        table_origem.setValueAt(numero + " *", row, 1);

        // Garante que a linha de destino exista
        while (tabela_final.getRowCount() <= row) {
            // Use valores padrão se quiser:
            ((DefaultTableModel) tabela_final.getModel()).addRow(
                    new Object[]{"", "", "", "", "", "", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""}
            );
            att_linha_created(((DefaultTableModel) tabela_final.getModel()), row);
        }

        // Copia valores da linha selecionada
        for (int col = 0; col < table_origem.getColumnCount(); col++) {
            Object valor = table_origem.getValueAt(row, col);
            tabela_final.setValueAt(valor, row, col);
        }

        boolean ultima_linha_final = (row == tabela_final.getRowCount() - 1);
        if (ultima_linha_final) {
            add_linha_last(tabela_final);
        }

        if (listener != null) {
            listener.att_tempo(tabela_final);
        }

        // Salva a tabela atualizada
        Funcoes.acao_save_backup(tabela_final, file_final, view.controller);

        File lauda_line = lauda.lauda_select(lauda.list_laudas(), row);
        if (lauda_line.exists()) {
            lauda.copy_lauda(lauda_line, "Lauda_Final");
        } else {
            System.out.println("Linha sem Lauda");
        }

        if (Tabela.linhasComErroDeTempo.contains(row)) {
            System.out.println("Linha com erro");
            Tabela.tranferLineErro(row, produto_info, config);
        }

        System.out.println("Linha " + (row) + " transferida para o arquivo Final.");

    }

    public int proximoNumeroMarcador(JTable tabela, int row) {
        int max = 0;
        Object valor = tabela.getValueAt(row, 1);
        if (valor != null) {
            String texto = valor.toString().trim();
            if (texto.matches("\\d+\\s*\\*")) {
                int num = Integer.parseInt(texto.split("\\*")[0].trim());
                if (num > max) {
                    max = num;
                    System.out.println("Max: " + max);
                }
            }
        }

        return max + 1;
    }

    private int[] parseMinSec(Object val) {
        if (val == null || val.toString().isEmpty()) {
            return new int[]{0, 0};
        }
        String[] parts = val.toString().trim().split(":");
        int min = parts.length > 0 ? Funcoes.parseSafe(parts[0]) : 0;
        int sec = parts.length > 1 ? Funcoes.parseSafe(parts[1]) : 0;
        return new int[]{min, sec};
    }

    // Listeneres
    public void add_tempo_entrada(JTable table) {
        int linha = table.getSelectedRow();
        int coluna = table.getSelectedColumn();

        if (linha == 0 && coluna == 13) {
            String valor = (String) table.getValueAt(linha, coluna);
            if (valor != null) {
                // Notifica o ouvinte (o JFrame Principal)
                if (listener != null) {
                    listener.tempo_entrada(valor);
                    listener.tempo_saida();
                    listener.stts_jornal();
                }
            }
        }
    }

    public String tempo_producao(JTable table) {
        int r = table.getRowCount() - 1;

        String acumulado = "00:00:00";

        for (int c = 1; c < r; c++) {
            String valor_tMat_1 = (String) table.getValueAt(c, 10);

            valor_tMat_1 = Funcoes.format_ms_to_hms(valor_tMat_1);
            acumulado = Funcoes.soma_tempo(acumulado, valor_tMat_1);
        }

        return acumulado;
    }

    public String calculo_linhas_selecionadas(JTable table) {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            return "00:00:00";
        }
        String tempo_int = Funcoes.format_ms_to_hms((String) table.getValueAt(rows[0], 10));
        for (int i = 1; i < rows.length; i++) {
            String tempo_next = Funcoes.format_ms_to_hms((String) table.getValueAt(rows[i], 10));
            tempo_int = Funcoes.soma_tempo(tempo_int, tempo_next);
        }
        return tempo_int;
    }

    public void updatePopupSelecao(JTable table) {
        int[] rows = table.getSelectedRows();
        int[] columns = table.getSelectedColumns();
        if (rows.length <= 1) {
            close_pop_up();
            return;
        }

        String tempo = calculo_linhas_selecionadas(table);
        JLabel label = new JLabel("<html>" + tempo + "</html>");
        label.setOpaque(true);
        label.setBackground(new Color(255, 255, 210));
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Calcula a posição na borda da seleção:
        int ultimaLinha = rows[rows.length - 1];
        int ultimaColuna = columns[columns.length - 1];
        Rectangle cellRect = table.getCellRect(ultimaLinha, ultimaColuna, true);
        Point tablePos = table.getLocationOnScreen();
        int x = tablePos.x + cellRect.x + cellRect.width;
        int y = tablePos.y + cellRect.y + cellRect.height;

        // Fecha popup anterior antes de mostrar novo
        close_pop_up();

        PopupFactory factory = PopupFactory.getSharedInstance();
        popupAtual = factory.getPopup(table, label, x, y);
        popupAtual.show();
    }

    public void close_pop_up() {
        // Esconde popup anterior
        if (popupAtual != null) {
            popupAtual.hide();
            popupAtual = null;
        }
    }

}
