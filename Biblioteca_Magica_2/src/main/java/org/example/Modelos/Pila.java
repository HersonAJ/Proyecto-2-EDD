package org.example.Modelos;

import java.util.ArrayList;
import java.util.List;

public class Pila<T> {

    private NodoPila<T> tope;
    private int tamaño;

    private static class NodoPila<T> {
        T dato;
        NodoPila<T> siguiente;

        NodoPila(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    public Pila() {
        this.tope = null;
        this.tamaño = 0;
    }

    public void apilar(T elemento) {
        NodoPila<T>  nuevoNodo = new NodoPila<>(elemento);
        nuevoNodo.siguiente = tope;
        tope = nuevoNodo;
        tamaño++;
    }

    public T desapilar() {
        if (estaVacia()) {
            throw new IllegalStateException("Pila vacia");
        }
        T dato = tope.dato;
        tope = tope.siguiente;
        tamaño--;
        return dato;
    }

    public T peek() {
        if (estaVacia()) {
            throw new IllegalStateException("Pila vacia");
        }
        return tope.dato;
    }

    public boolean estaVacia() {
        return tope == null;
    }

    public int tamaño() {
        return tamaño;
    }

    public void limpiar() {
        tope = null;
        tamaño = 0;
    }

    public List<T> obtenerElementos() {
        List<T> elementos = new ArrayList<>();
        NodoPila<T> actual = tope;

        while (actual != null) {
            elementos.add(actual.dato);
            actual = actual.siguiente;
        }
        return elementos;
    }
}
