package co.edu.demoacademico.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.demoacademico.model.EstadoMatricula;
import co.edu.demoacademico.model.Matricula;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    boolean existsByEstudianteIdAndCursoIdAndEstado(Long estudianteId, Long cursoId, EstadoMatricula estado);

    boolean existsByEstudianteId(Long estudianteId);

    boolean existsByCursoId(Long cursoId);

    void deleteByEstudianteId(Long estudianteId);

    void deleteByCursoId(Long cursoId);
}
