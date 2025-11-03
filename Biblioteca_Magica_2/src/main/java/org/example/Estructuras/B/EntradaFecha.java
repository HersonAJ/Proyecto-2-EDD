package org.example.Estructuras.B;

import org.example.AVL_Auxiliar.IndiceISBN;

public class EntradaFecha {
    public int fecha;
    public IndiceISBN indiceISBN;

    public EntradaFecha() {
        this.fecha = 0;
        this.indiceISBN = new IndiceISBN();
    }

    public EntradaFecha(int fecha) {
        this.fecha = fecha;
        this.indiceISBN = new IndiceISBN();
    }
}
