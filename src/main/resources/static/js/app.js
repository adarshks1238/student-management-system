// Shared helpers for all pages
const TOKEN_KEY = 'sms_token';
const ROLE_KEY = 'sms_role';

function saveSession(data) {
  localStorage.setItem(TOKEN_KEY, data.token);
  localStorage.setItem(ROLE_KEY, data.role);
}
function logout() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(ROLE_KEY);
  window.location.href = '/index.html';
}
function requireRole(role) {
  if (!localStorage.getItem(TOKEN_KEY) || localStorage.getItem(ROLE_KEY) !== role) {
    logout();
  }
}
async function api(path, options = {}) {
  const headers = Object.assign({ 'Content-Type': 'application/json' }, options.headers || {});
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) headers['Authorization'] = 'Bearer ' + token;
  const res = await fetch(path, Object.assign({}, options, { headers }));
  if (res.status === 401 && !path.includes('/auth/')) { logout(); return; }
  if (!res.ok) {
    let msg = 'Request failed (' + res.status + ')';
    try {
      const err = await res.json();
      msg = err.message || msg;
      if (err.fieldErrors) msg += ': ' + Object.values(err.fieldErrors).join(', ');
    } catch (e) { /* ignore */ }
    throw new Error(msg);
  }
  const type = res.headers.get('content-type') || '';
  return type.includes('application/json') ? res.json() : res;
}
function showMsg(id, text, ok) {
  const el = document.getElementById(id);
  el.textContent = text;
  el.className = 'msg ' + (ok ? 'ok' : 'error');
}
function esc(v) {
  return String(v ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}
