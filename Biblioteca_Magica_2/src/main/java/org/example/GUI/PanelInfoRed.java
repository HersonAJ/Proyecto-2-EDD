package org.example.GUI;

import org.example.Grafo.GrafoBibliotecas;
import org.example.Modelos.Biblioteca;
import org.example.Grafo.Arista;
import org.example.Grafo.ListaAdyacencia;
import org.example.TablaHash.Iterador;
import org.example.GUI.MainWindow2;

import javax.swing.*;
import java.awt.*;

public class PanelInfoRed extends JPanel {
    private GrafoBibliotecas grafo;
    private JTextArea textArea;
    private MainWindow2 mainWindow;

    public PanelInfoRed(GrafoBibliotecas grafo, MainWindow2 mainWindow) {
        this.grafo = grafo;
        this.mainWindow = mainWindow;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JButton btnActualizar = new JButton("Actualizar Información");
        btnActualizar.addActionListener(e -> actualizarInformacion());

        JButton btnVisualizarGrafo = new JButton("Visualizar Grafo");
        btnVisualizarGrafo.addActionListener(e -> {
            MainWindowGrafo ventanaGrafo = new MainWindowGrafo(grafo);
            ventanaGrafo.setVisible(true);
        });

        JButton btnEliminarBiblioteca = new JButton("🗑️ Eliminar Biblioteca");
        btnEliminarBiblioteca.addActionListener(e -> eliminarBiblioteca());

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnActualizar);
        panelBotones.add(btnVisualizarGrafo);
        panelBotones.add(btnEliminarBiblioteca);

        add(panelBotones, BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        actualizarInformacion();
    }

    private void actualizarInformacion() {
        textArea.setText(obtenerInformacionRed());
    }

    private String obtenerInformacionRed() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SISTEMA DE BIBLIOTECAS MÁGICAS ===\n\n");

        sb.append("RESUMEN:\n");
        sb.append("• Bibliotecas: ").append(grafo.getBibliotecas().size()).append("\n");
        sb.append("• Conexiones: ").append(grafo.getTodasLasAristas().getTamaño()).append("\n\n");

        sb.append("BIBLIOTECAS:\n");
        var bibliotecas = grafo.getBibliotecas();
        Iterador<Biblioteca> iteradorBib = bibliotecas.iteradorValores();
        while (iteradorBib.tieneSiguiente()) {
            Biblioteca bib = iteradorBib.siguiente();
            sb.append("• ").append(bib.getId())
                    .append(" - ").append(bib.getNombre())
                    .append(" (").append(bib.getUbicacion()).append(")\n")
                    .append("  Tiempos [Ingreso:").append(bib.getColaIngreso().getTiempoProcesamiento())
                    .append("s, Traspaso:").append(bib.getColaTraspaso().getTiempoProcesamiento())
                    .append("s, Intervalo:").append(bib.getColaSalida().getTiempoProcesamiento()).append("s]\n");
        }

        sb.append("\nCONEXIONES:\n");
        ListaAdyacencia todasAristas = grafo.getTodasLasAristas();
        ListaAdyacencia.IteradorLista iteradorAristas = todasAristas.iterador();
        while (iteradorAristas.tieneSiguiente()) {
            Arista arista = iteradorAristas.siguiente();
            sb.append("• ").append(arista.getIdOrigen())
                    .append(" → ").append(arista.getIdDestino())
                    .append(" [Tiempo:").append(arista.getTiempo())
                    .append("s, Costo:").append(arista.getCosto()).append("]\n");
        }

        return sb.toString();
    }

    public void actualizarVista() {
        actualizarInformacion();
    }

    private void eliminarBiblioteca() {
        if (grafo.getBibliotecas().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay bibliotecas disponibles para eliminar.",
                    "Sin Bibliotecas",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener lista de bibliotecas para mostrar
        String[] opcionesBibliotecas = obtenerListaBibliotecas();

        String seleccion = (String) JOptionPane.showInputDialog(this,
                "Seleccione la biblioteca a eliminar:",
                "Eliminar Biblioteca",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcionesBibliotecas,
                opcionesBibliotecas[0]);

        if (seleccion != null && !seleccion.isEmpty()) {
            // Extraer ID de la selección (formato: "ID - Nombre (Ubicación)")
            String idBiblioteca = seleccion.split(" - ")[0];

            // Confirmación de eliminación
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar la biblioteca '" + seleccion + "'?\n" +
                            "Esta acción eliminará:\n" +
                            "• La biblioteca y todos sus libros\n" +
                            "• Todas sus conexiones de red\n" +
                            "• No se puede deshacer",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirmacion == JOptionPane.YES_OPTION) {
                eliminarBibliotecaCompleta(idBiblioteca);
            }
        }
    }

    private String[] obtenerListaBibliotecas() {
        var bibliotecas = grafo.getBibliotecas();
        String[] lista = new String[bibliotecas.size()];
        Iterador<Biblioteca> iterador = bibliotecas.iteradorValores();

        int i = 0;
        while (iterador.tieneSiguiente()) {
            Biblioteca bib = iterador.siguiente();
            lista[i++] = bib.getId() + " - " + bib.getNombre() + " (" + bib.getUbicacion() + ")";
        }

        return lista;
    }

    private void eliminarBibliotecaCompleta(String idBiblioteca) {
        try {
            // 1. Detener todas las colas de la biblioteca
            Biblioteca biblioteca = grafo.getBiblioteca(idBiblioteca);
            if (biblioteca != null) {
                biblioteca.getColaIngreso().detenerProcesamiento();
                biblioteca.getColaTraspaso().detenerProcesamiento();
                biblioteca.getColaSalida().detenerProcesamiento();
            }

            // 2. Eliminar del grafo (esto debe eliminar también las conexiones)
            boolean eliminado = grafo.eliminarBiblioteca(idBiblioteca);

            if (eliminado) {
                // 3. Actualizar interfaz
                actualizarInformacion();

                // 4. Notificar a MainWindow para actualizar otros panels
                if (mainWindow != null) {
                    mainWindow.actualizarTodosLosPaneles();
                }

                JOptionPane.showMessageDialog(this,
                        "Biblioteca '" + idBiblioteca + "' eliminada exitosamente.",
                        "Eliminación Completada",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar la biblioteca '" + idBiblioteca + "'.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar biblioteca: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}