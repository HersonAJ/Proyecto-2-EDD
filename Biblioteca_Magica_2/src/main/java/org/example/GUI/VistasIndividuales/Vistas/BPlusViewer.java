package org.example.GUI.VistasIndividuales.Vistas;

import org.example.Estructuras.BPlus.ArbolBPlus;
import org.example.include.ExportadorDotBPlus;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;

public class BPlusViewer extends JPanel {

    private ArbolBPlus arbol;
    private JLabel imagenLabel;
    private JScrollPane scrollArea;
    private ImageIcon imagenOriginal;
    private double escala = 1.0;

    public BPlusViewer(ArbolBPlus arbol) {
        this.arbol = arbol;

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

    public void actualizarVista() {

        if (arbol == null || arbol.getRaiz() == null) {
            imagenLabel.setText("El arbol esta vacio");
            return;
        }

        String dotFile = "bplus_viewer.dot";
        String pngFile = "bplus_viewer.png";

        if (!ExportadorDotBPlus.generarArchivo(arbol, dotFile)) {
            return;
        }

        try {
            // Ejecutar comando dot para generar PNG
            String comando = "dot -Tpng " + dotFile + " -o " + pngFile;
            Process process = Runtime.getRuntime().exec(comando);
            int resultado = process.waitFor();

            if (resultado == 0) {
                // Cargar la imagen PNG
                ImageIcon icon = new ImageIcon(pngFile);
                if (icon.getIconWidth() > 0) {
                    imagenOriginal = icon;
                    escalarImagen();
                }
            }

            // Limpiar archivos temporales
            new File(dotFile).delete();
            new File(pngFile).delete();

        } catch (Exception e) {
            System.err.println("Error al generar imagen B+: " + e.getMessage());
        }
    }

    private void wheelEvent(MouseWheelEvent event) {
        if (event.isControlDown()) {
            if (event.getWheelRotation() < 0) {
                escala *= 1.1; // zoom in
            } else {
                escala /= 1.1; // zoom out
            }

            if (imagenOriginal != null) {
                escalarImagen();
            }

            event.consume();
        }
    }

    private void escalarImagen() {
        if (imagenOriginal != null) {
            Image img = imagenOriginal.getImage();
            int newWidth = (int) (img.getWidth(null) * escala);
            int newHeight = (int) (img.getHeight(null) * escala);

            Image scaledImg = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            imagenLabel.setIcon(new ImageIcon(scaledImg));
        }
    }

    // Método para cambiar el árbol (si es necesario)
    public void setArbol(ArbolBPlus arbol) {
        this.arbol = arbol;
    }
}