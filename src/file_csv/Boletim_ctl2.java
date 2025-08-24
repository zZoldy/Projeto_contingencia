/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package file_csv;

import framework.Funcoes;
import framework.Log;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Z D K
 */
public class Boletim_ctl2 {

    String[] colunas = {"PG", "EDICAO", "TIPO", "SUBTIPO", "ORI", "RETRANCA", "REP", "LOC", "tCab", "tVT", "tMat", "MODI", "APV", "TEMPO", "ASSUNTO"};
    List<String[]> linhas = new ArrayList<>();

    public Boletim_ctl2(String path, String produto, String arquivo) {
        this.linhas.add(new String[]{"0", "", "", "", "", produto + "  -  " + arquivo, "", "", "", "", "", "", "", "00:00:00", ""});
        this.linhas.add(new String[]{"1", "", "", "", "", "", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""});
        this.linhas.add(new String[]{"2", "", "", "", "", "", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""});
        try {
            Funcoes.criarCSV(path, colunas, linhas);
        } catch (Exception e) {
            Log.registrarErro("Falha na Criação do Arquivo: " + arquivo + "/" + produto, e);
        }
    }
}
