package org.example.TablaHash;

public class TablaHash<K, V> {
    private static final int CAPACIDAD_INICIAL = 16;
    private static final double FACTOR_CARGA = 0.75;

    private Entrada<K, V>[] tabla;
    private int tamaño;
    private int capacidad;

    //clase interna para las entradas
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

    private int has(K clave) {
        return Math.abs(clave.hashCode()) % capacidad;
    }

    public void put(K clave, V valor) {
        if (clave == null) return;
        int indiceOriginal = has(clave);

        //redimensionar si es necesario
        if ((double)tamaño / capacidad >= FACTOR_CARGA) {
            redimensionar();
        }

        int indice = has(clave);
        Entrada<K, V> nuevaEntrada = new Entrada<>(clave, valor);

        //si no hay colision
        if (tabla[indice] == null) {
            tabla[indice] = nuevaEntrada;
        } else  {
            //manejar colision con lista enlazada
            Entrada<K, V>  actual = tabla[indice];
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

    public V get(K clave) {
        if (clave == null) return  null;

        int indice = has(clave);
        Entrada<K, V> actual = tabla[indice];

        while (actual != null) {
            if (actual.clave.equals(clave)) {
                return actual.valor;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    public boolean containsKey(K clave) {
        return get(clave) != null;
    }

    public int size() {
        return tamaño;
    }

    public boolean isEmpty() {
        return tamaño == 0;
    }

    @SuppressWarnings("unchecked")
    private void redimensionar() {

        capacidad *= 2;
        Entrada<K, V>[] tablaVieja = tabla;
        tabla = new Entrada[capacidad];
        tamaño = 0;

        // Reinsertar todas las entradas SIN llamar a put() para evitar recursión
        for (Entrada<K, V> entrada : tablaVieja) {
            Entrada<K, V> actual = entrada;
            while (actual != null) {
                reinsertarEntrada(actual.clave, actual.valor);
                actual = actual.siguiente;
            }
        }
    }

    // Método auxiliar para reinsertar sin verificar redimensionamiento
    private void reinsertarEntrada(K clave, V valor) {
        int indice = has(clave);
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

    // Iterador para valores
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

    // Iterador para claves (opcional)
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
}