/**
 * After login (login.html), calls include credentials via HTTP Basic.
 * Usage: authFetch('/api/loans/all') instead of fetch(...)
 */
function authFetch(url, options) {
  options = options || {};
  var headers = Object.assign({}, options.headers || {});
  var auth = sessionStorage.getItem('cblosAuth');
  if (auth) {
    headers['Authorization'] = auth;
  }
  options.headers = headers;
  return fetch(url, options);
}

function requireLogin() {
  if (!sessionStorage.getItem('cblosAuth')) {
    window.location.href = '/login.html';
  }
}
