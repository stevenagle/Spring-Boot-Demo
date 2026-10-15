import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { createUser, getUser, type CreateUserPayload } from '../api/UserApi';
import { saveCurrentUsername } from '../utils/session';
import './LoginPage.css';

const fields = [
  ['emailAddress', 'Email address', 'email', 'you@example.com', 254],
  ['streetAddress', 'Street address', 'text', '123 Main Street', 255],
  ['city', 'City', 'text', 'Chicago', 100],
  ['state', 'State', 'text', 'IL', 2],
  ['zipCode', 'ZIP code', 'text', '60601', 10],
] as const;

export default function LoginPage() {
  const [creating, setCreating] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  async function submit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    const username = String(data.get('username')).trim();
    setError('');
    setLoading(true);
    try {
      const profile = creating
        ? await createUser({
            username,
            emailAddress: String(data.get('emailAddress') ?? '').trim(),
            streetAddress: String(data.get('streetAddress') ?? '').trim(),
            city: String(data.get('city') ?? '').trim(),
            state: String(data.get('state') ?? '').trim(),
            zipCode: String(data.get('zipCode') ?? '').trim(),
          } satisfies CreateUserPayload)
        : await getUser(username);
      saveCurrentUsername(profile.username);
      navigate('/profile');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unable to connect. Please try again.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <Navbar />
      <main className="portal-layout">
        <section className="portal-story" aria-labelledby="portal-title">
          <span className="eyebrow">
            <span className="status-dot" /> THE MOCKPORTAL EXPERIENCE
          </span>
          <h1 id="portal-title">
            Small demo.
            <br />
            Big possibilities<span className="accent">.</span>
          </h1>
          <p>A little Spring. A little React. A place to make yourself at home.</p>
          <div className="orbit-art" aria-hidden="true">
            <div className="orbit orbit-one" />
            <div className="orbit orbit-two" />
            <div className="orbit-core">
              m<span>•</span>
            </div>
            <span className="orbit-tag tag-top">Built to connect ↗</span>
            <span className="orbit-tag tag-bottom">React + Spring Boot</span>
          </div>
          <div className="story-footer">
            <span>01 / EXPLORE THE DEMO</span>
            <span>Made for curious minds.</span>
          </div>
        </section>
        <section className="portal-form-panel" aria-labelledby="form-title">
          <div className="form-heading">
            <span className="eyebrow">YOUR SPACE, SIMPLIFIED</span>
            <h2 id="form-title">{creating ? 'Make it yours.' : 'Welcome back.'}</h2>
            <p>
              {creating
                ? 'Create a sample account and explore the possibilities.'
                : 'Pick up where you left off. Your demo account awaits.'}
            </p>
          </div>
          <div className="mode-switch" aria-label="Account mode">
            <button
              type="button"
              aria-pressed={!creating}
              disabled={loading}
              onClick={() => {
                setCreating(false);
                setError('');
              }}
            >
              Open account
            </button>
            <button
              type="button"
              aria-pressed={creating}
              disabled={loading}
              onClick={() => {
                setCreating(true);
                setError('');
              }}
            >
              Create account
            </button>
          </div>
          <form className="login-form" onSubmit={submit} aria-busy={loading}>
            <label htmlFor="username">Username</label>
            <input
              id="username"
              name="username"
              placeholder="e.g. alpha001"
              autoComplete="username"
              required
              pattern="[A-Za-z0-9]{1,32}"
              maxLength={32}
              title="Use 1–32 letters or numbers"
              disabled={loading}
            />
            {creating && (
              <div className="registration-fields">
                {fields.map(([name, label, type, placeholder, maxLength]) => (
                  <div className={`form-field field-${name}`} key={name}>
                    <label htmlFor={name}>{label}</label>
                    <input
                      id={name}
                      name={name}
                      type={type}
                      placeholder={placeholder}
                      maxLength={maxLength}
                      required
                      disabled={loading}
                      pattern={
                        name === 'state'
                          ? '[A-Z]{2}'
                          : name === 'zipCode'
                            ? '[0-9]{5}(-[0-9]{4})?'
                            : undefined
                      }
                      autoComplete={
                        {
                          emailAddress: 'email',
                          streetAddress: 'street-address',
                          city: 'address-level2',
                          state: 'address-level1',
                          zipCode: 'postal-code',
                        }[name]
                      }
                    />
                  </div>
                ))}
              </div>
            )}
            {error && (
              <p className="form-error" role="alert">
                {error}
              </p>
            )}
            <button className="primary-button" type="submit" disabled={loading}>
              {loading ? 'Connecting…' : creating ? 'Create demo account' : 'Enter your account'}{' '}
              <span aria-hidden="true">→</span>
            </button>
          </form>
          <aside className="demo-note">
            <span aria-hidden="true">ⓘ</span>
            <div>
              <strong>A playground, by design.</strong>
              <p>
                This demo uses a username only. No password or authentication. Please use sample
                data.
              </p>
              {!creating && (
                <p>
                  Try{' '}
                  <button
                    type="button"
                    className="sample-user"
                    onClick={() => {
                      const input = document.getElementById('username') as HTMLInputElement;
                      input.value = 'alpha001';
                      input.focus();
                    }}
                  >
                    alpha001
                  </button>{' '}
                  to take a look around.
                </p>
              )}
            </div>
          </aside>
          <p className="portal-footnote">A full stack idea. A thoughtfully simple experience.</p>
        </section>
      </main>
    </>
  );
}
