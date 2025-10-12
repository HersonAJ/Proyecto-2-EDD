package org.example;

import org.example.GUI.MainWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            MainWindow ventana = new MainWindow();
            ventana.setVisible(true);
        });

    }
}