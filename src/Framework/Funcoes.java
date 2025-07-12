/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Framework;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import model.NodeTree;
import model.Table;
import model.Tema;

public class Funcoes {

// Constantes para blocos e layout 
    private static final int NUM_LINHAS = 2;
    private static final int NUM_COLUNAS = 8;
    private static final int ESPACAMENTO = 10;
    private static final int BLOCO_LARGURA = 140;
    private static final int BLOCO_ALTURA = 30;

    public static void install() {

    }

    public static JLabel addAtalho_inTree_soutPanel(NodeTree node, JPanel destino, List<JLabel> lista, Consumer<JLabel> dragHandler, String tema) {
        // Garante que o layout esteja atualizado
        System.out.println("Largura do painel: " + destino.getWidth() + ", Altura: " + destino.getHeight());

        destino.doLayout();

        System.out.println("Largura do painel: " + destino.getWidth() + ", Altura: " + destino.getHeight());

        File fileDoNode = node.getArquivo().getFile();

        // Verifica duplicata
        for (JLabel lbl : lista) {
            File fileExistente = (File) lbl.getClientProperty("file");
            if (fileExistente != null && fileExistente.equals(fileDoNode)) {
                System.out.println("Atalho já existe para: " + fileDoNode.getName());
                return null;
            }
        }

        // Limite máximo de atalhos
        if (lista.size() >= 8) {
            System.out.println("Limite de 8 atalhos atingido.");
            return null;
        }

        String produto_info = new File(fileDoNode.getParent()).getName();
        String nome_table = node.getNome();

        JLabel atalho;
        if ("BOLETIM_CTL1".equals(nome_table)) {
            atalho = new JLabel("BO_CTL1 - " + produto_info);
        } else if ("BOLETIM_CTL2".equals(nome_table)) {
            atalho = new JLabel("BO_CTL2 - " + produto_info);
        } else {
            atalho = new JLabel(nome_table + " - " + produto_info);
        }

        atalho.setOpaque(true);

        if ("Default".equals(tema)) {
            Tema.def_tema_default_atalho(atalho);
        } else if ("Dark".equals(tema)) {
            Tema.def_tema_dark_atalho(atalho);
        }

        atalho.setHorizontalAlignment(SwingConstants.CENTER);
        atalho.setVerticalAlignment(SwingConstants.CENTER);

        atalho.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        atalho.setSize(BLOCO_LARGURA, BLOCO_ALTURA);

        List<Rectangle> blocos = calcularBlocos(destino.getWidth());

        Set<Rectangle> blocosIndisponiveis = blocos.stream()
                .filter(b -> !blocoVisivel(b, destino))
                .collect(Collectors.toSet());

        Rectangle blocoLivre = null;
        for (Rectangle bloco : blocos) {
            if (blocosIndisponiveis.contains(bloco)) {
                continue;
            }

            boolean ocupado = false;
            for (JLabel existente : lista) {
                if (bloco.intersects(existente.getBounds())) {
                    ocupado = true;
                    break;
                }
            }
            if (!ocupado) {
                blocoLivre = bloco;
                break;
            }
        }

        if (blocoLivre == null) {
            System.out.println("Todos os blocos disponíveis estão ocupados ou fora da tela.");
            return null;
        }

// posiciona com tamanho do blocoLivre...
        atalho.setSize(blocoLivre.width, blocoLivre.height);
        atalho.setLocation(blocoLivre.x, blocoLivre.y);
        atalho.putClientProperty("file", fileDoNode);
        atalho.putClientProperty("nome", nome_table);
        atalho.putClientProperty("relX", (double) blocoLivre.x / destino.getWidth());
        atalho.putClientProperty("relY", (double) blocoLivre.y / destino.getHeight());

        // Popup menu e arrastável
        pop_menu_atalho(destino, atalho, lista, dragHandler);

        return atalho;
    }

    public static void pop_menu_atalho(JPanel destino, JLabel atalho, List<JLabel> lista, Consumer<JLabel> dragHandler) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem removerItem = new JMenuItem("Remover atalho");

        removerItem.addActionListener(e -> {
            destino.remove(atalho);
            lista.remove(atalho);
            destino.revalidate();
            destino.repaint();
        });

        popupMenu.add(removerItem);

