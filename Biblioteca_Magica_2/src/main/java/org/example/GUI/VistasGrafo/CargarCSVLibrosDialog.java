package org.example.GUI.VistasGrafo;

import org.example.Grafo.GrafoBibliotecas;
import org.example.Modelos.LectorCSVLibros;
import org.example.Modelos.CoordinadorEnvios;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CargarCSVLibrosDialog extends JDialog {
    private GrafoBibliotecas grafo;
    private CoordinadorEnvios coordinador;
    private Runnable onCargaCompletada;
    private LectorCSVLibros.ProgresoCallback progresoCallback;

    public CargarCSVLibrosDialog(JFrame parent, GrafoBibliotecas grafo, CoordinadorEnvios coordinador,
                                 Runnable onCargaCompletada, LectorCSVLibros.ProgresoCallback progresoCallback) {
        super(parent, "Cargar Libros desde CSV", true);
        this.grafo = grafo;
        this.coordinador = coordinador;
        this.onCargaCompletada = onCargaCompletada;
        this.progresoCallback = progresoCallback;

        // Configurar para que se cierre correctamente
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Ejecutar el file choiser de forma asíncrona para evitar bloqueos
        SwingUtilities.invokeLater(this::seleccionarYProcesarCSV);
    }

    private void seleccionarYProcesarCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo CSV de libros");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos CSV", "csv"));

        int resultado = fileChooser.showOpenDialog(this);

        // Procesar el resultado
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            procesarArchivoCSV(archivo);
        } else {
            // Usuario canceló
            progresoCallback.reportarLinea("Carga de CSV cancelada por el usuario", "info");
        }

        // Cerrar el dialog después de procesar
        dispose();
    }

    private void procesarArchivoCSV(File archivo) {
        try {
            // Usar el LectorCSVLibros con callback
            LectorCSVLibros lector = new LectorCSVLibros(grafo, coordinador);

            // Configurar callback para reportar progreso en tiempo real
            lector.setProgresoCallback(progresoCallback);

            // Procesar el archivo
            LectorCSVLibros.ResultadoCarga resultado = lector.procesarArchivo(archivo.getAbsolutePath());

            // Mostrar resumen final
            progresoCallback.reportarLinea("Carga completada - Éxitos: " + resultado.getExitosos() +
                    ", Fallos: " + resultado.getFallidos(), "info");

            if (resultado.getFallidos() > 0) {
                progresoCallback.reportarLinea("Se encontraron " + resultado.getErrores().size() + " errores", "error");
            }

            // Ejecutar callback final
            if (onCargaCompletada != null) {
                onCargaCompletada.run();
            }

        } catch (Exception e) {
            progresoCallback.reportarLinea("Error procesando archivo: " + e.getMessage(), "error");
        }
    }
}