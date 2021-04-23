package org.oscarehr.ws.rest.conversion;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.common.model.ConsultationServices;
import org.oscarehr.ws.rest.to.model.ConsultationServiceTo1;
import org.oscarehr.ws.rest.to.model.ProfessionalSpecialistTo1;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConsultationServicesToTransferConverter extends AbstractModelConverter<ConsultationServices, ConsultationServiceTo1>
{
	@Autowired
	private ProfessionalSpecialistToTransferConverter specialistToTransferConverter;

	@Override
	public ConsultationServiceTo1 convert(ConsultationServices domain)
	{
		ConsultationServiceTo1 transfer = new ConsultationServiceTo1();

		BeanUtils.copyProperties(domain, transfer, "specialists");

		List<ProfessionalSpecialistTo1> specialists = specialistToTransferConverter.convert(domain.getSpecialists());
		transfer.setSpecialists(specialists);

		return transfer;
	}
}
