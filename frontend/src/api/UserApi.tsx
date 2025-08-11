export interface UserProfile {
  id: number;
  username: string;
  emailAddress: string;
  streetAddress: string;
  city: string;
  state: string;
  zipCode: string;
}

export interface UpdateUserDto {
  op: 'replace';
  path: keyof UserProfile;
  value: string;
}

export interface CreateUserPayload
  extends Omit<UserProfile, 'id'> {}

const BASE_URL = import.meta.env.VITE_API_URL;

async function handleResponse<T>(res: Response): Promise<T> {
  const text = await res.text();
  if (!res.ok) {
    throw new Error(text || res.statusText);
  }
  try {
    return JSON.parse(text) as T;
  } catch {
    // When there's no JSON body
    return undefined as T;
  }
}

// --- GET ---
export async function getUser(username: string): Promise<UserProfile> {
  return handleResponse<UserProfile>(
    await fetch(`${BASE_URL}/${encodeURIComponent(username)}`)
  );
}

// --- CREATE ---
export async function createUser(
  payload: CreateUserPayload
): Promise<void> {
  await handleResponse<void>(
    await fetch(BASE_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })
  );
}

// --- UPDATE ---
export async function updateUser(
  username: string,
  dto: UpdateUserDto
): Promise<string> {
  return handleResponse<string>(
    await fetch(`${BASE_URL}/${encodeURIComponent(username)}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(dto),
    })
  );
}

// --- DELETE ---
export async function deleteUser(username: string): Promise<void> {
  await handleResponse<void>(
    await fetch(`${BASE_URL}/${encodeURIComponent(username)}`, {
      method: 'DELETE',
    })
  );
}
