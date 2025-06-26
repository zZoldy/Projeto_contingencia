/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import Framework.Funcoes;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import model.Lauda;
import model.Table;
import view.Tbl_news;

/**
 *
 * @author Z D K
 */
public class C_tbl_news {

    Tbl_news view;
    public File arquivo;
    public Lauda lauda;

    public C_tbl_news(Tbl_news view) {
        this.view = view;
    }

    public void in_celula(JTable tabela) {
        int linha = tabela.getSelectedRow();
        int coluna = tabela.getSelectedColumn();

        if (linha != -1 && coluna != -1) {
            tabela.editCellAt(linha, coluna);
            Component editor = tabela.getEditorComponent();

            if (editor != null) {
                editor.requestFocus();

                TableCellEditor cellEditor = tabela.getCellEditor(linha, coluna);
                if (cellEditor != null) {
                    cellEditor.addCellEditorListener(new CellEditorListener() {
                        @Override
                        public void editingStopped(ChangeEvent e) {
                            ajustar_largura_colunas(tabela);
                        }

                        @Override
                        public void editingCanceled(ChangeEvent e) {
                            ajustar_largura_colunas(tabela);
                        }
                    });
                }
            }
        }
    }

    public void ajustar_largura_colunas(JTable tabela) {
        final int margem = 10; // Margem extra para evitar cortes

        for (int coluna = 0; coluna < tabela.getColumnCount(); coluna++) {
            TableColumn tableColumn = tabela.getColumnModel().getColumn(coluna);
            int larguraMax = 0;

            // Verifica o tamanho do cabeçalho
            TableCellRenderer headerRenderer = tabela.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(tabela, tableColumn.getHeaderValue(), false, false, 0, 0);
            larguraMax = headerComp.getPreferredSize().width;

            // Verifica o tamanho do conteúdo
            for (int linha = 0; linha < tabela.getRowCount(); linha++) {
                TableCellRenderer cellRenderer = tabela.getCellRenderer(linha, coluna);
                Component cellComp = tabela.prepareRenderer(cellRenderer, linha, coluna);
                larguraMax = Math.max(larguraMax, cellComp.getPreferredSize().width);
            }

            // Define a largura final da coluna
            tableColumn.setPreferredWidth(larguraMax + margem);
        }
    }

    void add_linha_last(JTable table) {
        DefaultTableModel modelo_tabela = (DefaultTableModel) table.getModel();

        int num_pg = modelo_tabela.getRowCount();

        modelo_tabela.addRow(new Object[]{
            num_pg, "", "", "", "", "", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""
        });
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

    }

