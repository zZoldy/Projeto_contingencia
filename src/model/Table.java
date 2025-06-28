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
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Z D K
 */
public class Table {

    public JTable table;
    public String produto;
    public String modelo;

    public static Set<Integer> linhasComErroDeTempo = new HashSet<>();

    public Table(JTable table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return table.getName() + "\nTable: " + table
                + "\nModelo-Table: " + modelo
                + "\nProduto-Table: " + produto;
    }

    // Defina a tabela em base do modelo
    public static void model_padrao(DefaultTableModel modelo, JTable table) {
        table.setModel(modelo);
        
        int[] colunasComEditor = {8, 9, 13};
        for (int col : colunasComEditor) {
            if (col < table.getColumnCount()) {
                table.getColumnModel().getColumn(col).setCellEditor(new TimeCellEditor(col == 13 ? "##:##:##" : "##:##"));
            }
        }

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(30);

        // 
        renderer_line_table(table);
        renderer_header_table(table);
    }

    public static DefaultTableModel modelos(String arquivo) {
        DefaultTableModel modelo;

        if (arquivo.equals("Formato")) {
            modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
        } else if (arquivo.equals("Prelim") || arquivo.equals("Final")) {
            modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    if (row == 0) {
                        return column == 5 || column == 13;
                    }
                    return column != 0 && column != 10 && column != 13;
                }
            };
        } else {
            modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column != 0 && column != 10;
                }
            };
        }

        return modelo;
    }

    static void renderer_header_table(JTable tabela) {
        JTableHeader header = tabela.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setBackground(new Color(0, 0, 0));  // Cor de fundo
                label.setForeground(Color.WHITE);             // Cor do texto
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(label.getFont().deriveFont(Font.BOLD));
                label.setOpaque(true); // Necessário para aplicar a cor
                return label;
            }
        });
    }

    static void renderer_line_table(JTable tabela) {
        tabela.setOpaque(false);
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(SwingConstants.CENTER);  // Centraliza

                renderer_lines(isSelected, cell, row);
                error_calc_tMat(!isSelected, cell, column, row, value);
                return cell;
            }
        });

    }

    static void renderer_lines(boolean isSelected, Component cell, int row) {
        if (!isSelected) {
            if (row % 2 == 0) {
                cell.setBackground(new Color(80, 80, 80)); // linhas pares
                cell.setForeground(Color.white);
            } else {
                cell.setBackground(new Color(30, 30, 30)); // linhas ímpares
                cell.setForeground(Color.white);
            }
        } else {
            if (row % 2 == 0) {
                cell.setBackground(new Color(140, 140, 140));
                cell.setForeground(Color.white);
            } else {
                cell.setBackground(new Color(90, 90, 90));
                cell.setForeground(Color.white);
            }
        }
    }

    static void error_calc_tMat(boolean isSelected, Component cell, int column, int row, Object valor) {
        if (isSelected) {
            // Só mexe nas cores se a célula NÃO estiver selecionada
            if (column == 10) {
                String cellText = (valor != null) ? valor.toString().trim() : "";

                if (cellText.equals("00:00") && linhasComErroDeTempo.contains(row)) {
                    cell.setBackground(Color.RED);
                }
            }

        }
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }
}
