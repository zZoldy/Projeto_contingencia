/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import framework.Config;
import framework.Funcoes;
import framework.TimeCellEditor;
import controller.C_tabela;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Z D K
 */
public class Tabela {
    // Conjunto de linhas com erros detectados na coluna de tempo (coluna 10)

    public static Set<Integer> linhasComErroDeTempo = new HashSet<>();

    public Tabela() {

    }

    @Override
    public String toString() {
        return "";
    }

    /**
     * Aplica o modelo e configurações visuais na tabela recebida.
     *
     * @param table JTable a ser configurada
     */
    public static void model_padrao(JTable table) {
        // Define editores de célula personalizados para colunas específicas
        int[] colunasComEditor = {8, 9, 13};
        for (int col : colunasComEditor) {
            // Aplica editor de tempo (HH:mm ou HH:mm:ss conforme coluna)
            if (col < table.getColumnCount()) {
                table.getColumnModel().getColumn(col).setCellEditor(new TimeCellEditor(col == 13 ? "##:##:##" : "##:##"));
            }
        }

        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(40);

        table.setFillsViewportHeight(true);
        table.setDoubleBuffered(true); // garantir pintura rápida
        table.setOpaque(true);
    }

    /**
     * Retorna um modelo de tabela configurado de acordo com o tipo de arquivo.
     *
     * @param arquivo Nome identificador do tipo ("Formato", "Prelim", "Final"
     * etc.)
     * @return Modelo de tabela configurado
     */
    public static DefaultTableModel modelos(String arquivo) {
        DefaultTableModel modelo;

        if (arquivo.equals("Formato")) {
            modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Nenhuma célula editável
                }
            };
        } else if (arquivo.equals("Prelim") || arquivo.equals("Final") || arquivo.equals("BO_CTL1") || arquivo.equals("BO_CTL2")) {
            modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Primeira linha só permite edição nas colunas 5 e 13
                    if (row == 0) {
                        return column == 5 || column == 13;
                    }
                    // Outras linhas, tudo exceto colunas 10 e 13
                    return column != 1 && column != 10 && column != 13;
                }
            };
        } else {
            modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            Funcoes.message_error(null, "Arquivo/Tabela Não Identificado");
        }

        return modelo;
    }

    /**
     * Aplica renderização personalizada ao cabeçalho da tabela.
     *
     * @param tabela JTable que terá o cabeçalho renderizado
     * @param background
     * @param foreground
     * @param border
     */
    public static void renderer_header_table(JTable tabela, Color background, Color foreground) {
        JTableHeader header = tabela.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            Font font = new Font("Arial", Font.BOLD, 12);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setBackground(background);  // Cor de fundo
                label.setForeground(foreground);             // Cor do texto
                label.setFont(font);
                label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(true); // Necessário para aplicar a cor

                return label;
            }
        });
    }

    /**
     * Aplica renderização personalizada às células da tabela, incluindo
     * alternância de cores por linha e detecção de erro de tempo.
     *
     * @param tabela JTable que terá as células renderizadas
     * @param background
     * @param foreground
     * @param background_2
     * @param foreground_2
     * @param background_selected_1
     * @param foreground_selected_1
     * @param background_selected_2
     * @param foreground_selected_2
     */
    public static void renderer_line_table(JTable tabela, Color background, Color foreground, Color background_2, Color foreground_2, Color background_selected_1, Color foreground_selected_1, Color background_selected_2, Color foreground_selected_2) {
        tabela.setOpaque(false);
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            Font font = new Font("Arial", Font.BOLD, 16);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
                // Se for coluna 5, usa JTextArea
                // Demais colunas usam JLabel padrão
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                label.setFont(font);

                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);

                renderer_lines(isSelected, label, row, background, foreground, background_2, foreground_2,
                        background_selected_1, foreground_selected_1, background_selected_2, foreground_selected_2);
                error_calc_tMat(!isSelected, label, column, row, value);

                return label;
            }
        });
    }

    /**
     * Altera a cor de fundo e texto da célula de acordo com a paridade da linha
     * e se está selecionada.
     *
     * @param isSelected Se a célula está selecionada
     * @param cell Componente da célula
     * @param row Índice da linha
     */
    static void renderer_lines(boolean isSelected, Component cell, int row, Color background_1, Color foreground_1, Color background_2, Color foreground_2, Color background_selected_1, Color foreground_selected_1, Color background_selected_2, Color foreground_selected_2) {
        if (!isSelected) {
            if (row % 2 == 0) {
                cell.setBackground(background_1); // linhas pares
                cell.setForeground(foreground_1);
            } else {
                cell.setBackground(background_2); // linhas ímpares
                cell.setForeground(foreground_2);
            }
        } else {
            if (row % 2 == 0) {
                cell.setBackground(background_selected_1); // Selecionado linha par
                cell.setForeground(foreground_selected_1);
            } else {
                cell.setBackground(background_selected_2);// Selecionado linha ímpar
                cell.setForeground(foreground_selected_2);
            }
        }
    }

    /**
     * Aplica cor vermelha nas células da coluna 10 cujo valor for "00:00" e a
     * linha estiver listada como com erro.
     *
     * @param notSelected Se a célula não está selecionada
     * @param cell Componente da célula
     * @param column Índice da coluna
     * @param row Índice da linha
     * @param valor Valor da célula
     */
    static void error_calc_tMat(boolean isSelected, Component cell, int column, int row, Object valor) {
        // Só mexe nas cores se a célula NÃO estiver selecionada
        if (column == 10) {
            String cellText = (valor != null) ? valor.toString().trim() : "";
            if (cellText.equals("00:00") && linhasComErroDeTempo.contains(row)) {
                cell.setBackground(Color.RED);// Marca com vermelho
            }
        }

    }

    /**
     * Ajusta automaticamente a largura de cada coluna de uma {@link JTable} com
     * base no conteúdo visível das células e nos títulos das colunas.
     *
     * - Para colunas com conteúdo, calcula a largura ideal baseada no maior
     * valor renderizado. - Para colunas vazias, usa o tamanho do cabeçalho como
     * referência. - Aplica margem adicional (40px para células, 20px para
     * cabeçalhos).
     *
     * @param tabela A {@link JTable} cuja largura das colunas será ajustada.
     */
    public static void ajustarLarguraColuna(JTable tabela) {
        TableColumnModel columnModel = tabela.getColumnModel();

        for (int col = 0; col < tabela.getColumnCount(); col++) {
            int larguraMax = 50;
            boolean encontrouConteudo = false;

            // Verifica conteúdo das células
            for (int row = 0; row < tabela.getRowCount(); row++) {
                Object value = tabela.getValueAt(row, col);
                if (value != null && !value.toString().trim().isEmpty()) {
                    TableCellRenderer renderer = tabela.getCellRenderer(row, col);
                    Component comp = tabela.prepareRenderer(renderer, row, col);
                    larguraMax = Math.max(larguraMax, comp.getPreferredSize().width + 40);
                    encontrouConteudo = true;
                }
            }

            // Se não encontrou conteúdo, usa o cabeçalho
            if (!encontrouConteudo) {
                TableCellRenderer headerRenderer = tabela.getTableHeader().getDefaultRenderer();
                Component compHeader = headerRenderer.getTableCellRendererComponent(
                        tabela, tabela.getColumnName(col), false, false, 0, col
                );
                larguraMax = Math.max(larguraMax, compHeader.getPreferredSize().width + 20);
            }

            // Aplica largura
            columnModel.getColumn(col).setPreferredWidth(larguraMax);
        }
    }

    public static void tempo_prelim_bo(JTable table) {
        int total_lines = table.getRowCount();
        int selected_line = table.getSelectedRow() + 1;
        for (int c = 1; c < (total_lines - 1); c++) {
            String valor_tempo = (String) table.getValueAt(c, 13);

            String valor_tMat = (String) table.getValueAt(c, 10);
            valor_tMat = Funcoes.format_ms_to_hms(valor_tMat);

            String soma = Funcoes.soma_tempo(valor_tMat, valor_tempo);

            if (selected_line == total_lines) {
                C_tabela.add_linha_last(table);
                tempo_prelim_bo(table);
                return;
            }
            table.setValueAt(soma, (c + 1), 13);
        }
    }

    public static void tempo_final(JTable table) {
        int total_lines = table.getRowCount();
        int selected_line = table.getSelectedRow() + 1;

        for (int c = 0; c < (total_lines - 1); c++) {
            String valor_tempo = (String) table.getValueAt(c, 13);

            String valor_tMat = (String) table.getValueAt(c, 10);
            valor_tMat = Funcoes.format_ms_to_hms(valor_tMat);

            String soma = Funcoes.soma_tempo(valor_tMat, valor_tempo);

            if (selected_line == total_lines) {
                C_tabela.add_linha_last(table);
                tempo_final(table);
                return;
            }
            table.setValueAt(soma, (c + 1), 13);
        }
    }

    public static void resetarLinha(JTable table, int row) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int colunas = table.getColumnCount();

        // Defina os valores "zerados" conforme o seu modelo de dados!
        Object[] valoresZerados = {
            row, // Número da linha (ou "" se preferir)
            "", "", "", "", "", "", "",
            "00:00", "00:00", "00:00", "", "", "00:00:00", ""
        };

        // Se quiser garantir compatibilidade com o número de colunas:
        for (int col = 0; col < colunas; col++) {
            Object valor = (col < valoresZerados.length) ? valoresZerados[col] : "";
            model.setValueAt(valor, row, col);
        }
    }

    public static void tranferLineErro(int row, String produto, Config config) {
        // Transfer linhas com erro para final
        // Constrói a chave para o arquivo final
        String line_erro_final_key = "LinhasComErro_" + produto + "_Final";

        // Lê valor atual salvo
        String existente = config.get(line_erro_final_key);
        Set<Integer> linhas = new HashSet<>();

        // Converte string para set de inteiros
        if (existente != null && !existente.isBlank()) {
            String[] partes = existente.split(",");
            for (String parte : partes) {
                try {
                    linhas.add(Integer.parseInt(parte.trim()));
                } catch (NumberFormatException e) {
                    // ignora valores inválidos
                }
            }
        }

        // Adiciona a nova linha
        linhas.add(row);

        // Constrói nova string ordenada
        List<Integer> ordenado = new ArrayList<>(linhas);
        Collections.sort(ordenado);
        StringBuilder builder = new StringBuilder();
        for (Integer n : ordenado) {
            builder.append(n).append(",");
        }
        if (!ordenado.isEmpty()) {
            builder.setLength(builder.length() - 1); // remove última vírgula
        }

        // Salva novamente no config
        config.set(line_erro_final_key, builder.toString());
        System.out.println("Erros transferidos para Final: " + row);
    }

}
