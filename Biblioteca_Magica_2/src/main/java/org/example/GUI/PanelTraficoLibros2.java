package org.example.GUI;

import org.example.Modelos.Biblioteca;
import org.example.Modelos.Cola;
import org.example.Modelos.Libro;
import org.example.Modelos.CoordinadorEnvios;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanelTraficoLibros2 extends JPanel {
    private JTable tablaTrafico;
    private ModeloTablaTrafico modeloTabla;
    private CoordinadorEnvios coordinador;
    private Map<String, FilaTrafico> filasActivas;
    private Timer timerActualizacion;

    public PanelTraficoLibros2(CoordinadorEnvios coordinador) {
        this.coordinador = coordinador;
        this.filasActivas = new HashMap<>();
        inicializarComponentes();
        configurarActualizaciones();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Crear modelo de tabla
        modeloTabla = new ModeloTablaTrafico();
        tablaTrafico = new JTable(modeloTabla);

        // Configurar tabla
        tablaTrafico.setRowHeight(25);
        tablaTrafico.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tablaTrafico.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tablaTrafico.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Agregar scroll
        JScrollPane scrollPane = new JScrollPane(tablaTrafico);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.CENTER);

        // Etiqueta de título
        JLabel lblTitulo = new JLabel("Monitoreo de Tráfico de Libros - Tiempo Real");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitulo, BorderLayout.NORTH);
    }

    private void configurarActualizaciones() {
        // Timer para actualizar cada segundo
        timerActualizacion = new Timer(1000, e -> actualizarDatos());
        timerActualizacion.start();
    }

    private void actualizarDatos() {
        // Obtener libros en tránsito del coordinador
        List<Libro> librosEnTransito = coordinador.getLibrosEnTransito();

        // Procesar cada libro en tránsito
        for (Libro libro : librosEnTransito) {
            String claveLibro = generarClaveLibro(libro);

            if (!filasActivas.containsKey(claveLibro)) {
                // Nueva fila para libro en tránsito
                FilaTrafico nuevaFila = new FilaTrafico(libro);
                filasActivas.put(claveLibro, nuevaFila);
            } else {
                // Actualizar fila existente si no está congelada
                FilaTrafico filaExistente = filasActivas.get(claveLibro);
                if (!"Disponible".equals(filaExistente.estado)) {
                    filaExistente.actualizarDesdeLibro(libro);

                    // Si ahora está disponible, congelar la fila
                    if ("Disponible".equals(filaExistente.estado)) {
                        // La fila se mantiene pero no se actualizará más
                        System.out.println("Libro '" + libro.getTitulo() + "' llegó a destino. Congelando fila.");
                    }
                }
            }
        }

        // Actualizar el modelo de tabla
        modeloTabla.actualizarDatos(new ArrayList<>(filasActivas.values()));
    }

    private String generarClaveLibro(Libro libro) {
        // Clave única por envío específico (ISBN + origen + destino)
        // Esto asegura que cada envío del mismo libro tenga su propia fila
        return libro.getIsbn() + "_" + libro.getIdBibliotecaOrigen() + "_" + libro.getIdBibliotecaDestino() + "_" + System.identityHashCode(libro);
    }

    // Clase interna para representar una fila de tráfico
    private class FilaTrafico {
        String libro;
        String ubicacionActual;
        String destinoFinal;
        String estado;
        int tiempoEstimado;
        Libro libroReferencia;

        public FilaTrafico(Libro libro) {
            this.libroReferencia = libro;
            actualizarDesdeLibro(libro);
        }

        public void actualizarDesdeLibro(Libro libro) {
            this.libro = libro.getTitulo() + " (" + libro.getIsbn() + ")";
            this.destinoFinal = libro.getIdBibliotecaDestino();
            this.estado = calcularEstado(libro);
            this.ubicacionActual = calcularUbicacionActual(libro);
            this.tiempoEstimado = calcularTiempoEstimado(libro);
        }

        private String calcularEstado(Libro libro) {
            String estadoOriginal = libro.getEstado();

            // Simplificar estados según los requerimientos
            if ("Recibido En Prestamo".equals(estadoOriginal) ||
                    "Disponible".equals(estadoOriginal)) {
                return "Disponible";
            } else {
                return "En Transito";
            }
        }

        private String calcularUbicacionActual(Libro libro) {
            // Obtener biblioteca actual basada en la ruta
            List<String> ruta = libro.getRuta();
            int indiceActual = libro.getIndiceRutaActual();

            if (ruta == null || ruta.isEmpty() || indiceActual >= ruta.size()) {
                return "Desconocida";
            }

            String bibliotecaId = ruta.get(indiceActual);
            String tipoCola = determinarTipoCola(libro, bibliotecaId);

            return bibliotecaId + " (" + tipoCola + ")";
        }

        private String determinarTipoCola(Libro libro, String bibliotecaId) {
            try {
                // Obtener la biblioteca actual
                Biblioteca biblioteca = getBibliotecaPorId(bibliotecaId);
                if (biblioteca == null) {
                    return "biblioteca no encontrada";
                }

                // Verificar en qué cola está el libro
                if (estaEnCola(biblioteca.getColaIngreso(), libro)) {
                    return "cola de entrada";
                } else if (estaEnCola(biblioteca.getColaTraspaso(), libro)) {
                    return "cola de procesamiento";
                } else if (estaEnCola(biblioteca.getColaSalida(), libro)) {
                    return "cola de salida";
                } else {
                    // Si no está en ninguna cola, podría estar siendo procesado
                    return "en procesamiento";
                }
            } catch (Exception e) {
                return "error ubicación";
            }
        }

        private boolean estaEnCola(Cola<Libro> cola, Libro libro) {
            if (cola == null) return false;

            // Usar el método obtenerElementos() de la cola
            List<Libro> elementos = cola.obtenerElementos();
            for (Libro libroEnCola : elementos) {
                if (libroEnCola == libro ||
                        (libroEnCola.getIsbn().equals(libro.getIsbn()) &&
                                libroEnCola.getTitulo().equals(libro.getTitulo()))) {
                    return true;
                }
            }
            return false;
        }

        private Biblioteca getBibliotecaPorId(String id) {
            // Necesitamos acceso al grafo para obtener la biblioteca
            // Esto requiere que CoordinadorEnvios exponga el grafo
            try {
                // Usar reflexión o método público para acceder al grafo
                java.lang.reflect.Method method = coordinador.getClass().getMethod("getGrafo");
                Object grafo = method.invoke(coordinador);

                java.lang.reflect.Method getBiblioMethod = grafo.getClass().getMethod("getBiblioteca", String.class);
                return (Biblioteca) getBiblioMethod.invoke(grafo, id);
            } catch (Exception e) {
                System.err.println("Error obteniendo biblioteca " + id + ": " + e.getMessage());
                return null;
            }
        }

        private int calcularTiempoEstimado(Libro libro) {
            // Si el libro ya está disponible, tiempo estimado es 0
            if ("Disponible".equals(estado)) {
                return 0;
            }

            // Cálculo basado en la ruta restante
            List<String> ruta = libro.getRuta();
            int indiceActual = libro.getIndiceRutaActual();

            if (ruta == null || indiceActual < 0 || indiceActual >= ruta.size() - 1) {
                return 0;
            }

            int bibliotecasRestantes = ruta.size() - indiceActual - 1;

            // Estimación de tiempo por biblioteca (en minutos)
            // Considerando tiempos de procesamiento en cada cola
            int tiempoPorBiblioteca = 3; // 3 minutos por biblioteca como base

            return Math.max(0, bibliotecasRestantes * tiempoPorBiblioteca);
        }
    }

    // Modelo de tabla personalizado
    private class ModeloTablaTrafico extends AbstractTableModel {
        private final String[] columnNames = {"Libro", "Ubicación Actual", "Destino Final", "Estado", "Tiempo Estimado (min)"};
        private List<FilaTrafico> datos;

        public ModeloTablaTrafico() {
            this.datos = new ArrayList<>();
        }

        public void actualizarDatos(List<FilaTrafico> nuevosDatos) {
            this.datos = nuevosDatos;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return datos.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= datos.size()) return "";

            FilaTrafico fila = datos.get(rowIndex);
            switch (columnIndex) {
                case 0: return fila.libro;
                case 1: return fila.ubicacionActual;
                case 2: return fila.destinoFinal;
                case 3: return fila.estado;
                case 4: return fila.tiempoEstimado;
                default: return "";
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 4) return Integer.class;
            return String.class;
        }
    }

    // Método para limpiar el panel cuando sea necesario
    public void limpiar() {
        filasActivas.clear();
        modeloTabla.actualizarDatos(new ArrayList<>());
    }

    // Detener el timer cuando se cierre la aplicación
    public void detenerActualizaciones() {
        if (timerActualizacion != null) {
            timerActualizacion.stop();
        }
    }
}
