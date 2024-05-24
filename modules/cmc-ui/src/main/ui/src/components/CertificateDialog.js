import React, { useState } from 'react';
import { Modal, Button, Form, Input, Upload } from 'antd';
import { PlusOutlined, UploadOutlined } from '@ant-design/icons';
import { uploadCertificate } from '../services/api';

export default function CertificateDialog ({isOpen, setIsOpen, onDialogClose}){
  const [form] = Form.useForm();
  const [selectedFile, setSelectedFile] = useState(null);
  const [selectedFileName, setSelectedFileName] = useState('');
  const [fileList, setFileList] = useState([]);

  const handleFileUpload = (file) => {
    setSelectedFile(file);
    setSelectedFileName(file.name);
    return false;
  };

  const uploadFile = async () => {
  try {
    const values = await form.validateFields();
    if (!selectedFile || values.alias.trim() === '') {
      //alert("Please select a file and enter an alias!");
      return false;
    }

    try {
      await uploadCertificate(selectedFile, values.alias);
      form.resetFields();
      setIsOpen(false);
      onDialogClose();
      setFileList([]);
      setSelectedFile(null);
      return true;
    } catch (error) {
      console.error("Error uploading file:", error);
    }
  } catch (errorInfo) {
    console.error('Failed:', errorInfo);
  };
}

  const normFile = e => {
    if (Array.isArray(e)) {
      return e;
    }
    return e && e.fileList;
  };

  return (
  <Modal open={isOpen}
    onCancel={() => {
    setIsOpen(false);
    form.resetFields();
    setFileList([]);
    setSelectedFile(null);
    }}
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
        <Form.Item
          name="upload"
          label="Upload"
          valuePropName="fileList"
          getValueFromEvent={normFile}
          rules={[{ required: true, message: 'Please upload a file!' }]}
        >
        <Upload
        accept=".pem"
        beforeUpload={handleFileUpload}
        showUploadList={true}
         fileList={fileList} // Pass fileList as a prop to Upload
          onChange={({ fileList: newFileList }) => {
            setFileList(newFileList); // Update fileList in the onChange handler
          }}
        >
          <Button icon={<UploadOutlined />}>Upload file</Button>
        </Upload>
        </Form.Item>
      </Form>
    </Modal>
  );
}