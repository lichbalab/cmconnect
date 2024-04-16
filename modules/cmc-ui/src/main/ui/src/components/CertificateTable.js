
import React, { useState, useEffect } from 'react';
import { getCertificates, deleteCertificate, uploadCertificate } from '../services/api';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button, TextField } from '@mui/material';
import { Link } from 'react-router-dom';


const CertificateTable = () => {
    const [certificates, setCertificates] = useState([]);
    const [selectedFile, setSelectedFile] = useState(null);
    const [alias, setAlias] = useState(''); // State for the certificate alias

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

    const handleFileUpload = (event) => {
        setSelectedFile(event.target.files[0]);
    };

    const handleAliasChange = (event) => {
        setAlias(event.target.value);
    };

    const uploadFile = async () => {
        if (!selectedFile || alias.trim() === '') {
            alert("Please select a file and enter an alias!");
            return;
        }

        // Assuming 'uploadCertificate' is the function from your api service to upload the file
        try {
            await uploadCertificate(selectedFile, alias);
            setAlias('');
            loadCertificates(); // Reload certificates after successful upload
        } catch (error) {
            console.error("Error uploading file:", error);
            // Handle the error appropriately
        }
    };

     return (
        <div style={{ width: '100%', height: '100%' }}>
            <h2 style={{ textAlign: 'center' }}>Certificates</h2>
            <TableContainer component={Paper} style={{ width: '100%', height: '100%' }}>
              <TextField label="Alias" value={alias} onChange={handleAliasChange} required />
              <input type="file" onChange={handleFileUpload} />
                <Button onClick={uploadFile} variant="contained" color="primary">
                    Upload Certificate
                </Button>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Alias</TableCell>
                    <TableCell>Subject</TableCell>
                    <TableCell>Issuer</TableCell>
                    <TableCell>Serial Number</TableCell>
                    <TableCell>Expiration Timestamp</TableCell>
                    <TableCell>Action</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {certificates.map((row) => (
                    <TableRow key={row.alias}>
                      <TableCell>{row.alias}</TableCell>
                      <TableCell>{row.subject}</TableCell>
                      <TableCell>{row.issuer}</TableCell>
                      <TableCell>{row.serialNumber}</TableCell>
                      <TableCell>{row.expirationDate}</TableCell>
                      <TableCell>
                        <Button
                          variant="contained"
                          color="secondary"
                          onClick={() => handleDelete(row.id)}
                        >
                          Remove
                        </Button>
                        <Button
                            variant="contained"
                            color="primary"
                            component={Link}
                            to={`/certificate/${row.id}`}
                        >
                            View Details
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
        </div>
      );
    };

export default CertificateTable;