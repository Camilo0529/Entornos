# Guía para completar el proyecto de Matrículas Académicas

## Propósito

Este documento sirve como entrada para un agente de código con acceso al repositorio existente. El proyecto ya está en desarrollo: **no asumir que está vacío ni empezar a reescribirlo sin inspección**. El objetivo es revisar su estado, proponer un plan basado en la evidencia encontrada y, siguiendo ese plan, completar una demostración funcional de autenticación con JWT y cuatro CRUDs, uno por integrante del equipo.

## Marco del proyecto

El proyecto de clase se denomina **Sistema de Gestión de Matrículas Académicas** y propone una arquitectura de microservicios desarrollada con **Spring Boot y Java 21**, persistencia en **MySQL**, documentación con **Swagger/OpenAPI** y una interfaz sencilla en **HTML, CSS y JavaScript**.

El sistema busca modernizar la gestión académica separando responsabilidades y evitando información dispersa, validaciones inconsistentes, registros duplicados y dificultades de mantenimiento y escalabilidad.

### Actores

- **Administrador:** gestiona estudiantes, cursos y matrículas.
- **Docente:** consulta cursos y matrículas/listas.
- **Estudiante:** consulta cursos y su información académica, según lo que soporte el alcance implementado.
- **Sistema de autenticación:** valida credenciales y emite tokens.

### Procesos y entidades del dominio

- **Estudiante:** nombre, apellido y correo único.
- **Curso:** código único, nombre, descripción y créditos mayores que cero.
- **Matrícula:** relaciona estudiante y curso; debe comprobar que ambos existan, impedir matrículas activas duplicadas y permitir anular cambiando su estado a `ANULADA`, sin borrar físicamente el registro.
- **Usuario/rol:** permite autenticar y aplicar permisos. Los roles de referencia son `ADMIN`, `DOCENTE` y `ESTUDIANTE`.

### Requisitos relevantes de la definición

1. El login recibe credenciales; ante éxito entrega un JWT con rol y expiración definida. La historia de usuario especifica 24 horas.
2. Los endpoints protegidos validan `Authorization: Bearer <token>`.
3. Las contraseñas se almacenan con BCrypt, nunca en texto plano.
4. Las operaciones de escritura (POST, PUT, DELETE y acciones equivalentes) requieren `ADMIN`; las lecturas requieren autenticación y admiten los roles pertinentes.
5. Se deben usar códigos HTTP estándar, errores claros y sin exponer secretos ni datos sensibles.
6. El backend sigue, en lo posible, el patrón por capas `controller-service-repository`; los microservicios se comunican por HTTP y tienen separación de datos/esquemas según el diseño existente.
7. El frontend previsto es HTML/CSS/JavaScript simple. La consigna actual exige consumir el backend con JavaScript genérico mediante `fetch` u otra opción equivalente, no necesariamente `XMLHttpRequest`.

## Objetivos de esta intervención

### A. Login y seguridad JWT de extremo a extremo

Construir o completar el login con Spring Boot como backend y conectarlo desde la interfaz existente mediante JavaScript genérico (`fetch` recomendado, salvo que el repositorio ya tenga una decisión técnica coherente).

La demostración debe cubrir:

- Formulario de usuario/correo y contraseña, conforme al contrato de autenticación existente.
- Envío al endpoint real de login y manejo de respuestas exitosas y fallidas.
- Emisión y validación de JWT, expiración definida y rol incluido como autoridad/claim.
- Protección de rutas; respuestas `401 Unauthorized` para falta/token inválido y `403 Forbidden` para rol insuficiente.
- Contraseñas verificadas con BCrypt y credenciales de demostración provisionadas de forma segura y reproducible.
- Uso del token en las llamadas protegidas del frontend (`Authorization: Bearer ...`).
- Cierre de sesión y tratamiento de expiración/error de sesión.
- No incluir secretos reales en el repositorio ni registrar contraseñas/tokens en logs.

Decidir almacenamiento del token en función de la arquitectura actual y documentar el motivo. Para una demostración académica, no inventar una afirmación de seguridad absoluta sobre `localStorage`; minimizar exposición, no persistir el token más allá de lo necesario y considerar las protecciones del frontend desplegado. Si el diseño existente permite cookie `HttpOnly` de forma viable, evaluar su integración sin desviar el alcance.

### B. Cuatro CRUDs demostrables

Debe existir **un CRUD completo por cada uno de los cuatro integrantes**. Primero inspeccionar si el repositorio, README, Jira exportado, commits u otra documentación identifica integrantes o asignaciones. Si no es posible determinar los nombres/asignaciones, mantener cuatro módulos/recursos claramente identificables y registrar en el plan que la asignación final queda pendiente de los nombres del equipo; no inventar integrantes.

Preferir como conjunto mínimo los recursos del dominio ya definidos en el PDF (por ejemplo, estudiantes, cursos, matrículas y usuarios/roles, solo si el alcance actual soporta apropiadamente el CRUD de usuarios). No añadir una entidad ajena al problema solo para llegar a cuatro. Si el proyecto ya asignó otros recursos pertinentes o ya implementó parcialmente cuatro CRUDs, respetar y completar esa organización.

Para cada CRUD elegido, verificar que la interfaz permita crear, listar, consultar/seleccionar, editar y eliminar o ejecutar la operación de baja que el dominio define. En matrículas, la baja debe ser **anulación lógica**, no eliminación física. Aplicar permisos por rol y las validaciones de negocio del documento.

## Instrucciones obligatorias para el agente de código

### Fase 1: detección del estado actual (antes de editar)

