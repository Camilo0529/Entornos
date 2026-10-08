package co.edu.demoacademico.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.demoacademico.dto.MatriculaRequestDTO;
import co.edu.demoacademico.exception.RecursoNoEncontradoException;
import co.edu.demoacademico.exception.ReglaNegocioException;
import co.edu.demoacademico.model.Curso;
import co.edu.demoacademico.model.EstadoMatricula;
import co.edu.demoacademico.model.Estudiante;
import co.edu.demoacademico.model.Matricula;
import co.edu.demoacademico.repositories.CursoRepository;
import co.edu.demoacademico.repositories.EstudianteRepository;
import co.edu.demoacademico.repositories.MatriculaRepository;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;

    public MatriculaService(MatriculaRepository matriculaRepository,
                            EstudianteRepository estudianteRepository,
                            CursoRepository cursoRepository) {
        this.matriculaRepository = matriculaRepository;
        this.estudianteRepository = estudianteRepository;
        this.cursoRepository = cursoRepository;
    }

    public Matricula crear(MatriculaRequestDTO request) {
        Estudiante estudiante = estudianteRepository.findById(request.getEstudianteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante no encontrado: " + request.getEstudianteId()));
        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Curso no encontrado: " + request.getCursoId()));

        if (matriculaRepository.existsByEstudianteIdAndCursoIdAndEstado(
                estudiante.getId(), curso.getId(), EstadoMatricula.ACTIVA)) {
            throw new ReglaNegocioException(
                    "Ya existe una matrícula activa para ese estudiante y curso");
        }

        Matricula matricula = new Matricula();
        matricula.setEstudiante(estudiante);
        matricula.setCurso(curso);
        matricula.setEstado(EstadoMatricula.ACTIVA);
        matricula.setFechaMatricula(LocalDateTime.now());
        return matriculaRepository.save(matricula);
    }

    public List<Matricula> listar() {
        return matriculaRepository.findAll();
    }

    public Matricula obtenerPorId(Long id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada: " + id));
    }

    public Matricula anular(Long id) {
        Matricula matricula = obtenerPorId(id);
        if (matricula.getEstado() == EstadoMatricula.ANULADA) {
            throw new ReglaNegocioException("La matrícula ya está anulada");
        }
        matricula.setEstado(EstadoMatricula.ANULADA);
        return matriculaRepository.save(matricula);
    }
}
