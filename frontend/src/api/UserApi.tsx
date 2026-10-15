import { API_URL } from '../api';

export interface UserProfile {
  id: number;
  username: string;
  emailAddress: string;
  streetAddress: string;
  city: string;
  state: string;
  zipCode: string;
}

export type CreateUserPayload = Omit<UserProfile, 'id'>;
export type ProfileField = keyof CreateUserPayload;
export interface UpdateUserDto {
  op: 'replace';
  path: ProfileField;
  value: string;
}

async function request<T>(path = '', init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_URL}${path}`, init);
  if (response.status === 204) return undefined as T;
  let body: unknown;
  try {
    body = await response.json();
  } catch {
    throw new Error('The server returned an unreadable response. Please try again later.');
  }
  if (!response.ok) {
    const problem = (body ?? {}) as { detail?: string; errors?: Record<string, string> };
    const fields = Object.entries(problem.errors ?? {}).map(
      ([field, message]) => `${field}: ${message}`,
    );
    throw new Error(
      fields.length
        ? fields.join('. ')
        : (problem.detail ?? 'The request failed. Please try again.'),
    );
  }
  return body as T;
}

export function getUser(username: string): Promise<UserProfile> {
  return request(`/${encodeURIComponent(username)}`);
}

export function createUser(payload: CreateUserPayload): Promise<UserProfile> {
  return request('', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
}

export function updateUser(username: string, dto: UpdateUserDto): Promise<UserProfile> {
  return request(`/${encodeURIComponent(username)}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(dto),
  });
}

export function deleteUser(username: string): Promise<void> {
  return request(`/${encodeURIComponent(username)}`, { method: 'DELETE' });
}
