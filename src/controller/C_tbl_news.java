/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import Framework.Funcoes;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import model.Lauda;
import model.Table;
import view.Tbl_news;
import Listener.Tempo_listener;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

/**
 *
 * @author Z D K
 */
public class C_tbl_news {

    Tbl_news view;

    public Lauda lauda;
    public String tempo_entrada;
    public String tempo_producao;

    Popup popupAtual = null;

    public Tempo_listener listener;

    public C_tbl_news(Tbl_news view) {
        this.view = view;
    }

    public void setListener(Tempo_listener listener) {
        this.listener = listener;
    }

    public String getTempoEntrada() {
        int column = 13;
        if (column >= 0 && column < view.tbl_news.getColumnCount()) {
            tempo_entrada = (String) view.tbl_news.getValueAt(0, column);
        } else {
            tempo_entrada = "00:00:00";
        }

        return tempo_entrada;
    }

    public String getTempoProducao() {
        int r = view.tbl_news.getRowCount() - 1;

        String acumulado = "00:00:00";

        for (int c = 0; c < r; c++) {
            String valor_tMat_1 = (String) view.tbl_news.getValueAt(c, 10);

            valor_tMat_1 = Funcoes.format_ms_to_hms(valor_tMat_1);
            acumulado = Funcoes.soma_tempo(acumulado, valor_tMat_1);
        }

        this.tempo_producao = acumulado;
        return this.tempo_producao;
    }

    public String getTempoSaida() {
        return Funcoes.soma_tempo(tempo_entrada, tempo_producao);
    }

    public void in_celula(JTable tabela) {
        int linha = tabela.getSelectedRow();
        int coluna = tabela.getSelectedColumn();

        if (linha != -1 && coluna != -1) {
            tabela.editCellAt(linha, coluna);
            Component editor = tabela.getEditorComponent();

            if (editor != null) {
                editor.requestFocus();
            }
        }

        if (linha == tabela.getRowCount() - 1) {
            add_linha_last(tabela);
        }
    }

