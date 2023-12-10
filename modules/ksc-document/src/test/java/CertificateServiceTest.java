import com.lichbalab.ksc.dto.CertificateDto;
import com.lichbalab.ksc.mapper.CertificateMapper;
import com.lichbalab.ksc.repository.CertificateRepository;
import com.lichbalab.ksc.service.CertificateService;

public class CertificateServiceTest extends CertificateService {

    private CertificateDto certificate;

    public CertificateServiceTest(CertificateDto certificate) {
        super(null, null);
        this.certificate = certificate;
    }

    public CertificateServiceTest(CertificateRepository certificateRepository, CertificateMapper mapper) {
        super(certificateRepository, mapper);
    }

    @Override
    public CertificateDto getCertByAlias(String alias) {
        return certificate;
    }
}
