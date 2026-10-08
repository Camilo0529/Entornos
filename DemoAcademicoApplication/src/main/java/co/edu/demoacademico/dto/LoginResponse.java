package co.edu.demoacademico.dto;

import co.edu.demoacademico.model.Rol;

public class LoginResponse {

    private String token;
    private String tipo = "Bearer";
    private Rol rol;
    private long expiraEn;

    public LoginResponse() {}

    public LoginResponse(String token, Rol rol, long expiraEn) {
        this.token = token;
        this.rol = rol;
        this.expiraEn = expiraEn;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public long getExpiraEn() { return expiraEn; }
    public void setExpiraEn(long expiraEn) { this.expiraEn = expiraEn; }
}
