package org.example.Grafo;

import org.example.Modelos.Biblioteca;
import org.example.TablaHash.*;

import java.util.ArrayList;
import java.util.List;

public class GrafoBibliotecas {
    private TablaHash<String, Vertice> vertices;

    public GrafoBibliotecas() {
        this.vertices = new TablaHash<>();
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

    public TablaHash<String, Biblioteca> getBibliotecas() {
        TablaHash<String, Biblioteca> bibliotecas = new TablaHash<>();
        Iterador<Vertice> iterador = vertices.iteradorValores();

        int contador = 0;
        while (iterador.tieneSiguiente()) {
            Vertice vertice = iterador.siguiente();
            bibliotecas.put(vertice.getId(), vertice.getBiblioteca());
            contador++;
        }
        return bibliotecas;
    }

    public boolean existeBiblioteca(String id) {
        return vertices.containsKey(id);
    }

    // Métodos para gestionar conexiones
    public boolean conectarBibliotecas(String idOrigen, String idDestino, int tiempo, double costo) {
        // Validar que existan ambos vértices
        if (!existeBiblioteca(idOrigen) || !existeBiblioteca(idDestino)) {
            return false;
        }

        // Validar que no exista ya la conexión
        if (estanConectadas(idOrigen, idDestino)) {
            return false;
        }

        Vertice origen = vertices.get(idOrigen);
        Arista arista = new Arista(idOrigen, idDestino, tiempo, costo);
        origen.agregarConexion(arista);
        return true;
    }

    public ListaAdyacencia getTodasLasAristas() {
        ListaAdyacencia todasAristas = new ListaAdyacencia();
        Iterador<Vertice> iterador = vertices.iteradorValores();

        while (iterador.tieneSiguiente()) {
            Vertice vertice = iterador.siguiente();
            ListaAdyacencia.IteradorLista iteradorAristas = vertice.getConexionesSalientes().iterador();

            while (iteradorAristas.tieneSiguiente()) {
                todasAristas.agregar(iteradorAristas.siguiente());
            }
        }
        return todasAristas;
    }

    public ListaAdyacencia getConexionesSalientes(String idBiblioteca) {
        Vertice vertice = vertices.get(idBiblioteca);
        return (vertice != null) ? vertice.getConexionesSalientes() : new ListaAdyacencia();
    }

    // Método para verificar conexión entre bibliotecas
    public boolean estanConectadas(String idOrigen, String idDestino) {
        Vertice origen = vertices.get(idOrigen);
        if (origen == null) return false;

        ListaAdyacencia.IteradorLista iterador = origen.getConexionesSalientes().iterador();
        while (iterador.tieneSiguiente()) {
            Arista arista = iterador.siguiente();
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

        Iterador<Vertice> iterador = vertices.iteradorValores();
        while (iterador.tieneSiguiente()) {
            Vertice vertice = iterador.siguiente();
            sb.append("- ").append(vertice.getId())
                    .append(" tiene ").append(vertice.getConexionesSalientes().getTamaño())
                    .append(" conexiones salientes\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "GrafoBibliotecas{" +
                "vertices=" + vertices.size() +
                ", aristas=" + getTodasLasAristas().getTamaño() +
                '}';
    }

    //metodos para la eliminacion de conexiones
    public boolean eliminarConexion(String idOrigen, String idDestino) {
        if (!existeBiblioteca(idOrigen)) {
            return false;
        }
        Vertice origen = vertices.get(idOrigen);
        return origen.eliminarConexion(idDestino);
    }
    public List<Arista> getConexionesSalientesList(String idBiblioteca) {
        List<Arista> conexiones = new ArrayList<>();
        Vertice vertice = vertices.get(idBiblioteca);

        if (vertice != null) {
            ListaAdyacencia.IteradorLista iterador = vertice.getConexionesSalientes().iterador();
            while (iterador.tieneSiguiente()) {
                conexiones.add(iterador.siguiente());
            }
        }

        return conexiones;
    }
}
