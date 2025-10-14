package org.example.BPlus;

import org.example.Modelos.Libro;
import org.example.AVL_Auxiliar.IndiceISBN;

public class InsertarBPlus {
    private static final int MAX_KEYS = 2 * NodoHoja.T_BPLUS - 1;

    public void insertarSoloGenero(ArbolBPlus arbol, String genero) {
        try {
            if (genero == null || genero.isEmpty()) return;

            NodoHoja hoja = buscarHoja(arbol.getRaiz(), genero);
            if (hoja == null) return;

            // buscar si el género ya existe
            int pos = 0;
            while (pos < hoja.numClaves && genero.compareTo(hoja.entradas[pos].genero) > 0) {
                pos++;
            }

            boolean generoExiste = (pos < hoja.numClaves && hoja.entradas[pos].genero.equals(genero));

            // Si no existe y la hoja está llena, partirla ANTES de insertar
            if (!generoExiste && hoja.numClaves >= MAX_KEYS) {
                dividirHoja(arbol, hoja);
                hoja = buscarHoja(arbol.getRaiz(), genero); // reobtener la hoja correcta después de dividir
                // recalcular pos y existencia
                pos = 0;
                while (pos < hoja.numClaves && genero.compareTo(hoja.entradas[pos].genero) > 0) pos++;
                generoExiste = (pos < hoja.numClaves && hoja.entradas[pos].genero.equals(genero));
            }

            if (generoExiste) {
                // ya existe, nada más (los IndiceISBN quedan vacíos hasta que se carguen los libros)
                return;
            }

            // Insertar nueva entrada
            if (hoja.numClaves >= MAX_KEYS) {
                // si aún está llena, abortamos para no corromper memoria
                System.err.println("[ERROR] insertarSoloGenero: hoja aún llena tras split");
                return;
            }

            // Desplazar elementos a la derecha
            for (int j = hoja.numClaves - 1; j >= pos; --j) {
                hoja.entradas[j + 1] = hoja.entradas[j];
            }

            // Crear la nueva entrada
            NodoHoja.EntradaGenero nueva = new NodoHoja.EntradaGenero(genero);
            // no se crean indiceISBN todavía (la idea es crear solo con generos sin libros antes)

            // Asignar la nueva entrada al hueco
            hoja.entradas[pos] = nueva;
            hoja.numClaves++;
        } catch (Exception e) {
            System.err.println("Error en InsertarBPlus::insertarSoloGenero: " + e.getMessage());
        }
    }

    public void insertarLibroEnGenero(ArbolBPlus arbol, Libro libro) {
        try {
            if (libro == null) return;
            String genero = libro.getGenero();
            NodoHoja hoja = buscarHoja(arbol.getRaiz(), genero);
            if (hoja == null) return;

            // buscar la entrada exacta al género
            for (int i = 0; i < hoja.numClaves; i++) {
                if (hoja.entradas[i].genero.equals(genero)) {
                    if (hoja.entradas[i].indiceISBN == null) {
                        hoja.entradas[i].indiceISBN = new IndiceISBN();
                    }
                    hoja.entradas[i].indiceISBN.insertar(libro.getIsbn(), libro);
                    return;
                }
            }
            insertarEnHoja(arbol, hoja, libro);
        } catch (Exception e) {
            System.err.println("Error en InsertarBPlus::insertarLibroEnGenero: " + e.getMessage());
        }
    }

    // Buscar hoja adecuada
    public NodoHoja buscarHoja(NodoBPlus raiz, String genero) {
        try {
            NodoBPlus nodo = raiz;

            while (!nodo.esHoja) {
                NodoInterno interno = (NodoInterno) nodo;

                int i = 0;
                while (i < interno.numClaves && genero.compareTo(interno.claves[i]) >= 0) {
                    i++;
                }

                if (i > interno.numClaves || interno.hijos[i] == null) {
                    throw new RuntimeException("Hijo nulo o índice inválido en buscarHoja");
                }

                nodo = interno.hijos[i];
            }

            return (NodoHoja) nodo;
        } catch (Exception e) {
            System.err.println("Error en InsertarBPlus::buscarHoja: " + e.getMessage());
            return null;
        }
    }

