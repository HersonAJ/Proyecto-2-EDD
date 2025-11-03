package org.example.GUI;

import javax.swing.*;

public class MainWindow extends JFrame {
    /*

    private JTabbedPane tabs;
    private JTextPane logWidget;

    private ListadoAlfabetico listadoAlfabetico;
    private AVLViewer avlViewer;
    private BViewer bViewer;
    private BPlusViewer bPlusViewer;
    private BusquedaUnificada busquedaUniificada;
    private PruebaRendimiento rendimiento;
    private LectorCSV lector;
    private ArbolAVL arbol;
    private ArbolB arbolB;
    private ArbolBPlus arbolBPlus;
    private IndiceISBN indiceGlobal;
    private Catalogo catalogo;

    public MainWindow() {
        super("Biblioteca Magica");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.arbol = new ArbolAVL();
        this.arbolB = new ArbolB();
        this.arbolBPlus = new ArbolBPlus();
        this.indiceGlobal = new IndiceISBN();
        this.catalogo = new Catalogo();
        this.lector = new LectorCSV(arbol, arbolB, indiceGlobal, arbolBPlus, catalogo, this::appendLog);

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

        busquedaUniificada = new BusquedaUnificada(
                this.arbol,           // ArbolAVL arbolTitulos
                this.indiceGlobal,    // IndiceISBN indiceISBN
                this.arbolB,          // ArbolB arbolB
                this.arbolBPlus,      // ArbolBPlus arbolBPlus
                this::appendLog       // BiConsumer<String, String> logCallBack
        );
        tabs.addTab("Busqueda" ,busquedaUniificada);

        rendimiento = new PruebaRendimiento(this.arbol, this.indiceGlobal, this.catalogo);
        tabs.addTab("Rendimiento" ,rendimiento);

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

    private void onCargarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv"));

        int resultado = fileChooser.showOpenDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) return;

        java.io.File archivo = fileChooser.getSelectedFile();
        String ruta = archivo.getAbsolutePath();

        appendLog("Cargando archivo: " + archivo.getName(), "info");

        // Deshabilitar menú mientras carga
        setEnabled(false);

        // Ejecutar en segundo plano
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    lector.procesarArchivo(ruta);
                } catch (Exception e) {
                    publish("Error al procesar archivo: " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void process(java.util.List<String> mensajes) {
                for (String msg : mensajes) appendLog(msg, "info");
            }

            @Override
            protected void done() {
                try {
                    get(); // Propaga excepciones
                    appendLog("Archivo cargado exitosamente: " + archivo.getName(), "ok");
                    actualizarTodasLasVistas();
                } catch (Exception e) {
                    appendLog("Error al cargar el archivo: " + e.getMessage(), "error");
                    JOptionPane.showMessageDialog(MainWindow.this,
                            "Error al cargar el archivo:\n" + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void onExportarAVL() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar imagen del árbol AVL");

        // Filtros para SVG y PNG
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imagen SVG (*.svg)", "svg"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imagen PNG (*.png)", "png"));

        int resultado = fileChooser.showSaveDialog(this);

        if (resultado != JFileChooser.APPROVE_OPTION) return;

        java.io.File archivo = fileChooser.getSelectedFile();
        String ruta = archivo.getAbsolutePath();

        // Nombre único con timestamp para evitar solapamientos
        String timestamp = String.valueOf(System.currentTimeMillis());
        String dotFile = "arbol_export_" + timestamp + ".dot";

        // Determinar formato basado en extensión del archivo
        String formato = ruta.toLowerCase().endsWith(".svg") ? "svg" : "png";

        // Asegurar extensión correcta
        if (!ruta.toLowerCase().endsWith("." + formato)) {
            ruta += "." + formato;
        }

        // Generar DOT del AVL (necesitas implementar este método en ArbolAVL)
        arbol.guardarComoDOT(dotFile);

        try {
            // Ejecutar comando dot
            String comando = "dot -T" + formato + " " + dotFile + " -o " + ruta ;
            Process process = Runtime.getRuntime().exec(comando);
            int exitCode = process.waitFor();


            // Limpiar archivo temporal DOT
            new java.io.File(dotFile).delete();

            if (exitCode == 0) {
                appendLog("Árbol AVL exportado: " + ruta + " (" + formato + ")", "ok");
                JOptionPane.showMessageDialog(this,
                        "Árbol AVL exportado correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                appendLog("Error al generar la imagen del árbol AVL.", "error");
                JOptionPane.showMessageDialog(this,
                        "No se pudo generar la imagen. Verifica que Graphviz esté instalado.",
                        "Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            appendLog("Error al exportar árbol AVL: " + e.getMessage(), "error");
            JOptionPane.showMessageDialog(this,
                    "Error al exportar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onExportarB() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar imagen del Árbol B");

        // Filtros para SVG y PNG
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imagen SVG (*.svg)", "svg"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imagen PNG (*.png)", "png"));

        int resultado = fileChooser.showSaveDialog(this);

        if (resultado != JFileChooser.APPROVE_OPTION) return;

        java.io.File archivo = fileChooser.getSelectedFile();
        String ruta = archivo.getAbsolutePath();

        // Nombre único con timestamp para evitar solapamientos
        String timestamp = String.valueOf(System.currentTimeMillis());
        String dotFile = "arbolB_export_" + timestamp + ".dot";

        // Determinar formato basado en extensión del archivo
        String formato = ruta.toLowerCase().endsWith(".svg") ? "svg" : "png";

        // Asegurar extensión correcta
        if (!ruta.toLowerCase().endsWith("." + formato)) {
            ruta += "." + formato;
        }

        // Generar DOT del Árbol B
        if (!org.example.include.ExportadorDotB.generarArchivo(arbolB, dotFile)) {
            appendLog("Error al generar archivo DOT del Árbol B.", "error");
            JOptionPane.showMessageDialog(this,
                    "No se pudo generar el archivo DOT.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Ejecutar comando dot
            String comando = "dot -T" + formato + " " + dotFile + " -o " + ruta;
            Process process = Runtime.getRuntime().exec(comando);
            int exitCode = process.waitFor();

            // Limpiar archivo temporal DOT
            new java.io.File(dotFile).delete();

            if (exitCode == 0) {
                appendLog("Árbol B exportado: " + ruta + " (" + formato + ")", "ok");
                JOptionPane.showMessageDialog(this,
                        "Árbol B exportado correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                appendLog("Error al generar la imagen del Árbol B.", "error");
                JOptionPane.showMessageDialog(this,
                        "No se pudo generar la imagen. Verifica que Graphviz esté instalado.",
                        "Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            appendLog("Error al exportar árbol B: " + e.getMessage(), "error");
            JOptionPane.showMessageDialog(this,
                    "Error al exportar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onExportarBPlus() {
        if (arbolBPlus.getRaiz() == null) {
            appendLog("El árbol B+ está vacío. No hay nada que exportar.", "error");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar imagen del Árbol B+");

        // Filtros para SVG y PNG
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imagen SVG (*.svg)", "svg"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imagen PNG (*.png)", "png"));

        int resultado = fileChooser.showSaveDialog(this);

        if (resultado != JFileChooser.APPROVE_OPTION) return;

        java.io.File archivo = fileChooser.getSelectedFile();
        String ruta = archivo.getAbsolutePath();

        // Nombre único con timestamp para evitar solapamientos
        String timestamp = String.valueOf(System.currentTimeMillis());
        String dotFile = "arbolBPlus_export_" + timestamp + ".dot";

        // Determinar formato basado en extensión del archivo
        String formato = ruta.toLowerCase().endsWith(".svg") ? "svg" : "png";

        // Asegurar extensión correcta
        if (!ruta.toLowerCase().endsWith("." + formato)) {
            ruta += "." + formato;
        }

        // Generar DOT del Árbol B+
        if (!org.example.include.ExportadorDotBPlus.generarArchivo(arbolBPlus, dotFile)) {
            appendLog("Error al generar archivo DOT del Árbol B+.", "error");
            JOptionPane.showMessageDialog(this,
                    "No se pudo generar el archivo DOT.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Ejecutar comando dot
            String comando = "dot -T" + formato + " " + dotFile + " -o " + ruta;
            Process process = Runtime.getRuntime().exec(comando);
            int exitCode = process.waitFor();

            // Limpiar archivo temporal DOT
            new java.io.File(dotFile).delete();

            if (exitCode == 0) {
                appendLog("Árbol B+ exportado: " + ruta + " (" + formato + ")", "ok");
                JOptionPane.showMessageDialog(this,
                        "Árbol B+ exportado correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                appendLog("Error al generar la imagen del Árbol B+.", "error");
                JOptionPane.showMessageDialog(this,
                        "No se pudo generar la imagen. Verifica que Graphviz esté instalado.",
                        "Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            appendLog("Error al exportar árbol B+: " + e.getMessage(), "error");
            JOptionPane.showMessageDialog(this,
                    "Error al exportar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAgregarLibro() {
        // Diálogo personalizado para ingresar datos del libro
        JDialog dialog = new JDialog(this, "Agregar Nuevo Libro", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        // Panel de formulario
        JPanel panelForm = new JPanel(new GridLayout(5, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Campos de entrada
        JTextField editTitulo = new JTextField();
        JTextField editISBN = new JTextField();
        JTextField editGenero = new JTextField();
        JTextField editFecha = new JTextField();
        JTextField editAutor = new JTextField();

        // Agregar campos al formulario
        panelForm.add(new JLabel("Título:"));
        panelForm.add(editTitulo);
        panelForm.add(new JLabel("ISBN:"));
        panelForm.add(editISBN);
        panelForm.add(new JLabel("Género:"));
        panelForm.add(editGenero);
        panelForm.add(new JLabel("Fecha (año):"));
        panelForm.add(editFecha);
        panelForm.add(new JLabel("Autor:"));
        panelForm.add(editAutor);

        dialog.add(panelForm, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        btnAceptar.addActionListener(e -> {
            // Obtener datos
            String titulo = editTitulo.getText().trim();
            String isbn = editISBN.getText().trim();
            String genero = editGenero.getText().trim();
            String fecha = editFecha.getText().trim();
            String autor = editAutor.getText().trim();

            // Validaciones
            if (titulo.isEmpty() || isbn.isEmpty() || genero.isEmpty() || fecha.isEmpty() || autor.isEmpty()) {
                appendLog("Error: Todos los campos son obligatorios", "error");
                JOptionPane.showMessageDialog(dialog,
                        "Todos los campos son obligatorios.",
                        "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar que la fecha sea numérica
            try {
                Integer.parseInt(fecha);
            } catch (NumberFormatException ex) {
                appendLog("Error: La fecha debe ser un año válido", "error");
                JOptionPane.showMessageDialog(dialog,
                        "La fecha debe ser un año válido (ej: 2023).",
                        "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Usar el LectorCSV para agregar el libro
            boolean exito = lector.agregarLibroIndividual(titulo, isbn, genero, fecha, autor);

            if (exito) {
                // Actualizar vistas
                actualizarTodasLasVistas();

                appendLog("Libro agregado manualmente: " + titulo + " - ISBN: " + isbn, "ok");
                JOptionPane.showMessageDialog(dialog,
                        "Libro agregado correctamente al sistema.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                appendLog("Error al agregar libro manualmente: " + titulo, "error");
                JOptionPane.showMessageDialog(dialog,
                        "No se pudo agregar el libro. Verifique que el ISBN no exista y los datos sean válidos.",
                        "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        dialog.add(panelBotones, BorderLayout.SOUTH);

        // Mostrar diálogo
        dialog.setVisible(true);
    }
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
        boolean eliminadoDelCatalogo = catalogo.eliminarLibroPorISBN(isbnStr); // Catálogo global

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
                doc.insertString(doc.getLength(),  tipo.toUpperCase() + ": ", typeStyle);

                javax.swing.text.Style messageStyle = logWidget.addStyle("Message", null);
                javax.swing.text.StyleConstants.setForeground(messageStyle, Color.BLACK);
                doc.insertString(doc.getLength(), mensaje + "\n", messageStyle);

                logWidget.setCaretPosition(doc.getLength());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
*/
}