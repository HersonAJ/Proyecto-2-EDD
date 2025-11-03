package org.example.Estructuras.AVL;

import org.example.Modelos.Libro;
import org.example.Modelos.ListaLibros;

class BuscarAVL {

    protected Libro buscarPorTituloRecursivo(NodoAVL nodo, String titulo) {
        if (nodo == null) return null;

        int cmp = titulo.compareTo(nodo.getLibro().getTitulo());

        if (cmp == 0) {
            return nodo.getLibro(); // primera coincidencia encontrada
        } else if (cmp < 0) {
            return buscarPorTituloRecursivo(nodo.getIzquierdo(), titulo);
        } else {
            return buscarPorTituloRecursivo(nodo.getDerecho(), titulo);
        }
    }

    protected void buscarTodosPorTituloRecursivo(NodoAVL nodo, String titulo, ListaLibros lista) {
        if (nodo == null) return;

        // recorrido inorden para encontrar todos
        buscarTodosPorTituloRecursivo(nodo.getIzquierdo(), titulo, lista);

        if (titulo.compareTo(nodo.getLibro().getTitulo()) == 0) {
            lista.insertar(nodo.getLibro());
        }

        buscarTodosPorTituloRecursivo(nodo.getDerecho(), titulo, lista);
    }

    protected NodoAVL buscarNodo(NodoAVL nodo, String titulo, String isbn) {
        if (nodo == null) return null;

        int cmp = titulo.compareTo(nodo.getLibro().getTitulo());

        if (cmp == 0) {
            // Mismo título, verificar ISBN si se proporciona
            if (isbn.isEmpty() || isbn.equals(nodo.getLibro().getIsbn())) {
                return nodo;
            }
        }

        if (cmp <= 0) {
            return buscarNodo(nodo.getIzquierdo(), titulo, isbn);
        } else {
            return buscarNodo(nodo.getDerecho(), titulo, isbn);
        }
    }

    protected void recorrerEnOrdenRecursivo(NodoAVL nodo, ListaLibros lista) {
        if (nodo == null) return;

        try {
            // recorrer sub árbol izquierdo menores
            recorrerEnOrdenRecursivo(nodo.getIzquierdo(), lista);

            // agregar libro actual a la lista
            if (nodo.getLibro() != null) {
                lista.insertar(nodo.getLibro());
            }

            // recorrer sub árbol derecho mayores
            recorrerEnOrdenRecursivo(nodo.getDerecho(), lista);
        } catch (Exception e) {
            System.err.println("Error en BuscarAVL::recorrerEnOrdenRecursivo: " + e.getMessage());
            throw e;
        }
    }

    protected int compararIsbn(Libro a, Libro b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Libro nulo en comparación ISBN");
        }
        return a.getIsbn().compareTo(b.getIsbn());
    }

    protected ListaLibros obtenerEnOrdenAlfabetico(NodoAVL raiz) {
        try {
            ListaLibros lista = new ListaLibros();
            recorrerEnOrdenRecursivo(raiz, lista);
            return lista;
        } catch (Exception e) {
            System.err.println("Error en bucarAVL.obtenerLibrosEnOrdenAlfabetico: " + e.getMessage());
            return new ListaLibros();
        }
    }
}