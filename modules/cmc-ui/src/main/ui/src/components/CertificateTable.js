import React, { useState, useEffect } from 'react';
import { getCertificates, deleteCertificate } from '../services/api';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button} from '@mui/material';
import { Link, useNavigate } from 'react-router-dom';
import AddIcon from '@mui/icons-material/Add';
import CertificateDialog from './CertificateDialog';

const CertificateTable = () => {
    const [certificates, setCertificates] = useState([]);
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


    const navigate = useNavigate();

    const handleRowDoubleClick = (id) => {
        navigate(`/certificate/${id}`);
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
            <CertificateDialog
              isFormVisible={isFormVisible}
              setFormVisible={setFormVisible}
              onDialogClose={loadCertificates}
            />
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
                            <TableRow key={row.id} onDoubleClick={() => handleRowDoubleClick(row.id)}>
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