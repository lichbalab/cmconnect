package com.lichbalab.cmc.repository;

import com.lichbalab.cmc.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    Certificate getCertByAlias(String alias);
}

