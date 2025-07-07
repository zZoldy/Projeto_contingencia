/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import Framework.TimeCellEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Classe responsável por configurar e customizar a exibição de uma JTable, com
 * base em modelos e renderizações visuais específicas.
 *
 * Utiliza TimeCellEditor para edição customizada de colunas com formato de
 * hora.
 *
 * Autor: Z D K
 */
public class Table {

    // Conjunto de linhas com erros detectados na coluna de tempo (coluna 10)
    public static Set<Integer> linhasComErroDeTempo = new HashSet<>();

    public Table() {

    }

    @Override
    public String toString() {
        return "";
    }

    /**
     * Aplica o modelo e configurações visuais na tabela recebida.
     *
     * @param modelo Modelo de dados para a tabela
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

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(30);
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
        } else if (arquivo.equals("Prelim") || arquivo.equals("Final")) {
            modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Primeira linha só permite edição nas colunas 5 e 13
                    if (row == 0) {
                        return column == 5 || column == 13;
                    }
                    // Outras linhas, tudo exceto colunas 10 e 13
                    return column != 10 && column != 13;
                }
            };
        } else {
            modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column != 10 && column != 13;
                }
            };
        }

        return modelo;
    }

    /**
     * Aplica renderização personalizada ao cabeçalho da tabela.
     *
     * @param tabela JTable que terá o cabeçalho renderizado
     */
    public static void renderer_header_table(JTable tabela, Color background, Color foreground) {
        JTableHeader header = tabela.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setBackground(background);  // Cor de fundo
                label.setForeground(foreground);             // Cor do texto
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(label.getFont().deriveFont(Font.BOLD));
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
     */
    public static void renderer_line_table(JTable tabela, Color background, Color foreground, Color background_2, Color foreground_2, Color background_selected_1, Color foreground_selected_1, Color background_selected_2, Color foreground_selected_2) {
        tabela.setOpaque(false);
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            Font font = new Font("Arial Black", Font.BOLD, 16);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                cell.setFont(font);
                setHorizontalAlignment(SwingConstants.CENTER);  // Centraliza

                renderer_lines(isSelected, cell, row, background, foreground, background_2, foreground_2, background_selected_1, foreground_selected_1, background_selected_2, foreground_selected_2);
                error_calc_tMat(!isSelected, cell, column, row, value); // Aplica destaque de erro se necessário
                return cell;
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
        if (isSelected) {
            // Só mexe nas cores se a célula NÃO estiver selecionada
            if (column == 10) {
                String cellText = (valor != null) ? valor.toString().trim() : "";
                if (cellText.equals("00:00") && linhasComErroDeTempo.contains(row)) {
                    cell.setBackground(Color.RED);// Marca com vermelho
                }
            }

        }
    }
}
