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
public class Formato {

    String[] colunas = {"PG", "EDICAO", "TIPO", "SUBTIPO", "ORI", "RETRANCA", "REP", "LOC", "tCab", "tVT", "tMat", "MODI", "APV", "TEMPO", "ASSUNTO"};
    List<String[]> linhas;

    public List<String[]> linhas_format(String produto, String arquivo) {
        List<String[]> l = new ArrayList<>();
        int total_lines = 70;

        for (int c = 0; c < total_lines; c++) {

            switch (c) {
                case 0:
                    l.add(new String[]{String.valueOf(c), "", "", "", "", produto + "  -  " + arquivo, "", "", "", "", "", "", "", "00:00:00", ""});
                    break;

                case 15:
                    l.add(new String[]{String.valueOf(c), "", "", "", "", "***** INTERVALO 1 *****", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""});
                    break;

                case 25:
                    l.add(new String[]{String.valueOf(c), "", "", "", "", "***** INTERVALO 2 *****", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""});
                    break;

                case 35:
                    l.add(new String[]{String.valueOf(c), "", "", "", "", "***** INTERVALO 3 *****", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""});
                    break;

                case 45:
                    l.add(new String[]{String.valueOf(c), "", "", "", "", "***** ENCERRAMENTO *****", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""});
                    break;

                case 46:
                    l.add(new String[]{"46", "", "", "", "", "SAÍDA: ", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""});
                    break;

                case 47:
                    l.add(new String[]{"47", "", "", "", "", "TEMPO de PROD: ", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""});
                    break;

                case 48:
                    l.add(new String[]{"48", "", "", "", "", "ENTRADA JORNAL: ", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""});
                    break;

                case 49:
                    l.add(new String[]{String.valueOf(c), "", "", "", "", "***** STAND - BY *****", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""});
                    break;

                case 61, 62, 63, 64, 65:
                    l.add(new String[]{String.valueOf(c), "", "", "", "", "CHAMADA ", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""});
                    break;

                default:
                    l.add(new String[]{String.valueOf(c), "", "", "", "", "", "", "", "00:00", "00:00", "00:00", "", "", "00:00:00", ""});
                    break;
            }

        }
        return l;
    }

    public Formato(String path, String produto, String arquivo) {
        this.linhas = linhas_format(produto, arquivo);
        try {
            Funcoes.criarCSV(path, colunas, linhas);
        } catch (Exception e) {
            Log.registrarErro("Falha na Criação do Arquivo: " + arquivo+"/"+produto, e);
        }
    }
}
