package com.biblioteca;

public class Tesis extends Documento {

    private String autor;
    private String carrera;

    public Tesis(int id, String titulo, String ubicacionFisica,
                 int ejemplaresDisponibles, int ejemplaresTotales,
                 String autor, String carrera) {
        super(id, titulo, ubicacionFisica, ejemplaresDisponibles, ejemplaresTotales);
        this.autor = autor;
        this.carrera = carrera;
    }

    // Getters
    public String getAutor() {
        return autor;
    }

    public String getCarrera() {
        return carrera;
    }

    // Setters
    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    @Override
    public String toString() {
        return "Tesis{" +
                "id=" + getId() +
                ", titulo='" + getTitulo() + '\'' +
                ", ubicacionFisica='" + getUbicacionFisica() + '\'' +
                ", ejemplaresDisponibles=" + getEjemplaresDisponibles() +
                ", ejemplaresTotales=" + getEjemplaresTotales() +
                ", autor='" + autor + '\'' +
                ", carrera='" + carrera + '\'' +
                '}';
    }
}
