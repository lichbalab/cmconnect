package com.lichbalab.cmc.sdk.ssl;

import javax.net.ssl.SSLContext;

public interface SslProfile {

    SSLContext createSslContext();
}
