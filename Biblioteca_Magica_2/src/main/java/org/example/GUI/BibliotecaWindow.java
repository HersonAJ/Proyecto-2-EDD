package org.example.GUI;

import org.example.Grafo.GrafoBibliotecas;
import org.example.Modelos.Biblioteca;
import org.example.GUI.Vistas.*;

import javax.swing.*;
import java.awt.*;

public class BibliotecaWindow extends JPanel {

    private Biblioteca biblioteca;
    private JTabbedPane tabs;

    private ListadoAlfabetico listadoAlfabetico;
    private AVLViewer avlViewer;
    private BViewer bViewer;
    private BPlusViewer bPlusViewer;
    private BusquedaUnificada busquedaUnificada;
    private GrafoBibliotecas grafo;
    private PruebaRendimiento pruebaRendimiento;

    public BibliotecaWindow(Biblioteca biblioteca, GrafoBibliotecas grafo) {
        this.biblioteca = biblioteca;
        this.grafo = grafo;
        setLayout(new BorderLayout());
        initComponents();
        if (biblioteca != null) {
            actualizarVistas();
        }
    }

    private void initComponents() {
        tabs = new JTabbedPane();

        if (biblioteca != null) {
            // Pestaña de Listado Alfabético con botón de exportación
            JPanel panelListado = crearPanelConExportacion(
                    new ListadoAlfabetico(biblioteca),
                    "Listado Alfabético",
                    this::exportarListadoAlfabetico
            );
            this.listadoAlfabetico = (ListadoAlfabetico) ((JScrollPane) ((BorderLayout) panelListado.getLayout()).getLayoutComponent(BorderLayout.CENTER)).getViewport().getView();
            tabs.addTab("Listado Alfabético", panelListado);

            // Pestaña de AVL con botón de exportación
            JPanel panelAVL = crearPanelConExportacion(
                    new AVLViewer(biblioteca.getArbolTitulos()),
                    "Árbol AVL",
                    this::exportarAVL
            );
            this.avlViewer = (AVLViewer) ((JScrollPane) ((BorderLayout) panelAVL.getLayout()).getLayoutComponent(BorderLayout.CENTER)).getViewport().getView();
            tabs.addTab("AVL", panelAVL);

            // Pestaña de Árbol B con botón de exportación
            JPanel panelB = crearPanelConExportacion(
                    new BViewer(biblioteca.getArbolFechas()),
                    "Árbol B",
                    this::exportarB
            );
            this.bViewer = (BViewer) ((JScrollPane) ((BorderLayout) panelB.getLayout()).getLayoutComponent(BorderLayout.CENTER)).getViewport().getView();
            tabs.addTab("Árbol B", panelB);

            // Pestaña de Árbol B+ con botón de exportación
            JPanel panelBPlus = crearPanelConExportacion(
                    new BPlusViewer(biblioteca.getArbolGeneros()),
                    "Árbol B+",
                    this::exportarBPlus
            );
            this.bPlusViewer = (BPlusViewer) ((JScrollPane) ((BorderLayout) panelBPlus.getLayout()).getLayoutComponent(BorderLayout.CENTER)).getViewport().getView();
            tabs.addTab("Árbol B+", panelBPlus);

            // Pestaña de Búsqueda (sin exportación)
            this.busquedaUnificada = new BusquedaUnificada(
                    biblioteca.getArbolTitulos(),
                    //biblioteca.getIndiceISBN(),
                    biblioteca.getTablaHash(),
                    biblioteca.getArbolFechas(),
                    biblioteca.getArbolGeneros(),
                    (mensaje, tipo) -> {
                        System.out.println("[" + biblioteca.getId() + "][" + tipo + "] " + mensaje);
                    }
            );
            tabs.addTab("Búsqueda", busquedaUnificada);

            HistorialPrestamos historialPrestamos = new HistorialPrestamos(biblioteca, grafo);
            tabs.addTab("Historial Préstamos", historialPrestamos);

            PanelOrdenamientos panelOrdenamientos = new PanelOrdenamientos(biblioteca);
            tabs.addTab("Ordenamientos", panelOrdenamientos);

            this.pruebaRendimiento = new PruebaRendimiento(
                    biblioteca.getArbolTitulos(),
                    biblioteca.getTablaHash(),
                    biblioteca.getCatalogo()
            );
            tabs.addTab(" Rendimiento", pruebaRendimiento);

        } else {
            JLabel lblMensaje = new JLabel("Seleccione una biblioteca para cargar", JLabel.CENTER);
            tabs.addTab("Sin Biblioteca", lblMensaje);
        }

        add(tabs, BorderLayout.CENTER);
    }
    
