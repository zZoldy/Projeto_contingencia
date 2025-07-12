/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import Framework.CustomTreeRenderer;
import Framework.Funcoes;
import controller.C_principal;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JViewport;

/**
 *
 * @author Z D K
 */
public class Tema {

    final static Color FUNDO_TABLE = Color.LIGHT_GRAY;

    final static Color COLOR_DEFAULT = new Color(255, 255, 255);
    final static Color BORDER_DEFAULT = new Color(255, 255, 255);

    final static Color COLOR_DARK = new Color(30, 30, 30);
    final static Color BORDER_DARK = new Color(30, 30, 30);

    final static Color COLOR_MENTA = new Color(180, 190, 200);

    public static Color header_jtable;

    public static final Color DEFAULT_BACKGROUND_LINE = new Color(200, 200, 200);
    public static final Color DEFAULT_FOREGROUND_LINE = Color.BLACK;

    public static final Color DEFAULT_BACKGROUND_LINE_SELECTED = new Color(200, 200, 200);
    public static final Color DEFAULT_FOREGROUND_LINE_SELECTED = new Color(200, 200, 200);

    public static String modelo_tema;

    JDesktopPane Desktop;

    JPanel superior_1, pn_lateral_esquerdo, inferior_1, inferior_2;

    JLabel lbl_frame_open, lbl_hora_atual, lbl_close, lbl_in_jorn, lbl_in_jornal_tempo, lbl_tempo_producao, lbl_tempo_producao_tempo, lbl_out_jorn, lbl_out_jornal_tempo, lbl_encerramento, lbl_stts_jornal, lbl_stts_jornal_tempo;

    JFormattedTextField lbl_encerramento_tempo;

    JTree tree;

    public JMenuBar mn_superior;

    public static JTable table;

    public Tema(JDesktopPane Desktop, JPanel superior_1, JPanel pn_lateral_esquerdo, JPanel inferior_1, JPanel inferior_2, JLabel lbl_hora_atual, JLabel lbl_frame_open, JLabel lbl_close, JLabel lbl_in_jorn, JLabel lbl_in_jornal_tempo, JLabel lbl_tempo_producao, JLabel lbl_tempo_producao_tempo, JLabel lbl_out_jorn, JLabel lbl_out_jornal_tempo, JLabel lbl_encerramento, JFormattedTextField lbl_encerramento_tempo, JLabel lbl_stts_jornal, JLabel lbl_stts_jornal_tempo, JTree tree, JMenuBar mn_superior) {
        this.Desktop = Desktop;
        this.superior_1 = superior_1;
        this.inferior_1 = inferior_1;
        this.inferior_2 = inferior_2;
        this.pn_lateral_esquerdo = pn_lateral_esquerdo;

        this.lbl_hora_atual = lbl_hora_atual;
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
        modelo_tema = "Default";

        this.Desktop.setBackground(COLOR_MENTA);

        aplicar_cor_panels(COLOR_MENTA);

        aplicar_cor_labels(COLOR_DARK);

        aplicar_cor_jtree(COLOR_MENTA, Color.BLACK, new Color(0, 0, 0, 0), Color.ORANGE);

        aplicar_cor_jmnBar(COLOR_DARK, COLOR_DARK, COLOR_DEFAULT);

        Funcoes.salvarConfiguracao(C_principal.config, "Tema", "Default");

    }

    public static void JTable_default(JTable table) {
        Tema.table = table;
        aplicar_cor_jtable(COLOR_MENTA, COLOR_DARK, COLOR_DEFAULT, BORDER_DEFAULT, COLOR_DEFAULT, COLOR_DARK, new Color(200, 200, 200), COLOR_DARK, new Color(160, 160, 160), Color.orange, new Color(110, 110, 110), Color.orange);

    }

    public void Desktop_dark(Properties config) {
        modelo_tema = "Dark";

        this.Desktop.setBackground(COLOR_DARK);

        aplicar_cor_panels(COLOR_DARK);

        aplicar_cor_labels(COLOR_DEFAULT);

        aplicar_cor_jtree(COLOR_DARK, COLOR_DEFAULT, new Color(0, 0, 0, 0), Color.ORANGE);

        aplicar_cor_jmnBar(COLOR_DEFAULT, COLOR_DEFAULT, COLOR_DARK);

        Funcoes.salvarConfiguracao(C_principal.config, "Tema", "Dark");

    }

    public static void JTable_dark(JTable table) {
        Tema.table = table;
        aplicar_cor_jtable(COLOR_DARK, COLOR_DEFAULT, COLOR_DARK, BORDER_DARK, new Color(80, 80, 80), COLOR_DEFAULT, new Color(30, 30, 30), COLOR_DEFAULT, new Color(120, 120, 120), Color.ORANGE, new Color(70, 70, 70), Color.orange);

    }

