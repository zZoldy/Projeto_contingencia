/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import framework.Funcoes;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Classe responsável pelo gerenciamento dos arquivos de texto chamados
 * "laudas", que estão associados a linhas específicas de uma tabela (JTable)
 * para um determinado produto e arquivo.
 * <p>
 * Permite abrir, editar, salvar, excluir e renomear arquivos de lauda, mantendo
 * a sequência correta ao inserir ou remover linhas na tabela. A interface de
 * edição é integrada com um painel que divide a visualização entre a tabela e o
 * editor de texto.
 * </p>
 *
 * @author Z D K
 */
public class Lauda {

    final int WPM_BASE = 150;

    /**
     * Indica se uma lauda está atualmente aberta no editor.
     */
    public boolean lauda_aberta = false;

    /**
     * Nome do produto associado às laudas.
     */
    public String produto;

    /**
     * Nome do arquivo associado às laudas.
     */
    public String arquivo;

    /**
     * Componente JTextPane usado como editor de texto para a lauda aberta.
     */
    JTextPane txt_lauda;

    /**
     * Arquivo de texto atualmente aberto para edição.
     */
    File file_lauda;

    /**
     * Construtor que inicializa uma instância da classe Lauda para um produto e
     * arquivo específicos.
     *
     * @param produto Nome do produto
     * @param arquivo Nome do arquivo
     */
    public Lauda(String produto, String arquivo) {
        this.produto = produto;
        this.arquivo = arquivo;
    }

