package org.example.Estructuras.AVL;

class RotacionesAVL extends OperacionesAVL {

    protected NodoAVL rotarIzquierda(NodoAVL nodo) {
        if (nodo == null || nodo.getDerecho() == null) {
            throw new IllegalArgumentException("rotarIzquierda requiere hijo derecho no nulo");
        }

        NodoAVL nuevaRaiz = nodo.getDerecho();
        NodoAVL aux = nuevaRaiz.getIzquierdo();

        nuevaRaiz.setIzquierdo(nodo);
        nodo.setDerecho(aux);

        actualizarAltura(nodo);
        actualizarAltura(nuevaRaiz);

        return nuevaRaiz;
    }

    protected NodoAVL rotarDerecha(NodoAVL nodo) {
        if (nodo == null || nodo.getIzquierdo() == null) {
            throw new IllegalArgumentException("rotarDerecha requiere hijo izquierdo no nulo");
        }

        NodoAVL nuevaRaiz = nodo.getIzquierdo();
        NodoAVL aux = nuevaRaiz.getDerecho();

        nuevaRaiz.setDerecho(nodo);
        nodo.setIzquierdo(aux);

        actualizarAltura(nodo);
        actualizarAltura(nuevaRaiz);

        return nuevaRaiz;
    }

    protected NodoAVL rotacionDobleIzquierda(NodoAVL nodo) {
        try {
            if (nodo == null || nodo.getDerecho() == null) {
                throw new IllegalArgumentException("rotacionDobleIzquierda requiere nodo y hijo derecho no nulos");
            }
            nodo.setDerecho(rotarDerecha(nodo.getDerecho()));
            return rotarIzquierda(nodo);
        } catch (Exception e) {
            System.err.println("Error en RotacionesAVL::rotacionDobleIzquierda: " + e.getMessage());
            throw e;
        }
    }

    protected NodoAVL rotacionDobleDerecha(NodoAVL nodo) {
        try {
            if (nodo == null || nodo.getIzquierdo() == null) {
                throw new IllegalArgumentException("rotacionDobleDerecha requiere nodo y hijo izquierdo no nulos");
            }
            nodo.setIzquierdo(rotarIzquierda(nodo.getIzquierdo()));
            return rotarDerecha(nodo);
        } catch (Exception e) {
            System.err.println("Error en RotacionesAVL::rotacionDobleDerecha: " + e.getMessage());
            throw e;
        }
    }

    protected NodoAVL balancear(NodoAVL nodo) {
        if (nodo == null) return null;

        try {
            actualizarAltura(nodo);
            int fb = factorBalance(nodo);

            // Desbalanceo hacia la derecha
            if (fb > 1) {
                if (factorBalance(nodo.getDerecho()) < 0) {
                    return rotacionDobleIzquierda(nodo);
                } else {
                    return rotarIzquierda(nodo);
                }
            }
            // Desbalanceo hacia la izquierda
            else if (fb < -1) {
                if (factorBalance(nodo.getIzquierdo()) > 0) {
                    return rotacionDobleDerecha(nodo);
                } else {
                    return rotarDerecha(nodo);
                }
            }

            return nodo;
        } catch (Exception e) {
            System.err.println("Error en RotacionesAVL::balancear: " + e.getMessage());
            return nodo;
        }
    }
}