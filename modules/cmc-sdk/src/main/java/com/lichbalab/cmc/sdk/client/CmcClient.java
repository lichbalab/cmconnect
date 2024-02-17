package com.lichbalab.cmc.sdk.client;

import com.lichbalab.certificate.Certificate;

public interface CmcClient {

    Certificate getCertificateByAlias(String alias);
}
