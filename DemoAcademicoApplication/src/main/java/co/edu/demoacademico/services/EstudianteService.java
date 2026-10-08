package co.edu.demoacademico.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.demoacademico.exception.RecursoNoEncontradoException;
import co.edu.demoacademico.exception.ReglaNegocioException;
import co.edu.demoacademico.model.Estudiante;
import co.edu.demoacademico.repositories.EstudianteRepository;
import co.edu.demoacademico.repositories.MatriculaRepository;

@Service
public class EstudianteService {

    private final EstudianteRepository repository;
    private final MatriculaRepository matriculaRepository;

    public EstudianteService(EstudianteRepository repository, MatriculaRepository matriculaRepository) {
        this.repository = repository;
        this.matriculaRepository = matriculaRepository;
    }

    public Estudiante crear(Estudiante estudiante) {
        if (repository.existsByEmail(estudiante.getEmail())) {
            throw new ReglaNegocioException("Ya existe un estudiante con el email: " + estudiante.getEmail());
        }
        return repository.save(estudiante);
    }

    public List<Estudiante> listar() {
        return repository.findAll();
    }

    public Page<Estudiante> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Estudiante obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado: " + id));
    }

    public Estudiante buscarPorEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró estudiante con el email: " + email));
    }

    public Estudiante actualizar(Long id, Estudiante datos) {
        Estudiante existente = obtenerPorId(id);
        if (repository.existsByEmailAndIdNot(datos.getEmail(), id)) {
            throw new ReglaNegocioException("Ya existe un estudiante con el email: " + datos.getEmail());
        }
        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setEmail(datos.getEmail());
        return repository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNoEncontradoException("Estudiante no encontrado: " + id);
        }
        // Las matrículas dependen del estudiante; se eliminan para respetar la FK de MySQL.
        matriculaRepository.deleteByEstudianteId(id);
        repository.deleteById(id);
    }
}
