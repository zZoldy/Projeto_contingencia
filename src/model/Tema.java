/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import Framework.CustomTreeRenderer;
import Framework.Funcoes;
import java.awt.Color;
import java.io.File;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Z D K
 */
public class Tema {

    public boolean modo_default;

    JPanel superior_1, superior_2, inferior_1, inferior_2;
    JTree tree;

    JTable table;

    Color bg_superior_1, bg_superior_2, bg_inferior_1, bg_inferior_2, bg_table_line, bg_table_header;
    Color fg_superior_1, fg_superior_2, fg_inferior_1, fg_inferior_2, fg_table_line, fg_table_header;

    public Tema() {
    }

    public Tema(boolean modo_default, JPanel superior_1, JPanel superior_2, JPanel inferior_1, JPanel inferior_2, JTree tree, JTable table, Color bg_superior_1, Color bg_superior_2, Color bg_inferior_1, Color bg_inferior_2, Color bg_table_line, Color bg_table_header, Color fg_superior_1, Color fg_superior_2, Color fg_inferior_1, Color fg_inferior_2, Color fg_table_line, Color fg_table_header) {
        this.modo_default = modo_default;
        this.superior_1 = superior_1;
        this.superior_2 = superior_2;
        this.inferior_1 = inferior_1;
        this.inferior_2 = inferior_2;
        this.tree = tree;
        this.table = table;
        this.bg_superior_1 = bg_superior_1;
        this.bg_superior_2 = bg_superior_2;
        this.bg_inferior_1 = bg_inferior_1;
        this.bg_inferior_2 = bg_inferior_2;
        this.bg_table_line = bg_table_line;
        this.bg_table_header = bg_table_header;
        this.fg_superior_1 = fg_superior_1;
        this.fg_superior_2 = fg_superior_2;
        this.fg_inferior_1 = fg_inferior_1;
        this.fg_inferior_2 = fg_inferior_2;
        this.fg_table_line = fg_table_line;
        this.fg_table_header = fg_table_header;

        if (modo_default) {
            Desktopt_default(Color.WHITE, Color.BLACK);
        } else {
            //Desktop_dark(color_back, color_back);
        }
    }

    public void Tema_table(File file, JTable table, Color back_line, Color fore_line, Color back_header, Color fore_header) {
        this.bg_table_line = back_line;
        this.fg_table_line = fore_line;
        this.bg_table_header = back_header;
        this.fg_table_header = fore_header;



    }

    void Desktopt_default(Color color_background, Color color_foreground) {
        aplicar_cor(color_background, color_foreground);
    }

    void Desktop_dark(Color color_background, Color color_foreground) {
        aplicar_cor(color_background, color_foreground);
    }

    void aplicar_cor(Color color_background, Color color_foreground) {
        superior_1.setBackground(color_background);
        superior_1.setForeground(color_foreground);

        superior_2.setBackground(color_background);
        superior_2.setForeground(color_foreground);

        inferior_1.setBackground(color_background);
        inferior_1.setForeground(color_foreground);

        inferior_2.setBackground(color_background);
        inferior_2.setForeground(color_foreground);

        tree.setBackground(color_background);

    }

    @Override
    public String toString() {
        return "\n\tModo: " + modo_default
                + "\nColor_back Superior1: " + superior_1.getBackground()
                + "\nColor_fore Superior2: " + superior_1.getForeground()
                + "\nColor_back  Superior2: " + superior_2.getBackground()
                + "\nColor_fore Superior2: " + superior_2.getForeground()
                + "\nColor_back  inferior_1: " + inferior_1.getBackground()
                + "\nColor_fore inferior_1: " + inferior_1.getForeground()
                + "\nColor_back  inferior_2: " + inferior_2.getBackground()
                + "\nColor_fore inferior_2: " + inferior_2.getForeground();
    }
}
