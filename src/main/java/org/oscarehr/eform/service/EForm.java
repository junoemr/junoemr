package org.oscarehr.eform.service;

import org.apache.log4j.Logger;
import org.oscarehr.eform.dao.EFormDataDao;
import org.oscarehr.eform.dao.EFormValueDao;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EForm
{
	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	private EFormDataDao eformDataDao;

	@Autowired
	private EFormValueDao eformValueDao;


}
