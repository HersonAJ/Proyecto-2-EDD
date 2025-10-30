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
    private Map<String, String> ubicacionesDetalladas = new HashMap<>();

    public PanelTraficoLibros(CoordinadorEnvios coordinador) {
        this.coordinador = coordinador;
        initComponents();

        this.coordinador.agregarListener(mensaje ->
                SwingUtilities.invokeLater(() -> procesarMensaje(mensaje))
        );
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        tableModel = new TraficoTableModel();
        tablaTrafico = new JTable(tableModel);

        tablaTrafico.setFillsViewportHeight(true);
        tablaTrafico.setRowHeight(28);
        tablaTrafico.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tablaTrafico.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        tablaTrafico.getColumnModel().getColumn(0).setPreferredWidth(220);
        tablaTrafico.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaTrafico.getColumnModel().getColumn(2).setPreferredWidth(160);
        tablaTrafico.getColumnModel().getColumn(3).setPreferredWidth(120);
        tablaTrafico.getColumnModel().getColumn(4).setPreferredWidth(120);

        // Renderer con colores
        tablaTrafico.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground(Color.WHITE);

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
        if (mensaje.contains("pasó a Traspaso") || mensaje.contains("pasó a Salida") ||
                mensaje.contains("salió de") || mensaje.contains("llegó a destino") ||
                mensaje.contains("recibido en PRÉSTAMO") || mensaje.contains("ENVÍO INICIADO") ||
                mensaje.contains("Envío iniciado") || mensaje.contains("PRÉSTAMO INICIADO")) {

            String ubicacionDetallada = extraerUbicacionDetallada(mensaje);
            String tituloLibro = extraerTituloLibro(mensaje);
            String isbn = extraerISBN(mensaje);

            if (tituloLibro != null && isbn != null) {
                String clave = generarClaveEnvio(isbn, mensaje);

                if (ubicacionDetallada == null && mensaje.contains("ENVÍO INICIADO")) {
                    if (mensaje.contains("de ") && mensaje.contains(" a ")) {
                        String parteOrigen = mensaje.substring(mensaje.indexOf("de ") + 3);
                        String biblioteca = parteOrigen.split(" ")[0];
                        ubicacionDetallada = biblioteca + " - Cola Ingreso";
                    }
                }

                if (ubicacionDetallada != null) {
                    ubicacionesDetalladas.put(clave, ubicacionDetallada);
                }
                tableModel.actualizarDatosCompletos();
            }
        }
    }

    private String generarClaveEnvio(String isbn, String mensaje) {
        String destino = "N/A";
        if (mensaje.contains(" a ")) {
            destino = mensaje.substring(mensaje.indexOf(" a ") + 3).split(" ")[0];
        }
        return isbn + "_" + destino;
    }

    private String extraerISBN(String mensaje) {
        if (mensaje.contains("(") && mensaje.contains(")")) {
            try {
                return mensaje.substring(mensaje.indexOf("(") + 1, mensaje.indexOf(")"));
            } catch (Exception ignored) {}
        }
        return null;
    }

    private String extraerUbicacionDetallada(String mensaje) {
        if (mensaje.contains("ENVÍO INICIADO")) {
            String biblioteca = mensaje.substring(mensaje.lastIndexOf("en ") + 3);
            return biblioteca.split(" - ")[0] + " - Cola Ingreso";
        }
        if (mensaje.contains("pasó a Traspaso")) {
            String biblioteca = mensaje.substring(mensaje.lastIndexOf("en ") + 3);
            return biblioteca + " - Cola Ingreso";
        } else if (mensaje.contains("pasó a Salida")) {
            String biblioteca = mensaje.substring(mensaje.lastIndexOf("en ") + 3);
            return biblioteca + " - Cola Traspaso";
        } else if (mensaje.contains("salió de")) {
            String biblioteca = mensaje.substring(mensaje.lastIndexOf("de ") + 3);
            return biblioteca + " - Cola Salida";
        } else if (mensaje.contains("llegó a destino")) {
            String biblioteca = mensaje.substring(mensaje.lastIndexOf("en ") + 3);
            return biblioteca + " - En tránsito";
        } else if (mensaje.contains("recibido en PRÉSTAMO")) {
            String biblioteca = mensaje.substring(mensaje.lastIndexOf("en ") + 3);
            return biblioteca + " - En tránsito";
        }
        return null;
    }

    private String extraerTituloLibro(String mensaje) {
        if (mensaje.contains(":")) {
            String sinPrefijo = mensaje.substring(mensaje.indexOf(":") + 2);
            if (sinPrefijo.contains(" en ")) {
                return sinPrefijo.substring(0, sinPrefijo.indexOf(" en ")).trim();
            }
        }
        if (mensaje.contains("'")) {
            int start = mensaje.indexOf("'") + 1;
            int end = mensaje.indexOf("'", start);
            if (end > start) return mensaje.substring(start, end);
        }
        return null;
    }

    private void actualizarDatos() {
        tableModel.actualizarDatosCompletos();
    }

    private void filtrarEnTransito() {
        TableRowSorter<TraficoTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setRowFilter(RowFilter.regexFilter("En tránsito|En transito", 3));
        tablaTrafico.setRowSorter(sorter);
    }

    private void filtrarTodos() {
        tablaTrafico.setRowSorter(null);
    }

    // -------------------- MODELO TABLA --------------------
    private class TraficoTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Libro", "Ubicación Actual", "Destino Final", "Estado", "Tiempo Estimado"};
        private final java.util.List<Object[]> data = new ArrayList<>();

        @Override
        public int getRowCount() { return data.size(); }
        @Override
        public int getColumnCount() { return columnNames.length; }
        @Override
        public String getColumnName(int column) { return columnNames[column]; }
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) { return data.get(rowIndex)[columnIndex]; }

        public void actualizarDatosCompletos() {
            data.clear();
            java.util.List<Libro> librosEnTransito = coordinador.getLibrosEnTransito();

            for (Libro libro : librosEnTransito) {
                String clave = libro.getIsbn() + "_" + libro.getIdBibliotecaDestino();
                Object[] row = new Object[5];
                row[0] = libro.getTitulo() + " (" + libro.getIsbn() + ")";
                row[1] = ubicacionesDetalladas.getOrDefault(clave, obtenerUbicacionDetallada(libro));
                row[2] = libro.getIdBibliotecaDestino();
                row[3] = traducirEstadoParaTrafico(libro.getEstado());
                row[4] = obtenerTiempoEstimado(libro);
                data.add(row);
            }
            fireTableDataChanged();
        }

        private String obtenerUbicacionDetallada(Libro libro) {
            if (libro.getRuta() == null || libro.getRuta().isEmpty())
                return libro.getIdBibliotecaOrigen() + " - Origen";
            int indice = libro.getIndiceRutaActual();
            if (indice < libro.getRuta().size())
                return libro.getRuta().get(indice) + " - En tránsito";
            return libro.getIdBibliotecaDestino() + " - ✅ Destino Final";
        }

        private String obtenerTiempoEstimado(Libro libro) {
            if (libro.getRuta() == null) return "N/A";
            int saltosRestantes = libro.getRuta().size() - libro.getIndiceRutaActual() - 1;
            return saltosRestantes <= 0 ? "Llegando..." : saltosRestantes + " saltos restantes";
        }
    }

    private String traducirEstadoParaTrafico(String estadoOriginal) {
        if (estadoOriginal == null) return "Desconocido";
        if (estadoOriginal.equalsIgnoreCase("Recibido En Prestamo"))
            return "Disponible";  // se muestra como disponible en tabla de tráfico
        if (estadoOriginal.equalsIgnoreCase("Disponible"))
            return "Disponible";
        if (estadoOriginal.toLowerCase().contains("transito"))
            return "En tránsito";
        return "Disponible";
    }
}