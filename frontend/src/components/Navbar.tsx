import { useEffect, useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { clearCurrentUser, loadCurrentUsername } from '../utils/session';
import './Navbar.css';

export default function Navbar() {
  const navigate = useNavigate();
  const [username, setUsername] = useState(loadCurrentUsername);
  useEffect(() => {
    const sync = () => setUsername(loadCurrentUsername());
    window.addEventListener('demo-session-change', sync);
    return () => window.removeEventListener('demo-session-change', sync);
  }, []);

  return (
    <nav className="navbar" aria-label="Main navigation">
      <NavLink to="/" className="navbar-brand">
        <span className="brand-mark">
          m<span>•</span>
        </span>{' '}
        MockPortal
      </NavLink>
      <div className="navbar-links">
        <NavLink to="/">Home</NavLink>
        <NavLink to="/about">About</NavLink>
        <NavLink to={username ? '/profile' : '/login'}>
          {username ?? 'Open demo'} <span aria-hidden="true">↗</span>
        </NavLink>
        {username && (
          <button
            className="logout-btn"
            onClick={() => {
              clearCurrentUser();
              navigate('/login');
            }}
          >
            Sign out
          </button>
        )}
      </div>
    </nav>
  );
}
