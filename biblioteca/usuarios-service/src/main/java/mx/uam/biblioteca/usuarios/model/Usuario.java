package mx.uam.biblioteca.usuarios.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String email;
    private boolean penalizado;
    private int prestamosActivos;

    public Usuario() {}

    public Usuario(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
        this.penalizado = false;
        this.prestamosActivos = 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public boolean isPenalizado() { return penalizado; }
    public void setPenalizado(boolean penalizado) { this.penalizado = penalizado; }
    
    public int getPrestamosActivos() { return prestamosActivos; }
    public void setPrestamosActivos(int prestamosActivos) { this.prestamosActivos = prestamosActivos; }
}