    // Insertar en hoja
    public void insertarEnHoja(ArbolBPlus arbol, NodoHoja hoja, Libro libro) {
        try {
            if (hoja == null || libro == null) return;
            String genero = libro.getGenero();

            int i = 0;
            while (i < hoja.numClaves && genero.compareTo(hoja.entradas[i].genero) > 0) {
                i++;
            }

            // Caso: género ya existe
            if (i < hoja.numClaves && hoja.entradas[i].genero.equals(genero)) {
                if (hoja.entradas[i].indiceISBN == null) {
                    hoja.entradas[i].indiceISBN = new IndiceISBN();
                }
                hoja.entradas[i].indiceISBN.insertar(libro.getIsbn(), libro);
                return;
            }

            // Nuevo género: si la hoja está llena, partirla antes de insertar
            if (hoja.numClaves >= MAX_KEYS) {
                dividirHoja(arbol, hoja);
                hoja = buscarHoja(arbol.getRaiz(), genero); // re-obtener la hoja correcta después de la division
                // recalcular posición
                i = 0;
                while (i < hoja.numClaves && genero.compareTo(hoja.entradas[i].genero) > 0) i++;
                if (i < hoja.numClaves && hoja.entradas[i].genero.equals(genero)) {
                    if (hoja.entradas[i].indiceISBN == null)
                        hoja.entradas[i].indiceISBN = new IndiceISBN();
                    hoja.entradas[i].indiceISBN.insertar(libro.getIsbn(), libro);
                    return;
                }
            }

            // Desplazar
            for (int j = hoja.numClaves - 1; j >= i; --j) {
                hoja.entradas[j + 1] = hoja.entradas[j];
            }

            // Crear la nueva entrada en i
            hoja.entradas[i] = new NodoHoja.EntradaGenero(genero);
            if (hoja.entradas[i].indiceISBN == null) {
                hoja.entradas[i].indiceISBN = new IndiceISBN();
            }
            hoja.entradas[i].indiceISBN.insertar(libro.getIsbn(), libro);
            hoja.numClaves++;
        } catch (Exception e) {
            System.err.println("Error en InsertarBPlus::insertarEnHoja: " + e.getMessage());
            throw e;
        }
    }

    // Dividir hoja
    public void dividirHoja(ArbolBPlus arbol, NodoHoja hoja) {
        try {
            if (hoja == null) return;
            int oldNum = hoja.numClaves;
            if (oldNum <= 0) return;

            // Elegir punto de corte:
            int mitad = oldNum / 2;
            if (mitad <= 0) mitad = 1;
            if (mitad >= oldNum) mitad = oldNum / 2;

            NodoHoja nuevaHoja = new NodoHoja();
            nuevaHoja.numClaves = oldNum - mitad;

            // Mover la mitad superior hacia la nueva hoja
            for (int i = 0; i < nuevaHoja.numClaves; ++i) {
                int srcIdx = mitad + i;
                // seguridad contra índices
                if (srcIdx >= oldNum || i >= MAX_KEYS) break;
                nuevaHoja.entradas[i] = hoja.entradas[srcIdx];
            }

            // Limpiar las entradas en la hoja original
            for (int i = mitad; i < oldNum; ++i) {
                hoja.entradas[i] = new NodoHoja.EntradaGenero(); // nueva entrada vacía
            }

            hoja.numClaves = mitad;

            // Ajustar enlaces entre hojas
            nuevaHoja.siguiente = hoja.siguiente;
            if (nuevaHoja.siguiente != null) {
                nuevaHoja.siguiente.anterior = nuevaHoja;
            }
            hoja.siguiente = nuevaHoja;
            nuevaHoja.anterior = hoja;

            // Subir clave al padre
            String claveSubir = (nuevaHoja.numClaves > 0) ? nuevaHoja.entradas[0].genero : "";

            if (hoja == arbol.getRaiz()) {
                NodoInterno nuevaRaiz = new NodoInterno();
                nuevaRaiz.claves[0] = claveSubir;
                nuevaRaiz.hijos[0] = hoja;
                nuevaRaiz.hijos[1] = nuevaHoja;
                nuevaRaiz.numClaves = 1;
                arbol.setRaiz(nuevaRaiz);
                return;
            }

            NodoInterno padre = buscarPadre(arbol.getRaiz(), hoja);
            if (padre == null) {
                System.err.println("Error: No se encontro padre de la hoja");
                return;
            }

            int i = 0;
            while (i < padre.numClaves && claveSubir.compareTo(padre.claves[i]) > 0) i++;

            // Desplazar claves/hijos en padre
            for (int j = padre.numClaves - 1; j >= i; --j) {
                padre.claves[j + 1] = padre.claves[j];
                padre.hijos[j + 2] = padre.hijos[j + 1];
            }

            padre.claves[i] = claveSubir;
            padre.hijos[i + 1] = nuevaHoja;
            padre.numClaves++;

            if (padre.numClaves >= MAX_KEYS) {
                dividirInterno(arbol, padre);
            }
        } catch (Exception e) {
            System.err.println("Error en InsertarBPlus::dividirHoja: " + e.getMessage());
            throw e;
        }
    }

