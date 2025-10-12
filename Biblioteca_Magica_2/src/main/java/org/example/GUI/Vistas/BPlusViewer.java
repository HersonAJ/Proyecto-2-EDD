package org.example.GUI.Vistas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class BPlusViewer extends JPanel {

    private Object arbol; // referencia al ArbolBPlus (se definirá después)
    private JLabel imagenLabel;
    private JScrollPane scrollArea;
    private Image imagenOriginal;
    private double escala = 1.0;

    public BPlusViewer(Object arbol) {
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

    // Método para actualizar la vista (vacío por ahora)
    public void actualizarVista() {

    }

    // Manejo del zoom con la rueda del ratón
    private void wheelEvent(MouseWheelEvent event) {

    }
}
