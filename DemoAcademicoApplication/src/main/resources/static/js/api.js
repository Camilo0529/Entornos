import { getToken, logout } from './auth.js';

export async function api(path, options = {}) {
  const headers = Object.assign(
    { 'Content-Type': 'application/json' },
    options.headers || {}
  );

  const token = getToken();
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(path, { ...options, headers });

  if (response.status === 401) {
    logout();
    throw new Error('Sesión expirada o no autenticado');
  }

  if (response.status === 204) {
    return null;
  }

  const text = await response.text();
  let body = null;
  if (text) {
    try {
      body = JSON.parse(text);
    } catch {
      body = { message: text };
    }
  }

  if (!response.ok) {
    const msg = body?.message || `Error HTTP ${response.status}`;
    const error = new Error(msg);
    error.status = response.status;
    error.body = body;
    throw error;
  }

  return body;
}
