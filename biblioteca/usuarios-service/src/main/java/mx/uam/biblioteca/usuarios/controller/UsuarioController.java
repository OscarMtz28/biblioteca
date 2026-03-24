package mx.uam.biblioteca.usuarios.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.uam.biblioteca.usuarios.model.Usuario;
import mx.uam.biblioteca.usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/biblioteca/usuarios")
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios", description = "API para la gestión de usuarios de la biblioteca")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista con todos los usuarios registrados en el sistema.")
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario", description = "Registra un usuario nuevo con 0 préstamos y sin penalizaciones.")
    public Usuario createUsuario(@RequestBody Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Retorna los detalles de un usuario específico buscándolo por su ID.")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/puede-solicitar")
    @Operation(summary = "Validar disponibilidad de préstamo", description = "Verifica si el usuario no tiene penalizaciones y tiene menos de 3 préstamos activos.")
    public ResponseEntity<Boolean> puedeSolicitarPrestamo(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    boolean puede = !usuario.isPenalizado() && usuario.getPrestamosActivos() < 3;
                    return ResponseEntity.ok(puede);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/incrementar-prestamo")
    @Operation(summary = "Incrementar contador de préstamos", description = "Aumenta en 1 la cantidad de libros prestados activos que tiene el usuario.")
    public ResponseEntity<Usuario> incrementarPrestamo(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setPrestamosActivos(usuario.getPrestamosActivos() + 1);
                    return ResponseEntity.ok(usuarioRepository.save(usuario));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/decrementar-prestamo")
    @Operation(summary = "Decrementar contador de préstamos", description = "Reduce en 1 la cantidad de libros prestados activos del usuario asegurando que nunca sea menor a 0.")
    public ResponseEntity<Usuario> decrementarPrestamo(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setPrestamosActivos(Math.max(0, usuario.getPrestamosActivos() - 1));
                    return ResponseEntity.ok(usuarioRepository.save(usuario));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
