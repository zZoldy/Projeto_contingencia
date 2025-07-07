/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import Framework.CustomTreeRenderer;
import Framework.Funcoes;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 *
 * @author Z D K
 */
public class Tema {

    final Color COLOR_DEFAULT = new Color(255, 255, 255);

    public Color header_jtable;

    public final Color DEFAULT_BACKGROUND_LINE = new Color(200, 200, 200);
    public final Color DEFAULT_FOREGROUND_LINE = Color.BLACK;

    public final Color DEFAULT_BACKGROUND_LINE_SELECTED = new Color(200, 200, 200);
    public final Color DEFAULT_FOREGROUND_LINE_SELECTED = new Color(200, 200, 200);

    public String modelo_tema;

    JPanel superior_1, superior_2, pn_lateral_esquerdo, inferior_1, inferior_2;

    JLabel lbl_frame_open, lbl_close, lbl_in_jorn, lbl_in_jornal_tempo, lbl_tempo_producao, lbl_tempo_producao_tempo, lbl_out_jorn, lbl_out_jornal_tempo, lbl_encerramento, lbl_encerramento_tempo, lbl_stts_jornal, lbl_stts_jornal_tempo;

    public JMenuBar mn_superior;

    JTree tree;
    JTable table;

    public Tema(JPanel superior_1, JPanel superior_2, JPanel pn_lateral_esquerdo, JPanel inferior_1, JPanel inferior_2, JLabel lbl_frame_open, JLabel lbl_close, JLabel lbl_in_jorn, JLabel lbl_in_jornal_tempo, JLabel lbl_tempo_producao, JLabel lbl_tempo_producao_tempo, JLabel lbl_out_jorn, JLabel lbl_out_jornal_tempo, JLabel lbl_encerramento, JLabel lbl_encerramento_tempo, JLabel lbl_stts_jornal, JLabel lbl_stts_jornal_tempo, JTree tree, JMenuBar mn_superior) {
        this.superior_1 = superior_1;
        this.superior_2 = superior_2;
        this.inferior_1 = inferior_1;
        this.inferior_2 = inferior_2;
        this.pn_lateral_esquerdo = pn_lateral_esquerdo;

        this.lbl_frame_open = lbl_frame_open;
        this.lbl_close = lbl_close;
        this.lbl_in_jorn = lbl_in_jorn;
        this.lbl_in_jornal_tempo = lbl_in_jornal_tempo;
        this.lbl_tempo_producao = lbl_tempo_producao;
        this.lbl_tempo_producao_tempo = lbl_tempo_producao_tempo;
        this.lbl_out_jorn = lbl_out_jorn;
        this.lbl_out_jornal_tempo = lbl_out_jornal_tempo;
        this.lbl_encerramento = lbl_encerramento;
        this.lbl_encerramento_tempo = lbl_encerramento_tempo;
        this.lbl_stts_jornal = lbl_stts_jornal;
        this.lbl_stts_jornal_tempo = lbl_stts_jornal_tempo;
        this.tree = tree;
        this.mn_superior = mn_superior;
    }

    public void Desktop_default(Properties config) {
        aplicar_cor_panels(COLOR_DEFAULT);

        this.lbl_frame_open.setForeground(Color.BLACK);
        this.lbl_close.setForeground(Color.red);
        this.lbl_in_jorn.setForeground(Color.BLACK);
        this.lbl_in_jornal_tempo.setForeground(Color.BLACK);
        this.lbl_tempo_producao.setForeground(Color.BLACK);
        this.lbl_tempo_producao_tempo.setForeground(Color.BLACK);
        this.lbl_out_jorn.setForeground(Color.BLACK);
        this.lbl_out_jornal_tempo.setForeground(Color.BLACK);

        this.lbl_encerramento.setForeground(Color.BLACK);
        this.lbl_encerramento_tempo.setForeground(Color.BLACK);

        this.lbl_stts_jornal.setForeground(Color.BLACK);
        this.lbl_stts_jornal_tempo.setForeground(Color.BLACK);

        aplicar_cor_jtree(COLOR_DEFAULT, Color.BLACK, Color.BLUE, Color.BLACK);

        aplicar_cor_jmnBar(COLOR_DEFAULT, Color.WHITE, Color.BLACK);

        config = Funcoes.salvarConfiguracao("Tema", "Default");
        modelo_tema = "Default";
    }

    public void Desktop_dark(Properties config) {
        this.superior_1.setBackground(new Color(30, 30, 30));

        this.superior_2.setBackground(new Color(30, 30, 30));

        this.inferior_1.setBackground(new Color(30, 30, 30));
        this.inferior_2.setBackground(new Color(30, 30, 30));

        config = Funcoes.salvarConfiguracao("Tema", "Dark");
        modelo_tema = "Dark";
    }

    // Aplicar cor nos Paineis Superior 1, 2 e Inferior 1, 2;
    void aplicar_cor_panels(Color color_background) {
        superior_1.setBackground(color_background);

        superior_2.setBackground(color_background);

        pn_lateral_esquerdo.setBackground(color_background);

        inferior_1.setBackground(color_background);

        inferior_2.setBackground(color_background);
    }

    void aplicar_cor_jtree(Color cor_fundo, Color cor_texto, Color cor_fundo_selecionado, Color cor_texto_selecionado) {
        this.pn_lateral_esquerdo.setBackground(cor_fundo);
        this.tree.setBackground(cor_fundo);
        this.tree.setCellRenderer(new CustomTreeRenderer(cor_fundo, cor_texto, cor_fundo_selecionado, cor_texto_selecionado));
    }

    void aplicar_cor_jmnBar(Color fundo, Color background, Color foreground) {
        this.mn_superior.setUI(new javax.swing.plaf.basic.BasicMenuBarUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                g.setColor(fundo);
                g.fillRect(0, 0, c.getWidth(), c.getHeight());
            }
        });

        for (int i = 0; i < mn_superior.getMenuCount(); i++) {
            JMenu menu = mn_superior.getMenu(i);
            if (menu != null) {
                menu.setForeground(foreground);
                menu.setBackground(background);
                menu.setOpaque(true);
            }
        }
    }

    public void aplicar_cor_jtable(JTable table, Color fundo_table, Color header_background, Color header_foreground, Color background_1, Color foreground_1, Color background_2, Color foreground_2, Color background_selected_1, Color foreground_selected_1, Color background_selected_2, Color foreground_selected_2) {
        this.table = table;
        Container parent = table.getParent();
        if (parent instanceof JViewport viewport) {
            // Agora você tem acesso ao viewport
            viewport.setBackground(fundo_table); // Exemplo de uso
        }
        Table.renderer_header_table(table, header_background, header_foreground);
        Table.renderer_line_table(table, background_1, foreground_1, background_2, foreground_2, background_selected_1, foreground_selected_1, background_selected_2, foreground_selected_2);
    }

    @Override
    public String toString() {
        return "\nColor_back Superior1: " + superior_1.getBackground()
                + "\nColor_fore Superior2: " + superior_1.getForeground()
                + "\nColor_back  Superior2: " + superior_2.getBackground()
                + "\nColor_fore Superior2: " + superior_2.getForeground()
                + "\nColor_back  inferior_1: " + inferior_1.getBackground()
                + "\nColor_fore inferior_1: " + inferior_1.getForeground()
                + "\nColor_back  inferior_2: " + inferior_2.getBackground()
                + "\nColor_fore inferior_2: " + inferior_2.getForeground();
    }
}
