package co.edu.demoacademico.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.javafaker.Faker;

import co.edu.demoacademico.model.Curso;
import co.edu.demoacademico.model.EstadoMatricula;
import co.edu.demoacademico.model.Estudiante;
import co.edu.demoacademico.model.Matricula;
import co.edu.demoacademico.model.Rol;
import co.edu.demoacademico.model.Usuario;
import co.edu.demoacademico.repositories.CursoRepository;
import co.edu.demoacademico.repositories.EstudianteRepository;
import co.edu.demoacademico.repositories.MatriculaRepository;
import co.edu.demoacademico.repositories.UsuarioRepository;

@Configuration
public class DataSeeder {

    @Value("${app.seed.enabled:true}")
    private boolean enabled;

    @Value("${app.seed.cantidad:20}")
    private int cantidad;

    @Bean
    CommandLineRunner seedData(UsuarioRepository usuarioRepository,
                               EstudianteRepository estudianteRepository,
                               CursoRepository cursoRepository,
                               MatriculaRepository matriculaRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (!enabled) {
                return;
            }

            seedUsuarios(usuarioRepository, passwordEncoder);
            seedCursos(cursoRepository);
            seedEstudiantes(estudianteRepository);
            seedMatriculas(matriculaRepository, estudianteRepository, cursoRepository);
        };
    }

    private void seedUsuarios(UsuarioRepository repo, PasswordEncoder encoder) {
        createUsuarioIfAbsent(repo, encoder, "admin", "admin@demo.com", "admin123", Rol.ADMIN);
        createUsuarioIfAbsent(repo, encoder, "docente", "docente@demo.com", "docente123", Rol.DOCENTE);
        createUsuarioIfAbsent(repo, encoder, "estudiante", "estudiante@demo.com", "estudiante123", Rol.ESTUDIANTE);
    }

    private void createUsuarioIfAbsent(UsuarioRepository repo, PasswordEncoder encoder,
                                       String username, String email, String password, Rol rol) {
        if (repo.existsByUsername(username)) {
            return;
        }
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(password));
        u.setRol(rol);
        u.setActivo(true);
        repo.save(u);
    }

    private void seedCursos(CursoRepository repo) {
        if (repo.count() > 0) {
            return;
        }
        repo.save(curso("SW301", "Ingeniería de Software III", "Arquitectura y microservicios", 3));
        repo.save(curso("BD201", "Bases de Datos", "Modelado y SQL", 4));
        repo.save(curso("POO101", "Programación Orientada a Objetos", "Fundamentos OOP", 3));
    }

    private Curso curso(String codigo, String nombre, String descripcion, int creditos) {
        Curso c = new Curso();
        c.setCodigo(codigo);
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setCreditos(creditos);
        return c;
    }

    private void seedEstudiantes(EstudianteRepository repo) {
        if (repo.count() == 0) {
            Estudiante a = new Estudiante();
            a.setNombre("Ana");
            a.setApellido("Pérez");
            a.setEmail("ana@demo.com");
            repo.save(a);

            Estudiante b = new Estudiante();
            b.setNombre("Juan");
            b.setApellido("Gómez");
            b.setEmail("juan@demo.com");
            repo.save(b);

            Estudiante c = new Estudiante();
            c.setNombre("Carlos");
            c.setApellido("Ruiz");
            c.setEmail("carlos@demo.com");
            repo.save(c);
        }

        if (repo.count() >= cantidad) {
            return;
        }

        Faker faker = new Faker(new Locale("es"));
        int creados = 0;
        while (repo.count() < cantidad && creados < cantidad) {
            String nombre = faker.name().firstName();
            String apellido = faker.name().lastName();
            String email = ("est" + creados + "_" + faker.internet().emailAddress())
                    .toLowerCase()
                    .replace(" ", "")
                    .replace("..", ".");

            Estudiante e = new Estudiante();
            e.setNombre(nombre);
            e.setApellido(apellido);
            e.setEmail(email);
            try {
                repo.save(e);
                creados++;
            } catch (Exception ignored) {
                // reintento si el email colisiona
            }
        }
    }

    private void seedMatriculas(MatriculaRepository matriculaRepository,
                                EstudianteRepository estudianteRepository,
                                CursoRepository cursoRepository) {
        if (matriculaRepository.count() > 0) {
            return;
        }
        var estudiantes = estudianteRepository.findAll();
        var cursos = cursoRepository.findAll();
        if (estudiantes.isEmpty() || cursos.isEmpty()) {
            return;
        }

        Matricula m1 = new Matricula();
        m1.setEstudiante(estudiantes.get(0));
        m1.setCurso(cursos.get(0));
        m1.setEstado(EstadoMatricula.ACTIVA);
        matriculaRepository.save(m1);

        if (estudiantes.size() > 1 && cursos.size() > 1) {
            Matricula m2 = new Matricula();
            m2.setEstudiante(estudiantes.get(1));
            m2.setCurso(cursos.get(1));
            m2.setEstado(EstadoMatricula.ACTIVA);
            matriculaRepository.save(m2);
        }
    }
}
