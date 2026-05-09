package com.biblioteca;

import java.time.LocalDate;

public class Revista extends Documento {

    private String periodicidad;
    private LocalDate fechaPublicacion;

    public Revista(int id, String titulo, String ubicacionFisica,
                   int ejemplaresDisponibles, int ejemplaresTotales,
                   String periodicidad, LocalDate fechaPublicacion) {
        super(id, titulo, ubicacionFisica, ejemplaresDisponibles, ejemplaresTotales);
        this.periodicidad = periodicidad;
        this.fechaPublicacion = fechaPublicacion;
    }

    // Getters
    public String getPeriodicidad() {
        return periodicidad;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    // Setters
    public void setPeriodicidad(String periodicidad) {
        this.periodicidad = periodicidad;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    @Override
    public String toString() {
        return "Revista{" +
                "id=" + getId() +
                ", titulo='" + getTitulo() + '\'' +
                ", ubicacionFisica='" + getUbicacionFisica() + '\'' +
                ", ejemplaresDisponibles=" + getEjemplaresDisponibles() +
                ", ejemplaresTotales=" + getEjemplaresTotales() +
                ", periodicidad='" + periodicidad + '\'' +
                ", fechaPublicacion=" + fechaPublicacion +
                '}';
    }
}
