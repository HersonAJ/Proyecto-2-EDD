package org.example.GUI.VistasGrafo;

import org.example.Grafo.GrafoBibliotecas;
import org.example.Modelos.Biblioteca;
import org.example.Modelos.Libro;
import org.example.TablaHash.Iterador;
import org.example.TablaHash.TablaHash;

import javax.swing.*;
import java.awt.*;

public class AgregarLibroManual extends JDialog {

    private GrafoBibliotecas grafo;
    private JComboBox<String> cmbBibliotecas;
    private JTextField txtTitulo;
    private JTextField txtISBN;
    private JTextField txtGenero;
    private JTextField txtFecha;
    private JTextField txtAutor;
    private Runnable onLibroAgregado;

    public AgregarLibroManual(JFrame parent, GrafoBibliotecas grafo, Runnable onLibroAgregado) {
        super(parent, "Agregar Libro", true);
        this.grafo = grafo;
        this.onLibroAgregado = onLibroAgregado;

        initComponents();
        cargarBibliotecas();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new GridLayout(7, 2, 5, 5));
        setSize(400, 300);

        cmbBibliotecas = new JComboBox<>();
        txtTitulo = new JTextField();
        txtISBN = new JTextField();
        txtGenero = new JTextField();
        txtFecha = new JTextField();
        txtAutor = new JTextField();

        add(new JLabel("Biblioteca Destino:"));
        add(cmbBibliotecas);
        add(new JLabel("Título:"));
        add(txtTitulo);
        add(new JLabel("ISBN:"));
        add(txtISBN);
        add(new JLabel("Género:"));
        add(txtGenero);
        add(new JLabel("Fecha:"));
        add(txtFecha);
        add(new JLabel("Autor:"));
        add(txtAutor);

        JButton btnAceptar = new JButton("Agregar");
        JButton btnCancelar = new JButton("Cancelar");

        btnAceptar.addActionListener(e -> agregarLibro());
        btnCancelar.addActionListener(e -> dispose());

        add(btnAceptar);
        add(btnCancelar);
    }

    private void cargarBibliotecas() {
        cmbBibliotecas.removeAllItems();

        TablaHash<String, Biblioteca> bibliotecas = grafo.getBibliotecas();
        Iterador<String> iterador = bibliotecas.iteradorClaves();
        while (iterador.tieneSiguiente()) {
            String id = iterador.siguiente();
            cmbBibliotecas.addItem(id);
        }

        // Seleccionar el primero por defecto si existe
        if (cmbBibliotecas.getItemCount() > 0) {
            cmbBibliotecas.setSelectedIndex(0);
        }
    }

    private void agregarLibro() {
        try {
            String idBiblioteca = (String) cmbBibliotecas.getSelectedItem();
            String titulo = txtTitulo.getText().trim();
            String isbn = txtISBN.getText().trim();
            String genero = txtGenero.getText().trim();
            String fecha = txtFecha.getText().trim();
            String autor = txtAutor.getText().trim();

            // Validaciones básicas
            if (titulo.isEmpty() || isbn.isEmpty() || genero.isEmpty() || fecha.isEmpty() || autor.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar que la fecha sea numérica
            try {
                Integer.parseInt(fecha);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "La fecha debe ser un año válido (ej: 2023).", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Biblioteca bibliotecaDestino = grafo.getBiblioteca(idBiblioteca);
            if (bibliotecaDestino == null) {
                JOptionPane.showMessageDialog(this,
                        "Biblioteca no encontrada.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar si el ISBN ya existe en ESA biblioteca
            if (bibliotecaDestino.buscarPorISBN(isbn) != null) {
                JOptionPane.showMessageDialog(this,
                        "El ISBN ya existe en esta biblioteca.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crear y agregar libro
            Libro libro = new Libro(titulo, isbn, genero, fecha, autor);
            bibliotecaDestino.agregarLibro(libro);

            // Callback
            if (onLibroAgregado != null) {
                onLibroAgregado.run();
            }

            JOptionPane.showMessageDialog(this,
                    "Libro agregado correctamente a la biblioteca: " + idBiblioteca,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al agregar libro: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}