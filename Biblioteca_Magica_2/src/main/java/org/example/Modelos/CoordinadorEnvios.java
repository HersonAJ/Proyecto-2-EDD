package org.example.Modelos;

import org.example.Grafo.GrafoBibliotecas;
import java.util.*;

public class CoordinadorEnvios {
    private GrafoBibliotecas grafo;
    private boolean simulacionActiva;

    public CoordinadorEnvios(GrafoBibliotecas grafo) {
        this.grafo = grafo;
        this.simulacionActiva = false;
    }

     //Inicia el envío de un libro desde una biblioteca origen a destino
    public boolean iniciarEnvioLibro(Libro libro, String idOrigen, String idDestino, String prioridad) {
        // Validar bibliotecas existentes
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

        // Encolar en biblioteca origen
        Biblioteca bibliotecaOrigen = grafo.getBiblioteca(idOrigen);
        bibliotecaOrigen.getColaIngreso().encolar(libro);

        System.out.println("📦 Libro '" + libro.getTitulo() + "' encolado en " + idOrigen +
                " con ruta: " + ruta);
        return true;
    }

     //Calcula la ruta óptima usando Dijkstra
    private List<String> calcularRutaOptima(String origen, String destino, String prioridad) {
        // Implementación simplificada - aquí iría Dijkstra completo
        // Por ahora retorna una ruta directa si existe conexión
        if (grafo.estanConectadas(origen, destino)) {
            return Arrays.asList(origen, destino);
        }

        // Buscar una ruta intermedia (simulación)
        // En la implementación real aquí iría el algoritmo de Dijkstra
        List<String> ruta = buscarRutaIntermedia(origen, destino);
        return ruta;
    }

     //Busca una ruta con bibliotecas intermedias (simulación temporal)
    private List<String> buscarRutaIntermedia(String origen, String destino) {
        // Esto es una simulación - la implementación real usaría Dijkstra
        // Revisar conexiones de origen
        var conexionesOrigen = grafo.getConexionesSalientes(origen);
        var iterador = conexionesOrigen.iterador();

        while (iterador.tieneSiguiente()) {
            var arista = iterador.siguiente();
            String intermedia = arista.getIdDestino();

            // Si la intermedia conecta con el destino
            if (grafo.estanConectadas(intermedia, destino)) {
                return Arrays.asList(origen, intermedia, destino);
            }
        }
        return null; // No se encontró ruta
    }

    //Configura el libro con toda la información del envío
    private void configurarLibroParaEnvio(Libro libro, String origen, String destino,
                                          String prioridad, List<String> ruta) {
        libro.setIdBibliotecaOrigen(origen);
        libro.setIdBibliotecaDestino(destino);
        libro.setPrioridad(prioridad);
        libro.setRuta(ruta);
        libro.setIndiceRutaActual(0);
        libro.setEstado("En tránsito");
    }

    //Mueve un libro a la siguiente biblioteca en su ruta
    public void moverLibroASiguienteBiblioteca(Libro libro) {
        if (libro.esDestinoFinal()) {
            // Llegó a su destino final
            String idDestino = libro.getIdBibliotecaDestino();
            Biblioteca bibliotecaDestino = grafo.getBiblioteca(idDestino);

            if (bibliotecaDestino != null) {
                // Encolar primero para procesamiento local
                bibliotecaDestino.getColaIngreso().encolar(libro);
                libro.setEstado("Pendiente de registro");
                System.out.println("🎯 Libro '" + libro.getTitulo() + "' llegó a destino (" + idDestino + ") y espera registro.");
            }
            return;
        }

        // Caso general: aún hay ruta por recorrer
        String siguienteBiblioteca = libro.getSiguienteBiblioteca();
        if (siguienteBiblioteca != null) {
            libro.avanzarEnRuta();
            Biblioteca bibliotecaDestino = grafo.getBiblioteca(siguienteBiblioteca);

            if (bibliotecaDestino != null) {
                bibliotecaDestino.getColaIngreso().encolar(libro);
                System.out.println("➡️ Libro '" + libro.getTitulo() + "' movido a: " + siguienteBiblioteca);
            }
        }
    }

