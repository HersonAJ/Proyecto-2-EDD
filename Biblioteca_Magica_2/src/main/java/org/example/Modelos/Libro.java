package org.example.Modelos;

public class Libro {

    private int cantidad;
    private String titulo;
    private String isbn;
    private String genero;
    private String fecha;
    private String autor;

    //nuevos campos
    /*private String estado;
    private String idBibliotecaOrigen;
    private String idBibliotecaDestiono;
    private String prioridad;*/

    //constructor vacio
    public Libro() { this.titulo = ""; this.isbn = ""; this.genero = ""; this.fecha = ""; this.autor = ""; this.cantidad = 1; }

    //constructor con parametros
    public Libro(String ttitulo, String isbn, String genero, String fecha, String autor) {
        this.titulo = ttitulo;
        this.isbn = isbn;
        this.genero = genero;
        this.fecha = fecha;
        this.autor = autor;
        this.cantidad = 1;
    }

    //getters
    public int getCantidad() { return cantidad; }
    public String getTitulo() { return titulo; }
    public String getIsbn() { return isbn; }
    public String getGenero() { return genero; }
    public String getFecha() { return fecha; }
    public String getAutor() { return autor; }

    //setters
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setGenero(String genero) { this.genero = genero; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setAutor(String autor) { this.autor = autor; }

    //incrementar ejemplares
    public void incrementarCantidad() { this.cantidad++; }

    public String toString() {
        return "Titulo: " + (titulo.isEmpty() ? "<vacío>" : titulo) +
                " | ISBN: " + (isbn.isEmpty() ? "<vacío>" : isbn) +
                " | Genero: " + (genero.isEmpty() ? "<vacío>" : genero) +
                " | Fecha: " + (fecha.isEmpty() ? "<vacío>" : fecha) +
                " | Autor: " + (autor.isEmpty() ? "<vacío>" : autor) +
                " | Ejemplares: " + cantidad;
    }

    // Comparaciones
    public int compararPorTitulo(Libro otro) {
        return this.titulo.compareTo(otro.titulo);
    }

    public int compararPorIsbn(Libro otro) {
        return this.isbn.compareTo(otro.isbn);
    }

    public int compararPorFecha(Libro otro) {
        try {
            int anio1 = this.fecha.isEmpty() ? 0 : Integer.parseInt(this.fecha);
            int anio2 = otro.fecha.isEmpty() ? 0 : Integer.parseInt(otro.fecha);
            return anio1 - anio2;
        } catch (NumberFormatException e) {
            return this.fecha.compareTo(otro.fecha);
        }
    }

    // Fecha como entero
    public int getFechaInt() {
        try {
            return this.fecha.isEmpty() ? 0 : Integer.parseInt(this.fecha);
        } catch (NumberFormatException e) {
            System.err.println("ERROR convirtiendo fecha: '" + fecha + "'");
            return 0;
        }
    }

}

