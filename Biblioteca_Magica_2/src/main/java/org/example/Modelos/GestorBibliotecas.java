package org.example.Modelos;

import java.util.HashMap;
import java.util.Map;

public class GestorBibliotecas {
    private Map<String, Biblioteca> bibliotecas;

    public GestorBibliotecas() {
        this.bibliotecas = new HashMap<>();
    }

    public void agregarBiblioteca(String id, String nombre, String ubicacion,
                                  int tiempoIngreso, int tiempoTraspaso, int intervaloDespacho) {
        bibliotecas.put(id, new Biblioteca(id, nombre, ubicacion, tiempoIngreso, tiempoTraspaso, intervaloDespacho));
    }

    public Biblioteca getBiblioteca(String id) {
        return bibliotecas.get(id);
    }
}
