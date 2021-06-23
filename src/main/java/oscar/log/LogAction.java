/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version. 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


package oscar.log;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.OscarLogDao;
import org.oscarehr.log.dao.RestServiceLogDao;
import org.oscarehr.common.model.OscarLog;
import org.oscarehr.common.model.Provider;
import org.oscarehr.log.model.RestServiceLog;
import org.oscarehr.util.DeamonThreadFactory;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.external.soap.logging.dao.SoapServiceLogDao;
import org.oscarehr.ws.external.soap.logging.model.SoapServiceLog;

public class LogAction {
	private static Logger logger = MiscUtils.getLogger();
	private static OscarLogDao oscarLogDao = (OscarLogDao) SpringUtils.getBean("oscarLogDao");
	private static RestServiceLogDao restLogDao = (RestServiceLogDao) SpringUtils.getBean(org.oscarehr.log.dao.RestServiceLogDao.class);
	private static SoapServiceLogDao soapLogDao = (SoapServiceLogDao) SpringUtils.getBean(SoapServiceLogDao.class);
	private static ExecutorService executorService = Executors.newCachedThreadPool(new DeamonThreadFactory(LogAction.class.getSimpleName()+".executorService", Thread.MAX_PRIORITY));
	
	/**
	 * This method will add a log entry asynchronously in a separate thread.
	 * @deprecated - use addLogEntry instead
	 */
	@Deprecated
	public static void addLog(String provider_no, String action, String content, String data) {
		addLog(provider_no, action, content, null, null, null, data);
	}

	/**
	 * This method will add a log entry asynchronously in a separate thread.
	 * @deprecated - use addLogEntry instead
	 */
	@Deprecated
	public static void addLog(String provider_no, String action, String content, String contentId, String ip) {
		addLog(provider_no, action, content, contentId, ip, null, null);
	}

	/**
	 * This method will add a log entry asynchronously in a separate thread.
	 * @deprecated - use addLogEntry instead
	 */
	@Deprecated
	public static void addLog(String provider_no, String action, String content, String contentId, String ip, String demographicNo) {
		addLog(provider_no, action, content, contentId, ip, demographicNo, null);
	}

	/**
	 * @deprecated - use addLogEntry instead
	 */
	@Deprecated
	public static void addLog(LoggedInInfo loggedInInfo, String action, String content, String contentId, String demographicNoStr, String data)
	{
		String providerNo = loggedInInfo.getLoggedInProvider()!=null ? loggedInInfo.getLoggedInProviderNo() : null;
		Integer securityId = loggedInInfo.getLoggedInProvider()!=null ? loggedInInfo.getLoggedInSecurity().getSecurityNo() : null;
		Integer demographicNo = null;
		try {
			demographicNoStr=StringUtils.trimToNull(demographicNoStr);
			if (demographicNoStr != null) demographicNo = Integer.parseInt(demographicNoStr);
		}
		catch (Exception e) {
			logger.error("Unexpected error", e);
		}
		
		addLogEntry(providerNo, demographicNo, action, content, null, contentId, loggedInInfo.getIp(), data, securityId);
	}
	
	/**
	 * This method will add a log entry asynchronously in a separate thread.
	 * @deprecated - use addLogEntry instead
	 */
	@Deprecated
	public static void addLog(String providerNo, String action, String content, String contentId, String ip, String demographicNoStr, String data) {
		Integer demographicNo = null;
		try {
			demographicNoStr=StringUtils.trimToNull(demographicNoStr);
			if (demographicNoStr != null) demographicNo = Integer.parseInt(demographicNoStr);
		}
		catch (Exception e) {
			logger.error("Unexpected error", e);
		}
		addLogEntry(providerNo, demographicNo, action, content, null, contentId, ip, data, null);
	}
	/**
	 * This method will add a log entry asynchronously in a separate thread.
	 */
	public static void addLogEntry(String providerNo, Integer demographicNo, String action, String module, String status, String contentId, String ip, String data, Integer securityId) {
		OscarLog oscarLog = new OscarLog();

		oscarLog.setProviderNo(providerNo);
		oscarLog.setDemographicId(demographicNo);
		oscarLog.setAction(action);
		oscarLog.setContent(module);
		oscarLog.setStatus(status);
		oscarLog.setContentId(contentId);
		oscarLog.setIp(ip);
		oscarLog.setSecurityId(securityId);
		oscarLog.setData(data);

		executorService.execute(new AddLogExecutorTask(oscarLog));
	}
	public static void addLogEntry(String providerNo, Integer demographicNo, String action, String module, String status, String contentId, String ip, String data) {
		addLogEntry(providerNo, demographicNo, action, module, status, contentId, ip, data, null);
	}
	public static void addLogEntry(String providerNo, Integer demographicNo, String action, String module, String status, String contentId, String ip) {
		addLogEntry(providerNo, demographicNo, action, module, status, contentId, ip, null, null);
	}
	public static void addLogEntry(String providerNo, Integer demographicNo, String action, String module, String status, String data) {
		addLogEntry(providerNo, demographicNo, action, module, status, null, null, data, null);
	}
	public static void addLogEntry(String providerNo, String action, String module, String status, String contentId, String ip) {
		addLogEntry(providerNo, null, action, module, status, contentId, ip, null, null);
	}
	public static void addLogEntry(String providerNo, String action, String module, String status, String data) {
		addLogEntry(providerNo, null, action, module, status, null, null, data, null);
	}


