package org.example.Grafo;

import java.util.*;
import org.example.Modelos.Biblioteca;

public class GrafoBibliotecas {
    private Map<String, Vertice> vertices;

    public GrafoBibliotecas() {
        this.vertices = new HashMap<>();
    }

    // Métodos para gestionar bibliotecas
    public boolean agregarBiblioteca(String id, String nombre, String ubicacion,
                                     int tiempoIngreso, int tiempoTraspaso, int intervaloDespacho) {

        // Validar si ya existe
        if (existeBiblioteca(id)) {
            return false; // No se agregó porque ya existe
        }

        Biblioteca biblioteca = new Biblioteca(id, nombre, ubicacion, tiempoIngreso, tiempoTraspaso, intervaloDespacho);
        vertices.put(id, new Vertice(biblioteca));
        return true; // Se agregó exitosamente
    }

    public Biblioteca getBiblioteca(String id) {
        Vertice vertice = vertices.get(id);
        return (vertice != null) ? vertice.getBiblioteca() : null;
    }

    public Map<String, Biblioteca> getBibliotecas() {
        Map<String, Biblioteca> bibliotecas = new HashMap<>();
        for (Vertice vertice : vertices.values()) {
            bibliotecas.put(vertice.getId(), vertice.getBiblioteca());
        }
        return bibliotecas;
    }

    public boolean existeBiblioteca(String id) {
        return vertices.containsKey(id);
    }

    // Métodos para gestionar conexiones
    public void conectarBibliotecas(String idOrigen, String idDestino, int tiempo, double costo) {
        Vertice origen = vertices.get(idOrigen);
        Vertice destino = vertices.get(idDestino);

        if (origen != null && destino != null) {
            Arista arista = new Arista(idOrigen, idDestino, tiempo, costo);
            origen.agregarConexion(arista);
        }
    }

    public List<Arista> getTodasLasAristas() {
        List<Arista> todasAristas = new ArrayList<>();
        for (Vertice vertice : vertices.values()) {
            todasAristas.addAll(vertice.getConexionesSalientes());
        }
        return todasAristas;
    }

    public List<Arista> getConexionesSalientes(String idBiblioteca) {
        Vertice vertice = vertices.get(idBiblioteca);
        return (vertice != null) ? vertice.getConexionesSalientes() : new ArrayList<>();
    }

    // Método para verificar conexión entre bibliotecas
    public boolean estanConectadas(String idOrigen, String idDestino) {
        Vertice origen = vertices.get(idOrigen);
        if (origen == null) return false;

        for (Arista arista : origen.getConexionesSalientes()) {
            if (arista.getIdDestino().equals(idDestino)) {
                return true;
            }
        }
        return false;
    }

    // Método para obtener información del grafo
    public String obtenerInfoGrafo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grafo con ").append(vertices.size()).append(" bibliotecas\n");

        for (Vertice vertice : vertices.values()) {
            sb.append("- ").append(vertice.getId())
                    .append(" tiene ").append(vertice.getConexionesSalientes().size())
                    .append(" conexiones salientes\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "GrafoBibliotecas{" +
                "vertices=" + vertices.size() +
                ", aristas=" + getTodasLasAristas().size() +
                '}';
    }
}
