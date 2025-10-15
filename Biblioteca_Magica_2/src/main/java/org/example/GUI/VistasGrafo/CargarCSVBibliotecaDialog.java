package org.example.GUI.VistasGrafo;

import org.example.Grafo.GrafoBibliotecas;
import org.example.Modelos.LectorCSVBiblioteca;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CargarCSVBibliotecaDialog extends JDialog {
    private GrafoBibliotecas grafo;
    private Runnable onCargaCompletada;
    private LectorCSVBiblioteca.ProgresoCallback progresoCallback;

    public CargarCSVBibliotecaDialog(JFrame parent, GrafoBibliotecas grafo, Runnable onCargaCompletada, LectorCSVBiblioteca.ProgresoCallback progresoCallback) {
        super(parent, "Cargar Bibliotecas desde CSV", true);
        this.grafo = grafo;
        this.onCargaCompletada = onCargaCompletada;
        this.progresoCallback = progresoCallback;

        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setSize(500, 200);

        // Panel de instrucciones
        JTextArea instrucciones = new JTextArea(
                "Seleccione un archivo CSV con el formato:\n" +
                        "\"ID\",\"Nombre\",\"Ubicacion\",\"t_ingreso\",\"t_traspaso\",\"dispatchInterval\"\n\n" +
                        "Ejemplo: \"A-101\",\"Biblioteca Central\",\"Madrid\",28800,45000,3600"
        );
        instrucciones.setEditable(false);
        instrucciones.setBackground(getBackground());
        add(instrucciones, BorderLayout.NORTH);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        JButton btnSeleccionar = new JButton("Seleccionar Archivo CSV");
        JButton btnCancelar = new JButton("Cancelar");

        btnSeleccionar.addActionListener(e -> seleccionarYProcesarCSV());
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnSeleccionar);
        panelBotones.add(btnCancelar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void seleccionarYProcesarCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos CSV", "csv"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();

            // Usar el LectorCSVBiblioteca con callback
            LectorCSVBiblioteca lector = new LectorCSVBiblioteca(grafo);

            // Configurar callback para reportar progreso en tiempo real
            lector.setCallback(progresoCallback);

            lector.cargarBibliotecasDesdeCSV(archivo.getAbsolutePath());

            // Ejecutar callback final
            if (onCargaCompletada != null) {
                onCargaCompletada.run();
            }

            dispose();
        }
    }
}