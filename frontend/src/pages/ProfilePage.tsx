import React, { useState, useEffect } from 'react';
import Modal from '../components/Modal';
import { useParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import './UserHomePage.css';


const UserHomePage: React.FC = () => {
    const { id } = useParams();
    const [profile, setProfile] = useState<any>(null);
    const [editedFields, setEditedFields] = useState<{ [key: string]: boolean }>({});

    useEffect(() => {
        // Fetch user profile from backend
        fetch(`/api/users/${id}`)
            .then(res => res.json())
            .then(data => setProfile(data));
    }, [id]);

    const navigate = useNavigate();

    const [modalMessage, setModalMessage] = useState<string | null>(null);
    const handleLogout = () => {
        setModalMessage("You've been logged out.");
    };

    const handleEdit = (field: string, value: string) => {
        fetch(`/api/users/${id}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ [field]: value })
        }).then(() => {
            setProfile((prev: any) => ({ ...prev, [field]: value }));
            setEditedFields(prev => ({ ...prev, [field]: true }));
            setTimeout(() => {
                setEditedFields(prev => ({ ...prev, [field]: false }));
            }, 2000);
        });
    };

    const handleDelete = () => {
        fetch(`/api/users/${id}`, { method: 'DELETE' })
            .then(() => {
                setModalMessage("Profile deleted.");
            });
    };


    const handleModalConfirm = () => {
        setModalMessage(null);
        navigate('/');
    };


    if (!profile) return <div className="user-home">Loading...</div>;

    return (
        <div className="user-home">
            <h2>Welcome, {profile.username}</h2>
            <div className="profile-section">
                {['email', 'street', 'city', 'state', 'zip'].map(field => (
                    <div key={field} className="profile-field">
                        <label>{field.charAt(0).toUpperCase() + field.slice(1)}:</label>
                        <input
                            type="text"
                            defaultValue={profile[field]}
                            onBlur={(e) => handleEdit(field, e.target.value)}
                        />
                        <span className="edit-icon">✏️</span>
                        {editedFields[field] && <span className="saved-indicator">✅ Saved!</span>}
                    </div>
                ))}
            </div>
            <div className="profile-actions-btns">
                <button className="delete-button" onClick={handleDelete}>Delete User</button>
                <button className="logout-button" onClick={handleLogout}>Log Out</button>
                {modalMessage && (
                    <Modal message={modalMessage} onConfirm={handleModalConfirm} />
                )}
            </div>
        </div>
    );
};

export default UserHomePage;
