package org.example.GUI.Vistas;

import org.example.AVL.ArbolAVL;
import org.example.Modelos.ListaLibros;
import org.example.Modelos.Libro;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ListadoAlfabetico extends JPanel {
    private ArbolAVL arbolTitulos;
    private JTable tablaLibros;
    private JScrollPane scrollPane;

    public ListadoAlfabetico(ArbolAVL arbolTitulos) {
        this.arbolTitulos = arbolTitulos;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Título
        JLabel titulo = new JLabel("📚 LISTADO DE LIBROS EN ORDEN ALFABÉTICO");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titulo, BorderLayout.NORTH);

        // Tabla
        configurarTabla();
        add(scrollPane, BorderLayout.CENTER);
    }

    private void configurarTabla() {
        // Modelo de tabla
        String[] headers = {"No.", "Título", "Autor", "ISBN", "Género", "Fecha", "Estado", "Ejemplares"};
        DefaultTableModel model = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };

        tablaLibros = new JTable(model);

        // Configurar propiedades de la tabla
        tablaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaLibros.setRowSelectionAllowed(true);
        tablaLibros.setAutoCreateRowSorter(true); // Ordenamiento
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
        header.setBackground(new Color(0x4C, 0xAF, 0x50)); // Verde
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setReorderingAllowed(false);

        // Ajustar columnas
        tablaLibros.getColumnModel().getColumn(0).setPreferredWidth(50); // No. más estrecho
        tablaLibros.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        scrollPane = new JScrollPane(tablaLibros);
        scrollPane.setPreferredSize(new Dimension(800, 600));
    }

    public void cargarDatosEnTabla() {
        DefaultTableModel model = (DefaultTableModel) tablaLibros.getModel();
        model.setRowCount(0); // Limpiar tabla

        if (arbolTitulos == null || arbolTitulos.estaVacio()) {
            model.addRow(new Object[]{"No hay libros en el catálogo", "", "", "", "", "", ""});
            return;
        }

        // Obtener libros ordenados alfabéticamente
        ListaLibros librosOrdenados = arbolTitulos.obtenerLibrosEnOrdenAlfabetico();

        // Llenar la tabla usando el iterador
        int fila = 0;
        ListaLibros.Iterador iterador = librosOrdenados.obtenerIterador();

        while (iterador.tieneSiguiente()) {
            Libro libro = iterador.siguiente();

            // Crear fila para cada libro
            model.addRow(new Object[]{
                    fila + 1,
                    libro.getTitulo(),
                    libro.getAutor(),
                    libro.getIsbn(),
                    libro.getGenero(),
                    libro.getFecha(),
                    libro.getEstado(),
                    libro.getCantidad()
            });
            fila++;
        }

        // Ajustar el tamaño de las columnas al contenido
        for (int i = 0; i < tablaLibros.getColumnCount(); i++) {
            tablaLibros.getColumnModel().getColumn(i).setPreferredWidth(100);
        }
        tablaLibros.getColumnModel().getColumn(0).setPreferredWidth(50); // No. más estrecho

        // Notificar a la tabla que los datos cambiaron
        model.fireTableDataChanged();
    }
}