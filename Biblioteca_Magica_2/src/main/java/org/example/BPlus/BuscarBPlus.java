package org.example.BPlus;

import org.example.Modelos.ListaLibros;
import org.example.AVL_Auxiliar.NodoIndiceISBN;

public class BuscarBPlus {

    public ListaLibros buscarPorGenero(ArbolBPlus arbol, String genero) {
        try {
            if (genero == null || genero.isEmpty())
                throw new IllegalArgumentException("Género vacío en buscarPorGenero");

            ListaLibros resultados = new ListaLibros();

            InsertarBPlus insertar = new InsertarBPlus();
            NodoHoja hoja = insertar.buscarHoja(arbol.getRaiz(), genero);
            if (hoja == null) {
                System.err.println("Error: Hoja nula en buscarPorGenero para género: " + genero);
                return resultados; // Devolver lista vacía
            }

            for (int i = 0; i < hoja.numClaves; i++) {
                if (hoja.entradas[i].genero.equals(genero)) {
                    if (hoja.entradas[i].indiceISBN != null) {
                        recorrerAVLyAgregarLibros(hoja.entradas[i].indiceISBN.getRaiz(), resultados);
                    }
                    break;
                }
            }

            return resultados;
        } catch (Exception e) {
            System.err.println("Error en BuscarBPlus::buscarPorGenero: " + e.getMessage());
            return new ListaLibros(); // Devolver lista vacía en caso de error
        }
    }

    public void recorrerAVLyAgregarLibros(NodoIndiceISBN nodoAVL, ListaLibros resultados) {
        if (nodoAVL == null) return;

        try {
            recorrerAVLyAgregarLibros(nodoAVL.izquierdo, resultados);

            if (nodoAVL.libro == null) {
                throw new RuntimeException("Libro nulo encontrado en AVL durante recorrido");
            }

            resultados.insertar(nodoAVL.libro);
            recorrerAVLyAgregarLibros(nodoAVL.derecho, resultados);
        } catch (Exception e) {
            System.err.println("Error en BuscarBPlus::recorrerAVLyAgregarLibros: " + e.getMessage());
            throw e;
        }
    }

    public boolean buscarGeneroAux(ArbolBPlus arbol, String genero, NodoHoja[] hojaOut, int[] pos) {
        try {
            if (genero == null || genero.isEmpty())
                throw new IllegalArgumentException("Género vacío en buscarGeneroAux");
            if (hojaOut == null)
                throw new IllegalArgumentException("Array de salida nulo en buscarGeneroAux");

            // Localizar la hoja
            InsertarBPlus insertar = new InsertarBPlus();
            NodoHoja hoja = insertar.buscarHoja(arbol.getRaiz(), genero);
            if (hoja == null) {
                hojaOut[0] = null;
                pos[0] = -1;
                return false;
            }

            // Buscar el género dentro de la hoja
            for (int i = 0; i < hoja.numClaves; i++) {
                if (hoja.entradas[i].genero.equals(genero)) {
                    hojaOut[0] = hoja;
                    pos[0] = i;
                    return true;
                }
            }

            // No se encontró
            hojaOut[0] = null;
            pos[0] = -1;
            return false;
        } catch (Exception e) {
            System.err.println("Error en BuscarBPlus::buscarGeneroAux: " + e.getMessage());
            if (hojaOut != null) hojaOut[0] = null;
            if (pos != null) pos[0] = -1;
            return false;
        }
    }
}