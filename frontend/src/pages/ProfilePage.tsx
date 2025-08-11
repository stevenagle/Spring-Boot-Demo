import React, { useState, useEffect } from 'react';
import Modal from '../components/Modal';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import './ProfilePage.css';
import {
    loadCurrentUser,
    clearCurrentUser,
    saveCurrentUser
} from '../utils/session';
import {
    getUser,
    updateUser,
    deleteUser,
    type UserProfile,
    type UpdateUserDto
} from '../api/UserApi';

const UserHomePage: React.FC = () => {
    const [profile, setProfile] = useState<UserProfile | null>(null);
    const [editedFields, setEditedFields] = useState<Partial<Record<keyof UserProfile, boolean>>>({});
    const [editingField, setEditingField] = useState<keyof UserProfile | null>(null);
    const [editValue, setEditValue] = useState<string>('');
    const [modalMessage, setModalMessage] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const current = loadCurrentUser();
        if (!current) {
            navigate('/login', { replace: true });
            return;
        }
        setProfile(current);
    }, [navigate]);

    const refreshProfile = async (username: string) => {
        const fresh = await getUser(username);
        setProfile(fresh);
        saveCurrentUser(fresh);
    };

    const handleLogout = () => {
        clearCurrentUser();
        setModalMessage("You've been logged out.");
    };

    // Enter edit mode for a specific field
    const startEditing = (field: keyof UserProfile) => {
        if (!profile) return;
        // Optional: allow only one field at a time
        setEditingField(field);
        setEditValue(String(profile[field] ?? ''));
    };

    // Cancel edit and revert
    const cancelEditing = () => {
        setEditingField(null);
        setEditValue('');
    };

    // Save the edit (PATCH) and refresh
    const saveEditing = async () => {
        if (!profile || !editingField) return;

        const original = String(profile[editingField] ?? '');
        const next = editValue.trim();

        // If unchanged or empty (for fields you want to require), just exit
        if (next === original) {
            cancelEditing();
            return;
        }
        if (next.length === 0) {
            // You can choose to show a toast instead
            return;
        }

        const dto: UpdateUserDto = {
            op: 'replace',
            path: editingField,
            value: next,
        };

        try {
            await updateUser(profile.username, dto);

            const nextUsername = editingField === 'username' ? next : profile.username;
            await refreshProfile(nextUsername);

            // Mark saved indicator for this field
            setEditedFields(prev => ({ ...prev, [editingField]: true }));
            setTimeout(() => {
                setEditedFields(prev => ({ ...prev, [editingField]: false }));
            }, 2000);

            cancelEditing();
        } catch (err) {
            console.error(err);
            // Optionally show an error message or toast
        }
    };

    const handleDelete = async () => {
        if (!profile) return;
        try {
            await deleteUser(profile.username);
            clearCurrentUser();
            setModalMessage('Profile deleted.');
        } catch (err) {
            console.error(err);
        }
    };

    const handleModalConfirm = () => {
        setModalMessage(null);
        navigate('/');
    };

    if (!profile) return <div className="user-home">Loading...</div>;

    const editableFields: (keyof UserProfile)[] = [
        'username',
        'emailAddress',
        'streetAddress',
        'city',
        'state',
        'zipCode',
    ];

    const isEditing = (field: keyof UserProfile) => editingField === field;

    return (
        <div className="user-home">
            <h2>Welcome, {profile.username}</h2>
            <Navbar />

            <div className="profile-section">
                {editableFields.map(field => {
                    const label = field.replace(/([A-Z])/g, ' $1').trim();
                    const value = isEditing(field) ? editValue : String(profile[field] ?? '');

                    return (
                        <div key={field} className={`profile-field ${isEditing(field) ? 'editing' : ''}`}>
                            <label>{label}:</label>

                            <input
                                type="text"
                                value={value}
                                readOnly={!isEditing(field)}
                                onChange={e => setEditValue(e.target.value)}
                                className={isEditing(field) ? 'input-editable' : 'input-readonly'}
                                aria-label={`${label} ${isEditing(field) ? 'editing' : 'read only'}`}
                                autoFocus={isEditing(field)}
                            />

                            {/* Action icons */}
                            {!isEditing(field) ? (
                                <button
                                    className="icon-button"
                                    aria-label={`Edit ${label}`}
                                    onClick={() => startEditing(field)}
                                    title={`Edit ${label}`}
                                >
                                    ✏️
                                </button>
                            ) : (
                                <div className="inline-actions">
                                    <button
                                        className="icon-button"
                                        aria-label={`Save ${label}`}
                                        onClick={saveEditing}
                                        disabled={
                                            editValue.trim() === String(profile[field] ?? '') ||
                                            editValue.trim().length === 0
                                        }
                                        title="Save"
                                    >
                                        ✅
                                    </button>
                                    <button
                                        className="icon-button"
                                        aria-label={`Cancel ${label} edit`}
                                        onClick={cancelEditing}
                                        title="Cancel"
                                    >
                                        ❌
                                    </button>
                                </div>
                            )}

                            {editedFields[field] && (
                                <span className="saved-indicator" aria-live="polite">
                                    ✅ Saved!
                                </span>
                            )}
                        </div>
                    );
                })}
            </div>

            <div className="profile-actions-btns">
                <button className="delete-button" onClick={handleDelete}>
                    Delete User
                </button>
                <button className="logout-button" onClick={handleLogout}>
                    Log Out
                </button>
                {modalMessage && (
                    <Modal message={modalMessage} onConfirm={handleModalConfirm} />
                )}
            </div>
        </div>
    );
};

export default UserHomePage;
