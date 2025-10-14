package org.example.GUI.VistasGrafo;

import org.example.Grafo.GrafoBibliotecas;
import javax.swing.*;
import java.awt.*;

public class AgregarBibliotecaManual extends JDialog {

    private GrafoBibliotecas grafo;
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtUbicacion;
    private JTextField txtTIngreso;
    private JTextField txtTTraspaso;
    private JTextField txtIntervalo;
    private Runnable onBibliotecaAgregada;

    public AgregarBibliotecaManual(JFrame parent, GrafoBibliotecas grafo, Runnable onBibliotecaAgregada) {
        super(parent, "Agregar Biblioteca", true);
        this.grafo = grafo;
        this.onBibliotecaAgregada = onBibliotecaAgregada;

        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new GridLayout(7, 2, 5, 5));
        setSize(400, 300);

        txtId = new JTextField();
        txtNombre = new JTextField();
        txtUbicacion = new JTextField();
        txtTIngreso = new JTextField();
        txtTTraspaso = new JTextField();
        txtIntervalo = new JTextField();

        add(new JLabel("ID:"));
        add(txtId);
        add(new JLabel("Nombre:"));
        add(txtNombre);
        add(new JLabel("Ubicación:"));
        add(txtUbicacion);
        add(new JLabel("Tiempo Ingreso (s):"));
        add(txtTIngreso);
        add(new JLabel("Tiempo Traspaso (s):"));
        add(txtTTraspaso);
        add(new JLabel("Intervalo Despacho (s):"));
        add(txtIntervalo);

        JButton btnAceptar = new JButton("Agregar");
        JButton btnCancelar = new JButton("Cancelar");

        btnAceptar.addActionListener(e -> agregarBiblioteca());
        btnCancelar.addActionListener(e -> dispose());

        add(btnAceptar);
        add(btnCancelar);
    }

    private void agregarBiblioteca() {
        try {
            String id = txtId.getText().trim();
            String nombre = txtNombre.getText().trim();
            String ubicacion = txtUbicacion.getText().trim();
            int tIngreso = Integer.parseInt(txtTIngreso.getText().trim());
            int tTraspaso = Integer.parseInt(txtTTraspaso.getText().trim());
            int intervalo = Integer.parseInt(txtIntervalo.getText().trim());

            // Validaciones
            if (id.isEmpty() || nombre.isEmpty() || ubicacion.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "ID, Nombre y Ubicación son campos obligatorios.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (grafo.existeBiblioteca(id)) {
                JOptionPane.showMessageDialog(this,
                        "Ya existe una biblioteca con el ID: " + id,
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (tIngreso <= 0 || tTraspaso <= 0 || intervalo <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Los tiempos deben ser valores positivos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Agregar la biblioteca
            grafo.agregarBiblioteca(id, nombre, ubicacion, tIngreso, tTraspaso, intervalo);

            // Ejecutar callback
            if (onBibliotecaAgregada != null) {
                onBibliotecaAgregada.run();
            }

            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Los tiempos deben ser números enteros válidos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al agregar biblioteca: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}