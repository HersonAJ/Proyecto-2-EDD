package org.example.GUI.VistasGrafo;

import org.example.Grafo.GrafoBibliotecas;
import javax.swing.*;
import java.awt.*;

public class ConexionManual extends JDialog {

    private GrafoBibliotecas grafo;
    private JComboBox<String> cmbOrigen;
    private JComboBox<String> cmbDestino;
    private JTextField txtTiempo;
    private JTextField txtCosto;
    private Runnable onConexionAgregada;

    public ConexionManual(JFrame parent, GrafoBibliotecas grafo, Runnable onConexionAgregada) {
        super(parent, "Agregar Conexión entre Bibliotecas", true);
        this.grafo = grafo;
        this.onConexionAgregada = onConexionAgregada;

        initComponents();
        cargarBibliotecas();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new GridLayout(6, 2, 5, 5));
        setSize(400, 250);

        cmbOrigen = new JComboBox<>();
        cmbDestino = new JComboBox<>();
        txtTiempo = new JTextField();
        txtCosto = new JTextField();

        add(new JLabel("Biblioteca Origen:"));
        add(cmbOrigen);
        add(new JLabel("Biblioteca Destino:"));
        add(cmbDestino);
        add(new JLabel("Tiempo (segundos):"));
        add(txtTiempo);
        add(new JLabel("Costo:"));
        add(txtCosto);

        JButton btnAceptar = new JButton("Agregar Conexión");
        JButton btnCancelar = new JButton("Cancelar");

        btnAceptar.addActionListener(e -> agregarConexion());
        btnCancelar.addActionListener(e -> dispose());

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        add(new JLabel()); // Espacio vacío
        add(new JLabel()); // Espacio vacío
        add(panelBotones);
    }

    private void cargarBibliotecas() {
        cmbOrigen.removeAllItems();
        cmbDestino.removeAllItems();

        for (String id : grafo.getBibliotecas().keySet()) {
            cmbOrigen.addItem(id);
            cmbDestino.addItem(id);
        }
    }

    private void agregarConexion() {
        try {
            String origen = (String) cmbOrigen.getSelectedItem();
            String destino = (String) cmbDestino.getSelectedItem();
            int tiempo = Integer.parseInt(txtTiempo.getText().trim());
            double costo = Double.parseDouble(txtCosto.getText().trim());

            // Validaciones
            if (origen.equals(destino)) {
                JOptionPane.showMessageDialog(this,
                        "No se puede conectar una biblioteca consigo misma.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (grafo.estanConectadas(origen, destino)) {
                JOptionPane.showMessageDialog(this,
                        "Ya existe una conexión entre estas bibliotecas.",
                        "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (tiempo <= 0 || costo <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Tiempo y costo deben ser valores positivos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Agregar la conexión
            grafo.conectarBibliotecas(origen, destino, tiempo, costo);

            // Ejecutar callback
            if (onConexionAgregada != null) {
                onConexionAgregada.run();
            }

            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Tiempo debe ser un número entero y costo un número decimal.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al agregar conexión: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}