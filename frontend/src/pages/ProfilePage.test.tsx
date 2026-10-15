import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import ProfilePage from './ProfilePage';
import { getUser, updateUser, deleteUser } from '../api/UserApi';
vi.mock('../api/UserApi', () => ({ getUser: vi.fn(), updateUser: vi.fn(), deleteUser: vi.fn() }));
const profile = {
  id: 1,
  username: 'sample',
  emailAddress: 'test@example.com',
  streetAddress: '1 Main',
  city: 'Chicago',
  state: 'IL',
  zipCode: '60601',
};
function renderProfile() {
  render(
    <MemoryRouter initialEntries={['/profile']}>
      <Routes>
        <Route path="/profile" element={<ProfilePage />} />
        <Route path="/login" element={<p>Choose an account</p>} />
      </Routes>
    </MemoryRouter>,
  );
}
beforeEach(() => {
  sessionStorage.setItem('demoUsername', 'sample');
  vi.mocked(getUser).mockResolvedValue(profile);
});
describe('account editing', () => {
  it('refreshes the account and synchronizes a renamed username', async () => {
    vi.mocked(updateUser).mockResolvedValue({ ...profile, username: 'renamed' });
    renderProfile();
    const user = userEvent.setup();
    await user.click(await screen.findByRole('button', { name: 'Edit Username' }));
    await user.clear(screen.getByLabelText('Username'));
    await user.type(screen.getByLabelText('Username'), 'renamed');
    await user.click(screen.getByRole('button', { name: 'Save' }));
    expect(await screen.findByText('Your changes are saved.')).toBeInTheDocument();
    expect(sessionStorage.getItem('demoUsername')).toBe('renamed');
    expect(updateUser).toHaveBeenCalledWith('sample', {
      op: 'replace',
      path: 'username',
      value: 'renamed',
    });
  });
  it('keeps the edit open and reports a failed save', async () => {
    vi.mocked(updateUser).mockRejectedValue(new Error('Username already exists.'));
    renderProfile();
    const user = userEvent.setup();
    await user.click(await screen.findByRole('button', { name: 'Edit Username' }));
    await user.click(screen.getByRole('button', { name: 'Save' }));
    expect(await screen.findByRole('alert')).toHaveTextContent('Username already exists.');
    expect(screen.getByLabelText('Username')).toBeInTheDocument();
  });
  it('requires confirmation before deletion and clears the selection', async () => {
    vi.mocked(deleteUser).mockResolvedValue(undefined);
    renderProfile();
    const user = userEvent.setup();
    await user.click(await screen.findByRole('button', { name: 'Delete account' }));
    expect(deleteUser).not.toHaveBeenCalled();
    await user.click(screen.getByRole('button', { name: 'Yes, delete account' }));
    expect(await screen.findByText('Choose an account')).toBeInTheDocument();
    expect(sessionStorage.getItem('demoUsername')).toBeNull();
  });
  it('redirects when no account is selected', async () => {
    sessionStorage.clear();
    renderProfile();
    await waitFor(() => expect(screen.getByText('Choose an account')).toBeInTheDocument());
    expect(getUser).not.toHaveBeenCalled();
  });
});
