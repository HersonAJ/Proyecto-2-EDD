package org.example.Grafo;

import org.example.Modelos.Biblioteca;

public class Vertice {
    private Biblioteca biblioteca;
    private ListaAdyacencia conexionesSalientes;

    public Vertice(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        this.conexionesSalientes = new ListaAdyacencia();
    }

    // Getters
    public Biblioteca getBiblioteca() {
        return biblioteca;
    }

    public ListaAdyacencia getConexionesSalientes() {
        return conexionesSalientes;
    }

    public void agregarConexion(Arista arista) {
        conexionesSalientes.agregar(arista);
    }

    public String getId() {
        return biblioteca.getId();
    }

    @Override
    public String toString() {
        return "Vertice: " + biblioteca.getId() + " [Conexiones: " + conexionesSalientes.getTamaño() + "]";
    }

    public boolean eliminarConexion(String idDestino) {
        ListaAdyacencia conexiones = getConexionesSalientes();
        ListaAdyacencia nuevasConexiones = new ListaAdyacencia();
        boolean encontrada = false;

        ListaAdyacencia.IteradorLista iterador = conexiones.iterador();
        while (iterador.tieneSiguiente()) {
            Arista arista = iterador.siguiente();
            if (!arista.getIdDestino().equals(idDestino)) {
                nuevasConexiones.agregar(arista);
            } else {
                encontrada = true;
            }
        }

        if (encontrada) {
            this.conexionesSalientes = nuevasConexiones;
        }

        return encontrada;
    }

    public void setConexionesSalientes(ListaAdyacencia conexiones) {
        this.conexionesSalientes = conexiones;
    }
}