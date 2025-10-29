package org.example.TablaHash;

public class TablaHash<K, V> {
    private static final int CAPACIDAD_INICIAL = 16;
    private static final double FACTOR_CARGA = 0.75;

    private Entrada<K, V>[] tabla;
    private int tamaño;
    private int capacidad;

    // Clase interna para las entradas de la tabla
    //tambien lista enlazada interna para colisiones
    private static class Entrada<K, V> {
        K clave;
        V valor;
        Entrada<K, V> siguiente;

        Entrada(K clave, V valor) {
            this.clave = clave;
            this.valor = valor;
            this.siguiente = null;
        }
    }

    @SuppressWarnings("unchecked")
    public TablaHash() {
        this.capacidad = CAPACIDAD_INICIAL;
        this.tabla = new Entrada[capacidad];
        this.tamaño = 0;
    }

    // Calcula el índice hash para una clave
    private int calcularHash(K clave) {
        //implementacion manual
        String claveStr = clave.toString(); //por si no lo fuera ya que la tabla es generica
        int primo = 31; //mencion en clase de que funciona mejor con numeros primos
        int hash = 0;

        for (int i = 0; i < claveStr.length(); i++) {
            hash = (primo * hash) + claveStr.charAt(i); //calculo polinomial por cada caracter y el resultado se suma a la siguiente iteracion
        }

        //se asegura que sea un entero positivo dentro de los limites de la tabla
        return (hash & 0x7FFFFFFF) % capacidad;
    }

    // Inserta o actualiza un valor en la tabla
    public void insertar(K clave, V valor) {
        if (clave == null) return;

        // Redimensionar si se supera el factor de carga
        if ((double)tamaño / capacidad >= FACTOR_CARGA) {
            redimensionar();
        }

        int indice = calcularHash(clave);
        Entrada<K, V> nuevaEntrada = new Entrada<>(clave, valor);

        // Si no hay colisión en este índice
        if (tabla[indice] == null) {
            tabla[indice] = nuevaEntrada;
        } else {
            // Manejar colisión con lista enlazada
            Entrada<K, V> actual = tabla[indice];
            while (actual.siguiente != null) {
                if (actual.clave.equals(clave)) {
                    actual.valor = valor; // Actualizar valor existente
                    return;
                }
                actual = actual.siguiente;
            }
            // Verificar el último nodo
            if (actual.clave.equals(clave)) {
                actual.valor = valor; // Actualizar valor existente
            } else {
                actual.siguiente = nuevaEntrada; // Agregar nuevo nodo
            }
        }
        tamaño++;
    }

    // Obtiene un valor por su clave
    public V obtener(K clave) {
        if (clave == null) return null;

        int indice = calcularHash(clave);
        Entrada<K, V> actual = tabla[indice];

        // Buscar en la lista enlazada
        while (actual != null) {
            if (actual.clave.equals(clave)) {
                return actual.valor;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    // Verifica si una clave existe en la tabla
    public boolean contieneClave(K clave) {
        return obtener(clave) != null;
    }

    // Retorna el número de elementos
    public int tamano() {
        return tamaño;
    }

    // Verifica si la tabla está vacía
    public boolean estaVacia() {
        return tamaño == 0;
    }

    // Duplica el tamaño de la tabla cuando es necesario
    @SuppressWarnings("unchecked")
    private void redimensionar() {
        capacidad *= 2;
        Entrada<K, V>[] tablaVieja = tabla;
        tabla = new Entrada[capacidad];
        tamaño = 0;

        // Reinsertar todas las entradas con la nueva capacidad
        for (Entrada<K, V> entrada : tablaVieja) {
            Entrada<K, V> actual = entrada;
            while (actual != null) {
                reinsertarEntrada(actual.clave, actual.valor);
                actual = actual.siguiente;
            }
        }
    }

    // Método auxiliar para reinsertar durante redimensionamiento
    private void reinsertarEntrada(K clave, V valor) {
        int indice = calcularHash(clave); // Se recalcula con nueva capacidad
        Entrada<K, V> nuevaEntrada = new Entrada<>(clave, valor);

        if (tabla[indice] == null) {
            tabla[indice] = nuevaEntrada;
        } else {
            Entrada<K, V> actual = tabla[indice];
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevaEntrada;
        }
        tamaño++;
    }

    // Iterador para recorrer todos los valores
    public Iterador<V> iteradorValores() {
        return new Iterador<V>() {
            private int indiceActual = 0;
            private Entrada<K, V> entradaActual = null;

            private void encontrarSiguiente() {
                while (entradaActual == null && indiceActual < capacidad) {
                    entradaActual = tabla[indiceActual];
                    indiceActual++;
                }
            }

            @Override
            public boolean tieneSiguiente() {
                if (entradaActual != null) {
                    return true;
                }
                encontrarSiguiente();
                return entradaActual != null;
            }

            @Override
            public V siguiente() {
                if (!tieneSiguiente()) {
                    return null;
                }
                V valor = entradaActual.valor;
                entradaActual = entradaActual.siguiente;
                return valor;
            }
        };
    }

    // Iterador para recorrer todas las claves
    public Iterador<K> iteradorClaves() {
        return new Iterador<K>() {
            private int indiceActual = 0;
            private Entrada<K, V> entradaActual = null;

            private void encontrarSiguiente() {
                while (entradaActual == null && indiceActual < capacidad) {
                    entradaActual = tabla[indiceActual];
                    indiceActual++;
                }
            }

            @Override
            public boolean tieneSiguiente() {
                if (entradaActual != null) {
                    return true;
                }
                encontrarSiguiente();
                return entradaActual != null;
            }

            @Override
            public K siguiente() {
                if (!tieneSiguiente()) {
                    return null;
                }
                K clave = entradaActual.clave;
                entradaActual = entradaActual.siguiente;
                return clave;
            }
        };
    }

    // Elimina una entrada por su clave
    public boolean eliminar(K clave) {
        if (clave == null) return false;

        int indice = calcularHash(clave);
        Entrada<K, V> actual = tabla[indice];
        Entrada<K, V> anterior = null;

        while (actual != null) {
            if (actual.clave.equals(clave)) {
                if (anterior == null) {
                    // Es el primer elemento de la lista
                    tabla[indice] = actual.siguiente;
                } else {
                    // Es un elemento intermedio o final
                    anterior.siguiente = actual.siguiente;
                }
                tamaño--;
                return true;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return false;
    }
}