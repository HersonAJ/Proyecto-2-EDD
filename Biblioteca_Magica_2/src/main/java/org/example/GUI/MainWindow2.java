package org.example.GUI;

import org.example.Grafo.GrafoBibliotecas;
import org.example.Grafo.ListaAdyacencia;
import org.example.Modelos.Biblioteca;
import org.example.Grafo.Arista;
import org.example.Modelos.Libro;
import org.example.Modelos.LectorCSV;
import org.example.GUI.VistasGrafo.ConexionManual;
import org.example.GUI.VistasGrafo.AgregarBibliotecaManual;
import org.example.GUI.VistasGrafo.AgregarLibroManual;
import org.example.GUI.VistasGrafo.CargarCSVBibliotecaDialog;
import org.example.Modelos.LectorCSVBiblioteca;
import org.example.Modelos.LectorCSVConexiones;
import org.example.GUI.VistasGrafo.CargarCSVConexionesDialog;
import org.example.TablaHash.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class MainWindow2 extends JFrame {

    private GrafoBibliotecas grafo;
    private JTabbedPane tabs;
    private JTextPane logWidget;
    private BibliotecaWindow bibliotecaWindow;

    private LectorCSV lectorLibros;//cambiar al nuevo global

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

        JPanel panelBibliotecaIndividual = crearPanelBibliotecaIndividual();
        tabs.addTab("Biblioteca Individual", panelBibliotecaIndividual);

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
    private JPanel crearPanelBibliotecaIndividual() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // === Panel superior: selector de biblioteca ===
        JPanel panelSelector = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel lblSeleccion = new JLabel("Seleccione una biblioteca:");
        JComboBox<String> cmbBibliotecas = new JComboBox<>();

        // Cargar bibliotecas existentes (si las hay)
        actualizarComboBibliotecas(cmbBibliotecas);

        JButton btnCargar = new JButton("Cargar Biblioteca");
        btnCargar.addActionListener(e -> {
            String seleccion = (String) cmbBibliotecas.getSelectedItem();
            if (seleccion == null) {
                JOptionPane.showMessageDialog(this,
                        "No hay bibliotecas disponibles o ninguna seleccionada.",
                        "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String idBiblioteca = seleccion.split(" - ")[0];
            Biblioteca biblioteca = grafo.getBiblioteca(idBiblioteca);

            if (biblioteca != null) {
                // Crear y mostrar la biblioteca seleccionada
                bibliotecaWindow = new BibliotecaWindow(biblioteca);

                panel.removeAll();
                panel.add(panelSelector, BorderLayout.NORTH);
                panel.add(bibliotecaWindow, BorderLayout.CENTER);

                panel.revalidate();
                panel.repaint();

                appendLog("Biblioteca cargada: " + biblioteca.getNombre(), "ok");
            }
        });

        panelSelector.add(lblSeleccion);
        panelSelector.add(cmbBibliotecas);
        panelSelector.add(btnCargar);

        panel.add(panelSelector, BorderLayout.NORTH);

        // === Mensaje inicial ===
        JTextArea mensajeInicial = new JTextArea(
                "Seleccione una biblioteca para visualizar.\n\n" +
                        "Aquí podrá explorar la información, libros y estadísticas de cada biblioteca.");
        mensajeInicial.setEditable(false);
        mensajeInicial.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(mensajeInicial, BorderLayout.CENTER);

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
        // Crear el callback para el progreso en tiempo real
        LectorCSVBiblioteca.ProgresoCallback progresoCallback = new LectorCSVBiblioteca.ProgresoCallback() {
            @Override
            public void reportarLinea(String mensaje, String tipo) {
                appendLog(mensaje, tipo);
            }
        };

        CargarCSVBibliotecaDialog dialog = new CargarCSVBibliotecaDialog(this, grafo, () -> {
            appendLog("Proceso de carga de CSV completado", "info");
            actualizarVista();
            actualizarComboBibliotecas(buscarComboBibliotecas());
        }, progresoCallback);

        dialog.setVisible(true);
    }

    private void cargarCSVConexiones() {
        // Crear el callback para el progreso en tiempo real
        LectorCSVConexiones.ProgresoCallback progresoCallback = new LectorCSVConexiones.ProgresoCallback() {
            @Override
            public void reportarLinea(String mensaje, String tipo) {
                appendLog(mensaje, tipo);
            }
        };

        CargarCSVConexionesDialog dialog = new CargarCSVConexionesDialog(this, grafo, () -> {
            appendLog("Proceso de carga de conexiones CSV completado", "info");
            actualizarVista();
        }, progresoCallback);

        dialog.setVisible(true);
    }

    private void cargarCSVLibros() {
        appendLog("Función de carga de libros pendiente de integración", "info");
        // TODO: Integrar con el LectorCSV existente
    }

    // Métodos para gestión manual
    private void agregarBibliotecaManual() {
        AgregarBibliotecaManual dialog = new AgregarBibliotecaManual(this, grafo, () -> {
                    appendLog("Biblioteca agregada manualmente", "ok");
                    actualizarVista();
                }
        );
        dialog.setVisible(true);
        actualizarComboBibliotecas(buscarComboBibliotecas());
    }


    private void agregarConexionManual() {
        ConexionManual dialog = new ConexionManual(this, grafo, () -> {
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

        AgregarLibroManual dialog = new AgregarLibroManual(this, grafo, () -> {
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
        sb.append("• Conexiones: ").append(grafo.getTodasLasAristas().getTamaño()).append("\n\n");

        sb.append("BIBLIOTECAS:\n");
        TablaHash<String, Biblioteca> bibliotecas = grafo.getBibliotecas();
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

    private void actualizarComboBibliotecas(JComboBox<String> combo) {
        combo.removeAllItems();
        if (grafo.getBibliotecas().isEmpty()) {
            combo.addItem("(No hay bibliotecas disponibles)");
            combo.setEnabled(false);
            return;
        }
        combo.setEnabled(true);
        TablaHash<String, Biblioteca> bibliotecas = grafo.getBibliotecas();
        Iterador<String> iterador = bibliotecas.iteradorClaves();
        while (iterador.tieneSiguiente()) {
            String id = iterador.siguiente();
            Biblioteca bib = grafo.getBiblioteca(id);
            combo.addItem(id + " - " + bib.getNombre() + " (" + bib.getUbicacion() + ")");
        }
    }

    private JComboBox<String> buscarComboBibliotecas() {
        try {
            JPanel panel = (JPanel) tabs.getComponentAt(2);
            JPanel panelSelector = (JPanel) ((BorderLayout) panel.getLayout()).getLayoutComponent(BorderLayout.NORTH);
            for (Component c : panelSelector.getComponents()) {
                if (c instanceof JComboBox) {
                    @SuppressWarnings("unchecked")
                    JComboBox<String> combo = (JComboBox<String>) c;
                    return combo;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

}