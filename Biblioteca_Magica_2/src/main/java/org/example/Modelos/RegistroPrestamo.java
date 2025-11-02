package org.example.Modelos;

import java.util.Date;

public class RegistroPrestamo {
    private String isbn;
    private String bibliotecaDestino;
    private String titulo;
    private Date fechaPrestamo;

    public RegistroPrestamo(String isbn, String bibliotecaDestino, String titulo, Date fechaPrestamo) {
        this.isbn = isbn;
        this.bibliotecaDestino = bibliotecaDestino;
        this.titulo = titulo;
        this.fechaPrestamo = fechaPrestamo;
    }

    //getters
    public String getIsbn() { return isbn; }
    public String getBibliotecaDestino() { return bibliotecaDestino; }
    public String getTitulo() { return titulo; }
    public Date getFechaPrestamo() { return fechaPrestamo; }
}
