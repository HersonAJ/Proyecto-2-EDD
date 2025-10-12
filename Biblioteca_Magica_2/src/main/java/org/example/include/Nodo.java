package org.example.include;

import org.example.Modelos.Libro;

public class Nodo {
    Libro libro;
    Nodo siguiente;

    public Nodo(Libro libro) {
        this.libro = libro;
        this.siguiente = null;
    }

    public Nodo getSiguiente() {
        return this.siguiente;
    }

    public void setSiguiente(Nodo siguiente) {
        this.siguiente = siguiente;
    }

    public Libro getLibro() {
        return this.libro;
    }
}
