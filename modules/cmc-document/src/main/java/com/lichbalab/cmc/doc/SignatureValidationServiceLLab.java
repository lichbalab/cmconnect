package com.lichbalab.cmc.doc;

import eu.europa.esig.dss.ws.validation.dto.WSReportsDTO;

public interface SignatureValidationServiceLLab {

    WSReportsDTO validateSignature(byte[] signedDocument);
}
