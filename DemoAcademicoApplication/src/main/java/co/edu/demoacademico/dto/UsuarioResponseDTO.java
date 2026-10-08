package co.edu.demoacademico.dto;

import co.edu.demoacademico.model.Rol;
import co.edu.demoacademico.model.Usuario;

public class UsuarioResponseDTO {

    private Long id;
    private String username;
    private String email;
    private Rol rol;
    private boolean activo;

    public UsuarioResponseDTO() {}

    public static UsuarioResponseDTO from(Usuario u) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.id = u.getId();
        dto.username = u.getUsername();
        dto.email = u.getEmail();
        dto.rol = u.getRol();
        dto.activo = u.isActivo();
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
