package org.example.GUI;

import org.example.Grafo.GrafoBibliotecas;
import org.example.Modelos.Biblioteca;
import org.example.Modelos.Libro;
import org.example.TablaHash.Iterador;
import org.example.TablaHash.TablaHash;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EliminarLibroGlobal extends JDialog {

    private GrafoBibliotecas grafo;
    private JTextField txtISBN;
    private JTextArea areaResultados;
    private JButton btnBuscar;
    private JButton btnEliminar;
    private JButton btnCancelar;
    private JCheckBox checkSeleccionarTodos;
    private JPanel panelCheckboxes;
    private Runnable onEliminacionCompletada;

    private List<Biblioteca> bibliotecasConLibro;
    private List<Libro> librosEncontrados;
    private List<JCheckBox> checkboxes;
    private String isbnActual;

    public EliminarLibroGlobal(JFrame parent, GrafoBibliotecas grafo, Runnable onEliminacionCompletada) {
        super(parent, "Eliminar Libro - Global", true);
        this.grafo = grafo;
        this.onEliminacionCompletada = onEliminacionCompletada;
        this.bibliotecasConLibro = new ArrayList<>();
        this.librosEncontrados = new ArrayList<>();
        this.checkboxes = new ArrayList<>();

        initComponents();
        pack();
        setLocationRelativeTo(parent);
        setSize(600, 500); // Un poco más grande para los checkboxes
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setResizable(true);

        // Panel superior - Entrada de ISBN
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblISBN = new JLabel("ISBN del libro a eliminar:");
        lblISBN.setFont(new Font("Arial", Font.BOLD, 12));

        txtISBN = new JTextField(15);
        txtISBN.setFont(new Font("Arial", Font.PLAIN, 12));

        btnBuscar = new JButton("Buscar");
        btnBuscar.setPreferredSize(new Dimension(100, 25));
        btnBuscar.setFocusPainted(false);

        JPanel panelEntrada = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelEntrada.add(lblISBN);
        panelEntrada.add(txtISBN);
        panelEntrada.add(btnBuscar);

        panelSuperior.add(panelEntrada, BorderLayout.CENTER);
        add(panelSuperior, BorderLayout.NORTH);

        // Panel central - Checkboxes de selección
        panelCheckboxes = new JPanel();
        panelCheckboxes.setLayout(new BoxLayout(panelCheckboxes, BoxLayout.Y_AXIS));
        panelCheckboxes.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Seleccionar bibliotecas para eliminar",
                TitledBorder.LEFT,
                TitledBorder.TOP
        ));
        panelCheckboxes.setPreferredSize(new Dimension(580, 150));

        checkSeleccionarTodos = new JCheckBox("Seleccionar todos los disponibles");
        checkSeleccionarTodos.setEnabled(false);
        checkSeleccionarTodos.addActionListener(e -> seleccionarTodos());

        JPanel panelCheckSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCheckSuperior.add(checkSeleccionarTodos);
        panelCheckboxes.add(panelCheckSuperior);

        JScrollPane scrollCheckboxes = new JScrollPane(panelCheckboxes);
        scrollCheckboxes.setPreferredSize(new Dimension(580, 150));
        add(scrollCheckboxes, BorderLayout.CENTER);

        // Panel inferior - Resultados y botones
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Área de resultados
        areaResultados = new JTextArea(6, 50);
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaResultados.setBackground(new Color(240, 240, 240));
        areaResultados.setText("Ingrese un ISBN y haga click en 'Buscar' para comenzar.");

        JScrollPane scrollResultados = new JScrollPane(areaResultados);
        scrollResultados.setBorder(BorderFactory.createTitledBorder("Información del libro"));
        panelInferior.add(scrollResultados, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        btnEliminar = new JButton("Eliminar Seleccionados");
        btnEliminar.setBackground(new Color(220, 80, 60));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setEnabled(false);
        btnEliminar.setPreferredSize(new Dimension(180, 30));

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(100, 30));
        btnCancelar.setFocusPainted(false);

        panelBotones.add(btnEliminar);
        panelBotones.add(btnCancelar);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);

        // Listeners
        btnBuscar.addActionListener(e -> buscarLibro());
        btnEliminar.addActionListener(e -> eliminarLibro());
        btnCancelar.addActionListener(e -> dispose());
        txtISBN.addActionListener(e -> buscarLibro());
    }

    private void buscarLibro() {
        String isbn = txtISBN.getText().trim();

        if (isbn.isEmpty()) {
            areaResultados.setText("Error: Debe ingresar un ISBN");
            return;
        }

        // Limpiar resultados anteriores
        bibliotecasConLibro.clear();
        librosEncontrados.clear();
        checkboxes.clear();
        panelCheckboxes.removeAll();
        btnEliminar.setEnabled(false);
        checkSeleccionarTodos.setEnabled(false);
        checkSeleccionarTodos.setSelected(false);

        // Buscar en todas las bibliotecas
        TablaHash<String, Biblioteca> bibliotecas = grafo.getBibliotecas();

        if (bibliotecas.estaVacia()) {
            areaResultados.setText("No hay bibliotecas en el sistema.");
            return;
        }

        Iterador<Biblioteca> iterador = bibliotecas.iteradorValores();
        while (iterador.tieneSiguiente()) {
            Biblioteca bib = iterador.siguiente();
            Libro libro = bib.buscarPorISBN(isbn);
            if (libro != null) {
                bibliotecasConLibro.add(bib);
                librosEncontrados.add(libro);
            }
        }

        // Mostrar resultados
        if (bibliotecasConLibro.isEmpty()) {
            areaResultados.setText("Libro no encontrado\n\n" +
                    "ISBN: " + isbn + "\n" +
                    "No existe en ninguna biblioteca del sistema.");
        } else {
            isbnActual = isbn;
            mostrarCheckboxesSeleccion();
            mostrarInformacionLibro();
        }
    }

    private void mostrarCheckboxesSeleccion() {
        // Panel para "Seleccionar todos"
        JPanel panelCheckSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCheckSuperior.add(checkSeleccionarTodos);
        panelCheckboxes.add(panelCheckSuperior);

        // Crear checkboxes para cada biblioteca
        for (int i = 0; i < bibliotecasConLibro.size(); i++) {
            Biblioteca bib = bibliotecasConLibro.get(i);
            Libro libro = librosEncontrados.get(i);

            JCheckBox checkBox = new JCheckBox();
            String texto = String.format("%s - %s (%s) - Estado: %s",
                    bib.getId(), bib.getNombre(), bib.getUbicacion(), libro.getEstado());

            checkBox.setText(texto);
            checkBox.setEnabled("Disponible".equalsIgnoreCase(libro.getEstado()));

            if (!"Disponible".equalsIgnoreCase(libro.getEstado())) {
                checkBox.setToolTipText("No se puede eliminar - Estado: " + libro.getEstado());
                checkBox.setForeground(Color.GRAY);
            }

            checkboxes.add(checkBox);
            panelCheckboxes.add(checkBox);
        }

        checkSeleccionarTodos.setEnabled(true);
        btnEliminar.setEnabled(true);

        panelCheckboxes.revalidate();
        panelCheckboxes.repaint();
    }

    private void mostrarInformacionLibro() {
        if (librosEncontrados.isEmpty()) return;

        // Tomar el primer libro para mostrar información general
        Libro primerLibro = librosEncontrados.get(0);

        StringBuilder sb = new StringBuilder();
        sb.append("INFORMACIÓN DEL LIBRO\n\n");
        sb.append("Título: ").append(primerLibro.getTitulo()).append("\n");
        sb.append("Autor: ").append(primerLibro.getAutor()).append("\n");
        sb.append("ISBN: ").append(primerLibro.getIsbn()).append("\n");
        sb.append("Género: ").append(primerLibro.getGenero()).append("\n");
        sb.append("Año: ").append(primerLibro.getFecha()).append("\n\n");

        sb.append("📊 DISTRIBUCIÓN EN BIBLIOTECAS:\n");
        sb.append("• Total encontrado: ").append(bibliotecasConLibro.size()).append(" biblioteca(s)\n");

        long disponibles = librosEncontrados.stream()
                .filter(libro -> "Disponible".equalsIgnoreCase(libro.getEstado()))
                .count();
        long noDisponibles = bibliotecasConLibro.size() - disponibles;

        sb.append("• Disponibles para eliminar: ").append(disponibles).append("\n");
        sb.append("• No disponibles: ").append(noDisponibles).append("\n\n");

        sb.append("Seleccione las bibliotecas de donde desea eliminar el libro.");

        areaResultados.setText(sb.toString());
    }

    private void seleccionarTodos() {
        boolean seleccionar = checkSeleccionarTodos.isSelected();
        for (JCheckBox checkBox : checkboxes) {
            if (checkBox.isEnabled()) {
                checkBox.setSelected(seleccionar);
            }
        }
    }

    private void eliminarLibro() {
        if (bibliotecasConLibro.isEmpty() || isbnActual == null) {
            return;
        }

        // Verificar que al menos uno esté seleccionado
        boolean algunSeleccionado = checkboxes.stream()
                .anyMatch(JCheckBox::isSelected);

        if (!algunSeleccionado) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar al menos una biblioteca para eliminar.",
                    "Ninguna selección",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Contar seleccionados
        int seleccionados = 0;
        for (JCheckBox checkBox : checkboxes) {
            if (checkBox.isSelected()) {
                seleccionados++;
            }
        }

        // Confirmación final
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el libro de " + seleccionados + " biblioteca(s)?\n\n" +
                        "ISBN: " + isbnActual + "\n" +
                        "Esta acción no se puede deshacer.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        // Procesar eliminación
        int eliminadosExitosos = 0;
        int errores = 0;

        for (int i = 0; i < checkboxes.size(); i++) {
            if (checkboxes.get(i).isSelected()) {
                Biblioteca bib = bibliotecasConLibro.get(i);

                try {
                    boolean eliminado = bib.eliminarLibroPorISBN(isbnActual);
                    if (eliminado) {
                        eliminadosExitosos++;
                    } else {
                        errores++;
                    }
                } catch (Exception e) {
                    errores++;
                }
            }
        }

        // Mostrar resumen
        StringBuilder resultado = new StringBuilder();
        resultado.append("RESULTADO DE ELIMINACIÓN:\n\n");
        resultado.append("Eliminados exitosamente: ").append(eliminadosExitosos).append("\n");

        if (errores > 0) {
            resultado.append("Errores durante la eliminación: ").append(errores).append("\n");
        }

        areaResultados.setText(resultado.toString());
        btnEliminar.setEnabled(false);
        checkSeleccionarTodos.setEnabled(false);

        // Ejecutar callback si hubo eliminaciones exitosas
        if (eliminadosExitosos > 0 && onEliminacionCompletada != null) {
            onEliminacionCompletada.run();
        }
    }
}