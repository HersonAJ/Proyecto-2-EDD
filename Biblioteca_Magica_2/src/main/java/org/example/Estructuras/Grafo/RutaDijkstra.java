package org.example.Estructuras.Grafo;

import org.example.Modelos.Biblioteca;
import org.example.Estructuras.TablaHash.Iterador;
import org.example.Estructuras.TablaHash.TablaHash;

import java.util.*;

public class RutaDijkstra {

    public enum Criterio { TIEMPO, COSTO }

    public static List<String> calcularRuta(GrafoBibliotecas grafo, String origen, String destino, Criterio criterio) {

        Map<String, Double> distancias = new HashMap<>();
        Map<String, String> anteriores = new HashMap<>();
        Set<String> visitados = new HashSet<>();

        // Inicialización
        TablaHash<String, Biblioteca> bibliotecas = grafo.getBibliotecas();
        Iterador<Biblioteca> iter = bibliotecas.iteradorValores();
        while (iter.tieneSiguiente()) {
            String id = iter.siguiente().getId();
            distancias.put(id, Double.MAX_VALUE);
            anteriores.put(id, null);
        }
        distancias.put(origen, 0.0);

        // Dijkstra con arreglos
        int totalVertices = bibliotecas.tamano();

        for (int i = 0; i < totalVertices; i++) {
            // PASO 2: Elegir vértice con distancia mínima no visitado
            String actual = null;
            double minDistancia = Double.MAX_VALUE;

            Iterador<Biblioteca> iter2 = bibliotecas.iteradorValores();
            while (iter2.tieneSiguiente()) {
                String id = iter2.siguiente().getId();
                if (!visitados.contains(id) && distancias.get(id) < minDistancia) {
                    minDistancia = distancias.get(id);
                    actual = id;
                }
            }

            if (actual == null || actual.equals(destino)) break;
            visitados.add(actual);

            // PASO 2.1: Actualizar vecinos (usando  ListaAdyacencia)
            ListaAdyacencia conexiones = grafo.getConexionesSalientes(actual);
            ListaAdyacencia.IteradorLista iterAristas = conexiones.iterador();

            while (iterAristas.tieneSiguiente()) {
                Arista arista = iterAristas.siguiente();
                String vecino = arista.getIdDestino();

                if (!visitados.contains(vecino)) {
                    double peso = obtenerPeso(arista, criterio);
                    double nuevaDistancia = distancias.get(actual) + peso;

                    if (nuevaDistancia < distancias.get(vecino)) {
                        distancias.put(vecino, nuevaDistancia);
                        anteriores.put(vecino, actual);
                    }
                }
            }
        }

        return reconstruirRuta(anteriores, origen, destino, criterio);
    }

    private static double obtenerPeso(Arista arista, Criterio criterio) {
        return criterio == Criterio.TIEMPO ? arista.getTiempo() : arista.getCosto();
    }

    private static List<String> reconstruirRuta(Map<String, String> anteriores,
                                                String origen, String destino,
                                                Criterio criterio) {
        List<String> ruta = new ArrayList<>();

        // Reconstruir ruta desde destino hasta origen
        String nodoActual = destino;

        while (nodoActual != null) {
            ruta.add(0, nodoActual); // Insertar al inicio
            nodoActual = anteriores.get(nodoActual);
        }

        // Verificar que la ruta comienza en el origen
        if (ruta.isEmpty() || !ruta.get(0).equals(origen)) {
            System.err.println("Dijkstra: No hay camino de " + origen + " a " + destino);
            return null;
        }

        // Mensaje informativo
        String unidad = (criterio == Criterio.TIEMPO) ? "segundos" : "unidades de costo";
        return ruta;
    }

}