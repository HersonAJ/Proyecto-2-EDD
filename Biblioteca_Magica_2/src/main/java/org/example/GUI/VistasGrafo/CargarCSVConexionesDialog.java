package org.example.GUI.VistasGrafo;

import org.example.Grafo.GrafoBibliotecas;
import org.example.Modelos.LectorCSVConexiones;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CargarCSVConexionesDialog extends JDialog {
    private GrafoBibliotecas grafo;
    private Runnable onCargaCompletada;
    private LectorCSVConexiones.ProgresoCallback progresoCallback;

    public CargarCSVConexionesDialog(JFrame parent, GrafoBibliotecas grafo, Runnable onCargaCompletada, LectorCSVConexiones.ProgresoCallback progresoCallback) {
        super(parent, "Cargar Conexiones desde CSV", true);
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
                        "\"OrigenID\",\"DestinoID\",\"Tiempo\",\"Costo\"\n\n" +
                        "Ejemplo: \"A-101\",\"B-205\",12600,250.00\n\n" +
                        "Nota: Ambos IDs deben existir previamente en el sistema."
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

            // Usar el LectorCSVConexiones con callback
            LectorCSVConexiones lector = new LectorCSVConexiones(grafo);
            lector.setCallback(progresoCallback);
            lector.cargarConexionesDesdeCSV(archivo.getAbsolutePath());

            // Ejecutar callback final
            if (onCargaCompletada != null) {
                onCargaCompletada.run();
            }

            dispose();
        }
    }
}