    public static void aplicar_cor_jtable(Color fundo_table, Color header_background, Color header_foreground, Color border, Color background_1, Color foreground_1, Color background_2, Color foreground_2, Color background_selected_1, Color foreground_selected_1, Color background_selected_2, Color foreground_selected_2) {
        Container parent = Tema.table.getParent();
        if (parent instanceof JViewport viewport) {
            // Agora você tem acesso ao viewport
            viewport.setBackground(fundo_table); // Exemplo de uso
        }
        Table.renderer_header_table(Tema.table, header_background, header_foreground, border);
        Table.renderer_line_table(Tema.table, background_1, foreground_1, background_2, foreground_2, background_selected_1, foreground_selected_1, background_selected_2, foreground_selected_2);
    }

    public static void def_tema_dark_atalho(JLabel atalho) {
        atalho.setBackground(Color.BLUE);
        atalho.setForeground(Color.WHITE);
    }

    public static void def_tema_default_atalho(JLabel atalho) {
        atalho.setBackground(Color.BLUE);
        atalho.setForeground(Color.BLACK);
    }

    // Aplicar cor nos Paineis Superior 1, 2 e Inferior 1, 2;
    void aplicar_cor_panels(Color color_background) {
        superior_1.setBackground(color_background);

        pn_lateral_esquerdo.setBackground(color_background);

        inferior_1.setBackground(color_background);

        inferior_2.setBackground(color_background);
    }

    void aplicar_cor_labels(Color color_foreground) {
        Font font = new Font("Arial", Font.BOLD, 14);

        this.lbl_hora_atual.setForeground(color_foreground);
        this.lbl_hora_atual.setFont(font);

        this.lbl_frame_open.setForeground(color_foreground);
        this.lbl_frame_open.setFont(font);

        this.lbl_close.setForeground(Color.red);
        this.lbl_close.setFont(font);

        this.lbl_in_jorn.setForeground(color_foreground);
        this.lbl_in_jorn.setFont(font);

        this.lbl_in_jornal_tempo.setForeground(color_foreground);
        this.lbl_in_jornal_tempo.setFont(font);

        this.lbl_tempo_producao.setForeground(color_foreground);
        this.lbl_tempo_producao.setFont(font);

        this.lbl_tempo_producao_tempo.setForeground(color_foreground);
        this.lbl_tempo_producao_tempo.setFont(font);

        this.lbl_out_jorn.setForeground(color_foreground);
        this.lbl_out_jorn.setFont(font);

        this.lbl_out_jornal_tempo.setForeground(color_foreground);
        this.lbl_out_jornal_tempo.setFont(font);

        this.lbl_encerramento.setForeground(color_foreground);
        this.lbl_encerramento.setFont(font);

        this.lbl_encerramento_tempo.setForeground(color_foreground);
        this.lbl_encerramento_tempo.setFont(font);

        if (modelo_tema.equals("Dark")) {
            this.lbl_encerramento_tempo.setBackground(COLOR_DARK);
            this.lbl_encerramento_tempo.setBorder(BorderFactory.createLineBorder(COLOR_DEFAULT));
        } else if (modelo_tema.equals("Default")) {
            this.lbl_encerramento_tempo.setBackground(COLOR_DEFAULT);
            this.lbl_encerramento_tempo.setBorder(BorderFactory.createLineBorder(COLOR_DARK));
        }

        this.lbl_stts_jornal.setForeground(color_foreground);
        this.lbl_stts_jornal.setFont(font);

        this.lbl_stts_jornal_tempo.setFont(font);

        if (this.lbl_stts_jornal_tempo.getText().contains("Buraco")) {
            this.lbl_stts_jornal_tempo.setForeground(Color.YELLOW);
        } else if (this.lbl_stts_jornal_tempo.getText().contains("Estouro")) {
            this.lbl_stts_jornal_tempo.setForeground(Color.RED);
        } else if (this.lbl_stts_jornal_tempo.getText().contains("OK")) {
            this.lbl_stts_jornal_tempo.setForeground(Color.GREEN);
        }

    }

    void aplicar_cor_jtree(Color cor_fundo, Color cor_texto, Color cor_fundo_selecionado, Color cor_texto_selecionado) {
        Font font = new Font("Arial", Font.BOLD, 14);

        this.pn_lateral_esquerdo.setBackground(cor_fundo);
        this.tree.setFont(font);
        this.tree.setBackground(cor_fundo);

        this.tree.setCellRenderer(new CustomTreeRenderer(cor_fundo, cor_texto, cor_fundo_selecionado, cor_texto_selecionado));

    }

    void aplicar_cor_jmnBar(Color fundo, Color background, Color foreground) {
        Font font = new Font("Arial", Font.PLAIN, 14);

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
                menu.setFont(font);
                menu.setOpaque(true);
            }
        }
    }

    @Override
    public String toString() {
        return "\n\tTema: " + modelo_tema;
    }
}
