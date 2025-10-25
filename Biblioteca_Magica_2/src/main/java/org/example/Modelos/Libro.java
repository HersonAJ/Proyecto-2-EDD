package org.example.Modelos;

import java.util.ArrayList;
import java.util.List;

public class Libro {

    private int cantidad;
    private String titulo;
    private String isbn;
    private String genero;
    private String fecha;
    private String autor;

    //nuevos campos
    private String estado;
    private String idBibliotecaOrigen;
    private String idBibliotecaDestino;
    private String prioridad;
    private List<String> ruta;
    private int indiceRutaActual;

    //constructor vacio
    public Libro() {
        this.titulo = "";
        this.isbn = "";
        this.genero = "";
        this.fecha = "";
        this.autor = "";
        this.cantidad = 1;
        this.estado = "Disponible";
        this.idBibliotecaOrigen = null;
        this.idBibliotecaDestino = null;
        this.prioridad = null;
        this.ruta = null;
        this.indiceRutaActual = 0;
    }

    //constructor con parametros
    public Libro(String titulo, String isbn, String genero, String fecha, String autor) {
        this();
        this.titulo = titulo;
        this.isbn = isbn;
        this.genero = genero;
        this.fecha = fecha;
        this.autor = autor;
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


    // === GETTERS para nuevos campos ===
    public String getEstado() { return estado; }
    public String getIdBibliotecaOrigen() { return idBibliotecaOrigen; }
    public String getIdBibliotecaDestino() { return idBibliotecaDestino; }
    public String getPrioridad() { return prioridad; }
    public List<String> getRuta() { return ruta; }
    public int getIndiceRutaActual() { return indiceRutaActual; }

    // === SETTERS para nuevos campos ===
    public void setEstado(String estado) { this.estado = estado; }
    public void setIdBibliotecaOrigen(String idBibliotecaOrigen) { this.idBibliotecaOrigen = idBibliotecaOrigen; }
    public void setIdBibliotecaDestino(String idBibliotecaDestino) { this.idBibliotecaDestino = idBibliotecaDestino; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }
    public void setRuta(List<String> ruta) { this.ruta = ruta; }
    public void setIndiceRutaActual(int indiceRutaActual) { this.indiceRutaActual = indiceRutaActual; }

// === MÉTODOS DE UTILIDAD para manejo de rutas ===


    //Obtiene la biblioteca actual en la ruta basado en indiceRutaActual
    public String getBibliotecaActual() {
        if (ruta == null || ruta.isEmpty() || indiceRutaActual < 0 || indiceRutaActual >= ruta.size()) {
            return null;
        }
        return ruta.get(indiceRutaActual);
    }


     //Obtiene la siguiente biblioteca en la ruta
    public String getSiguienteBiblioteca() {
        if (ruta == null || indiceRutaActual < 0 || indiceRutaActual >= ruta.size() - 1) {
            return null;
        }
        return ruta.get(indiceRutaActual + 1);
    }


     //Avanza a la siguiente biblioteca en la ruta @return true si avanzó, false si ya está en la última
    public boolean avanzarEnRuta() {
        if (getSiguienteBiblioteca() != null) {
            indiceRutaActual++;
            return true;
        }
        return false;
    }

     //Verifica si el libro llegó a su destino final
    public boolean esDestinoFinal() {
        if (ruta == null || ruta.isEmpty()) return false;
        return indiceRutaActual == ruta.size() - 1;
    }


     //Verifica si el libro está en tránsito
    public boolean estaEnTransito() {
        return "En tránsito".equals(estado);
    }

    @Override
    public String toString() {
        return "Titulo: " + (titulo.isEmpty() ? "<vacío>" : titulo) +
                " | ISBN: " + (isbn.isEmpty() ? "<vacío>" : isbn) +
                " | Genero: " + (genero.isEmpty() ? "<vacío>" : genero) +
                " | Fecha: " + (fecha.isEmpty() ? "<vacío>" : fecha) +
                " | Autor: " + (autor.isEmpty() ? "<vacío>" : autor) +
                " | Estado: " + (estado != null ? estado : "No asignado") +
                " | Origen: " + (idBibliotecaOrigen != null ? idBibliotecaOrigen : "No asignado") +
                " | Destino: " + (idBibliotecaDestino != null ? idBibliotecaDestino : "No asignado") +
                " | Ejemplares: " + cantidad;
    }
}

