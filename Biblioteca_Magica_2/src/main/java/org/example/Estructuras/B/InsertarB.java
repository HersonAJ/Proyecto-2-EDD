package org.example.Estructuras.B;

import org.example.Modelos.Libro;
//import java.util.NoSuchElementException;

class InsertarB {

    public  NodoB insertar(NodoB raiz, Libro libro) {
        try {
            if (libro == null) throw new IllegalArgumentException("Libro nulo en ArbolB::insertar");

            int fechaInt = libro.getFechaInt();

            // PRIMERO buscar si la fecha ya existe en TODO el árbol
            EntradaFecha entradaExistente = buscarFechaGlobal(raiz, fechaInt);

            if (entradaExistente != null) {
                // Fecha existe -> solo agregar ISBN
                entradaExistente.indiceISBN.insertar(libro.getIsbn(), libro);
                return raiz;  // NO continuar con la inserción normal
            }

            // Fecha NO existe -> proceder con inserción normal
            if (raiz == null) {
                raiz = new NodoB(true);
                raiz.claves[0] = new EntradaFecha(fechaInt);
                raiz.claves[0].indiceISBN.insertar(libro.getIsbn(), libro);
                raiz.numClaves = 1;
            } else {
                if (raiz.numClaves == 2 * NodoB.T - 1) {
                    NodoB nuevaRaiz = new NodoB(false);
                    nuevaRaiz.hijos[0] = raiz;
                    dividirHijo(nuevaRaiz, 0);
                    raiz = nuevaRaiz;

                    int i = 0;
                    if (fechaInt > nuevaRaiz.claves[0].fecha) {
                        i++;
                    }
                    insertarNoLleno(nuevaRaiz.hijos[i], fechaInt, libro);
                } else {
                    insertarNoLleno(raiz, fechaInt, libro);
                }
            }
            return raiz;
        } catch (Exception e) {
            System.err.println("Error en ArbolB::insertar: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void dividirHijo(NodoB padre, int indice) {
        try {
            NodoB hijo = padre.hijos[indice];
            if (hijo == null) throw new RuntimeException("Hijo nulo en dividirHijo");

            NodoB nuevoHijo = new NodoB(hijo.esHoja);
            nuevoHijo.numClaves = NodoB.T - 1;

            // TRANSFERIR claves al nuevo hijo
            for (int j = 0; j < NodoB.T - 1; j++) {
                nuevoHijo.claves[j] = hijo.claves[j + NodoB.T];
                hijo.claves[j + NodoB.T] = null;
            }

            if (!hijo.esHoja) {
                for (int j = 0; j < NodoB.T; j++) {
                    nuevoHijo.hijos[j] = hijo.hijos[j + NodoB.T];
                    hijo.hijos[j + NodoB.T] = null;
                }
            }

            // Guardar la clave promovida y LIMPIARLA del hijo
            EntradaFecha clavePromovida = hijo.claves[NodoB.T - 1];
            if (clavePromovida == null) throw new RuntimeException("Clave promovida nula en dividirHijo");

            hijo.claves[NodoB.T - 1] = null;
            hijo.numClaves = NodoB.T - 1;

            // Reorganizar hijos del padre
            for (int j = padre.numClaves; j >= indice + 1; j--) {
                padre.hijos[j + 1] = padre.hijos[j];
            }
            padre.hijos[indice + 1] = nuevoHijo;

            // Reorganizar claves del padre
            for (int j = padre.numClaves - 1; j >= indice; j--) {
                padre.claves[j + 1] = padre.claves[j];
            }

            padre.claves[indice] = clavePromovida;
            padre.numClaves++;
        } catch (Exception e) {
            System.err.println("Error en ArbolB::dividirHijo: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void insertarNoLleno(NodoB nodo, int fecha, Libro libro) {
        try {
            if (nodo == null) throw new IllegalArgumentException("Nodo nulo en insertarNoLleno");
            if (libro == null) throw new IllegalArgumentException("Libro nulo en insertarNoLleno");

            int i = nodo.numClaves - 1;

            if (nodo.esHoja) {
                // Este método SOLO se llama para FECHAS NUEVAS
                int pos = 0;
                while (pos < nodo.numClaves && fecha > nodo.claves[pos].fecha) {
                    pos++;
                }

                // Desplazar y crear nueva entrada
                for (int j = nodo.numClaves; j > pos; j--) {
                    nodo.claves[j] = nodo.claves[j - 1];
                }

                nodo.claves[pos] = new EntradaFecha(fecha);
                nodo.claves[pos].indiceISBN.insertar(libro.getIsbn(), libro);
                nodo.numClaves++;

            } else {
                // Buscar hijo adecuado
                while (i >= 0 && fecha < nodo.claves[i].fecha) {
                    i--;
                }
                i++;

                if (nodo.hijos[i].numClaves == 2 * NodoB.T - 1) {
                    dividirHijo(nodo, i);
                    if (fecha > nodo.claves[i].fecha) {
                        i++;
                    }
                }
                insertarNoLleno(nodo.hijos[i], fecha, libro);
            }
        } catch (Exception e) {
            System.err.println("Error en ArbolB::insertarNoLleno: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private EntradaFecha buscarFechaGlobal(NodoB raiz, int fecha) {
        try {
            return buscarFechaEnNodo(raiz, fecha);
        } catch (Exception e) {
            System.err.println("Error en ArbolB::buscarFechaGlobal: " + e.getMessage());
            return null;
        }
    }

    private EntradaFecha buscarFechaEnNodo(NodoB nodo, int fecha) {
        if (nodo == null) return null;

        try {
            // BÚSQUEDA BINARIA dentro del nodo - O(log T)
            int izquierda = 0;
            int derecha = nodo.numClaves - 1;

            while (izquierda <= derecha) {
                int mid = izquierda + (derecha - izquierda) / 2;

                if (nodo.claves[mid] == null) {
                    throw new RuntimeException("Clave nula encontrada durante búsqueda binaria");
                }

                int fechaActual = nodo.claves[mid].fecha;

                if (fechaActual == fecha) {
                    return nodo.claves[mid];  // Encontrado
                } else if (fechaActual < fecha) {
                    izquierda = mid + 1;
                } else {
                    derecha = mid - 1;
                }
            }

            // Si no es hoja, buscar recursivamente en el hijo adecuado
            if (!nodo.esHoja) {
                // izquierda ahora indica la posición del hijo donde debería estar
                return buscarFechaEnNodo(nodo.hijos[izquierda], fecha);
            }

            return null;
        } catch (Exception e) {
            System.err.println("Error en ArbolB::buscarFechaEnNodo: " + e.getMessage());
            return null;
        }
    }
}