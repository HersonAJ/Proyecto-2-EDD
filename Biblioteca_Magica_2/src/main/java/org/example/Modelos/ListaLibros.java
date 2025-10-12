package org.example.Modelos;

import org.example.include.Nodo;

public class ListaLibros {

    private Nodo cabeza;
    private Nodo cola;
    private int tamaño;

    public ListaLibros() {
        this.cabeza = null;
        this.cola = null;
        this.tamaño = 0;
    }

    public void insertar(Libro libro) {
        Nodo nuevo = new Nodo(libro);

        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.setSiguiente(nuevo);
            cola = nuevo;
        }
        tamaño++;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public int getTamaño() {
        return tamaño;
    }

    public Nodo getCabeza() {
        return cabeza;
    }

    //iterador interno
    public class Iterador {
        private Nodo actual;

        public Iterador(Nodo nodo) {
            this.actual = nodo;
        }

        public boolean tieneSiguiente() {
            return actual != null;
        }

        public Libro siguiente() {
            Libro libro = actual.getLibro();
            actual = actual.getSiguiente();
            return libro;
        }
    }

    public Iterador obtenerIterador() {
        return new Iterador(cabeza);
    }
}
