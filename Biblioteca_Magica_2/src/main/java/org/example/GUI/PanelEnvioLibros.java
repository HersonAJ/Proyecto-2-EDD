package org.example.GUI;


import org.example.Grafo.RutaDijkstra;
import org.example.Modelos.Biblioteca;
import org.example.Modelos.Libro;
import org.example.Modelos.CoordinadorEnvios;
import org.example.Grafo.GrafoBibliotecas;
import java.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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
        this.coordinador.agregarListener(mensaje -> SwingUtilities.invokeLater(() -> agregarLog(mensaje)));
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
        comboBibliotecasOrigen.addActionListener(e -> cargarLibrosReales());
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

        // Obtener la biblioteca origen seleccionada (si hay una)
        String origenSeleccionado = (String) comboBibliotecasOrigen.getSelectedItem();
        String idOrigen = null;

        if (origenSeleccionado != null && !origenSeleccionado.isEmpty()) {
            idOrigen = origenSeleccionado.split(" - ")[0];
        }

        var bibliotecas = grafo.getBibliotecas();
        var iterador = bibliotecas.iteradorValores();

        boolean hayLibros = false;

        while (iterador.tieneSiguiente()) {
            Biblioteca bib = iterador.siguiente();
            if (idOrigen != null && !bib.getId().equals(idOrigen)) {
                continue;
            }

            if (bib.getCatalogo() != null && !bib.getCatalogo().estaVacio()) {
                for (var libro : bib.getCatalogo().obtenerTodosLosLibros()) {
                    comboLibros.addItem(libro.getTitulo() + " - " + libro.getIsbn() + " (en " + bib.getNombre() + ")");
                    hayLibros = true;
                }
            }
        }

        if (!hayLibros) {
            if (idOrigen != null) {
                comboLibros.addItem("No hay libros disponibles en " + idOrigen);
            } else {
                comboLibros.addItem("Seleccione una biblioteca origen primero");
            }
        }
    }

    private void realizarEnvio(ActionEvent e) {
        try {
            if (comboBibliotecasOrigen.getSelectedItem() == null ||
                    comboBibliotecasDestino.getSelectedItem() == null ||
                    comboLibros.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this,
                        "Seleccione bibliotecas y libro para el envío",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String origenCompleto = (String) comboBibliotecasOrigen.getSelectedItem();
            String destinoCompleto = (String) comboBibliotecasDestino.getSelectedItem();
            String idOrigen = origenCompleto.split(" - ")[0];
            String idDestino = destinoCompleto.split(" - ")[0];

            if (idOrigen.equals(idDestino)) {
                JOptionPane.showMessageDialog(this,
                        "La biblioteca origen y destino deben ser diferentes",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String libroSeleccionado = (String) comboLibros.getSelectedItem();
            Libro libro = obtenerLibroRealDesdeSeleccion(libroSeleccionado, idOrigen);

            String prioridad = (String) comboPrioridad.getSelectedItem();

            boolean exito = coordinador.iniciarEnvioLibro(libro, idOrigen, idDestino, prioridad);

            if (exito) {
                agregarLog("📦 Envío iniciado: " + libro.getTitulo() +
                        " de " + idOrigen + " a " + idDestino +
                        " (Prioridad: " + prioridad + ")");
                JOptionPane.showMessageDialog(this,
                        "El envío ha comenzado. El libro se moverá automáticamente según los tiempos de las colas.",
                        "Envío en curso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                agregarLog("❌ Error: no se pudo iniciar el envío de " + libro.getTitulo());
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

            agregarLog("Calculando ruta de " + idOrigen + " a " + idDestino +
                    " (Prioridad: " + prioridad + ")");

            // USAR DIJKSTRA REAL
            RutaDijkstra.Criterio criterio = prioridad.equals("costo")
                    ? RutaDijkstra.Criterio.COSTO
                    : RutaDijkstra.Criterio.TIEMPO;

            List<String> ruta = RutaDijkstra.calcularRuta(grafo, idOrigen, idDestino, criterio);

            if (ruta != null && !ruta.isEmpty()) {
                // Formatear ruta bonita
                String rutaFormateada = String.join(" → ", ruta);
                String unidad = (criterio == RutaDijkstra.Criterio.TIEMPO) ? "segundos" : "unidades de costo";

                agregarLog("✅ Ruta óptima por " + prioridad.toLowerCase() + ": " + rutaFormateada);

                // Mostrar detalles de la ruta
                if (ruta.size() > 2) {
                    agregarLog("   ↳ Ruta con " + (ruta.size() - 1) + " saltos");
                } else {
                    agregarLog("   ↳ Ruta directa");
                }
            } else {
                agregarLog("❌ No se encontró ruta posible entre " + idOrigen + " y " + idDestino);
            }

        } catch (Exception ex) {
            agregarLog("ERROR al calcular ruta: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private Libro obtenerLibroRealDesdeSeleccion(String seleccion, String idBibliotecaOrigen) {
        String[] partes = seleccion.split(" - ");
        String tituloBuscado = partes[0];
        String isbnBuscado = partes[1].split(" ")[0];

        System.out.println("🔍 Buscando libro: '" + tituloBuscado + "' en biblioteca " + idBibliotecaOrigen);

        // Buscar específicamente en la biblioteca origen
        Biblioteca bibliotecaOrigen = grafo.getBiblioteca(idBibliotecaOrigen);

        if (bibliotecaOrigen != null && bibliotecaOrigen.getCatalogo() != null) {
            for (Libro libro : bibliotecaOrigen.getCatalogo().obtenerTodosLosLibros()) {
                if (libro.getTitulo().equals(tituloBuscado) &&
                        libro.getIsbn().equals(isbnBuscado)) {
                    System.out.println("✅ Libro REAL encontrado en origen");
                    return libro;
                }
            }
        }

        throw new RuntimeException("No se encontró el libro '" + tituloBuscado + "' en la biblioteca " + idBibliotecaOrigen +
                ". El libro puede haber sido movido o eliminado.");
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