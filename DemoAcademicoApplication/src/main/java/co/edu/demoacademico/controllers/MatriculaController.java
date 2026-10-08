package co.edu.demoacademico.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import co.edu.demoacademico.dto.MatriculaRequestDTO;
import co.edu.demoacademico.model.Matricula;
import co.edu.demoacademico.services.MatriculaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    private final MatriculaService service;

    public MatriculaController(MatriculaService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Matricula crear(@Valid @RequestBody MatriculaRequestDTO request) {
        return service.crear(request);
    }

    @GetMapping
    public List<Matricula> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Matricula obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PutMapping("/{id}/anular")
    public Matricula anular(@PathVariable Long id) {
        return service.anular(id);
    }
}
