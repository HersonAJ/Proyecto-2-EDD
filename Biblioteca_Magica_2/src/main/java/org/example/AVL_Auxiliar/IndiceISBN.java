package org.example.AVL_Auxiliar;

import org.example.Modelos.Libro;

public class IndiceISBN {
    private NodoIndiceISBN raiz;

    // Constructor
    public IndiceISBN() {
        this.raiz = null;
    }

    // Métodos privados auxiliares
    private int altura(NodoIndiceISBN nodo) {
        return nodo != null ? nodo.altura : 0;
    }

    private int factorBalance(NodoIndiceISBN nodo) {
        if (nodo == null) return 0;
        return altura(nodo.izquierdo) - altura(nodo.derecho);
    }

    private NodoIndiceISBN nodoMinimo(NodoIndiceISBN nodo) {
        NodoIndiceISBN actual = nodo;
        while (actual != null && actual.izquierdo != null) {
            actual = actual.izquierdo;
        }
        return actual;
    }

    // Rotaciones
    private NodoIndiceISBN rotarIzquierda(NodoIndiceISBN ref) {
        NodoIndiceISBN nodo = ref.derecho;
        NodoIndiceISBN nodo1 = nodo.izquierdo;

        nodo.izquierdo = ref;
        ref.derecho = nodo1;

        ref.altura = 1 + Math.max(altura(ref.izquierdo), altura(ref.derecho));
        nodo.altura = 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));

        return nodo;
    }

    private NodoIndiceISBN rotarDerecha(NodoIndiceISBN ref) {
        NodoIndiceISBN nodo = ref.izquierdo;
        NodoIndiceISBN nodo1 = nodo.derecho;

        nodo.derecho = ref;
        ref.izquierdo = nodo1;

        ref.altura = 1 + Math.max(altura(ref.izquierdo), altura(ref.derecho));
        nodo.altura = 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));

        return nodo;
    }

    // Balanceo
    private NodoIndiceISBN balancear(NodoIndiceISBN nodo) {
        int balance = factorBalance(nodo);

        if (balance > 1 && factorBalance(nodo.izquierdo) >= 0)
            return rotarDerecha(nodo);

        if (balance > 1 && factorBalance(nodo.izquierdo) < 0) {
            nodo.izquierdo = rotarIzquierda(nodo.izquierdo);
            return rotarDerecha(nodo);
        }

        if (balance < -1 && factorBalance(nodo.derecho) <= 0)
            return rotarIzquierda(nodo);

        if (balance < -1 && factorBalance(nodo.derecho) > 0) {
            nodo.derecho = rotarDerecha(nodo.derecho);
            return rotarIzquierda(nodo);
        }

        return nodo;
    }

    // Inserción
    private NodoIndiceISBN insertarNodo(NodoIndiceISBN nodo, String isbn, Libro libro) {
        if (nodo == null) return new NodoIndiceISBN(isbn, libro);

        if (isbn.compareTo(nodo.isbn) < 0)
            nodo.izquierdo = insertarNodo(nodo.izquierdo, isbn, libro);
        else if (isbn.compareTo(nodo.isbn) > 0)
            nodo.derecho = insertarNodo(nodo.derecho, isbn, libro);
        else
            return nodo; // ISBN ya existe, no duplicados

        nodo.altura = 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));
        return balancear(nodo);
    }

    public void insertar(String isbn, Libro libro) {
        raiz = insertarNodo(raiz, isbn, libro);
    }

    // Eliminación
    private NodoIndiceISBN eliminarNodo(NodoIndiceISBN nodo, String isbn) {
        if (nodo == null) return null;

        if (isbn.compareTo(nodo.isbn) < 0)
            nodo.izquierdo = eliminarNodo(nodo.izquierdo, isbn);
        else if (isbn.compareTo(nodo.isbn) > 0)
            nodo.derecho = eliminarNodo(nodo.derecho, isbn);
        else {
            if (nodo.izquierdo == null && nodo.derecho == null) {
                return null;
            }
            else if (nodo.izquierdo == null) {
                return nodo.derecho;
            }
            else if (nodo.derecho == null) {
                return nodo.izquierdo;
            }
            else {
                NodoIndiceISBN sucesor = nodoMinimo(nodo.derecho);
                // GUARDAR ISBN ANTES de eliminar
                String isbnSucesor = sucesor.isbn;

                // Eliminar el sucesor
                nodo.derecho = eliminarNodo(nodo.derecho, isbnSucesor);

                // Actualizar solo el ISBN
                nodo.isbn = isbnSucesor;
            }
        }

        nodo.altura = 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));
        return balancear(nodo);
    }

    public void eliminar(String isbn) {
        raiz = eliminarNodo(raiz, isbn);
    }

    // Búsqueda
    public Libro buscar(String isbn) {
        NodoIndiceISBN actual = raiz;
        while (actual != null) {
            int cmp = isbn.compareTo(actual.isbn);
            if (cmp < 0)
                actual = actual.izquierdo;
            else if (cmp > 0)
                actual = actual.derecho;
            else
                return actual.libro;
        }
        return null;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public NodoIndiceISBN getRaiz() {
        return raiz;
    }
}
