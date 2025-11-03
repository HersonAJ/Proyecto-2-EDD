package org.example.GUI.VistasGenerales;

import org.example.Modelos.Libro;
import org.example.Modelos.CoordinadorEnvios;
import org.example.Modelos.EnvioListener;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PanelTraficoLibros2 extends JPanel implements EnvioListener {
    private JTable tablaTrafico;
    private ModeloTablaTrafico modeloTabla;
    private CoordinadorEnvios coordinador;
    private Map<String, FilaTrafico> filasActivas;
    private Map<String, String> ultimaUbicacionConocida = new HashMap<>();
    private boolean reiniciando = false;

    public PanelTraficoLibros2(CoordinadorEnvios coordinador) {
        this.coordinador = coordinador;
        this.filasActivas = new LinkedHashMap<>();
        this.ultimaUbicacionConocida = new HashMap<>();
        this.coordinador.agregarListener(this);
        inicializarComponentes();
    }

    @Override
    public void onEvento(String mensaje) {
        SwingUtilities.invokeLater(() -> procesarEvento(mensaje));
    }

    private void procesarEvento(String mensaje) {
        // Procesar el evento y actualizar la tabla inmediatamente
        if (mensaje.contains("pasó a Traspaso en")) {
            procesarPasoATraspaso(mensaje);
        } else if (mensaje.contains("pasó a Salida en")) {
            procesarPasoASalida(mensaje);
        } else if (mensaje.contains("salió de")) {
            procesarSalida(mensaje);
        } else if (mensaje.contains("llegó a destino final en")) {
            procesarDestinoFinal(mensaje);
        } else if (mensaje.contains("llegó a ")) {
            procesarLlegada(mensaje);
        } else if (mensaje.contains("ENVÍO INICIADO") || mensaje.contains("PRÉSTAMO INICIADO")) {
            procesarInicioEnvio(mensaje);
        }

        actualizarTabla();
    }

    private void procesarInicioEnvio(String mensaje) {
        String titulo;
        if (mensaje.contains("ENVÍO INICIADO: ")) {
            titulo = mensaje.split("ENVÍO INICIADO: ")[1].split(" en ")[0].trim();
        } else if (mensaje.contains("PRÉSTAMO INICIADO: ")) {
            titulo = mensaje.split("PRÉSTAMO INICIADO: ")[1].split(" de ")[0].trim();
        } else {
            return;
        }

        String biblioteca = extraerBibliotecaDeMensaje(mensaje);
        if (biblioteca != null && !biblioteca.equals("Desconocida")) {
            actualizarUbicacionPorTitulo(titulo, biblioteca + " (cola de entrada)");
        }
    }

    private void procesarPasoATraspaso(String mensaje) {
        String titulo = mensaje.split(" pasó a Traspaso en ")[0].trim();
        String biblioteca = extraerBibliotecaDeMensaje(mensaje);
        actualizarUbicacionPorTitulo(titulo, biblioteca + " (cola de procesamiento)");
    }

    private void procesarPasoASalida(String mensaje) {
        String titulo = mensaje.split(" pasó a Salida en ")[0].trim();
        String biblioteca = extraerBibliotecaDeMensaje(mensaje);
        actualizarUbicacionPorTitulo(titulo, biblioteca + " (cola de salida)");
    }

    private void procesarSalida(String mensaje) {
        String titulo = mensaje.split(" salió de ")[0].trim();
        actualizarUbicacionPorTitulo(titulo, "En tránsito a siguiente biblioteca");
    }

    private void procesarLlegada(String mensaje) {
        String titulo;
        if (mensaje.contains("'")) {
            titulo = mensaje.split("'")[1].trim();
        } else {
            titulo = mensaje.split(" llegó a ")[0].trim();
        }
        String biblioteca = extraerBibliotecaDeMensaje(mensaje);
        actualizarUbicacionPorTitulo(titulo, biblioteca + " (cola de entrada)");
    }

    private void procesarDestinoFinal(String mensaje) {
        String titulo = mensaje.split(" llegó a destino final en ")[0].trim();
        String biblioteca = extraerBibliotecaDeMensaje(mensaje);
        actualizarUbicacionPorTitulo(titulo, biblioteca + " (destino final)");
    }

    private void actualizarUbicacionPorTitulo(String titulo, String ubicacion) {
        // Extraer la biblioteca del mensaje de ubicación
        String bibliotecaEvento = ubicacion.split(" ")[0];

        // Buscar TODOS los libros con este título y encontrar el que coincida
        Libro libroCoincidente = null;
        for (Libro libro : coordinador.getLibrosEnTransito()) {
            if (libro.getTitulo().equals(titulo)) {
                // Verificar si este libro está relacionado con la biblioteca del evento
                if (coincideConEvento(libro, bibliotecaEvento, ubicacion)) {
                    libroCoincidente = libro;
                    break;
                }
            }
        }

        if (libroCoincidente != null) {
            String clave = generarClaveLibro(libroCoincidente);
            ultimaUbicacionConocida.put(clave, ubicacion);
        }
    }

    private boolean coincideConEvento(Libro libro, String bibliotecaEvento, String ubicacionCompleta) {
        // Si es destino final, debe coincidir con el destino del libro
        if (ubicacionCompleta.contains("destino final")) {
            return libro.getIdBibliotecaDestino().equals(bibliotecaEvento);
        }
        // Si es en tránsito o en cola, puede coincidir con origen, destino o rutas intermedias
        else {
            return libro.getIdBibliotecaOrigen().equals(bibliotecaEvento) ||
                    libro.getIdBibliotecaDestino().equals(bibliotecaEvento) ||
                    (libro.getRuta() != null && libro.getRuta().contains(bibliotecaEvento));
        }
    }

    private String extraerBibliotecaDeMensaje(String mensaje) {
        // Buscar patrones de ID de biblioteca directamente
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[A-E]-\\d{3}");
        java.util.regex.Matcher matcher = pattern.matcher(mensaje);

        if (matcher.find()) {
            return matcher.group();
        }

        return "Desconocida";
    }

    private void actualizarTabla() {
        // Actualizar todas las filas con la información más reciente
        for (Libro libro : coordinador.getLibrosEnTransito()) {
            String clave = generarClaveLibro(libro);

            if (!filasActivas.containsKey(clave)) {
                // Nueva fila para libro en tránsito
                filasActivas.put(clave, new FilaTrafico(libro));
            } else {
                // Actualizar fila existente si no está congelada
                FilaTrafico filaExistente = filasActivas.get(clave);
                if (!"Disponible".equals(filaExistente.estado)) {
                    filaExistente.actualizarDesdeLibro(libro);
                }
            }

            // Aplicar ubicación conocida si existe
            if (ultimaUbicacionConocida.containsKey(clave)) {
                FilaTrafico fila = filasActivas.get(clave);
                if (fila != null) {
                    fila.ubicacionActual = ultimaUbicacionConocida.get(clave);
                }
            }
        }

        modeloTabla.actualizarDatos(new ArrayList<>(filasActivas.values()));
    }

    private String generarClaveLibro(Libro libro) {
        // Incluir más datos para asegurar unicidad
        return libro.getIsbn() + "_" +
                libro.getIdBibliotecaOrigen() + "_" +
                libro.getIdBibliotecaDestino() + "_" +
                libro.getPrioridad() + "_" +
                System.identityHashCode(libro);
    }

    private String calcularEstado(Libro libro) {
        String estadoOriginal = libro.getEstado();
        if ("Recibido En Prestamo".equals(estadoOriginal) || "Disponible".equals(estadoOriginal)) {
            return "Disponible";
        } else {
            return "En Transito";
        }
    }

    // Clase interna para representar una fila de tráfico
    private class FilaTrafico {
        String libro;
        String ubicacionActual;
        String destinoFinal;
        String estado;
        String saltosRestantes;

        public FilaTrafico(Libro libro) {
            actualizarDesdeLibro(libro);
        }

        public void actualizarDesdeLibro(Libro libro) {
            this.libro = libro.getTitulo() + " (" + libro.getIsbn() + ")";
            this.destinoFinal = libro.getIdBibliotecaDestino();
            this.estado = calcularEstado(libro);
            this.saltosRestantes = calcularSaltosRestantes(libro);

            if (this.ubicacionActual == null) {
                this.ubicacionActual = "Iniciando envío...";
            }
        }

        private String calcularSaltosRestantes(Libro libro) {
            if ("Disponible".equals(estado)) return "0";

            List<String> ruta = libro.getRuta();
            int indiceActual = libro.getIndiceRutaActual();

            if (ruta == null || indiceActual < 0 || indiceActual >= ruta.size() - 1) {
                return "0";
            }

            int saltos = ruta.size() - indiceActual - 1;
            return String.valueOf(Math.max(0, saltos));
        }
    }

    // Modelo de tabla personalizado
    private class ModeloTablaTrafico extends AbstractTableModel {
        private final String[] columnNames = {"Libro", "Ubicación Actual", "Destino Final", "Estado", "Saltos Restantes"};
        private List<FilaTrafico> datos = new ArrayList<>();

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
                case 4: return fila.saltosRestantes;
                default: return "";
            }
        }
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloTabla = new ModeloTablaTrafico();
        tablaTrafico = new JTable(modeloTabla);
        tablaTrafico.setRowHeight(25);
        tablaTrafico.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tablaTrafico.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tablaTrafico.getColumnModel().getColumn(3).setCellRenderer(new EstadoCellRenderer());

        JScrollPane scrollPane = new JScrollPane(tablaTrafico);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.CENTER);

        JLabel lblTitulo = new JLabel("Monitoreo de Tráfico de Libros - Tiempo Real");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitulo, BorderLayout.NORTH);
    }

    private class EstadoCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                String estado = value.toString();

                // Colores para cada estado
                if ("En Transito".equals(estado)) {
                    c.setBackground(Color.ORANGE);  // Naranja para en tránsito
                    c.setForeground(Color.BLACK);
                } else if ("Disponible".equals(estado)) {
                    c.setBackground(Color.GREEN);   // Verde para disponible
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(table.getBackground());
                    c.setForeground(table.getForeground());
                }
            }

            // Centrar el texto
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(getFont().deriveFont(Font.BOLD)); // Texto en negrita

            return c;
        }
    }
}