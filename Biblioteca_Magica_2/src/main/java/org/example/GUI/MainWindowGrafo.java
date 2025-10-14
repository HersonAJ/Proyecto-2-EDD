package org.example.GUI;

import org.example.Grafo.GrafoBibliotecas;
import org.example.Grafo.Vertice;
import org.example.Modelos.Biblioteca;
import org.example.Grafo.Arista;

import javax.swing.*;
import java.util.List;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainWindowGrafo extends JFrame {

    private GrafoBibliotecas grafo;
    private JPanel panelGrafo;

    public MainWindowGrafo(GrafoBibliotecas grafo) {
        super("Red de Bibliotecas - Visualización");
        this.grafo = grafo;

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        panelGrafo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarGrafo(g);
            }
        };

        panelGrafo.setBackground(Color.WHITE);
        add(new JScrollPane(panelGrafo), BorderLayout.CENTER);

        // Panel de información
        JPanel panelInfo = new JPanel(new BorderLayout());
        JTextArea textAreaInfo = new JTextArea();
        textAreaInfo.setEditable(false);
        textAreaInfo.setText(obtenerInfoGrafo());
        panelInfo.add(new JLabel("Información de la Red:"), BorderLayout.NORTH);
        panelInfo.add(new JScrollPane(textAreaInfo), BorderLayout.CENTER);

        add(panelInfo, BorderLayout.EAST);
    }

    private void dibujarGrafo(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Mapeo de posiciones
        Map<String, Biblioteca> bibliotecas = grafo.getBibliotecas();
        int total = bibliotecas.size();
        Map<String, Point> posiciones = new HashMap<>();
        int i = 0;
        for (String id : bibliotecas.keySet()) {
            posiciones.put(id, calcularPosicion(i, total, 400, 300, 200));
            i++;
        }

// === DIBUJAR ARISTAS ===
        for (Arista arista : grafo.getTodasLasAristas()) {
            Point pOrigen = posiciones.get(arista.getIdOrigen());
            Point pDestino = posiciones.get(arista.getIdDestino());
            if (pOrigen == null || pDestino == null) continue;

            // Calcular dirección de la línea
            double angulo = Math.atan2(pDestino.y - pOrigen.y, pDestino.x - pOrigen.x);
            int radioNodo = 30; // mismo tamaño del círculo
            int offset = 5;     // separa la flecha del borde
            int xAjustado = (int) (pDestino.x - (radioNodo + offset) * Math.cos(angulo));
            int yAjustado = (int) (pDestino.y - (radioNodo + offset) * Math.sin(angulo));

            // Dibujar línea principal (hasta antes del nodo destino)
            g2d.setColor(Color.GRAY);
            g2d.drawLine(pOrigen.x, pOrigen.y, xAjustado, yAjustado);

            // === Flecha direccional visible ===
            dibujarFlecha(g2d, pOrigen, new Point(xAjustado, yAjustado), 12, 25);

            // Etiqueta de la arista
            String etiqueta = "T:" + arista.getTiempo() + "s";
            g2d.setColor(Color.BLUE);
            g2d.drawString(etiqueta,
                    (pOrigen.x + pDestino.x) / 2,
                    (pOrigen.y + pDestino.y) / 2
            );
        }


        // === DIBUJAR NODOS ===
        for (Map.Entry<String, Point> entry : posiciones.entrySet()) {
            String id = entry.getKey();
            Point pos = entry.getValue();
            Biblioteca bib = bibliotecas.get(id);

            g2d.setColor(Color.ORANGE);
            g2d.fillOval(pos.x - 30, pos.y - 30, 60, 60);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(pos.x - 30, pos.y - 30, 60, 60);

            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString(bib.getId(), pos.x - 10, pos.y - 5);
            g2d.setFont(new Font("Arial", Font.PLAIN, 8));
            g2d.drawString(bib.getNombre(), pos.x - 25, pos.y + 10);
        }
    }


    private Point calcularPosicion(int index, int total, int centroX, int centroY, int radio) {
        double angulo = 2 * Math.PI * index / total;
        int x = centroX + (int) (radio * Math.cos(angulo));
        int y = centroY + (int) (radio * Math.sin(angulo));
        return new Point(x, y);
    }

    private String obtenerInfoGrafo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RED DE BIBLIOTECAS ===\n\n");

        sb.append("Bibliotecas (").append(grafo.getBibliotecas().size()).append("):\n");
        for (Biblioteca bib : grafo.getBibliotecas().values()) {
            sb.append("• ").append(bib.getId())
                    .append(" - ").append(bib.getNombre())
                    .append(" (").append(bib.getUbicacion()).append(")\n");
        }

        sb.append("\nConexiones REALES en el grafo:\n");
        for (Arista arista : grafo.getTodasLasAristas()) {
            sb.append("• ").append(arista.getIdOrigen())
                    .append(" → ").append(arista.getIdDestino())
                    .append(" [T:").append(arista.getTiempo())
                    .append("s, C:").append(arista.getCosto()).append("]\n");
        }

        // DEBUG: Verificar conexiones por biblioteca
        sb.append("\nDEBUG - Conexiones por biblioteca:\n");
        for (String id : grafo.getBibliotecas().keySet()) {
            List<Arista> conexiones = grafo.getConexionesSalientes(id);
            sb.append("• ").append(id).append(" → ").append(conexiones.size()).append(" conexiones: ");
            for (Arista arista : conexiones) {
                sb.append(arista.getIdDestino()).append(" ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public void actualizarVista() {
        panelGrafo.repaint();
    }

    private void dibujarFlecha(Graphics2D g2d, Point p1, Point p2, int tamaño, int ancho) {
        double angulo = Math.atan2(p2.y - p1.y, p2.x - p1.x);

        // Calcular puntos de las alas de la flecha
        int x1 = (int) (p2.x - tamaño * Math.cos(angulo - Math.toRadians(ancho)));
        int y1 = (int) (p2.y - tamaño * Math.sin(angulo - Math.toRadians(ancho)));

        int x2 = (int) (p2.x - tamaño * Math.cos(angulo + Math.toRadians(ancho)));
        int y2 = (int) (p2.y - tamaño * Math.sin(angulo + Math.toRadians(ancho)));

        // Dibujar las alas
        g2d.setColor(Color.RED);
        g2d.drawLine(p2.x, p2.y, x1, y1);
        g2d.drawLine(p2.x, p2.y, x2, y2);
    }
}
