import React, { useState, useEffect } from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import './Navbar.css';
import { getCookie, deleteCookie } from '../utils/cookies';
import { clearCurrentUser } from '../utils/session';
import { useNavigate } from 'react-router-dom';

const Navbar: React.FC = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const [username, setUsername] = useState<string | null>(null);

    const isActive = (path: string) => location.pathname === path;

    useEffect(() => {
        const u = getCookie('demoUser');
        if (u) {
            setUsername(decodeURIComponent(u));
        }
    }, []);

    const handleLogout = () => {
        deleteCookie('demoUser');
        clearCurrentUser();
        navigate('/');
    };

    return (
        <nav className="navbar">
            <div className="navbar-logo">
                <NavLink to="/">MockPortal</NavLink>
            </div>
            <ul className="navbar-links">
                <li><NavLink to="/" className={isActive('/') ? 'active' : ''}>Home</NavLink></li>
                <li><NavLink to="/about" className={isActive('/about') ? 'active' : ''}>About</NavLink></li>
                {username ? (
                    <>
                        <li>
                            <NavLink
                                to="/profile"
                                className={isActive('/profile') ? 'active' : ''}
                            >
                                {username}
                            </NavLink>
                        </li>
                        <li>
                            <button onClick={handleLogout} className="logout-btn">
                                Logout
                            </button>
                        </li>
                    </>
                ) : (
                    <li>
                        <NavLink to="/login" className={isActive('/login') ? 'active' : ''}>
                            Login
                        </NavLink>
                    </li>
                )}
            </ul>
        </nav>
    );
};

export default Navbar;
