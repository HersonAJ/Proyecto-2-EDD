package org.example.Modelos;

import org.example.Grafo.GrafoBibliotecas;
import java.util.*;
import java.util.concurrent.*;

public class CoordinadorEnvios {
    private GrafoBibliotecas grafo;
    private ScheduledExecutorService scheduler;

    public CoordinadorEnvios(GrafoBibliotecas grafo) {
        this.grafo = grafo;
        this.scheduler = Executors.newScheduledThreadPool(10); // hasta 10 tareas simultáneas
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

        // Encolar en cola de ingreso de biblioteca origen
        Biblioteca origen = grafo.getBiblioteca(idOrigen);
        origen.getColaIngreso().encolar(libro);
        System.out.println("📦 Libro '" + libro.getTitulo() + "' encolado en Ingreso de " + idOrigen);

        // Programar procesamiento de ingreso
        programarProcesamientoIngreso(origen, libro);

        return true;
    }

    // PROGRAMADORES DE EVENTOS DE COLAS
    private void programarProcesamientoIngreso(Biblioteca biblioteca, Libro libro) {
        long tiempo = biblioteca.getColaIngreso().getTiempoProcesamiento();
        scheduler.schedule(() -> procesarIngreso(biblioteca, libro), tiempo, TimeUnit.MILLISECONDS);
    }

    private void programarProcesamientoTraspaso(Biblioteca biblioteca, Libro libro) {
        long tiempo = biblioteca.getColaTraspaso().getTiempoProcesamiento();
        scheduler.schedule(() -> procesarTraspaso(biblioteca, libro), tiempo, TimeUnit.MILLISECONDS);
    }

    private void programarProcesamientoSalida(Biblioteca biblioteca, Libro libro) {
        long tiempo = biblioteca.getColaSalida().getTiempoProcesamiento();
        scheduler.schedule(() -> procesarSalida(biblioteca, libro), tiempo, TimeUnit.MILLISECONDS);
    }

    // ETAPAS DE PROCESAMIENTO
    private void procesarIngreso(Biblioteca biblioteca, Libro libro) {
        notificar("📥 " + libro.getTitulo() + " pasó a Traspaso en " + biblioteca.getId());
        biblioteca.getColaTraspaso().encolar(libro);
        programarProcesamientoTraspaso(biblioteca, libro);
    }

    private void procesarTraspaso(Biblioteca biblioteca, Libro libro) {
        // VERIFICAR SI ES EL DESTINO FINAL
        if (biblioteca.getId().equals(libro.getIdBibliotecaDestino())) {
            // ES EL DESTINO FINAL - Registrar directamente sin pasar por Salida
            notificar("📚 " + libro.getTitulo() + " llegó a destino final en " + biblioteca.getId());
            biblioteca.agregarLibro(libro);
            libro.setEstado("Disponible");
            System.out.println("Libro '" + libro.getTitulo() + "' registrado en destino final " + biblioteca.getId());
        } else {
            // NO es destino final - continuar flujo normal
            notificar("🚚 " + libro.getTitulo() + " pasó a Salida en " + biblioteca.getId());
            biblioteca.getColaSalida().encolar(libro);
            programarProcesamientoSalida(biblioteca, libro);
        }
    }

