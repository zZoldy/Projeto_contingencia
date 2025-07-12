/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 *
 * @author Z D K
 */
public class Lauda {

    public boolean lauda_aberta = false;

    public String produto;
    public String arquivo;

    JTextPane txt_lauda;
    File file_lauda;

    public Lauda(String produto, String arquivo) {
        this.produto = produto;
        this.arquivo = arquivo;
    }

    File path_lauda() {
        String path_home = System.getProperty("user.dir");
        String path_paste = "exportacoes";
        File path_base = new File(path_home, path_paste);

        String path_subPaste = "Produtos";
        File path_produto = new File(path_base, path_subPaste);

        File path_produto_select = new File(path_produto, produto);

        String path_paste_txt = "Lauda_" + arquivo;
        File path_lauda = new File(path_produto_select, path_paste_txt);
        return path_lauda;
    }

    public boolean ver_stts_lauda() {
        if (lauda_aberta) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.file_lauda))) {
                writer.write(txt_lauda.getText());
                writer.flush();
                System.out.println("Arquivo refatorado");
                return true;
            } catch (IOException e) {
                System.err.println("\tErro ao refatorar aarquivo: \n" + e);
            }
        }
        return false;
    }

    public void lauda(JTable table, JPanel externo) {
        File path_lauda = path_lauda();

        if (!path_lauda.exists()) {
            path_lauda.mkdirs();
        }

        int line_selected = table.getSelectedRow();

        if (line_selected == -1 || line_selected == 0) {
            return;
        }

        String nome_arquivo;
        if (line_selected >= 1 && line_selected <= 9) {
            nome_arquivo = "Line_0" + line_selected + ".txt";
        } else {
            nome_arquivo = "Line_" + line_selected + ".txt";
        }

        File arquivo_reader = new File(path_lauda, nome_arquivo);

        if (ver_stts_lauda()) {
            fechar_editor_lauda(table, externo);
            return;
        }

        try {
            if (!arquivo_reader.exists()) {
                // Cria e escreve o conteúdo da linha
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo_reader))) {
                    bw.flush();
                    System.out.println("Arquivo criado: " + arquivo_reader.getAbsolutePath());
                } catch (Exception e) {
                    System.err.println("\tErro ao Criar Arquivo");
                }
            }

            abrir_editor_lauda(arquivo_reader, table, externo);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void abrir_editor_lauda(File file, JTable table, JPanel externo) {
        // Cria o label com o nome da lauda
        JLabel text_select_lauda = new JLabel(file.getName());
        text_select_lauda.setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane scrollTabela = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, table);

        JPanel painelSuperior = new JPanel(new BorderLayout());
        // Painel superior contendo a tabela e o texto
        painelSuperior.add(scrollTabela, BorderLayout.CENTER);
        painelSuperior.add(text_select_lauda, BorderLayout.SOUTH);
        

        // Painel inferior: editor de texto
        txt_lauda = new JTextPane();
        txt_lauda.getInputMap().put(KeyStroke.getKeyStroke("alt RIGHT"), "acaoAltDireita");
        txt_lauda.getActionMap().put("acaoAltDireita", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("ALT + → pressionado!");
                ver_stts_lauda();
                fechar_editor_lauda(table, externo);
            }
        });

        try {
            FileReader reader = new FileReader(file);
            txt_lauda.read(reader, null);
            reader.close();
            file_lauda = file;
            lauda_aberta = true;
            txt_lauda.setEditable(true);
        } catch (IOException e) {
            e.printStackTrace();
            if (lauda_aberta == true) {
                file_lauda = null;
                lauda_aberta = false;
            }

        }
        JScrollPane scrollTexto = new JScrollPane(txt_lauda);
        

        // SplitPane com os dois painéis
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, painelSuperior, scrollTexto);

        int select = table.getSelectedRow();

        if (select >= 0) {
            // Força o split no meio do painel
            int alturaTotal = externo.getHeight();
            if (alturaTotal == 0) {
                splitPane.validate();
                alturaTotal = externo.getHeight();
            }
            int divisor = alturaTotal / 2;
            splitPane.setDividerLocation(divisor);

            SwingUtilities.invokeLater(() -> {
                Rectangle rect = table.getCellRect(select, 0, true);
                if (rect != null) {
                    JViewport viewport2 = (JViewport) table.getParent();

                    // Calcula o ponto de rolagem para alinhar a linha selecionada logo acima do divisor
                    int deslocamentoY = divisor - rect.height;
                    int novaY = rect.y - deslocamentoY + 45;
                    System.out.println("N: " + novaY);

                    if (novaY < 0) {
                        novaY = 0; // Evita scroll negativo
                    }
                    viewport2.setViewPosition(new Point(rect.x, novaY));
                }
            });

        }

        // Adiciona no painel principal
        externo.removeAll();
        externo.setLayout(new BorderLayout());
        externo.add(splitPane, BorderLayout.CENTER);
        externo.revalidate();
        externo.repaint();

        table.repaint();
        table.requestFocusInWindow();
    }

    public void fechar_editor_lauda(JTable table, JPanel externo) {
        this.txt_lauda = null;
        this.file_lauda = null;
        lauda_aberta = false;
        externo.removeAll();
        externo.add(new JScrollPane(table));
        externo.revalidate();
        externo.repaint();
        table.requestFocusInWindow();
    }

    public void excluir_lauda(File lauda_selecionado, int row_selection) {
        String format_select = "";
        if (row_selection >= 1 && row_selection <= 9) {
            format_select = "0" + row_selection;
        } else {
            format_select = String.valueOf(row_selection);
        }

        if (lauda_selecionado.getName().equals("Line_" + format_select + ".txt")) {
            System.out.println("Arquivo a ser excluido");
            if (lauda_selecionado.delete()) {
                rename_laudas_in_linha(lauda_selecionado.getName());
            }
        } else {
            System.out.println("Não tem arquivo para ser excluido");
        }
    }

    public void rename_laudas_in_linha(String lauda_string) {
        List<File> laudas = list_laudas();
        List<File> rename_laudas = new ArrayList<>();
        try {
            int n_lauda_ex = Integer.parseInt(lauda_string.replace("Line_", "").replace(".txt", ""));
            System.out.println("Lauda Excluida: " + n_lauda_ex);
            System.out.println("Laudas Size: " + laudas.size());

            for (File lauda : laudas) {
                int lauda_in = Integer.parseInt(lauda.getName().replace("Line_", "").replace(".txt", ""));
                System.out.println("Lauda de Ver: " + lauda_in);
                if (n_lauda_ex < lauda_in) {
                    System.out.println("Lauda de seguência: " + lauda.getName());
                    rename_laudas.add(lauda);
                }
            }

            System.out.println(rename_laudas);

            for (File rename : rename_laudas) {
                int rename_value_int = Integer.parseInt(rename.getName().replace("Line_", "").replace(".txt", ""));
                String rename_value_string = "";
                if (rename_value_int >= 1 && rename_value_int <= 9) {
                    rename_value_string = "0" + (rename_value_int - 1);
                } else {
                    rename_value_string = String.valueOf(rename_value_int - 1);
                    if (Integer.parseInt(rename_value_string) >= 1 && Integer.parseInt(rename_value_string) <= 9) {
                        rename_value_string = "0" + rename_value_string;
                    }
                }
                File new_lauda = new File(path_lauda(), "Line_" + rename_value_string + ".txt");

                rename.renameTo(new_lauda);
                System.out.println("Lauda: " + rename.getName() + " - Renomeada: " + new_lauda.getName());
            }
            // System.out.println("FILE:" + file);
        } catch (Exception e) {
            System.err.println("Erro ao renomear_in: " + e);
        }
    }

    public void rename_laudas_excluir_line(int line_select) {
        List<File> laudas = list_laudas();
        List<File> rename_laudas = new ArrayList<>();

        try {
            System.out.println("Line select: " + line_select);
            for (File lauda : laudas) {
                int lauda_in = Integer.parseInt(lauda.getName().replace("Line_", "").replace(".txt", ""));
                System.out.println("Lauda in: " + lauda_in);
                if (line_select < lauda_in) {
                    System.out.println("Lauda seg: " + lauda.getName());
                    rename_laudas.add(lauda);
                }
            }

            System.out.println("Laudas: " + rename_laudas);

            for (File rename : rename_laudas) {
                int rename_value_int = Integer.parseInt(rename.getName().replace("Line_", "").replace(".txt", ""));
                String rename_value_string = "";
                if (rename_value_int >= 1 && rename_value_int <= 9) {
                    rename_value_string = "0" + (rename_value_int - 1);
                } else {
                    rename_value_string = String.valueOf(rename_value_int - 1);
                    if (Integer.parseInt(rename_value_string) >= 1 && Integer.parseInt(rename_value_string) <= 9) {
                        rename_value_string = "0" + rename_value_string;
                    }
                }
                File new_lauda = new File(path_lauda(), "Line_" + rename_value_string + ".txt");

                rename.renameTo(new_lauda);
                System.out.println("Lauda: " + rename.getName() + " - Renomeada: " + new_lauda.getName());
            }
        } catch (Exception e) {
            System.err.println("Erro ao renomear_out: " + e);
        }
    }

    public void rename_laudas_add_line(int line_select) {
        List<File> laudas = list_laudas();
        List<File> rename_laudas = new ArrayList<>();

        try {
            System.out.println("Line select: " + line_select);
            for (File lauda : laudas) {
                int lauda_in = Integer.parseInt(lauda.getName().replace("Line_", "").replace(".txt", ""));
                System.out.println("Lauda in: " + lauda_in);
                if (lauda_in >= line_select) {
                    System.out.println("Lauda seg: " + lauda.getName());
                    rename_laudas.add(lauda);
                }
            }

            System.out.println("Laudas: " + rename_laudas);

            // Ordena em ordem decrescente para evitar sobrescrever
            rename_laudas.sort((a, b) -> {
                int n1 = Integer.parseInt(a.getName().replace("Line_", "").replace(".txt", ""));
                int n2 = Integer.parseInt(b.getName().replace("Line_", "").replace(".txt", ""));
                return Integer.compare(n2, n1);
            });

            for (File rename : rename_laudas) {
                int numero_atual = Integer.parseInt(rename.getName().replace("Line_", "").replace(".txt", ""));
                int novo_numero = numero_atual + 1;

                String nome_novo = String.format("Line_%02d.txt", novo_numero);
                File destino = new File(path_lauda(), nome_novo);

                System.out.println("Renomeando " + rename.getName() + " → " + destino.getName());
                rename.renameTo(destino);
            }

        } catch (Exception e) {
            System.err.println("Erro ao renomear_out: " + e);
        }
    }

    public List<File> list_laudas() {
        File path_lauda = path_lauda();

        System.out.println("Path: " + path_lauda);

        List<File> laudas = new ArrayList<>();

        File[] arquivos = path_lauda.listFiles((dir, nome) -> nome.toLowerCase().endsWith(".txt"));

        try {
            if (arquivos != null && arquivos.length > 0) {
                laudas.addAll(Arrays.asList(arquivos));
            } else {
                return laudas;
            }
        } catch (Exception e) {
            System.err.println("\nProduto com Lauda Vazia: \n");
        }

        return laudas;
    }

    @Override
    public String toString() {
        return "\n\tLaudas"
                + "\nAberta: " + lauda_aberta
                + "\nProduto: " + produto
                + "\nArquivo: " + arquivo
                + "\nJTextPane: " + txt_lauda
                + "\nFile: " + file_lauda;
    }
}
