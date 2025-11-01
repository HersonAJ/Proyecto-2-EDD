package org.example.GUI.Vistas;

import org.example.AVL.ArbolAVL;
import org.example.Modelos.ListaLibros;
import org.example.Modelos.Libro;
import org.example.Modelos.Biblioteca;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ListadoAlfabetico extends JPanel {
    private ArbolAVL arbolTitulos;
    private JTable tablaLibros;
    private JScrollPane scrollPane;
    private Biblioteca biblioteca;

    public ListadoAlfabetico(ArbolAVL arbolTitulos) {
        this.arbolTitulos = arbolTitulos;
        setupUI();
    }

    // Nuevo constructor con biblioteca
    public ListadoAlfabetico(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        this.arbolTitulos = biblioteca.getArbolTitulos();
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Título
        JLabel titulo = new JLabel("LISTADO DE LIBROS EN ORDEN ALFABÉTICO");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titulo, BorderLayout.NORTH);

        // Tabla
        configurarTabla();
        add(scrollPane, BorderLayout.CENTER);
    }

    private void configurarTabla() {
        // Modelo de tabla
        String[] headers = {"No.", "Título", "Autor", "ISBN", "Género", "Fecha", "Estado", "Ejemplares", "Acciones"};
        DefaultTableModel model = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Solo la columna de acciones es editable (para el botón)
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return column == 8 ? JButton.class : String.class;
            }
        };

        tablaLibros = new JTable(model);

        // Configurar el renderizador y editor para el botón de eliminar
        tablaLibros.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
        tablaLibros.getColumnModel().getColumn(8).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Configurar propiedades de la tabla
        tablaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaLibros.setRowSelectionAllowed(true);
        tablaLibros.setAutoCreateRowSorter(true);
        tablaLibros.setShowGrid(true);
        tablaLibros.setGridColor(new Color(0xd0, 0xd0, 0xd0));

        // Colores alternados para filas
        tablaLibros.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(0xf8, 0xf8, 0xf8));
                    }
                }
                return c;
            }
        });

        // Configurar header
        JTableHeader header = tablaLibros.getTableHeader();
        header.setBackground(new Color(0x4C, 0xAF, 0x50));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setReorderingAllowed(false);

        // Ajustar columnas
        tablaLibros.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaLibros.getColumnModel().getColumn(8).setPreferredWidth(100); // Acciones
        tablaLibros.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        scrollPane = new JScrollPane(tablaLibros);
        scrollPane.setPreferredSize(new Dimension(900, 600));
    }

    public void cargarDatosEnTabla() {
        DefaultTableModel model = (DefaultTableModel) tablaLibros.getModel();
        model.setRowCount(0);

        if (arbolTitulos == null || arbolTitulos.estaVacio()) {
            model.addRow(new Object[]{"No hay libros en el catálogo", "", "", "", "", "", "", "", ""});
            return;
        }

        // Obtener libros ordenados alfabéticamente
        ListaLibros librosOrdenados = arbolTitulos.obtenerLibrosEnOrdenAlfabetico();

        // Llenar la tabla usando el iterador
        int fila = 0;
        ListaLibros.Iterador iterador = librosOrdenados.obtenerIterador();

        while (iterador.tieneSiguiente()) {
            Libro libro = iterador.siguiente();

            // Crear botón de eliminar para cada fila
            JButton btnEliminar = new JButton("Eliminar");
            btnEliminar.setBackground(new Color(0xff, 0x44, 0x44));
            btnEliminar.setForeground(Color.WHITE);
            btnEliminar.setFocusPainted(false);

            // Agregar action listener al botón
            final String isbn = libro.getIsbn();
            final String tituloLibro = libro.getTitulo();
            final String estado = libro.getEstado();

            btnEliminar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    eliminarLibro(isbn, tituloLibro, estado);
                }
            });

            model.addRow(new Object[]{
                    fila + 1,
                    libro.getTitulo(),
                    libro.getAutor(),
                    libro.getIsbn(),
                    libro.getGenero(),
                    libro.getFecha(),
                    libro.getEstado(),
                    libro.getCantidad(),
                    btnEliminar
            });
            fila++;
        }

        model.fireTableDataChanged();
    }

    private void eliminarLibro(String isbn, String titulo, String estado) {
        // Validar que el libro esté disponible
        if (!"Disponible".equalsIgnoreCase(estado)) {
            JOptionPane.showMessageDialog(this,
                    "No se puede eliminar el libro: '" + titulo + "'\n" +
                            "Estado actual: " + estado + "\n" +
                            "Solo se pueden eliminar libros con estado 'Disponible'.",
                    "No se puede eliminar",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmar eliminación
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el libro:\n" +
                        "'" + titulo + "'\n" +
                        "ISBN: " + isbn + "\n\n" +
                        "Esta acción no se puede deshacer.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            // Ejecutar eliminación
            boolean eliminado = biblioteca.eliminarLibroPorISBN(isbn);

            if (eliminado) {
                JOptionPane.showMessageDialog(this,
                        "Libro eliminado exitosamente:\n" +
                                "'" + titulo + "'",
                        "Eliminación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);

                // Recargar la tabla para reflejar los cambios
                cargarDatosEnTabla();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar el libro.\n" +
                                "El libro puede haber sido eliminado por otro usuario o no existe.",
                        "Error de Eliminación",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Clases para renderizar y editar el botón en la tabla
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof JButton) {
                return (JButton) value;
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            if (value instanceof JButton) {
                button = (JButton) value;
            } else {
                button.setText((value == null) ? "" : value.toString());
            }
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                // El ActionListener del botón ya maneja la acción
            }
            isPushed = false;
            return button;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}