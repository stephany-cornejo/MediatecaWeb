package com.mediateca.domain;

public class Tesis extends DocumentoBase {

    private String autor;
    private String carrera;
    private String detalle;

    public Tesis(int id, String titulo, String ubicacionFisica, int ejemplaresDisponibles, int ejemplaresTotales,
                 String autor, String carrera, String detalle) {
        super(id, titulo, ubicacionFisica, ejemplaresDisponibles, ejemplaresTotales);
        this.autor = autor;
        this.carrera = carrera;
        this.detalle = detalle;
    }

    public String getAutor() {
        return autor;
    }

    public String getCarrera() {
        return carrera;
    }

    public String getDetalle() {
        return detalle;
    }
}

