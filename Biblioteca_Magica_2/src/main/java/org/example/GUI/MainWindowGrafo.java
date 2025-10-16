package org.example.GUI;

import org.example.Grafo.GrafoBibliotecas;
import org.example.Grafo.ListaAdyacencia;
import org.example.Grafo.Vertice;
import org.example.Modelos.Biblioteca;
import org.example.Grafo.Arista;
import org.example.TablaHash.Iterador;
import org.example.TablaHash.TablaHash;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class MainWindowGrafo extends JFrame {

    private GrafoBibliotecas grafo;
    private JPanel panelGrafo;
    private Map<String, Point> posiciones;
    private Timer timerAnimacion;

    public MainWindowGrafo(GrafoBibliotecas grafo) {
        super("Red de Bibliotecas - Visualización");
        this.grafo = grafo;
        this.posiciones = new HashMap<>();

        setSize(1000, 700); // Ventana más grande para más nodos
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        inicializarLayoutFuerza();
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
        panelGrafo.setPreferredSize(new Dimension(2000, 2000)); // Panel más grande
        add(new JScrollPane(panelGrafo), BorderLayout.CENTER);

        // Panel de información
        JPanel panelInfo = new JPanel(new BorderLayout());
        JTextArea textAreaInfo = new JTextArea();
        textAreaInfo.setEditable(false);
        textAreaInfo.setText(obtenerInfoGrafo());
        panelInfo.add(new JLabel("Información de la Red:"), BorderLayout.NORTH);
        panelInfo.add(new JScrollPane(textAreaInfo), BorderLayout.CENTER);

        add(panelInfo, BorderLayout.EAST);

        // Botón para reiniciar layout
        JButton btnReiniciarLayout = new JButton("Reiniciar Layout");
        btnReiniciarLayout.addActionListener(e -> reiniciarLayout());

        JPanel panelSur = new JPanel();
        panelSur.add(btnReiniciarLayout);
        add(panelSur, BorderLayout.SOUTH);
    }

    private void inicializarLayoutFuerza() {
        // Inicializar posiciones aleatorias dentro del área visible
        Random rand = new Random();
        TablaHash<String, Biblioteca> bibliotecas = grafo.getBibliotecas();

        Iterador<String> iterador = bibliotecas.iteradorClaves();
        while (iterador.tieneSiguiente()) {
            String id = iterador.siguiente();
            int x = 100 + rand.nextInt(1800); // Distribuir en área amplia
            int y = 100 + rand.nextInt(1800);
            posiciones.put(id, new Point(x, y));
        }

        // Ejecutar algoritmo de fuerza por 100 iteraciones
        ejecutarAlgoritmoFuerza(100);
    }

    private void ejecutarAlgoritmoFuerza(int iteraciones) {
        final double REPULSION = 50000.0;  // Fuerza de repulsión
        final double ATRACCION = 0.01;     // Fuerza de atracción
        final double LONGITUD_ARISTA = 150.0; // Longitud ideal de aristas

        TablaHash<String, Biblioteca> bibliotecas = grafo.getBibliotecas();
        ListaAdyacencia aristas = grafo.getTodasLasAristas();

        for (int iter = 0; iter < iteraciones; iter++) {
            Map<String, Point> fuerzas = new HashMap<>();

            // Inicializar fuerzas en cero
            Iterador<String> iteradorIds = bibliotecas.iteradorClaves();
            while (iteradorIds.tieneSiguiente()) {
                String id = iteradorIds.siguiente();
                fuerzas.put(id, new Point(0, 0));
            }

            // Calcular fuerzas de repulsión entre todos los nodos
            Iterador<String> iterador1 = bibliotecas.iteradorClaves();
            while (iterador1.tieneSiguiente()) {
                String id1 = iterador1.siguiente();
                Point p1 = posiciones.get(id1);

                Iterador<String> iterador2 = bibliotecas.iteradorClaves();
                while (iterador2.tieneSiguiente()) {
                    String id2 = iterador2.siguiente();
                    if (!id1.equals(id2)) {
                        Point p2 = posiciones.get(id2);
                        double dx = p1.x - p2.x;
                        double dy = p1.y - p2.y;
                        double distancia = Math.sqrt(dx * dx + dy * dy);

                        if (distancia > 0) {
                            double fuerza = REPULSION / (distancia * distancia);
                            Point fuerzaActual = fuerzas.get(id1);
                            fuerzas.put(id1, new Point(
                                    (int)(fuerzaActual.x + fuerza * dx / distancia),
                                    (int)(fuerzaActual.y + fuerza * dy / distancia)
                            ));
                        }
                    }
                }
            }

            // Calcular fuerzas de atracción por las aristas
            ListaAdyacencia.IteradorLista iteradorAristas = aristas.iterador();
            while (iteradorAristas.tieneSiguiente()) {
                Arista arista = iteradorAristas.siguiente();
                Point p1 = posiciones.get(arista.getIdOrigen());
                Point p2 = posiciones.get(arista.getIdDestino());

                if (p1 != null && p2 != null) {
                    double dx = p2.x - p1.x;
                    double dy = p2.y - p1.y;
                    double distancia = Math.sqrt(dx * dx + dy * dy);

                    if (distancia > 0) {
                        double fuerza = ATRACCION * (distancia - LONGITUD_ARISTA);
                        Point fuerzaOrigen = fuerzas.get(arista.getIdOrigen());
                        Point fuerzaDestino = fuerzas.get(arista.getIdDestino());

                        fuerzas.put(arista.getIdOrigen(), new Point(
                                (int)(fuerzaOrigen.x + fuerza * dx / distancia),
                                (int)(fuerzaOrigen.y + fuerza * dy / distancia)
                        ));

                        fuerzas.put(arista.getIdDestino(), new Point(
                                (int)(fuerzaDestino.x - fuerza * dx / distancia),
                                (int)(fuerzaDestino.y - fuerza * dy / distancia)
                        ));
                    }
                }
            }

            // Aplicar fuerzas con límite de movimiento
            Iterador<String> iteradorFinal = bibliotecas.iteradorClaves();
            while (iteradorFinal.tieneSiguiente()) {
                String id = iteradorFinal.siguiente();
                Point fuerza = fuerzas.get(id);
                Point pos = posiciones.get(id);

                // Limitar el movimiento máximo por iteración
                double movimiento = Math.sqrt(fuerza.x * fuerza.x + fuerza.y * fuerza.y);
                double maxMovimiento = 10.0;
                if (movimiento > maxMovimiento) {
                    fuerza.x = (int)(fuerza.x * maxMovimiento / movimiento);
                    fuerza.y = (int)(fuerza.y * maxMovimiento / movimiento);
                }

                // Aplicar movimiento
                int newX = Math.max(50, Math.min(1950, pos.x + fuerza.x));
                int newY = Math.max(50, Math.min(1950, pos.y + fuerza.y));
                posiciones.put(id, new Point(newX, newY));
            }
        }

        panelGrafo.repaint();
    }

    private void dibujarGrafo(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // === DIBUJAR ARISTAS PRIMERO (para que queden detrás de los nodos) ===
        g2d.setStroke(new BasicStroke(1.5f));
        ListaAdyacencia todasAristas = grafo.getTodasLasAristas();
        ListaAdyacencia.IteradorLista iteradorAristas = todasAristas.iterador();
        while (iteradorAristas.tieneSiguiente()) {
            Arista arista = iteradorAristas.siguiente();
            Point pOrigen = posiciones.get(arista.getIdOrigen());
            Point pDestino = posiciones.get(arista.getIdDestino());
            if (pOrigen == null || pDestino == null) continue;

            // Calcular dirección de la línea
            double angulo = Math.atan2(pDestino.y - pOrigen.y, pDestino.x - pOrigen.x);
            int radioNodo = 25;
            int xAjustado = (int) (pDestino.x - radioNodo * Math.cos(angulo));
            int yAjustado = (int) (pDestino.y - radioNodo * Math.sin(angulo));

            // Dibujar línea
            g2d.setColor(new Color(100, 100, 100, 150)); // Gris semi-transparente
            g2d.drawLine(pOrigen.x, pOrigen.y, xAjustado, yAjustado);

            // Flecha direccional
            dibujarFlecha(g2d, pOrigen, new Point(xAjustado, yAjustado), 10, 20);

            // Etiqueta de la arista (solo si hay espacio)
            if (pOrigen.distance(pDestino) > 80) {
                String etiqueta = "T:" + arista.getTiempo() + "s";
                g2d.setColor(Color.BLUE);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                int labelX = (pOrigen.x + pDestino.x) / 2;
                int labelY = (pOrigen.y + pDestino.y) / 2;
                g2d.drawString(etiqueta, labelX, labelY);
            }
        }

        // === DIBUJAR NODOS ===
        g2d.setStroke(new BasicStroke(2f));
        TablaHash<String, Biblioteca> bibliotecas = grafo.getBibliotecas();

        for (Map.Entry<String, Point> entry : posiciones.entrySet()) {
            String id = entry.getKey();
            Point pos = entry.getValue();
            Biblioteca bib = bibliotecas.get(id);

            // Nodo con gradiente
            GradientPaint gradient = new GradientPaint(
                    pos.x - 25, pos.y - 25, new Color(255, 200, 100),
                    pos.x + 25, pos.y + 25, new Color(255, 150, 50)
            );
            g2d.setPaint(gradient);
            g2d.fillOval(pos.x - 25, pos.y - 25, 50, 50);

            g2d.setColor(Color.BLACK);
            g2d.drawOval(pos.x - 25, pos.y - 25, 50, 50);

            // Texto del ID
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(id);
            g2d.drawString(id, pos.x - textWidth/2, pos.y - 8);

            // Nombre abreviado (solo si cabe)
            g2d.setFont(new Font("Arial", Font.PLAIN, 8));
            String nombreAbreviado = abreviarNombre(bib.getNombre(), 12);
            textWidth = g2d.getFontMetrics().stringWidth(nombreAbreviado);
            g2d.drawString(nombreAbreviado, pos.x - textWidth/2, pos.y + 15);
        }
    }

    private String abreviarNombre(String nombre, int maxLength) {
        if (nombre.length() <= maxLength) return nombre;
        return nombre.substring(0, maxLength - 3) + "...";
    }

    private void reiniciarLayout() {
        inicializarLayoutFuerza();
    }

    // ... (los métodos obtenerInfoGrafo, dibujarFlecha se mantienen igual)
    private String obtenerInfoGrafo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RED DE BIBLIOTECAS ===\n\n");

        sb.append("Bibliotecas (").append(grafo.getBibliotecas().size()).append("):\n");
        TablaHash<String, Biblioteca> bibliotecas = grafo.getBibliotecas();
        Iterador<Biblioteca> iteradorBib = bibliotecas.iteradorValores();
        while (iteradorBib.tieneSiguiente()) {
            Biblioteca bib = iteradorBib.siguiente();
            sb.append("• ").append(bib.getId())
                    .append(" - ").append(bib.getNombre())
                    .append(" (").append(bib.getUbicacion()).append(")\n");
        }

        sb.append("\nConexiones (").append(grafo.getTodasLasAristas().getTamaño()).append("):\n");
        ListaAdyacencia todasAristas = grafo.getTodasLasAristas();
        ListaAdyacencia.IteradorLista iteradorAristas = todasAristas.iterador();
        while (iteradorAristas.tieneSiguiente()) {
            Arista arista = iteradorAristas.siguiente();
            sb.append("• ").append(arista.getIdOrigen())
                    .append(" → ").append(arista.getIdDestino())
                    .append(" [T:").append(arista.getTiempo())
                    .append("s, C:").append(arista.getCosto()).append("]\n");
        }

        return sb.toString();
    }
    private void dibujarFlecha(Graphics2D g2d, Point p1, Point p2, int tamaño, int ancho) {
        double angulo = Math.atan2(p2.y - p1.y, p2.x - p1.x);

        int x1 = (int) (p2.x - tamaño * Math.cos(angulo - Math.toRadians(ancho)));
        int y1 = (int) (p2.y - tamaño * Math.sin(angulo - Math.toRadians(ancho)));

        int x2 = (int) (p2.x - tamaño * Math.cos(angulo + Math.toRadians(ancho)));
        int y2 = (int) (p2.y - tamaño * Math.sin(angulo + Math.toRadians(ancho)));

        g2d.setColor(Color.RED);
        g2d.drawLine(p2.x, p2.y, x1, y1);
        g2d.drawLine(p2.x, p2.y, x2, y2);
    }
}