package org.example.Estructuras.Catalogo;

import org.example.Modelos.Libro;

public class Catalogo {
    private Nodo cabeza;
    private Nodo cola;
    private int tamaño;

    public Catalogo() {
        this.cabeza = null;
        this.cola = null;
        this.tamaño = 0;
    }

    // Clase interna Nodo
    private static class Nodo {
        public Libro libro;
        public Nodo siguiente;

        public Nodo(Libro libro) {
            this.libro = libro;
            this.siguiente = null;
        }
    }

    public void agregarLibro(Libro libro) {
        Nodo nuevoNodo = new Nodo(libro);

        if (estaVacio()) {
            cabeza = nuevoNodo;
            cola = nuevoNodo;
        } else {
            cola.siguiente = nuevoNodo;
            cola = nuevoNodo;
        }
        tamaño++;
    }

    public boolean eliminarLibroPorISBN(String isbn) {
        if (estaVacio()) return false;

        // Caso especial: eliminar cabeza
        if (cabeza.libro.getIsbn().equals(isbn)) {
            Nodo temp = cabeza;
            cabeza = cabeza.siguiente;
            if (cabeza == null) cola = null; // Lista queda vacía
            // En Java no necesitamos delete explícito
            tamaño--;
            return true;
        }

        // Buscar en el resto de la lista
        Nodo actual = cabeza;
        while (actual.siguiente != null) {
            if (actual.siguiente.libro.getIsbn().equals(isbn)) {
                Nodo temp = actual.siguiente;
                actual.siguiente = temp.siguiente;

                if (temp == cola) {
                    cola = actual; // Actualizar cola si eliminamos el último
                }

                // En Java no necesitamos delete explícito
                tamaño--;
                return true;
            }
            actual = actual.siguiente;
        }

        return false;
    }

    public boolean estaVacio() {
        return cabeza == null;
    }

    public int getTamaño() {
        return tamaño;
    }

    public Libro buscarTituloSecuencial(String titulo) {
        Nodo actual = cabeza;
        while (actual != null) {
            if (actual.libro.getTitulo().equals(titulo)) {
                return actual.libro;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    public Libro buscarISBNSecuencial(String isbn) {
        Nodo actual = cabeza;
        while (actual != null) {
            if (actual.libro.getIsbn().equals(isbn)) {
                return actual.libro;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    // Método adicional para obtener todos los libros (útil para iterar)
    public java.util.List<Libro> obtenerTodosLosLibros() {
        java.util.List<Libro> libros = new java.util.ArrayList<>();
        Nodo actual = cabeza;
        while (actual != null) {
            libros.add(actual.libro);
            actual = actual.siguiente;
        }
        return libros;
    }
}