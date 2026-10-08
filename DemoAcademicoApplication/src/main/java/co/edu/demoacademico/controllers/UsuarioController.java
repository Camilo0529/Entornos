package co.edu.demoacademico.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import co.edu.demoacademico.dto.UsuarioRequestDTO;
import co.edu.demoacademico.dto.UsuarioResponseDTO;
import co.edu.demoacademico.services.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO crear(@Valid @RequestBody UsuarioRequestDTO request) {
        return service.crear(request);
    }

    @GetMapping
    public List<UsuarioResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public UsuarioResponseDTO obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public UsuarioResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
