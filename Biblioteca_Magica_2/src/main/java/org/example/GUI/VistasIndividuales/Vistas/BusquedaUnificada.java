package org.example.GUI.VistasIndividuales.Vistas;

import org.example.Estructuras.AVL.ArbolAVL;
import org.example.Estructuras.B.ArbolB;
import org.example.Estructuras.BPlus.ArbolBPlus;
import org.example.Modelos.Libro;
import org.example.Modelos.ListaLibros;
import org.example.Estructuras.TablaHash.TablaHash;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.BiConsumer;

public class BusquedaUnificada extends JPanel {
    private ArbolAVL arbolTitulos;
    //private IndiceISBN indiceISBN;
    private TablaHash<String, Libro> tablaHash;
    private ArbolB arbolB;
    private ArbolBPlus arbolBPlus;
    private BiConsumer<String, String> appendLog;

    // Componentes de UI
    private JComboBox<String> comboTipoBusqueda;
    private JLabel labelCampo;
    private JTextField editBusquedaSimple;
    private JSpinner spinFechaDesde;
    private JSpinner spinFechaHasta;
    private JButton btnBuscar;
    private JTable tablaResultados;
    private JScrollPane scrollTabla;

    public BusquedaUnificada(ArbolAVL arbolTitulos, /*IndiceISBN indiceISBN,*/ TablaHash<String, Libro> tablaHash,
                             ArbolB arbolB, ArbolBPlus arbolBPlus,
                             BiConsumer<String, String> logCallBack) {
        this.arbolTitulos = arbolTitulos;
        //this.indiceISBN = indiceISBN;
        this.tablaHash = tablaHash;
        this.arbolB = arbolB;
        this.arbolBPlus = arbolBPlus;
        this.appendLog = logCallBack;

        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Panel de controles de búsqueda
        JPanel panelBusqueda = new JPanel();
        panelBusqueda.setLayout(new BoxLayout(panelBusqueda, BoxLayout.Y_AXIS));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Configurar Búsqueda"));

        // Tipo de búsqueda
        JPanel panelTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTipo.add(new JLabel("Tipo de búsqueda:"));

        comboTipoBusqueda = new JComboBox<>(new String[]{
                "Por Título", "Por ISBN", "Por Género", "Por Fecha"
        });
        comboTipoBusqueda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onTipoBusquedaCambiado(comboTipoBusqueda.getSelectedIndex());
            }
        });
        panelTipo.add(comboTipoBusqueda);
        panelBusqueda.add(panelTipo);

        // Campos dinámicos
        JPanel panelCampos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelCampo = new JLabel("Término:");
        panelCampos.add(labelCampo);

        editBusquedaSimple = new JTextField(20);
        editBusquedaSimple.setToolTipText("Ingrese término de búsqueda...");
        panelCampos.add(editBusquedaSimple);

        // Campos de fecha
        spinFechaDesde = new JSpinner(new SpinnerNumberModel(1900, 0, 3000, 1));
        spinFechaDesde.setVisible(false);
        JLabel lblDesde = new JLabel("Desde:");
        lblDesde.setVisible(false);
        panelCampos.add(lblDesde);
        panelCampos.add(spinFechaDesde);

        spinFechaHasta = new JSpinner(new SpinnerNumberModel(2024, 0, 3000, 1));
        spinFechaHasta.setVisible(false);
        JLabel lblHasta = new JLabel("Hasta:");
        lblHasta.setVisible(false);
        panelCampos.add(lblHasta);
        panelCampos.add(spinFechaHasta);

        panelBusqueda.add(panelCampos);

        // Botón buscar
        btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(btnBuscar.getFont().deriveFont(Font.BOLD));
        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBuscarClicked();
            }
        });
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBoton.add(btnBuscar);
        panelBusqueda.add(panelBoton);

        add(panelBusqueda, BorderLayout.NORTH);

        // Tabla de resultados
        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBorder(BorderFactory.createTitledBorder("Resultados"));

        configurarTabla();
        scrollTabla = new JScrollPane(tablaResultados);
        panelResultados.add(scrollTabla, BorderLayout.CENTER);

        add(panelResultados, BorderLayout.CENTER);

        // Mostrar campos iniciales
        mostrarCamposPorTipo(0);
    }

    private void configurarTabla() {
        String[] columnNames = {"No.", "Título", "Autor", "ISBN", "Género", "Fecha", "Ejemplares"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaResultados = new JTable(model);

        // Configurar propiedades de la tabla
        tablaResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaResultados.setRowSelectionAllowed(true);
        tablaResultados.setShowGrid(true);
        tablaResultados.setGridColor(Color.LIGHT_GRAY);
        tablaResultados.setFillsViewportHeight(true);

        // Ajustar columnas
        tablaResultados.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaResultados.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaResultados.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaResultados.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaResultados.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaResultados.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaResultados.getColumnModel().getColumn(6).setPreferredWidth(80);

        // Estilos
        tablaResultados.setSelectionBackground(new Color(220, 240, 255));
        tablaResultados.setSelectionForeground(Color.BLACK);

        // Header personalizado
        tablaResultados.getTableHeader().setBackground(new Color(33, 150, 243));
        tablaResultados.getTableHeader().setForeground(Color.WHITE);
        tablaResultados.getTableHeader().setFont(tablaResultados.getTableHeader().getFont().deriveFont(Font.BOLD));

        // Mostrar tabla vacía inicialmente
        limpiarTabla();
    }

    private void onTipoBusquedaCambiado(int index) {
        mostrarCamposPorTipo(index);
    }

    private void mostrarCamposPorTipo(int tipo) {
        // Ocultar todos primero
        editBusquedaSimple.setVisible(false);
        spinFechaDesde.setVisible(false);
        spinFechaHasta.setVisible(false);

        // Mostrar etiquetas de fecha
        Component[] components = ((JPanel)spinFechaDesde.getParent()).getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                comp.setVisible(false);
            }
        }

        // Mostrar los apropiados según el tipo
        switch(tipo) {
            case 0: // Título
                labelCampo.setText("Título:");
                editBusquedaSimple.setToolTipText("Ingrese título del libro...");
                editBusquedaSimple.setVisible(true);
                break;
            case 1: // ISBN
                labelCampo.setText("ISBN:");
                editBusquedaSimple.setToolTipText("Ingrese ISBN del libro...");
                editBusquedaSimple.setVisible(true);
                break;
            case 2: // Género
                labelCampo.setText("Género:");
                editBusquedaSimple.setToolTipText("Ingrese género literario...");
                editBusquedaSimple.setVisible(true);
                break;
            case 3: // Fecha
                labelCampo.setText("Rango de años:");
                spinFechaDesde.setVisible(true);
                spinFechaHasta.setVisible(true);
                // Mostrar etiquetas de fecha
                for (Component comp : components) {
                    if (comp instanceof JLabel) {
                        comp.setVisible(true);
                    }
                }
                break;
        }

        // Revalidar el layout
        revalidate();
        repaint();
    }

    private void onBuscarClicked() {
        int tipo = comboTipoBusqueda.getSelectedIndex();

        // Validaciones básicas
        switch(tipo) {
            case 0: case 1: case 2: // Título, ISBN, Género
                if (editBusquedaSimple.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Ingrese un término de búsqueda.",
                            "Campo vacío", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                break;
            case 3: // Fecha
                int desde = (Integer) spinFechaDesde.getValue();
                int hasta = (Integer) spinFechaHasta.getValue();
                if (desde > hasta) {
                    JOptionPane.showMessageDialog(this, "El año 'desde' no puede ser mayor al año 'hasta'.",
                            "Rango inválido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                break;
        }

        // Ejecutar búsqueda según tipo
        switch(tipo) {
            case 0: // Título
                buscarPorTitulo(editBusquedaSimple.getText().trim());
                break;
            case 1: // ISBN
                buscarPorISBN(editBusquedaSimple.getText().trim());
                break;
            case 2: // Género
                buscarPorGenero(editBusquedaSimple.getText().trim());
                break;
            case 3: // Fecha
                int desde = (Integer) spinFechaDesde.getValue();
                int hasta = (Integer) spinFechaHasta.getValue();
                buscarPorFecha(desde, hasta);
                break;
        }
    }
    private void buscarPorTitulo(String titulo) {
        if (arbolTitulos == null || arbolTitulos.estaVacio()) {
            appendLog.accept("El árbol de títulos está vacío. Cargue datos antes de buscar.", "error");
            JOptionPane.showMessageDialog(this, "El árbol de títulos está vacío.",
                    "Árbol vacío", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        appendLog.accept("\n--- Búsqueda por Título: '" + titulo + "' ---", "ok");

        // Primera coincidencia
        Libro primerEncontrado = arbolTitulos.buscarPorTitulo(titulo);

        // Lista completa de coincidencias
        ListaLibros resultados = arbolTitulos.buscarTodosPorTitulo(titulo);

        if (primerEncontrado != null && resultados.getTamaño() > 0) {
            appendLog.accept("Primer libro encontrado:\n " + primerEncontrado.toString(), "ok");

            // Otras coincidencias
            ListaLibros.Iterador iterador = resultados.obtenerIterador();
            int contador = 0;
            boolean hayOtras = false;

            while (iterador.tieneSiguiente()) {
                Libro libro = iterador.siguiente();
                if (libro != primerEncontrado) {
                    if (!hayOtras) {
                        appendLog.accept("Otras coincidencias:", "ok");
                        hayOtras = true;
                    }
                    contador++;
                    appendLog.accept(contador + ". " + libro.toString(), "ok");
                }
            }

            if (!hayOtras) {
                appendLog.accept("No hay otras coincidencias.", "info");
            }

        } else {
            appendLog.accept("No se encontró el libro con el título: " + titulo, "error");
        }

        llenarTablaDesdeListaLibros(resultados);  // Cambiar a este método
    }

    private void buscarPorISBN(String isbn) {
        //if (indiceISBN == null || indiceISBN.estaVacio()) {
            if (tablaHash == null || tablaHash.estaVacia()) {
            appendLog.accept("El índice ISBN está vacío. Cargue datos antes de buscar.", "error");
            JOptionPane.showMessageDialog(this, "El índice ISBN está vacío.",
                    "Índice vacío", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        appendLog.accept("\n--- Búsqueda por ISBN: '" + isbn + "' ---", "ok");

        //Libro resultado = indiceISBN.buscar(isbn);
        Libro resultado = tablaHash.obtener(isbn);

        if (resultado != null) {
            appendLog.accept("Libro encontrado:\n" + resultado.toString(), "ok");
        } else {
            appendLog.accept("No se encontró ningún libro con ISBN: " + isbn, "error");
        }

        llenarTablaDesdeLibroUnico(resultado);
    }

    private void buscarPorGenero(String genero) {
        if (arbolBPlus == null || arbolBPlus.getRaiz() == null) {
            appendLog.accept("El árbol B+ está vacío. Cargue datos antes de buscar.", "error");
            JOptionPane.showMessageDialog(this, "El árbol B+ está vacío.",
                    "Árbol vacío", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        appendLog.accept("\n--- Búsqueda por Género: '" + genero + "' ---", "ok");

        ListaLibros resultados = arbolBPlus.buscarPorGenero(genero);

        if (resultados.getTamaño() > 0) {
            String encabezado = "Se encontraron " + resultados.getTamaño() +
                    " libros en el género '" + genero + "':";
            appendLog.accept(encabezado, "warning");

            // Recorrer resultados para el log
            StringBuilder mensaje = new StringBuilder();
            ListaLibros.Iterador iterador = resultados.obtenerIterador();
            while (iterador.tieneSiguiente()) {
                Libro libro = iterador.siguiente();
                mensaje.append("- ").append(libro.toString()).append("\n");
            }
            appendLog.accept(mensaje.toString(), "ok");

        } else {
            appendLog.accept("No se encontraron libros en el género '" + genero + "'", "error");
        }

        llenarTablaDesdeListaLibros(resultados);
    }

    private void buscarPorFecha(int desde, int hasta) {
        if (arbolB == null || arbolB.getRaiz() == null) {
            appendLog.accept("El árbol B está vacío. Cargue datos antes de buscar.", "error");
            JOptionPane.showMessageDialog(this, "El árbol B está vacío.",
                    "Árbol vacío", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        appendLog.accept("\n--- Búsqueda por Fecha: " + desde + " - " + hasta + " ---", "ok");

        ListaLibros resultados = arbolB.buscarPorRango(desde, hasta);

        if (resultados.getTamaño() > 0) {
            String encabezado = "Se encontraron " + resultados.getTamaño() +
                    " libros entre " + desde + " y " + hasta + ":";
            appendLog.accept(encabezado, "warning");

            // Recorrer resultados para el log
            StringBuilder mensaje = new StringBuilder();
            ListaLibros.Iterador iterador = resultados.obtenerIterador();
            while (iterador.tieneSiguiente()) {
                Libro libro = iterador.siguiente();
                mensaje.append("- ").append(libro.toString()).append("\n");
            }
            appendLog.accept(mensaje.toString(), "ok");

        } else {
            appendLog.accept("No se encontraron libros entre " + desde + " y " + hasta, "error");
        }

        llenarTablaDesdeListaLibros(resultados);
    }

    private void limpiarTabla() {
        DefaultTableModel model = (DefaultTableModel) tablaResultados.getModel();
        model.setRowCount(0);
    }

    /*private void llenarTablaDesdeListaEncontados(ListaEncontados resultados) {
        limpiarTabla();

        if (resultados == null || resultados.getCabeza() == null) {
            mostrarMensajeNoResultados();
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tablaResultados.getModel();
        Nodo actual = resultados.getCabeza();
        int fila = 0;

        while (actual != null) {
            Libro libro = actual.libro;
            model.addRow(new Object[]{
                    fila + 1,
                    libro.getTitulo(),
                    libro.getAutor(),
                    libro.getIsbn(),
                    libro.getGenero(),
                    libro.getFecha(),
                    libro.getCantidad()
            });
            actual = actual.siguiente;
            fila++;
        }
    }*/

    private void llenarTablaDesdeListaLibros(ListaLibros resultados) {
        limpiarTabla();

        if (resultados == null || resultados.getTamaño() == 0) {
            mostrarMensajeNoResultados();
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tablaResultados.getModel();
        ListaLibros.Iterador iterador = resultados.obtenerIterador();  // USAR ITERADOR CORRECTO
        int fila = 0;

        while (iterador.tieneSiguiente()) {
            Libro libro = iterador.siguiente();
            model.addRow(new Object[]{
                    fila + 1,
                    libro.getTitulo(),
                    libro.getAutor(),
                    libro.getIsbn(),
                    libro.getGenero(),
                    libro.getFecha(),
                    libro.getCantidad()
            });
            fila++;
        }
    }

    private void llenarTablaDesdeLibroUnico(Libro libro) {
        limpiarTabla();

        if (libro == null) {
            mostrarMensajeNoResultados();
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tablaResultados.getModel();
        model.addRow(new Object[]{
                1,
                libro.getTitulo(),
                libro.getAutor(),
                libro.getIsbn(),
                libro.getGenero(),
                libro.getFecha(),
                libro.getCantidad()
        });
    }

    private void mostrarMensajeNoResultados() {
        DefaultTableModel model = (DefaultTableModel) tablaResultados.getModel();
        model.addRow(new Object[]{"No se encontraron resultados", "", "", "", "", "", ""});
    }
}