    /**
     * Retorna o caminho completo do diretório onde os arquivos de lauda para o
     * produto e arquivo atuais são armazenados.
     *
     * @return Objeto File representando o caminho da pasta das laudas
     */
    public File path_lauda() {
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

    /**
     * Verifica se uma lauda está aberta e, se estiver, salva o conteúdo atual
     * do editor de texto no arquivo associado.
     *
     * @return true se a lauda foi salva com sucesso, false caso contrário
     */
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

    /**
     * Abre ou cria o arquivo de lauda correspondente à linha selecionada da
     * tabela, exibindo o editor de texto integrado no painel externo. Se uma
     * lauda já estiver aberta, salva e fecha antes de abrir uma nova.
     *
     * @param table JTable onde a linha está selecionada
     * @param externo JPanel onde o editor será inserido
     */
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

            SwingUtilities.invokeLater(() -> {
                abrir_editor_lauda(arquivo_reader, table, externo);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Abre o editor de texto para o arquivo de lauda especificado, posiciona a
     * interface dividida entre a tabela e o editor e configura atalhos de
     * teclado.
     *
     * @param file Arquivo de lauda a ser aberto
     * @param table JTable para interação com o editor
     * @param externo JPanel para adicionar o editor e a tabela
     */
    public void abrir_editor_lauda(File file, JTable table, JPanel externo) {
        // Cria o label com o nome da lauda

        // --- validações base ---
        if (file == null || table == null || externo == null) {
            return;
        }

        int l = table.getSelectedRow();

        JScrollPane scrollTabela = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, table);

        Font fonte = new Font("Arial", Font.BOLD, 16);

        JLabel text_select_lauda = new JLabel(table.getValueAt(l, 5).toString(), SwingConstants.RIGHT);
        text_select_lauda.setFont(fonte);

        JLabel wpm = new JLabel("Tempo estimado: 0:00 • 0 palavras", SwingConstants.LEFT);
        wpm.setFont(fonte);

        // Painel superior contendo a tabela e o texto
        JPanel painelLabels = new JPanel(new BorderLayout());
        painelLabels.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        painelLabels.add(wpm, BorderLayout.WEST);
        painelLabels.add(text_select_lauda, BorderLayout.EAST);

        JPanel painelSuperior = new JPanel(new BorderLayout());
        painelSuperior.add(scrollTabela, BorderLayout.CENTER);
        painelSuperior.add(painelLabels, BorderLayout.SOUTH);

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

        atualizarLabelWpm(txt_lauda, wpm, WPM_BASE);
        JScrollPane scrollTexto = new JScrollPane(txt_lauda);

        // SplitPane com os dois painéis
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, painelSuperior, scrollTexto);

        int select = l;

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
                    int novaY = rect.y - deslocamentoY + 50;

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

        txt_lauda.repaint();
        txt_lauda.requestFocusInWindow();
        txt_lauda.setCaretPosition(txt_lauda.getDocument().getLength());

        txt_lauda.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarLabelWpm(txt_lauda, wpm, WPM_BASE);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarLabelWpm(txt_lauda, wpm, WPM_BASE);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarLabelWpm(txt_lauda, wpm, WPM_BASE);
            }
        });
    }

    /**
     * Fecha o editor de lauda, limpando os componentes e atualizando o painel
     * externo para mostrar apenas a tabela.
     *
     * @param table JTable a ser exibida após fechar o editor
     * @param externo JPanel onde a tabela será adicionada
     */
    public void fechar_editor_lauda(JTable table, JPanel externo) {
        this.txt_lauda = null;
        this.file_lauda = null;
        lauda_aberta = false;
        externo.removeAll();
        externo.add(new JScrollPane(table));
        externo.revalidate();
        externo.repaint();
        table.requestFocusInWindow();

        if (Tema.modo_tema.equals("Default")) {
            Tema.jTable_default(table);

        } else if (Tema.modo_tema.equals("Dark")) {
            Tema.jTabel_dark(table);
        } else if (Tema.modo_tema.equals("Star Light")) {
            Tema.jTable_star_light(table);
        }
    }

    /**
     * Exclui o arquivo de lauda correspondente à linha selecionada, se existir,
     * e renomeia os arquivos subsequentes para manter a sequência correta.
     *
     * @param lauda_selecionado Arquivo de lauda a ser excluído
     * @param row_selection Índice da linha selecionada na tabela
     */
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

    /**
     * Renomeia arquivos de lauda sequenciais para ajustar a numeração após
     * exclusão de um arquivo.
     *
     * @param lauda_string Nome do arquivo excluído, usado para referência
     */
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
        } catch (Exception e) {
            System.err.println("Erro ao renomear_in: " + e);
        }
    }

    /**
     * Renomeia arquivos de lauda após exclusão de uma linha na tabela,
     * ajustando a numeração para manter sequência correta.
     *
     * @param line_select Número da linha excluída
     */
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

    /**
     * Renomeia arquivos de lauda após adição de uma nova linha na tabela,
     * deslocando para frente os arquivos existentes para abrir espaço.
     *
     * @param line_select Número da linha adicionada
     */
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

    /**
     * Lista todos os arquivos de lauda presentes na pasta correspondente ao
     * produto e arquivo atuais.
     *
     * @return Lista de objetos File representando os arquivos de lauda
     */
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

    public File lauda_select(List<File> laudas, int select) {
        System.out.println("Org_Laudas: " + laudas);
        File lauda_txt = new File("Não existe");

        String format_select = "";
        if (select >= 1 && select <= 9) {
            format_select = "0" + select;
        } else {
            format_select = String.valueOf(select);
        }

        try {
            for (File file : laudas) {
                if (file.getName().equals("Line_" + format_select + ".txt")) {
                    lauda_txt = file;
                    System.out.println("Select - Linha: " + select);
                    System.out.println("Select - Lauda_txt: " + lauda_txt.getName());
                    return lauda_txt;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao selecionar Lauda: " + e);
        }
        return lauda_txt;
    }

    public void copy_lauda(File origem, String dest) {
        File paste_lauda_prelim = new File(origem.getParent());
        System.out.println("Pasta lauda: " + paste_lauda_prelim);
        File paste_lauda_final = new File(paste_lauda_prelim.getParent(), dest);
        System.out.println("Pasta Produto: " + paste_lauda_final);
        if (!paste_lauda_final.exists()) {
            paste_lauda_final.mkdirs();
        }
        File destino = new File(paste_lauda_final, origem.getName());
        System.out.println("Destino: " + destino.getAbsolutePath());
        Funcoes.copy_file(origem, destino);
    }

    public void ver_file_lauda(String destino) {
        if (file_lauda != null) {
            copy_lauda(file_lauda, destino);
        }
    }

    //
    private void atualizarLabelWpm(JTextPane txt_lauda, JLabel lblWpm, int wpmBase) {
        String texto = txt_lauda.getText();
        int palavras = contarPalavrasPtBr(texto);

        double tempoMin = (wpmBase > 0) ? (palavras / (double) wpmBase) : 0.0;
        int tempoSeg = (int) Math.round(tempoMin * 60.0);

        lblWpm.setText(String.format("Tempo estimado: %s  •  %d palavras",
                formatarDuracaoSegundos(tempoSeg), palavras));
    }

    private int contarPalavrasPtBr(String texto) {
        if (texto == null || texto.isBlank()) {
            return 0;
        }
        BreakIterator it = BreakIterator.getWordInstance(new Locale("pt", "BR"));
        it.setText(texto);
        int count = 0;
        int start = it.first();
        for (int end = it.next(); end != BreakIterator.DONE; start = end, end = it.next()) {
            String token = texto.substring(start, end).trim();
            if (!token.isEmpty() && Character.isLetterOrDigit(token.codePointAt(0))) {
                count++;
            }
        }
        return count;
    }

    private String formatarDuracaoSegundos(int total) {
        if (total <= 0) {
            return "0:00";
        }
        int h = total / 3600;
        int m = (total % 3600) / 60;
        int s = total % 60;
        if (h > 0) {
            return String.format("%d:%02d:%02d", h, m, s);
        }
        return String.format("%d:%02d", m, s);
    }

    /**
     * Representação em String da instância da classe, mostrando o status da
     * lauda, produto, arquivo, componente de edição e arquivo atual.
     *
     * @return String formatada com informações da instância
     */
    @Override
    public String toString() {
        return "\n\tLaudas"
                + "\nAberta: " + lauda_aberta
                + "\nProduto: " + produto
                + "\nArquivo: " + arquivo
                + "\nJTextPane: " + txt_lauda
                + "\nFile: " + file_lauda;
    }

    public String toString_to() {
        return produto + " - " + arquivo;
    }
}
