package org.example.GUI.Vistas;

import org.example.AVL.ArbolAVL;
import org.example.AVL_Auxiliar.IndiceISBN;
import org.example.Catalogo.Catalogo;
import org.example.Modelos.Libro;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PruebaRendimiento extends JPanel {
    private ArbolAVL arbolTitulos;
    private IndiceISBN indiceISBN;
    private Catalogo catalogo;

    // Componentes de UI
    private JComboBox<String> comboBusqueda;
    private JLabel labelInstruccion;
    private JTextField editBusqueda;
    private JButton btnComparar;
    private JTextPane textResultados;
    private JScrollPane scrollResultados;
    private StyledDocument doc;

    public PruebaRendimiento(ArbolAVL arbolTitulos, IndiceISBN indiceISBN, Catalogo catalogo) {
        this.arbolTitulos = arbolTitulos;
        this.indiceISBN = indiceISBN;
        this.catalogo = catalogo;

        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Panel de controles
        JPanel panelControles = new JPanel();
        panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));
        panelControles.setBorder(BorderFactory.createTitledBorder("Configuración de Prueba"));

        // Tipo de búsqueda
        JPanel panelTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTipo.add(new JLabel("Tipo de búsqueda:"));

        comboBusqueda = new JComboBox<>(new String[]{"Por Título", "Por ISBN"});
        comboBusqueda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onTipoBusquedaChanged(comboBusqueda.getSelectedIndex());
            }
        });
        panelTipo.add(comboBusqueda);
        panelControles.add(panelTipo);

        // Campo de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelInstruccion = new JLabel("Título a buscar:");
        panelBusqueda.add(labelInstruccion);

        editBusqueda = new JTextField(20);
        editBusqueda.setToolTipText("Ingrese el título...");
        panelBusqueda.add(editBusqueda);
        panelControles.add(panelBusqueda);

        // Botón
        btnComparar = new JButton("Comparar Rendimiento");
        btnComparar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCompararClicked();
            }
        });
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBoton.add(btnComparar);
        panelControles.add(panelBoton);

        add(panelControles, BorderLayout.NORTH);

        // Área de resultados con JTextPane para colores
        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBorder(BorderFactory.createTitledBorder("Resultados"));

        textResultados = new JTextPane();
        textResultados.setEditable(false);
        textResultados.setFont(new Font("Monospaced", Font.PLAIN, 12));
        doc = textResultados.getStyledDocument();

        scrollResultados = new JScrollPane(textResultados);
        panelResultados.add(scrollResultados, BorderLayout.CENTER);

        add(panelResultados, BorderLayout.CENTER);
    }

    private void onTipoBusquedaChanged(int index) {
        if (index == 0) { // Título
            labelInstruccion.setText("Título a buscar:");
            editBusqueda.setToolTipText("Ingrese el título...");
        } else { // ISBN
            labelInstruccion.setText("ISBN a buscar:");
            editBusqueda.setToolTipText("Ingrese el ISBN...");
        }
    }

    private void onCompararClicked() {
        String texto = editBusqueda.getText().trim();
        if (texto.isEmpty()) {
            appendResultado("❌ Error: Ingrese un valor para buscar", Color.RED);
            return;
        }

        int tipo = comboBusqueda.getSelectedIndex();
        clearResults(); // Limpiar resultados anteriores

        appendResultado("🔍 INICIANDO COMPARACIÓN DE RENDIMIENTO", Color.BLUE);
        appendResultado("========================================", Color.BLUE);

        if (tipo == 0) {
            ejecutarComparacionTitulo(texto);
        } else {
            ejecutarComparacionISBN(texto);
        }
    }

    private void clearResults() {
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private long medirTiempo(Runnable funcion) {
        long inicio = System.nanoTime();
        funcion.run();
        long fin = System.nanoTime();
        return (fin - inicio) / 1000; // Convertir a microsegundos
    }

    private void ejecutarComparacionTitulo(String titulo) {
        appendResultado("📚 COMPARACIÓN: BÚSQUEDA POR TÍTULO", new Color(0, 0, 139)); // darkblue
        appendResultado("Buscando: \"" + titulo + "\"", Color.BLACK);
        appendResultado("", Color.BLACK);

        // Búsqueda secuencial
        Libro resultadoSecuencial = null;
        final Libro[] tempSecuencial = new Libro[1];
        long tiempoSecuencial = medirTiempo(() -> {
            tempSecuencial[0] = catalogo.buscarTituloSecuencial(titulo);
        });
        resultadoSecuencial = tempSecuencial[0];

        // Búsqueda binaria (AVL)
        Libro resultadoBinario = null;
        final Libro[] tempBinario = new Libro[1];
        long tiempoBinario = medirTiempo(() -> {
            tempBinario[0] = arbolTitulos.buscarPorTitulo(titulo);
        });
        resultadoBinario = tempBinario[0];

        // Mostrar resultados
        appendResultado("📊 RESULTADOS:", new Color(0, 100, 0)); // darkgreen
        appendResultado("• Búsqueda Secuencial (Lista): " + tiempoSecuencial + " μs",
                resultadoSecuencial != null ? Color.GREEN : Color.RED);
        appendResultado("• Búsqueda Binaria (AVL): " + tiempoBinario + " μs",
                resultadoBinario != null ? Color.GREEN : Color.RED);

        if (tiempoSecuencial > 0) {
            double mejora = ((double)tiempoSecuencial - tiempoBinario) / tiempoSecuencial * 100;
            String mejoraStr = String.format("%.2f", Math.abs(mejora));
            Color colorMejora = mejora > 0 ? Color.ORANGE : Color.RED;
            String direccion = mejora > 0 ? "más rápido" : "más lento";
            appendResultado("• Mejora: " + mejoraStr + "% " + direccion, colorMejora);
        }

        appendResultado("", Color.BLACK);
        appendResultado("----------------------------------------", Color.GRAY);
    }

    private void ejecutarComparacionISBN(String isbn) {
        appendResultado("🔖 COMPARACIÓN: BÚSQUEDA POR ISBN", new Color(0, 0, 139)); // darkblue
        appendResultado("Buscando: \"" + isbn + "\"", Color.BLACK);
        appendResultado("", Color.BLACK);

        // Búsqueda secuencial
        Libro resultadoSecuencial = null;
        final Libro[] tempSecuencial = new Libro[1];
        long tiempoSecuencial = medirTiempo(() -> {
            tempSecuencial[0] = catalogo.buscarISBNSecuencial(isbn);
        });
        resultadoSecuencial = tempSecuencial[0];

        // Búsqueda binaria (IndiceISBN)
        Libro resultadoBinario = null;
        final Libro[] tempBinario = new Libro[1];
        long tiempoBinario = medirTiempo(() -> {
            tempBinario[0] = indiceISBN.buscar(isbn);
        });
        resultadoBinario = tempBinario[0];

        // Mostrar resultados
        appendResultado("📊 RESULTADOS:", new Color(0, 100, 0)); // darkgreen
        appendResultado("• Búsqueda Secuencial (Lista): " + tiempoSecuencial + " μs",
                resultadoSecuencial != null ? Color.GREEN : Color.RED);
        appendResultado("• Búsqueda Binaria (AVL ISBN): " + tiempoBinario + " μs",
                resultadoBinario != null ? Color.GREEN : Color.RED);

        if (tiempoSecuencial > 0) {
            double mejora = ((double)tiempoSecuencial - tiempoBinario) / tiempoSecuencial * 100;
            String mejoraStr = String.format("%.2f", Math.abs(mejora));
            Color colorMejora = mejora > 0 ? Color.ORANGE : Color.RED;
            String direccion = mejora > 0 ? "más rápido" : "más lento";
            appendResultado("• Mejora: " + mejoraStr + "% " + direccion, colorMejora);
        }

        appendResultado("", Color.BLACK);
        appendResultado("----------------------------------------", Color.GRAY);
    }

    private void appendResultado(String texto, Color color) {
        try {
            Style style = textResultados.addStyle("Style", null);
            StyleConstants.setForeground(style, color);
            StyleConstants.setFontFamily(style, "Monospaced");
            StyleConstants.setFontSize(style, 12);

            doc.insertString(doc.getLength(), texto + "\n", style);

            // Auto-scroll al final
            textResultados.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}