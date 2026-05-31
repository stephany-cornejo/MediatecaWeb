package com.mediateca.domain;

public abstract class DocumentoBase {

    private int id;
    private String titulo;
    private String ubicacionFisica;
    private int ejemplaresDisponibles;
    private int ejemplaresTotales;

    protected DocumentoBase(int id, String titulo, String ubicacionFisica, int ejemplaresDisponibles, int ejemplaresTotales) {
        this.id = id;
        this.titulo = titulo;
        this.ubicacionFisica = ubicacionFisica;
        this.ejemplaresDisponibles = ejemplaresDisponibles;
        this.ejemplaresTotales = ejemplaresTotales;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUbicacionFisica() {
        return ubicacionFisica;
    }

    public void setUbicacionFisica(String ubicacionFisica) {
        this.ubicacionFisica = ubicacionFisica;
    }

    public int getEjemplaresDisponibles() {
        return ejemplaresDisponibles;
    }

    public void setEjemplaresDisponibles(int ejemplaresDisponibles) {
        this.ejemplaresDisponibles = ejemplaresDisponibles;
    }

    public int getEjemplaresTotales() {
        return ejemplaresTotales;
    }

    public void setEjemplaresTotales(int ejemplaresTotales) {
        this.ejemplaresTotales = ejemplaresTotales;
    }
}

