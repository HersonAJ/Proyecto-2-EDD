package org.example.GUI;

import org.example.Grafo.GrafoBibliotecas;
import org.example.Modelos.Biblioteca;
import org.example.Grafo.Arista;
import org.example.Grafo.ListaAdyacencia;
import org.example.TablaHash.Iterador;

import javax.swing.*;
import java.awt.*;

public class PanelInfoRed extends JPanel {
    private GrafoBibliotecas grafo;
    private JTextArea textArea;

    public PanelInfoRed(GrafoBibliotecas grafo) {
        this.grafo = grafo;
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
            // ✅ INSTANCIAR DIRECTAMENTE
            MainWindowGrafo ventanaGrafo = new MainWindowGrafo(grafo);
            ventanaGrafo.setVisible(true);
        });

        // ✅ Botón para eliminar biblioteca
        JButton btnEliminarBiblioteca = new JButton("🗑️ Eliminar Biblioteca");
        btnEliminarBiblioteca.addActionListener(e -> eliminarBiblioteca());

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnActualizar);
        panelBotones.add(btnVisualizarGrafo);
        panelBotones.add(btnEliminarBiblioteca);

        add(panelBotones, BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Cargar información inicial
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

    private void eliminarBiblioteca() {
        // ✅ Esto lo implementaremos en el siguiente paso
        JOptionPane.showMessageDialog(this,
                "Funcionalidad de eliminación por implementar",
                "En Desarrollo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Método para que MainWindow pueda actualizar este panel
    public void actualizarVista() {
        actualizarInformacion();
    }
}