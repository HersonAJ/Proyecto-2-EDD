package org.example.Modelos;

import org.example.Grafo.GrafoBibliotecas;
import org.example.Grafo.RutaDijkstra;

import java.util.*;

public class CoordinadorEnvios {
    private GrafoBibliotecas grafo;
    private List<EnvioListener> listeners = new ArrayList<>();
    private List<Libro> librosEnTransito = new ArrayList<>();

    public CoordinadorEnvios(GrafoBibliotecas grafo) {
        this.grafo = grafo;
    }

    // Inicia el envío de un libro desde origen a destino
    public boolean iniciarEnvioLibro(Libro libro, String idOrigen, String idDestino, String prioridad) {
        if (!grafo.existeBiblioteca(idOrigen) || !grafo.existeBiblioteca(idDestino)) {
            System.err.println("Error: Bibliotecas origen o destino no existen");
            return false;
        }

        // Calcular ruta óptima
        List<String> ruta = calcularRutaOptima(idOrigen, idDestino, prioridad);
        if (ruta == null || ruta.isEmpty()) {
            System.err.println("Error: No se pudo calcular ruta entre " + idOrigen + " y " + idDestino);
            return false;
        }

        // Configurar libro para el envío
        configurarLibroParaEnvio(libro, idOrigen, idDestino, prioridad, ruta);

        notificar("🚀 ENVÍO INICIADO: " + libro.getTitulo() + " en " + idOrigen + " - Cola Ingreso");

        // Configurar procesadores de colas de la biblioteca origen (si no están configurados aún)
        configurarProcesadoresBiblioteca(grafo.getBiblioteca(idOrigen));

        // Encolar en cola de ingreso de biblioteca origen
        Biblioteca origen = grafo.getBiblioteca(idOrigen);
        origen.getColaIngreso().encolar(libro);
        System.out.println("📦 Libro '" + libro.getTitulo() + "' encolado en Ingreso de " + idOrigen);
        librosEnTransito.add(libro);

        return true;
    }

    // Configura los procesadores de las colas de una biblioteca
    private void configurarProcesadoresBiblioteca(Biblioteca biblioteca) {
        // Cola de Ingreso
        biblioteca.getColaIngreso().setProcesador((libro, tipo) -> {
            notificar("📥 " + libro.getTitulo() + " pasó a Traspaso en " + biblioteca.getId());
            biblioteca.getColaTraspaso().encolar(libro);
        });
        biblioteca.getColaIngreso().iniciarProcesamiento();

        // Cola de Traspaso
        biblioteca.getColaTraspaso().setProcesador((libro, tipo) -> {
            if (biblioteca.getId().equals(libro.getIdBibliotecaDestino())) {
                // Destino final
                notificar("📚 " + libro.getTitulo() + " llegó a destino final en " + biblioteca.getId());
                biblioteca.agregarLibro(libro);
                libro.setEstado("Disponible");
            } else {
                // Continuar a salida
                notificar("🚚 " + libro.getTitulo() + " pasó a Salida en " + biblioteca.getId());
                biblioteca.getColaSalida().encolar(libro);
            }
        });
        biblioteca.getColaTraspaso().iniciarProcesamiento();

        // Cola de Salida
        biblioteca.getColaSalida().setProcesador((libro, tipo) -> {
            notificar("🚀 " + libro.getTitulo() + " salió de " + biblioteca.getId());

            String siguienteBiblioteca = libro.getSiguienteBiblioteca();
            if (siguienteBiblioteca == null) {
                // Última biblioteca
                biblioteca.agregarLibro(libro);
                libro.setEstado("Disponible");
                notificar("📚 '" + libro.getTitulo() + "' llegó a su destino final " + biblioteca.getId());
            } else {
                // Mover a la siguiente biblioteca
                moverLibroASiguienteBiblioteca(libro);
            }
        });
        biblioteca.getColaSalida().iniciarProcesamiento();
    }

    // MOVIMIENTO ENTRE BIBLIOTECAS
    private void moverLibroASiguienteBiblioteca(Libro libro) {
        String siguienteId = libro.getSiguienteBiblioteca();
        Biblioteca siguiente = grafo.getBiblioteca(siguienteId);

        if (siguiente != null) {
            configurarProcesadoresBiblioteca(siguiente); // asegurar que tenga procesadores
            siguiente.getColaIngreso().encolar(libro);
            libro.avanzarEnRuta();
            notificar("➡️ '" + libro.getTitulo() + "' llegó a " + siguiente.getId());
        } else {
            System.err.println("⚠️ No se encontró la siguiente biblioteca en la ruta: " + siguienteId);
        }
    }

    // UTILITARIOS
    private List<String> calcularRutaOptima(String origen, String destino, String prioridad) {
        RutaDijkstra.Criterio criterio = prioridad.equals("costo") ? RutaDijkstra.Criterio.COSTO : RutaDijkstra.Criterio.TIEMPO;
        return RutaDijkstra.calcularRuta(grafo, origen, destino, criterio);
    }

    private void configurarLibroParaEnvio(Libro libro, String origen, String destino, String prioridad, List<String> ruta) {
        libro.setIdBibliotecaOrigen(origen);
        libro.setIdBibliotecaDestino(destino);
        libro.setPrioridad(prioridad);
        libro.setRuta(ruta);
        libro.setIndiceRutaActual(0);
        libro.setEstado("En tránsito");
    }

    // Listeners
    public void agregarListener(EnvioListener listener) {
        listeners.add(listener);
    }

    private void notificar(String mensaje) {
        for (EnvioListener l : listeners) {
            l.onEvento(mensaje);
        }
    }
    public List<Libro> getLibrosEnTransito() { return new ArrayList<>(librosEnTransito); }
}
