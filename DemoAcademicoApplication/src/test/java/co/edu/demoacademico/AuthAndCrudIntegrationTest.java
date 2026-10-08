package co.edu.demoacademico;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.demoacademico.model.Curso;
import co.edu.demoacademico.model.Estudiante;
import co.edu.demoacademico.model.Rol;
import co.edu.demoacademico.model.Usuario;
import co.edu.demoacademico.repositories.CursoRepository;
import co.edu.demoacademico.repositories.EstudianteRepository;
import co.edu.demoacademico.repositories.MatriculaRepository;
import co.edu.demoacademico.repositories.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthAndCrudIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired EstudianteRepository estudianteRepository;
    @Autowired CursoRepository cursoRepository;
    @Autowired MatriculaRepository matriculaRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        matriculaRepository.deleteAll();
        estudianteRepository.deleteAll();
        cursoRepository.deleteAll();
        usuarioRepository.deleteAll();

        usuarioRepository.save(usuario("admin", "admin@test.com", "admin123", Rol.ADMIN));
        usuarioRepository.save(usuario("docente", "docente@test.com", "docente123", Rol.DOCENTE));
    }

    private Usuario usuario(String username, String email, String password, Rol rol) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setRol(rol);
        u.setActivo(true);
        return u;
    }

    private String login(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.rol").exists())
                .andReturn();
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("token").asText();
    }

    @Test
    void loginInvalidoDevuelve401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"mala"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    @Test
    void endpointProtegidoSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/estudiantes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void escrituraConRolInsuficienteDevuelve403() throws Exception {
        String token = login("docente", "docente123");
        mockMvc.perform(post("/api/estudiantes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","apellido":"Perez","email":"ana@test.com"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminPuedeCrearYListarEstudiantes() throws Exception {
        String token = login("admin", "admin123");
        mockMvc.perform(post("/api/estudiantes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","apellido":"Perez","email":"ana@test.com"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ana@test.com"));

        mockMvc.perform(get("/api/estudiantes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].apellido").value("Perez"));
    }

    @Test
    void emailDuplicadoDevuelve409() throws Exception {
        String token = login("admin", "admin123");
        String body = """
                {"nombre":"Ana","apellido":"Perez","email":"dup@test.com"}
                """;
        mockMvc.perform(post("/api/estudiantes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/estudiantes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void cursoConCreditosInvalidosDevuelveError() throws Exception {
        String token = login("admin", "admin123");
        mockMvc.perform(post("/api/cursos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"codigo":"X1","nombre":"Curso","descripcion":"d","creditos":0}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void matriculaDuplicadaActivaYAnulacion() throws Exception {
        String token = login("admin", "admin123");

        Estudiante est = new Estudiante();
        est.setNombre("Luis");
        est.setApellido("Diaz");
        est.setEmail("luis@test.com");
        est = estudianteRepository.save(est);

        Curso curso = new Curso();
        curso.setCodigo("C100");
        curso.setNombre("Curso Test");
        curso.setDescripcion("desc");
        curso.setCreditos(3);
        curso = cursoRepository.save(curso);

        String body = """
                {"estudianteId":%d,"cursoId":%d}
                """.formatted(est.getId(), curso.getId());

        MvcResult created = mockMvc.perform(post("/api/matriculas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("ACTIVA"))
                .andReturn();

        mockMvc.perform(post("/api/matriculas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());

        long matriculaId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(put("/api/matriculas/" + matriculaId + "/anular")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ANULADA"))
                .andExpect(jsonPath("$.id").value(matriculaId));

        mockMvc.perform(get("/api/matriculas/" + matriculaId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ANULADA"));
    }

    @Test
    void usuarioNoExponePasswordHash() throws Exception {
        String token = login("admin", "admin123");
        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].passwordHash").doesNotExist())
                .andExpect(jsonPath("$[0].username").exists());

        mockMvc.perform(delete("/api/usuarios/9999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
