package org.example.Estructuras.AVL;

import org.example.Modelos.Libro;

class InsertarAVL extends RotacionesAVL {

    protected NodoAVL insertarNodo(NodoAVL nodo, Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("Libro nulo en insertarNodo");
        }
        if (nodo == null) {
            return new NodoAVL(libro);
        }

        int cmp = compararLibros(libro, nodo.getLibro());

        if (cmp < 0) {
            nodo.setIzquierdo(insertarNodo(nodo.getIzquierdo(), libro));
        } else if (cmp > 0) {
            nodo.setDerecho(insertarNodo(nodo.getDerecho(), libro));
        } else {
            // Libro repetido, incrementar ejemplares
            nodo.getLibro().incrementarCantidad();
            return nodo;
        }

        // Actualizar altura y balancear
        actualizarAltura(nodo);
        return balancear(nodo);
    }

    private int compararLibros(Libro a, Libro b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Libro nulo en comparación");
        }
        int cmp = a.getTitulo().compareTo(b.getTitulo());
        if (cmp == 0) {
            return a.getIsbn().compareTo(b.getIsbn());
        }
        return cmp;
    }
}