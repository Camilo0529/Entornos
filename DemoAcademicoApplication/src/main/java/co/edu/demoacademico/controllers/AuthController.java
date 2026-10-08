package co.edu.demoacademico.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.demoacademico.dto.LoginRequest;
import co.edu.demoacademico.dto.LoginResponse;
import co.edu.demoacademico.model.Usuario;
import co.edu.demoacademico.repositories.UsuarioRepository;
import co.edu.demoacademico.security.JwtService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            Usuario usuario = usuarioRepository.findByUsername(authentication.getName())
                    .orElseThrow();

            String token = jwtService.generateToken(usuario.getUsername(), usuario.getRol());
            LoginResponse response = new LoginResponse(token, usuario.getRol(), jwtService.getExpirationMs());
            return ResponseEntity.ok(response);
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", 401,
                            "error", "Unauthorized",
                            "message", "Credenciales inválidas"
                    ));
        }
    }
}
