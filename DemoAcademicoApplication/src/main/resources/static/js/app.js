import { api } from './api.js';
import { requireAuth, getRol, isAdmin, logout } from './auth.js';

requireAuth();

const msgEl = document.getElementById('msg');
document.getElementById('userInfo').textContent = `Rol: ${getRol()}`;
document.getElementById('btnLogout').addEventListener('click', logout);

if (!isAdmin()) {
  document.querySelectorAll('.admin-only').forEach((el) => el.classList.add('hidden'));
}

function showMsg(text, ok = false) {
  msgEl.textContent = text;
  msgEl.className = `msg show ${ok ? 'ok' : 'error'}`;
}

function clearMsg() {
  msgEl.className = 'msg';
  msgEl.textContent = '';
}

function esc(value) {
  return String(value ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;');
}

document.querySelectorAll('.tab').forEach((btn) => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.tab').forEach((b) => b.classList.remove('active'));
    document.querySelectorAll('.panel').forEach((p) => p.classList.add('hidden'));
    btn.classList.add('active');
    document.getElementById(`tab-${btn.dataset.tab}`).classList.remove('hidden');
    clearMsg();
  });
});

/* ========== Estudiantes ========== */
async function loadEstudiantes() {
  const data = await api('/api/estudiantes');
  const rows = data.map((e) => `
    <tr>
      <td>${e.id}</td>
      <td>${esc(e.nombre)}</td>
      <td>${esc(e.apellido)}</td>
      <td>${esc(e.email)}</td>
      <td class="actions admin-only">
        <button type="button" data-edit-est="${e.id}">Editar</button>
        <button type="button" class="danger" data-del-est="${e.id}">Eliminar</button>
      </td>
    </tr>`).join('');

  document.getElementById('estudiantesTable').innerHTML = `
    <table>
      <thead><tr><th>ID</th><th>Nombre</th><th>Apellido</th><th>Email</th><th></th></tr></thead>
      <tbody>${rows || '<tr><td colspan="5">Sin datos</td></tr>'}</tbody>
    </table>`;

  if (!isAdmin()) {
    document.querySelectorAll('#estudiantesTable .admin-only').forEach((el) => el.classList.add('hidden'));
  }

  document.querySelectorAll('[data-edit-est]').forEach((btn) => {
    btn.addEventListener('click', async () => {
      const est = await api(`/api/estudiantes/${btn.dataset.editEst}`);
      document.getElementById('estId').value = est.id;
      document.getElementById('estNombre').value = est.nombre;
      document.getElementById('estApellido').value = est.apellido;
      document.getElementById('estEmail').value = est.email;
      document.getElementById('estFormTitle').textContent = 'Editar estudiante';
    });
  });

  document.querySelectorAll('[data-del-est]').forEach((btn) => {
    btn.addEventListener('click', async () => {
      if (!confirm('¿Eliminar estudiante?')) return;
      try {
        await api(`/api/estudiantes/${btn.dataset.delEst}`, { method: 'DELETE' });
        showMsg('Estudiante eliminado', true);
        await loadEstudiantes();
        await fillMatriculaSelects();
      } catch (err) {
        showMsg(err.message);
      }
    });
  });
}

function resetEstForm() {
  document.getElementById('estForm').reset();
  document.getElementById('estId').value = '';
  document.getElementById('estFormTitle').textContent = 'Nuevo estudiante';
}

document.getElementById('estCancel').addEventListener('click', resetEstForm);
document.getElementById('estForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const id = document.getElementById('estId').value;
  const body = {
    nombre: document.getElementById('estNombre').value.trim(),
    apellido: document.getElementById('estApellido').value.trim(),
    email: document.getElementById('estEmail').value.trim()
  };
  try {
    if (id) {
      await api(`/api/estudiantes/${id}`, { method: 'PUT', body: JSON.stringify(body) });
      showMsg('Estudiante actualizado', true);
    } else {
      await api('/api/estudiantes', { method: 'POST', body: JSON.stringify(body) });
      showMsg('Estudiante creado', true);
    }
    resetEstForm();
    await loadEstudiantes();
    await fillMatriculaSelects();
  } catch (err) {
    showMsg(err.message);
  }
});

