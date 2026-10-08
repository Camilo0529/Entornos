package co.edu.demoacademico.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.demoacademico.exception.RecursoNoEncontradoException;
import co.edu.demoacademico.exception.ReglaNegocioException;
import co.edu.demoacademico.model.Curso;
import co.edu.demoacademico.repositories.CursoRepository;
import co.edu.demoacademico.repositories.MatriculaRepository;

@Service
public class CursoService {

    private final CursoRepository repository;
    private final MatriculaRepository matriculaRepository;

    public CursoService(CursoRepository repository, MatriculaRepository matriculaRepository) {
        this.repository = repository;
        this.matriculaRepository = matriculaRepository;
    }

    public Curso crear(Curso curso) {
        if (curso.getCreditos() == null || curso.getCreditos() <= 0) {
            throw new ReglaNegocioException("Los créditos deben ser mayores que cero");
        }
        if (repository.existsByCodigo(curso.getCodigo())) {
            throw new ReglaNegocioException("Ya existe un curso con el código: " + curso.getCodigo());
        }
        return repository.save(curso);
    }

    public List<Curso> listar() {
        return repository.findAll();
    }

    public Curso obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado: " + id));
    }

    public Curso actualizar(Long id, Curso datos) {
        Curso existente = obtenerPorId(id);
        if (datos.getCreditos() == null || datos.getCreditos() <= 0) {
            throw new ReglaNegocioException("Los créditos deben ser mayores que cero");
        }
        if (repository.existsByCodigoAndIdNot(datos.getCodigo(), id)) {
            throw new ReglaNegocioException("Ya existe un curso con el código: " + datos.getCodigo());
        }
        existente.setCodigo(datos.getCodigo());
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setCreditos(datos.getCreditos());
        return repository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNoEncontradoException("Curso no encontrado: " + id);
        }
        // Las matrículas dependen del curso; se eliminan para respetar la FK de MySQL.
        matriculaRepository.deleteByCursoId(id);
        repository.deleteById(id);
    }
}
