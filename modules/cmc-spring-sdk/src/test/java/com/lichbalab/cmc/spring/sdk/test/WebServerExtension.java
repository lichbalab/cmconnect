package com.lichbalab.cmc.spring.sdk.test;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.web.server.WebServer;

@SpringBootTest
public class WebServerExtension implements BeforeEachCallback {

    @Autowired
    private WebServer webServer;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        // Restart logic for WebServer
        if (webServer != null) {
            webServer.stop();
            webServer.start();
        }
    }
}