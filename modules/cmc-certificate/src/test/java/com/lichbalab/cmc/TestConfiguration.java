package com.lichbalab.cmc;


import com.lichbalab.cmc.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

/**
 * The class added just for package scanning investigation.
 */
@ContextConfiguration(classes = {CertificateService.class})
@DataJpaTest(properties = { "spring.test.database.replace=none"})
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.lichbalab.cmc")
class TestConfiguration {

    @Autowired
    private CertificateService service;

    //@Test
    void contextLoads() {
    }

}
