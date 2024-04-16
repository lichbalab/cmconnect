// CertificateDetails.js
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { getCertificate } from '../services/api';

const CertificateDetails = () => {
    const { id } = useParams();
    const [certificate, setCertificate] = useState(null);

    useEffect(() => {
        const fetchCertificate = async () => {
            const response = await getCertificate(id);
            setCertificate(response.data);
        };

        fetchCertificate();
    }, [id]);

    if (!certificate) {
        return <div>Loading...</div>;
    }

    return (
        <div>
            <h2>Certificate Details</h2>
            <p><strong>Alias:</strong> {certificate.alias}</p>
            <p><strong>Subject:</strong> {certificate.subject}</p>
            <p><strong>Issuer:</strong> {certificate.issuer}</p>
            <p><strong>Serial Number:</strong> {certificate.serialNumber}</p>
            <p><strong>Expiration Date:</strong> {certificate.expirationDate}</p>
            {/* Add more fields as needed */}
        </div>
    );
};

export default CertificateDetails;