/* ========== Cursos ========== */
async function loadCursos() {
  const data = await api('/api/cursos');
  const rows = data.map((c) => `
    <tr>
      <td>${c.id}</td>
      <td>${esc(c.codigo)}</td>
      <td>${esc(c.nombre)}</td>
      <td>${c.creditos}</td>
      <td class="actions admin-only">
        <button type="button" data-edit-curso="${c.id}">Editar</button>
        <button type="button" class="danger" data-del-curso="${c.id}">Eliminar</button>
      </td>
    </tr>`).join('');

  document.getElementById('cursosTable').innerHTML = `
    <table>
      <thead><tr><th>ID</th><th>Código</th><th>Nombre</th><th>Créditos</th><th></th></tr></thead>
      <tbody>${rows || '<tr><td colspan="5">Sin datos</td></tr>'}</tbody>
    </table>`;

  if (!isAdmin()) {
    document.querySelectorAll('#cursosTable .admin-only').forEach((el) => el.classList.add('hidden'));
  }

  document.querySelectorAll('[data-edit-curso]').forEach((btn) => {
    btn.addEventListener('click', async () => {
      const c = await api(`/api/cursos/${btn.dataset.editCurso}`);
      document.getElementById('cursoId').value = c.id;
      document.getElementById('cursoCodigo').value = c.codigo;
      document.getElementById('cursoNombre').value = c.nombre;
      document.getElementById('cursoDescripcion').value = c.descripcion || '';
      document.getElementById('cursoCreditos').value = c.creditos;
      document.getElementById('cursoFormTitle').textContent = 'Editar curso';
    });
  });

  document.querySelectorAll('[data-del-curso]').forEach((btn) => {
    btn.addEventListener('click', async () => {
      if (!confirm('¿Eliminar curso?')) return;
      try {
        await api(`/api/cursos/${btn.dataset.delCurso}`, { method: 'DELETE' });
        showMsg('Curso eliminado', true);
        await loadCursos();
        await fillMatriculaSelects();
      } catch (err) {
        showMsg(err.message);
      }
    });
  });
}

function resetCursoForm() {
  document.getElementById('cursoForm').reset();
  document.getElementById('cursoId').value = '';
  document.getElementById('cursoFormTitle').textContent = 'Nuevo curso';
}

document.getElementById('cursoCancel').addEventListener('click', resetCursoForm);
document.getElementById('cursoForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const id = document.getElementById('cursoId').value;
  const body = {
    codigo: document.getElementById('cursoCodigo').value.trim(),
    nombre: document.getElementById('cursoNombre').value.trim(),
    descripcion: document.getElementById('cursoDescripcion').value.trim(),
    creditos: Number(document.getElementById('cursoCreditos').value)
  };
  try {
    if (id) {
      await api(`/api/cursos/${id}`, { method: 'PUT', body: JSON.stringify(body) });
      showMsg('Curso actualizado', true);
    } else {
      await api('/api/cursos', { method: 'POST', body: JSON.stringify(body) });
      showMsg('Curso creado', true);
    }
    resetCursoForm();
    await loadCursos();
    await fillMatriculaSelects();
  } catch (err) {
    showMsg(err.message);
  }
});

/* ========== Matrículas ========== */
async function fillMatriculaSelects() {
  const [estudiantes, cursos] = await Promise.all([
    api('/api/estudiantes'),
    api('/api/cursos')
  ]);
  document.getElementById('matEstudiante').innerHTML = estudiantes
    .map((e) => `<option value="${e.id}">${esc(e.nombre)} ${esc(e.apellido)} (${e.id})</option>`)
    .join('');
  document.getElementById('matCurso').innerHTML = cursos
    .map((c) => `<option value="${c.id}">${esc(c.codigo)} - ${esc(c.nombre)}</option>`)
    .join('');
}

async function loadMatriculas() {
  const data = await api('/api/matriculas');
  const rows = data.map((m) => `
    <tr>
      <td>${m.id}</td>
      <td>${esc(m.estudiante?.nombre)} ${esc(m.estudiante?.apellido)}</td>
      <td>${esc(m.curso?.codigo)} - ${esc(m.curso?.nombre)}</td>
      <td>${esc(m.estado)}</td>
      <td class="actions admin-only">
        ${m.estado === 'ACTIVA'
          ? `<button type="button" class="danger" data-anular="${m.id}">Anular</button>`
          : '<span class="muted">—</span>'}
      </td>
    </tr>`).join('');

  document.getElementById('matriculasTable').innerHTML = `
    <table>
      <thead><tr><th>ID</th><th>Estudiante</th><th>Curso</th><th>Estado</th><th></th></tr></thead>
      <tbody>${rows || '<tr><td colspan="5">Sin datos</td></tr>'}</tbody>
    </table>`;

  if (!isAdmin()) {
    document.querySelectorAll('#matriculasTable .admin-only').forEach((el) => el.classList.add('hidden'));
  }

  document.querySelectorAll('[data-anular]').forEach((btn) => {
    btn.addEventListener('click', async () => {
      if (!confirm('¿Anular matrícula?')) return;
      try {
        await api(`/api/matriculas/${btn.dataset.anular}/anular`, { method: 'PUT' });
        showMsg('Matrícula anulada', true);
        await loadMatriculas();
      } catch (err) {
        showMsg(err.message);
      }
    });
  });
}

