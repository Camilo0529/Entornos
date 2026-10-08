package co.edu.demoacademico.services;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.demoacademico.dto.UsuarioRequestDTO;
import co.edu.demoacademico.dto.UsuarioResponseDTO;
import co.edu.demoacademico.exception.RecursoNoEncontradoException;
import co.edu.demoacademico.exception.ReglaNegocioException;
import co.edu.demoacademico.model.Usuario;
import co.edu.demoacademico.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponseDTO crear(UsuarioRequestDTO request) {
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ReglaNegocioException("La contraseña es obligatoria al crear un usuario");
        }
        if (repository.existsByUsername(request.getUsername())) {
            throw new ReglaNegocioException("Ya existe un usuario con el username: " + request.getUsername());
        }
        if (repository.existsByEmail(request.getEmail())) {
            throw new ReglaNegocioException("Ya existe un usuario con el email: " + request.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());
        usuario.setActivo(request.getActivo() == null || request.getActivo());
        return UsuarioResponseDTO.from(repository.save(usuario));
    }

    public List<UsuarioResponseDTO> listar() {
        return repository.findAll().stream().map(UsuarioResponseDTO::from).toList();
    }

    public UsuarioResponseDTO obtenerPorId(Long id) {
        return UsuarioResponseDTO.from(obtenerEntidad(id));
    }

    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO request) {
        Usuario existente = obtenerEntidad(id);
        if (repository.existsByUsernameAndIdNot(request.getUsername(), id)) {
            throw new ReglaNegocioException("Ya existe un usuario con el username: " + request.getUsername());
        }
        if (repository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new ReglaNegocioException("Ya existe un usuario con el email: " + request.getEmail());
        }

        existente.setUsername(request.getUsername());
        existente.setEmail(request.getEmail());
        existente.setRol(request.getRol());
        if (request.getActivo() != null) {
            existente.setActivo(request.getActivo());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existente.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        return UsuarioResponseDTO.from(repository.save(existente));
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNoEncontradoException("Usuario no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private Usuario obtenerEntidad(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));
    }
}
