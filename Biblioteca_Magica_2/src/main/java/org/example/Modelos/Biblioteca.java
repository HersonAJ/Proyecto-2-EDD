package org.example.Modelos;

import org.example.AVL.ArbolAVL;
import org.example.AVL_Auxiliar.IndiceISBN;
import org.example.B.ArbolB;
import org.example.BPlus.ArbolBPlus;
import org.example.Catalogo.Catalogo;

public class Biblioteca {

    private String id;
    private String nombre;
    private String ubicacion;

    //parametros de tiempo
    private int tiempoIngreso;
    private int tiempoTraspaso;
    private int intervaloDespacho;

    //estructuras de cada arbol
    private ArbolAVL arbolTitulos;
    private ArbolB arbolFechas;
    private ArbolBPlus arbolGeneros;
    private Catalogo catalogo;
    private IndiceISBN indiceISBN;

    //constructor basico
    public Biblioteca(String id, String nombre, String ubicacion, int tiempoIngreso, int tiempoTraspaso, int intervaloDespacho) { //pasar el string a int para los tiempos
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.tiempoIngreso = tiempoIngreso;
        this.tiempoTraspaso = tiempoTraspaso;
        this.intervaloDespacho = intervaloDespacho;

        //inicializacion de estructuras vacias
        this.arbolTitulos = new ArbolAVL();
        this.arbolFechas = new ArbolB();
        this.arbolGeneros = new ArbolBPlus();
        this.catalogo = new Catalogo();
        this.indiceISBN = new IndiceISBN();

    }

    // Getters y Setters básicos
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUbicacion() { return ubicacion; }
    public int getTiempoIngreso() { return tiempoIngreso; }
    public int getTiempoTraspaso() { return tiempoTraspaso; }
    public int getIntervaloDespacho() { return intervaloDespacho; }

    public ArbolAVL getArbolTitulos() { return arbolTitulos; }
    public ArbolB getArbolFechas() { return arbolFechas; }
    public ArbolBPlus getArbolGeneros() { return arbolGeneros; }
    public Catalogo getCatalogo() { return catalogo; }
    public IndiceISBN getIndiceISBN() { return indiceISBN; }

    // Método para pruebas
    public void agregarLibro(Libro libro) {
        arbolTitulos.insertar(libro);
        arbolFechas.insertar(libro);
        arbolGeneros.insertarLibroEnGenero(libro);
        catalogo.agregarLibro(libro);
        indiceISBN.insertar(libro.getIsbn(), libro);
    }

    public boolean eliminarLibroPorISBN(String isbn) {
        // Buscar en el índice ISBN de esta biblioteca
        Libro libro = indiceISBN.buscar(isbn);
        if (libro == null) {
            return false; // Libro no encontrado en esta biblioteca
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
        indiceISBN.eliminar(isbn);

        return eliminadoDelCatalogo;
    }

    // Métodos de búsqueda para esta biblioteca
    public Libro buscarPorTitulo(String titulo) {
        return arbolTitulos.buscarPorTitulo(titulo);
    }

    public ListaLibros buscarTodosPorTitulo(String titulo) {
        return arbolTitulos.buscarTodosPorTitulo(titulo);
    }

    public Libro buscarPorISBN(String isbn) {
        return indiceISBN.buscar(isbn);
    }

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

    @Override
    public String toString() {
        return id + " - " + nombre + " (" + ubicacion + ") " +
                "[Ingreso:" + tiempoIngreso + "s, Traspaso:" + tiempoTraspaso + "s, Intervalo de Despacho:" + intervaloDespacho + "s]";
    }
}
