/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package framework;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import view.jInternal_console;

/**
 *
 * @author Z D K
 */
public class Log {

    public static void registrarErro(String contexto, Exception ex) {
        String mensagem = "[" + java.time.LocalDateTime.now() + "] "
                + "Erro em: " + contexto + " -> "
                + ex.getClass().getSimpleName() + ": "
                + ex.getMessage();

        System.err.println(mensagem); // Exibe no console

        // (Opcional) Escreve em arquivo de log
        try (java.io.FileWriter fw = new java.io.FileWriter("Logs.txt", true)) {
            fw.write(mensagem + System.lineSeparator());
        } catch (IOException ioe) {
            System.err.println("Falha ao escrever no log: " + ioe.getMessage());
        }
        jInternal_console.getInstance().append(jInternal_console.Level.INFO, contexto);
        jInternal_console.getInstance().append(jInternal_console.Level.ERROR, "Deu ruim!", ex);
        // (Opcional) Exibe em popup
        // JOptionPane.showMessageDialog(null, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public static void registrarErro_noEx(String contexto) {
        String mensagem = "[" + java.time.LocalDateTime.now() + "] "
                + "Atenção em: " + contexto;

        System.err.println(mensagem); // Exibe no console

        // (Opcional) Escreve em arquivo de log
        try (java.io.FileWriter fw = new java.io.FileWriter("Logs.txt", true)) {
            fw.write(mensagem + System.lineSeparator());
        } catch (IOException ioe) {
            System.err.println("Falha ao escrever no log: " + ioe.getMessage());
        }

        jInternal_console.getInstance().append(jInternal_console.Level.INFO, contexto);

        // (Opcional) Exibe em popup
        // JOptionPane.showMessageDialog(null, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public static void memory_log(String mensagem, String memoria) {
        String timetamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String linha = timetamp + " - " + mensagem + " - " + memoria;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("memory_log.txt", true))) {
            writer.write(linha);
            writer.newLine();
        } catch (IOException e) {
            Funcoes.message_error("Erro ao gravar log de memória: " + e.getMessage());
        }
    }
}
