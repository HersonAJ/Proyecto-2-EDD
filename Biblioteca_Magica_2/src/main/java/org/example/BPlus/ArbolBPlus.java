package org.example.BPlus;

import org.example.Modelos.Libro;
import org.example.Modelos.ListaLibros;

public class ArbolBPlus {
    private NodoBPlus raiz;
    private NodoHoja primeraHoja;
    private InsertarBPlus insertarBPlus;
    private EliminarBPlus eliminarBPlus;
    private BuscarBPlus buscarBPlus;

    public ArbolBPlus() {
        this.raiz = new NodoHoja();   // al inicio la raíz es una hoja
        this.primeraHoja = (NodoHoja) raiz;
        this.insertarBPlus = new InsertarBPlus();
        this.eliminarBPlus = new EliminarBPlus();
        this.buscarBPlus = new BuscarBPlus();
    }

    public void insertarSoloGenero(String genero) {
        insertarBPlus.insertarSoloGenero(this, genero);
    }

    public void insertarLibroEnGenero(Libro libro) {
        insertarBPlus.insertarLibroEnGenero(this, libro);
    }

    public void eliminarPorISBN(String isbn, String genero) {
        eliminarBPlus.eliminarPorISBN(this, isbn, genero);
    }

    public ListaLibros buscarPorGenera(String genero) {
        return buscarBPlus.buscarPorGenero(this, genero);
    }

    // Getters necesarios para las operaciones internas
    public NodoBPlus getRaiz() {
        return raiz;
    }

    public void setRaiz(NodoBPlus nuevaRaiz) {
        this.raiz = nuevaRaiz;
    }

    public NodoHoja getPrimeraHoja() {
        return primeraHoja;
    }

    public void setPrimeraHoja(NodoHoja primeraHoja) {
        this.primeraHoja = primeraHoja;
    }

    public void balancerInterno(NodoInterno interno) {
        eliminarBPlus.balancearInterno(this, interno);
    }

}