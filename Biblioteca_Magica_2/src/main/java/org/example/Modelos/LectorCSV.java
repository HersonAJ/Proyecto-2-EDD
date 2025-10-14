/*package org.example.Modelos;

import org.example.AVL.ArbolAVL;
// import org.example.ArbolB.ArbolB;
// import org.example.ArbolBPlus.ArbolBPlus;
// import org.example.Indices.IndiceISBN;
// import org.example.Catalogo.Catalogo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LectorCSV {
    private String rutaArchivo;
    private ArbolAVL arbol;
    // private ArbolB arbolB;
    // private IndiceISBN indiceISBN;
    // private ArbolBPlus arbolBPlus;
    // private Catalogo catalogoGlobal;
    private java.util.function.BiConsumer<String, String> logger;

    // Constructor
    public LectorCSV(ArbolAVL arbolDestino/*, ArbolB arbolB, IndiceISBN indice, ArbolBPlus arbolBPlus, Catalogo catalogo/, java.util.function.BiConsumer<String, String> logger) {
        this.rutaArchivo = "";
        this.arbol = arbolDestino;
        // this.arbolB = arbolB;
        // this.indiceISBN = indice;
        // this.arbolBPlus = arbolBPlus;
        // this.catalogoGlobal = catalogo;
        this.logger = logger;
    }



    // Sobrecarga para recibir ruta
    public void procesarArchivo(String ruta) {
        try {
            rutaArchivo = ruta;
            procesarArchivo();
        } catch (Exception e) {
            System.err.println("Error en LectorCSV::procesarArchivo (sobrecarga): " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void procesarArchivo() {
        try (BufferedReader archivo = new BufferedReader(new FileReader(rutaArchivo))) {
            log("Iniciando la lectura del archivo....");

            String linea;
            int numLinea = 0;
            // ListaLibros librosTemporales = new ListaLibros();

            while ((linea = archivo.readLine()) != null) {
                numLinea++;

                if (linea.isEmpty()) continue;

                List<String> campos = parseCSVLine(linea);

                if (campos.size() != 5) {
                    log("Error en línea " + numLinea + ": número incorrecto de campos. Se esperaban 5.");
                    log("Contenido: " + linea);
                    continue;
                }

                String titulo = campos.get(0);
                String isbn = campos.get(1);
                String genero = campos.get(2);
                String fecha = campos.get(3);
                String autor = campos.get(4);

                // Validar comillas antes de limpiar
                if (!tieneComillasValidas(titulo) ||
                        !tieneComillasValidas(isbn) ||
                        !tieneComillasValidas(genero) ||
                        !tieneComillasValidas(fecha) ||
                        !tieneComillasValidas(autor)) {

                    log("Error en línea " + numLinea + ": formato inválido, faltan comillas o campo vacío.");
                    log("Contenido: " + linea);
                    continue;
                }

                // Limpiar campos
                titulo = limpiarCampo(titulo);
                isbn = limpiarCampo(isbn);
                genero = limpiarCampo(genero);
                fecha = limpiarCampo(fecha);
                autor = limpiarCampo(autor);

                if (!validarISBN(isbn)) {
                    log("Error en línea " + numLinea + ": ISBN con formato inválido: " + isbn);
                    log("Formato esperado: xxx-xx-xxx-xxxx-x (13 dígitos)");
                    continue;
                }

                // VALIDACIÓN DE FECHA (siempre activa)
                if (!validarFecha(fecha)) {
                    log("Error en línea " + numLinea + ": Fecha inválida: " + fecha);
                    log("La fecha debe ser un número positivo");
                    continue;
                }

                Libro libroAVL = new Libro(titulo, isbn, genero, fecha, autor);     // Para Árbol AVL
                // Libro libroB = new Libro(titulo, isbn, genero, fecha, autor);       // Para Árbol B
                // Libro libroGlobal = new Libro(titulo, isbn, genero, fecha, autor);  // Para IndiceISBN global
                // Libro libroBPlus = new Libro(titulo, isbn, genero, fecha, autor);   // Para Árbol B+

                // Guardar la copia del B+ en la lista temporal
                // librosTemporales.insertar(libroBPlus);

                // arbolBPlus.insertarSoloGenero(genero);
                arbol.insertar(libroAVL);                    // Árbol AVL con su copia
                // arbolB.insertar(libroB);                     // Árbol B con su copia
                // indiceISBN.insertar(libroGlobal.getIsbn(), libroGlobal);  // Global con su copia
                // catalogoGlobal.agregarLibro(new Libro(titulo, isbn, genero, fecha, autor));//lista para busqueda secuencial

                log("Línea " + numLinea + " válida: " + titulo + ", " + isbn + ", " + genero + ", " + fecha + ", " + autor);
            }

            // Procesar libros temporales para B+
            // ListaLibros.Iterador iter = librosTemporales.obtenerIterador();
            // while (iter.tieneSiguiente()) {
            //     Libro libroBPlus = iter.siguiente();
            //     arbolBPlus.insertarLibroEnGenero(libroBPlus);
            // }

            log("Lectura finalizada");
        } catch (IOException e) {
            log("Error: no se pudo abrir el archivo: " + rutaArchivo);
            System.err.println("Error en LectorCSV::procesarArchivo: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error en LectorCSV::procesarArchivo: " + e.getMessage());
            log("Error crítico durante el procesamiento del archivo: " + e.getMessage());
        }
    }

    // agregarLibroIndividual
    public boolean agregarLibroIndividual(String titulo, String isbn, String genero, String fecha, String autor) {
        try {
            if (!validarISBN(isbn)) {
                log("Error: ISBN con formato inválido: " + isbn);
                log("Formato esperado: xxx-xx-xxx-xxxx-x (13 dígitos)");
                return false;
            }

            // VALIDACIÓN DE FECHA (siempre activa)
            if (!validarFecha(fecha)) {
                log("Error: Fecha inválida: " + fecha);
                log("La fecha debe ser un número positivo");
                return false;
            }

            // Validar que el ISBN no exista
            // if (indiceISBN.buscar(isbn) != null) {
            //     log("Error: El ISBN '" + isbn + "' ya existe");
            //     return false;
            // }

            // Validar campos no vacíos
            if (titulo.isEmpty() || isbn.isEmpty() || genero.isEmpty() || fecha.isEmpty() || autor.isEmpty()) {
                log("Error: Todos los campos son obligatorios");
                return false;
            }

            // MISMAS 5 COPIAS que en procesarArchivo()
            // Libro libroGlobal = new Libro(titulo, isbn, genero, fecha, autor);
            Libro libroAVL = new Libro(titulo, isbn, genero, fecha, autor);
            // Libro libroB = new Libro(titulo, isbn, genero, fecha, autor);
            // Libro libroBPlus = new Libro(titulo, isbn, genero, fecha, autor);
            // Libro libroCatalogo = new Libro(titulo, isbn, genero, fecha, autor);

            // MISMAS INSERCIONES que en procesarArchivo()
            arbol.insertar(libroAVL);
            // arbolB.insertar(libroB);
            // indiceISBN.insertar(isbn, libroGlobal);
            // catalogoGlobal.agregarLibro(libroCatalogo);

            // MISMO PROCESO para Árbol B+
            // arbolBPlus.insertarSoloGenero(genero);
            // arbolBPlus.insertarLibroEnGenero(libroBPlus);

            log("Libro agregado manualmente: " + titulo + " - ISBN: " + isbn);
            return true;

        } catch (Exception e) {
            System.err.println("Error en LectorCSV::agregarLibroIndividual: " + e.getMessage());
            log("Error excepción al agregar libro: " + e.getMessage());
            return false;
        }
    }

    // Métodos auxiliares
    private List<String> parseCSVLine(String linea) {
        List<String> campos = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean dentroComillas = false;

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (c == '"') {
                dentroComillas = !dentroComillas;
                campo.append(c);
            } else if (c == ',' && !dentroComillas) {
                campos.add(campo.toString());
                campo.setLength(0);
            } else {
                campo.append(c);
            }
        }

        campos.add(campo.toString());
        return campos;
    }

    private boolean tieneComillasValidas(String campoOriginal) {
        try {
            String campo = campoOriginal;

            // Quitar espacios iniciales y finales
            campo = campo.trim();
            if (campo.isEmpty()) return false;

            // validar comillas
            if (campo.length() < 2) return false;
            if (campo.charAt(0) != '"' || campo.charAt(campo.length() - 1) != '"') return false;

            // Contenido interno
            String interno = campo.substring(1, campo.length() - 1).trim();
            return !interno.isEmpty();
        } catch (Exception e) {
            System.err.println("Error en LectorCSV::tieneComillasValidas: " + e.getMessage());
            return false;
        }
    }

    private String limpiarCampo(String campoOriginal) {
        try {
            String campo = campoOriginal.trim();
            if (campo.isEmpty()) return "";

            // Quitar comillas si están al inicio y final
            if (campo.length() >= 2 && campo.charAt(0) == '"' && campo.charAt(campo.length() - 1) == '"') {
                campo = campo.substring(1, campo.length() - 1);
            }

            return campo.trim();
        } catch (Exception e) {
            System.err.println("Error en LectorCSV::limpiarCampo: " + e.getMessage());
            return "";
        }
    }

    private boolean validarISBN(String isbn) {
        try {
            // Formato: xxx-xx-xxx-xxxx-x (ISBN-13)
            Pattern patronISBN = Pattern.compile("^\\d{3}-\\d{1,5}-\\d{1,7}-\\d{1,7}-\\d{1}$");
            return patronISBN.matcher(isbn).matches();
        } catch (Exception e) {
            System.err.println("Error en LectorCSV::validarISBN: " + e.getMessage());
            return false;
        }
    }

    private boolean validarFecha(String fecha) {
        try {
            // Solo verificar que sea número positivo
            int año = Integer.parseInt(fecha);
            return año >= 0;  // Cualquier número positivo
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void log(String mensaje) {
        if (logger != null) {
            logger.accept(mensaje, "info"); // ✅ Usar el logger de MainWindow
        } else {
            System.out.println("[LectorCSV] " + mensaje); // Fallback
        }
    }

    private void logError(String mensaje) {
        if (logger != null) {
            logger.accept(mensaje, "error"); // ✅ Log con tipo error
        } else {
            System.err.println("[LectorCSV] " + mensaje); // Fallback
        }
    }
}*/

