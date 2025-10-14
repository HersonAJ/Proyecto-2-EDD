package org.example;

import org.example.GUI.MainWindow;
import org.example.Modelos.Biblioteca;
import org.example.Modelos.GestorBibliotecas;
import org.example.Modelos.Libro;

import javax.swing.*;
/*
public class Main {
    public static void main(String[] args) {

 /*       SwingUtilities.invokeLater(() -> {
            MainWindow ventana = new MainWindow();
            ventana.setVisible(true);
        });*/
/*
        GestorBibliotecas gestor = new GestorBibliotecas();
                gestor.agregarBiblioteca("A-101", "Almacen Principal", "Madrir", 28800, 45000, 36000);
                gestor.agregarBiblioteca("B-205", "Centro Distribucion", "Barcelona", 34200, 50400, 1800);

        Biblioteca bib = gestor.getBiblioteca("A-101");
        Libro libro = new Libro("Cien años de soledad", "123", "realismo" , "1967", "garcia marquez");
        Libro libro2 = new Libro("Cien años de soledad 2", "456", "realismo" , "1967", "garcia marquez");
        Libro libro3 = new Libro("Cien años de soledad 3", "789", "realismo" , "1967", "garcia marquez");
        bib.agregarLibro(libro);
        bib.agregarLibro(libro2);
        bib.agregarLibro(libro3);

        System.out.println(bib.getCatalogo().obtenerTodosLosLibros());
        bib.eliminarLibroPorISBN("456");
        System.out.println("\n");
        System.out.println(bib.getCatalogo().obtenerTodosLosLibros());



    }
}*/

import org.example.Grafo.GrafoBibliotecas;
import org.example.GUI.MainWindowGrafo;

public class Main {
    public static void main(String[] args) {
        // Crear grafo y hardcodear datos de prueba
        GrafoBibliotecas grafo = new GrafoBibliotecas();

        // Hardcodear bibliotecas
        grafo.agregarBiblioteca("A-101", "Almacén Principal", "Madrid", 28800, 45000, 3600);
        grafo.agregarBiblioteca("B-205", "Centro de Distribución", "Barcelona", 34200, 50400, 1800);
        grafo.agregarBiblioteca("C-309", "Plataforma Logística Sur", "Valencia", 39600, 60300, 2700);

        // Hardcodear conexiones
        grafo.conectarBibliotecas("A-101", "B-205", 12600, 250.00);
        grafo.conectarBibliotecas("A-101", "C-309", 18000, 400.00);
        grafo.conectarBibliotecas("C-309", "B-205", 9000, 150.00);

        // Mostrar ventana de visualización - ahora pasa GrafoBibliotecas
        SwingUtilities.invokeLater(() -> {
            MainWindowGrafo ventanaGrafo = new MainWindowGrafo(grafo); // Pasa el grafo completo
            ventanaGrafo.setVisible(true);
        });
    }
}