document.getElementById('matForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const body = {
    estudianteId: Number(document.getElementById('matEstudiante').value),
    cursoId: Number(document.getElementById('matCurso').value)
  };
  try {
    await api('/api/matriculas', { method: 'POST', body: JSON.stringify(body) });
    showMsg('Matrícula creada', true);
    await loadMatriculas();
  } catch (err) {
    showMsg(err.message);
  }
});

/* ========== Usuarios ========== */
async function loadUsuarios() {
  const data = await api('/api/usuarios');
  const rows = data.map((u) => `
    <tr>
      <td>${u.id}</td>
      <td>${esc(u.username)}</td>
      <td>${esc(u.email)}</td>
      <td>${esc(u.rol)}</td>
      <td>${u.activo ? 'Sí' : 'No'}</td>
      <td class="actions admin-only">
        <button type="button" data-edit-usr="${u.id}">Editar</button>
        <button type="button" class="danger" data-del-usr="${u.id}">Eliminar</button>
      </td>
    </tr>`).join('');

  document.getElementById('usuariosTable').innerHTML = `
    <table>
      <thead><tr><th>ID</th><th>Username</th><th>Email</th><th>Rol</th><th>Activo</th><th></th></tr></thead>
      <tbody>${rows || '<tr><td colspan="6">Sin datos</td></tr>'}</tbody>
    </table>`;

  if (!isAdmin()) {
    document.querySelectorAll('#usuariosTable .admin-only').forEach((el) => el.classList.add('hidden'));
  }

  document.querySelectorAll('[data-edit-usr]').forEach((btn) => {
    btn.addEventListener('click', async () => {
      const u = await api(`/api/usuarios/${btn.dataset.editUsr}`);
      document.getElementById('usrId').value = u.id;
      document.getElementById('usrUsername').value = u.username;
      document.getElementById('usrEmail').value = u.email;
      document.getElementById('usrPassword').value = '';
      document.getElementById('usrRol').value = u.rol;
      document.getElementById('usrActivo').value = String(u.activo);
      document.getElementById('usrFormTitle').textContent = 'Editar usuario';
    });
  });

  document.querySelectorAll('[data-del-usr]').forEach((btn) => {
    btn.addEventListener('click', async () => {
      if (!confirm('¿Eliminar usuario?')) return;
      try {
        await api(`/api/usuarios/${btn.dataset.delUsr}`, { method: 'DELETE' });
        showMsg('Usuario eliminado', true);
        await loadUsuarios();
      } catch (err) {
        showMsg(err.message);
      }
    });
  });
}

function resetUsrForm() {
  document.getElementById('usrForm').reset();
  document.getElementById('usrId').value = '';
  document.getElementById('usrFormTitle').textContent = 'Nuevo usuario';
}

document.getElementById('usrCancel').addEventListener('click', resetUsrForm);
document.getElementById('usrForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const id = document.getElementById('usrId').value;
  const body = {
    username: document.getElementById('usrUsername').value.trim(),
    email: document.getElementById('usrEmail').value.trim(),
    password: document.getElementById('usrPassword').value,
    rol: document.getElementById('usrRol').value,
    activo: document.getElementById('usrActivo').value === 'true'
  };
  try {
    if (id) {
      await api(`/api/usuarios/${id}`, { method: 'PUT', body: JSON.stringify(body) });
      showMsg('Usuario actualizado', true);
    } else {
      await api('/api/usuarios', { method: 'POST', body: JSON.stringify(body) });
      showMsg('Usuario creado', true);
    }
    resetUsrForm();
    await loadUsuarios();
  } catch (err) {
    showMsg(err.message);
  }
});

async function init() {
  try {
    await Promise.all([
      loadEstudiantes(),
      loadCursos(),
      loadMatriculas(),
      loadUsuarios(),
      fillMatriculaSelects()
    ]);
  } catch (err) {
    showMsg(err.message);
  }
}

init();
