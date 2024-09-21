import React from 'react';
import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
import { Layout, Menu } from 'antd';
import { HomeOutlined, SafetyCertificateOutlined } from '@ant-design/icons';
import './App.css';
import CertificateDashboard from './components/CertificateDashboard';
import CertificateDetails from './components/CertificateDetails';

const { Header, Content, Footer } = Layout;

function App() {
  return (
    <Router>
      <Layout className="layout" style={{ minHeight: '100vh' }}>
        <Header>
          <div className="logo" />
          <Menu theme="dark" mode="horizontal" defaultSelectedKeys={['1']}>
            <Menu.Item key="1" icon={<HomeOutlined />}>
              <Link to="/">Dashboard</Link>
            </Menu.Item>
            <Menu.Item key="2" icon={<SafetyCertificateOutlined />}>
              <Link to="/">Certificates</Link>
            </Menu.Item>
          </Menu>
        </Header>
        <Content style={{ padding: '0 25px', marginTop: 34 }}>
          <div className="site-layout-content" style={{ background: '#fff', padding: 24, minHeight: 380 }}>
            <Routes>
              <Route path="/" element={<CertificateDashboard />} />
              <Route path="/certificate/:id" element={<CertificateDetails />} />
            </Routes>
          </div>
        </Content>
        <Footer style={{ textAlign: 'center' }}>lichbalab ©2024</Footer>
      </Layout>
    </Router>
  );
}

export default App;
