package com.biblioteca;

public class Libro extends Documento {

    private String autor;
    private String isbn;
    private String editorial;

    public Libro(int id, String titulo, String ubicacionFisica,
                 int ejemplaresDisponibles, int ejemplaresTotales,
                 String autor, String isbn, String editorial) {
        super(id, titulo, ubicacionFisica, ejemplaresDisponibles, ejemplaresTotales);
        this.autor = autor;
        this.isbn = isbn;
        this.editorial = editorial;
    }

    // Getters
    public String getAutor() {
        return autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getEditorial() {
        return editorial;
    }

    // Setters
    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + getId() +
                ", titulo='" + getTitulo() + '\'' +
                ", ubicacionFisica='" + getUbicacionFisica() + '\'' +
                ", ejemplaresDisponibles=" + getEjemplaresDisponibles() +
                ", ejemplaresTotales=" + getEjemplaresTotales() +
                ", autor='" + autor + '\'' +
                ", isbn='" + isbn + '\'' +
                ", editorial='" + editorial + '\'' +
                '}';
    }
}