    public void excluir_linha(JTable table, JPanel externo) {
        if (lauda.lauda_aberta) {
            lauda.ver_stts_lauda();
            lauda.fechar_editor_lauda(table, externo);
        }

        if (view.tbl_news.isEditing()) {
            view.tbl_news.getCellEditor().stopCellEditing();
        }

        DefaultTableModel modelo = (DefaultTableModel) table.getModel();

        int linha_selecionada = table.getSelectedRow();
        if (linha_selecionada == -1 || linha_selecionada == 0) {
            return;
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

            modelo.removeRow(linha_selecionada);
            att_linha_delected(modelo, linha_selecionada);

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
        StringBuilder lines = builder_linhas();
        C_principal.config = Funcoes.salvarConfiguracao(line_erro_file, lines.toString());

        ajustar_largura_colunas(view.tbl_news);
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

    public void add_tempo(JTable table) {
        table.getModel().addTableModelListener((TableModelEvent e) -> {
            int colunaEditada = e.getColumn(); // Obtém a coluna editada
            int linhaEditada = e.getFirstRow(); // Obtém a linha editada

            if (colunaEditada == 8 || colunaEditada == 9) {
                // Obtém os valores das colunas "tCab" e "tVt"
                Object tCabValue = table.getValueAt(linhaEditada, 8);
                Object tVtValue = table.getValueAt(linhaEditada, 9);

                // Faz o parsing manual do tempo mm:ss
                int[] tCabParts = parseMinSec(tCabValue);
                int[] tVtParts = parseMinSec(tVtValue);

                // Soma os tempos em segundos
                int totalSegundos = (tCabParts[0] * 60 + tCabParts[1]) + (tVtParts[0] * 60 + tVtParts[1]);

                String totalTimeFormatted;

                String line_erro_file = "LinhasComErro_" + view.info.get(0) + "_" + view.info.get(1);

                // Se ultrapassar 59:59 (3599 segundos), zera
                if (totalSegundos > (59 * 60 + 59)) {
                    totalTimeFormatted = "00:00";
                    Table.linhasComErroDeTempo.add(linhaEditada);
                } else {
                    int horas = totalSegundos / 60;
                    int minutos = totalSegundos % 60;
                    totalTimeFormatted = String.format("%02d:%02d", horas, minutos);
                    Table.linhasComErroDeTempo.remove(linhaEditada);
                }

                List<Integer> linhasOrdenadas = new ArrayList<>(Table.linhasComErroDeTempo);
                Collections.sort(linhasOrdenadas);

                StringBuilder lines = builder_linhas();

                C_principal.config = Funcoes.salvarConfiguracao(line_erro_file, lines.toString());
                // Atualiza o valor na coluna 10 (tempo total)
                view.tbl_news.setValueAt(totalTimeFormatted, linhaEditada, 10);

                add_tempo_tMat(linhaEditada, totalTimeFormatted, table);

            }
        });
    }

    void add_tempo_tMat(int line_edit, String valor, JTable table) {
        int total_lines = table.getRowCount();
        String last_value = "";

        String format_valor = Funcoes.format_ms_to_hms(valor);
        System.out.println("Valor Formatado: " + format_valor);

        if (valor.equals("00:00")) {
            int next_line = line_edit + 1;
            if (next_line < total_lines) {
                table.setValueAt(format_valor, next_line, 13);
            } else {
                System.out.println("1");
                System.out.println("Próxima linha (" + next_line + ") fora do intervalo da tabela.");
                add_linha_last(table);
                table.setValueAt(format_valor, next_line, 13);
            }
            return;
        }

        for (int i = line_edit; i >= 1; i--) {
            last_value = (String) table.getValueAt(i, 13);
            if (!last_value.equals("00:00:00")) {
                break;
            } else {
                last_value = "00:00:00";
            }
        }

        String calc_tempo_anterior = Funcoes.soma_tempo(last_value, format_valor);

        // Atualiza a linha seguinte, só se não estiver fora do range
        int next_line = line_edit + 1;
        if (next_line < total_lines) {
            table.setValueAt(calc_tempo_anterior, next_line, 13);
        } else {
            System.out.println("2");
            System.out.println("Próxima linha (" + next_line + ") fora do intervalo da tabela.");
            add_linha_last(table);
            table.setValueAt(calc_tempo_anterior, next_line, 13);
        }

        for (int c = line_edit + 1; c < total_lines - 1; c++) {
            String valor_seg_tMat = (String) table.getValueAt(c, 10);
            if (!valor_seg_tMat.equals("00:00")) {
                System.out.println("\n");
                System.out.println("Indice: " + c + " - Valor: " + valor_seg_tMat);
                String format_seg_tMat = Funcoes.format_ms_to_hms(valor_seg_tMat);
                System.out.println("Indice: " + c + " - Valor Formatado: " + format_seg_tMat);

                calc_tempo_anterior = Funcoes.soma_tempo(calc_tempo_anterior, format_seg_tMat);
                System.out.println("Calculo: " + calc_tempo_anterior);

                table.setValueAt(calc_tempo_anterior, (c + 1), 13);

            }
        }
        calc_tempo_anterior = "00:00:00";
    }

    // Função auxiliar para parsing de mm:ss
    private int[] parseMinSec(Object val) {
        if (val == null || val.toString().isEmpty()) {
            return new int[]{0, 0};
        }
        String[] parts = val.toString().trim().split(":");
        int min = parts.length > 0 ? Funcoes.parseSafe(parts[0]) : 0;
        int sec = parts.length > 1 ? Funcoes.parseSafe(parts[1]) : 0;
        return new int[]{min, sec};
    }

    StringBuilder builder_linhas() {
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

}
