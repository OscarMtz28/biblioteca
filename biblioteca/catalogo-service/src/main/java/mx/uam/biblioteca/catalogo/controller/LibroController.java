package mx.uam.biblioteca.catalogo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.uam.biblioteca.catalogo.model.Libro;
import mx.uam.biblioteca.catalogo.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/biblioteca/libros")
@CrossOrigin(origins = "*")
@Tag(name = "Catálogo de Libros", description = "API para gestionar el inventario y catálogo de la biblioteca")
public class LibroController {

    @Autowired
    private LibroRepository libroRepository;

    @GetMapping
    @Operation(summary = "Obtener libros", description = "Retorna el catálogo completo de libros registrados.")
    public List<Libro> getAllLibros() {
        return libroRepository.findAll();
    }

    @PostMapping
    @Operation(summary = "Registrar libro", description = "Crea un nuevo registro de un libro especificando su inventario total.")
    public Libro createLibro(@RequestBody Libro libro) {
        if (libro.getInventarioDisponible() == 0) {
            libro.setInventarioDisponible(libro.getInventarioTotal());
        }
        return libroRepository.save(libro);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener libro por ID", description = "Busca un libro por su ID único.")
    public ResponseEntity<Libro> getLibroById(@PathVariable Long id) {
        return libroRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar libro", description = "Actualiza toda la información de un libro usando el ID provisto.")
    public ResponseEntity<Libro> updateLibro(@PathVariable Long id, @RequestBody Libro newLibro) {
        return libroRepository.findById(id)
                .map(libro -> {
                    libro.setTitulo(newLibro.getTitulo());
                    libro.setAutor(newLibro.getAutor());
                    libro.setIsbn(newLibro.getIsbn());
                    libro.setInventarioTotal(newLibro.getInventarioTotal());
                    libro.setInventarioDisponible(newLibro.getInventarioDisponible());
                    return ResponseEntity.ok(libroRepository.save(libro));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar libro", description = "Elimina físicamente el libro de la base de datos de manera permanente.")
    public ResponseEntity<Void> deleteLibro(@PathVariable Long id) {
        if (libroRepository.existsById(id)) {
            libroRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/prestar")
    @Operation(summary = "Sustraer inventario para préstamo", description = "Restaura 1 del inventario disponible para registrar que el libro está siendo prestado.")
    public ResponseEntity<Libro> prestarLibro(@PathVariable Long id) {
        return libroRepository.findById(id)
                .map(libro -> {
                    if (libro.getInventarioDisponible() > 0) {
                        libro.setInventarioDisponible(libro.getInventarioDisponible() - 1);
                        return ResponseEntity.ok(libroRepository.save(libro));
                    }
                    return ResponseEntity.badRequest().body(libro);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/devolver")
    @Operation(summary = "Restaurar inventario de devolución", description = "Aumenta 1 al inventario disponible para registrar que el libro regresó a la biblioteca.")
    public ResponseEntity<Libro> devolverLibro(@PathVariable Long id) {
        return libroRepository.findById(id)
                .map(libro -> {
                    if (libro.getInventarioDisponible() < libro.getInventarioTotal()) {
                        libro.setInventarioDisponible(libro.getInventarioDisponible() + 1);
                    }
                    return ResponseEntity.ok(libroRepository.save(libro));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
