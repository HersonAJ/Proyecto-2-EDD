package org.example.Estructuras.BPlus;

import org.example.AVL_Auxiliar.IndiceISBN;

public class NodoHoja extends NodoBPlus{
    public static final int T_BPLUS = 3;

    public static class EntradaGenero{
        public String genero;
        public IndiceISBN indiceISBN;

        public EntradaGenero() {
            this.genero = "";
            this.indiceISBN = null;
        }

        public EntradaGenero(String genero) {
            this.genero = genero;
            this.indiceISBN = new IndiceISBN();
        }
    }

    public EntradaGenero[] entradas;
    public NodoHoja siguiente;
    public NodoHoja anterior;

    public NodoHoja() {
        super(true);
        this.numClaves = 0;
        this.entradas = new EntradaGenero[2 * T_BPLUS - 1];
        this.siguiente = null;
        this.anterior = null;

        //inicializar el array de entradas
        for (int i = 0; i < entradas.length; i++) {
            entradas[i] = new EntradaGenero();
        }
    }

    public int buscarIndicePorEntrada(String genero) {
        return  0;
    }
}
