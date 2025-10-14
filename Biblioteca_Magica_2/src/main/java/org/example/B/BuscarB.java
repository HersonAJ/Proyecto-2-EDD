package org.example.B;

import org.example.Modelos.ListaLibros;
import org.example.AVL_Auxiliar.NodoIndiceISBN;

public class BuscarB {

    public ListaLibros buscarPorRango(NodoB raiz, int fechaInicio, int fechaFin) {
        try {
            if (fechaInicio > fechaFin) {
                throw new IllegalArgumentException("Fecha inicio no puede ser mayor que fecha fin");
            }

            ListaLibros resultados = new ListaLibros();
            buscarPorRangoRecursivo(raiz, fechaInicio, fechaFin, resultados);
            return resultados;
        } catch (Exception e) {
            System.err.println("Error en BuscarB::buscarPorRango: " + e.getMessage());
            return new ListaLibros(); // Devolver lista vacía en caso de error
        }
    }

    public void buscarPorRangoRecursivo(NodoB nodo, int fechaInicio, int fechaFin, ListaLibros resultados) {
        if (nodo == null) return;

        try {
            int i = 0;

            // Recorrer todas las claves de este nodo
            while (i < nodo.numClaves) {
                if (nodo.claves[i] == null) {
                    throw new RuntimeException("Clave nula encontrada durante búsqueda por rango");
                }

                int fechaActual = nodo.claves[i].fecha;

                // Si no es hoja, visitar el hijo i PRIMERO (claves menores que fechaActual)
                if (!nodo.esHoja) {
                    buscarPorRangoRecursivo(nodo.hijos[i], fechaInicio, fechaFin, resultados);
                }

                // Si la fecha está dentro del rango, procesar todos sus libros
                if (fechaActual >= fechaInicio && fechaActual <= fechaFin) {
                    recorrerAVLyAgregarLibros(nodo.claves[i].indiceISBN.getRaiz(), resultados);
                }

                i++;
            }

            // Último hijo para claves mayores a la última clave de este nodo
            if (!nodo.esHoja) {
                buscarPorRangoRecursivo(nodo.hijos[i], fechaInicio, fechaFin, resultados);
            }
        } catch (Exception e) {
            System.err.println("Error en BuscarB::buscarPorRangoRecursivo: " + e.getMessage());
            throw e;
        }
    }

    public void recorrerAVLyAgregarLibros(NodoIndiceISBN nodoAVL, ListaLibros resultados) {
        if (nodoAVL == null) return;

        try {
            // recorrido in-orden del avl para agregar todos los libros
            recorrerAVLyAgregarLibros(nodoAVL.izquierdo, resultados);

            // Validar que el libro no sea nulo antes de insertar
            if (nodoAVL.libro == null) {
                throw new RuntimeException("Libro nulo encontrado en AVL interno durante búsqueda por rango");
            }

            resultados.insertar(nodoAVL.libro);

            recorrerAVLyAgregarLibros(nodoAVL.derecho, resultados);
        } catch (Exception e) {
            System.err.println("Error en BuscarB::recorrerAVLyAgregarLibros: " + e.getMessage());
            throw e;
        }
    }
}
