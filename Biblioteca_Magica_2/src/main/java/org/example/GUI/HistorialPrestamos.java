package org.example.GUI;

import org.example.Modelos.Biblioteca;
import org.example.Modelos.RegistroPrestamo;
import org.example.Grafo.GrafoBibliotecas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;

public class HistorialPrestamos extends JPanel {
    private Biblioteca biblioteca;
    private GrafoBibliotecas grafo;
    private JTable tablaPrestamos;
    private JButton btnDeshacer;
    private JLabel lblEstado;

    public HistorialPrestamos(Biblioteca biblioteca, GrafoBibliotecas grafo) {
        this.biblioteca = biblioteca;
        this.grafo = grafo;
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Título
        JLabel titulo = new JLabel("📚 HISTORIAL DE PRÉSTAMOS - " + biblioteca.getNombre());
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // Panel central - Tabla de préstamos
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createTitledBorder("Préstamos Realizados (Último → Primero)"));

        // Configurar tabla
        configurarTabla();
        JScrollPane scrollTabla = new JScrollPane(tablaPrestamos);
        panelCentral.add(scrollTabla, BorderLayout.CENTER);

        // Panel inferior - Botones y estado
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));

        // Botón deshacer
        btnDeshacer = new JButton("Deshacer Último Préstamo");
        btnDeshacer.setBackground(new Color(220, 80, 60));
        btnDeshacer.setForeground(Color.WHITE);
        btnDeshacer.setFont(new Font("Arial", Font.BOLD, 12));
        btnDeshacer.setFocusPainted(false);
        btnDeshacer.setEnabled(false);

        // Etiqueta de estado
        lblEstado = new JLabel("Cargando historial...");
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.add(btnDeshacer);

        panelInferior.add(panelBotones, BorderLayout.WEST);
        panelInferior.add(lblEstado, BorderLayout.CENTER);

        // Agregar componentes principales
        add(panelCentral, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        // Listeners
        btnDeshacer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deshacerUltimoPrestamo();
            }
        });
    }

    private void configurarTabla() {
        String[] columnas = {"#", "Título", "ISBN", "Biblioteca Destino", "Fecha Préstamo", "Estado"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable
            }
        };

        tablaPrestamos = new JTable(model);
        tablaPrestamos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPrestamos.getTableHeader().setReorderingAllowed(false);

        // Ajustar anchos de columnas
        tablaPrestamos.getColumnModel().getColumn(0).setPreferredWidth(40);  // #
        tablaPrestamos.getColumnModel().getColumn(1).setPreferredWidth(200); // Título
        tablaPrestamos.getColumnModel().getColumn(2).setPreferredWidth(120); // ISBN
        tablaPrestamos.getColumnModel().getColumn(3).setPreferredWidth(150); // Destino
        tablaPrestamos.getColumnModel().getColumn(4).setPreferredWidth(120); // Fecha
        tablaPrestamos.getColumnModel().getColumn(5).setPreferredWidth(100); // Estado
    }

    public void cargarDatos() {
        DefaultTableModel model = (DefaultTableModel) tablaPrestamos.getModel();
        model.setRowCount(0); // Limpiar tabla

        java.util.List<RegistroPrestamo> prestamos = obtenerPrestamosDePila();

        if (prestamos.isEmpty()) {
            model.addRow(new Object[]{"-", "No hay préstamos registrados", "", "", "", ""});
            lblEstado.setText("No hay préstamos para deshacer");
            btnDeshacer.setEnabled(false);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        int numero = 1;

        for (RegistroPrestamo prestamo : prestamos) {
            String estado = (numero == 1) ? "⏳ PRÓXIMO A DESHACER" : "📋 EN ESPERA";

            model.addRow(new Object[]{
                    numero,
                    prestamo.getTitulo(),
                    prestamo.getIsbn(),
                    prestamo.getBibliotecaDestino(),
                    sdf.format(prestamo.getFechaPrestamo()),
                    estado
            });
            numero++;
        }

        // Actualizar estado y botón
        lblEstado.setText("Préstamos en pila: " + prestamos.size() + " - Solo se puede deshacer el más reciente");
        btnDeshacer.setEnabled(!prestamos.isEmpty());
    }

    private List<RegistroPrestamo> obtenerPrestamosDePila() {
            try {
                // Ahora usamos el método real de la biblioteca
                return biblioteca.obtenerPrestamos();
            } catch (Exception e) {
                System.err.println("Error obteniendo préstamos: " + e.getMessage());
                lblEstado.setText("Error cargando préstamos");
                return new java.util.ArrayList<>();
            }
        }


    private void deshacerUltimoPrestamo() {
        if (!biblioteca.hayPrestamosParaDeshacer()) {
            JOptionPane.showMessageDialog(this,
                    "No hay préstamos para deshacer",
                    "Pila Vacía",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmación
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de deshacer el último préstamo?\n\n" +
                        "Esta acción:\n" +
                        "• Cambiará el estado del libro a 'Disponible' en esta biblioteca\n" +
                        "• Eliminará el libro de la biblioteca destino\n" +
                        "• No se puede deshacer esta operación",
                "Confirmar Devolución",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean exito = biblioteca.deshacerUltimoPrestamo(grafo);

            if (exito) {
                JOptionPane.showMessageDialog(this,
                        "Préstamo deshecho exitosamente\n" +
                                "El libro ha sido devuelto a esta biblioteca",
                        "Devolución Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);

                // Recargar datos
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al deshacer el préstamo\n" +
                                "El libro puede no existir o tener estado incorrecto",
                        "Error en Devolución",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Método para actualizar desde fuera
    public void actualizarVista() {
        cargarDatos();
    }
}