/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 *
 * @author Z D K
 */
public class ScrollBar extends BasicScrollBarUI {

    private boolean isHover = false;
    

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();

        if (Tema.modo_tema.equals("Default")) {
            g2.setColor(new Color(180, 190, 200));

        } else {
            g2.setColor(new Color(30, 30, 30));
        }
        g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 10, 10);
        g2.dispose();
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color thumbColor = Tema.modo_tema.equals("Default") ? new Color(30, 30, 30) : new Color(180, 190, 200);
        if (isHover) {
            thumbColor = Tema.modo_tema.equals("Default") ? new Color(30, 30, 30, 100) : new Color(180, 190, 200, 100);// ou escolha uma cor customizada
        }
        g2.setColor(thumbColor);

        g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);

        g2.dispose();
    }

    // Remove os botões de seta
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createInvisibleButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createInvisibleButton();
    }

    private JButton createInvisibleButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        button.setVisible(false);
        return button;
    }

    // Define altura/largura mínimos do thumb
    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(40, 10); // para horizontal, 40px de largura
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        scrollbar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                isHover = true;
                scrollbar.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                isHover = false;
                scrollbar.repaint();
            }
        });
    }

}