package org.example.Modelos;

import org.example.AVL.ArbolAVL;
import org.example.B.ArbolB;
import org.example.BPlus.ArbolBPlus;
import org.example.AVL_Auxiliar.IndiceISBN;
// import org.example.Catalogo.Catalogo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LectorCSV {
    private String rutaArchivo;
    private ArbolAVL arbol;
    private ArbolB arbolB;
    private IndiceISBN indiceISBN;
    private ArbolBPlus arbolBPlus;
    // private Catalogo catalogoGlobal;
    private java.util.function.BiConsumer<String, String> logger;

    // Constructor
    public LectorCSV(ArbolAVL arbolDestino, ArbolB arbolB, IndiceISBN indice, ArbolBPlus arbolBPlus/*, Catalogo catalogo*/, java.util.function.BiConsumer<String, String> logger) {
        this.rutaArchivo = "";
        this.arbol = arbolDestino;
        this.arbolB = arbolB;
        this.indiceISBN = indice;
        this.arbolBPlus = arbolBPlus;
        // this.catalogoGlobal = catalogo;
        this.logger = logger;
    }

    // Sobrecarga para recibir ruta
    public void procesarArchivo(String ruta) {
        try {
            rutaArchivo = ruta;
            procesarArchivo();
        } catch (Exception e) {
            logError("Error en LectorCSV::procesarArchivo (sobrecarga): " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void procesarArchivo() {
        try (BufferedReader archivo = new BufferedReader(new FileReader(rutaArchivo))) {
            log("Iniciando la lectura del archivo....", "info");

            String linea;
            int numLinea = 0;
            ListaLibros librosTemporales = new ListaLibros();

            while ((linea = archivo.readLine()) != null) {
                numLinea++;

                if (linea.isEmpty()) continue;

                List<String> campos = parseCSVLine(linea);

                if (campos.size() != 5) {
                    logError("Error en línea " + numLinea + ": número incorrecto de campos. Se esperaban 5.");
                    logError("Contenido: " + linea);
                    continue;
                }

                String titulo = campos.get(0);
                String isbn = campos.get(1);
                String genero = campos.get(2);
                String fecha = campos.get(3);
                String autor = campos.get(4);

                // Validar comillas antes de limpiar
                if (!tieneComillasValidas(titulo) ||
                        !tieneComillasValidas(isbn) ||
                        !tieneComillasValidas(genero) ||
                        !tieneComillasValidas(fecha) ||
                        !tieneComillasValidas(autor)) {

                    logError("Error en línea " + numLinea + ": formato inválido, faltan comillas o campo vacío.");
                    logError("Contenido: " + linea);
                    continue;
                }

                // Limpiar campos
                titulo = limpiarCampo(titulo);
                isbn = limpiarCampo(isbn);
                genero = limpiarCampo(genero);
                fecha = limpiarCampo(fecha);
                autor = limpiarCampo(autor);

                if (!validarISBN(isbn)) {
                    logError("Error en línea " + numLinea + ": ISBN con formato inválido: " + isbn);
                    logError("Formato esperado: xxx-xx-xxx-xxxx-x (13 dígitos)");
                    continue;
                }

                // VALIDACIÓN DE FECHA (siempre activa)
                if (!validarFecha(fecha)) {
                    logError("Error en línea " + numLinea + ": Fecha inválida: " + fecha);
                    logError("La fecha debe ser un número positivo");
                    continue;
                }

                Libro libroAVL = new Libro(titulo, isbn, genero, fecha, autor);     // Para Árbol AVL
                Libro libroB = new Libro(titulo, isbn, genero, fecha, autor);       // Para Árbol B
                Libro libroGlobal = new Libro(titulo, isbn, genero, fecha, autor);  // Para IndiceISBN global
                Libro libroBPlus = new Libro(titulo, isbn, genero, fecha, autor);   // Para Árbol B+

                // Guardar la copia del B+ en la lista temporal
                librosTemporales.insertar(libroBPlus);

                arbolBPlus.insertarSoloGenero(genero);
                arbol.insertar(libroAVL);                    // Árbol AVL con su copia
                arbolB.insertar(libroB);                     // Árbol B con su copia
                indiceISBN.insertar(libroGlobal.getIsbn(), libroGlobal);  // Global con su copia
                // catalogoGlobal.agregarLibro(new Libro(titulo, isbn, genero, fecha, autor));//lista para busqueda secuencial

                log("Línea " + numLinea + " válida: " + titulo + ", " + isbn + ", " + genero + ", " + fecha + ", " + autor, "ok");
            }

            // Procesar libros temporales para B+
            ListaLibros.Iterador iter = librosTemporales.obtenerIterador();
             while (iter.tieneSiguiente()) {
                Libro libroBPlus = iter.siguiente();
                 arbolBPlus.insertarLibroEnGenero(libroBPlus);
             }

            log("Lectura finalizada", "info");
        } catch (IOException e) {
            logError("Error: no se pudo abrir el archivo: " + rutaArchivo);
        } catch (Exception e) {
            logError("Error crítico durante el procesamiento del archivo: " + e.getMessage());
        }
    }

    // agregarLibroIndividual
    public boolean agregarLibroIndividual(String titulo, String isbn, String genero, String fecha, String autor) {
        try {
            if (!validarISBN(isbn)) {
                logError("Error: ISBN con formato inválido: " + isbn);
                logError("Formato esperado: xxx-xx-xxx-xxxx-x (13 dígitos)");
                return false;
            }

            // VALIDACIÓN DE FECHA (siempre activa)
            if (!validarFecha(fecha)) {
                logError("Error: Fecha inválida: " + fecha);
                logError("La fecha debe ser un número positivo");
                return false;
            }

            // Validar que el ISBN no exista
            // if (indiceISBN.buscar(isbn) != null) {
            //     logError("Error: El ISBN '" + isbn + "' ya existe");
            //     return false;
            // }

            // Validar campos no vacíos
            if (titulo.isEmpty() || isbn.isEmpty() || genero.isEmpty() || fecha.isEmpty() || autor.isEmpty()) {
                logError("Error: Todos los campos son obligatorios");
                return false;
            }

            // MISMAS 5 COPIAS que en procesarArchivo()
            Libro libroGlobal = new Libro(titulo, isbn, genero, fecha, autor);
            Libro libroAVL = new Libro(titulo, isbn, genero, fecha, autor);
            Libro libroB = new Libro(titulo, isbn, genero, fecha, autor);
            Libro libroBPlus = new Libro(titulo, isbn, genero, fecha, autor);
            // Libro libroCatalogo = new Libro(titulo, isbn, genero, fecha, autor);

            // MISMAS INSERCIONES que en procesarArchivo()
            arbol.insertar(libroAVL);
            arbolB.insertar(libroB);
            indiceISBN.insertar(isbn, libroGlobal);
            // catalogoGlobal.agregarLibro(libroCatalogo);

            // MISMO PROCESO para Árbol B+
            arbolBPlus.insertarSoloGenero(genero);
            arbolBPlus.insertarLibroEnGenero(libroBPlus);

            log("Libro agregado manualmente: " + titulo + " - ISBN: " + isbn, "ok");
            return true;

        } catch (Exception e) {
            logError("Error excepción al agregar libro: " + e.getMessage());
            return false;
        }
    }

    // Métodos auxiliares
    private List<String> parseCSVLine(String linea) {
        List<String> campos = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean dentroComillas = false;

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (c == '"') {
                dentroComillas = !dentroComillas;
                campo.append(c);
            } else if (c == ',' && !dentroComillas) {
                campos.add(campo.toString());
                campo.setLength(0);
            } else {
                campo.append(c);
            }
        }

        campos.add(campo.toString());
        return campos;
    }

    private boolean tieneComillasValidas(String campoOriginal) {
        try {
            String campo = campoOriginal;

            // Quitar espacios iniciales y finales
            campo = campo.trim();
            if (campo.isEmpty()) return false;

            // validar comillas
            if (campo.length() < 2) return false;
            if (campo.charAt(0) != '"' || campo.charAt(campo.length() - 1) != '"') return false;

            // Contenido interno
            String interno = campo.substring(1, campo.length() - 1).trim();
            return !interno.isEmpty();
        } catch (Exception e) {
            logError("Error en LectorCSV::tieneComillasValidas: " + e.getMessage());
            return false;
        }
    }

    private String limpiarCampo(String campoOriginal) {
        try {
            String campo = campoOriginal.trim();
            if (campo.isEmpty()) return "";

            // Quitar comillas si están al inicio y final
            if (campo.length() >= 2 && campo.charAt(0) == '"' && campo.charAt(campo.length() - 1) == '"') {
                campo = campo.substring(1, campo.length() - 1);
            }

            return campo.trim();
        } catch (Exception e) {
            logError("Error en LectorCSV::limpiarCampo: " + e.getMessage());
            return "";
        }
    }

    private boolean validarISBN(String isbn) {
        try {
            // Formato: xxx-xx-xxx-xxxx-x (ISBN-13)
            Pattern patronISBN = Pattern.compile("^\\d{3}-\\d{1,5}-\\d{1,7}-\\d{1,7}-\\d{1}$");
            return patronISBN.matcher(isbn).matches();
        } catch (Exception e) {
            logError("Error en LectorCSV::validarISBN: " + e.getMessage());
            return false;
        }
    }

    private boolean validarFecha(String fecha) {
        try {
            // Solo verificar que sea número positivo
            int año = Integer.parseInt(fecha);
            return año >= 0;  // Cualquier número positivo
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void log(String mensaje, String tipo) {
        if (logger != null) {
            logger.accept(mensaje, tipo);
        } else {
            System.out.println("[LectorCSV][" + tipo + "] " + mensaje);
        }
    }

    private void logError(String mensaje) {
        log(mensaje, "error");
    }
}