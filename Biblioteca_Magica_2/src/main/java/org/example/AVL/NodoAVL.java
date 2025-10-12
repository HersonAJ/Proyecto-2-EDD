package org.example.AVL;

import org.example.Modelos.Libro;

public class NodoAVL {

    private Libro libro;
    private NodoAVL izquierdo;
    private NodoAVL derecho;
    private int altura;

    //constructor
    public NodoAVL(Libro libro) {
        this.libro = libro;
        this.izquierdo = null;
        this.derecho = null;
        this.altura = 1;
    }

    //getters
    public Libro getLibro() { return libro; }
    public NodoAVL getIzquierdo() { return izquierdo; }
    public NodoAVL getDerecho() { return derecho; }
    public int getAltura() { return altura; }

    //setters
    public void setLibro(Libro libro) { this.libro = libro; }
    public void setIzquierdo(NodoAVL izquierdo) { this.izquierdo = izquierdo; }
    public void setDerecho(NodoAVL derecho) { this.derecho = derecho; }
    public void setAltura(int altura) { this.altura = altura; }

    //contar ejemplares
    public int contarEjemplares() {
        return (libro != null) ? libro.getCantidad() : 0 ;
    }
}