	public static void addLogEntrySynchronous(String providerNo, Integer demographicNo, String action, String module, String status, String contentId, String ip, String data, Integer securityId) {
		OscarLog oscarLog = new OscarLog();

		oscarLog.setProviderNo(providerNo);
		oscarLog.setDemographicId(demographicNo);
		oscarLog.setAction(action);
		oscarLog.setContent(module);
		oscarLog.setStatus(status);
		oscarLog.setContentId(contentId);
		oscarLog.setIp(ip);
		oscarLog.setSecurityId(securityId);
		oscarLog.setData(data);

		addLogSynchronous(oscarLog);
	}
	public static void addLogEntrySynchronous(String providerNo, Integer demographicNo, String action, String module, String status, String contentId, String ip, String data) {
		addLogEntrySynchronous(providerNo, demographicNo, action, module, status, contentId, ip, data, null);
	}
	public static void addLogEntrySynchronous(String providerNo, String action, String module, String status, String contentId, String ip, String data) {
		addLogEntrySynchronous(providerNo, null, action, module, status, contentId, ip, data, null);
	}
	public static void addLogEntrySynchronous(String providerNo, String action, String module, String status, String contentId, String ip) {
		addLogEntrySynchronous(providerNo, null, action, module, status, contentId, ip, null, null);
	}
	public static void addLogEntrySynchronous(String providerNo, String action, String module, String status, String data) {
		addLogEntrySynchronous(providerNo, null, action, module, status, null, null, data, null);
	}
	public static void addLogEntrySynchronous(String action, String data) {
		addLogEntrySynchronous(null, null, action, null, null, null, null, data, null);
	}
	
	/**
	 * @deprecated - use addLogEntrySynchronous instead
	 */
	@Deprecated
	public static void addLogSynchronous(LoggedInInfo loggedInInfo, String action, String data)
	{
		OscarLog logEntry=new OscarLog();
		if (loggedInInfo.getLoggedInSecurity()!=null) logEntry.setSecurityId(loggedInInfo.getLoggedInSecurity().getSecurityNo());
		if (loggedInInfo.getLoggedInProvider()!=null) logEntry.setProviderNo(loggedInInfo.getLoggedInProviderNo());
		logEntry.setAction(action);
		logEntry.setData(data);
		LogAction.addLogSynchronous(logEntry);		
	}
	/**
	 * This method will add a log entry in the same thread and can participate in the same transaction if one exists.
	 * @deprecated - use addLogEntrySynchronous instead
	 */
	@Deprecated
	public static void addLogSynchronous(String provider_no, String action, String content, String contentId, String ip) {
		OscarLog oscarLog = new OscarLog();

		oscarLog.setProviderNo(provider_no);
		oscarLog.setAction(action);
		oscarLog.setContent(content);
		oscarLog.setContentId(contentId);
		oscarLog.setIp(ip);

		addLogSynchronous(oscarLog);
	}

	/**
	 * This method will add the log entry in the same thread and transaction as it's being called in. This method will not throw exceptions, it will log to the file / console / log4j logger if an error occurs.
	 */
	public static void addLogSynchronous(OscarLog oscarLog) {
		try {
			oscarLogDao.persist(oscarLog);
		} catch (Exception e) {
			logger.error("Error in logger.", e);
			logger.error("Error log entry : " + oscarLog);
		}
	}
	
	
	/**
	 * ported from the old caisi pmm_log
	 */
	public static void log(String accessType, String entity, String entityId, HttpServletRequest request) {		
		OscarLog log = new OscarLog();
		
		Provider provider=(Provider) request.getSession().getAttribute("provider");
		if (provider!=null) log.setProviderNo(provider.getProviderNo());
		
		log.setAction(accessType);
		log.setContent(entity);
		log.setContentId(entityId);
		log.setIp(request.getRemoteAddr());
	
		oscarLogDao.persist(log);
	}
	/**
	 * persists the rest log using the restServiceLogDAO
	 * @param restLog
	 */
	public static void saveRestLogEntry(RestServiceLog restLog) {
		restLogDao.persist(restLog);
	}

	public static void saveSoapLogEntry(SoapServiceLog soapLog)
	{
		try
		{
			soapLogDao.persist(soapLog);
		}
		catch (PersistenceException e)
		{
			logger.error("Could not save SOAP service log");
		}

	}

	public static Instant printDuration(Instant start, String what)
	{
		Instant now = Instant.now();
		logger.info("[DURATION] " + what + " took " + Duration.between(start, now));
		return now;
	}
}
