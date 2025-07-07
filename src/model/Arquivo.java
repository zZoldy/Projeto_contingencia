/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.File;
import javax.swing.JTable;

/**
 *
 * @author Z D K
 */
public class Arquivo {

    private String path_file;
    private File file;
    private Table table;

    public Arquivo(String path_file, File file) {
        this.path_file = path_file;
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public String getPath_file() {
        return path_file;
    }

    public void setPath_file(String path_file) {
        this.path_file = path_file;
    }

    @Override
    public String toString() {
        return "\n\tArquivo"
                + "\nNome do arquivo: " + ((file != null) ? file.getName() : "null")
                + "\nPath do arquivo: " + ((path_file != null) ? path_file : "null")
                + "\nParent do arquivo: " + ((file != null && file.getParent() != null) ? file.getParent() : "null")
                + "\nFile: " + ((file != null) ? file.toString() : "null")
                + "\nTable: " + ((table != null) ? table : "null");
    }

}
