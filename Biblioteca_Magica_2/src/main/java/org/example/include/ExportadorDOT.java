package org.example.include;

import org.example.Estructuras.AVL.NodoAVL;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ExportadorDOT {
    private final NodoAVL raiz;
    private final String rutaArchivo;

    public ExportadorDOT(NodoAVL raiz, String rutaArchivo) {
        this.raiz = raiz;
        this.rutaArchivo = rutaArchivo;
    }

    public void exportar() {
        try (PrintWriter file = new PrintWriter(new FileWriter(rutaArchivo))) {
            file.println("digraph Arbol {");
            file.println("    rankdir=TB;");
            file.println("    node [shape=record, style=filled, fillcolor=lightyellow, fontname=\"Arial\", fontsize=10];");
            recorrer(raiz, file);
            file.println("}");
        } catch (IOException e) {
            System.err.println("Error: no se pudo crear el archivo DOT en " + rutaArchivo);
            System.err.println("Error en ExportadorDOT::exportar: " + e.getMessage());
        }
    }

    private String escapeLabel(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '"' || c == '\\') out.append('\\');
            out.append(c);
        }
        return out.toString();
    }

    private String nodeId(NodoAVL nodo) {
        return "n" + System.identityHashCode(nodo);
    }

    private void declararNodo(NodoAVL nodo, PrintWriter out) {
        if (nodo == null || nodo.getLibro() == null) return;

        out.println("    " + nodeId(nodo) +
                " [label=\"<L> | " +
                escapeLabel(nodo.getLibro().getTitulo()) +
                " | <R>\"];");
    }

    private void recorrer(NodoAVL nodo, PrintWriter out) {
        if (nodo == null) return;

        declararNodo(nodo, out);

        if (nodo.getIzquierdo() != null) {
            declararNodo(nodo.getIzquierdo(), out);
            out.println("    " + nodeId(nodo) + ":L -> " + nodeId(nodo.getIzquierdo()) + ";");
            recorrer(nodo.getIzquierdo(), out);
        }
        if (nodo.getDerecho() != null) {
            declararNodo(nodo.getDerecho(), out);
            out.println("    " + nodeId(nodo) + ":R -> " + nodeId(nodo.getDerecho()) + ";");
            recorrer(nodo.getDerecho(), out);
        }
    }
}