import type { UserProfile } from '../api/UserApi';

/**
 * Saves the current user profile to session storage.
 * @param user - The user profile to save.
 */

const KEY = 'currentUser';

export function saveCurrentUser(user: UserProfile) {
  sessionStorage.setItem(KEY, JSON.stringify(user));
}

export function loadCurrentUser(): UserProfile | null {
  const raw = sessionStorage.getItem(KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as UserProfile;
  } catch {
    sessionStorage.removeItem(KEY);
    return null;
  }
}

export function clearCurrentUser() {
  sessionStorage.removeItem(KEY);
}
