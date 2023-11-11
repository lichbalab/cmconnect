
import React, { useState, useEffect } from 'react';
import { getCertificates, deleteCertificate } from '../services/api';

const CertificateTable = () => {
    const [certificates, setCertificates] = useState([]);

    useEffect(() => {
        loadCertificates();
    }, []);

    const loadCertificates = async () => {
        const response = await getCertificates();
        setCertificates(response.data);
    };

    const handleDelete = async (id) => {
        await deleteCertificate(id);
        loadCertificates();
    };

    return (
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                {certificates.map(certificate => (
                    <tr key={certificate.id}>
                        <td>{certificate.id}</td>
                        <td>{certificate.name}</td>
                        <td>
                            <button onClick={() => handleDelete(certificate.id)}>Delete</button>
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
};

export default CertificateTable;
