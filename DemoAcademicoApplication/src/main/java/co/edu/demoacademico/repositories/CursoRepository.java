package co.edu.demoacademico.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.demoacademico.model.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    Optional<Curso> findByCodigo(String codigo);
}
