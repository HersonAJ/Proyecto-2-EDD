package org.example.CSV;

import org.example.Estructuras.Grafo.GrafoBibliotecas;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LectorCSVConexiones {
    private GrafoBibliotecas grafo;
    private ProgresoCallback callback;

    public interface ProgresoCallback {
        void reportarLinea(String mensaje, String tipo);
    }

    public LectorCSVConexiones(GrafoBibliotecas grafo) {
        this.grafo = grafo;
    }

    public void setCallback(ProgresoCallback callback) {
        this.callback = callback;
    }

    public void cargarConexionesDesdeCSV(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int lineasProcesadas = 0;
            int lineasError = 0;

            if (callback != null) {
                callback.reportarLinea("Iniciando carga de conexiones desde: " + rutaArchivo, "info");
            }

            while ((linea = br.readLine()) != null) {
                if (procesarLinea(linea)) {
                    lineasProcesadas++;
                    if (callback != null) {
                        callback.reportarLinea("Conexión creada: " + linea, "ok");
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
                callback.reportarLinea("Error al leer archivo: " + e.getMessage(), "error");
            }
        }
    }

    private boolean procesarLinea(String linea) {
        // Validar formato básico
        if (!linea.contains("\"")) {
            return false;
        }

        // Separar campos preservando comillas
        String[] campos = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        // Validar número de campos
        if (campos.length != 4) {
            return false;
        }

        try {
            // Limpiar comillas y espacios
            String origenID = limpiarCampo(campos[0]);
            String destinoID = limpiarCampo(campos[1]);
            String tiempoStr = limpiarCampo(campos[2]);
            String costoStr = limpiarCampo(campos[3]);

            // Validar IDs (formato Letra-3números)
            if (!validarID(origenID) || !validarID(destinoID)) {
                return false;
            }

            // Validar que los IDs existan en el grafo
            if (!grafo.existeBiblioteca(origenID) || !grafo.existeBiblioteca(destinoID)) {
                return false;
            }

            // Validar formatos numéricos
            int tiempo = Integer.parseInt(tiempoStr);
            double costo = Double.parseDouble(costoStr);

            // Validar valores positivos
            if (tiempo <= 0 || costo <= 0) {
                return false;
            }

            // Intentar crear conexión (ya valida duplicados internamente)
            boolean conectada = grafo.conectarBibliotecas(origenID, destinoID, tiempo, costo);
            return conectada;

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