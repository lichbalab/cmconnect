import React, { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, Stack, Paper } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import { styled } from '@mui/material/styles';
import Draggable from 'react-draggable';
import { uploadCertificate } from '../services/api'; // Assuming the uploadCertificate function is exported from this file

const VisuallyHiddenInput = styled('input')({
  clip: 'rect(0 0 0 0)',
  clipPath: 'inset(50%)',
  height: 1,
  overflow: 'hidden',
  position: 'absolute',
  bottom: 0,
  left: 0,
  whiteSpace: 'nowrap',
  width: 1,
});


export default function CertificateDialog ({isFormVisible, setFormVisible, onDialogClose}){
  const [alias, setAlias] = useState('');
  const [selectedFile, setSelectedFile] = useState(null);
  const [selectedFileName, setSelectedFileName] = useState('');

  const handleFileUpload = (event) => {
    setSelectedFile(event.target.files[0]);
    setSelectedFileName(event.target.files[0].name);
  };

  const handleAliasChange = (event) => {
    setAlias(event.target.value);
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
      onDialogClose();
    } catch (error) {
      console.error("Error uploading file:", error);
    }
  };

  return (
    <Dialog open={isFormVisible} onClose={() => setFormVisible(false)} maxWidth="md" sx={{ width: 500 }} fullWidth={true}>
      <DialogTitle style={{ cursor: 'move' }} id="draggable-dialog-title">Add Certificate</DialogTitle>
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
          value={alias} onChange={handleAliasChange}/>
        <Stack direction="row" spacing={2} alignItems="center">
          <Button
            component="label"
            role={undefined}
            variant="contained"
            tabIndex={-1}
            startIcon={<CloudUploadIcon />}
          >
            Upload file
            <VisuallyHiddenInput type="file" onChange={handleFileUpload}/>
          </Button>
          {selectedFileName && <p> {selectedFileName}</p>}
        </Stack>
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
  );
}