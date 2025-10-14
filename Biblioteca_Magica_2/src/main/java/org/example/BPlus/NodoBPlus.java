package org.example.BPlus;

public abstract class NodoBPlus {
    public boolean esHoja;
    public int numClaves;

    public NodoBPlus(boolean hoja) {
        this.esHoja = hoja;
        this.numClaves = 0;
    }
}
