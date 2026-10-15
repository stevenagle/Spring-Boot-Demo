import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { clearCurrentUser, loadCurrentUsername, saveCurrentUsername } from '../utils/session';
import {
  deleteUser,
  getUser,
  updateUser,
  type UserProfile,
  type ProfileField,
} from '../api/UserApi';
import './ProfilePage.css';

const fields: [ProfileField, string][] = [
  ['username', 'Username'],
  ['emailAddress', 'Email address'],
  ['streetAddress', 'Street address'],
  ['city', 'City'],
  ['state', 'State'],
  ['zipCode', 'ZIP code'],
];

export default function ProfilePage() {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [editing, setEditing] = useState<ProfileField | null>(null);
  const [value, setValue] = useState('');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [confirmDelete, setConfirmDelete] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const username = loadCurrentUsername();
    if (!username) {
      navigate('/login', { replace: true });
      return;
    }
    let active = true;
    getUser(username)
      .then((user) => {
        if (active) setProfile(user);
      })
      .catch((err) => {
        if (active) setError(err.message);
      });
    return () => {
      active = false;
    };
  }, [navigate]);

  async function save(event: React.FormEvent) {
    event.preventDefault();
    if (!profile || !editing) return;
    setBusy(true);
    setError('');
    setNotice('');
    try {
      const updated = await updateUser(profile.username, {
        op: 'replace',
        path: editing,
        value: value.trim(),
      });
      setProfile(updated);
      saveCurrentUsername(updated.username);
      setEditing(null);
      setNotice('Your changes are saved.');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unable to save changes.');
    } finally {
      setBusy(false);
    }
  }

  async function remove() {
    if (!profile) return;
    setBusy(true);
    setError('');
    try {
      await deleteUser(profile.username);
      clearCurrentUser();
      navigate('/login', { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unable to delete account.');
    } finally {
      setBusy(false);
    }
  }

  return (
    <>
      <Navbar />
      <main className="account-page">
        <header className="account-heading">
          <div>
            <span className="eyebrow">YOUR PERSONAL CORNER</span>
            <h1>
              Good to see you{profile ? `, ${profile.username}` : ''}
              <span className="accent">.</span>
            </h1>
            <p>A few details. A space that feels like you.</p>
          </div>
          <span className="account-badge">
            <span className="status-dot" /> Demo account
          </span>
        </header>
        {error && (
          <p className="form-error" role="alert">
            {error}
          </p>
        )}
        <p role="status" className="save-notice">
          {notice}
        </p>
        {profile ? (
          <div className="account-grid">
            <aside className="account-summary">
              <div className="avatar" aria-hidden="true">
                {profile.username.slice(0, 2).toUpperCase()}
              </div>
              <h2>{profile.username}</h2>
              <p>{profile.emailAddress}</p>
              <span className="summary-rule" />
              <span className="eyebrow">A LITTLE ABOUT THIS SPACE</span>
              <p>Your profile is backed by a Spring Boot API. Every edit makes the round trip.</p>
              <span className="stack-pill">REACT ↔ SPRING ↔ H2</span>
            </aside>
            <section className="account-details" aria-labelledby="details-title">
              <div className="details-heading">
                <h2 id="details-title">Account details</h2>
                <p>Keep your sample information up to date.</p>
              </div>
              {fields.map(([field, label]) => (
                <div className="profile-field" key={field}>
                  {editing === field ? (
                    <form onSubmit={save} className="edit-form">
                      <label htmlFor={`edit-${field}`}>{label}</label>
                      <input
                        id={`edit-${field}`}
                        value={value}
                        onChange={(event) => setValue(event.target.value)}
                        required
                        autoFocus
                        disabled={busy}
                        type={field === 'emailAddress' ? 'email' : 'text'}
                        maxLength={
                          field === 'username'
                            ? 32
                            : field === 'state'
                              ? 2
                              : field === 'zipCode'
                                ? 10
                                : field === 'city'
                                  ? 100
                                  : field === 'emailAddress'
                                    ? 254
                                    : 255
                        }
                        pattern={
                          field === 'username'
                            ? '[A-Za-z0-9]{1,32}'
                            : field === 'state'
                              ? '[A-Z]{2}'
                              : field === 'zipCode'
                                ? '[0-9]{5}(-[0-9]{4})?'
                                : undefined
                        }
                      />
                      <div className="edit-actions">
                        <button type="submit" className="primary-button" disabled={busy}>
                          {busy ? 'Saving…' : 'Save'}
                        </button>
                        <button type="button" disabled={busy} onClick={() => setEditing(null)}>
                          Cancel
                        </button>
                      </div>
                    </form>
                  ) : (
                    <>
                      <div>
                        <span className="field-label">{label}</span>
                        <p>{profile[field]}</p>
                      </div>
                      <button
                        className="edit-button"
                        disabled={busy || editing !== null}
                        onClick={() => {
                          setEditing(field);
                          setValue(profile[field]);
                          setError('');
                          setNotice('');
                        }}
                        aria-label={`Edit ${label}`}
                      >
                        Edit <span aria-hidden="true">↗</span>
                      </button>
                    </>
                  )}
                </div>
              ))}
            </section>
            <section className="account-danger">
              <div>
                <h2>Ready for a fresh start?</h2>
                <p>Deleting this demo account permanently removes its profile.</p>
              </div>
              {confirmDelete ? (
                <div className="delete-confirm" role="group" aria-label="Confirm account deletion">
                  <strong>Delete this account?</strong>
                  <button className="danger-button" onClick={remove} disabled={busy}>
                    {busy ? 'Deleting…' : 'Yes, delete account'}
                  </button>
                  <button disabled={busy} onClick={() => setConfirmDelete(false)}>
                    Cancel
                  </button>
                </div>
              ) : (
                <button
                  className="danger-button"
                  disabled={busy || editing !== null}
                  onClick={() => setConfirmDelete(true)}
                >
                  Delete account
                </button>
              )}
            </section>
          </div>
        ) : (
          <p role="status">
            {error ? 'Return to the demo to select an available account.' : 'Loading your account…'}{' '}
            <button
              onClick={() => {
                clearCurrentUser();
                navigate('/login');
              }}
            >
              Open demo
            </button>
          </p>
        )}
        <footer className="account-footer">
          DEMO ENVIRONMENT <span>Sample data only · No authentication</span>
        </footer>
      </main>
    </>
  );
}
