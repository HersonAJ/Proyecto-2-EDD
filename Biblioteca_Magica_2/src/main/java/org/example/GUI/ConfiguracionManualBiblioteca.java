package org.example.GUI;

import org.example.Grafo.Arista;
import org.example.Grafo.GrafoBibliotecas;
import org.example.Modelos.Biblioteca;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class ConfiguracionManualBiblioteca extends JPanel {
    private GrafoBibliotecas grafo;

    private JComboBox<String> comboBibliotecas;
    private JTextField txtTiempoIngreso;
    private JTextField txtTiempoTraspaso;
    private JTextField txtTiempoSalida;
    private JButton btnCargar;
    private JButton btnGuardar;
    private JButton btnRestablecer;
    private JLabel lblEstado;
    private JPanel panelConexiones;
    private JScrollPane scrollConexiones;
    private java.util.List<JCheckBox> checkboxesConexiones;

    private Biblioteca bibliotecaActual;
    private boolean hayCambios = false;

    public ConfiguracionManualBiblioteca(GrafoBibliotecas grafo) {
        this.grafo = grafo;
        initComponents();
        cargarBibliotecas();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel superior - selector de biblioteca
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Seleccionar Biblioteca"));

        JLabel lblBiblioteca = new JLabel("Biblioteca:");
        lblBiblioteca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelSuperior.add(lblBiblioteca);

        comboBibliotecas = new JComboBox<>();
        comboBibliotecas.setPreferredSize(new Dimension(250, 26));
        comboBibliotecas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBibliotecas.addActionListener(e -> seleccionarBiblioteca());
        panelSuperior.add(comboBibliotecas);

        btnCargar = new JButton("Cargar Configuración");
        btnCargar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCargar.setFocusable(false);
        panelSuperior.add(btnCargar);

        // Panel central - configuración de tiempos
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBorder(BorderFactory.createTitledBorder("Configuración de Tiempos (milisegundos)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblIngreso = new JLabel("Tiempo Ingreso:");
        JLabel lblTraspaso = new JLabel("Tiempo Traspaso:");
        JLabel lblSalida = new JLabel("Tiempo Salida:");
        JLabel lblMinimo = new JLabel("Mínimo: 1000 ms (1 segundo)");
        lblMinimo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblMinimo.setForeground(UIManager.getColor("Label.foreground"));

        txtTiempoIngreso = crearCampoTiempo();
        txtTiempoTraspaso = crearCampoTiempo();
        txtTiempoSalida = crearCampoTiempo();

        Dimension campoDim = new Dimension(120, 26);
        txtTiempoIngreso.setPreferredSize(campoDim);
        txtTiempoTraspaso.setPreferredSize(campoDim);
        txtTiempoSalida.setPreferredSize(campoDim);

        gbc.gridx = 0; gbc.gridy = 0;
        panelCentral.add(lblIngreso, gbc);
        gbc.gridx = 1;
        panelCentral.add(txtTiempoIngreso, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelCentral.add(lblTraspaso, gbc);
        gbc.gridx = 1;
        panelCentral.add(txtTiempoTraspaso, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelCentral.add(lblSalida, gbc);
        gbc.gridx = 1;
        panelCentral.add(txtTiempoSalida, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panelCentral.add(lblMinimo, gbc);

        // Panel inferior - botones y estado
        JPanel panelInferior = new JPanel(new BorderLayout(5, 5));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        btnGuardar = new JButton("💾 Guardar Cambios");
        btnGuardar.setEnabled(false);
        btnGuardar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnGuardar.setFocusable(false);
        btnGuardar.setPreferredSize(new Dimension(160, 30));
        panelBotones.add(btnGuardar);

        btnRestablecer = new JButton("🔄 Restablecer");
        btnRestablecer.setEnabled(false);
        btnRestablecer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRestablecer.setFocusable(false);
        btnRestablecer.setPreferredSize(new Dimension(130, 30));
        panelBotones.add(btnRestablecer);

        lblEstado = new JLabel("Seleccione una biblioteca para comenzar", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEstado.setForeground(Color.BLUE);
        lblEstado.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));

        panelInferior.add(panelBotones, BorderLayout.CENTER);
        panelInferior.add(lblEstado, BorderLayout.SOUTH);

        // Agregar componentes al panel principal
        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        // Listeners
        btnCargar.addActionListener(e -> cargarConfiguracionActual());
        btnGuardar.addActionListener(e -> guardarCambios());
        btnRestablecer.addActionListener(e -> restablecerCampos());

        //panel para las conexiones
        panelConexiones = new JPanel(new BorderLayout());
        panelConexiones.setBorder(BorderFactory.createTitledBorder("Conexiones Salientes"));
        panelConexiones.setVisible(false); // Inicialmente oculto

        JLabel lblInfoConexiones = new JLabel("Seleccione conexiones a eliminar:");
        panelConexiones.add(lblInfoConexiones, BorderLayout.NORTH);

        JPanel panelListaConexiones = new JPanel(new GridLayout(0, 1, 3, 3));
        scrollConexiones = new JScrollPane(panelListaConexiones);
        scrollConexiones.setPreferredSize(new Dimension(400, 120));
        panelConexiones.add(scrollConexiones, BorderLayout.CENTER);

        JButton btnEliminarConexiones = new JButton("🗑️ Eliminar Conexiones Seleccionadas");
        btnEliminarConexiones.addActionListener(e -> eliminarConexionesSeleccionadas());
        panelConexiones.add(btnEliminarConexiones, BorderLayout.SOUTH);

        // Agregar componentes al panel principal en orden
        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelConexiones, BorderLayout.EAST);
        add(panelInferior, BorderLayout.SOUTH);


        setCamposHabilitados(false);
    }

    private void eliminarConexionesSeleccionadas(java.util.List<JCheckBox> checkboxes, JDialog dialog) {
        int eliminadas = 0;

        for (JCheckBox checkBox : checkboxes) {
            if (checkBox.isSelected()) {
                // Extraer ID destino del texto del checkbox
                String texto = checkBox.getText();
                String idDestino = texto.split(" → ")[1].split(" ")[0];

                if (grafo.eliminarConexion(bibliotecaActual.getId(), idDestino)) {
                    eliminadas++;
                }
            }
        }

        if (eliminadas > 0) {
            JOptionPane.showMessageDialog(dialog,
                    "Se eliminaron " + eliminadas + " conexiones exitosamente.",
                    "Conexiones Eliminadas",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(dialog,
                    "No se seleccionaron conexiones para eliminar.",
                    "Sin Cambios",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        dialog.dispose();
    }

    private JTextField crearCampoTiempo() {
        JTextField campo = new JTextField();
        campo.setEnabled(false);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setHorizontalAlignment(JTextField.RIGHT);

        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                verificarCambios();
            }
        });
        return campo;
    }


private void cargarBibliotecas() {
        comboBibliotecas.removeAllItems();

        var bibliotecas = grafo.getBibliotecas();
        var iterador = bibliotecas.iteradorValores();

        boolean hayBibliotecas = false;

        while (iterador.tieneSiguiente()) {
            Biblioteca bib = iterador.siguiente();
            comboBibliotecas.addItem(bib.getId() + " - " + bib.getNombre());
            hayBibliotecas = true;
        }

        if (!hayBibliotecas) {
            comboBibliotecas.addItem("No hay bibliotecas disponibles");
            btnCargar.setEnabled(false);
        }
    }

    private void seleccionarBiblioteca() {
        String seleccion = (String) comboBibliotecas.getSelectedItem();
        if (seleccion == null || seleccion.equals("No hay bibliotecas disponibles")) {
            setCamposHabilitados(false);
            panelConexiones.setVisible(false);
            btnCargar.setEnabled(false);
            return;
        }

        btnCargar.setEnabled(true);
        panelConexiones.setVisible(false); // Ocultar hasta que se cargue
    }

    private void cargarConfiguracionActual() {
        String seleccion = (String) comboBibliotecas.getSelectedItem();
        if (seleccion == null) return;

        String idBiblioteca = seleccion.split(" - ")[0];
        bibliotecaActual = grafo.getBiblioteca(idBiblioteca);

        if (bibliotecaActual != null) {
            // Cargar tiempos actuales
            txtTiempoIngreso.setText(String.valueOf(bibliotecaActual.getColaIngreso().getTiempoProcesamiento()));
            txtTiempoTraspaso.setText(String.valueOf(bibliotecaActual.getColaTraspaso().getTiempoProcesamiento()));
            txtTiempoSalida.setText(String.valueOf(bibliotecaActual.getColaSalida().getTiempoProcesamiento()));

            // CARGAR CONEXIONES
            cargarConexionesBiblioteca();

            setCamposHabilitados(true);
            hayCambios = false;
            actualizarEstadoBotones();

            lblEstado.setText("Configuración cargada: " + bibliotecaActual.getNombre());
            lblEstado.setForeground(Color.BLUE);
        }
    }

    private void cargarConexionesBiblioteca() {
        // Limpiar conexiones anteriores
        JPanel panelLista = (JPanel) scrollConexiones.getViewport().getView();
        panelLista.removeAll();
        checkboxesConexiones = new ArrayList<>();

        // Obtener conexiones actuales
        java.util.List<Arista> conexiones = grafo.getConexionesSalientesList(bibliotecaActual.getId());

        if (conexiones.isEmpty()) {
            panelLista.add(new JLabel("No hay conexiones salientes"));
            panelConexiones.setVisible(false);
        } else {
            for (Arista conexion : conexiones) {
                JCheckBox checkBox = new JCheckBox(
                        String.format("%s → %s [T:%ds, C:%.1f]",
                                conexion.getIdOrigen(),
                                conexion.getIdDestino(),
                                conexion.getTiempo(),
                                conexion.getCosto())
                );
                checkboxesConexiones.add(checkBox);
                panelLista.add(checkBox);
            }
            panelConexiones.setVisible(true);
        }

        panelLista.revalidate();
        panelLista.repaint();
    }

    private void eliminarConexionesSeleccionadas() {
        if (bibliotecaActual == null || checkboxesConexiones == null) return;

        int eliminadas = 0;
        java.util.List<String> destinosAEliminar = new ArrayList<>();

        // Primero recolectar las seleccionadas
        for (JCheckBox checkBox : checkboxesConexiones) {
            if (checkBox.isSelected()) {
                String texto = checkBox.getText();
                String idDestino = texto.split(" → ")[1].split(" ")[0];
                destinosAEliminar.add(idDestino);
            }
        }

        // Luego eliminarlas
        for (String idDestino : destinosAEliminar) {
            if (grafo.eliminarConexion(bibliotecaActual.getId(), idDestino)) {
                eliminadas++;
            }
        }

        if (eliminadas > 0) {
            // Recargar la lista de conexiones
            cargarConexionesBiblioteca();
            lblEstado.setText("✅ " + eliminadas + " conexiones eliminadas");
            lblEstado.setForeground(new Color(0, 128, 0));
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se seleccionaron conexiones para eliminar.",
                    "Sin Cambios",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void setCamposHabilitados(boolean habilitado) {
        txtTiempoIngreso.setEnabled(habilitado);
        txtTiempoTraspaso.setEnabled(habilitado);
        txtTiempoSalida.setEnabled(habilitado);
    }

    private void verificarCambios() {
        if (bibliotecaActual == null) return;

        boolean cambiosDetectados = !txtTiempoIngreso.getText().equals(String.valueOf(bibliotecaActual.getColaIngreso().getTiempoProcesamiento())) ||
                !txtTiempoTraspaso.getText().equals(String.valueOf(bibliotecaActual.getColaTraspaso().getTiempoProcesamiento())) ||
                !txtTiempoSalida.getText().equals(String.valueOf(bibliotecaActual.getColaSalida().getTiempoProcesamiento()));

        hayCambios = cambiosDetectados;
        actualizarEstadoBotones();

        if (hayCambios) {
            lblEstado.setText("⚠️ Cambios pendientes por guardar");
            lblEstado.setForeground(Color.ORANGE);
        } else {
            lblEstado.setText("Configuración actualizada");
            lblEstado.setForeground(Color.BLUE);
        }
    }

    private void actualizarEstadoBotones() {
        btnGuardar.setEnabled(hayCambios);
        btnRestablecer.setEnabled(hayCambios);
    }

    private void guardarCambios() {
        if (bibliotecaActual == null || !hayCambios) return;

        try {
            // Validar y obtener nuevos tiempos
            int nuevoIngreso = validarTiempo(txtTiempoIngreso.getText());
            int nuevoTraspaso = validarTiempo(txtTiempoTraspaso.getText());
            int nuevoSalida = validarTiempo(txtTiempoSalida.getText());

            // Aplicar cambios
            bibliotecaActual.getColaIngreso().setTiempoProcesamiento(nuevoIngreso);
            bibliotecaActual.getColaTraspaso().setTiempoProcesamiento(nuevoTraspaso);
            bibliotecaActual.getColaSalida().setTiempoProcesamiento(nuevoSalida);

            hayCambios = false;
            actualizarEstadoBotones();

            lblEstado.setText("✅ Cambios guardados exitosamente");
            lblEstado.setForeground(new Color(0, 128, 0)); // Verde

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Error: Los tiempos deben ser números válidos",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int validarTiempo(String texto) {
        if (texto.trim().isEmpty()) {
            throw new NumberFormatException("Campo vacío");
        }

        int tiempo = Integer.parseInt(texto.trim());

        if (tiempo < 1000) {
            throw new IllegalArgumentException("El tiempo debe ser al menos 1000 ms (1 segundo)");
        }

        return tiempo;
    }

    private void restablecerCampos() {
        if (bibliotecaActual != null) {
            cargarConfiguracionActual(); // Recarga los valores originales
        }
    }

    // Método para actualizar cuando se agregan nuevas bibliotecas
    public void actualizarBibliotecas() {
        cargarBibliotecas();
        bibliotecaActual = null;
        setCamposHabilitados(false);
        hayCambios = false;
        actualizarEstadoBotones();
        lblEstado.setText("Lista de bibliotecas actualizada");
        lblEstado.setForeground(Color.BLUE);
    }
}