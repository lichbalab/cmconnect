package com.lichbalab.cmc.mapper;

import com.lichbalab.certificate.dto.CertificateDto;
import com.lichbalab.cmc.model.Certificate;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CertificateMapper {
    CertificateDto domToDto(Certificate certificate);

    Certificate dtoToDom(CertificateDto certificateDTO);
}
