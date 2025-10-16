package org.example.Grafo;

public class ListaAdyacencia {
    private Nodo cabeza;
    private int tamaño;

    private static class Nodo {
        Arista dato;
        Nodo siguiente;

        Nodo(Arista dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    public ListaAdyacencia() {
        this.cabeza = null;
        this.tamaño = 0;
    }

    public void agregar(Arista dato) {
        Nodo nuevoNodo = new Nodo(dato);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            Nodo actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoNodo;
        }
        tamaño++;
    }

    public boolean contiene(Arista dato) {
        Nodo actual = cabeza;
        while (actual != null) {
            if (actual.dato.equals(dato)) {
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    public int getTamaño() {
        return tamaño;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    //iterador
    public class IteradorLista {
        private Nodo actual;

        public IteradorLista(Nodo cabeza) {
            this.actual = cabeza;
        }

        public boolean tieneSiguiente() {
            return actual != null;
        }

        public Arista siguiente() {
            if (actual == null) return null;
            Arista dato = actual.dato;
            actual = actual.siguiente;
            return dato;
        }
    }

    public IteradorLista iterador() {
        return new IteradorLista(cabeza);
    }
}