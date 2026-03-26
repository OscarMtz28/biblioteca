package mx.uam.biblioteca.prestamos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.uam.biblioteca.prestamos.model.Prestamo;
import mx.uam.biblioteca.prestamos.repository.PrestamoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/biblioteca/prestamos")
@CrossOrigin(origins = "*")
@Tag(name = "Gestión de Préstamos", description = "API para registrar préstamos y devoluciones de libros a los usuarios.")
public class PrestamoController {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${usuarios.service.url}")
    private String usuariosUrl;

    @Value("${catalogo.service.url}")
    private String catalogoUrl;

    @PostMapping
    @Operation(summary = "Generar préstamo", description = "Valida y registra un préstamo verificando el usuario con el microservicio Usuarios y el inventario con el microservicio Catálogo.")
    public ResponseEntity<?> generarPrestamo(@RequestBody Prestamo peticion) {
        // 1. Validar Usuario
        String puedeSolicitarUrl = usuariosUrl + "/" + peticion.getUsuarioId() + "/puede-solicitar";
        try {
            Boolean puedeSolicitar = restTemplate.getForObject(puedeSolicitarUrl, Boolean.class);
            if (puedeSolicitar == null || !puedeSolicitar) {
                return ResponseEntity.badRequest().body("El usuario no existe, esta penalizado o alcanzó el límite de préstamos.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al validar usuario: " + e.getMessage());
        }

        // 2. Prestar Libro
        String prestarLibroUrl = catalogoUrl + "/" + peticion.getLibroId() + "/prestar";
        try {
            restTemplate.put(prestarLibroUrl, null);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al prestar libro (¿Sin stock?): " + e.getMessage());
        }

        // 3. Registrar Préstamo en el usuario
        String incrementarUrl = usuariosUrl + "/" + peticion.getUsuarioId() + "/incrementar-prestamo";
        try {
            restTemplate.put(incrementarUrl, null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar usuario");
        }

        // 4. Guardar el préstamo
        Prestamo prestamo = new Prestamo(peticion.getUsuarioId(), peticion.getLibroId(), LocalDate.now());
        Prestamo guardado = prestamoRepository.save(prestamo);
        return ResponseEntity.ok(guardado);
    }

    @PutMapping("/{id}/devolucion")
    @Operation(summary = "Marcar devolución", description = "Actualiza el préstamo como DEVUELTO y notifica a Catálogo para restaurar stock y a Usuarios para liberar límite de préstamo.")
    public ResponseEntity<?> registrarDevolucion(@PathVariable Long id) {
        return prestamoRepository.findById(id).map(prestamo -> {
            if ("DEVUELTO".equals(prestamo.getEstado())) {
                return ResponseEntity.badRequest().body("El préstamo ya está DEVUELTO.");
            }
            
            // 1. Devolver Libro
            String devolverLibroUrl = catalogoUrl + "/" + prestamo.getLibroId() + "/devolver";
            try {
                restTemplate.put(devolverLibroUrl, null);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("Error al comunicar con catalogo-service");
            }

            // 2. Decrementar prestamo del usuario
            String decrementarUrl = usuariosUrl + "/" + prestamo.getUsuarioId() + "/decrementar-prestamo";
            try {
                restTemplate.put(decrementarUrl, null);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("Error al actualizar usuario");
            }

            // 3. Actualizar estado
            prestamo.setEstado("DEVUELTO");
            prestamo.setFechaDevolucion(LocalDate.now());
            prestamoRepository.save(prestamo);
            
            return ResponseEntity.ok(prestamo);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener préstamos activos del usuario", description = "Retorna una lista con todos los libros no devueltos de un usuario específico.")
    public List<Prestamo> getPrestamosPorUsuario(@PathVariable Long usuarioId) {
        return prestamoRepository.findByUsuarioIdAndEstado(usuarioId, "ACTIVO");
    }
}
