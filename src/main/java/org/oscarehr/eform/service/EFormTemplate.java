package org.oscarehr.eform.service;

import org.apache.log4j.Logger;
import org.oscarehr.eform.dao.EFormDao;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EFormTemplate
{
	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	private EFormDao eformDao;
}
