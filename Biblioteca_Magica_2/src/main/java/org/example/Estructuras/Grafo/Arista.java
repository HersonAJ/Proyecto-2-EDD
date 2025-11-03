package org.example.Estructuras.Grafo;

public class Arista {
    private String idOrigen;
    private String idDestino;
    private int tiempo; //en segundos
    private double costo;

    public Arista(String idOrigen, String idDestino, int tiempo, double costo) {
        this.idOrigen = idOrigen;
        this.idDestino = idDestino;
        this.tiempo = tiempo;
        this.costo = costo;
    }

    //getters y setters
    public String getIdOrigen() { return idOrigen; }
    public String getIdDestino() { return idDestino; }
    public int getTiempo() { return tiempo; }
    public double getCosto() { return costo; }

    @Override
    public String toString() {
        return idOrigen + " -> " + idDestino + " [T: " + tiempo + "s, C: " + costo + "]";
    }
}
