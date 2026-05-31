package com.mediateca.domain;

public class Revista extends DocumentoBase {

    private String periodicidad;
    private String fechaPublicacion;
    private String detalle;

    public Revista(int id, String titulo, String ubicacionFisica, int ejemplaresDisponibles, int ejemplaresTotales,
                   String periodicidad, String fechaPublicacion, String detalle) {
        super(id, titulo, ubicacionFisica, ejemplaresDisponibles, ejemplaresTotales);
        this.periodicidad = periodicidad;
        this.fechaPublicacion = fechaPublicacion;
        this.detalle = detalle;
    }

    public String getPeriodicidad() {
        return periodicidad;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public String getDetalle() {
        return detalle;
    }
}

