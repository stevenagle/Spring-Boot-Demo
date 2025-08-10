import React from 'react';
import './Modal.css';

interface ModalProps {
    message: string;
    onConfirm: () => void;
}

const Modal: React.FC<ModalProps> = ({ message, onConfirm }) => {
    return (
        <div className="modal-overlay">
            <div className="modal-box">
                <p>{message}</p>
                <button onClick={onConfirm}>OK</button>
            </div>
        </div>
    );
};

export default Modal;