        atalho.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupMenu.show(atalho, e.getX(), e.getY());
                }
            }
        });

        if (destino.getClientProperty("resizeListenerAdded") == null) {
            destino.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    reposicionarAtalhosRelativos(destino, lista);
                }
            });
            destino.putClientProperty("resizeListenerAdded", true);
        }

        dragHandler.accept(atalho); // Tornar arrastável
        lista.add(atalho);
        destino.add(atalho);
        destino.revalidate();
        destino.repaint();
    }

    public static void jlabel_arrastado(List<JLabel> atalhos, JLabel label, JPanel panel) {
        final Point[] offset = {null};

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                offset[0] = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Ao soltar o mouse, "gruda" o label no bloco mais próximo e livre
                Rectangle melhorBloco = null;
                double menorDistancia = Double.MAX_VALUE;

                List<Rectangle> blocos = calcularBlocos(panel.getWidth());

                for (Rectangle bloco : blocos) {
                    if (!blocoVisivel(bloco, panel)) {
                        continue;
                    }

                    boolean ocupado = false;

                    for (JLabel outro : atalhos) {
                        if (outro != label && bloco.intersects(outro.getBounds())) {
                            ocupado = true;
                            break;
                        }
                    }
                    if (!ocupado) {
                        // calcula distância do centro do label até centro do bloco
                        int centroLabelX = label.getX() + label.getWidth() / 2;
                        int centroLabelY = label.getY() + label.getHeight() / 2;
                        int centroBlocoX = bloco.x + bloco.width / 2;
                        int centroBlocoY = bloco.y + bloco.height / 2;

                        double dist = Point.distance(centroLabelX, centroLabelY, centroBlocoX, centroBlocoY);
                        if (dist < menorDistancia) {
                            menorDistancia = dist;
                            melhorBloco = bloco;
                        }
                    }
                }

                if (melhorBloco != null) {
                    label.setLocation(melhorBloco.x, melhorBloco.y);
                    // Atualiza propriedades relativas
                    label.putClientProperty("relX", (double) melhorBloco.x / panel.getWidth());
                    label.putClientProperty("relY", (double) melhorBloco.y / panel.getHeight());
                    panel.revalidate();
                    panel.repaint();
                }
            }
        });

        label.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point current = e.getLocationOnScreen();
                SwingUtilities.convertPointFromScreen(current, panel);

                int x = current.x - offset[0].x;
                int y = current.y - offset[0].y;
                int maxX = panel.getWidth() - label.getWidth() / 2;
                int maxY = panel.getHeight() - label.getHeight() / 2;

                x = Math.max(0, Math.min(x, maxX));
                y = Math.max(0, Math.min(y, maxY));

                Rectangle novoBounds = new Rectangle(x, y, label.getWidth(), label.getHeight());
                for (JLabel outro : atalhos) {
                    if (outro != label && outro.getBounds().intersects(novoBounds)) {
                        return;
                    }
                }

                label.setLocation(x, y);
                // Não atualiza relX/Y aqui para só atualizar no "snap" (mouseReleased)
            }
        });
    }

    public static void reposicionarAtalhosRelativos(JPanel destino, List<JLabel> lista) {
        int larguraPainel = destino.getWidth();
        int alturaPainel = destino.getHeight();

        List<Rectangle> blocos = calcularBlocos(larguraPainel);

        // Marca blocos visíveis e blocos ocupados
        Map<JLabel, Rectangle> mapaAtalhoParaBloco = new HashMap<>();

        for (JLabel atalho : lista) {
            Rectangle boundsAtalho = atalho.getBounds();

            // Encontrar o bloco atual do atalho (intersecta)
            Rectangle blocoAtual = null;
            for (Rectangle bloco : blocos) {
                if (bloco.intersects(boundsAtalho)) {
                    blocoAtual = bloco;
                    break;
                }
            }

            if (blocoAtual != null) {
                mapaAtalhoParaBloco.put(atalho, blocoAtual);
            }
        }

        // Lista blocos indisponíveis (fora do painel)
        Set<Rectangle> blocosIndisponiveis = new HashSet<>();
        for (Rectangle bloco : blocos) {
            if (!blocoVisivel(bloco, destino)) {
                blocosIndisponiveis.add(bloco);
            }
        }

        // Mover atalhos que estão em blocos indisponíveis para blocos disponíveis
        for (JLabel atalho : lista) {
            Rectangle blocoAtual = mapaAtalhoParaBloco.get(atalho);
            if (blocoAtual == null) {
                continue;
            }

            if (blocosIndisponiveis.contains(blocoAtual)) {
                // Procurar próximo bloco disponível e livre para mover o atalho
                Rectangle blocoLivre = null;

                for (Rectangle bloco : blocos) {
                    if (!blocosIndisponiveis.contains(bloco)) {
                        boolean ocupado = false;
                        for (JLabel outro : lista) {
                            if (outro != atalho && bloco.intersects(outro.getBounds())) {
                                ocupado = true;
                                break;
                            }
                        }
                        if (!ocupado) {
                            blocoLivre = bloco;
                            break;
                        }
                    }
                }

                if (blocoLivre != null) {
                    atalho.putClientProperty("relX", (double) blocoLivre.x / larguraPainel);
                    atalho.putClientProperty("relY", (double) blocoLivre.y / alturaPainel);
                } else {
                    // Nenhum bloco disponível, deixar o atalho onde está, limitado ao painel
                    int x = Math.max(0, Math.min(atalho.getX(), larguraPainel - atalho.getWidth()));
                    int y = Math.max(0, Math.min(atalho.getY(), alturaPainel - atalho.getHeight()));
                    atalho.setLocation(x, y);
                }
            } else {
                // Bloco disponível, reposiciona normal pelo relX/relY e limita dentro do painel
                Double relX = (Double) atalho.getClientProperty("relX");
                Double relY = (Double) atalho.getClientProperty("relY");

                if (relX != null && relY != null) {
                    int x = (int) (relX * larguraPainel);
                    int y = (int) (relY * alturaPainel);

                    int maxX = destino.getWidth() - destino.getWidth() / 2;
                    int maxY = destino.getHeight() - destino.getHeight() / 2;

                    x = Math.max(0, Math.min(x, maxX));
                    y = Math.max(0, Math.min(y, maxY));

                    atalho.setLocation(x, y);
                }
            }
        }

        destino.revalidate();
        destino.repaint();
    }

    private static List<Rectangle> calcularBlocos(int larguraPainel) {
        List<Rectangle> blocos = new ArrayList<>();

        // Calcula largura total para espaçamento
        int totalEspaco = ESPACAMENTO * (NUM_COLUNAS + 1);

        // Calcula largura disponível para blocos
        int larguraDisponivel = larguraPainel - totalEspaco;

        // Largura de cada bloco para encaixar proporcionalmente
        int larguraBlocoDinamica = larguraDisponivel / NUM_COLUNAS;

        for (int i = 0; i < NUM_LINHAS; i++) {
            for (int j = 0; j < NUM_COLUNAS; j++) {
                int x = ESPACAMENTO + j * (larguraBlocoDinamica + ESPACAMENTO);
                int y = ESPACAMENTO + i * (BLOCO_ALTURA + ESPACAMENTO);
                blocos.add(new Rectangle(x, y, larguraBlocoDinamica, BLOCO_ALTURA));
            }
        }

        return blocos;
    }

    private static boolean blocoVisivel(Rectangle bloco, JPanel painel) {
        int larguraPainel = painel.getWidth();
        int alturaPainel = painel.getHeight();

        // Verifica se metade ou mais do bloco está fora da área do painel na horizontal ou vertical
        int meioX = bloco.x + bloco.width / 2;
        int meioY = bloco.y + bloco.height / 2;

        boolean metadeForaHorizontal = (meioX < 0) || (meioX > larguraPainel);
        boolean metadeForaVertical = (meioY < 0) || (meioY > alturaPainel);

        // Considera indisponível se metade estiver fora horizontal ou vertical
        return !(metadeForaHorizontal || metadeForaVertical);
    }

    // Funções TEMPO
    public static void temporizador_acao(int delayMs, Runnable acao, AtomicBoolean temporizador_disp, String msg) {
        System.out.println("IN Temporizador: " + temporizador_disp);

        if (!temporizador_disp.get()) {
            System.out.println("Ação " + msg + " Bloqueada!");
            System.out.println("IF Temporizador: " + temporizador_disp);
            return;
        }

        temporizador_disp.set(false);
        acao.run();

        javax.swing.Timer timer = new javax.swing.Timer(delayMs, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                temporizador_disp.set(true);
                System.out.println("Ação " + msg + " Desbloqueada");
                System.out.println("Timer Temporizador: " + temporizador_disp);
            }
        });

        timer.setRepeats(false);
        timer.start();
    }

    public static void agendar_acao(String tempo, Runnable acao) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            Date for_tempo = sdf.parse(tempo);
            long milissegundos = for_tempo.getTime() - sdf.parse("00:00:00").getTime();

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    acao.run();
                    timer.cancel();
                }
            }, milissegundos);
        } catch (ParseException e) {
            System.err.println("Erro ao converter Tempo \n" + e);
        }
    }

    public static boolean delay_acao(String tempo, Runnable acao) {
        boolean stts_acao = false;

        if (stts_acao == true) {
            return true;
        }

        return stts_acao;

    }

    public static void iniciar_disparo(int segundos, Runnable acao) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(acao, 0, segundos, TimeUnit.SECONDS);
    }

    public static String data_atual() {
        Date date_atual = new Date();
        SimpleDateFormat data_atual_formatada = new SimpleDateFormat("dd/MM/yyyy");
        return data_atual_formatada.format(date_atual);
    }

    // Processa um arquivo para definir um modelo de tabela;
    public static int parseSafe(String part) {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static int converter_to_segundos(String tempo) {
        String[] partes = tempo.split(":");

        int horas = 0;
        int minutos = 0;
        int segundos = 0;

        if (partes.length == 3) {
            horas = Integer.parseInt(partes[0]);
            minutos = Integer.parseInt(partes[1]);
            segundos = Integer.parseInt(partes[2]);
        } else if (partes.length == 2) {
            minutos = Integer.parseInt(partes[0]);
            segundos = Integer.parseInt(partes[1]);
        } else if (partes.length == 1) {
            segundos = Integer.parseInt(partes[0]);
        }

        return horas * 3600 + minutos * 60 + segundos;
    }

    public static String soma_tempo(String tempo_1, String tempo_2) {
        int segundos_1 = converter_to_segundos(tempo_1);
        int segundos_2 = converter_to_segundos(tempo_2);
        int total_segundos = segundos_1 + segundos_2;

        int horas = total_segundos / 3600;
        int mins = (total_segundos % 3600) / 60;
        int segundos = total_segundos % 60;

        return String.format("%02d:%02d:%02d", horas, mins, segundos);
    }

    public static String soma_mins(String tempo_1, String tempo_2) {
        int segundos_1 = converter_to_segundos(tempo_1);
        int segundos_2 = converter_to_segundos(tempo_2);
        int total_segundos = segundos_1 + segundos_2;

        int horas = total_segundos / 3600;
        int mins = (total_segundos % 3600) / 60;
        int segundos = total_segundos % 60;

        return String.format("%02d:%02d", mins, segundos);
    }

    public static String format_ms_to_hms(String mm_ss) {
        // Converte mm:ss para segundos
        String[] partes = mm_ss.split(":");
        int minutos = partes.length > 0 ? Integer.parseInt(partes[0]) : 0;
        int segundos = partes.length > 1 ? Integer.parseInt(partes[1]) : 0;
        int totalSegundos = minutos * 60 + segundos;

        // Converte totalSegundos para HH:mm:ss
        int horas = totalSegundos / 3600;
        int minutosRestantes = (totalSegundos % 3600) / 60;
        int segundosRestantes = totalSegundos % 60;

        String tempoFormatado = String.format("%02d:%02d:%02d", horas, minutosRestantes, segundosRestantes);
        return tempoFormatado;
    }

    public static String formatarDuracao(Duration d) {
        long horas = d.toHours();
        long minutos = d.toMinutesPart();
        long segundos = d.toSecondsPart();

        return String.format("%02d:%02d:%02d", horas, minutos, segundos);
    }

    public static void init_lines_erro(File file, Properties config) {
        String produto = new File(file.getParent()).getName();
        String arquivo = file.getName().replaceFirst("[.][^.]+$", "");

        String chave_lines_erro = "LinhasComErro_" + produto + "_" + arquivo;

        for (String chave : config.stringPropertyNames()) {
            if (chave.equals(chave_lines_erro)) {
                String valor = config.getProperty(chave);
                if (valor != null && !valor.trim().isEmpty()) {
                    String[] lines = valor.split(",");
                    for (String line : lines) {
                        try {
                            Table.linhasComErroDeTempo.add(Integer.valueOf(line.trim()));
                        } catch (NumberFormatException ex) {
                            System.err.println("Erro ao converter linha de erro: " + line);
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    // Funcoes Arquivo
    public static void processar_arquivo(File file, JTable tabela) {
        if (file == null || !file.exists()) {
            System.out.println("Arquivo não encontrado: " + file.getAbsolutePath());
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linha;
            boolean primeira_linha = true;

            String model_table = file.getName().contains(".") ? file.getName().substring(0, file.getName().lastIndexOf(".")) : file.getName();
            DefaultTableModel modelo = Table.modelos(model_table);

            if (modelo == null) {
                System.err.println("Modelo Nulo");
                return;
            }

            while ((linha = br.readLine()) != null) {
                String[] valores = linha.split(",");

                if (primeira_linha) {
                    for (String coluna : valores) {
                        modelo.addColumn(coluna.trim());
                    }

                    primeira_linha = false;
                } else {
                    Vector<String> row = new Vector<>();
                    for (String valor : valores) {
                        row.add(valor.trim());
                    }
                    modelo.addRow(row);
                }
            }

            tabela.setModel(modelo);

            Table.model_padrao(tabela);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro geral ao processar o arquivo");
        }
    }

    public static void criarCSV(String caminhoArquivo, String[] colunas, List<String[]> linhas) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            // Escreve as colunas (primeira linha)
            bw.write(String.join(",", colunas));
            bw.newLine();

            if (linhas != null) {
                // Escreve cada linha de dados
                for (String[] line : linhas) {
                    bw.write(String.join(",", line));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar arquivo CSV");
            e.printStackTrace();
        }
    }

    public static void copy_file(File copia, File novo) {
        if (copia == null || novo == null || !copia.exists()) {
            System.out.println("Arquivo de origem não encontrado.");
            return;
        }

        try (BufferedReader leitor = new BufferedReader(new FileReader(copia)); BufferedWriter escritor = new BufferedWriter(new FileWriter(novo))) {

            String linha;
            while ((linha = leitor.readLine()) != null) {
                escritor.write(linha);
                escritor.newLine();
            }
            System.out.println("Arquivo copiado com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao copiar o arquivo.");
        }
    }

    public static void save_file(JTable tabela, File file) {
        if (tabela.isEditing()) {
            tabela.getCellEditor().stopCellEditing();
        }

        try {
            String path_completo = file.getPath();

            if (!path_completo.endsWith(".csv")) {
                path_completo += ".csv";
            }

            TableModel modelo = tabela.getModel();
            FileWriter writer = new FileWriter(path_completo);

            // Escreve os Cabeçalhos
            for (int i = 0; i < modelo.getColumnCount(); i++) {
                writer.write(modelo.getColumnName(i) + (i == modelo.getColumnCount() - 1 ? "\n" : ","));
            }

            // Escreve os Dados
            for (int i = 0; i < modelo.getRowCount(); i++) {
                for (int j = 0; j < modelo.getColumnCount(); j++) {
                    Object valor = modelo.getValueAt(i, j);
                    writer.write((valor != null ? valor.toString() : "") + (j == modelo.getColumnCount() - 1 ? "\n" : ","));
                }
            }

            writer.close();
            System.out.println("Tabela salva com Sucesso");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar: " + e.getMessage());
        }
    }

    public static Properties init_properties() {
        Properties properties = new Properties();
        File config = new File("config.properties");

        if (config.exists()) {
            try (FileInputStream fis = new FileInputStream(config)) {
                properties.load(fis);
                System.out.println("Configuração carregada com sucesso.");
            } catch (IOException e) {
                System.err.println("Erro ao ler o arquivo de configuração:");
                e.printStackTrace();
            }
        } else {
            System.out.println("Arquivo de configuração não existe ainda.");
            properties.setProperty("Tema", "Default");
            try (FileOutputStream fos = new FileOutputStream(config)) {
                properties.store(fos, "Arquivo de configuração gerado");
            } catch (IOException e) {
                System.err.println("❌ Erro ao criar o arquivo de configuração:");
                e.printStackTrace();
            }
        }

        return properties;

    }

    public static void salvarConfiguracao(Properties props, String chave, String valor) {
        props.setProperty(chave, valor);
        try (FileOutputStream out = new FileOutputStream("config.properties")) {
            props.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Caixa de Mensagem Interface
    public static void message_error(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean message_confirm(Component parent, String mensagem, String titulo) {
        int resposta = JOptionPane.showConfirmDialog(
                parent,
                mensagem,
                titulo,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        return resposta == JOptionPane.YES_OPTION;
    }
}
