package org.example.GUI;

import org.example.Grafo.GrafoBibliotecas;
import org.example.Modelos.Biblioteca;
import org.example.Grafo.Arista;
import org.example.Modelos.Libro;
import org.example.Modelos.LectorCSV;
import org.example.GUI.VistasGrafo.ConexionManual;
import org.example.GUI.VistasGrafo.AgregarBibliotecaManual;
import org.example.GUI.VistasGrafo.AgregarLibroManual;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class MainWindow2 extends JFrame {

    private GrafoBibliotecas grafo;
    private JTabbedPane tabs;
    private JTextPane logWidget;
    private ConexionManual conexionManual;

    // Para compatibilidad temporal con el sistema actual
    private LectorCSV lectorLibros;

    public MainWindow2() {
        super("Sistema de Red de Bibliotecas Mágicas");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.grafo = new GrafoBibliotecas();

        initComponents();
        createMenu();
    }

    private void initComponents() {
        // Panel principal con pestañas
        tabs = new JTabbedPane();

        // Panel de log
        logWidget = new JTextPane();
        logWidget.setEditable(false);
        tabs.addTab("Log del Sistema", new JScrollPane(logWidget));

        // Panel de información del grafo
        JPanel panelInfo = crearPanelInformacion();
        tabs.addTab("Información de la Red", panelInfo);

        setContentPane(tabs);
    }

    private JPanel crearPanelInformacion() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JButton btnActualizar = new JButton("Actualizar Información");
        btnActualizar.addActionListener(e -> {
            textArea.setText(obtenerInformacionRed());
        });

        JButton btnVisualizarGrafo = new JButton("Visualizar Grafo");
        btnVisualizarGrafo.addActionListener(e -> {
            abrirVisualizacionGrafo();
        });

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnActualizar);
        panelBotones.add(btnVisualizarGrafo);

        panel.add(panelBotones, BorderLayout.NORTH);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Cargar información inicial
        textArea.setText(obtenerInformacionRed());

        return panel;
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");

        JMenuItem cargarBibliotecas = new JMenuItem("Cargar Bibliotecas (CSV)");
        cargarBibliotecas.addActionListener(e -> cargarCSVBibliotecas());
        menuArchivo.add(cargarBibliotecas);

        JMenuItem cargarConexiones = new JMenuItem("Cargar Conexiones (CSV)");
        cargarConexiones.addActionListener(e -> cargarCSVConexiones());
        menuArchivo.add(cargarConexiones);

        JMenuItem cargarLibros = new JMenuItem("Cargar Libros (CSV)");
        cargarLibros.addActionListener(e -> cargarCSVLibros());
        menuArchivo.add(cargarLibros);

        menuArchivo.addSeparator();

        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> dispose());
        menuArchivo.add(salir);

        // Menú Gestión Manual
        JMenu menuGestion = new JMenu("Gestión Manual");

        JMenuItem agregarBiblioteca = new JMenuItem("Agregar Biblioteca");
        agregarBiblioteca.addActionListener(e -> agregarBibliotecaManual());
        menuGestion.add(agregarBiblioteca);

        JMenuItem agregarConexion = new JMenuItem("Agregar Conexión");
        agregarConexion.addActionListener(e -> agregarConexionManual());
        menuGestion.add(agregarConexion);

        JMenuItem agregarLibro = new JMenuItem("Agregar Libro");
        agregarLibro.addActionListener(e -> agregarLibroManual());
        menuGestion.add(agregarLibro);

        // Menú Visualización
        JMenu menuVisualizacion = new JMenu("Visualización");

        JMenuItem verGrafo = new JMenuItem("Ver Grafo de Bibliotecas");
        verGrafo.addActionListener(e -> abrirVisualizacionGrafo());
        menuVisualizacion.add(verGrafo);

        // Agregar menús
        menuBar.add(menuArchivo);
        menuBar.add(menuGestion);
        menuBar.add(menuVisualizacion);

        setJMenuBar(menuBar);
    }

    // Métodos para cargar archivos CSV
    private void cargarCSVBibliotecas() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                int contador = 0;
                BufferedReader br = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
                String linea;

                // Saltar header
                br.readLine();

                while ((linea = br.readLine()) != null) {
                    String[] datos = linea.split(",");
                    if (datos.length >= 6) {
                        String id = datos[0].replace("\"", "").trim();
                        String nombre = datos[1].replace("\"", "").trim();
                        String ubicacion = datos[2].replace("\"", "").trim();
                        int tIngreso = Integer.parseInt(datos[3].trim());
                        int tTraspaso = Integer.parseInt(datos[4].trim());
                        int intervalo = Integer.parseInt(datos[5].trim());

                        grafo.agregarBiblioteca(id, nombre, ubicacion, tIngreso, tTraspaso, intervalo);
                        contador++;
                    }
                }
                br.close();

                appendLog("Cargadas " + contador + " bibliotecas desde CSV", "ok");
                actualizarVista();

            } catch (Exception e) {
                appendLog("Error cargando bibliotecas: " + e.getMessage(), "error");
            }
        }
    }

    private void cargarCSVConexiones() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                int contador = 0;
                BufferedReader br = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
                String linea;

                // Saltar header
                br.readLine();

                while ((linea = br.readLine()) != null) {
                    String[] datos = linea.split(",");
                    if (datos.length >= 4) {
                        String origen = datos[0].replace("\"", "").trim();
                        String destino = datos[1].replace("\"", "").trim();
                        int tiempo = Integer.parseInt(datos[2].trim());
                        double costo = Double.parseDouble(datos[3].trim());

                        grafo.conectarBibliotecas(origen, destino, tiempo, costo);
                        contador++;
                    }
                }
                br.close();

                appendLog("Cargadas " + contador + " conexiones desde CSV", "ok");
                actualizarVista();

            } catch (Exception e) {
                appendLog("Error cargando conexiones: " + e.getMessage(), "error");
            }
        }
    }

    private void cargarCSVLibros() {
        appendLog("Función de carga de libros pendiente de integración", "info");
        // TODO: Integrar con el LectorCSV existente
    }

    // Métodos para gestión manual
    private void agregarBibliotecaManual() {
        AgregarBibliotecaManual dialog = new AgregarBibliotecaManual(
                this,
                grafo,
                () -> {
                    appendLog("Biblioteca agregada manualmente", "ok");
                    actualizarVista();
                }
        );
        dialog.setVisible(true);
    }


    private void agregarConexionManual() {
        ConexionManual dialog = new ConexionManual(
                this,
                grafo,
                () -> {
                    appendLog("Conexión agregada manualmente", "ok");
                    actualizarVista();
                }
        );
        dialog.setVisible(true);
    }
    private void agregarLibroManual() {
        if (grafo.getBibliotecas().isEmpty()) {
            appendLog("Error: No hay bibliotecas disponibles. Agrega una biblioteca primero.", "error");
            JOptionPane.showMessageDialog(this,
                    "No hay bibliotecas disponibles. Agrega una biblioteca primero.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AgregarLibroManual dialog = new AgregarLibroManual(
                this,
                grafo,
                () -> {
                    appendLog("Libro agregado manualmente a la biblioteca seleccionada", "ok");
                }
        );
        dialog.setVisible(true);
    }

    private void abrirVisualizacionGrafo() {
        MainWindowGrafo ventanaGrafo = new MainWindowGrafo(grafo);
        ventanaGrafo.setVisible(true);
    }

    private String obtenerInformacionRed() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SISTEMA DE BIBLIOTECAS MÁGICAS ===\n\n");

        sb.append("RESUMEN:\n");
        sb.append("• Bibliotecas: ").append(grafo.getBibliotecas().size()).append("\n");
        sb.append("• Conexiones: ").append(grafo.getTodasLasAristas().size()).append("\n\n");

        sb.append("BIBLIOTECAS:\n");
        for (Biblioteca bib : grafo.getBibliotecas().values()) {
            sb.append("• ").append(bib.getId())
                    .append(" - ").append(bib.getNombre())
                    .append(" (").append(bib.getUbicacion()).append(")\n")
                    .append("  Tiempos [Ingreso:").append(bib.getTiempoIngreso())
                    .append("s, Traspaso:").append(bib.getTiempoTraspaso())
                    .append("s, Intervalo:").append(bib.getIntervaloDespacho()).append("s]\n");
        }

        sb.append("\nCONEXIONES:\n");
        for (Arista arista : grafo.getTodasLasAristas()) {
            sb.append("• ").append(arista.getIdOrigen())
                    .append(" → ").append(arista.getIdDestino())
                    .append(" [Tiempo:").append(arista.getTiempo())
                    .append("s, Costo:").append(arista.getCosto()).append("]\n");
        }

        return sb.toString();
    }

    private void actualizarVista() {
        // Actualizar la pestaña de información
        tabs.setComponentAt(1, crearPanelInformacion());
    }

    private void appendLog(String mensaje, String tipo) {
        SwingUtilities.invokeLater(() -> {
            try {
                javax.swing.text.StyledDocument doc = logWidget.getStyledDocument();

                javax.swing.text.Style typeStyle = logWidget.addStyle("Type", null);
                Color color;
                switch (tipo.toLowerCase()) {
                    case "ok": color = new Color(0, 128, 0); break;
                    case "error": color = Color.RED; break;
                    case "info": color = new Color(0, 0, 192); break;
                    default: color = Color.BLACK;
                }
                javax.swing.text.StyleConstants.setForeground(typeStyle, color);
                javax.swing.text.StyleConstants.setBold(typeStyle, true);
                doc.insertString(doc.getLength(), tipo.toUpperCase() + ": ", typeStyle);

                javax.swing.text.Style messageStyle = logWidget.addStyle("Message", null);
                javax.swing.text.StyleConstants.setForeground(messageStyle, Color.BLACK);
                doc.insertString(doc.getLength(), mensaje + "\n", messageStyle);

                logWidget.setCaretPosition(doc.getLength());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}