package org.example.B;

public class EliminarB {

    public void eliminar(ArbolB arbol, String fechaStr) {
        try {
            if (arbol.getRaiz() == null) return;

            int fechaInt = Integer.parseInt(fechaStr);
            eliminarRecursivo(arbol.getRaiz(), fechaInt);

            // Si la raíz queda sin claves y no es hoja, subir el primer hijo
            if (arbol.getRaiz().numClaves == 0 && !arbol.getRaiz().esHoja) {
                NodoB viejaRaiz = arbol.getRaiz();
                arbol.setRaiz(arbol.getRaiz().hijos[0]);
            }
        } catch (Exception e) {
            System.err.println("Error en EliminarB::eliminar: " + e.getMessage());
        }
    }

    public EntradaFecha obtenerMaximo(NodoB nodo) {
        try {
            if (nodo == null) throw new IllegalArgumentException("Nodo nulo en obtenerMaximo");

            NodoB actual = nodo;
            while (!actual.esHoja) {
                if (actual.hijos[actual.numClaves] == null) {
                    throw new RuntimeException("Hijo nulo en obtenerMaximo");
                }
                actual = actual.hijos[actual.numClaves]; // último hijo
            }

            if (actual.numClaves == 0 || actual.claves[actual.numClaves - 1] == null) {
                throw new RuntimeException("No hay claves válidas en obtenerMaximo");
            }

            return actual.claves[actual.numClaves - 1];
        } catch (Exception e) {
            System.err.println("Error en EliminarB::obtenerMaximo: " + e.getMessage());
            return null;
        }
    }

    public EntradaFecha obtenerMinimo(NodoB nodo) {
        try {
            if (nodo == null) throw new IllegalArgumentException("Nodo nulo en obtenerMinimo");

            NodoB actual = nodo;
            while (!actual.esHoja) {
                if (actual.hijos[0] == null) {
                    throw new RuntimeException("Hijo nulo en obtenerMinimo");
                }
                actual = actual.hijos[0];
            }

            if (actual.numClaves == 0 || actual.claves[0] == null) {
                throw new RuntimeException("No hay claves válidas en obtenerMinimo");
            }

            return actual.claves[0];
        } catch (Exception e) {
            System.err.println("Error en EliminarB::obtenerMinimo: " + e.getMessage());
            return null;
        }
    }

    public void fusionar(NodoB nodo, int idx) {
        try {
            if (nodo == null) throw new IllegalArgumentException("Nodo nulo en fusionar");
            if (idx < 0 || idx >= nodo.numClaves) throw new IndexOutOfBoundsException("Índice inválido en fusionar");

            NodoB hijoIzq = nodo.hijos[idx];
            NodoB hijoDer = nodo.hijos[idx + 1];

            if (hijoIzq == null || hijoDer == null) throw new RuntimeException("Hijos nulos en fusionar");

            // mover la clave del padre hacia hijoIzq (movemos el puntero)
            hijoIzq.claves[hijoIzq.numClaves] = nodo.claves[idx];

            // copiar claves de hijoDer a hijoIzq
            for (int i = 0; i < hijoDer.numClaves; ++i) {
                hijoIzq.claves[hijoIzq.numClaves + 1 + i] = hijoDer.claves[i];
            }

            // copiar hijos si no son hojas
            if (!hijoIzq.esHoja) {
                for (int i = 0; i <= hijoDer.numClaves; ++i) {
                    hijoIzq.hijos[hijoIzq.numClaves + 1 + i] = hijoDer.hijos[i];
                }
            }

            // actualizar cuenta de claves en hijoIzq
            hijoIzq.numClaves += 1 + hijoDer.numClaves;

            // mover claves e hijos del nodo padre a la izquierda para "eliminar" clave idx
            for (int i = idx; i < nodo.numClaves - 1; ++i) {
                nodo.claves[i] = nodo.claves[i + 1];
                nodo.hijos[i + 1] = nodo.hijos[i + 2];
            }

            // limpiar referencias sobrantes
            nodo.claves[nodo.numClaves - 1] = null;
            nodo.hijos[nodo.numClaves] = null;
            nodo.numClaves--;
        } catch (Exception e) {
            System.err.println("Error en EliminarB::fusionar: " + e.getMessage());
            throw e;
        }
    }

    public void prestarDeIzquierda(NodoB nodo, int idx) {
        try {
            if (nodo == null) throw new IllegalArgumentException("Nodo nulo en prestarDeIzquierda");
            if (idx <= 0 || idx > nodo.numClaves) throw new IndexOutOfBoundsException("Índice inválido en prestarDeIzquierda");

            NodoB hijo = nodo.hijos[idx];
            NodoB hermano = nodo.hijos[idx - 1];

            if (hijo == null || hermano == null) throw new RuntimeException("Hijos nulos en prestarDeIzquierda");

            // desplazar claves del hijo a la derecha para abrir el puesto 0
            for (int i = hijo.numClaves - 1; i >= 0; --i) {
                hijo.claves[i + 1] = hijo.claves[i];
            }
            if (!hijo.esHoja) {
                for (int i = hijo.numClaves; i >= 0; --i) {
                    hijo.hijos[i + 1] = hijo.hijos[i];
                }
            }

            // bajar la clave del padre al hijo (movemos puntero)
            hijo.claves[0] = nodo.claves[idx - 1];

            // si el hermano no es hoja, mover su último hijo como hijo->hijos[0]
            if (!hermano.esHoja) {
                hijo.hijos[0] = hermano.hijos[hermano.numClaves];
                hermano.hijos[hermano.numClaves] = null;
            }

            // subir la última clave del hermano al padre
            nodo.claves[idx - 1] = hermano.claves[hermano.numClaves - 1];
            hermano.claves[hermano.numClaves - 1] = null;

            hijo.numClaves++;
            hermano.numClaves--;
        } catch (Exception e) {
            System.err.println("Error en EliminarB::prestarDeIzquierda: " + e.getMessage());
            throw e;
        }
    }

