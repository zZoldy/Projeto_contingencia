/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Listener;

import javax.swing.JTable;

/**
 *
 * @author Z D K
 */
public interface Tabela_to_principal {
 
    void tempo_entrada(String valor);
    
    void tempo_producao();
    
    void tempo_saida();
    
    void att_tempo(JTable table);
    
    void stts_jornal();
}
