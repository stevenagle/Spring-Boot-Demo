import React, { useState } from 'react';
import './LoginPage.css';
import Navbar from '../components/Navbar';
import { useNavigate } from 'react-router-dom';

const LoginPage = () => {
    const [isCreatingUser, setIsCreatingUser] = useState(false);
    const [formData, setFormData] = useState({});
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData(prev => ({
            ...prev,
            [e.target.name]: e.target.value
        }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (isCreatingUser) {
            // POST to create new user
        } else {
            // GET to log in existing user
        }
    };

    return (
        <>
            <Navbar />
            <div className="login-page">
                <h2>User Portal</h2>
                <form onSubmit={handleSubmit} className="login-form">
                    <input
                        type="username"
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
                        <><input
                            type="email"
                            name="emailAddress"
                            placeholder="Email Address"
                            onChange={handleChange}
                            required
                        />
                            <input
                                type="text"
                                name="street"
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
                                name="zip"
                                placeholder="Zip Code"
                                onChange={handleChange}
                                required
                            />
                        </>
                    )}

                    <button type="submit">
                        {isCreatingUser ? 'Create new user' : 'Log in'}
                    </button>
                </form>

                <p className="toggle-link" onClick={() => setIsCreatingUser(prev => !prev)}>
                    {isCreatingUser ? 'Already a user?' : 'Create New User'}
                </p>
            </div>
        </>
    );
};

export default LoginPage;
