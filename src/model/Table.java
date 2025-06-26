/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import Framework.TimeCellEditor;
import java.awt.Color;
import java.awt.Component;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

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

        int[] colunasComEditor = {8, 9};
        for (int col : colunasComEditor) {
            if (col < table.getColumnCount()) {
                table.getColumnModel().getColumn(col).setCellEditor(new TimeCellEditor("##:##"));
            }
        }

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(30);

        // 
        renderer_column_tempo(table);
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
                    if (column == 13) {
                        if (row > 0) {
                            return column != 0 && column != 10 && column != 13;
                        }
                    }
                    return column != 0 && column != 10;
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
    
    public static void renderer_column_tempo(JTable tabela) {
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(SwingConstants.CENTER);  // Centraliza

                if (!isSelected) {
                    // Só mexe nas cores se a célula NÃO estiver selecionada
                    if (column == 10) {
                        String cellText = (value != null) ? value.toString().trim() : "";

                        if (cellText.equals("00:00") && linhasComErroDeTempo.contains(row)) {
                            cell.setBackground(Color.RED);
                            cell.setForeground(UIManager.getColor("Table.foreground"));
                        } else {
                            cell.setBackground(UIManager.getColor("Table.background"));
                            cell.setForeground(UIManager.getColor("Table.foreground"));
                        }
                    } else {
                        cell.setBackground(UIManager.getColor("Table.background"));
                        cell.setForeground(UIManager.getColor("Table.foreground"));
                    }
                }

                return cell;
            }
        });
    }
    
    

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }
}
