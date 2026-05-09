package com.biblioteca;

public class CD extends Documento {

    private String genero;
    private int duracion; // duración en segundos

    public CD(int id, String titulo, String ubicacionFisica,
              int ejemplaresDisponibles, int ejemplaresTotales,
              String genero, int duracion) {
        super(id, titulo, ubicacionFisica, ejemplaresDisponibles, ejemplaresTotales);
        this.genero = genero;
        this.duracion = duracion;
    }

    // Getters
    public String getGenero() {
        return genero;
    }

    public int getDuracion() {
        return duracion;
    }

    // Setters
    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    @Override
    public String toString() {
        return "CD{" +
                "id=" + getId() +
                ", titulo='" + getTitulo() + '\'' +
                ", ubicacionFisica='" + getUbicacionFisica() + '\'' +
                ", ejemplaresDisponibles=" + getEjemplaresDisponibles() +
                ", ejemplaresTotales=" + getEjemplaresTotales() +
                ", genero='" + genero + '\'' +
                ", duracion=" + duracion + "s" +
                '}';
    }
}
