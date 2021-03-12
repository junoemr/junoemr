package org.oscarehr.clinic.service;

import org.oscarehr.common.dao.ClinicDAO;
import org.oscarehr.common.model.Clinic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service("clinic.service.ClinicService")
@Transactional
public class ClinicService
{
    @Autowired
    ClinicDAO clinicDao;

    public String createClinicUuid(Clinic clinic)
    {
        UUID newUuid = UUID.randomUUID();
        clinic.setUuid(newUuid.toString());
        clinicDao.save(clinic);

        return clinic.getUuid();
    }
}
