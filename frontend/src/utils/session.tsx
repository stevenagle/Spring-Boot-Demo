const KEY = 'demoUsername';

export function saveCurrentUsername(username: string) {
  sessionStorage.setItem(KEY, username);
  window.dispatchEvent(new Event('demo-session-change'));
}

export function loadCurrentUsername(): string | null {
  return sessionStorage.getItem(KEY);
}

export function clearCurrentUser() {
  sessionStorage.removeItem(KEY);
  sessionStorage.removeItem('currentUser');
  window.dispatchEvent(new Event('demo-session-change'));
}
