package org.example.Estructuras.BPlus;

public class EliminarBPlus {

    public boolean buscarPosicionGenero(ArbolBPlus arbol, String genero, NodoHoja[] hojaResultado, int[] posicionResultado) {
        try {
            if (genero == null || genero.isEmpty())
                throw new IllegalArgumentException("Género vacío en buscarPosicionGenero");
            if (hojaResultado == null)
                throw new IllegalArgumentException("Array de hoja resultado nulo");

            InsertarBPlus insertar = new InsertarBPlus();
            hojaResultado[0] = insertar.buscarHoja(arbol.getRaiz(), genero);
            if (hojaResultado[0] == null) {
                throw new RuntimeException("Hoja nula retornada por buscarHoja");
            }

            for (int i = 0; i < hojaResultado[0].numClaves; i++) {
                if (hojaResultado[0].entradas[i].genero.equals(genero)) {
                    posicionResultado[0] = i;
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error en EliminarBPlus::buscarPosicionGenero: " + e.getMessage());
            if (hojaResultado != null) hojaResultado[0] = null;
            if (posicionResultado != null) posicionResultado[0] = -1;
            return false;
        }
    }

    public void eliminarPorISBN(ArbolBPlus arbol, String isbn, String genero) {
        try {
            if (genero == null || genero.isEmpty()) {
                System.out.println("Género vacío para ISBN: " + isbn);
                return;
            }

            // Buscar la posición del género en el B+
            NodoHoja[] hojaResultado = new NodoHoja[1];
            int[] posicionResultado = new int[1];
            boolean generoEncontrado = buscarPosicionGenero(arbol, genero, hojaResultado, posicionResultado);

            if (!generoEncontrado) {
                System.out.println("Género '" + genero + "' no encontrado en el árbol B+.");
                return;
            }

            NodoHoja hoja = hojaResultado[0];
            int posicion = posicionResultado[0];

            // Eliminar el libro del AVL específico
            if (hoja.entradas[posicion].indiceISBN == null) {
                throw new RuntimeException("IndiceISBN nulo en eliminarPorISBN");
            }

            hoja.entradas[posicion].indiceISBN.eliminar(isbn);

            // Verificar si el AVL quedó vacío después de eliminar
            if (hoja.entradas[posicion].indiceISBN.estaVacio()) {
                eliminarGeneroDeHoja(arbol, hoja, posicion);
            }
        } catch (Exception e) {
            System.err.println("Error en EliminarBPlus::eliminarPorISBN: " + e.getMessage());
        }
    }

    public void eliminarGeneroDeHoja(ArbolBPlus arbol, NodoHoja hoja, int posicion) {//si falla verificar la limpieza manual de referencias duplicadas
        try {
            if (hoja == null) throw new IllegalArgumentException("Hoja nula en eliminarGeneroDeHoja");
            if (posicion < 0 || posicion >= hoja.numClaves) {
                throw new IndexOutOfBoundsException("Posición inválida en eliminarGeneroDeHoja");
            }

            // Eliminar la entrada desplazando las demás
            for (int i = posicion; i < hoja.numClaves - 1; i++) {
                hoja.entradas[i] = hoja.entradas[i + 1];
            }
            // Limpiar la última entrada
            hoja.entradas[hoja.numClaves - 1] = new NodoHoja.EntradaGenero();
            hoja.numClaves--;

            // Verificar si la hoja queda con muy pocas entradas
            if (hoja.numClaves < NodoHoja.T_BPLUS - 1) {
                balancearHoja(arbol, hoja);
            }
        } catch (Exception e) {
            System.err.println("Error en EliminarBPlus::eliminarGeneroDeHoja: " + e.getMessage());
            throw e;
        }
    }

    public void balancearHoja(ArbolBPlus arbol, NodoHoja hoja) {
        try {
            if (hoja == null) throw new IllegalArgumentException("Hoja nula en balancearHoja");

            // Si es la raíz y tiene al menos 1 entrada, está bien
            if (hoja == arbol.getRaiz()) {
                if (hoja.numClaves >= 1) return; // Raíz puede tener mínimo 1
                return;
            }

            InsertarBPlus insertar = new InsertarBPlus();
            NodoInterno padre = insertar.buscarPadre(arbol.getRaiz(), hoja);
            if (padre == null) return;

            // Encontrar la posición de la hoja en el padre
            int posEnPadre = -1;
            for (int i = 0; i <= padre.numClaves; i++) {
                if (padre.hijos[i] == hoja) {
                    posEnPadre = i;
                    break;
                }
            }
            if (posEnPadre == -1) return;

            // Verificar hoja hermana izquierda (si existe)
            if (posEnPadre > 0) {
                NodoHoja hermanaIzq = (NodoHoja) padre.hijos[posEnPadre - 1];
                if (hermanaIzq != null && hermanaIzq.numClaves > NodoHoja.T_BPLUS - 1) {
                    redistribuirHojas(hermanaIzq, hoja, padre, posEnPadre - 1);
                    return;
                }
            }

            // Verificar hoja hermana derecha (si existe)
            if (posEnPadre < padre.numClaves) {
                NodoHoja hermanaDer = (NodoHoja) padre.hijos[posEnPadre + 1];
                if (hermanaDer != null && hermanaDer.numClaves > 1) {
                    redistribuirHojas(hoja, hermanaDer, padre, posEnPadre);
                    return;
                }
            }

            // Si ninguna hermana puede prestar, hacer fusión
            if (posEnPadre > 0) {
                // Fusionar con hermana izquierda
                NodoHoja hermanaIzq = (NodoHoja) padre.hijos[posEnPadre - 1];
                if (hermanaIzq != null) {
                    fusionarHojas(arbol, hermanaIzq, hoja, padre, posEnPadre - 1);
                }
            } else if (posEnPadre < padre.numClaves) {
                // Fusionar con hermana derecha
                NodoHoja hermanaDer = (NodoHoja) padre.hijos[posEnPadre + 1];
                if (hermanaDer != null) {
                    fusionarHojas(arbol, hoja, hermanaDer, padre, posEnPadre);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en EliminarBPlus::balancearHoja: " + e.getMessage());
            throw e;
        }
    }

    public void redistribuirHojas(NodoHoja hojaIzq, NodoHoja hojaDer, NodoInterno padre, int posClavePadre) {//si falla venir aqui para la eliminacion manual de sobrantes
        try {
            if (hojaIzq == null || hojaDer == null || padre == null) {
                throw new IllegalArgumentException("Parámetros nulos en redistribuirHojas");
            }
            if (posClavePadre < 0 || posClavePadre >= padre.numClaves) {
                throw new IndexOutOfBoundsException("Posición de clave padre inválida en redistribuirHojas");
            }

            // Calcular total de entradas
            int totalEntradas = hojaIzq.numClaves + hojaDer.numClaves;
            int mitad = totalEntradas / 2;

            // Mover entradas de derecha a izquierda si izquierda tiene menos
            if (hojaIzq.numClaves < mitad) {
                int necesarias = mitad - hojaIzq.numClaves;

                // Mover entradas de derecha a izquierda
                for (int i = 0; i < necesarias; i++) {
                    hojaIzq.entradas[hojaIzq.numClaves] = hojaDer.entradas[i];
                    hojaIzq.numClaves++;
                }

                // Desplazar entradas en hoja derecha
                for (int i = 0; i < hojaDer.numClaves - necesarias; i++) {
                    hojaDer.entradas[i] = hojaDer.entradas[i + necesarias];
                }
                // Limpiar entradas sobrantes
                for (int i = hojaDer.numClaves - necesarias; i < hojaDer.numClaves; i++) {
                    hojaDer.entradas[i] = new NodoHoja.EntradaGenero();
                }
                hojaDer.numClaves -= necesarias;

            } else { // Mover de izquierda a derecha
                int sobrantes = hojaIzq.numClaves - mitad;

                // Hacer espacio en hoja derecha
                for (int i = hojaDer.numClaves - 1; i >= 0; i--) {
                    hojaDer.entradas[i + sobrantes] = hojaDer.entradas[i];
                }

                // Mover entradas de izquierda a derecha
                for (int i = 0; i < sobrantes; i++) {
                    hojaDer.entradas[i] = hojaIzq.entradas[hojaIzq.numClaves - sobrantes + i];
                }
                hojaDer.numClaves += sobrantes;

                // Limpiar entradas movidas en izquierda
                for (int i = hojaIzq.numClaves - sobrantes; i < hojaIzq.numClaves; i++) {
                    hojaIzq.entradas[i] = new NodoHoja.EntradaGenero();
                }
                hojaIzq.numClaves -= sobrantes;
            }

            // Actualizar clave en el padre
            if (hojaDer.numClaves > 0) {
                padre.claves[posClavePadre] = hojaDer.entradas[0].genero;
            }
        } catch (Exception e) {
            System.err.println("Error en EliminarBPlus::redistribuirHojas: " + e.getMessage());
            throw e;
        }
    }

    public void fusionarHojas(ArbolBPlus arbol, NodoHoja hojaIzq, NodoHoja hojaDer, NodoInterno padre, int posClavePadre) {
        try {
            if (hojaIzq == null || hojaDer == null || padre == null) {
                throw new IllegalArgumentException("Parámetros nulos en fusionarHojas");
            }
            if (posClavePadre < 0 || posClavePadre >= padre.numClaves) {
                throw new IndexOutOfBoundsException("Posición de clave padre inválida en fusionarHojas");
            }

            // Mover todas las entradas de hojaDer a hojaIzq
            for (int i = 0; i < hojaDer.numClaves; i++) {
                hojaIzq.entradas[hojaIzq.numClaves] = hojaDer.entradas[i];
                hojaIzq.numClaves++;
            }

            // Actualizar enlaces entre hojas
            hojaIzq.siguiente = hojaDer.siguiente;
            if (hojaDer.siguiente != null) {
                hojaDer.siguiente.anterior = hojaIzq;
            }

            // En Java no necesitamos delete explícito

            // Eliminar clave del padre y ajustar hijos
            for (int i = posClavePadre; i < padre.numClaves - 1; i++) {
                padre.claves[i] = padre.claves[i + 1];
                padre.hijos[i + 1] = padre.hijos[i + 2];
            }
            padre.numClaves--;

            // Limpiar última referencia
            padre.hijos[padre.numClaves + 1] = null;

            // Verificar si el padre necesita balanceo después de la fusión
            if (padre.numClaves < 1 && padre != arbol.getRaiz()) {
                balancearInterno(arbol, padre);
            }
        } catch (Exception e) {
            System.err.println("Error en EliminarBPlus::fusionarHojas: " + e.getMessage());
            throw e;
        }
    }

    public void balancearInterno(ArbolBPlus arbol, NodoInterno interno) {
        try {
            if (interno == null) throw new IllegalArgumentException("Nodo interno nulo en balancearInterno");

            // Si es la raíz y tiene al menos 1 clave, está bien
            if (interno == arbol.getRaiz()) {
                if (interno.numClaves >= 1) return;

                // Si la raíz queda sin claves pero tiene un hijo, convertir ese hijo en raíz
                if (interno.numClaves == 0 && interno.hijos[0] != null) {
                    NodoBPlus nuevaRaiz = interno.hijos[0];
                    // En Java no necesitamos delete
                    arbol.setRaiz(nuevaRaiz);

                    // Si la nueva raíz es hoja, actualizar primeraHoja
                    if (nuevaRaiz.esHoja) {
                        arbol.setPrimeraHoja((NodoHoja) nuevaRaiz);
                    }
                }
                return;
            }

            InsertarBPlus insertar = new InsertarBPlus();
            NodoInterno padre = insertar.buscarPadre(arbol.getRaiz(), interno);
            if (padre == null) return;

            // Encontrar la posición del interno en el padre
            int posEnPadre = -1;
            for (int i = 0; i <= padre.numClaves; i++) {
                if (padre.hijos[i] == interno) {
                    posEnPadre = i;
                    break;
                }
            }
            if (posEnPadre == -1) return;

            // Verificar hermano izquierdo (si existe)
            if (posEnPadre > 0) {
                NodoInterno hermanoIzq = (NodoInterno) padre.hijos[posEnPadre - 1];
                if (hermanoIzq != null && hermanoIzq.numClaves > NodoInterno.T_BPLUS - 1) {
                    redistribuirInternos(hermanoIzq, interno, padre, posEnPadre - 1);
                    return;
                }
            }

            // Verificar hermano derecho (si existe)
            if (posEnPadre < padre.numClaves) {
                NodoInterno hermanoDer = (NodoInterno) padre.hijos[posEnPadre + 1];
                if (hermanoDer != null && hermanoDer.numClaves > NodoInterno.T_BPLUS - 1) {
                    redistribuirInternos(interno, hermanoDer, padre, posEnPadre);
                    return;
                }
            }

            // Si ningún hermano puede prestar, hacer fusión
            if (posEnPadre > 0) {
                NodoInterno hermanoIzq = (NodoInterno) padre.hijos[posEnPadre - 1];
                if (hermanoIzq != null) {
                    fusionarInternos(arbol, hermanoIzq, interno, padre, posEnPadre - 1);
                }
            } else if (posEnPadre < padre.numClaves) {
                NodoInterno hermanoDer = (NodoInterno) padre.hijos[posEnPadre + 1];
                if (hermanoDer != null) {
                    fusionarInternos(arbol, interno, hermanoDer, padre, posEnPadre);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en EliminarBPlus::balancearInterno: " + e.getMessage());
            throw e;
        }
    }

    public void redistribuirInternos(NodoInterno internoIzq, NodoInterno internoDer, NodoInterno padre, int posClavePadre) {
        try {
            if (internoIzq == null || internoDer == null || padre == null) {
                throw new IllegalArgumentException("Parámetros nulos en redistribuirInternos");
            }
            if (posClavePadre < 0 || posClavePadre >= padre.numClaves) {
                throw new IndexOutOfBoundsException("Posición de clave padre inválida en redistribuirInternos");
            }

            // Calcular total de claves
            int totalClaves = internoIzq.numClaves + internoDer.numClaves + 1; // +1 por la clave del padre
            int mitad = totalClaves / 2;

            if (internoIzq.numClaves < mitad) {
                // Mover de derecha a izquierda

                // Tomar clave del padre
                internoIzq.claves[internoIzq.numClaves] = padre.claves[posClavePadre];
                internoIzq.numClaves++;

                // Mover primera clave de derecho al padre
                padre.claves[posClavePadre] = internoDer.claves[0];

                // Mover primer hijo de derecho a izquierdo
                internoIzq.hijos[internoIzq.numClaves] = internoDer.hijos[0];

                // Desplazar claves e hijos en derecho
                for (int i = 0; i < internoDer.numClaves - 1; i++) {
                    internoDer.claves[i] = internoDer.claves[i + 1];
                    internoDer.hijos[i] = internoDer.hijos[i + 1];
                }
                internoDer.hijos[internoDer.numClaves - 1] = internoDer.hijos[internoDer.numClaves];
                internoDer.numClaves--;

            } else {
                // Mover de izquierda a derecha

                // Hacer espacio en derecho
                for (int i = internoDer.numClaves; i > 0; i--) {
                    internoDer.claves[i] = internoDer.claves[i - 1];
                    internoDer.hijos[i + 1] = internoDer.hijos[i];
                }
                internoDer.hijos[1] = internoDer.hijos[0];

                // Mover última clave de izquierdo al padre
                internoDer.claves[0] = padre.claves[posClavePadre];
                internoDer.numClaves++;

                // Mover última clave de izquierdo al padre
                padre.claves[posClavePadre] = internoIzq.claves[internoIzq.numClaves - 1];

                // Mover último hijo de izquierdo a derecho
                internoDer.hijos[0] = internoIzq.hijos[internoIzq.numClaves];

                internoIzq.numClaves--;
            }
        } catch (Exception e) {
            System.err.println("Error en EliminarBPlus::redistribuirInternos: " + e.getMessage());
            throw e;
        }
    }

    public void fusionarInternos(ArbolBPlus arbol, NodoInterno internoIzq, NodoInterno internoDer, NodoInterno padre, int posClavePadre) {
        try {
            if (internoIzq == null || internoDer == null || padre == null) {
                throw new IllegalArgumentException("Parámetros nulos en fusionarInternos");
            }
            if (posClavePadre < 0 || posClavePadre >= padre.numClaves) {
                throw new IndexOutOfBoundsException("Posición de clave padre inválida en fusionarInternos");
            }

            // Mover clave del padre al interno izquierdo
            internoIzq.claves[internoIzq.numClaves] = padre.claves[posClavePadre];
            internoIzq.numClaves++;

            // Mover todas las claves e hijos del derecho al izquierdo
            for (int i = 0; i < internoDer.numClaves; i++) {
                internoIzq.claves[internoIzq.numClaves] = internoDer.claves[i];
                internoIzq.hijos[internoIzq.numClaves] = internoDer.hijos[i];
                internoIzq.numClaves++;
            }
            internoIzq.hijos[internoIzq.numClaves] = internoDer.hijos[internoDer.numClaves];

            // Eliminar clave del padre y ajustar hijos
            for (int i = posClavePadre; i < padre.numClaves - 1; i++) {
                padre.claves[i] = padre.claves[i + 1];
                padre.hijos[i + 1] = padre.hijos[i + 2];
            }
            padre.numClaves--;

            // Limpiar última referencia
            padre.hijos[padre.numClaves + 1] = null;

            // Verificar si el padre necesita balanceo después de la fusión
            if (padre.numClaves < NodoInterno.T_BPLUS - 1 && padre != arbol.getRaiz()) {
                balancearInterno(arbol, padre);
            }
        } catch (Exception e) {
            System.err.println("Error en EliminarBPlus::fusionarInternos: " + e.getMessage());
            throw e;
        }
    }
}