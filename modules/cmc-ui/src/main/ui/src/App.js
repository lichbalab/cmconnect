import React from 'react';
import './App.css';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import CertificateTable from './components/CertificateTable';
import CertificateDetails from './components/CertificateDetails';

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path='/certificate/:id' element={<CertificateDetails/>} />
                <Route path='/' element={<CertificateTable/>} />
            </Routes>
        </Router>
    );
};

export default App;