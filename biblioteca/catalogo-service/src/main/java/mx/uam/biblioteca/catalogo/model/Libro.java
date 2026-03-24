package mx.uam.biblioteca.catalogo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Libro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titulo;
    private String autor;
    private String isbn;
    private int inventarioTotal;
    private int inventarioDisponible;

    public Libro() {}

    public Libro(String titulo, String autor, String isbn, int inventario) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.inventarioTotal = inventario;
        this.inventarioDisponible = inventario;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public int getInventarioTotal() { return inventarioTotal; }
    public void setInventarioTotal(int inventarioTotal) { this.inventarioTotal = inventarioTotal; }
    
    public int getInventarioDisponible() { return inventarioDisponible; }
    public void setInventarioDisponible(int inventarioDisponible) { this.inventarioDisponible = inventarioDisponible; }
}
