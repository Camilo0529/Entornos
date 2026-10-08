/**
 * Token en sessionStorage:
 * - suficiente para una demo académica
 * - se limpia al cerrar la pestaña
 * - no afirma seguridad absoluta frente a XSS
 */
const AUTH_KEY = 'demo_academico_auth';

export function saveAuth(data) {
  sessionStorage.setItem(AUTH_KEY, JSON.stringify(data));
}

export function getAuth() {
  const raw = sessionStorage.getItem(AUTH_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

export function getToken() {
  return getAuth()?.token || null;
}

export function getRol() {
  return getAuth()?.rol || null;
}

export function isAdmin() {
  return getRol() === 'ADMIN';
}

export function logout() {
  sessionStorage.removeItem(AUTH_KEY);
  window.location.href = '/index.html';
}

export function requireAuth() {
  if (!getToken()) {
    window.location.href = '/index.html';
  }
}
