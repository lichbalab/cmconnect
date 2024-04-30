import React from 'react';
import './App.css';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import CertificateDashboard from './components/CertificateDashboard';
import CertificateDetails from './components/CertificateDetails';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<CertificateDashboard />} />
        <Route path="/certificate/:id" element={<CertificateDetails />} />
      </Routes>
    </Router>
  );
}

export default App;