    private JPanel crearPanelConExportacion(JComponent viewer, String nombreEstructura, Runnable accionExportar) {
        JPanel panel = new JPanel(new BorderLayout());

        // Botón de exportación
        JButton btnExportar = new JButton("Exportar " + nombreEstructura);
        btnExportar.addActionListener(e -> accionExportar.run());

        // Panel superior con el botón
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSuperior.add(btnExportar);

        // Agregar componentes al panel
        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(new JScrollPane(viewer), BorderLayout.CENTER);

        return panel;
    }

    public void actualizarVistas() {
        if (listadoAlfabetico != null) {
            listadoAlfabetico.cargarDatosEnTabla();
            avlViewer.actualizarVista();
            bViewer.actualizarVista();
            bPlusViewer.actualizarVista();
        }
    }

    private void exportarAVL() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar AVL de " + biblioteca.getId());

            // Filtros para formatos de imagen
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Imagen PNG (*.png)", "png"));
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Imagen SVG (*.svg)", "svg"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File archivo = fileChooser.getSelectedFile();
                String ruta = archivo.getAbsolutePath();

                // Determinar formato basado en la extensión seleccionada
                String formato = "png";
                if (ruta.toLowerCase().endsWith(".svg")) {
                    formato = "svg";
                }

                // Asegurar extensión correcta
                if (!ruta.toLowerCase().endsWith("." + formato)) {
                    ruta += "." + formato;
                }

                // Generar archivo DOT temporal
                String timestamp = String.valueOf(System.currentTimeMillis());
                String dotFile = "arbolAVL_" + biblioteca.getId() + "_" + timestamp + ".dot";
                biblioteca.getArbolTitulos().guardarComoDOT(dotFile);

                // Ejecutar comando dot para generar la imagen
                String comando = "dot -T" + formato + " " + dotFile + " -o " + ruta;
                Process process = Runtime.getRuntime().exec(comando);
                int exitCode = process.waitFor();

                // Limpiar archivo temporal DOT
                new java.io.File(dotFile).delete();

