package org.example.include;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Ordenamientos {

    //ordenamiento bubblesort complejidad de O(n^2)
    public static <T> long bubbleSort(List<T> lista, Comparator<T> criterio) {
        long tiempoIncio = System.nanoTime();

        if (lista == null || lista.size() <= 1) {
            return System.nanoTime() - tiempoIncio;
        }

        int n = lista.size();
        boolean intercambiado;

        for (int i = 0; i < n - 1; i++) {
            intercambiado = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (criterio.compare(lista.get(j), lista.get(j + 1)) > 0 ) {
                    //intercambiar elementos
                    T temp = lista.get(j);
                    lista.set(j , lista.get(j + 1));
                    lista.set(j + 1, temp);
                    intercambiado = true;
                }
            }

            //si no hubo cintercambio, la lista ya esta ordenada
            if (!intercambiado) break;
        }
        return System.nanoTime() - tiempoIncio;
    }

    //ordenamiento Selection Sort  complejidad de O(n^2)
    public static <T> long selectionSort(List<T> lista, Comparator<T> criterio) {
        long tiempoIncio = System.nanoTime();

        if (lista == null || lista.size() <= 1) {
            return System.nanoTime() - tiempoIncio;
        }

        int n = lista.size();

        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;

            //encontrar el elemento minimo en el subarreglo no ordenado
            for (int j = i + 1; j < n; j++) {
                if (criterio.compare(lista.get(j), lista.get(minIndex)) < 0) {
                    minIndex = j;
                }
            }

            //intermabiar el elemento minimo con el primer elemento del subarreglo no ordenado
            if (minIndex != i) {
                T temp = lista.get(minIndex);
                lista.set(minIndex, lista.get(i));
                lista.set(i, temp);
            }
        }
        return System.nanoTime() - tiempoIncio;
    }

    //ordenamiento por insercion  complejidad de O(n^2)
    public static <T> long insertionSort(List<T> lista, Comparator<T> criterio) {
        long tiempoIncio = System.nanoTime();

        if (lista == null || lista.size() <= 1) {
            return System.nanoTime() - tiempoIncio;
        }

        int n = lista.size();

        for (int i = 1; i < n; i++) {
            T clave = lista.get(i);
            int j = i - 1;

            //mover elementos mayores que la clave una posicision adelante
            while (j >= 0 && criterio.compare(lista.get(j), clave) > 0 ) {
                lista.set(j + 1, lista.get(j));
                j = j - 1;
            }
            lista.set(j + 1, clave);
        }
        return System.nanoTime() - tiempoIncio;
    }

    //ordenamiento shell complejidad O(n lon n)
    public static <T> long shellSort(List<T> lista, Comparator<T> criterio) {
        long startTime = System.nanoTime();

        if (lista == null || lista.size() <= 1) {
            return System.nanoTime() - startTime;
        }

        int n = lista.size();

        for (int gap = n / 2; gap > 0; gap /= 2) {
            // Hacer insertion sort para este gap
            for (int i = gap; i < n; i++) {
                T temp = lista.get(i);
                int j;

                for (j = i; j >= gap && criterio.compare(lista.get(j - gap), temp) > 0; j -= gap) {
                    lista.set(j, lista.get(j - gap));
                }
                lista.set(j, temp);
            }
        }

        return System.nanoTime() - startTime;
    }

    //ordenamiento quick sort complejidad O(n log n) o O(n^2) en el pero de los casos
    public static <T> long quickSort(List<T> lista, Comparator<T> criterio) {
        long startTime = System.nanoTime();

        if (lista != null && lista.size() > 1) {
            quickSortRecursivo(lista, 0, lista.size() - 1, criterio);
        }

        return System.nanoTime() - startTime;
    }

    private static <T> void quickSortRecursivo(List<T> lista, int low, int high, Comparator<T> criterio) {
        if (low < high) {
            // pi es el índice de partición
            int pi = particionar(lista, low, high, criterio);

            // Ordenar recursivamente los elementos antes y después de la partición
            quickSortRecursivo(lista, low, pi - 1, criterio);
            quickSortRecursivo(lista, pi + 1, high, criterio);
        }
    }

     // Método de partición para QuickSort
    private static <T> int particionar(List<T> lista, int low, int high, Comparator<T> criterio) {
        T pivot = lista.get(high);
        int i = (low - 1); // índice del elemento más pequeño

        for (int j = low; j < high; j++) {
            // Si el elemento actual es menor o igual al pivot
            if (criterio.compare(lista.get(j), pivot) <= 0) {
                i++;

                // Intercambiar lista[i] y lista[j]
                T temp = lista.get(i);
                lista.set(i, lista.get(j));
                lista.set(j, temp);
            }
        }

        // Intercambiar lista[i+1] y lista[high] (o pivot)
        T temp = lista.get(i + 1);
        lista.set(i + 1, lista.get(high));
        lista.set(high, temp);

        return i + 1;
    }

    public static <T> List<T> copiarLista(List<T> original) {
        return new ArrayList<>(original);
    }

    public static <T> boolean estaOrdenada(List<T> lista, Comparator<T> criterio) {
        if (lista == null || lista.size() <= 1) {
            return true;
        }

        for (int i = 0; i < lista.size() - 1; i++) {
            if (criterio.compare(lista.get(i), lista.get(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }
}
