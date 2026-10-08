package co.edu.demoacademico.controllers;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import co.edu.demoacademico.model.Estudiante;
import co.edu.demoacademico.services.EstudianteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private final EstudianteService service;

    public EstudianteController(EstudianteService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Estudiante crear(@Valid @RequestBody Estudiante estudiante) {
        return service.crear(estudiante);
    }

    @GetMapping
    public List<Estudiante> listar() {
        return service.listar();
    }

    @GetMapping("/pagina")
    public Page<Estudiante> listarPaginado(@ParameterObject Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/buscar")
    public Estudiante buscarPorEmail(@RequestParam String email) {
        return service.buscarPorEmail(email);
    }

    @GetMapping("/{id}")
    public Estudiante obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public Estudiante actualizar(@PathVariable Long id, @Valid @RequestBody Estudiante estudiante) {
        return service.actualizar(id, estudiante);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
