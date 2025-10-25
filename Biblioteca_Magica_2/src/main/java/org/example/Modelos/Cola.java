package org.example.Modelos;

import java.util.ArrayList;
import java.util.List;

public class Cola<T> {
    private class Nodo {
        T dato;
        Nodo siguiente;
        Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    public interface Procesador<T> {
        void procesar(T elemento, String tipoCola);
    }

    // Atributos esenciales
    private Nodo frente;
    private Nodo fin;
    private int tiempoProcesamiento; // milisegundos
    private String tipo; // ingreso, traspaso, salida
    private Thread hiloProcesamiento;
    private boolean activa;
    private Procesador<T> procesador;

    public Cola(int tiempoProcesamiento, String tipo) {
        this.frente = null;
        this.fin = null;
        this.tiempoProcesamiento = tiempoProcesamiento;
        this.tipo = tipo;
        this.activa = false;
    }

    public void encolar(T elemento) {
        synchronized (this) {
            Nodo nuevoNodo = new Nodo(elemento);
            if (frente == null) {
                frente = nuevoNodo;
            } else {
                fin.siguiente = nuevoNodo;
            }
            fin = nuevoNodo;
            this.notify();
        }
    }

    public T desencolar() {
        synchronized (this) {
            if (frente == null) return null;
            T dato = frente.dato;
            frente = frente.siguiente;
            if (frente == null) fin = null;
            return dato;
        }
    }

    public boolean estaVacia() {
        synchronized (this) {
            return frente == null;
        }
    }

    public int getTiempoProcesamiento() { return tiempoProcesamiento; }
    public String getTipo() { return tipo; }

    public void setProcesador(Procesador<T> procesador) {
        this.procesador = procesador;
    }

    // WORKER SUPER SIMPLE - CADA ELEMENTO ESPERA SU TIEMPO COMPLETO
    public void iniciarProcesamiento() {
        synchronized (this) {
            if (activa) return;
            activa = true;
        }

        hiloProcesamiento = new Thread(() -> {
            while (activa) {
                try {
                    T elemento = null;

                    // Esperar hasta que haya elementos
                    synchronized (this) {
                        while (activa && frente == null) {
                            this.wait();
                        }
                        if (!activa) break;

                        // Tomar el elemento
                        elemento = desencolar();
                    }

                    // ESPERAR EL TIEMPO CONFIGURADO para este elemento
                    if (elemento != null) {
                        Thread.sleep(tiempoProcesamiento);

                        // Procesar el elemento
                        if (procesador != null) {
                            procesador.procesar(elemento, tipo);
                        }
                    }

                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    System.err.println("Error en cola " + tipo + ": " + e.getMessage());
                }
            }
        }, "Cola-Worker-" + tipo);

        hiloProcesamiento.setDaemon(true);
        hiloProcesamiento.start();
    }

    public void detenerProcesamiento() {
        synchronized (this) {
            activa = false;
            this.notify();
        }
    }

    //metodos para el monitoreo
    public int getTamaño() {
        synchronized (this) {
            int count = 0;
            Nodo actual = frente;
            while (actual != null) {
                count++;
                actual = actual.siguiente;
            }
            return count;
        }
    }

    public T frente() {
        synchronized (this) {
            return (frente != null) ? frente.dato : null;
        }
    }

    public java.util.List<T> obtenerElementos() {
        synchronized (this) {
            List<T> elementos = new ArrayList<>();
            Nodo actual = frente;
            while (actual != null) {
                elementos.add(actual.dato);
                actual = actual.siguiente;
            }
            return elementos;
        }
    }
}