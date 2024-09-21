// CertificateDetails.js
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Card, Descriptions, Spin, Typography, Space } from 'antd';
import { SafetyCertificateOutlined } from '@ant-design/icons';
import { getCertificate } from '../services/api';

const { Title } = Typography;

const CertificateDetails = () => {
    const { id } = useParams();
    const [certificate, setCertificate] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchCertificate = async () => {
            try {
                const response = await getCertificate(id);
                setCertificate(response.data);
            } catch (error) {
                console.error("Error fetching certificate:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchCertificate();
    }, [id]);

    if (loading) {
        return (
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <Spin size="large" />
            </div>
        );
    }

    if (!certificate) {
        return (
            <Card>
                <Title level={3}>Certificate not found</Title>
            </Card>
        );
    }

    return (
        <Card
            title={
                <Space>
                    <SafetyCertificateOutlined style={{ fontSize: '24px', color: '#1890ff' }} />
                    <Title level={2} style={{ margin: 0 }}>Certificate Details</Title>
                </Space>
            }
            style={{ maxWidth: 800, margin: '0 auto', marginTop: 20 }}
        >
            <Descriptions bordered column={1}>
                <Descriptions.Item label="Alias">{certificate.alias}</Descriptions.Item>
                <Descriptions.Item label="Subject">{certificate.subject}</Descriptions.Item>
                <Descriptions.Item label="Issuer">{certificate.issuer}</Descriptions.Item>
                <Descriptions.Item label="Serial Number">{certificate.serialNumber}</Descriptions.Item>
                <Descriptions.Item label="Expiration Date">{certificate.expirationDate}</Descriptions.Item>
                {/* Add more fields as needed */}
            </Descriptions>
        </Card>
    );
};

export default CertificateDetails;
