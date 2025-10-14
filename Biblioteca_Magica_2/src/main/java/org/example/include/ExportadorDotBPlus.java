package org.example.include;

import org.example.BPlus.ArbolBPlus;
import org.example.BPlus.NodoBPlus;
import org.example.BPlus.NodoInterno;
import org.example.BPlus.NodoHoja;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ExportadorDotBPlus {

    // Genera el contenido DOT como string.
    public static String generar(ArbolBPlus arbol) {
        StringBuilder out = new StringBuilder();
        out.append("digraph arbol_bplus {\n");
        out.append("  node [shape=box, style=filled, fontname=\"Helvetica\", fontsize=10];\n");
        out.append("  edge [arrowhead=vee];\n\n");

        int[] idCounter = {0};
        NodoBPlus raiz = arbol.getRaiz();
        if (raiz != null) {
            dibujarNodo(raiz, out, idCounter);

            // Dibujar enlaces entre hojas (azules)
            dibujarEnlacesHojas(arbol, out);
        } else {
            out.append("  vacio [label=\"Árbol B+ vacío\", shape=plaintext];\n");
        }

        out.append("}\n");
        return out.toString();
    }

    // Genera el archivo DOT directamente.
    public static boolean generarArchivo(ArbolBPlus arbol, String ruta) {
        String dot = generar(arbol);
        try (PrintWriter f = new PrintWriter(new FileWriter(ruta))) {
            f.print(dot);
            return true;
        } catch (IOException e) {
            System.err.println("Error al generar archivo DOT B+: " + e.getMessage());
            return false;
        }
    }

    private static String etiquetaNodoInterno(NodoInterno nodo) {
        StringBuilder lbl = new StringBuilder();
        for (int i = 0; i < nodo.numClaves; ++i) {
            if (i > 0) lbl.append(" | ");
            lbl.append(nodo.claves[i]);
        }
        return lbl.toString();
    }

    private static String etiquetaNodoHoja(NodoHoja nodo) {
        StringBuilder lbl = new StringBuilder();
        for (int i = 0; i < nodo.numClaves; ++i) {
            if (i > 0) lbl.append(" | ");
            lbl.append(nodo.entradas[i].genero);
        }
        return lbl.toString();
    }

    // Dibuja el subárbol
    private static int dibujarNodo(NodoBPlus nodo, StringBuilder out, int[] idCounter) {
        if (nodo == null) return -1;

        int myId = idCounter[0]++;

        if (!nodo.esHoja) {
            NodoInterno interno = (NodoInterno) nodo;
            out.append("  n").append(myId).append(" [label=\"");
            out.append(etiquetaNodoInterno(interno));
            out.append("\", fillcolor=lightblue];\n");

            for (int i = 0; i <= interno.numClaves; ++i) {
                if (interno.hijos[i] != null) {
                    int childId = dibujarNodo(interno.hijos[i], out, idCounter);
                    if (childId >= 0) {
                        out.append("  n").append(myId).append(" -> n").append(childId).append(";\n");
                    }
                }
            }
        } else {
            NodoHoja hoja = (NodoHoja) nodo;
            out.append("  n").append(myId).append(" [label=\"");
            out.append(etiquetaNodoHoja(hoja));
            out.append("\", fillcolor=lightgreen];\n");
        }
        return myId;
    }

    // Dibuja los enlaces entre hojas (para recorrido secuencial)
    private static void dibujarEnlacesHojas(ArbolBPlus arbol, StringBuilder out) {
        out.append("  // Enlaces entre hojas\n");
        out.append("  edge [color=blue, constraint=false, style=dashed];\n");

        NodoHoja actual = arbol.getPrimeraHoja();
        List<Integer> idsHojas = new ArrayList<>();
        int currentId = 0;

        // Primera pasada: recolectar IDs de hojas
        NodoHoja temp = actual;
        while (temp != null) {
            idsHojas.add(currentId++);
            temp = temp.siguiente;
        }

        // Segunda pasada: dibujar enlaces
        actual = arbol.getPrimeraHoja();
        currentId = 0;
        while (actual != null && actual.siguiente != null) {
            out.append("  n").append(idsHojas.get(currentId))
                    .append(" -> n").append(idsHojas.get(currentId + 1)).append(";\n");
            actual = actual.siguiente;
            currentId++;
        }

        out.append("  edge [color=black, constraint=true, style=solid];\n");
    }
}
