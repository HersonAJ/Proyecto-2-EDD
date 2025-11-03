package org.example.Modelos;

import org.example.Estructuras.AVL.ArbolAVL;
import org.example.Estructuras.B.ArbolB;
import org.example.Estructuras.BPlus.ArbolBPlus;
import org.example.Estructuras.Catalogo.Catalogo;
import org.example.Estructuras.Cola;
import org.example.Estructuras.Grafo.GrafoBibliotecas;
import org.example.Estructuras.Pila;
import org.example.Estructuras.TablaHash.TablaHash;

import java.util.Date;
import java.util.List;

public class Biblioteca {

    private String id;
    private String nombre;
    private String ubicacion;

    //nuevos atributos
    private Cola<Libro> colaIngreso;
    private Cola<Libro> colaTraspaso;
    private Cola<Libro> colaSalida;

    //estructuras de cada arbol
    private ArbolAVL arbolTitulos;
    private ArbolB arbolFechas;
    private ArbolBPlus arbolGeneros;
    private Catalogo catalogo;
    //private IndiceISBN indiceISBN;
    private TablaHash<String, Libro> tablaHash;
    private Pila<RegistroPrestamo> pilaPrestamo;

    //constructor basico
    //sobrecargando el constructor
    public Biblioteca(String id, String nombre, String ubicacion, int tiempoIngreso, int tiempoTraspaso, int intervaloDespacho) { //pasar el string a int para los tiempos
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.colaIngreso = new Cola<>(tiempoIngreso, "ingreso");
        this.colaTraspaso = new Cola<>(tiempoTraspaso, "traspaso");
        this.colaSalida = new Cola<>(intervaloDespacho, "salida");

        //inicializacion de estructuras vacias
        this.arbolTitulos = new ArbolAVL();
        this.arbolFechas = new ArbolB();
        this.arbolGeneros = new ArbolBPlus();
        this.catalogo = new Catalogo();
        //this.indiceISBN = new IndiceISBN();
        this.tablaHash = new TablaHash<String, Libro>();
        this.pilaPrestamo = new Pila<>();

    }


    // Getters y Setters básicos
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUbicacion() { return ubicacion; }

    public ArbolAVL getArbolTitulos() { return arbolTitulos; }
    public ArbolB getArbolFechas() { return arbolFechas; }
    public ArbolBPlus getArbolGeneros() { return arbolGeneros; }
    public Catalogo getCatalogo() { return catalogo; }
    //public IndiceISBN getIndiceISBN() { return indiceISBN; }
    public TablaHash<String, Libro> getTablaHash() { return tablaHash; }

    // Método para pruebas
    public void agregarLibro(Libro libro) {
        arbolTitulos.insertar(libro);
        arbolFechas.insertar(libro);
        arbolGeneros.insertarSoloGenero(libro.getGenero());
        arbolGeneros.insertarLibroEnGenero(libro);
        catalogo.agregarLibro(libro);
        //indiceISBN.insertar(libro.getIsbn(), libro);
        tablaHash.insertar(libro.getIsbn(), libro);
    }

    public boolean eliminarLibroPorISBN(String isbn) {
        // Buscar en el índice ISBN de esta biblioteca
        //Libro libro = indiceISBN.buscar(isbn);
        Libro libro = tablaHash.obtener(isbn);
        if (libro == null) {
            return false; // Libro no encontrado en esta biblioteca
        }

        if (!libro.getEstado().equalsIgnoreCase("Disponible")) {
            return false;
        }

        // Obtener todos los datos necesarios para eliminación
        String titulo = libro.getTitulo();
        String fecha = libro.getFecha();
        String genero = libro.getGenero();

        // Eliminar de todas las estructuras de ESTA biblioteca
        arbolTitulos.eliminarPorISBN(isbn, titulo);           // AVL por título
        arbolFechas.eliminarPorISBN(isbn, fecha);             // Árbol B por fecha
        arbolGeneros.eliminarPorISBN(isbn, genero);           // Árbol B+ por género
        boolean eliminadoDelCatalogo = catalogo.eliminarLibroPorISBN(isbn); // Catálogo

        // Eliminar del índice ISBN de ESTA biblioteca
        tablaHash.eliminar(isbn);
        //indiceISBN.eliminar(isbn);

        return eliminadoDelCatalogo;
    }

    // Métodos de búsqueda para esta biblioteca
    public Libro buscarPorTitulo(String titulo) {
        return arbolTitulos.buscarPorTitulo(titulo);
    }

