package mx.uam.biblioteca.prestamos.repository;

import mx.uam.biblioteca.prestamos.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    List<Prestamo> findByUsuarioIdAndEstado(Long usuarioId, String estado);
}
