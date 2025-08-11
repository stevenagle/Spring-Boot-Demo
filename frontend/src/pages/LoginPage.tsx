import React, { useState } from 'react';
import './LoginPage.css';
import Navbar from '../components/Navbar';
import { useNavigate } from 'react-router-dom';
import { getUser, createUser, type CreateUserPayload } from '../api/UserApi';
import { saveCurrentUser } from '../utils/session';

interface LoginFormData extends Partial<CreateUserPayload> {
  password?: string; // still collected, but unused
}

const LoginPage = () => {
  const [isCreatingUser, setIsCreatingUser] = useState(false);
  const [formData, setFormData] = useState<LoginFormData>({});
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const onLoginSuccess = (user: { username: string }) => {
    saveCurrentUser(user as any); // stores whole profile
    navigate('/profile');
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      if (isCreatingUser) {
        await createUser(formData as CreateUserPayload);
        const created = await getUser(formData.username!);
        onLoginSuccess(created);
      } else {
        const existing = await getUser(formData.username!);
        onLoginSuccess(existing);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Request failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Navbar />
      <div className="login-page">
        <h2>User Portal</h2>
        <form onSubmit={handleSubmit} className="login-form">
          <input
            type="text"
            name="username"
            placeholder="Username"
            onChange={handleChange}
            required
          />
          <input
            type="password"
            name="password"
            placeholder="Password"
            onChange={handleChange}
            required
          />

          {isCreatingUser && (
            <>
              <input
                type="email"
                name="emailAddress"
                placeholder="Email Address"
                onChange={handleChange}
                required
              />
              <input
                type="text"
                name="streetAddress"
                placeholder="Street Address"
                onChange={handleChange}
                required
              />
              <input
                type="text"
                name="city"
                placeholder="City"
                onChange={handleChange}
                required
              />
              <input
                type="text"
                name="state"
                placeholder="State"
                onChange={handleChange}
                required
              />
              <input
                type="text"
                name="zipCode"
                placeholder="Zip Code"
                onChange={handleChange}
                required
              />
            </>
          )}

          <button type="submit" disabled={loading}>
            {loading ? 'Processing…' : isCreatingUser ? 'Create new user' : 'Log in'}
          </button>
        </form>

        {error && <p className="error">{error}</p>}

        <p className="toggle-link" onClick={() => setIsCreatingUser(prev => !prev)}>
          {isCreatingUser ? 'Already a user?' : 'Create New User'}
        </p>
      </div>
    </>
  );
};

export default LoginPage;
