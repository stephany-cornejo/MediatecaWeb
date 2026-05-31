package com.mediateca.domain;

public class Libro extends DocumentoBase {

    private String autor;
    private String isbn;
    private String editorial;

    public Libro(int id, String titulo, String ubicacionFisica, int ejemplaresDisponibles, int ejemplaresTotales,
                 String autor, String isbn, String editorial) {
        super(id, titulo, ubicacionFisica, ejemplaresDisponibles, ejemplaresTotales);
        this.autor = autor;
        this.isbn = isbn;
        this.editorial = editorial;
    }

    public String getAutor() {
        return autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getEditorial() {
        return editorial;
    }
}

