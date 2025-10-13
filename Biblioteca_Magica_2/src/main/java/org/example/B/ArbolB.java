package org.example.B;

import org.example.Modelos.Libro;

public class ArbolB {
    private NodoB raiz;
    private InsertarB insertarB;

    public ArbolB() {
        this.raiz = null;
        this.insertarB = new InsertarB();
    }

    public void insertar(Libro libro) {
        this.raiz = insertarB.insertar(this.raiz, libro);
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public NodoB getRaiz() {
        return raiz;
    }
}
