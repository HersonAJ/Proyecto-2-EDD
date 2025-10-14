package org.example.AVL;

import org.example.Modelos.Libro;
import org.example.Modelos.ListaLibros;

public class ArbolAVL {
    private NodoAVL raiz;
    private InsertarAVL insertarAVL;
    private BuscarAVL buscarAVL;
    private EliminarAVL eliminarAVL;

    public ArbolAVL() {
        this.raiz = null;
        this.insertarAVL = new InsertarAVL();
        this.buscarAVL = new BuscarAVL();
        this.eliminarAVL = new EliminarAVL();
    }

    public void insertar(Libro libro) {
        try {
            if (libro == null) throw new IllegalArgumentException("Intento de insertar libro nulo");
            raiz = insertarAVL.insertarNodo(raiz, libro);
        } catch (Exception e) {
            System.err.println("Error en ArbolAVL::insertar: " + e.getMessage());
            throw e;
        }
    }

    public Libro buscarPorTitulo(String titulo) {
        try {
            if (titulo == null || titulo.isEmpty()) {
                throw new IllegalArgumentException("Título de búsqueda vacío");
            }
            return buscarAVL.buscarPorTituloRecursivo(raiz, titulo);
        } catch (Exception e) {
            System.err.println("Error en ArbolAVL::buscarPorTitulo: " + e.getMessage());
            return null;
        }
    }

    public ListaLibros buscarTodosPorTitulo(String titulo) {
        try {
            if (titulo == null || titulo.isEmpty()) {
                throw new IllegalArgumentException("Título de búsqueda vacío");
            }
            ListaLibros lista = new ListaLibros();
            buscarAVL.buscarTodosPorTituloRecursivo(raiz, titulo, lista);
            return lista;
        } catch (Exception e) {
            System.err.println("Error en ArbolAVL::buscarTodosPorTitulo: " + e.getMessage());
            return new ListaLibros();
        }
    }
    public void eliminarPorISBN(String isbn, String titulo) {
        try {
            if (isbn == null || isbn.isEmpty()) {
                throw new IllegalArgumentException("ISBN vacío en eliminación");
            }
            if (titulo == null || titulo.isEmpty()) {
                throw new IllegalArgumentException("Título vacío en eliminación");
            }

            raiz = eliminarAVL.eliminarNodoEficiente(raiz, titulo, isbn);
        } catch (Exception e) {
            System.err.println("Error en ArbolAVL::eliminarPorISBN: " + e.getMessage());
            throw e;
        }
    }

    public boolean estaVacio() {
        return raiz == null;
    }
    public NodoAVL getRaiz() {
        return raiz;
    }
    public ListaLibros obtenerLibrosEnOrdenAlfabetico() {
        return buscarAVL.obtenerEnOrdenAlfabetico(raiz);
    }
    public void guardarComoDOT(String ruta) {
        try {
            if (ruta == null || ruta.isEmpty())
                throw new IllegalArgumentException("Ruta vacía para exportar DOT");

            org.example.include.ExportadorDOT exportador = new org.example.include.ExportadorDOT(raiz, ruta);
            exportador.exportar();
        } catch (Exception e) {
            System.err.println("Error en ArbolAVL::guardarComoDOT: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}