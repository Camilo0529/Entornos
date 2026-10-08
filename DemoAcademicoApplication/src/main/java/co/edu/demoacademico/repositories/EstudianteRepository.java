package co.edu.demoacademico.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.demoacademico.model.Estudiante;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Estudiante> findByEmail(String email);
}
