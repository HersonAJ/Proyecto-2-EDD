package org.example.Modelos;

public class Cola <T> {
    //clase interna de nodo
    private class Nodo<T> {
        T dato;
        Nodo<T> siguiente;

        public Nodo (T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    //atributos de la cola
    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamaño;
    private int tiempoProcesamiento;
    private String tipo; //ingreso, traspaso, salida
    private long ultimoProcesamiento; //timestamp del ultimo procesamiento
    private Thread hiloProcesamiento;
    private boolean activa;

    //constructor
    public Cola(int tiempoProcesamiento, String tipo) {
        this.frente = null;
        this.fin = null;
        this.tamaño = 0;
        this.tiempoProcesamiento = tiempoProcesamiento;
        this.tipo = tipo;
        this.ultimoProcesamiento = 0;
    }

    //metodos de la cola
    //encolar al final
    public void encolar(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);

        if (estaVacia()) {
            frente = nuevoNodo;
        } else  {
            fin.siguiente = nuevoNodo;
        }
        fin = nuevoNodo;
        tamaño++;
    }

    //desencolar del frente
    public T desencolar() {
        if (estaVacia()) {
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

    //ver el elemento del frente sin removerlo
    public T frente() {
        if (estaVacia()) {
            return null;
        }
        return frente.dato;
    }

    public boolean estaVacia() {
        return frente == null;
    }

    //obtener el tamaño actual
    public int getTamaño() {
        return tamaño;
    }

    //verificar si se puede procesar respetando los tiempos
    public boolean puedeProcesar() {
        if (estaVacia()) {
            return false;
        }

        long tiempoActual = System.currentTimeMillis();
        return (tiempoActual - ultimoProcesamiento) >= tiempoProcesamiento;
    }

    //procesar el siguiente elemento si esta listo
    public T procesarSiguiente() {
        if (puedeProcesar() && !estaVacia()) {
            ultimoProcesamiento = System.currentTimeMillis();
            return desencolar();
        }
        return null;
    }

    //getters y setters
    public int getTiempoProcesamiento() {
        return tiempoProcesamiento;
    }

    public void setTiempoProcesamiento(int tiempoProcesamiento) {
        this.tiempoProcesamiento = tiempoProcesamiento;
    }

    public String getTipo() {
        return tipo;
    }

    //metodo de debug
    public void mostrarCola() {
        if (estaVacia()) {
            System.out.println("Cola " + tipo + " vacía");
            return;
        }

        System.out.print("Cola " + tipo + " (" + tamaño + " elementos): ");
        Nodo<T> actual = frente;
        while (actual != null) {
            System.out.print(actual.dato + " -> ");
            actual = actual.siguiente;
        }
        System.out.println("NULL");
    }

    public void iniciarProcesamiento() {
        this.activa = true;
        this.hiloProcesamiento = new Thread(() -> {
            while (activa) {
                if (puedeProcesar() && !estaVacia()) {
                    T elemento = desencolar();
                    // Notificar que se procesó (usar callback)
                    System.out.println("Procesado: " + elemento);
                }
                try {
                    Thread.sleep(100); // Revisar cada 100ms
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        hiloProcesamiento.start();
    }

    public void detenerProcesamiento() {
        this.activa = false;
    }
}
