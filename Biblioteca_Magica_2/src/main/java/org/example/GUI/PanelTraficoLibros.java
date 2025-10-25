package org.example.GUI;

import org.example.Modelos.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

public class PanelTraficoLibros extends JPanel {
    private CoordinadorEnvios coordinador;
    private JTable tablaTrafico;
    private TraficoTableModel tableModel;
    private Map<String, String> ubicacionesDetalladas = new HashMap<>(); // Mapa: libroId -> ubicación detallada

    public PanelTraficoLibros(CoordinadorEnvios coordinador) {
        this.coordinador = coordinador;
        initComponents();

        // Registrar como listener para actualizaciones en tiempo real
        this.coordinador.agregarListener(mensaje ->
                SwingUtilities.invokeLater(() -> procesarMensaje(mensaje))
        );
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // Modelo de tabla
        tableModel = new TraficoTableModel();
        tablaTrafico = new JTable(tableModel);

        // Configuración de tabla
        tablaTrafico.setFillsViewportHeight(true);
        tablaTrafico.setRowHeight(28);
        tablaTrafico.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tablaTrafico.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        // Ajustes de columnas
        tablaTrafico.getColumnModel().getColumn(0).setPreferredWidth(220); // Libro
        tablaTrafico.getColumnModel().getColumn(1).setPreferredWidth(200); // Ubicación (más ancha)
        tablaTrafico.getColumnModel().getColumn(2).setPreferredWidth(160); // Destino
        tablaTrafico.getColumnModel().getColumn(3).setPreferredWidth(120); // Estado
        tablaTrafico.getColumnModel().getColumn(4).setPreferredWidth(120); // Tiempo

        // Renderer para colorear filas según el estado
        tablaTrafico.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Resetea fondo
                if (!isSelected) c.setBackground(Color.WHITE);

                // Solo colorear la celda de la columna Estado
                int colEstado = table.getColumnModel().getColumnIndex("Estado");
                if (column == colEstado && value != null) {
                    String estado = value.toString().toLowerCase();
                    if (estado.contains("tránsito") || estado.contains("transito"))
                        c.setBackground(new Color(247, 255, 5));
                    else if (estado.contains("disponible"))
                        c.setBackground(new Color(35, 223, 35));
                    else
                        c.setBackground(Color.WHITE);
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaTrafico);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📦 Estado actual de los envíos de libros"));

        // Panel inferior con botones
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelControles.setBackground(Color.WHITE);

        JButton btnActualizar = new JButton("🔄 Actualizar");
        JButton btnFiltrarEnTransito = new JButton("🚚 En tránsito");
        JButton btnFiltrarTodos = new JButton("📚 Todos");

        btnActualizar.addActionListener(e -> actualizarDatos());
        btnFiltrarEnTransito.addActionListener(e -> filtrarEnTransito());
        btnFiltrarTodos.addActionListener(e -> filtrarTodos());

        panelControles.add(btnActualizar);
        panelControles.add(btnFiltrarEnTransito);
        panelControles.add(btnFiltrarTodos);

        add(scrollPane, BorderLayout.CENTER);
        add(panelControles, BorderLayout.SOUTH);
    }

    private void procesarMensaje(String mensaje) {
        // Solo procesar mensajes que indican movimiento de libros
        if (mensaje.contains("📥") || mensaje.contains("🚚") || mensaje.contains("🚀") ||
                mensaje.contains("➡️") || mensaje.contains("📚") ||
                mensaje.contains("ENVÍO INICIADO") || mensaje.contains("📦 Envío iniciado")) {

            // Extraer información del mensaje
            String ubicacionDetallada = extraerUbicacionDetallada(mensaje);
            String tituloLibro = extraerTituloLibro(mensaje);

            if (tituloLibro != null) {
                if (ubicacionDetallada == null && (mensaje.contains("ENVÍO INICIADO") || mensaje.contains("📦 Envío iniciado"))) {
                    // Buscar la biblioteca origen en el mensaje
                    if (mensaje.contains("de ") && mensaje.contains(" a ")) {
                        String parteOrigen = mensaje.substring(mensaje.indexOf("de ") + 3);
                        String biblioteca = parteOrigen.split(" ")[0]; // Tomar primera palabra después de "de "
                        ubicacionDetallada = biblioteca + " - Cola Ingreso";
                    }
                }

                if (ubicacionDetallada != null) {
                    ubicacionesDetalladas.put(tituloLibro, ubicacionDetallada);
                }
                tableModel.actualizarDatosCompletos();
            }
        }
    }

