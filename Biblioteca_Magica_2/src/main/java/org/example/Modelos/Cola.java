package org.example.Modelos;

public class Cola <T> {
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
    private int tamaño;
    private int tiempoProcesamiento; // milisegundos
    private String tipo; // ingreso, traspaso, salida
    private Thread hiloProcesamiento;
    private boolean activa;
    private Procesador<T> procesador;

    // Constructor simplificado
    public Cola(int tiempoProcesamiento, String tipo) {
        this.frente = null;
        this.fin = null;
        this.tamaño = 0;
        this.tiempoProcesamiento = tiempoProcesamiento;
        this.tipo = tipo;
        this.activa = false;
    }

    // ENCOLAR - versión simple
    public void encolar(T elemento) {
        synchronized (this) {
            Nodo nuevoNodo = new Nodo(elemento);
            if (frente == null) {
                frente = nuevoNodo;
            } else {
                fin.siguiente = nuevoNodo;
            }
            fin = nuevoNodo;
            tamaño++;
            // Notificar al worker que hay trabajo
            this.notify();
        }
    }

    // DESENCOLAR - versión simple
    public T desencolar() {
        synchronized (this) {
            if (frente == null) {
                return null;
            }
            T dato = frente.dato;
            frente = frente.siguiente;
            if (frente == null) {
                fin = null;
            }
            tamaño--;
            return dato;
        }
    }

    // VERIFICAR SI ESTÁ VACÍA
    public boolean estaVacia() {
        synchronized (this) {
            return frente == null;
        }
    }

    // GETTERS básicos
    public int getTamaño() {
        synchronized (this) {
            return tamaño;
        }
    }

    public int getTiempoProcesamiento() {
        return tiempoProcesamiento;
    }

    public String getTipo() {
        return tipo;
    }

    // CONFIGURAR PROCESADOR
    public void setProcesador(Procesador<T> procesador) {
        this.procesador = procesador;
    }

    // WORKER SIMPLIFICADO
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

                    // Si tenemos elemento, procesarlo después del tiempo configurado
                    if (elemento != null) {
                        // Esperar el tiempo de procesamiento
                        Thread.sleep(tiempoProcesamiento);

                        // Ejecutar el callback
                        if (procesador != null) {
                            procesador.procesar(elemento, tipo);
                        }
                    }

                } catch (InterruptedException e) {
                    // Salir silenciosamente si nos interrumpen
                    break;
                } catch (Exception e) {
                    // Capturar cualquier error para que el worker no muera
                    System.err.println("Error en cola " + tipo + ": " + e.getMessage());
                }
            }
        }, "Cola-Worker-" + tipo);

        hiloProcesamiento.setDaemon(true);
        hiloProcesamiento.start();
    }

    // DETENER PROCESAMIENTO
    public void detenerProcesamiento() {
        synchronized (this) {
            activa = false;
            this.notify(); // Despertar al worker para que salga
        }
    }

    // MÉTODO PARA DEBUG
    public void mostrarCola() {
        synchronized (this) {
            if (frente == null) {
                System.out.println("Cola " + tipo + " vacía");
                return;
            }
            System.out.print("Cola " + tipo + " (" + tamaño + " elementos): ");
            Nodo actual = frente;
            while (actual != null) {
                System.out.print(actual.dato + " -> ");
                actual = actual.siguiente;
            }
            System.out.println("NULL");
        }
    }
}