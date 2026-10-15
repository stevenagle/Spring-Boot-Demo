import { describe, expect, it, vi } from 'vitest';
import { createUser, deleteUser, getUser, updateUser } from './UserApi';

const profile = {
  id: 1,
  username: 'sample',
  emailAddress: 'test@example.com',
  streetAddress: '1 Main',
  city: 'Chicago',
  state: 'IL',
  zipCode: '60601',
};

describe('profile HTTP client', () => {
  it('reports a useful error when a proxy returns HTML', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(new Response('<h1>Unavailable</h1>', { status: 502 })),
    );
    await expect(getUser('sample')).rejects.toThrow('unreadable response');
  });
  it('uses the default API resource and encodes usernames', async () => {
    const fetch = vi.fn().mockResolvedValue(new Response(JSON.stringify(profile)));
    vi.stubGlobal('fetch', fetch);
    expect(await getUser('a/b')).toEqual(profile);
    expect(fetch).toHaveBeenCalledWith('/api/v1/demo/users/a%2Fb', undefined);
  });
  it('uses structured field validation errors', async () => {
    vi.stubGlobal(
      'fetch',
      vi
        .fn()
        .mockResolvedValue(
          new Response(
            JSON.stringify({ detail: 'Invalid input', errors: { state: 'must be two letters' } }),
            { status: 400 },
          ),
        ),
    );
    await expect(
      updateUser('sample', { op: 'replace', path: 'state', value: 'bad' }),
    ).rejects.toThrow('state: must be two letters');
  });
  it('preserves problem detail messages', async () => {
    vi.stubGlobal(
      'fetch',
      vi
        .fn()
        .mockResolvedValue(
          new Response(JSON.stringify({ detail: 'Profile not found.' }), { status: 404 }),
        ),
    );
    await expect(getUser('missing')).rejects.toThrow('Profile not found.');
  });
  it('handles 204 deletion without trying to parse JSON', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(new Response(null, { status: 204 })));
    await expect(deleteUser('sample')).resolves.toBeUndefined();
  });
  it('returns the created profile', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(new Response(JSON.stringify(profile), { status: 201 })),
    );
    expect(await createUser(profile)).toEqual(profile);
  });
});