    private String extraerUbicacionDetallada(String mensaje) {
        if (mensaje.contains("ENVÍO INICIADO")) {
            String biblioteca = mensaje.substring(mensaje.lastIndexOf("en ") + 3);
            return biblioteca.split(" - ")[0] + " - Cola Ingreso";
        }
        if (mensaje.contains("📥") && mensaje.contains("Traspaso")) {
            String biblioteca = mensaje.substring(mensaje.lastIndexOf("en ") + 3);
            return biblioteca + " - Cola Ingreso";
        } else if (mensaje.contains("🚚") && mensaje.contains("Salida")) {
            String biblioteca = mensaje.substring(mensaje.lastIndexOf("en ") + 3);
            return biblioteca + " - Cola Traspaso";
        } else if (mensaje.contains("🚀") && mensaje.contains("salió")) {
            String biblioteca = mensaje.substring(mensaje.lastIndexOf("de ") + 3);
            return biblioteca + " - Cola Salida";
        } else if (mensaje.contains("➡️") && mensaje.contains("llegó")) {
            String biblioteca = mensaje.substring(mensaje.lastIndexOf("a ") + 2);
            return biblioteca + " - En tránsito";
        } else if (mensaje.contains("📚") && mensaje.contains("llegó a destino")) {
            String biblioteca = mensaje.substring(mensaje.lastIndexOf("en ") + 3);
            return biblioteca + " - ✅ Destino Final";
        } else if (mensaje.contains("📥") && mensaje.contains("Ingreso")) {
            // Por si hay mensajes de ingreso explícitos
            String biblioteca = mensaje.substring(mensaje.lastIndexOf("en ") + 3);
            return biblioteca + " - Cola Ingreso";
        }
        return null;
    }

    private String extraerTituloLibro(String mensaje) {

        if (mensaje.contains("🚀 ENVÍO INICIADO")) {
            String sinPrefijo = mensaje.substring(mensaje.indexOf(":") + 2); // +2 para el espacio después de ":"
            String titulo = sinPrefijo.substring(0, sinPrefijo.indexOf(" en "));
            return titulo.trim();
        }
        try {
            if (mensaje.contains("➡️") && mensaje.contains("'")) {
                int start = mensaje.indexOf("'") + 1;
                int end = mensaje.indexOf("'", start);
                return mensaje.substring(start, end);
            } else {
                String[] partes = mensaje.split(" ");
                for (int i = 0; i < partes.length; i++) {
                    if (partes[i].equals("pasó") || partes[i].equals("salió") ||
                            partes[i].equals("llegó") || partes[i].equals("'")) {
                        // Reconstruir el título desde el inicio hasta esta palabra
                        StringBuilder titulo = new StringBuilder();
                        for (int j = 1; j < i; j++) { // Empezar desde 1 para saltar el emoji
                            if (j > 1) titulo.append(" ");
                            titulo.append(partes[j]);
                        }
                        return titulo.toString().replace("'", "").trim();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error extrayendo título del mensaje: " + mensaje);
        }
        return null;
    }

    private void actualizarDatos() {
        tableModel.actualizarDatosCompletos();
    }

    private void filtrarEnTransito() {
        TableRowSorter<TraficoTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setRowFilter(RowFilter.regexFilter("En tránsito|En transito", 3)); // columna estado
        tablaTrafico.setRowSorter(sorter);
    }

    private void filtrarTodos() {
        tablaTrafico.setRowSorter(null);
    }

    // Modelo de tabla
    private class TraficoTableModel extends AbstractTableModel {
        private final String[] columnNames = {
                "📖 Libro", "📍 Ubicación Actual", "🎯 Destino Final",
                "Estado", "⏱️ Tiempo Estimado"
        };

        private java.util.List<Object[]> data = new ArrayList<>();
        private java.util.List<Libro> librosReferencia = new ArrayList<>(); // Para mapear filas a libros

        @Override
        public int getRowCount() {
            return data.size();
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
            if (rowIndex < data.size()) {
                return data.get(rowIndex)[columnIndex];
            }
            return null;
        }

        public void actualizarDatosCompletos() {
            data.clear();
            librosReferencia.clear();

            java.util.List<Libro> librosEnTransito = coordinador.getLibrosEnTransito();

            for (Libro libro : librosEnTransito) {
                Object[] row = new Object[5];
                row[0] = libro.getTitulo() + " (" + libro.getIsbn() + ")";
                row[1] = obtenerUbicacionDetallada(libro);
                row[2] = libro.getIdBibliotecaDestino();
                row[3] = libro.getEstado();
                row[4] = obtenerTiempoEstimado(libro);
                data.add(row);
                librosReferencia.add(libro);
            }

            fireTableDataChanged();
        }

        private String obtenerUbicacionDetallada(Libro libro) {
            // Primero intentar obtener la ubicación del mapa de ubicaciones detalladas
            String ubicacionGuardada = ubicacionesDetalladas.get(libro.getTitulo());
            if (ubicacionGuardada != null) {
                return ubicacionGuardada;
            }

            // Fallback: usar la ubicación básica si no hay información detallada
            if (libro.getRuta() == null || libro.getRuta().isEmpty()) {
                return libro.getIdBibliotecaOrigen() + " - Origen";
            }
            int indice = libro.getIndiceRutaActual();
            if (indice < libro.getRuta().size()) {
                return libro.getRuta().get(indice) + " - En tránsito";
            }
            return libro.getIdBibliotecaDestino() + " - ✅ Destino Final";
        }

        private String obtenerTiempoEstimado(Libro libro) {
            if (libro.getRuta() == null) return "N/A";
            int saltosRestantes = libro.getRuta().size() - libro.getIndiceRutaActual() - 1;
            return saltosRestantes <= 0 ? "Llegando..." : saltosRestantes + " saltos restantes";
        }
    }
}