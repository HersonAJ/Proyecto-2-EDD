package org.example;

import org.example.GUI.MainWindow;
import org.example.GUI.MainWindow2;
import org.example.Modelos.Biblioteca;
import org.example.Modelos.GestorBibliotecas;
import org.example.Modelos.Libro;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            MainWindow2 ventana = new MainWindow2();
            ventana.setVisible(true);
        });
    }
}