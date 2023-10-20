package com.lichbalab.ksc.mapper;

import com.lichbalab.ksc.dto.CertificateDto;
import com.lichbalab.ksc.model.Certificate;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CertificateMapper {
    CertificateDto domToDto(Certificate certificate);

    Certificate dtoToDom(CertificateDto certificateDTO);
}
