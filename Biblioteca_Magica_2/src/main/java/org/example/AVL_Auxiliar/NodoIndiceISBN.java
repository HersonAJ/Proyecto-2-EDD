package org.example.AVL_Auxiliar;

import org.example.Modelos.Libro;

public class NodoIndiceISBN {
    public String isbn;
    public Libro libro;

    public NodoIndiceISBN izquierdo;
    public NodoIndiceISBN derecho;
    public int altura;

    public NodoIndiceISBN(String isbn, Libro libro) {
        this.isbn = isbn;
        this.libro = libro;
        this.izquierdo = null;
        this.derecho = null;
        this.altura = 1;
    }
}
