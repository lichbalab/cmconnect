
import React, { useState, useEffect } from 'react';
import { getCertificates, deleteCertificate } from '../services/api';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button } from '@mui/material';

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
        <TableContainer component={Paper} style={{ width: '100%', height: '100%' }}>
          <h2 style={{ textAlign: 'center' }}>Certificates</h2>
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
                  <TableCell>{row.expirationTimestamp}</TableCell>
                  <TableCell>
                    <Button
                      variant="contained"
                      color="secondary"
                      onClick={() => handleDelete(row.id)}
                    >
                      Remove
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      );
    };

export default CertificateTable;