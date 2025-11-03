package org.example.GUI.VistasIndividuales;

import org.example.Modelos.Biblioteca;
import org.example.Modelos.Libro;
import org.example.include.Ordenamientos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PanelOrdenamientos extends JPanel {
    private Biblioteca biblioteca;
    private JComboBox<String> cmbCriterio;
    private JButton btnEjecutarComparativa;
    private JButton btnLimpiar;
    private JTable tablaResultados;
    private JTable tablaLibrosOrdenados;
    private JLabel lblEstado;
    private JProgressBar progressBar;

    // Resultados de la comparativa
    private List<ResultadoOrdenamiento> resultados;

    public PanelOrdenamientos(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        this.resultados = new ArrayList<>();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel superior - Controles
        JPanel panelSuperior = crearPanelControles();
        add(panelSuperior, BorderLayout.NORTH);

        // Panel central - Resultados y libros ordenados
        JPanel panelCentral = crearPanelCentral();
        add(panelCentral, BorderLayout.CENTER);
    }

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Configuración de Ordenamiento"));

        // Panel de selección
        JPanel panelSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel lblCriterio = new JLabel("Criterio de ordenamiento:");
        cmbCriterio = new JComboBox<>(new String[]{
                "Título", "Autor", "ISBN", "Año", "Género"
        });
        cmbCriterio.setPreferredSize(new Dimension(120, 25));

        panelSeleccion.add(lblCriterio);
        panelSeleccion.add(cmbCriterio);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        btnEjecutarComparativa = new JButton("Ejecutar Comparativa");
        btnEjecutarComparativa.setBackground(new Color(70, 130, 180));
        btnEjecutarComparativa.setForeground(Color.WHITE);
        btnEjecutarComparativa.setFocusPainted(false);

        btnLimpiar = new JButton("🗑️ Limpiar Resultados");
        btnLimpiar.setFocusPainted(false);

        panelBotones.add(btnEjecutarComparativa);
        panelBotones.add(btnLimpiar);

        // Barra de progreso y estado
        JPanel panelEstado = new JPanel(new BorderLayout(5, 5));

        progressBar = new JProgressBar();
        progressBar.setVisible(false);

        lblEstado = new JLabel("Seleccione un criterio y haga click en 'Ejecutar Comparativa'");
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 12));

        panelEstado.add(progressBar, BorderLayout.NORTH);
        panelEstado.add(lblEstado, BorderLayout.CENTER);

        // Agregar componentes al panel principal
        panel.add(panelSeleccion, BorderLayout.NORTH);
        panel.add(panelBotones, BorderLayout.CENTER);
        panel.add(panelEstado, BorderLayout.SOUTH);

        // Listeners
        btnEjecutarComparativa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarComparativa();
            }
        });

        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarResultados();
            }
        });

        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));

        // Panel de resultados de comparativa
        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBorder(BorderFactory.createTitledBorder("📊 Resultados de la Comparativa"));

        tablaResultados = crearTablaResultados();
        JScrollPane scrollResultados = new JScrollPane(tablaResultados);
        panelResultados.add(scrollResultados, BorderLayout.CENTER);

        // Panel de libros ordenados
        JPanel panelLibros = new JPanel(new BorderLayout());
        panelLibros.setBorder(BorderFactory.createTitledBorder("Libros Ordenados (Método Más Rápido)"));

        tablaLibrosOrdenados = crearTablaLibros();
        JScrollPane scrollLibros = new JScrollPane(tablaLibrosOrdenados);
        panelLibros.add(scrollLibros, BorderLayout.CENTER);

        panel.add(panelResultados);
        panel.add(panelLibros);

        return panel;
    }

    private JTable crearTablaResultados() {
        String[] columnas = {"Método", "Tiempo (nanosegundos)", "Tiempo (milisegundos)", "Estado"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabla = new JTable(model);
        tabla.getTableHeader().setReorderingAllowed(false);

        // Ajustar anchos
        tabla.getColumnModel().getColumn(0).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100);

        return tabla;
    }

    private JTable crearTablaLibros() {
        String[] columnas = {"Título", "Autor", "ISBN", "Género", "Año", "Estado"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabla = new JTable(model);
        tabla.getTableHeader().setReorderingAllowed(false);
        return tabla;
    }

    private void ejecutarComparativa() {
        String criterioSeleccionado = (String) cmbCriterio.getSelectedItem();
        if (criterioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un criterio de ordenamiento", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener libros del catálogo
        List<Libro> librosOriginal = biblioteca.getCatalogo().obtenerTodosLosLibros();
        if (librosOriginal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay libros en el catálogo para ordenar", "Catálogo Vacío", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Configurar interfaz durante procesamiento
        btnEjecutarComparativa.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        lblEstado.setText("Ejecutando comparativa de ordenamientos...");

        // Ejecutar en hilo separado para no bloquear la UI
        new Thread(() -> {
            try {
                resultados.clear();
                Comparator<Libro> comparador = obtenerComparador(criterioSeleccionado);

                // Ejecutar los 5 métodos de ordenamiento
                ejecutarMetodo("Bubble Sort", librosOriginal, comparador, Ordenamientos::bubbleSort);
                ejecutarMetodo("Selection Sort", librosOriginal, comparador, Ordenamientos::selectionSort);
                ejecutarMetodo("Insertion Sort", librosOriginal, comparador, Ordenamientos::insertionSort);
                ejecutarMetodo("Shell Sort", librosOriginal, comparador, Ordenamientos::shellSort);
                ejecutarMetodo("Quick Sort", librosOriginal, comparador, Ordenamientos::quickSort);

                // Actualizar UI en el hilo de eventos
                SwingUtilities.invokeLater(() -> {
                    actualizarResultados();
                    mostrarLibrosOrdenados();
                    btnEjecutarComparativa.setEnabled(true);
                    progressBar.setVisible(false);
                    lblEstado.setText("Comparativa completada - " + librosOriginal.size() + " libros procesados");
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error durante la comparativa: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    btnEjecutarComparativa.setEnabled(true);
                    progressBar.setVisible(false);
                    lblEstado.setText("Error en la comparativa");
                });
            }
        }).start();
    }

    private Comparator<Libro> obtenerComparador(String criterio) {
        switch (criterio) {
            case "Título":
                return Comparator.comparing(Libro::getTitulo, String.CASE_INSENSITIVE_ORDER);
            case "Autor":
                return Comparator.comparing(Libro::getAutor, String.CASE_INSENSITIVE_ORDER);
            case "ISBN":
                return Comparator.comparing(Libro::getIsbn);
            case "Año":
                return Comparator.comparingInt(Libro::getFechaInt);
            case "Género":
                return Comparator.comparing(Libro::getGenero, String.CASE_INSENSITIVE_ORDER);
            default:
                return Comparator.comparing(Libro::getTitulo, String.CASE_INSENSITIVE_ORDER);
        }
    }

    private void ejecutarMetodo(String nombre, List<Libro> librosOriginal, Comparator<Libro> comparador, OrdenamientoMetodo metodo) {
        // Crear copia para no afectar la lista original
        List<Libro> copia = Ordenamientos.copiarLista(librosOriginal);

        long tiempo = metodo.ordenar(copia, comparador);
        boolean exito = Ordenamientos.estaOrdenada(copia, comparador);

        resultados.add(new ResultadoOrdenamiento(nombre, tiempo, exito, copia));
    }

    private void actualizarResultados() {
        DefaultTableModel model = (DefaultTableModel) tablaResultados.getModel();
        model.setRowCount(0);

        for (ResultadoOrdenamiento resultado : resultados) {
            String estado = resultado.exito ? "Completado" : "Falló";
            long ms = resultado.tiempoNs / 1_000_000; // Convertir a milisegundos

            model.addRow(new Object[]{
                    resultado.nombre,
                    String.format("%,d ns", resultado.tiempoNs),
                    String.format("%,d ms", ms),
                    estado
            });
        }
    }

    private void mostrarLibrosOrdenados() {
        // Encontrar el método más rápido que tuvo éxito
        ResultadoOrdenamiento masRapido = resultados.stream()
                .filter(r -> r.exito)
                .min((r1, r2) -> Long.compare(r1.tiempoNs, r2.tiempoNs))
                .orElse(null);

        if (masRapido == null) {
            JOptionPane.showMessageDialog(this, "Ningún método logró ordenar la lista correctamente", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Mostrar libros ordenados por el método más rápido
        DefaultTableModel model = (DefaultTableModel) tablaLibrosOrdenados.getModel();
        model.setRowCount(0);

        for (Libro libro : masRapido.librosOrdenados) {
            model.addRow(new Object[]{
                    libro.getTitulo(),
                    libro.getAutor(),
                    libro.getIsbn(),
                    libro.getGenero(),
                    libro.getFecha(),
                    libro.getEstado()
            });
        }

        lblEstado.setText(lblEstado.getText() + " - Más rápido: " + masRapido.nombre);
    }

    private void limpiarResultados() {
        resultados.clear();

        DefaultTableModel modelResultados = (DefaultTableModel) tablaResultados.getModel();
        modelResultados.setRowCount(0);

        DefaultTableModel modelLibros = (DefaultTableModel) tablaLibrosOrdenados.getModel();
        modelLibros.setRowCount(0);

        lblEstado.setText("Resultados limpiados - Seleccione un criterio y ejecute la comparativa");
    }

    // Interfaz funcional para los métodos de ordenamiento
    @FunctionalInterface
    private interface OrdenamientoMetodo {
        long ordenar(List<Libro> libros, Comparator<Libro> comparador);
    }

    // Clase para almacenar resultados
    private static class ResultadoOrdenamiento {
        String nombre;
        long tiempoNs;
        boolean exito;
        List<Libro> librosOrdenados;

        ResultadoOrdenamiento(String nombre, long tiempoNs, boolean exito, List<Libro> librosOrdenados) {
            this.nombre = nombre;
            this.tiempoNs = tiempoNs;
            this.exito = exito;
            this.librosOrdenados = librosOrdenados;
        }
    }
}