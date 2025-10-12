package org.example.GUI.Vistas;

import org.example.AVL.ArbolAVL;
import org.example.include.ExportadorDOT;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;

public class AVLViewer extends JPanel {

    private ArbolAVL arbol;
    private JLabel imagenLabel;
    private JScrollPane scrollArea;
    private ImageIcon imagenOriginal;
    private double escala = 1.0;

    public AVLViewer(ArbolAVL arbol) {
        this.arbol = arbol;
        inicializarComponentes();
        if (arbol != null && arbol.getRaiz() != null) {
            actualizarVista();
        }
    }

    public AVLViewer() {
        this(null);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        imagenLabel = new JLabel();
        imagenLabel.setHorizontalAlignment(SwingConstants.CENTER);

        scrollArea = new JScrollPane(imagenLabel);
        scrollArea.setPreferredSize(new Dimension(800, 600));

        add(scrollArea, BorderLayout.CENTER);

        // Listener para zoom con la rueda del ratón
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                wheelEvent(e);
            }
        });
    }

    public void setArbol(ArbolAVL arbol) {
        this.arbol = arbol;
        actualizarVista();
    }

    public void actualizarVista() {
        if (arbol == null || arbol.getRaiz() == null) {
            imagenLabel.setText("El arbol AVL esta vacio");
            return;
        }

        String dotFile = "avl_viewer.dot";
        String pngFile = "avl_viewer.png";

        try {
            // Exportar a DOT
            ExportadorDOT exportador = new ExportadorDOT(arbol.getRaiz(), dotFile);
            exportador.exportar();

            File archivoDOT = new File(dotFile);

            // Ejecutar Graphviz para convertir DOT
            String comando = "dot -Tpng " + dotFile + " -o " + pngFile;
            System.out.println("ejecutando: " + comando);
            Process process = Runtime.getRuntime().exec(comando);
            int resultado = process.waitFor();

            if (resultado == 0) {
                // Cargar la imagen
                File archivoPNG = new File(pngFile);
                if (archivoPNG.exists()) {
                    ImageIcon icono = new ImageIcon(pngFile);
                    imagenOriginal = icono;

                    // Escalar la imagen según la escala actual
                    if (imagenOriginal != null) {
                        Image imagenEscalada = imagenOriginal.getImage().getScaledInstance(
                                (int)(imagenOriginal.getIconWidth() * escala),
                                (int)(imagenOriginal.getIconHeight() * escala),
                                Image.SCALE_SMOOTH
                        );
                        imagenLabel.setIcon(new ImageIcon(imagenEscalada));
                        imagenLabel.setText("");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error en AVLViewer::actualizarVista: " + e.getMessage());
            // Mostrar mensaje de error en el label
            imagenLabel.setText("Error al generar visualización: " + e.getMessage());
        }
    }

    private void wheelEvent(MouseWheelEvent event) {
        if (event.isControlDown()) {
            if (event.getWheelRotation() < 0) {
                escala *= 1.1; // zoom in
            } else {
                escala /= 1.1; // zoom out
            }

            // Limitar escala mínima y máxima
            escala = Math.max(0.1, Math.min(escala, 5.0));

            if (imagenOriginal != null) {
                Image imagenEscalada = imagenOriginal.getImage().getScaledInstance(
                        (int)(imagenOriginal.getIconWidth() * escala),
                        (int)(imagenOriginal.getIconHeight() * escala),
                        Image.SCALE_SMOOTH
                );
                imagenLabel.setIcon(new ImageIcon(imagenEscalada));
            }

            event.consume();
        }
    }
}