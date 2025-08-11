import React, { useState } from 'react';
import './LoginPage.css';
import Navbar from '../components/Navbar';
import { useNavigate } from 'react-router-dom';

interface LoginFormData {
    username?: string;
    password?: string;
    emailAddress?: string;
    street?: string;
    city?: string;
    state?: string;
    zip?: string;
}
interface HandleChangeEvent extends React.ChangeEvent<HTMLInputElement> { }

const LoginPage = () => {

    const API_URL = import.meta.env.VITE_API_URL;
    const [isCreatingUser, setIsCreatingUser] = useState(false);
    const [formData, setFormData] = useState<LoginFormData>({});
    const navigate = useNavigate();

    const handleChange = (e: HandleChangeEvent): void => {
        setFormData((prev: LoginFormData) => ({
            ...prev,
            [e.target.name]: e.target.value
        }));
    };

    const onLoginSuccess = (user: { username: string; }) => {
        // Set a session cookie that expires in 1 hour
        const expires = new Date(Date.now() + 60 * 60 * 1000).toUTCString();
        document.cookie = `demoUser=${user.username}; expires=${expires}; path=/`;

        navigate('/profile');
    };

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (isCreatingUser) {
            try {
                const response = await fetch(API_URL, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: isCreatingUser ? JSON.stringify(formData) : undefined
                });

                if (!response.ok) {
                    throw new Error(`Server responded with ${response.status}`);
                }

                const data = await response.json();
                console.log('Success:', data);
                onLoginSuccess(data);

            } catch (error) {
                console.error('Error:', error);
                // Optionally show error to user
            }
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
                        <><input
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
