package co.edu.demoacademico.dto;

import jakarta.validation.constraints.NotNull;

public class MatriculaRequestDTO {

    @NotNull(message = "El id del estudiante es obligatorio")
    private Long estudianteId;

    @NotNull(message = "El id del curso es obligatorio")
    private Long cursoId;

    public Long getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Long estudianteId) { this.estudianteId = estudianteId; }

    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }
}
