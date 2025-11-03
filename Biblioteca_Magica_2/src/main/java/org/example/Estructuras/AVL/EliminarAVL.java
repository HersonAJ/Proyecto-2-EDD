package org.example.Estructuras.AVL;

class EliminarAVL extends RotacionesAVL {

    protected NodoAVL eliminarNodoEficiente(NodoAVL nodo, String titulo, String isbn) {
        if (nodo == null) return null;

        // Validar que el nodo tenga libro válido
        if (nodo.getLibro() == null) {
            throw new RuntimeException("Nodo AVL con libro nulo encontrado durante eliminación");
        }

        // Búsqueda binaria normal por título
        int cmp = titulo.compareTo(nodo.getLibro().getTitulo());

        if (cmp < 0) {
            nodo.setIzquierdo(eliminarNodoEficiente(nodo.getIzquierdo(), titulo, isbn));
        } else if (cmp > 0) {
            nodo.setDerecho(eliminarNodoEficiente(nodo.getDerecho(), titulo, isbn));
        } else {
            // Títulos iguales, buscar por ISBN específico
            if (isbn.equals(nodo.getLibro().getIsbn())) {
                // Eliminar este nodo específico
                if (nodo.getIzquierdo() == null && nodo.getDerecho() == null) {
                    return null;
                } else if (nodo.getIzquierdo() == null) {
                    return nodo.getDerecho();
                } else if (nodo.getDerecho() == null) {
                    return nodo.getIzquierdo();
                } else {
                    NodoAVL sucesor = nodoMinimo(nodo.getDerecho());
                    // Validar que el sucesor sea válido
                    if (sucesor == null || sucesor.getLibro() == null) {
                        throw new RuntimeException("Sucesor inválido encontrado durante eliminación");
                    }
                    nodo.setLibro(sucesor.getLibro());
                    nodo.setDerecho(eliminarNodoEficiente(nodo.getDerecho(),
                            sucesor.getLibro().getTitulo(), sucesor.getLibro().getIsbn()));
                }
            } else {
                // Mismo título pero diferente ISBN, seguir buscando
                int cmpISBN = isbn.compareTo(nodo.getLibro().getIsbn());
                if (cmpISBN < 0) {
                    nodo.setIzquierdo(eliminarNodoEficiente(nodo.getIzquierdo(), titulo, isbn));
                } else {
                    nodo.setDerecho(eliminarNodoEficiente(nodo.getDerecho(), titulo, isbn));
                }
            }
        }

        // Actualizar altura solo si el nodo no fue eliminado
        if (nodo != null) {
            actualizarAltura(nodo);
            return balancear(nodo);
        }

        return null;
    }

    protected NodoAVL nodoMinimo(NodoAVL nodo) {
        try {
            if (nodo == null) return null;

            NodoAVL actual = nodo;
            while (actual != null && actual.getIzquierdo() != null) {
                // Validar integridad de la estructura
                if (actual.getIzquierdo() == null) {
                    throw new RuntimeException("Estructura del árbol corrupta en nodoMinimo");
                }
                actual = actual.getIzquierdo();
            }
            return actual;
        } catch (Exception e) {
            System.err.println("Error en EliminarAVL::nodoMinimo: " + e.getMessage());
            return null;
        }
    }
}