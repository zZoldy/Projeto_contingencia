package framework;

import java.io.*;
import java.util.Properties;

public class Config {

    File path = new File("exportacoes");

    File file = new File(path, "config.properties");
    private Properties properties;

    public Config() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);
        } catch (IOException e) {
            System.out.println("Arquivo de configuração não encontrado. Criando novo.");
            if (!path.exists()) {
                path.mkdirs();
                if (file.exists()) {

                }
            }
            setValoresPadrao();
            salvarConfigPadrao();
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public void set(String key, String value) {
        synchronized (this) {
            properties.setProperty(key, value);
            salvarConfigPadrao();
        }
    }

    private void setValoresPadrao() {
        properties.setProperty("Tema", "Default");
        properties.setProperty("last_file_open", "No file");
        properties.setProperty("coluna_style", "LAST_COLUMN");
        properties.setProperty("Versao", "1.0.0");
        // Adicione mais padrões se quiser
    }

    /**
     * Método oficial de gravação do arquivo de configuração, seguro para uso
     * interno e para leitura com Properties.load().
     */
    public void salvarConfigPadrao() {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            properties.store(fos, "Arquivo de configuração gerado/atualizado");
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.registrarErro("Erro ao criar Arquivo de Configuração", ex);

        }
    }

    public void resetarParaPadroes() {
        properties.clear();           // Limpa todos os valores atuais
        // Reaplica os valores padrão (defina isso como quiser)
        salvarConfigPadrao();        // Grava no arquivo
    }

    /**
     * Método de exportação "organizada" (apenas para consulta humana). Não usar
     * este arquivo para leitura via Properties.load()!
     */
    public void exportarConfiguracaoOrganizada(File destino) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(destino), true)) {
            // =====================ream(destino), true)) {
            // Configurações Gerais
            // =====================
            writer.println("# ================================");
            writer.println("# Configurações Gerais");
            writer.println("# ================================");
            writer.println();
            writer.println("Tema=" + properties.getProperty("Tema", "Default"));
            writer.println("Coluna_style=" + properties.getProperty("coluna_style", "LAST_COLUMN"));
            writer.println("last_file_open=" + properties.getProperty("last_file_open", "No file"));
            writer.println();

            // =====================
            // Linhas com erro
            // =====================
            writer.println("# ================================");
            writer.println("# Linhas com erro");
            writer.println("# ================================");
            writer.println();
            for (String key : properties.stringPropertyNames()) {
                if (key.startsWith("LinhasComErro_")) {
                    writer.println(key + "=" + properties.getProperty(key, ""));
                }
            }
            writer.println();

            writer.println("# ================================");
            writer.println("# Tempos de Encerramento por Pasta/Arquivo");
            writer.println("# ================================");

            String[] pastas = {"BDBR", "BDDF", "DF1", "DF2", "GCO", "GE"};
            String[] arquivos = {"Final", "Formato", "Prelim"};

            for (String pasta : pastas) {
                writer.println();
                writer.println("# ---- " + pasta + " ----");
                for (String arquivo : arquivos) {
                    String key = "Tempo_encerramento_" + pasta + "_" + arquivo;
                    writer.println(key + "=" + properties.getProperty(key, "00:00:00"));
                }
                // Se DF2, inclui boletins:
                if ("DF2".equals(pasta)) {
                    writer.println("Tempo_encerramento_DF2_BOLETIM_CTL1="
                            + properties.getProperty("Tempo_encerramento_DF2_BOLETIM_CTL1", "00:00:00"));
                    writer.println("Tempo_encerramento_DF2_BOLETIM_CTL2="
                            + properties.getProperty("Tempo_encerramento_DF2_BOLETIM_CTL2", "00:00:00"));
                }
            }
            writer.println();

            Funcoes.open_file_desktop(null, destino);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return file.getAbsolutePath();
    }
}
