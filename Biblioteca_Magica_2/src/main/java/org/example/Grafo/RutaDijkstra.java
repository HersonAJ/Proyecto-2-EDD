package org.example.Grafo;

import org.example.Modelos.Biblioteca;
import org.example.TablaHash.Iterador;
import org.example.TablaHash.TablaHash;

import java.util.*;

public class RutaDijkstra {

    public enum Criterio { TIEMPO, COSTO }

    // Clase interna para almacenar información de cada vértice durante el algoritmo
    private static class InfoVertice implements Comparable<InfoVertice> {
        String id;
        double distancia;
        String anterior;

        InfoVertice(String id) {
            this.id = id;
            this.distancia = Double.MAX_VALUE;
            this.anterior = null;
        }

        @Override
        public int compareTo(InfoVertice otro) {
            return Double.compare(this.distancia, otro.distancia);
        }
    }

    public static List<String> calcularRuta(GrafoBibliotecas grafo,
                                            String origen, String destino,
                                            Criterio criterio) {
        // Validaciones básicas
        if (!grafo.existeBiblioteca(origen) || !grafo.existeBiblioteca(destino)) {
            System.err.println("❌ Dijkstra: Bibliotecas origen o destino no existen");
            return null;
        }

        if (origen.equals(destino)) {
            return Arrays.asList(origen);
        }

        // Estructuras para el algoritmo
        Map<String, InfoVertice> infoVertices = new HashMap<>();
        PriorityQueue<InfoVertice> colaPrioridad = new PriorityQueue<>();

        // Inicializar todos los vértices
        TablaHash<String, Biblioteca> bibliotecas = grafo.getBibliotecas();
        Iterador<Biblioteca> iter = bibliotecas.iteradorValores();
        while (iter.tieneSiguiente()) {
            String id = iter.siguiente().getId();
            InfoVertice info = new InfoVertice(id);
            infoVertices.put(id, info);
        }

        // Inicializar vértice origen
        InfoVertice infoOrigen = infoVertices.get(origen);
        infoOrigen.distancia = 0;
        colaPrioridad.offer(infoOrigen);

        // Algoritmo de Dijkstra
        while (!colaPrioridad.isEmpty()) {
            InfoVertice actual = colaPrioridad.poll();

            // Si llegamos al destino, podemos terminar
            if (actual.id.equals(destino)) {
                break;
            }

            // Si la distancia actual es infinita, no hay camino
            if (actual.distancia == Double.MAX_VALUE) {
                break;
            }

            // Procesar vecinos
            ListaAdyacencia conexiones = grafo.getConexionesSalientes(actual.id);
            ListaAdyacencia.IteradorLista iterAristas = conexiones.iterador();

            while (iterAristas.tieneSiguiente()) {
                Arista arista = iterAristas.siguiente();
                String vecinoId = arista.getIdDestino();

                InfoVertice infoVecino = infoVertices.get(vecinoId);
                if (infoVecino == null) continue;

                double peso = obtenerPeso(arista, criterio);
                double distanciaAlternativa = actual.distancia + peso;

                if (distanciaAlternativa < infoVecino.distancia) {
                    // Actualizar distancia y vértice anterior
                    colaPrioridad.remove(infoVecino);
                    infoVecino.distancia = distanciaAlternativa;
                    infoVecino.anterior = actual.id;
                    colaPrioridad.offer(infoVecino);
                }
            }
        }

        // Reconstruir ruta desde el destino
        return reconstruirRuta(infoVertices, origen, destino);
    }

    private static double obtenerPeso(Arista arista, Criterio criterio) {
        return criterio == Criterio.TIEMPO ? arista.getTiempo() : arista.getCosto();
    }

    private static List<String> reconstruirRuta(Map<String, InfoVertice> infoVertices,
                                                String origen, String destino) {
        List<String> ruta = new ArrayList<>();

        InfoVertice actual = infoVertices.get(destino);

        // Si no hay camino al destino
        if (actual == null || actual.distancia == Double.MAX_VALUE) {
            System.err.println("❌ Dijkstra: No hay camino de " + origen + " a " + destino);
            return null;
        }

        // Reconstruir ruta desde destino hasta origen
        String nodoActual = destino;
        while (nodoActual != null) {
            ruta.add(0, nodoActual); // Insertar al inicio
            InfoVertice info = infoVertices.get(nodoActual);
            nodoActual = info.anterior;
        }

        // Verificar que la ruta comienza en el origen
        if (!ruta.get(0).equals(origen)) {
            System.err.println("❌ Dijkstra: Ruta reconstruida no comienza en origen");
            return null;
        }

        System.out.println("✅ Dijkstra: Ruta encontrada " + ruta +
                " (distancia: " + infoVertices.get(destino).distancia + ")");
        return ruta;
    }

    // Método para debug del algoritmo
    public static void debugRuta(GrafoBibliotecas grafo, String origen, String destino) {
        System.out.println("\n=== DEBUG DIJKSTRA ===");
        System.out.println("Origen: " + origen + ", Destino: " + destino);

        List<String> rutaTiempo = calcularRuta(grafo, origen, destino, Criterio.TIEMPO);
        List<String> rutaCosto = calcularRuta(grafo, origen, destino, Criterio.COSTO);

        System.out.println("Ruta por TIEMPO: " + rutaTiempo);
        System.out.println("Ruta por COSTO: " + rutaCosto);

        // Mostrar diferencias
        if (rutaTiempo != null && rutaCosto != null && !rutaTiempo.equals(rutaCosto)) {
            System.out.println("⚠️  Las rutas son DIFERENTES según el criterio");
        }
        System.out.println("=====================\n");
    }
}