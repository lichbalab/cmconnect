import React, { useState, useEffect } from 'react';
import { getCertificates, deleteCertificate } from '../services/api';
import { Table, Button } from 'antd';
import { useNavigate } from 'react-router-dom';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
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

    const columns = [
        { title: 'Alias', dataIndex: 'alias', key: 'alias' },
        { title: 'Subject', dataIndex: 'subject', key: 'subject' },
        { title: 'Issuer', dataIndex: 'issuer', key: 'issuer' },
        { title: 'Serial Number', dataIndex: 'serialNumber', key: 'serialNumber' },
        { title: 'Expiration Timestamp', dataIndex: 'expirationDate', key: 'expirationDate' },
        { title: 'Action', key: 'action', render: (text, record) => (
            <>
                <Button icon={<DeleteOutlined/>} onClick={() => handleDelete(record.id)}/>
                <Button icon={<EditOutlined />} onClick={() => navigate(`/certificate/${record.id}`)}/>
            </>
        )}
    ];

    return (
        <div style={{ width: '100%', height: '100%' }}>
            <Button
                onClick={() => setFormVisible(true)}
                type="primary"
                icon={<PlusOutlined />}
                style={{ marginTop: 24, marginBottom: 24, float: 'right' }}
            >
                Add Certificate
            </Button>
            <CertificateDialog
              isOpen={isFormVisible}
              setIsOpen={setFormVisible}
              onDialogClose={loadCertificates}
            />
            <Table
                onRow={(record, rowIndex) => {
                    return {
                        onDoubleClick: event => handleRowDoubleClick(record.id),
                    };
                }}
                columns={columns}
                dataSource={certificates}
                rowKey="id"
            />
        </div>
    );
};

export default CertificateTable;