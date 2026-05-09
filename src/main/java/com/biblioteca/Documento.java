package com.biblioteca;

public abstract class Documento {

    private int id;
    private String titulo;
    private String ubicacionFisica;
    private int ejemplaresDisponibles;
    private int ejemplaresTotales;

    public Documento(int id, String titulo, String ubicacionFisica,
                     int ejemplaresDisponibles, int ejemplaresTotales) {
        this.id = id;
        this.titulo = titulo;
        this.ubicacionFisica = ubicacionFisica;
        this.ejemplaresDisponibles = ejemplaresDisponibles;
        this.ejemplaresTotales = ejemplaresTotales;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getUbicacionFisica() {
        return ubicacionFisica;
    }

    public int getEjemplaresDisponibles() {
        return ejemplaresDisponibles;
    }

    public int getEjemplaresTotales() {
        return ejemplaresTotales;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setUbicacionFisica(String ubicacionFisica) {
        this.ubicacionFisica = ubicacionFisica;
    }

    public void setEjemplaresDisponibles(int ejemplaresDisponibles) {
        this.ejemplaresDisponibles = ejemplaresDisponibles;
    }

    public void setEjemplaresTotales(int ejemplaresTotales) {
        this.ejemplaresTotales = ejemplaresTotales;
    }

    @Override
    public String toString() {
        return "Documento{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", ubicacionFisica='" + ubicacionFisica + '\'' +
                ", ejemplaresDisponibles=" + ejemplaresDisponibles +
                ", ejemplaresTotales=" + ejemplaresTotales +
                '}';
    }
}
