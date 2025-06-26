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
public class Prelim {

    String[] coluna = {"PG", "EDICAO", "TIPO", "SUBTIPO", "ORIGEM", "RETRANCA", "REP", "LOC", "tCab", "tVT", "tMat", "MODI", "APV", "TEMPO", "ASSUNTO"};
    String[] linha = {"0", "", "", "", "", "", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""};

    public Prelim(String path) {
        Funcoes.criarCSV(path, coluna, null, linha);
    }
}
