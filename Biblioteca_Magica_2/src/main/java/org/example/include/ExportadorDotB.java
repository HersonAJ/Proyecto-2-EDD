package org.example.include;

import org.example.Estructuras.B.ArbolB;
import org.example.Estructuras.B.NodoB;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ExportadorDotB {

    // Genera el contenido DOT como string.
    public static String generar(ArbolB arbol) {
        StringBuilder out = new StringBuilder();
        out.append("digraph arbol_b {\n");
        out.append("  node [shape=box, style=filled, fontname=\"Helvetica\", fontsize=10];\n\n");

        // Usar un objeto contenedor para hacer el idCounter mutable
        int[] idCounter = {0};
        NodoB raiz = arbol.getRaiz();
        if (raiz != null) {
            dibujarNodo(raiz, out, idCounter);
        } else {
            out.append("  vacio [label=\"Árbol B vacío\", shape=plaintext];\n");
        }

        out.append("}\n");
        return out.toString();
    }

    // Genera el archivo DOT directamente.
    public static boolean generarArchivo(ArbolB arbol, String ruta) {
        String dot = generar(arbol);
        try (PrintWriter f = new PrintWriter(new FileWriter(ruta))) {
            f.print(dot);
            return true;
        } catch (IOException e) {
            System.err.println("Error al generar archivo DOT B: " + e.getMessage());
            return false;
        }
    }

    // Construye la etiqueta de un nodo con sus claves separadas por coma.
    private static String etiquetaNodo(NodoB nodo) {
        StringBuilder lbl = new StringBuilder();
        for (int i = 0; i < nodo.numClaves; ++i) {
            if (i > 0) lbl.append(", ");
            if (nodo.claves[i] != null) {
                lbl.append(nodo.claves[i].fecha); // solo mostramos la fecha
            } else {
                lbl.append("<null>");
            }
        }
        if (nodo.numClaves == 0) {
            lbl.append("(sin claves)");
        }
        return lbl.toString();
    }

    // Dibuja el subárbol y devuelve el id asignado al nodo actual.
    private static int dibujarNodo(NodoB nodo, StringBuilder out, int[] idCounter) {
        if (nodo == null) return -1;

        int myId = idCounter[0]++;
        out.append("  n").append(myId).append(" [label=\"").append(etiquetaNodo(nodo)).append("\"];\n");

        // Conectar hijos en orden
        if (!nodo.esHoja) {
            for (int i = 0; i <= nodo.numClaves; ++i) {
                if (nodo.hijos[i] != null) {
                    int childId = dibujarNodo(nodo.hijos[i], out, idCounter);
                    if (childId >= 0) {
                        out.append("  n").append(myId).append(" -> n").append(childId).append(";\n");
                    }
                }
            }
        }
        return myId;
    }
}