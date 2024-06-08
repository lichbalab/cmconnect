package com.lichbalab.cmc.sdk.client;

import com.lichbalab.certificate.Certificate;

import java.util.List;

public interface CmcClient {

    Certificate getCertificateByAlias(String alias);

    List<Certificate> getCertificates();
}
