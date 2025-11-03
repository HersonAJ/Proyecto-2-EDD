package org.example.Estructuras.B;

public class NodoB {
    public static final int T=3;//grado minimo del arbol

    public int numClaves;
    public EntradaFecha[] claves;
    public NodoB[] hijos;
    public boolean esHoja;

    public NodoB(boolean hoja) {
        this.esHoja = hoja;
        this.numClaves = 0;
        this.claves = new EntradaFecha[2 * T - 1];
        this.hijos = new NodoB[2 * T];

        for (int i =0; i < 2 * T; i++) {
            hijos[i] = null;
            if (i < 2 * T - 1) {
                claves[i] = null;
            }
        }
    }
}
