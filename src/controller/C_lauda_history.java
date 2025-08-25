/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import framework.Funcoes;
import framework.Log;
import java.io.File;
import javax.swing.JList;
import view.Lauda_history;

/**
 *
 * @author Z D K
 */
public class C_lauda_history {

    Lauda_history view;

    String raiz = System.getProperty("user.dir");
    File raizProdutos = new File(raiz + File.separator + "exportacoes" + File.separator + "Produtos");

    public C_lauda_history(Lauda_history view) {
        this.view = view;
    }

    public void listarArquivos(String produto, String formato, JList<String> lista) {
        File pasta = new File(raizProdutos, produto + File.separator + "Backup" + File.separator + "History");

        if (!pasta.exists() || !pasta.isDirectory()) {
            Funcoes.message_error(view, "Pasta não encontrada");
            Log.registrarErro_noEx("Pasta não encontrada: " + produto + " - Arquivo:" + formato);
            lista.setListData(new String[]{});
            return;
        }

        File[] arquivos = pasta.listFiles((dir, nome) -> nome.contains(formato));

        if (arquivos == null || arquivos.length == 0) {
            Funcoes.message_error(view, "Nenhum arquivo");
            Log.registrarErro_noEx("Nenhum arquivo: " + formato + " - na pasta: " + produto);
            lista.setListData(new String[]{});
            return;
        }

        String[] nomes = new String[arquivos.length];
        for (int i = 0; i < arquivos.length; i++) {
            nomes[i] = arquivos[i].getName();
        }

        lista.setListData(nomes);
    }

}