    // Dividir interno
    public void dividirInterno(ArbolBPlus arbol, NodoInterno interno) {
        try {
            if (interno == null) throw new IllegalArgumentException("Nodo interno nulo en dividirInterno");

            int mitad = interno.numClaves / 2;

            NodoInterno nuevoInterno = new NodoInterno();

            // Número de claves que van al nuevo nodo (lado derecho)
            int numDerecha = interno.numClaves - mitad - 1;
            if (numDerecha < 0) numDerecha = 0;
            if (numDerecha > (2 * NodoInterno.T_BPLUS - 1)) numDerecha = 2 * NodoInterno.T_BPLUS - 1;
            nuevoInterno.numClaves = numDerecha;

            // Copiar claves e hijos al nuevo interno
            for (int i = 0; i < nuevoInterno.numClaves; i++) {
                nuevoInterno.claves[i] = interno.claves[mitad + 1 + i];
            }
            for (int i = 0; i <= nuevoInterno.numClaves; i++) {
                nuevoInterno.hijos[i] = interno.hijos[mitad + 1 + i];
            }

            // La clave que sube al padre
            String claveSubir = interno.claves[mitad];

            // Reducir el número de claves del interno original
            interno.numClaves = mitad;

            if (interno == arbol.getRaiz()) {
                // Crear nueva raíz
                NodoInterno nuevaRaiz = new NodoInterno();
                nuevaRaiz.claves[0] = claveSubir;
                nuevaRaiz.hijos[0] = interno;
                nuevaRaiz.hijos[1] = nuevoInterno;
                nuevaRaiz.numClaves = 1;
                arbol.setRaiz(nuevaRaiz);
            } else {
                NodoInterno padre = buscarPadre(arbol.getRaiz(), interno);

                if (padre == null) {
                    System.out.println("Error: No se encontró padre del nodo interno");
                    return;
                }

                int i = 0;
                while (i < padre.numClaves && claveSubir.compareTo(padre.claves[i]) > 0) {
                    i++;
                }

                // Desplazar claves/hijos en el padre para hacer espacio
                for (int j = padre.numClaves - 1; j >= i; --j) {
                    padre.claves[j + 1] = padre.claves[j];
                    padre.hijos[j + 2] = padre.hijos[j + 1];
                }

                // Insertar la clave y el nuevo hijo
                padre.claves[i] = claveSubir;
                padre.hijos[i + 1] = nuevoInterno;
                padre.numClaves++;

                // Si el padre ahora está lleno, dividirlo
                if (padre.numClaves >= (2 * NodoInterno.T_BPLUS - 1)) {
                    dividirInterno(arbol, padre);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en InsertarBPlus::dividirInterno: " + e.getMessage());
            throw e;
        }
    }

    // Buscar padre (auxiliar)
    public NodoInterno buscarPadre(NodoBPlus actual, NodoBPlus hijo) {
        try {
            if (actual == null || actual.esHoja) return null;

            NodoInterno interno = (NodoInterno) actual;
            for (int i = 0; i <= interno.numClaves; i++) {
                if (interno.hijos[i] == hijo) {
                    return interno;
                }
                NodoInterno posible = buscarPadre(interno.hijos[i], hijo);
                if (posible != null) return posible;
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error en InsertarBPlus::buscarPadre: " + e.getMessage());
            return null;
        }
    }

}
