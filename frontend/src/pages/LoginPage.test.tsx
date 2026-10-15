import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { describe, expect, it, vi } from 'vitest';
import LoginPage from './LoginPage';
import { getUser, createUser } from '../api/UserApi';
vi.mock('../api/UserApi', () => ({ getUser: vi.fn(), createUser: vi.fn() }));
const profile = {
  id: 1,
  username: 'sample',
  emailAddress: 'test@example.com',
  streetAddress: '1 Main',
  city: 'Chicago',
  state: 'IL',
  zipCode: '60601',
};
function renderLogin() {
  render(
    <MemoryRouter initialEntries={['/login']}>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/profile" element={<p>Account loaded</p>} />
      </Routes>
    </MemoryRouter>,
  );
}
describe('demo account access', () => {
  it('opens an account and stores only its username', async () => {
    vi.mocked(getUser).mockResolvedValue(profile);
    renderLogin();
    const user = userEvent.setup();
    expect(screen.queryByLabelText(/password/i)).not.toBeInTheDocument();
    await user.type(screen.getByLabelText('Username'), 'sample');
    await user.click(screen.getByRole('button', { name: /Enter your account/ }));
    expect(await screen.findByText('Account loaded')).toBeInTheDocument();
    expect(sessionStorage.getItem('demoUsername')).toBe('sample');
    expect(sessionStorage.getItem('currentUser')).toBeNull();
  });
  it('shows server errors and lets the user retry', async () => {
    vi.mocked(getUser).mockRejectedValue(new Error('Profile not found.'));
    renderLogin();
    const user = userEvent.setup();
    await user.type(screen.getByLabelText('Username'), 'missing');
    await user.click(screen.getByRole('button', { name: /Enter your account/ }));
    expect(await screen.findByRole('alert')).toHaveTextContent('Profile not found.');
    expect(screen.getByRole('button', { name: /Enter your account/ })).toBeEnabled();
  });
  it('creates an account directly from the returned representation', async () => {
    vi.mocked(createUser).mockResolvedValue(profile);
    renderLogin();
    const user = userEvent.setup();
    await user.click(screen.getByRole('button', { name: 'Create account' }));
    for (const [label, value] of [
      ['Username', 'sample'],
      ['Email address', 'test@example.com'],
      ['Street address', '1 Main'],
      ['City', 'Chicago'],
      ['State', 'IL'],
      ['ZIP code', '60601'],
    ]) {
      await user.type(screen.getByLabelText(label), value);
    }
    await user.click(screen.getByRole('button', { name: /Create demo account/ }));
    expect(await screen.findByText('Account loaded')).toBeInTheDocument();
    expect(createUser).toHaveBeenCalledWith({
      username: 'sample',
      emailAddress: 'test@example.com',
      streetAddress: '1 Main',
      city: 'Chicago',
      state: 'IL',
      zipCode: '60601',
    });
    expect(getUser).not.toHaveBeenCalled();
  });
});