    private void procesarSalida(Biblioteca biblioteca, Libro libro) {
        notificar("🚀 " + libro.getTitulo() + " salió de " + biblioteca.getId());
        biblioteca.getColaSalida().desencolar(); // procesado

        System.out.println("=== DEBUG PROCESAR SALIDA ===");
        System.out.println("Biblioteca actual: " + biblioteca.getId());
        System.out.println("Destino final: " + libro.getIdBibliotecaDestino());
        System.out.println("Siguiente biblioteca: " + libro.getSiguienteBiblioteca());
        System.out.println("Es destino final: " + libro.esDestinoFinal());

        // Verificar si la siguiente biblioteca es el destino final
        String siguienteBiblioteca = libro.getSiguienteBiblioteca();

        if (siguienteBiblioteca == null) {
            System.out.println("📌 ENTRO EN CASO: siguienteBiblioteca == null");
            // Ya está en la última biblioteca de la ruta, registrar y terminar
            biblioteca.agregarLibro(libro);
            libro.setEstado("Disponible");
            System.out.println("✅ SE LLAMÓ A agregarLibro() en " + biblioteca.getId());
            notificar("📚 '" + libro.getTitulo() + "' llegó a su destino final " + biblioteca.getId());
            return;
        } else {
            System.out.println("📌 ENTRO EN CASO: Hay siguiente biblioteca - " + siguienteBiblioteca);
        }

        // Si hay siguiente biblioteca, mover sin avanzar primero
        // NO llamar a avanzarEnRuta() aquí - se hará después de procesar en la siguiente biblioteca
        moverLibroASiguienteBiblioteca(libro);
    }

    // MOVIMIENTO ENTRE BIBLIOTECAS
    private void moverLibroASiguienteBiblioteca(Libro libro) {
        System.out.println("=== DEBUG MOVER ===");
        System.out.println("Ruta actual del libro: " + libro.getRuta());
        System.out.println("Indice actual: " + libro.getIndiceRutaActual());
        System.out.println("Biblioteca actual: " + libro.getBibliotecaActual());
        System.out.println("Siguiente biblioteca: " + libro.getSiguienteBiblioteca());

        String siguienteId = libro.getSiguienteBiblioteca();

        // Si siguienteId es null, significa que ya estamos en la última biblioteca
        if (siguienteId == null) {
            System.out.println("📌 MOVER: siguienteId es NULL - marcando como destino final");

            // Registrar en la biblioteca actual (que es el destino final)
            Biblioteca bibliotecaActual = grafo.getBiblioteca(libro.getBibliotecaActual());
            if (bibliotecaActual != null) {
                bibliotecaActual.agregarLibro(libro);
                libro.setEstado("Disponible");
                System.out.println("✅ REGISTRADO en biblioteca actual: " + libro.getBibliotecaActual());
                notificar("📚 '" + libro.getTitulo() + "' llegó a su destino final " + libro.getBibliotecaActual());
            }
            return;
        }

        // Si hay siguiente biblioteca, encolar en su cola de ingreso
        Biblioteca siguiente = grafo.getBiblioteca(siguienteId);
        if (siguiente != null) {
            siguiente.getColaIngreso().encolar(libro);

            // AVANZAR LA RUTA SOLO DESPUÉS de encolar en la siguiente biblioteca
            libro.avanzarEnRuta();

            notificar("➡️ '" + libro.getTitulo() + "' llegó a " + siguiente.getId());
            programarProcesamientoIngreso(siguiente, libro);
        } else {
            System.err.println("⚠️ No se encontró la siguiente biblioteca en la ruta: " + siguienteId);
        }
    }

    // UTILITARIOS
    private List<String> calcularRutaOptima(String origen, String destino, String prioridad) {
        // luego reemplazar con Dijkstra
        if (grafo.estanConectadas(origen, destino))
            return Arrays.asList(origen, destino);
        return null;
    }

    private void configurarLibroParaEnvio(Libro libro, String origen, String destino, String prioridad, List<String> ruta) {
        libro.setIdBibliotecaOrigen(origen);
        libro.setIdBibliotecaDestino(destino);
        libro.setPrioridad(prioridad);
        libro.setRuta(ruta);
        libro.setIndiceRutaActual(0);
        libro.setEstado("En tránsito");
    }

    // Llamar al final del programa
    public void apagar() {
        scheduler.shutdownNow();
    }

    private List<EnvioListener> listeners = new ArrayList<>();

    public void agregarListener(EnvioListener listener) {
        listeners.add(listener);
    }

    private void notificar(String mensaje) {
        for (EnvioListener l : listeners) {
            l.onEvento(mensaje);
        }
    }

}