1. Inspeccionar el repositorio completo: estructura, ramas/estado de Git, README, archivos de construcción y configuración, servicios, controladores, entidades, repositorios, seguridad, frontend, pruebas, contenedores y scripts de base de datos.
2. Identificar tecnologías y versiones efectivamente usadas (Java, Spring Boot, dependencias, MySQL, frontend) y cómo se levanta el sistema.
3. Seguir el recorrido existente de login y una operación CRUD, si existe, desde interfaz hasta base de datos.
4. Localizar contratos y puertos de cada servicio, CORS, configuración de Swagger y mecanismo actual de autenticación/autorización.
5. Identificar las cuatro personas y las asignaciones de CRUD a partir de evidencia del repositorio. Separar hechos confirmados de supuestos.
6. Registrar qué ya funciona, qué está incompleto y qué impide ejecutar el proyecto. No reemplazar componentes funcionales por una implementación paralela.

### Fase 2: plan basado en hallazgos

Antes de implementar, entregar un plan breve y ordenado que incluya:

- Estado observado y evidencia (rutas/archivos y comandos relevantes).
- Brechas frente a los objetivos A y B.
- Decisiones de integración con el código actual y dependencias que se reutilizarán.
- Los cuatro CRUDs, su correspondencia con integrantes si está documentada, y brechas por CRUD.
- Cambios propuestos por etapas, dependencias entre etapas y riesgos/bloqueos.
- Estrategia de verificación y comandos de ejecución.

No detenerse tras presentar el plan si se tiene permiso para modificar el repositorio: implementar el plan, ajustándolo si las pruebas revelan hechos nuevos. Si el entorno o el flujo de trabajo exige aprobación para escribir, dejar el plan concreto y explicar la limitación.

### Fase 3: implementación incremental

- Mantener convenciones, nombres, estructura y estilo del repositorio.
- Reutilizar configuración de seguridad y componentes comunes; evitar duplicar filtros o reglas entre microservicios si existe un mecanismo central apropiado.
- Mantener secretos fuera del código fuente; usar variables de entorno/configuración externa y proporcionar valores de ejemplo no sensibles.
- Asegurar validaciones del backend; la validación en navegador solo mejora la experiencia y no sustituye la del servidor.
- No exponer hashes, contraseñas, tokens de otros usuarios ni campos internos en DTOs/respuestas.
- Mantener CORS limitado a los orígenes necesarios para el frontend.
- Usar estados HTTP apropiados y formato uniforme de error cuando el proyecto ya tenga uno.
- Actualizar documentación de configuración, ejecución y usuario/credenciales demo con instrucciones reproducibles. No introducir credenciales reales.
- No ampliar a funciones fuera del alcance salvo que sean necesarias para que los dos objetivos funcionen.

## Criterios de aceptación verificables

### Login y acceso

- [ ] El frontend permite iniciar sesión contra el endpoint real del backend.
- [ ] Credenciales inválidas producen mensaje comprensible y no exponen detalles sensibles.
- [ ] Credenciales válidas producen token JWT con expiración y rol esperados.
- [ ] Un endpoint protegido rechaza una petición sin token o con token inválido (`401`).
- [ ] Una operación de escritura con rol no autorizado devuelve `403`; con `ADMIN` funciona.
- [ ] Las contraseñas persistidas se verifican con BCrypt.
- [ ] El frontend adjunta el Bearer token a las llamadas protegidas y permite cerrar sesión.

### Cuatro CRUDs

- [ ] Hay cuatro recursos/módulos CRUD pertinentes, completos y navegables desde la interfaz.
- [ ] Cada uno cubre alta, lectura/listado, consulta individual o selección, actualización y baja acorde al dominio.
- [ ] Los cuatro están asignados a integrantes identificados por evidencia, o la falta de nombres queda explícita como pendiente y la estructura permite asignarlos.
- [ ] Las validaciones clave están en servidor: correo/código únicos, créditos positivos, referencias válidas y ausencia de matrícula activa duplicada.
- [ ] Anular matrícula conserva el registro y cambia el estado.
- [ ] Se aplican permisos por rol de manera consistente.

### Ejecución y evidencia

- [ ] El agente informa instrucciones reproducibles para levantar servicios y frontend.
- [ ] Se ejecutan las pruebas existentes y se añaden/verifican pruebas significativas de autenticación, permisos y flujos CRUD según el marco de pruebas presente.
- [ ] Se documentan los comandos y resultados reales; no declarar pruebas ejecutadas si no se ejecutaron.
- [ ] Se incluye un recorrido de demostración: login correcto, rechazo de credenciales, acceso protegido y flujo CRUD de cada integrante.

## Entregable del agente

Al terminar, reportar:

1. Resumen del estado inicial y plan seguido.
2. Cambios realizados, organizados por backend, seguridad, frontend y datos/configuración.
3. Los cuatro CRUDs y a qué integrante corresponde cada uno (o qué dato de asignación falta).
4. Endpoints y roles relevantes.
5. Comandos exactos para ejecutar y demostrar el proyecto.
6. Pruebas ejecutadas con resultado real.
7. Limitaciones o decisiones que requieran definición del equipo.

## Nota de alcance

Este brief deriva los requisitos del documento de definición del proyecto y de la consigna actual. El PDF propone tres CRUDs principales (estudiantes, cursos y matrículas), pero la consigna de entrega pide cuatro, uno por miembro. El cuarto debe seleccionarse tras inspeccionar la implementación y las asignaciones del equipo, procurando que sea coherente con el dominio y sin inventar datos de integrantes.
