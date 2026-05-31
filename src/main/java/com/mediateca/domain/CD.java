package com.mediateca.domain;

public class CD extends DocumentoBase {

    private String genero;
    private String duracion;
    private String detalle;

    public CD(int id, String titulo, String ubicacionFisica, int ejemplaresDisponibles, int ejemplaresTotales,
              String genero, String duracion, String detalle) {
        super(id, titulo, ubicacionFisica, ejemplaresDisponibles, ejemplaresTotales);
        this.genero = genero;
        this.duracion = duracion;
        this.detalle = detalle;
    }

    public String getGenero() {
        return genero;
    }

    public String getDuracion() {
        return duracion;
    }

    public String getDetalle() {
        return detalle;
    }
}


