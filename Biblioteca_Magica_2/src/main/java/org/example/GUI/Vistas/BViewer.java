package org.example.GUI.Vistas;

import org.example.B.ArbolB;
import org.example.include.ExportadorDotB;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;

public class BViewer extends JPanel {

    private ArbolB arbol;
    private JLabel imagenLabel;
    private JScrollPane scrollArea;
    private ImageIcon imagenOriginal;
    private double escala = 1.0;

    public BViewer(ArbolB arbol) {
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
            imagenLabel.setText("El arbol B esta vacio.");
            return;
        }

        String dotFile = "b_viewer.dot";
        String pngFile = "b_viewer.png";

        if (!ExportadorDotB.generarArchivo(arbol, dotFile)) {
            return;
        }

        try {
            // Ejecutar comando dot
            String comando = "dot -Tpng " + dotFile + " -o " + pngFile;
            Process process = Runtime.getRuntime().exec(comando);
            int resultado = process.waitFor();

            if (resultado == 0) {
                // Cargar la imagen
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
            System.err.println("Error al generar imagen: " + e.getMessage());
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
}