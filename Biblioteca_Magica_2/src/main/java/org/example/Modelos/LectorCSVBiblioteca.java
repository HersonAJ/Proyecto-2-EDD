package org.example.Modelos;

import org.example.Grafo.GrafoBibliotecas;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LectorCSVBiblioteca {
    private GrafoBibliotecas grafo;
    private ProgresoCallback callback;

    // Interface para el callback
    public interface ProgresoCallback {
        void reportarLinea(String mensaje, String tipo);
    }

    public LectorCSVBiblioteca(GrafoBibliotecas grafo) {
        this.grafo = grafo;
    }

    public void setCallback(ProgresoCallback callback) {
        this.callback = callback;
    }

    public void cargarBibliotecasDesdeCSV(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            boolean primeraLinea = true;
            int lineasProcesadas = 0;
            int lineasError = 0;

            if (callback != null) {
                callback.reportarLinea("Iniciando carga de bibliotecas desde: " + rutaArchivo, "info");
            }

            while ((linea = br.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    if (callback != null) {
                        callback.reportarLinea("Encabezado detectado, iniciando procesamiento...", "info");
                    }
                    continue;
                }

                if (procesarLinea(linea)) {
                    lineasProcesadas++;
                    if (callback != null) {
                        callback.reportarLinea("Línea procesada: " + linea, "ok");
                    }
                } else {
                    lineasError++;
                    if (callback != null) {
                        callback.reportarLinea("Error en línea: " + linea, "error");
                    }
                }
            }

            // Resumen final
            if (callback != null) {
                callback.reportarLinea("📊 Carga completada: " + lineasProcesadas + " exitosas, " + lineasError + " errores", "info");
            }

        } catch (IOException e) {
            if (callback != null) {
                callback.reportarLinea(" Error al leer archivo: " + e.getMessage(), "error");
            }
        }
    }

    // ... (el resto del código de procesarLinea, limpiarCampo, validarID se mantiene igual)
    private boolean procesarLinea(String linea) {
        if (!linea.contains("\"")) {
            return false;
        }

        String[] campos = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        if (campos.length != 6) {
            return false;
        }

        try {
            String id = limpiarCampo(campos[0]);
            String nombre = limpiarCampo(campos[1]);
            String ubicacion = limpiarCampo(campos[2]);
            String tIngresoStr = limpiarCampo(campos[3]);
            String tTraspasoStr = limpiarCampo(campos[4]);
            String intervaloStr = limpiarCampo(campos[5]);

            if (!validarID(id)) {
                return false;
            }

            int tIngreso = Integer.parseInt(tIngresoStr);
            int tTraspaso = Integer.parseInt(tTraspasoStr);
            int intervalo = Integer.parseInt(intervaloStr);

            boolean agregado = grafo.agregarBiblioteca(id, nombre, ubicacion, tIngreso, tTraspaso, intervalo);
            return agregado;

        } catch (NumberFormatException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private String limpiarCampo(String campo) {
        return campo.trim().replaceAll("^\"|\"$", "");
    }

    private boolean validarID(String id) {
        return id.matches("[A-Z]-\\d{3}");
    }
}