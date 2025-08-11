import React, { useEffect, useState } from 'react';
import './LoginPage.css';
import Navbar from '../components/Navbar';
import { useNavigate } from 'react-router-dom';
import { getUser, createUser, type CreateUserPayload, type UserProfile } from '../api/UserApi';
import { saveCurrentUser } from '../utils/session';
import { setCookie, getCookie } from '../utils/cookies';

interface LoginFormData extends Partial<CreateUserPayload> {
    password?: string; // still collected, but unused
}

const LoginPage: React.FC = () => {
    const [isCreatingUser, setIsCreatingUser] = useState(false);
    const [formData, setFormData] = useState<LoginFormData>({});
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    // If a session cookie already exists, hydrate session and skip the login form
    useEffect(() => {
        const u = getCookie('demoUser');
        if (!u) return;

        // Optional: hydrate session state so profile has data without another login
        (async () => {
            try {
                const profile = await getUser(decodeURIComponent(u));
                if (profile) {
                    saveCurrentUser(profile);
                    navigate('/profile');
                }
            } catch {
                // If cookie is stale or user not found, ignore and let user log in
            }
        })();
    }, [navigate]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
    };

    const onLoginSuccess = (user: UserProfile) => {
        setCookie('demoUser', user.username, 1); // 1 day expiry
        saveCurrentUser(user); // keep session storage for quick hydration
        navigate('/profile');
    };

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            const username = formData.username?.trim();
            if (!username) {
                setError('Username is required');
                return;
            }

            if (isCreatingUser) {
                // Build payload explicitly to ensure required fields are present
                const payload: CreateUserPayload = {
                    username,
                    emailAddress: formData.emailAddress ?? '',
                    streetAddress: formData.streetAddress ?? '',
                    city: formData.city ?? '',
                    state: formData.state ?? '',
                    zipCode: formData.zipCode ?? ''
                };
                await createUser(payload);
                const created = await getUser(username);
                onLoginSuccess(created);
            } else {
                const existing = await getUser(username);
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

                <p
                    className="toggle-link"
                    onClick={() => setIsCreatingUser(prev => !prev)}
                    aria-live="polite"
                >
                    {isCreatingUser ? 'Already a user? Log in' : 'Create New User'}
                </p>
            </div>
        </>
    );
};

export default LoginPage;
