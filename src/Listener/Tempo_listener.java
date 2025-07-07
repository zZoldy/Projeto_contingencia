/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Listener;

/**
 *
 * @author Z D K
 */
public interface Tempo_listener {

    void onTempoEntradaAtualizado(String tempo);
    
    void attSaidaJornal();
    
    void onAttTempo();
    
    /**
     *
     * @param produto
     * @param arquivo
     */
    void attInTempos(String produto, String arquivo);
}
