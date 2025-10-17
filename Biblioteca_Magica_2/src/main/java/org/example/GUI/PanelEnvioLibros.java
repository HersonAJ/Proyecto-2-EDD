package org.example.GUI;


import org.example.Modelos.Biblioteca;
import org.example.Modelos.Libro;
import org.example.Modelos.CoordinadorEnvios;
import org.example.Grafo.GrafoBibliotecas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelEnvioLibros extends JPanel {
    private CoordinadorEnvios coordinador;
    private GrafoBibliotecas grafo;

    // Componentes de UI
    private JComboBox<String> comboBibliotecasOrigen;
    private JComboBox<String> comboBibliotecasDestino;
    private JComboBox<String> comboLibros;
    private JComboBox<String> comboPrioridad;
    private JButton btnEnviar;
    private JTextArea areaLog;

    public PanelEnvioLibros(CoordinadorEnvios coordinador, GrafoBibliotecas grafo) {
        this.coordinador = coordinador;
        this.grafo = grafo;
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior - Formulario de envío
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Configurar Envío de Libro"));

        // Bibliotecas Origen
        panelFormulario.add(new JLabel("Biblioteca Origen:"));
        comboBibliotecasOrigen = new JComboBox<>();
        panelFormulario.add(comboBibliotecasOrigen);

        // Bibliotecas Destino
        panelFormulario.add(new JLabel("Biblioteca Destino:"));
        comboBibliotecasDestino = new JComboBox<>();
        panelFormulario.add(comboBibliotecasDestino);

        // Libros disponibles
        panelFormulario.add(new JLabel("Libro a Enviar:"));
        comboLibros = new JComboBox<>();
        panelFormulario.add(comboLibros);

        // Prioridad
        panelFormulario.add(new JLabel("Prioridad de Envío:"));
        comboPrioridad = new JComboBox<>(new String[]{"tiempo", "costo"});
        panelFormulario.add(comboPrioridad);

        // Botón de envío
        panelFormulario.add(new JLabel()); // Espacio vacío
        btnEnviar = new JButton("📦 Iniciar Envío");
        btnEnviar.addActionListener(this::realizarEnvio);
        panelFormulario.add(btnEnviar);

        // Panel central - Área de log
        areaLog = new JTextArea(15, 50);
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Log de Envíos"));

        // Panel inferior - Botones adicionales
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnLimpiarLog = new JButton("Limpiar Log");
        btnLimpiarLog.addActionListener(e -> areaLog.setText(""));

        JButton btnVerRuta = new JButton("Ver Ruta Calculada");
        btnVerRuta.addActionListener(this::mostrarRutaCalculada);

        panelBotones.add(btnLimpiarLog);
        panelBotones.add(btnVerRuta);

        // Agregar componentes al panel principal
        add(panelFormulario, BorderLayout.NORTH);
        add(scrollLog, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        // Cargar bibliotecas en los combos
        comboBibliotecasOrigen.removeAllItems();
        comboBibliotecasDestino.removeAllItems();

        var bibliotecas = grafo.getBibliotecas();
        var iterador = bibliotecas.iteradorValores();

        while (iterador.tieneSiguiente()) {
            Biblioteca bib = iterador.siguiente();
            comboBibliotecasOrigen.addItem(bib.getId() + " - " + bib.getNombre());
            comboBibliotecasDestino.addItem(bib.getId() + " - " + bib.getNombre());
        }

        cargarLibrosReales();
    }

    private void cargarLibrosReales() {
        comboLibros.removeAllItems();

        var bibliotecas = grafo.getBibliotecas();
        var iterador = bibliotecas.iteradorValores();

        boolean hayLibros = false;

        while (iterador.tieneSiguiente()) {
            Biblioteca bib = iterador.siguiente();

            if (bib.getCatalogo() != null && !bib.getCatalogo().estaVacio()) {
                for (var libro : bib.getCatalogo().obtenerTodosLosLibros()) {
                    comboLibros.addItem(libro.getTitulo() + " - " + libro.getIsbn() + " (en " + bib.getNombre() + ")");
                    hayLibros = true;
                }
            }
        }

        if (!hayLibros) {
            comboLibros.addItem("No hay libros disponibles en ninguna biblioteca");
        }
    }

    private void realizarEnvio(ActionEvent e) {
        try {
            // Validar selecciones
            if (comboBibliotecasOrigen.getSelectedItem() == null ||
                    comboBibliotecasDestino.getSelectedItem() == null ||
                    comboLibros.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this,
                        "Seleccione bibliotecas y libro para el envío",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Extraer IDs de bibliotecas
            String origenCompleto = (String) comboBibliotecasOrigen.getSelectedItem();
            String destinoCompleto = (String) comboBibliotecasDestino.getSelectedItem();
            String idOrigen = origenCompleto.split(" - ")[0];
            String idDestino = destinoCompleto.split(" - ")[0];

            // Validar que no sean la misma biblioteca
            if (idOrigen.equals(idDestino)) {
                JOptionPane.showMessageDialog(this,
                        "La biblioteca origen y destino deben ser diferentes",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String libroSeleccionado = (String) comboLibros.getSelectedItem();
            Libro libro = crearLibroDesdeSeleccion(libroSeleccionado);

            String prioridad = (String) comboPrioridad.getSelectedItem();

            // Realizar envío
            boolean exito = coordinador.iniciarEnvioLibro(libro, idOrigen, idDestino, prioridad);

            if (exito) {
                agregarLog("ENVÍO INICIADO: " + libro.getTitulo() +
                        " de " + idOrigen + " a " + idDestino +
                        " (Prioridad: " + prioridad + ")");

                JOptionPane.showMessageDialog(this,
                        "Envío iniciado correctamente. El libro está en proceso de envío.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                agregarLog("ERROR: No se pudo iniciar el envío de " + libro.getTitulo());

                JOptionPane.showMessageDialog(this,
                        "No se pudo iniciar el envío. Verifique la conexión entre bibliotecas.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            agregarLog("ERROR: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error al realizar el envío: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarRutaCalculada(ActionEvent e) {
        try {
            if (comboBibliotecasOrigen.getSelectedItem() == null ||
                    comboBibliotecasDestino.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this,
                        "Seleccione bibliotecas origen y destino",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String origenCompleto = (String) comboBibliotecasOrigen.getSelectedItem();
            String destinoCompleto = (String) comboBibliotecasDestino.getSelectedItem();
            String idOrigen = origenCompleto.split(" - ")[0];
            String idDestino = destinoCompleto.split(" - ")[0];
            String prioridad = (String) comboPrioridad.getSelectedItem();

            // Aquí se integraría con el método de cálculo de ruta del coordinador
            agregarLog("Calculando ruta de " + idOrigen + " a " + idDestino +
                    " (Prioridad: " + prioridad + ")");

            // Simulación - en implementación real usaría coordinador.calcularRutaOptima()
            if (grafo.estanConectadas(idOrigen, idDestino)) {
                agregarLog("Ruta directa: " + idOrigen + " → " + idDestino);
            } else {
                agregarLog("⚠No hay conexión directa. Buscando ruta alternativa...");
                // Lógica para encontrar ruta intermedia
            }

        } catch (Exception ex) {
            agregarLog("ERROR al calcular ruta: " + ex.getMessage());
        }
    }

    private Libro crearLibroDesdeSeleccion(String seleccion) {
        String[] partes = seleccion.split(" - ");
        String titulo = partes[0];
        String isbn = partes[1];

        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setIsbn(isbn);
        libro.setAutor("Autor Ejemplo");
        libro.setGenero("Genero Ejemplo");
        libro.setFecha("2000");
        libro.setCantidad(1);

        return libro;
    }

    private void agregarLog(String mensaje) {
        String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        areaLog.append("[" + timestamp + "] " + mensaje + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    // Método para actualizar cuando cambian las bibliotecas
    public void actualizarDatos() {
        cargarDatos();
        agregarLog("📚 Datos actualizados - Bibliotecas disponibles: " +
                grafo.getBibliotecas().size());
    }
}