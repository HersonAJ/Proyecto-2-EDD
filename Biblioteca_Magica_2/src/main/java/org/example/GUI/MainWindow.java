package org.example.GUI;

import org.example.GUI.Vistas.*;
import org.example.Modelos.LectorCSV;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.example.AVL.ArbolAVL;
import org.example.B.ArbolB;
import org.example.BPlus.ArbolBPlus;
import org.example.AVL_Auxiliar.IndiceISBN;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private JTabbedPane tabs;
    private JTextPane logWidget;

    private ListadoAlfabetico listadoAlfabetico;
    private AVLViewer avlViewer;
    private BViewer bViewer;
    private BPlusViewer bPlusViewer;
    private BusquedaUnificada busquedaUniificada;
    private Object rendimiento;
    private LectorCSV lector;
    private ArbolAVL arbol;
    private ArbolB arbolB;
    private ArbolBPlus arbolBPlus;
    private IndiceISBN indiceGlobal;

    public MainWindow() {
        super("Biblioteca Magica");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.arbol = new ArbolAVL();
        this.arbolB = new ArbolB();
        this.arbolBPlus = new ArbolBPlus();
        this.indiceGlobal = new IndiceISBN();
        this.lector = new LectorCSV(arbol, arbolB, indiceGlobal, arbolBPlus,this::appendLog);

        //layout centrañ
        JPanel central = new JPanel(new BorderLayout());
        tabs = new JTabbedPane();

        //panel log
        logWidget = new JTextPane();
        logWidget.setEditable(false);
        tabs.addTab("Log", new JScrollPane(logWidget));

        this.listadoAlfabetico = new ListadoAlfabetico(arbol);
        tabs.addTab("Listado Alfabetico", listadoAlfabetico);

        //ventanas
        avlViewer = new AVLViewer(this.arbol);
        tabs.addTab("AVL" ,avlViewer);

        bViewer = new BViewer(this.arbolB);
        tabs.addTab("B" ,bViewer);

        bPlusViewer = new BPlusViewer(this.arbolBPlus);
        tabs.addTab("B+" ,bPlusViewer);

        busquedaUniificada = new BusquedaUnificada();
        tabs.addTab("Busqueda" ,busquedaUniificada);

        central.add(tabs, BorderLayout.CENTER);
        setContentPane(central);

        createMenu();

    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        //menu inicio
        JMenu menuArchivo = new JMenu("Archivo");

        JMenuItem actionCargar = new JMenuItem("Cargar archivo .csv");
        actionCargar.addActionListener(e -> onCargarArchivo());
        menuArchivo.add(actionCargar);

        JMenuItem actionExportarAVL = new JMenuItem("Export avl");
        actionExportarAVL.addActionListener(e -> onExportarAVL());
        menuArchivo.add(actionExportarAVL);

        JMenuItem actionExportarB = new JMenuItem("Export B");
        actionExportarB.addActionListener(e -> onExportarB());
        menuArchivo.add(actionExportarB);

        JMenuItem actionExportarBPlus = new JMenuItem("Export B+");
        actionExportarBPlus.addActionListener(e -> onExportarBPlus());
        menuArchivo.add(actionExportarBPlus);

        menuArchivo.addSeparator();

        JMenuItem actionSalir = new JMenuItem("Salir");
        actionSalir.addActionListener(e -> dispose());
        menuArchivo.add(actionSalir);

        // Menú Libros
        JMenu menuLibros = new JMenu("Libros");

        JMenuItem actionAgregar = new JMenuItem("Agregar libro");
        actionAgregar.addActionListener(e -> onAgregarLibro());
        menuLibros.add(actionAgregar);

        JMenuItem actionEliminar = new JMenuItem("Eliminar libro");
        actionEliminar.addActionListener(e -> onEliminarLibro());
        menuLibros.add(actionEliminar);

        JMenuItem actionBuscarUnificado = new JMenuItem("Buscar un libro");
        actionBuscarUnificado.addActionListener(e -> {
            // Aquí luego se implementará el cambio de pestaña
        });
        menuLibros.add(actionBuscarUnificado);

        // Menú Visualización
        JMenu menuVisualizacion = new JMenu("Visualización");

        JMenuItem actionVerListado = new JMenuItem("Ver Listado Alfabético");
        actionVerListado.addActionListener(e -> {
            listadoAlfabetico.cargarDatosEnTabla();
            tabs.setSelectedComponent(listadoAlfabetico);
        });
        menuVisualizacion.add(actionVerListado);

        JMenuItem actionVerAVL = new JMenuItem("Ver AVL");
        actionVerAVL.addActionListener(e -> {
            avlViewer.actualizarVista();
        });
        menuVisualizacion.add(actionVerAVL);

        JMenuItem actionVerB = new JMenuItem("Ver árbol B");
        actionVerB.addActionListener(e -> {
            bViewer.actualizarVista();
        });
        menuVisualizacion.add(actionVerB);

        JMenuItem actionVerBPlus = new JMenuItem("Ver árbol B+");
        actionVerBPlus.addActionListener(e -> {
            bPlusViewer.actualizarVista();
        });
        menuVisualizacion.add(actionVerBPlus);

        JMenuItem actionCompararRendimiento = new JMenuItem("Comparar rendimiento");
        actionCompararRendimiento.addActionListener(e -> {
            // tabs.setSelectedComponent(rendimiento);
        });
        menuVisualizacion.add(actionCompararRendimiento);

        // Agregar menús a la barra
        menuBar.add(menuArchivo);
        menuBar.add(menuLibros);
        menuBar.add(menuVisualizacion);

        setJMenuBar(menuBar);
    }

    // Métodos vacíos (se implementarán después)
    private void onCargarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv"));

        int resultado = fileChooser.showOpenDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File archivo = fileChooser.getSelectedFile();
                String ruta = archivo.getAbsolutePath();

                appendLog("Cargando archivo: " + archivo.getName(), "info");

                lector.procesarArchivo(ruta);

                appendLog("Archivo cargado exitosamente: " + archivo.getName(), "ok");

                avlViewer.setArbol(arbol);
                avlViewer.actualizarVista();
                listadoAlfabetico.cargarDatosEnTabla();
                bViewer.actualizarVista();
                bPlusViewer.actualizarVista();

            } catch (Exception e) {
                appendLog("Error al cargar el archivo: " + e.getMessage(), "error");
                JOptionPane.showMessageDialog(this,
                        "Error al cargar el archivo:\n" + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onExportarAVL() {}
    private void onExportarB() {}
    private void onExportarBPlus() {}
    private void onAgregarLibro() {}
    private void onEliminarLibro() {
        if (arbol.estaVacio()) {
            appendLog("El árbol está vacío. No hay libros para eliminar.", "error");
            return;
        }

        String isbn = JOptionPane.showInputDialog(this,
                "ISBN del libro a eliminar:",
                "Eliminar libro",
                JOptionPane.QUESTION_MESSAGE);

        if (isbn == null || isbn.trim().isEmpty()) return;

        String isbnStr = isbn.trim();

        // Búsqueda en el índice global
        org.example.Modelos.Libro libro = indiceGlobal.buscar(isbnStr);
        if (libro == null) {
            appendLog("Libro con ISBN '" + isbnStr + "' no encontrado.", "error");
            return;
        }

        // Obtener todos los datos necesarios
        String titulo = libro.getTitulo();
        String fecha = libro.getFecha();
        String genero = libro.getGenero();

        // Eliminar de todas las estructuras
        arbol.eliminarPorISBN(isbnStr, titulo);      // AVL general
        arbolB.eliminarPorISBN(isbnStr, fecha);      // Árbol B
        arbolBPlus.eliminarPorISBN(isbnStr, genero); // Árbol B+ (comentado hasta que exista)
        // boolean eliminadoDelCatalogo = catalogoGlobal.eliminarLibroPorISBN(isbnStr); // Catálogo global

        // Eliminar del índice global
        indiceGlobal.eliminar(isbnStr);

        appendLog("Libro eliminado con ISBN: " + isbnStr, "ok");
        JOptionPane.showMessageDialog(this,
                "El libro ha sido eliminado correctamente.",
                "Eliminado",
                JOptionPane.INFORMATION_MESSAGE);

        actualizarTodasLasVistas();
    }

    private void actualizarTodasLasVistas() {
        // Actualizar todas las vistas visuales
        avlViewer.actualizarVista();
        bViewer.actualizarVista();
        bPlusViewer.actualizarVista();
        listadoAlfabetico.cargarDatosEnTabla();
    }
    private void appendLog(String mensaje, String tipo) {
        String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));

        try {
            javax.swing.text.StyledDocument doc = logWidget.getStyledDocument();

            // Tipo de mensaje con color
            javax.swing.text.Style typeStyle = logWidget.addStyle("Type", null);
            Color color;
            switch (tipo.toLowerCase()) {
                case "ok": color = new Color(0, 128, 0); break; // Verde oscuro
                case "error": color = Color.RED; break;
                case "info": color = new Color(0, 0, 192); break; // Azul
                default: color = Color.BLACK;
            }
            javax.swing.text.StyleConstants.setForeground(typeStyle, color);
            javax.swing.text.StyleConstants.setBold(typeStyle, true);
            doc.insertString(doc.getLength(), tipo.toUpperCase() + ": ", typeStyle);

            // Mensaje normal
            javax.swing.text.Style messageStyle = logWidget.addStyle("Message", null);
            javax.swing.text.StyleConstants.setForeground(messageStyle, Color.BLACK);
            doc.insertString(doc.getLength(), mensaje + "\n", messageStyle);

            // Auto-scroll
            logWidget.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


