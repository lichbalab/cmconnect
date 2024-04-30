import React, { useState, useEffect } from 'react';
import { getCertificates, deleteCertificate, uploadCertificate } from '../services/api';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button, TextField, Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';
import { Link, useNavigate } from 'react-router-dom';
import AddIcon from '@mui/icons-material/Add';

const CertificateTable = () => {
    const [certificates, setCertificates] = useState([]);
    const [selectedFile, setSelectedFile] = useState(null);
    const [alias, setAlias] = useState(''); // State for the certificate alias
    const [isFormVisible, setFormVisible] = useState(false);

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

    const navigate = useNavigate();

    const handleRowDoubleClick = (id) => {
        navigate(`/certificate/${id}`);
    };

    const uploadFile = async () => {
        if (!selectedFile || alias.trim() === '') {
            alert("Please select a file and enter an alias!");
            return;
        }

        try {
            await uploadCertificate(selectedFile, alias);
            setAlias('');
            setFormVisible(false);
            loadCertificates(); // Reload certificates after successful upload
        } catch (error) {
            console.error("Error uploading file:", error);
        }
    };

    return (
        <div style={{ width: '100%', height: '100%' }}>
            <Button
                onClick={() => setFormVisible(true)}
                variant="contained"
                color="primary"
                startIcon={<AddIcon />}
                style={{ marginTop: 24,
                marginBottom: 24,
                float: 'right' }}
            >
                Add Certificate
            </Button>
            <Dialog open={isFormVisible} onClose={() => setFormVisible(false)}>
                <DialogTitle>Add Certificate</DialogTitle>
                <DialogContent>
                 <TextField
                            autoFocus
                            required
                            margin="dense"
                            id="alias"
                            name="alias"
                            label="Alias"
                            fullWidth
                            variant="standard"
                          />
                    <TextField label="Alias" value={alias} onChange={handleAliasChange} required />
                    <input type="file" onChange={handleFileUpload} />
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setFormVisible(false)}>
                        Cancel
                    </Button>
                    <Button onClick={uploadFile} variant="contained" color="primary" startIcon={<AddIcon />}>
                        Submit
                    </Button>
                </DialogActions>
            </Dialog>
            <TableContainer component={Paper} style={{ width: '100%', height: '100%' }}>
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
                            <TableRow key={row.alias} onDoubleClick={() => handleRowDoubleClick(row.id)}>
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