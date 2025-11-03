package org.example.Estructuras.BPlus;

public class NodoInterno extends NodoBPlus {
    public static final int T_BPLUS = 3;

    public String[] claves;
    public NodoBPlus[] hijos;

    public NodoInterno() {
        super(false);
        this.numClaves = 0;
        this.claves = new String[2 * T_BPLUS - 1];
        this.hijos = new NodoBPlus[2 * T_BPLUS];
        for (int i = 0; i < 2 * T_BPLUS; i++) {
            hijos[i] = null;
        }
    }

    public int buscarIndiceClave(String clave) {
        return 0;
    }
}
