/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tables;

import Framework.Funcoes;

/**
 *
 * @author Z D K
 */
public class Boletim_ctl1 {

    String[] coluna = {"PG", "EDICAO", "TIPO", "SUBTIPO", "ORIGEM", "RETRANCA", "REP", "LOC", "tCab", "tVT", "tMat", "MODI", "APV", "TEMPO", "ASSUNTO"};
    String[] linha;
    String produto;

    public Boletim_ctl1(String path, String produto) {
        this.produto = produto;
        this.linha = new String[]{"0", "", "", "", "", produto + " - " + Funcoes.data_atual(), "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""};
        Funcoes.criarCSV(path, coluna, null, linha);
    }
}
