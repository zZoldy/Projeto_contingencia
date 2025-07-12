/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tables;

import Framework.Funcoes;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Z D K
 */
public class Boletim_ctl2 {

    String[] coluna = {"PG", "EDICAO", "TIPO", "SUBTIPO", "ORIGEM", "RETRANCA", "REP", "LOC", "tCab", "tVT", "tMat", "MODI", "APV", "TEMPO", "ASSUNTO"};
    List<String[]> linhas = new ArrayList<>();

    public Boletim_ctl2(String path, String produto, String arquivo) {
        this.linhas.add(new String[]{"0", "", "", "", "", produto + "  -  " + arquivo, "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""});
        this.linhas.add(new String[]{"1", "", "", "", "", "", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""});
        Funcoes.criarCSV(path, coluna, linhas);
    }
}