    //Procesa todos los libros listos para salir de las colas de salida
    public void procesarDespachos() {
        var bibliotecas = grafo.getBibliotecas();
        var iterador = bibliotecas.iteradorValores();

        while (iterador.tieneSiguiente()) {
            Biblioteca bib = iterador.siguiente();
            procesarFlujoBiblioteca(bib);
        }
    }

    //Procesa los despachos de una biblioteca específica
    private void procesarDespachosBiblioteca(Biblioteca biblioteca) {
        Cola<Libro> colaSalida = biblioteca.getColaSalida();

        // Procesar todos los libros listos en cola salida
        Libro libro;
        while ((libro = colaSalida.procesarSiguiente()) != null) {
            System.out.println("🚀 Despachando libro '" + libro.getTitulo() +
                    "' desde " + biblioteca.getId());
            moverLibroASiguienteBiblioteca(libro);
        }
    }

    //Inicia la simulación continua de envíos
    public void iniciarSimulacion() {
        simulacionActiva = true;
        System.out.println("🔄 Iniciando simulación de envíos...");

        // En un sistema real, esto sería un thread separado
        new Thread(() -> {
            while (simulacionActiva) {
                try {
                    procesarDespachos();
                    Thread.sleep(1000); // Procesar cada segundo
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    //Detiene la simulación
    public void detenerSimulacion() {
        simulacionActiva = false;
        System.out.println("⏹️ Simulación detenida");
    }

    // Getters
    public GrafoBibliotecas getGrafo() { return grafo; }
    public boolean isSimulacionActiva() { return simulacionActiva; }

    private void procesarFlujoBiblioteca(Biblioteca biblioteca) {
        Cola<Libro> ingreso = biblioteca.getColaIngreso();
        Cola<Libro> traspaso = biblioteca.getColaTraspaso();
        Cola<Libro> salida = biblioteca.getColaSalida();

        Libro libro;

        // 🔄 ORDEN DE PRIORIDAD: Salida > Traspaso > Ingreso
        // (Para evitar cuellos de botella)

        // PRIORIDAD 1: Procesar SALIDA (libros listos para enviar)
        if (salida.puedeProcesar() && !salida.estaVacia()) {
            libro = salida.procesarSiguiente();
            if (libro != null) {
                System.out.println("🚀 Despachando libro '" + libro.getTitulo() + "' desde " + biblioteca.getId());
                moverLibroASiguienteBiblioteca(libro);
                return; // ✅ Solo procesar UNA operación por ciclo
            }
        }

        // PRIORIDAD 2: Procesar TRASPASO (libros en preparación)
        if (traspaso.puedeProcesar() && !traspaso.estaVacia()) {
            libro = traspaso.procesarSiguiente();
            if (libro != null) {
                if (libro.esDestinoFinal() && libro.getIdBibliotecaDestino().equals(biblioteca.getId())) {
                    // Destino final: agregar al catálogo
                    biblioteca.agregarLibro(libro);
                    libro.setEstado("Disponible");
                    System.out.println("📚 Libro '" + libro.getTitulo() + "' REGISTRADO en " + biblioteca.getId());
                } else {
                    // Biblioteca intermedia: enviar a salida
                    salida.encolar(libro);
                    System.out.println("🚪 Libro '" + libro.getTitulo() + "' listo para salir de " + biblioteca.getId());
                }
                return; // ✅ Solo procesar UNA operación por ciclo
            }
        }

        // PRIORIDAD 3: Procesar INGRESO (libros recién llegados)
        if (ingreso.puedeProcesar() && !ingreso.estaVacia()) {
            libro = ingreso.procesarSiguiente();
            if (libro != null) {
                traspaso.encolar(libro);
                System.out.println("📥 Libro '" + libro.getTitulo() + "' en preparación en " + biblioteca.getId());
                return; // ✅ Solo procesar UNA operación por ciclo
            }
        }
    }


}