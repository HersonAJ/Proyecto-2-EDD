package org.example.GUI;

import org.example.Grafo.GrafoBibliotecas;
import org.example.Modelos.Biblioteca;
import org.example.Modelos.Libro;
import org.example.Modelos.ListaLibros;
import org.example.TablaHash.Iterador;
import org.example.TablaHash.TablaHash;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

public class BusquedaGlobal extends JPanel {

    private GrafoBibliotecas grafo;

    // --- Componentes de UI ---
    private JComboBox<String> comboTipoBusqueda;
    private JLabel labelCampo;
    private JTextField editBusqueda;
    private JSpinner spinDesde, spinHasta;
    private JButton btnBuscar, btnLimpiar;
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;
    private JLabel lblResumen;

    public BusquedaGlobal(GrafoBibliotecas grafo) {
        this.grafo = grafo;
        configurarUI();
    }

    private void configurarUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // PANEL DE BÚSQUEDA SUPERIOR
        JPanel panelBusqueda = new JPanel();
        panelBusqueda.setLayout(new BoxLayout(panelBusqueda, BoxLayout.Y_AXIS));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("🔍 Configurar Búsqueda Global"));

        // Tipo de búsqueda
        JPanel panelTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTipo.add(new JLabel("Tipo de búsqueda:"));

        comboTipoBusqueda = new JComboBox<>(new String[]{
                "Por Título", "Por ISBN", "Por Género", "Por Rango de Fechas", "Por Autor"
        });
        comboTipoBusqueda.addActionListener(e -> onTipoBusquedaCambiado(comboTipoBusqueda.getSelectedIndex()));
        panelTipo.add(comboTipoBusqueda);
        panelBusqueda.add(panelTipo);

        // Campo de búsqueda dinámico
        JPanel panelCampos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelCampo = new JLabel("Término:");
        editBusqueda = new JTextField(25);
        editBusqueda.setToolTipText("Ingrese título, ISBN, género o autor...");

        spinDesde = new JSpinner(new SpinnerNumberModel(1900, 0, 3000, 1));
        spinHasta = new JSpinner(new SpinnerNumberModel(2025, 0, 3000, 1));

        JLabel lblDesde = new JLabel("Desde:");
        JLabel lblHasta = new JLabel("Hasta:");

        // Por defecto ocultos
        lblDesde.setVisible(false);
        lblHasta.setVisible(false);
        spinDesde.setVisible(false);
        spinHasta.setVisible(false);

        panelCampos.add(labelCampo);
        panelCampos.add(editBusqueda);
        panelCampos.add(lblDesde);
        panelCampos.add(spinDesde);
        panelCampos.add(lblHasta);
        panelCampos.add(spinHasta);
        panelBusqueda.add(panelCampos);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnBuscar = new JButton("Buscar");
        btnLimpiar = new JButton("Limpiar");

        btnBuscar.addActionListener(this::onBuscarClicked);
        btnLimpiar.addActionListener(e -> limpiarBusqueda());
        panelBotones.add(btnBuscar);
        panelBotones.add(btnLimpiar);
        panelBusqueda.add(panelBotones);

        add(panelBusqueda, BorderLayout.NORTH);

        // --- TABLA DE RESULTADOS ---
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder("📚 Resultados de la Búsqueda Global"));
        configurarTabla();
        panelTabla.add(new JScrollPane(tablaResultados), BorderLayout.CENTER);

        // Etiqueta resumen
        lblResumen = new JLabel("Sin resultados");
        lblResumen.setFont(new Font("Arial", Font.BOLD, 12));
        lblResumen.setHorizontalAlignment(SwingConstants.RIGHT);
        panelTabla.add(lblResumen, BorderLayout.SOUTH);

        add(panelTabla, BorderLayout.CENTER);
    }

    private void configurarTabla() {
        String[] columnas = {"#", "Biblioteca","Título", "Autor", "ISBN", "Género", "Fecha", "Ejemplares", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaResultados = new JTable(modeloTabla);
        tablaResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaResultados.setShowGrid(true);
        tablaResultados.setGridColor(Color.LIGHT_GRAY);
        tablaResultados.setFillsViewportHeight(true);

        // Estilo de cabecera
        tablaResultados.getTableHeader().setBackground(new Color(33, 150, 243));
        tablaResultados.getTableHeader().setForeground(Color.WHITE);
        tablaResultados.getTableHeader().setFont(tablaResultados.getTableHeader().getFont().deriveFont(Font.BOLD));

        // Estilo de filas
        tablaResultados.setSelectionBackground(new Color(220, 240, 255));
        tablaResultados.setSelectionForeground(Color.BLACK);
        tablaResultados.setRowHeight(24);

        // Ajuste de columnas
        int[] anchos = {40, 180, 150, 100, 100, 70, 80, 80, 120};
        for (int i = 0; i < anchos.length; i++) {
            tablaResultados.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    // --- Eventos ---
    private void onTipoBusquedaCambiado(int index) {
        boolean esFecha = (index == 3); // Rango de Fechas
        editBusqueda.setVisible(!esFecha);

        Component[] comps = ((JPanel) spinDesde.getParent()).getComponents();
        for (Component c : comps) {
            if (c instanceof JLabel lbl && (lbl.getText().equals("Desde:") || lbl.getText().equals("Hasta:"))) {
                lbl.setVisible(esFecha);
            }
        }
        spinDesde.setVisible(esFecha);
        spinHasta.setVisible(esFecha);

        switch (index) {
            case 0 -> labelCampo.setText("Título:");
            case 1 -> labelCampo.setText("ISBN:");
            case 2 -> labelCampo.setText("Género:");
            case 3 -> labelCampo.setText("Rango de años:");
            case 4 -> labelCampo.setText("Autor:");
        }

        revalidate();
        repaint();
    }

    private void onBuscarClicked(ActionEvent e) {
        int tipo = comboTipoBusqueda.getSelectedIndex();
        limpiarTabla();

        if (grafo == null || grafo.getBibliotecas().estaVacia()) {
            JOptionPane.showMessageDialog(this, "No hay bibliotecas cargadas.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        switch (tipo) {
            case 0 -> buscarPorTitulo();
            case 1 -> buscarPorISBN();
            case 2 -> buscarPorGenero();
            case 3 -> buscarPorRangoFechas();
            case 4 -> buscarPorAutor();
        }
    }

    // --- BÚSQUEDAS ---
    private void buscarPorTitulo() {
        String titulo = editBusqueda.getText().trim();
        if (titulo.isEmpty()) {
            mensajeError("Ingrese un título para buscar.");
            return;
        }
        Map<Biblioteca, List<Libro>> resultados = buscarEnTodas(bib -> bib.buscarTodosPorTitulo(titulo));
        mostrarResultados(resultados, "Título: " + titulo);
    }

    private void buscarPorISBN() {
        String isbn = editBusqueda.getText().trim();
        if (isbn.isEmpty()) {
            mensajeError("Ingrese un ISBN para buscar.");
            return;
        }
        Map<Biblioteca, List<Libro>> resultados = new HashMap<>();
        int total = 0;

        Iterador<Biblioteca> it = grafo.getBibliotecas().iteradorValores();
        while (it.tieneSiguiente()) {
            Biblioteca bib = it.siguiente();
            Libro libro = bib.buscarPorISBN(isbn);
            if (libro != null) {
                resultados.computeIfAbsent(bib, k -> new ArrayList<>()).add(libro);
                total++;
            }
        }
        mostrarResultados(resultados, "ISBN: " + isbn);
    }

    private void buscarPorGenero() {
        String genero = editBusqueda.getText().trim();
        if (genero.isEmpty()) {
            mensajeError("Ingrese un género para buscar.");
            return;
        }
        Map<Biblioteca, List<Libro>> resultados = buscarEnTodas(bib -> bib.buscarPorGenero(genero));
        mostrarResultados(resultados, "Género: " + genero);
    }

    private void buscarPorRangoFechas() {
        int desde = (Integer) spinDesde.getValue();
        int hasta = (Integer) spinHasta.getValue();
        if (desde > hasta) {
            mensajeError("El año inicial no puede ser mayor al final.");
            return;
        }
        Map<Biblioteca, List<Libro>> resultados = buscarEnTodas(bib -> bib.buscarPorRangoFechas(desde, hasta));
        mostrarResultados(resultados, "Rango: " + desde + " - " + hasta);
    }

    private void buscarPorAutor() {
        String autor = editBusqueda.getText().trim();
        if (autor.isEmpty()) {
            mensajeError("Ingrese un autor para buscar.");
            return;
        }

        Map<Biblioteca, List<Libro>> resultados = new HashMap<>();
        Iterador<Biblioteca> it = grafo.getBibliotecas().iteradorValores();

        while (it.tieneSiguiente()) {
            Biblioteca bib = it.siguiente();
            ListaLibros todos = bib.getArbolTitulos().obtenerLibrosEnOrdenAlfabetico();
            ListaLibros.Iterador iter = todos.obtenerIterador();
            List<Libro> lista = new ArrayList<>();

            while (iter.tieneSiguiente()) {
                Libro libro = iter.siguiente();
                if (libro.getAutor().toLowerCase().contains(autor.toLowerCase())) {
                    lista.add(libro);
                }
            }

            if (!lista.isEmpty()) resultados.put(bib, lista);
        }
        mostrarResultados(resultados, "Autor: " + autor);
    }

    // --- UTILITARIOS ---
    private Map<Biblioteca, List<Libro>> buscarEnTodas(java.util.function.Function<Biblioteca, ListaLibros> buscador) {
        Map<Biblioteca, List<Libro>> resultados = new HashMap<>();
        Iterador<Biblioteca> it = grafo.getBibliotecas().iteradorValores();

        while (it.tieneSiguiente()) {
            Biblioteca bib = it.siguiente();
            ListaLibros lista = buscador.apply(bib);
            if (!lista.estaVacia()) {
                List<Libro> libros = new ArrayList<>();
                ListaLibros.Iterador iter = lista.obtenerIterador();
                while (iter.tieneSiguiente()) libros.add(iter.siguiente());
                resultados.put(bib, libros);
            }
        }
        return resultados;
    }

    private void mostrarResultados(Map<Biblioteca, List<Libro>> resultados, String criterio) {
        limpiarTabla();
        int total = 0;

        for (Map.Entry<Biblioteca, List<Libro>> entry : resultados.entrySet()) {
            Biblioteca bib = entry.getKey();
            List<Libro> lista = entry.getValue();
            int i = 1;
            for (Libro l : lista) {
                modeloTabla.addRow(new Object[]{
                        modeloTabla.getRowCount() + 1,
                        bib.getNombre() +" - ID ->" +  bib.getId(),
                        l.getTitulo(),
                        l.getAutor(),
                        l.getIsbn(),
                        l.getGenero(),
                        l.getFecha(),
                        l.getCantidad(),
                        l.getEstado()
                });
                total++;
                i++;
            }
        }

        lblResumen.setText("📊 " + resultados.size() + " bibliotecas | " + total + " libros encontrados");
        if (total == 0)
            modeloTabla.addRow(new Object[]{"Sin resultados", "", "", "", "", "", "", "", ""});
    }

    private void limpiarTabla() {
        modeloTabla.setRowCount(0);
    }

    private void limpiarBusqueda() {
        editBusqueda.setText("");
        spinDesde.setValue(1900);
        spinHasta.setValue(2025);
        limpiarTabla();
        lblResumen.setText("Sin resultados");
    }

    private void mensajeError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.WARNING_MESSAGE);
    }
}
