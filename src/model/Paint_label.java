/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;

/**
 *
 * @author Z D K
 */
public class Paint_label extends JLabel {

    @FunctionalInterface
    public interface Desenhador {

        void desenhar(Graphics2D g, int width, int height);
    }

    private Desenhador desenhador;

    public Paint_label(Desenhador desenhador, Dimension size) {
        this.desenhador = desenhador;
        setPreferredSize(size);
        setOpaque(false);
    }

    public void setDesenhador(Desenhador desenhador) {
        this.desenhador = desenhador;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (desenhador != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            desenhador.desenhar(g2, getWidth(), getHeight());

            g2.dispose();

        }
    }

}
