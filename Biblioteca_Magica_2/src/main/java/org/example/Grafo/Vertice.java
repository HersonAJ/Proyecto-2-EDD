package org.example.Grafo;

import org.example.Modelos.Biblioteca;
import java.util.ArrayList;
import java.util.List;

public class Vertice {
    private Biblioteca biblioteca;
    private List<Arista> conexionesSalientes;

    public Vertice(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        this.conexionesSalientes = new ArrayList<>();
    }

    // Getters simples
    public Biblioteca getBiblioteca() { return biblioteca; }
    public List<Arista> getConexionesSalientes() { return conexionesSalientes; }

    public void agregarConexion(Arista arista) {
        conexionesSalientes.add(arista);
    }

    public String getId() {
        return biblioteca.getId();
    }

    @Override
    public String toString() {
        return "Vertice: " + biblioteca.getId() + " [Conexiones: " + conexionesSalientes.size() + "]";
    }
}