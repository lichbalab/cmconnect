import React, { useState } from 'react';
import { Modal, Button, Form, Input, Upload } from 'antd';
import { PlusOutlined, UploadOutlined } from '@ant-design/icons';
import { uploadCertificate } from '../services/api';

export default function CertificateDialog ({isFormVisible, setFormVisible, onDialogClose}){
  const [form] = Form.useForm();
  const [selectedFile, setSelectedFile] = useState(null);
  const [selectedFileName, setSelectedFileName] = useState('');

  const handleFileUpload = (file) => {
    setSelectedFile(file);
    setSelectedFileName(file.name);
    return false;
  };

  const uploadFile = async () => {
    const values = form.getFieldsValue();
    if (!selectedFile || values.alias.trim() === '') {
      alert("Please select a file and enter an alias!");
      return false;
    }

    try {
      await uploadCertificate(selectedFile, values.alias);
      form.resetFields();
      setFormVisible(false);
      onDialogClose();
      return true;
    } catch (error) {
      console.error("Error uploading file:", error);
    }
  };

  return (
  <Modal visible={isFormVisible}
    onCancel={() => setFormVisible(false)}
    onOk={uploadFile}
    width={500} title="Add Certificate"
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="alias"
          label="Alias"
          rules={[{ required: true, message: 'Please input an alias!' }]}
        >
          <Input />
        </Form.Item>
        <Upload beforeUpload={handleFileUpload} showUploadList={true}>
          <Button icon={<UploadOutlined />}>Upload file</Button>
        </Upload>
      </Form>
    </Modal>
  );
}