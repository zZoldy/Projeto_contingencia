/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Framework;

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
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import model.Table;

public class Funcoes {

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

    public static void iniciar_disparo(int segundos, Runnable acao) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(acao, 0, segundos, TimeUnit.SECONDS);
    }

    public static String data_atual() {
        Date date_atual = new Date();
        SimpleDateFormat data_atual_formatada = new SimpleDateFormat("dd/MM/yyyy");
        return data_atual_formatada.format(date_atual);
    }

    public static void criarCSV(String caminhoArquivo, String[] colunas, List<String[]> linhas, String[] linha) {
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
            if (linha != null) {
                bw.write(String.join(",", linha));
                bw.newLine();

                System.out.println("Arquivo CSV criado com sucesso: " + caminhoArquivo);
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
            properties.setProperty("Tema_desktop", "Default");
            properties.setProperty("Tema_table", "Default");
            properties.setProperty("last_file_open", "");
            try (FileOutputStream fos = new FileOutputStream(config)) {
                properties.store(fos, "Arquivo de configuração gerado");
                System.out.println("✔ Arquivo de configuração criado com valores padrão.");
            } catch (IOException e) {
                System.err.println("❌ Erro ao criar o arquivo de configuração:");
                e.printStackTrace();
            }
        }

        return properties;

    }

    public static Properties salvarConfiguracao(String chave, String valor) {
        Properties config = new Properties();
        File arquivo = new File("config.properties");

        // Se o arquivo já existir, carrega as configs antigas
        if (arquivo.exists()) {
            try (FileInputStream fis = new FileInputStream(arquivo)) {
                config.load(fis);
            } catch (IOException e) {
                System.err.println("Erro ao carregar configurações existentes:");
                e.printStackTrace();
            }
        }

        // Agora adiciona/atualiza a nova chave
        config.setProperty(chave, valor);

        // Salva de volta
        try (FileOutputStream fos = new FileOutputStream(arquivo)) {
            config.store(fos, "Configuração do sistema");
            System.out.println("Configuração salva: " + chave + "=" + valor);
        } catch (IOException e) {
            System.err.println("Erro ao salvar configuração:");
            e.printStackTrace();
        }

        return config;
    }

    public static String load_lines_erro(String chave) {
        Properties config = new Properties();

        return null;
    }

    // Processa um arquivo para definir um modelo de tabela;
    public static void processar_arquivo(File file, JTable tabela) {
        System.out.println("Procee: " + file.getPath());

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

            Table.model_padrao(modelo, tabela);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro geral ao processar o arquivo");
        }
    }

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

}
