package org.example.B;

import org.example.Modelos.Libro;
import org.example.Modelos.ListaLibros;

public class ArbolB {
    private NodoB raiz;
    private InsertarB insertarB;
    private EliminarB eliminarB;
    private BuscarB buscarB;

    public ArbolB() {
        this.raiz = null;
        this.insertarB = new InsertarB();
        this.eliminarB = new EliminarB();
    }

    public void insertar(Libro libro) {
        this.raiz = insertarB.insertar(this.raiz, libro);
    }

    public void eliminarPorISBN(String isbn, String fecha) {
        try {
            if (fecha.isEmpty()) {
                System.out.println("Fecha vacía para ISBN: " + isbn);
                return;
            }

            // Convertir fecha a entero
            int fechaInt;
            try {
                fechaInt = Integer.parseInt(fecha);
            } catch (Exception e) {
                System.out.println("Error convirtiendo fecha: '" + fecha + "' para ISBN: " + isbn);
                return;
            }

            // Buscar la clave de esa fecha en el árbol
            NodoB nodo = raiz;
            while (nodo != null) {
                int i = 0;
                while (i < nodo.numClaves && fechaInt > nodo.claves[i].fecha) {
                    i++;
                }

                if (i < nodo.numClaves && nodo.claves[i].fecha == fechaInt) {
                    // Se encontró la fecha
                    nodo.claves[i].indiceISBN.eliminar(isbn);

                    // Si el índice ISBN local se queda vacío se elimina la clave completamente en el B
                    if (nodo.claves[i].indiceISBN.estaVacio()) {
                        String fechaStr = String.valueOf(fechaInt);
                        eliminarB.eliminar(this, fechaStr);
                    }
                    return;
                }

                if (nodo.esHoja) break;
                nodo = nodo.hijos[i];
            }

            System.out.println("No se encontró la fecha asociada al ISBN en el árbol B: " + isbn);
        } catch (Exception e) {
            System.err.println("Error en ArbolB::eliminarPorISBN: " + e.getMessage());
        }
    }

    public ListaLibros buscarPorRango(int fechaInicio, int fechaFin) {
        return buscarB.buscarPorRango(raiz, fechaInicio, fechaFin);
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public NodoB getRaiz() {
        return raiz;
    }
    public void setRaiz(NodoB nuevaRaiz) { this.raiz = nuevaRaiz; }
}
