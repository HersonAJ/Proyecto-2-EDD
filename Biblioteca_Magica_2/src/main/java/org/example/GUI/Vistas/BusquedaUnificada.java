package org.example.GUI.Vistas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.BiConsumer;
import javax.swing.table.DefaultTableModel;

public class BusquedaUnificada extends JPanel {


    private Object arbolTitulos;
    private Object indiceISBN;
    private Object arbolB;
    private Object arbolBPlus;

    // Callback para logging
    private BiConsumer<String, String> appendLog;

    // Componentes UI
    private JComboBox<String> comboTipoBusqueda;
    private JTextField editBusquedaSimple;
    private JSpinner spinFechaDesde;
    private JSpinner spinFechaHasta;
    private JLabel labelCampo;
    private JButton btnBuscar;
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;



    public BusquedaUnificada(Object arbolTitulos,
                             Object indiceISBN,
                             Object arbolB,
                             Object arbolBPlus,
                             BiConsumer<String, String> logCallBack) {
        this.arbolTitulos = arbolTitulos;
        this.indiceISBN = indiceISBN;
        this.arbolB = arbolB;
        this.arbolBPlus = arbolBPlus;
        this.appendLog = logCallBack;

        setupUI();
    }

    //quitar sobrecarga luego
    public BusquedaUnificada() {
        this(null, null, null, null, (m, t) -> {});
    }


    private void setupUI() {
        setLayout(new BorderLayout());

        // Panel de configuración de búsqueda
        JPanel panelBusqueda = new JPanel(new GridLayout(0, 2));

        comboTipoBusqueda = new JComboBox<>(new String[]{
                "Por Título", "Por ISBN", "Por Género", "Por Fecha"
        });
        comboTipoBusqueda.addActionListener(e -> onTipoBusquedaCambiado(comboTipoBusqueda.getSelectedIndex()));
        panelBusqueda.add(new JLabel("Tipo de búsqueda:"));
        panelBusqueda.add(comboTipoBusqueda);

        // Campo simple
        editBusquedaSimple = new JTextField();
        panelBusqueda.add(new JLabel("Término:"));
        panelBusqueda.add(editBusquedaSimple);

        // Campos de fecha
        spinFechaDesde = new JSpinner(new SpinnerNumberModel(1900, 0, 3000, 1));
        spinFechaHasta = new JSpinner(new SpinnerNumberModel(2024, 0, 3000, 1));
        spinFechaDesde.setVisible(false);
        spinFechaHasta.setVisible(false);

        panelBusqueda.add(spinFechaDesde);
        panelBusqueda.add(spinFechaHasta);

        // Botón buscar
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBuscarClicked();
            }
        });

        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.add(panelBusqueda, BorderLayout.CENTER);
        panelTop.add(btnBuscar, BorderLayout.SOUTH);

        add(panelTop, BorderLayout.NORTH);

        // Tabla de resultados
        String[] columnas = {"No.", "Título", "Autor", "ISBN", "Género", "Fecha", "Ejemplares"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaResultados = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaResultados);

        add(scrollTabla, BorderLayout.CENTER);

        // Mostrar campos iniciales
        mostrarCamposPorTipo(0);
    }


    private void onTipoBusquedaCambiado(int index) {
        mostrarCamposPorTipo(index);
    }

    private void mostrarCamposPorTipo(int tipo) {
        // Aquí luego se implementará la lógica de mostrar/ocultar campos
    }

    private void onBuscarClicked() {
        // Aquí luego se implementará la lógica de validación y búsqueda
    }

    public void buscarPorTitulo(String titulo) {
        // Aquí luego se implementará la búsqueda por título
    }

    public void buscarPorISBN(String isbn) {
        // Aquí luego se implementará la búsqueda por ISBN
    }

    public void buscarPorGenero(String genero) {
        // Aquí luego se implementará la búsqueda por género
    }

    public void buscarPorFecha(int desde, int hasta) {
        // Aquí luego se implementará la búsqueda por rango de fechas
    }

    private void limpiarTabla() {
        modeloTabla.setRowCount(0);
    }

    private void llenarTablaDesdeListaEncontados(Object resultados) {
        // Aquí luego se implementará el llenado de la tabla desde ListaEncontados
    }

    private void llenarTablaDesdeListaLibros(Object resultados) {
        // Aquí luego se implementará el llenado de la tabla desde ListaLibros
    }

    private void llenarTablaDesdeLibroUnico(Object libro) {
        // Aquí luego se implementará el llenado de la tabla desde un único libro
    }

    private void mostrarMensajeNoResultados() {
        limpiarTabla();
        modeloTabla.addRow(new Object[]{"No se encontraron resultados"});
    }
}