    public void prestarDeDerecha(NodoB nodo, int idx) {
        try {
            if (nodo == null) throw new IllegalArgumentException("Nodo nulo en prestarDeDerecha");
            if (idx < 0 || idx >= nodo.numClaves) throw new IndexOutOfBoundsException("Índice inválido en prestarDeDerecha");

            NodoB hijo = nodo.hijos[idx];
            NodoB hermano = nodo.hijos[idx + 1];

            if (hijo == null || hermano == null) throw new RuntimeException("Hijos nulos en prestarDeDerecha");

            // bajar la clave del padre al final de hijo
            hijo.claves[hijo.numClaves] = nodo.claves[idx];

            // si no es hoja, mover primer hijo del hermano como hijo
            if (!hijo.esHoja) {
                hijo.hijos[hijo.numClaves + 1] = hermano.hijos[0];
            }

            // subir la primera clave del hermano al padre
            nodo.claves[idx] = hermano.claves[0];

            // desplazar claves del hermano a la izquierda
            for (int i = 1; i < hermano.numClaves; ++i) {
                hermano.claves[i - 1] = hermano.claves[i];
            }
            if (!hermano.esHoja) {
                for (int i = 1; i <= hermano.numClaves; ++i) {
                    hermano.hijos[i - 1] = hermano.hijos[i];
                }
            }

            // limpiar última referencia
            hermano.claves[hermano.numClaves - 1] = null;
            hermano.hijos[hermano.numClaves] = null;

            hijo.numClaves++;
            hermano.numClaves--;
        } catch (Exception e) {
            System.err.println("Error en EliminarB::prestarDeDerecha: " + e.getMessage());
            throw e;
        }
    }

    public void eliminarRecursivo(NodoB nodo, int fecha) {
        try {
            if (nodo == null) return;

            int idx = 0;
            while (idx < nodo.numClaves && fecha > nodo.claves[idx].fecha) {
                idx++;
            }

            // 1) clave encontrada en este nodo
            if (idx < nodo.numClaves && nodo.claves[idx].fecha == fecha) {
                if (nodo.esHoja) {
                    // Caso 1: nodo hoja -> eliminar entrada
                    for (int i = idx; i < nodo.numClaves - 1; ++i) {
                        nodo.claves[i] = nodo.claves[i + 1];
                    }
                    nodo.claves[nodo.numClaves - 1] = null;
                    nodo.numClaves--;
                    return;
                } else {
                    // Caso 2: clave en nodo interno
                    if (nodo.hijos[idx].numClaves >= NodoB.T) {
                        // 2a: tomar predecesor
                        EntradaFecha pred = obtenerMaximo(nodo.hijos[idx]);
                        if (pred == null) throw new RuntimeException("Predecesor nulo en eliminación");
                        // copiar contenido del predecesor al lugar de la clave
                        nodo.claves[idx].fecha = pred.fecha;
                        nodo.claves[idx].indiceISBN = pred.indiceISBN;
                        // eliminar el predecesor recursivamente
                        eliminarRecursivo(nodo.hijos[idx], pred.fecha);
                    } else if (nodo.hijos[idx + 1].numClaves >= NodoB.T) {
                        // 2b: tomar sucesor
                        EntradaFecha succ = obtenerMinimo(nodo.hijos[idx + 1]);
                        if (succ == null) throw new RuntimeException("Sucesor nulo en eliminación");
                        nodo.claves[idx].fecha = succ.fecha;
                        nodo.claves[idx].indiceISBN = succ.indiceISBN;
                        eliminarRecursivo(nodo.hijos[idx + 1], succ.fecha);
                    } else {
                        // 2c: ambos hijos tienen T-1 -> fusionar y eliminar en el hijo fusionado
                        fusionar(nodo, idx);
                        eliminarRecursivo(nodo.hijos[idx], fecha);
                    }
                }
            } else {
                // 2) clave NO está en este nodo
                if (nodo.esHoja) {
                    // no existe la clave
                    return;
                }

                boolean flagUltima = (idx == nodo.numClaves);

                if (nodo.hijos[idx].numClaves < NodoB.T) {
                    if (idx > 0 && nodo.hijos[idx - 1].numClaves >= NodoB.T) {
                        // pedir prestado del hermano izquierdo
                        prestarDeIzquierda(nodo, idx);
                    } else if (idx < nodo.numClaves && nodo.hijos[idx + 1].numClaves >= NodoB.T) {
                        // pedir prestado del hermano derecho
                        prestarDeDerecha(nodo, idx);
                    } else {
                        // fusionar con un hermano
                        if (idx < nodo.numClaves) {
                            fusionar(nodo, idx);
                        } else {
                            fusionar(nodo, idx - 1);
                            idx--; // ahora descendemos al hijo fusionado
                        }
                    }
                }

                // decidir en qué hijo bajar: si originalmente era el último y después de fill
                if (flagUltima && idx > nodo.numClaves) {
                    eliminarRecursivo(nodo.hijos[idx - 1], fecha);
                } else {
                    eliminarRecursivo(nodo.hijos[idx], fecha);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en EliminarB::eliminarRecursivo: " + e.getMessage());
            throw e;
        }
    }
}