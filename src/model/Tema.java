/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import framework.CustomTreeRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JViewport;

/**
 *
 * @author Z D K
 */
public class Tema {

    public static String modo_tema = "Default";

    public static Font font_tree = new Font("Arial", Font.BOLD, 12);
    public static Font font_desktop = new Font("Arial", Font.BOLD, 14);
    public static Font font_horario = new Font("Arial", Font.BOLD, 16);

    public static Color COLOR_DEFAULT = new Color(180, 190, 200);
    public static Color COLOR_DARK = new Color(30, 30, 30);
    public static Color COLOR_WHITE = new Color(255, 255, 255);

    public static void Default(JPanel component, JTree tree) {
        for (Component filhos : component.getComponents()) {
            filhos.setBackground(COLOR_DEFAULT);
            filhos.setForeground(COLOR_DARK);
            def_labels_default(filhos);
        }
        component.repaint();
        component.revalidate();

        tree.setFont(font_tree);
        tree.setBackground(COLOR_DEFAULT);
        tree.setCellRenderer(new CustomTreeRenderer(COLOR_DEFAULT, COLOR_DARK, new Color(0, 0, 0, 0), Color.ORANGE));

        tree.repaint();
        tree.revalidate();

        Tema.modo_tema = "Default";
    }

    public static void def_labels_default(Component component) {
        if (component instanceof Container container) {
            Component[] components = container.getComponents();

            // Entrando no Painel Desktop
            if ("pn_desktop".equals(component.getName())) {
                component.setBackground(COLOR_DEFAULT);
                for (Component filhos : components) {
                    // Entrando no Painel Superior do Desktop - Barra de Titulo
                    if ("pn_superior_desktop".equals(filhos.getName()) && filhos instanceof Container subContainer) {
                        filhos.setBackground(COLOR_DEFAULT);
                        Component[] netos = subContainer.getComponents();
                        for (Component neto : netos) {
                            //Entrando nos Labels/Components do Painel Superior
                            if (neto instanceof JLabel label) {

                                if ("lbl_close".equals(label.getName())) {
                                    label.setBackground(COLOR_DEFAULT);
                                    label.setForeground(Color.red);
                                    label.setFont(font_desktop);
                                    label.setOpaque(true);

                                } else if ("lbl_file_open".equals(label.getName())) {
                                    label.setBackground(COLOR_DEFAULT);
                                    label.setForeground(COLOR_DARK);
                                    label.setFont(font_desktop);
                                    label.setOpaque(true);
                                }
                            }
                        }
                    }
                }
            }

            for (Component filhos : components) {
                if (filhos instanceof JLabel label) {

                    if ("lbl_horario".equals(label.getName())) {
                        label.setBackground(new Color(0, 0, 0));
                        label.setForeground(new Color(0, 204, 0));
                        label.setFont(font_horario);

                    } else if ("lbl_stts_jornal_tempo".equals(label.getName())) {
                        label.setFont(font_horario);
                        continue;
                    } else {
                        label.setBackground(COLOR_DEFAULT);
                        label.setForeground(COLOR_DARK);
                        label.setFont(font_desktop);
                    }

                }
                if (filhos instanceof JFormattedTextField field) {
                    field.setBackground(COLOR_WHITE);
                    field.setForeground(COLOR_DARK);
                    field.setFont(font_desktop);
                }

                Font fonteLblOp = new Font("Arial", Font.BOLD, 10);
                if (container instanceof Container subContainer && "pn_lateral".equals(container.getName())) {
                    System.out.println("Painel Lateral");
                    Component[] filhos_pn_lateral = subContainer.getComponents();
                    for (Component neto : filhos_pn_lateral) {
                        //Entrando nos Labels/Components do Painel Superior
                        if (neto instanceof JLabel label) {
                            if ("lbl_cop".equals(label.getName())) {
                                System.out.println("Lbl cop");
                                label.setOpaque(true);                    // necessário para a cor de fundo aparecer
                                label.setBackground(COLOR_DEFAULT);
                                label.setForeground(COLOR_DARK);
                                label.setFont(fonteLblOp);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void Dark(JPanel component, JTree tree, String style) {
        for (Component filhos : component.getComponents()) {
            filhos.setBackground(COLOR_DARK);
            filhos.setForeground(COLOR_DEFAULT);
            def_labels_dark(filhos);
        }

        tree.setFont(font_tree);
        tree.setBackground(COLOR_DARK);
        tree.setCellRenderer(new CustomTreeRenderer(COLOR_DARK, COLOR_WHITE, new Color(0, 0, 0, 0), Color.ORANGE));

        Tema.modo_tema = style;
    }

    public static void def_labels_dark(Component component) {
        if (component instanceof Container container) {
            Component[] components = container.getComponents();
            // Entrando no Painel Desktop
            if ("pn_desktop".equals(component.getName())) {
                for (Component filhos : components) {
                    // Entrando no Painel Superior do Desktop - Barra de Titulo
                    if ("pn_superior_desktop".equals(filhos.getName()) && filhos instanceof Container subContainer) {
                        filhos.setBackground(COLOR_DARK);
                        Component[] netos = subContainer.getComponents();
                        for (Component neto : netos) {
                            //Entrando nos Labels/Components do Painel Superior
                            if (neto instanceof JLabel label) {

                                if ("lbl_close".equals(label.getName())) {
                                    label.setBackground(COLOR_DARK);
                                    label.setForeground(Color.red);
                                    label.setFont(font_desktop);
                                    label.setOpaque(true);

                                } else if ("lbl_file_open".equals(label.getName())) {
                                    label.setBackground(COLOR_DARK);
                                    label.setForeground(COLOR_WHITE);
                                    label.setFont(font_desktop);
                                    label.setOpaque(true);
                                }
                            }
                        }

                    }
                }
            }
            for (Component filhos : components) {
                if (filhos instanceof JLabel label) {
                    if ("lbl_horario".equals(label.getName())) {
                        label.setBackground(new Color(0, 0, 0));
                        label.setForeground(new Color(0, 204, 0));
                        label.setFont(font_horario);
                    } else if ("lbl_stts_jornal_tempo".equals(label.getName())) {
                        label.setFont(font_horario);
                        continue;
                    } else {
                        label.setBackground(COLOR_DARK);
                        label.setForeground(COLOR_WHITE);
                        label.setFont(font_desktop);
                    }
                }
                if (filhos instanceof JFormattedTextField field) {
                    field.setBackground(COLOR_WHITE);
                    field.setForeground(COLOR_DARK);
                    field.setFont(font_desktop);
                }

                Font fonteLblOp = new Font("Arial", Font.BOLD, 10);
                if (container instanceof Container subContainer && "pn_lateral".equals(container.getName())) {
                    System.out.println("Painel Lateral");
                    Component[] filhos_pn_lateral = subContainer.getComponents();
                    for (Component neto : filhos_pn_lateral) {
                        //Entrando nos Labels/Components do Painel Superior
                        if (neto instanceof JLabel label) {
                            if ("lbl_cop".equals(label.getName())) {
                                System.out.println("Lbl cop");
                                label.setOpaque(true);                    // necessário para a cor de fundo aparecer
                                label.setBackground(COLOR_DARK);
                                label.setForeground(COLOR_WHITE);
                                label.setFont(fonteLblOp);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void jTable_default(JTable table) {
        Container parent = table.getParent();
        if (parent instanceof JViewport viewport) {
            // Agora você tem acesso ao viewport
            viewport.setBackground(COLOR_DEFAULT); // Exemplo de uso
        }

        Tabela.renderer_header_table(table, COLOR_WHITE, COLOR_DARK);
        Tabela.renderer_line_table(table, new Color(200, 200, 200), new Color(0, 0, 0), COLOR_WHITE, new Color(0, 0, 0), new Color(184, 207, 229), new Color(0, 0, 0), new Color(184, 207, 229), new Color(0, 0, 0));

    }

    public static void jTabel_dark(JTable table) {
        Container parent = table.getParent();
        if (parent instanceof JViewport viewport) {
            // Agora você tem acesso ao viewport
            viewport.setBackground(COLOR_DARK); // Exemplo de uso
        }

        Tabela.renderer_header_table(table, COLOR_WHITE, COLOR_DARK);
        Tabela.renderer_line_table(table, new Color(80, 80, 80), COLOR_WHITE, new Color(30, 30, 30), COLOR_WHITE, new Color(184, 207, 229), new Color(0, 0, 0), new Color(184, 207, 229), new Color(0, 0, 0));

    }

    public static void jTable_star_light(JTable table) {
        Container parent = table.getParent();
        if (parent instanceof JViewport viewport) {
            // Agora você tem acesso ao viewport
            viewport.setBackground(COLOR_DARK); // Exemplo de uso
        }

        Tabela.renderer_header_table(table, Color.ORANGE, COLOR_DARK);
        Tabela.renderer_line_table(table, new Color(0, 0, 0), Color.ORANGE, new Color(0, 0, 0), Color.ORANGE, new Color(0, 0, 0, 0), Color.GREEN, new Color(0, 0, 0, 0), Color.GREEN);
    }

}