    public void configurarAjusteAutomatico(JTable tabela) {
        if (tabela.getClientProperty("autoResizeConfigured") != null) {
            return;
        }

        ajustar_largura_colunas(tabela);
        tabela.putClientProperty("terminateEditOnFocusLost", true);

        // Atualiza ao editar dados
        tabela.getModel().addTableModelListener(e -> {
            SwingUtilities.invokeLater(() -> ajustar_largura_colunas(tabela));
        });

        // Atualiza ao perder o foco
        tabela.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                ajustar_largura_colunas(tabela);
            }
        });

        // Atualiza ao redimensionar o painel pai
        Container parent = tabela.getParent();
        if (parent != null) {
            parent.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    SwingUtilities.invokeLater(() -> ajustar_largura_colunas(tabela));
                }
            });
        }

        tabela.putClientProperty("autoResizeConfigured", true); // Marca que foi configurado
    }

    public void ajustar_largura_colunas(JTable tabela) {
        if (tabela == null || tabela.getColumnCount() == 0) {
            return;
        }

        final int margem = 10;

        for (int coluna = 0; coluna < tabela.getColumnCount(); coluna++) {
            TableColumn tableColumn = tabela.getColumnModel().getColumn(coluna);
            int larguraMax = 0;

            // Cabeçalho
            TableCellRenderer headerRenderer = tabela.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(tabela, tableColumn.getHeaderValue(), false, false, 0, 0);
            larguraMax = headerComp.getPreferredSize().width;

            // Conteúdo
            for (int linha = 0; linha < tabela.getRowCount(); linha++) {
                TableCellRenderer cellRenderer = tabela.getCellRenderer(linha, coluna);
                Component cellComp = tabela.prepareRenderer(cellRenderer, linha, coluna);
                larguraMax = Math.max(larguraMax, cellComp.getPreferredSize().width);
            }

            tableColumn.setPreferredWidth(larguraMax + margem);
        }
    }

    void add_linha_last(JTable table) {
        DefaultTableModel modelo_tabela = (DefaultTableModel) table.getModel();

        int num_pg = modelo_tabela.getRowCount();

        modelo_tabela.addRow(new Object[]{
            num_pg, "", "", "", "", "", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""
        });
        if (listener != null) {
            listener.onAttTempo();
        }
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
            File arquivosTxt = lauda_select(laudas, linha_selecionada);
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
            listener.onAttTempo();
        }
    }

    public void excluir_linha(JTable table, JPanel externo) {
        int linha_selecionada = table.getSelectedRow();

        int rows = (table.getRowCount() - 1);
        if (rows == 0) {
            this.tempo_producao = "00:00:00";
        }
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
            File arquivosTxt = lauda_select(laudas, linha_selecionada);
            System.out.println("Arquivo TXT Selecionado - " + arquivosTxt.getName());

            if (!arquivosTxt.getName().equals("Não existe")) {
                lauda.excluir_lauda(arquivosTxt, linha_selecionada);
            } else {
                lauda.rename_laudas_excluir_line(linha_selecionada);
            }

        } catch (Exception e) {
            System.err.println("Nenhum arquivo selecioa");
        }

        Set<Integer> novasLinhasErro = new TreeSet<>();
        for (Integer linhaErro : Table.linhasComErroDeTempo) {
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

        Table.linhasComErroDeTempo = novasLinhasErro;
        String line_erro_file = "LinhasComErro_" + view.info.get(0) + "_" + view.info.get(1);
        StringBuilder lines = builder_linhas_erro();

        Funcoes.salvarConfiguracao(C_principal.config, line_erro_file, lines.toString());

        if (listener != null) {
            listener.onAttTempo();
            listener.attSaidaJornal();
            listener.attInTempos(view.info.get(0), view.info.get(1));
        }
    }

    public void att_linha_created(DefaultTableModel modelo, int linha_selecionada) {
        // Seleciona a nova linha inserida
        if (linha_selecionada >= 0 && linha_selecionada < modelo.getRowCount()) {
            view.tbl_news.setRowSelectionInterval(linha_selecionada, linha_selecionada);
        }

        // Atualiza a numeração da coluna 0
        for (int i = 0; i < modelo.getRowCount(); i++) {
            modelo.setValueAt(i, i, 0);
        }
    }

    public void att_linha_delected(DefaultTableModel modelo, int linha_selecionada) {
        // Seleciona nova linha (mantém a seleção no mesmo índice ou anterior)
        int totalLinhas = modelo.getRowCount();
        if (totalLinhas > 0) {
            int novaSelecao = Math.min(linha_selecionada, totalLinhas - 1);
            if (novaSelecao >= 1) {
                view.tbl_news.setRowSelectionInterval(novaSelecao, novaSelecao);
            }
        }

        // Atualiza a numeração da coluna 0
        for (int c = 0; c < modelo.getRowCount(); c++) {
            modelo.setValueAt(c, c, 0);
        }
    }

    public void padRows(DefaultTableModel model) {
        int cols = model.getColumnCount();

        for (int r = 0; r < model.getRowCount(); r++) {
            @SuppressWarnings("unchecked")
            java.util.Vector<Object> row = (java.util.Vector<Object>) model.getDataVector().elementAt(r);

            while (row.size() < cols) {
                row.add(null);   // completa
            }
            while (row.size() > cols) {
                row.remove(row.size() - 1); // poda sobras
            }
        }
    }

    public void ver_lauda(int rowSelect) {
        List<File> arquivosTxt = lauda.list_laudas();
        try {
            File lauda = lauda_select(arquivosTxt, rowSelect);
            System.out.println("Lauda Selecionada: " + lauda.getPath());
        } catch (Exception e) {
            System.err.println("\tErro ao selecionar a Lauda: \n" + e);
        }
    }

    public File lauda_select(List<File> laudas, int select) {
        System.out.println("Org_Laudas: " + laudas);
        File lauda_txt = new File("Não existe");

        String format_select = "";
        if (select >= 1 && select <= 9) {
            format_select = "0" + select;
        } else {
            format_select = String.valueOf(select);
        }

        try {
            for (File file : laudas) {
                if (file.getName().equals("Line_" + format_select + ".txt")) {
                    lauda_txt = file;
                    System.out.println("Select - Linha: " + select);
                    System.out.println("Select - Lauda_txt: " + lauda_txt.getName());
                    return lauda_txt;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao selecionar Lauda: " + e);
        }
        return lauda_txt;
    }

    void tempo_prelim_bo(JTable table) {
        int total_lines = table.getRowCount();
        int selected_line = table.getSelectedRow() + 1;
        for (int c = 1; c < (total_lines - 1); c++) {
            String valor_tempo = (String) table.getValueAt(c, 13);

            String valor_tMat = (String) table.getValueAt(c, 10);
            valor_tMat = Funcoes.format_ms_to_hms(valor_tMat);

            String soma = Funcoes.soma_tempo(valor_tMat, valor_tempo);

            if (selected_line == total_lines) {
                add_linha_last(table);
                table.setValueAt(soma, (c + 1), 13);
                return;
            }
            table.setValueAt(soma, (c + 1), 13);

            // Notifica o ouvinte (o JFrame Principal)
        }
    }

    void tempo_final(JTable table) {
        int total_lines = table.getRowCount();
        int selected_line = table.getSelectedRow() + 1;

        System.out.println("Total: " + total_lines);
        System.out.println("Selected: " + selected_line);

        for (int c = 0; c < (total_lines - 1); c++) {
            String valor_tempo = (String) table.getValueAt(c, 13);

            String valor_tMat = (String) table.getValueAt(c, 10);
            valor_tMat = Funcoes.format_ms_to_hms(valor_tMat);

            String soma = Funcoes.soma_tempo(valor_tMat, valor_tempo);

            if (selected_line == total_lines) {
                add_linha_last(table);
                table.setValueAt(soma, (c + 1), 13);
                return;
            }
            table.setValueAt(soma, (c + 1), 13);

        }
    }

    public void prelim_to_final(JTable table_origem, File file_origem) {
        int row = table_origem.getSelectedRow();
        if (row == -1) {
            Funcoes.message_error("Selecione uma linha para enviar ao Final.");
            return;
        }

        String path_final = file_origem.getParent();
        File file_final = new File(path_final, "Final.csv");
        System.out.println("FIle: " + file_final.getPath());

        if (!file_final.exists()) {
            Funcoes.message_error("Arquivo 'Final' não encontrado: " + file_final.getPath());
            return;
        }

        // Cria tabela temporária e carrega conteúdo do arquivo Final
        JTable tabela_final = new JTable(Table.modelos("Final"));
        Funcoes.processar_arquivo(file_final, tabela_final);

        // Garante que a linha de destino exista
        while (tabela_final.getRowCount() <= row) {
            ((DefaultTableModel) tabela_final.getModel()).addRow(new Object[table_origem.getColumnCount()]);
        }

        // Copia valores da linha selecionada
        for (int col = 0; col < table_origem.getColumnCount(); col++) {
            Object valor = table_origem.getValueAt(row, col);
            tabela_final.setValueAt(valor, row, col);
        }

        // Salva a tabela atualizada
        Funcoes.save_file(tabela_final, file_final);
        System.out.println("Linha " + (row + 1) + " transferida para o arquivo Final.");
        table_origem.setValueAt("*", row, 1);
    }

    public void in_tMat(JTable table) {
        int line = table.getSelectedRow();
        int column = table.getSelectedColumn();

        if (line == -1 || !(column == 8 || column == 9)) {
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
            Table.linhasComErroDeTempo.add(line);
        } else {
            int min = totalSeg / 60;
            int sec = totalSeg % 60;
            tMat = String.format("%02d:%02d", min, sec);
            Table.linhasComErroDeTempo.remove(line);
        }

        String line_erro_file = "LinhasComErro_" + view.info.get(0) + "_" + view.info.get(1);

        List<Integer> linhasOrdenadas = new ArrayList<>(Table.linhasComErroDeTempo);
        Collections.sort(linhasOrdenadas);

        StringBuilder lines = builder_linhas_erro();

        Funcoes.salvarConfiguracao(C_principal.config, line_erro_file, lines.toString());

        table.setValueAt(tMat, line, 10);
        if (listener != null) {
            listener.onAttTempo();
            listener.attSaidaJornal();
            listener.attInTempos(view.info.get(0), view.info.get(1));
        }

    }

    public String add_tempo_entrada(JTable table) {
        int linha = table.getSelectedRow();
        int coluna = table.getSelectedColumn();

        if (linha == 0 && coluna == 13) {
            String valor = (String) table.getValueAt(linha, coluna);
            if (valor != null) {
                this.tempo_entrada = valor;

                // Notifica o ouvinte (o JFrame Principal)
                if (listener != null) {
                    listener.onTempoEntradaAtualizado(valor);
                    listener.attSaidaJornal();
                    listener.attInTempos(view.info.get(0), view.info.get(1));
                }

                return tempo_entrada;
            }
        }
        return "";
    }

    public void tempo_linhas_selecionadas(JTable table) {
        StringBuilder tooltip = new StringBuilder("<html>");
        int rows[] = table.getSelectedRows();
        int columns[] = table.getSelectedColumns();
        if (rows.length == 1) {
            table.setToolTipText(null);
            close_pop_up();
            return;
        }

        String tempo_int = Funcoes.format_ms_to_hms((String) table.getValueAt(rows[0], 10));

        for (int line : rows) {
            int next_line = line + 1;
            if (next_line <= rows[rows.length - 1]) {
                String tempo_next = Funcoes.format_ms_to_hms((String) table.getValueAt(next_line, 10));
                tempo_int = Funcoes.soma_tempo(tempo_int, tempo_next);
            }
        }

        tooltip.append("Tempo: ").append(tempo_int).append("<br>");
        tooltip.append("</html>");

        Font font = new Font("Arial", Font.BOLD, 14);

        JLabel label = new JLabel(tooltip.toString());
        label.setOpaque(true);
        label.setBackground(new Color(255, 255, 210));
        label.setFont(font);
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Última linha da seleção
        int ultimaLinha = rows[rows.length - 1];
        int ultimaColuna = columns[columns.length - 1];

        // Pega o retângulo da última célula da coluna 0 (ou qualquer coluna)
        Rectangle cellRect = table.getCellRect(ultimaLinha, ultimaColuna, true);

        System.out.println("CellRect: " + cellRect);
        Point tablePos = table.getLocationOnScreen();

        int x = tablePos.x + cellRect.x + cellRect.width;
        int y = tablePos.y + cellRect.y + cellRect.height;

        // Cria popup
        PopupFactory factory = PopupFactory.getSharedInstance();
        popupAtual = factory.getPopup(table, label, x, y);
        popupAtual.show();
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

    StringBuilder builder_linhas_erro() {
        List<Integer> linhasOrdenadas = new ArrayList<>(Table.linhasComErroDeTempo);
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

    public void close_pop_up() {
        // Esconde popup anterior
        if (popupAtual != null) {
            popupAtual.hide();
            popupAtual = null;
        }
    }
}