                if (exitCode == 0) {
                    JOptionPane.showMessageDialog(this,
                            "AVL exportado correctamente: " + ruta,
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    throw new Exception("No se pudo generar la imagen. Verifica que Graphviz esté instalado.");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error exportando AVL: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarB() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Árbol B de " + biblioteca.getId());

            // Filtros para formatos de imagen
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Imagen PNG (*.png)", "png"));
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Imagen SVG (*.svg)", "svg"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File archivo = fileChooser.getSelectedFile();
                String ruta = archivo.getAbsolutePath();

                // Determinar formato basado en la extensión seleccionada
                String formato = "png";
                if (ruta.toLowerCase().endsWith(".svg")) {
                    formato = "svg";
                }

                // Asegurar extensión correcta
                if (!ruta.toLowerCase().endsWith("." + formato)) {
                    ruta += "." + formato;
                }

                // Generar archivo DOT temporal
                String timestamp = String.valueOf(System.currentTimeMillis());
                String dotFile = "arbolB_" + biblioteca.getId() + "_" + timestamp + ".dot";

                if (!org.example.include.ExportadorDotB.generarArchivo(biblioteca.getArbolFechas(), dotFile)) {
                    throw new Exception("No se pudo generar el archivo DOT");
                }

                // Ejecutar comando dot para generar la imagen
                String comando = "dot -T" + formato + " " + dotFile + " -o " + ruta;
                Process process = Runtime.getRuntime().exec(comando);
                int exitCode = process.waitFor();

                // Limpiar archivo temporal DOT
                new java.io.File(dotFile).delete();

                if (exitCode == 0) {
                    JOptionPane.showMessageDialog(this,
                            "Árbol B exportado correctamente: " + ruta,
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    throw new Exception("No se pudo generar la imagen. Verifica que Graphviz esté instalado.");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error exportando Árbol B: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarBPlus() {
        try {
            if (biblioteca.getArbolGeneros().getRaiz() == null) {
                JOptionPane.showMessageDialog(this,
                        "El árbol B+ está vacío",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Árbol B+ de " + biblioteca.getId());

            // Filtros para formatos de imagen
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Imagen PNG (*.png)", "png"));
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Imagen SVG (*.svg)", "svg"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File archivo = fileChooser.getSelectedFile();
                String ruta = archivo.getAbsolutePath();

                // Determinar formato basado en la extensión seleccionada
                String formato = "png";
                if (ruta.toLowerCase().endsWith(".svg")) {
                    formato = "svg";
                }

                // Asegurar extensión correcta
                if (!ruta.toLowerCase().endsWith("." + formato)) {
                    ruta += "." + formato;
                }

                // Generar archivo DOT temporal
                String timestamp = String.valueOf(System.currentTimeMillis());
                String dotFile = "arbolBPlus_" + biblioteca.getId() + "_" + timestamp + ".dot";

                if (!org.example.include.ExportadorDotBPlus.generarArchivo(biblioteca.getArbolGeneros(), dotFile)) {
                    throw new Exception("No se pudo generar el archivo DOT");
                }

                // Ejecutar comando dot para generar la imagen
                String comando = "dot -T" + formato + " " + dotFile + " -o " + ruta;
                Process process = Runtime.getRuntime().exec(comando);
                int exitCode = process.waitFor();

                // Limpiar archivo temporal DOT
                new java.io.File(dotFile).delete();

                if (exitCode == 0) {
                    JOptionPane.showMessageDialog(this,
                            "Árbol B+ exportado correctamente: " + ruta,
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    throw new Exception("No se pudo generar la imagen. Verifica que Graphviz esté instalado.");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error exportando Árbol B+: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void exportarListadoAlfabetico() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Listado Alfabético de " + biblioteca.getId());

            // Filtro para archivos de texto
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Archivo de texto (*.txt)", "txt"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File archivo = fileChooser.getSelectedFile();
                String ruta = archivo.getAbsolutePath();

                // Asegurar extensión .txt
                if (!ruta.toLowerCase().endsWith(".txt")) {
                    ruta += ".txt";
                }

                // Obtener los libros en orden alfabético
                org.example.Modelos.ListaLibros libros = biblioteca.getArbolTitulos().obtenerLibrosEnOrdenAlfabetico();

                // Crear el contenido del archivo con formato de tabla
                StringBuilder contenido = new StringBuilder();
                contenido.append("LISTADO ALFABÉTICO DE LIBROS - ").append(biblioteca.getNombre()).append("\n");
                contenido.append("==============================================================================================================\n");
                contenido.append("No. | Título | Autor | ISBN | Género | Fecha | Ejemplares\n");
                contenido.append("==============================================================================================================\n");

                if (libros.estaVacia()) {
                    contenido.append("No hay libros en esta biblioteca.\n");
                } else {
                    org.example.Modelos.ListaLibros.Iterador iter = libros.obtenerIterador();
                    int contador = 1;
                    while (iter.tieneSiguiente()) {
                        org.example.Modelos.Libro libro = iter.siguiente();
                        contenido.append(String.format("%-3d | %-30s | %-20s | %-15s | %-15s | %-4s | %d\n",
                                contador,
                                truncar(libro.getTitulo(), 30),
                                truncar(libro.getAutor(), 20),
                                truncar(libro.getIsbn(), 15),
                                truncar(libro.getGenero(), 15),
                                libro.getFecha(),
                                libro.getCantidad()
                        ));
                        contador++;
                    }
                    contenido.append("==============================================================================================================\n");
                    contenido.append("Total de libros: ").append(contador - 1).append("\n");
                }

                // Escribir el archivo
                java.io.FileWriter writer = new java.io.FileWriter(ruta);
                writer.write(contenido.toString());
                writer.close();

                JOptionPane.showMessageDialog(this,
                        "Listado alfabético exportado correctamente: " + ruta,
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error exportando listado alfabético: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método auxiliar para truncar texto muy largo
    private String truncar(String texto, int maxLength) {
        if (texto == null) return "";
        if (texto.length() <= maxLength) return texto;
        return texto.substring(0, maxLength - 3) + "...";
    }
}