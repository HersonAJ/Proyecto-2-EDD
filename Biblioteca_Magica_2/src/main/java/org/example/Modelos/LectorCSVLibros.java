package org.example.Modelos;

import org.example.Grafo.GrafoBibliotecas;

import java.io.*;
import java.util.*;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.sym.error;

public class LectorCSVLibros {
    private GrafoBibliotecas grafo;
    private CoordinadorEnvios coordinador;
    private List<String> errores;

    public LectorCSVLibros(GrafoBibliotecas grafo, CoordinadorEnvios coordinador) {
        this.grafo = grafo;
        this.coordinador = coordinador;
        this.errores = new ArrayList<>();
    }

    public ResultadoCarga procesarArchivo(String rutaArchivo) {
        ResultadoCarga resultado = new ResultadoCarga();
        errores.clear();

        // 1. Validar que el archivo existe y es CSV
        if (!validarArchivo(rutaArchivo)) {
            resultado.agregarError("Archivo", "Archivo no existe o no es CSV: " + rutaArchivo);
            return resultado;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int numeroLinea = 0;

            // Leer línea por línea
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                procesarLineaCSV(linea, numeroLinea, resultado);
            }

        } catch (IOException e) {
            resultado.agregarError("Sistema", "Error leyendo archivo: " + e.getMessage());
        }

        return resultado;
    }

    private boolean validarArchivo(String rutaArchivo) {
        File archivo = new File(rutaArchivo);
        return archivo.exists() && archivo.isFile() && rutaArchivo.toLowerCase().endsWith(".csv");
    }

    private void procesarLineaCSV(String linea, int numeroLinea, ResultadoCarga resultado) {
        // Saltar línea vacía
        if (linea.trim().isEmpty()) {
            return;
        }

        // 2. Validar formato de 9 campos
        String[] campos = parsearLineaCSV(linea);
        if (campos.length != 9) {
            String error = "Línea " + numeroLinea + ": Formato incorrecto. Se esperaban 9 campos, se encontraron " + campos.length;
            resultado.agregarError("Línea " + numeroLinea, error);
            return;
        }

        try {
            // Extraer campos
            String titulo = campos[0].trim();
            String isbn = campos[1].trim();
            String genero = campos[2].trim();
            String año = campos[3].trim();
            String autor = campos[4].trim();
            String estado = campos[5].trim();
            String idOrigen = campos[6].trim();
            String idDestino = campos[7].trim();
            String prioridad = campos[8].trim();

            // 3. Validar que bibliotecas existen
            if (!grafo.existeBiblioteca(idOrigen)) {
                String error = "Biblioteca origen no existe: " + idOrigen;
                resultado.agregarError(titulo, error);
                if (progresoCallback != null) {
                    progresoCallback.reportarLinea("❌ Error con libro '" + titulo + "': " + error, "error");
                }
                return;
            }

            if (!grafo.existeBiblioteca(idDestino)) {
                String error = "Biblioteca destino no existe: " + idDestino;
                resultado.agregarError(titulo, error);
                if (progresoCallback != null) {
                    progresoCallback.reportarLinea("❌ Error con libro '" + titulo + "': " + error, "error");
                }
                return;
            }

            // 4. Crear objeto Libro (NO agregar a biblioteca origen)
            Libro libro = new Libro(titulo, isbn, genero, año, autor);

            // 5. Configurar libro para envío inmediato
            configurarLibroParaEnvio(libro, idOrigen, idDestino, prioridad);

            // 6. Iniciar envío a través del coordinador
            boolean exito = coordinador.iniciarEnvioLibro(libro, idOrigen, idDestino, prioridad);

            if (exito) {
                // ✅ INCREMENTAR ÉXITOS
                resultado.incrementarExitosos();
                if (progresoCallback != null) {
                    progresoCallback.reportarLinea("✅ Libro '" + titulo + "' encolado para envío de " + idOrigen + " a " + idDestino, "ok");
                }
            } else {
                // ❌ INCREMENTAR FALLOS
                String error = "No se pudo iniciar el envío";
                resultado.agregarError(titulo, error);
                if (progresoCallback != null) {
                    progresoCallback.reportarLinea("❌ Error con libro '" + titulo + "': " + error, "error");
                }
            }

        } catch (Exception e) {
            String error = "Error procesando línea: " + e.getMessage();
            resultado.agregarError("Línea " + numeroLinea, error);
            if (progresoCallback != null) {
                progresoCallback.reportarLinea("❌ Error en línea " + numeroLinea + ": " + error, "error");
            }
        }
    }

    private String[] parsearLineaCSV(String linea) {
        List<String> campos = new ArrayList<>();
        StringBuilder campoActual = new StringBuilder();
        boolean entreComillas = false;

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);

            if (c == '"') {
                entreComillas = !entreComillas;
            } else if (c == ',' && !entreComillas) {
                campos.add(campoActual.toString());
                campoActual = new StringBuilder();
            } else {
                campoActual.append(c);
            }
        }

        // Agregar el último campo
        campos.add(campoActual.toString());

        return campos.toArray(new String[0]);
    }

    private void configurarLibroParaEnvio(Libro libro, String origen, String destino, String prioridad) {

        // Configurar libro para envío
        libro.setIdBibliotecaOrigen(origen);
        libro.setIdBibliotecaDestino(destino);
        libro.setPrioridad(prioridad);
        libro.setRuta(null);
        libro.setIndiceRutaActual(0);
        libro.setEstado("En tránsito");
    }

    // Clase para resultados de carga
    public static class ResultadoCarga {
        private int exitosos;
        private int fallidos;
        private List<String> errores;

        public ResultadoCarga() {
            this.exitosos = 0;
            this.fallidos = 0;
            this.errores = new ArrayList<>();
        }

        public void incrementarExitosos() { exitosos++; }
        public void incrementarFallidos() { fallidos++; }

        public void agregarError(String libro, String error) {
            errores.add(libro + ": " + error);
            fallidos++;
        }

        // Getters
        public int getExitosos() { return exitosos; }
        public int getFallidos() { return fallidos; }
        public List<String> getErrores() { return errores; }

        @Override
        public String toString() {
            return "ResultadoCarga{" +
                    "exitosos=" + exitosos +
                    ", fallidos=" + fallidos +
                    ", errores=" + errores.size() +
                    '}';
        }
    }

    // Método para mostrar reporte completo
    public void mostrarReporte(ResultadoCarga resultado) {
        System.out.println("\n=== REPORTE DE CARGA CSV ===");
        System.out.println("📊 Libros procesados exitosamente: " + resultado.getExitosos());
        System.out.println("❌ Libros con errores: " + resultado.getFallidos());

        if (!resultado.getErrores().isEmpty()) {
            System.out.println("\n--- Errores encontrados ---");
            for (String error : resultado.getErrores()) {
                System.out.println("  • " + error);
            }
        }

        System.out.println("============================\n");
    }

    public interface ProgresoCallback {
        void reportarLinea(String mensaje, String tipo);
    }

    private ProgresoCallback progresoCallback;

    public void setProgresoCallback(ProgresoCallback callback) {
        this.progresoCallback = callback;
    }
}