    public ListaLibros buscarTodosPorTitulo(String titulo) {
        return arbolTitulos.buscarTodosPorTitulo(titulo);
    }

    //public Libro buscarPorISBN(String isbn) { return indiceISBN.buscar(isbn); }
    public Libro buscarPorISBN(String isbn) { return tablaHash.obtener(isbn); }

    public ListaLibros buscarPorRangoFechas(int fechaInicio, int fechaFin) {
        return arbolFechas.buscarPorRango(fechaInicio, fechaFin);
    }

    public ListaLibros buscarPorGenero(String genero) {
        return arbolGeneros.buscarPorGenero(genero);
    }

    // Método para verificar si la biblioteca está vacía
    public boolean estaVacia() {
        return catalogo.estaVacio();
    }

   /* @Override
    public String toString() {
        return id + " - " + nombre + " (" + ubicacion + ") " +
                "[Ingreso:" + tiempoIngreso + "s, Traspaso:" + tiempoTraspaso + "s, Intervalo de Despacho:" + intervaloDespacho + "s]";
    }*/

    // Getters para las colas
    public Cola<Libro> getColaIngreso() { return colaIngreso; }
    public Cola<Libro> getColaTraspaso() { return colaTraspaso; }
    public Cola<Libro> getColaSalida() { return colaSalida; }

    //metodos para la pila de prestamos
    public void registrarPrestamo(String isbn, String bibliotecaDestino, String titulo) {
        RegistroPrestamo registro = new RegistroPrestamo(isbn, bibliotecaDestino, titulo, new Date());
        pilaPrestamo.apilar(registro);
    }

    public boolean deshacerUltimoPrestamo(GrafoBibliotecas grafo) {
        if (pilaPrestamo.estaVacia()) {
            return false;
        }

        RegistroPrestamo ultimoPrestamo = pilaPrestamo.desapilar();
        return procesarDevolucion(grafo, ultimoPrestamo);
    }

    private boolean procesarDevolucion(GrafoBibliotecas grafo, RegistroPrestamo prestamo) {
        try {
            Biblioteca destino = grafo.getBiblioteca(prestamo.getBibliotecaDestino());
            if (destino == null) {
                return false;
            }

            // 1. Cambiar estado del libro original en ESTA biblioteca (origen)
            Libro libroLocal = this.buscarPorISBN(prestamo.getIsbn());
            if (libroLocal != null && "En Prestamo".equals(libroLocal.getEstado())) {
                libroLocal.setEstado("Disponible");
            }

            // 2. Eliminar libro en biblioteca destino usando el método ESPECIAL
            Libro libroDestino = destino.buscarPorISBN(prestamo.getIsbn());
            if (libroDestino != null && "Recibido En Prestamo".equals(libroDestino.getEstado())) {
                boolean eliminado = destino.eliminarLibroPrestamo(prestamo.getIsbn());
                return eliminado;
            } else {
                if (libroDestino != null) {
                }
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error deshaciendo préstamo: " + e.getMessage());
            return false;
        }
    }

    public boolean hayPrestamosParaDeshacer() { return !pilaPrestamo.estaVacia();}

    public String getInforPila() {
        return "pila de " + this.id + " : " + pilaPrestamo.tamaño() + " prestamos.";
    }

    public List<RegistroPrestamo> obtenerPrestamos() {
        return pilaPrestamo.obtenerElementos();
    }

    //metodo para eliminar un libro prestado
    public boolean eliminarLibroPrestamo(String isbn) {
        Libro libro = tablaHash.obtener(isbn);
        if (libro == null) {
            return false; // Libro no encontrado
        }

        // Validación ESPECIAL para préstamos
        if (!"Recibido En Prestamo".equalsIgnoreCase(libro.getEstado())) {
            System.out.println("No se puede eliminar préstamo - Estado incorrecto: " + libro.getEstado());
            return false;
        }

        // Obtener datos para eliminación
        String titulo = libro.getTitulo();
        String fecha = libro.getFecha();
        String genero = libro.getGenero();

        // Eliminar de todas las estructuras
        arbolTitulos.eliminarPorISBN(isbn, titulo);
        arbolFechas.eliminarPorISBN(isbn, fecha);
        arbolGeneros.eliminarPorISBN(isbn, genero);
        boolean eliminadoDelCatalogo = catalogo.eliminarLibroPorISBN(isbn);
        tablaHash.eliminar(isbn);

        return eliminadoDelCatalogo;
    }
}
