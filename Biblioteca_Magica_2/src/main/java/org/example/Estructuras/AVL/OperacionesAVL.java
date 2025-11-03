package org.example.Estructuras.AVL;

class OperacionesAVL {

    protected int altura(NodoAVL nodo) {
        return nodo != null ? nodo.getAltura() : 0;
    }

    protected int factorBalance(NodoAVL nodo) {
        if (nodo == null) return 0;
        return altura(nodo.getDerecho()) - altura(nodo.getIzquierdo());
    }

    protected void actualizarAltura(NodoAVL nodo) {
        if (nodo != null) {
            nodo.setAltura(1 + Math.max(altura(nodo.getIzquierdo()), altura(nodo.getDerecho())));
        }
    }
}