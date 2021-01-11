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


package oscar.oscarBilling.ca.bc.pageUtil;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.billing.CA.BC.dao.TeleplanS21Dao;
import org.oscarehr.billing.CA.BC.model.TeleplanS21;
import org.oscarehr.billing.CA.service.EligibilityCheckService;
import org.oscarehr.billing.CA.transfer.EligibilityCheckTransfer;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.DiagnosticCodeDao;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import oscar.entities.Billactivity;
import oscar.oscarBilling.ca.bc.MSP.MspErrorCodes;
import oscar.oscarBilling.ca.bc.Teleplan.TeleplanAPI;
import oscar.oscarBilling.ca.bc.Teleplan.TeleplanCodesManager;
import oscar.oscarBilling.ca.bc.Teleplan.TeleplanResponse;
import oscar.oscarBilling.ca.bc.Teleplan.TeleplanSequenceDAO;
import oscar.oscarBilling.ca.bc.Teleplan.TeleplanService;
import oscar.oscarBilling.ca.bc.Teleplan.TeleplanUserPassDAO;
import oscar.oscarBilling.ca.bc.data.BillActivityDAO;
import oscar.oscarBilling.ca.bc.data.BillingCodeData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author jay
 */
public class ManageTeleplanAction extends DispatchAction {

    private static Logger log = MiscUtils.getLogger();
	private static final String teleplan_login_failure_msgHdr = "An error occured connecting to teleplan: <br/>";
	private static final OscarProperties props = OscarProperties.getInstance();

	/** Creates a new instance of ManageTeleplanAction */
    public ManageTeleplanAction() {
    }

	public ActionForward unspecified(ActionMapping mapping, ActionForm form,
	                                 HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		log.debug("UNSPECIFIED ACTION!");
		return mapping.findForward("success");
	}

	public ActionForward setUserName(ActionMapping mapping, ActionForm form,
	                                 HttpServletRequest request, HttpServletResponse response)
	{
		log.debug("SET USER NA<E ACTION JACKSON");
		String username = request.getParameter("user");
		String password = request.getParameter("pass");

		log.debug("username " + username + " password " + password);

		//TODO-legacy: validate username - make sure url is not null

		TeleplanUserPassDAO dao = new TeleplanUserPassDAO();
		dao.saveUpdateUsername(username);
		dao.saveUpdatePasssword(password);
		return mapping.findForward("success");
	}

	public ActionForward updateBillingCodes(ActionMapping mapping, ActionForm form,
	                                        HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		TeleplanUserPassDAO dao = new TeleplanUserPassDAO();
		String[] userpass = dao.getUsernamePassword();
		TeleplanService tService = new TeleplanService();
		TeleplanAPI tAPI = null;
		try
		{
			tAPI = tService.getTeleplanAPI(userpass[0], userpass[1]);
		}
		catch(Exception e)
		{
			request.setAttribute("error", teleplan_login_failure_msgHdr + e.getMessage());
			return mapping.findForward("error");
		}

		TeleplanResponse tr = tAPI.getAsciiFile("3");

		log.debug("real filename " + tr.getRealFilename());

		File file = tr.getFile();
		TeleplanCodesManager tcm = new TeleplanCodesManager();
		List list = tcm.parse(file);
		request.setAttribute("codes", list);
		return mapping.findForward("codelist");
	}


	public ActionForward updateteleplanICDCodesList(ActionMapping mapping, ActionForm form,
	                                                HttpServletRequest request, HttpServletResponse response) throws IOException
	{

		DiagnosticCodeDao bDx = SpringUtils.getBean(DiagnosticCodeDao.class);

		log.debug("UPDATE ICD  CODE WITH PARSING");
		TeleplanUserPassDAO dao = new TeleplanUserPassDAO();
		String[] userpass = dao.getUsernamePassword();
		TeleplanService tService = new TeleplanService();
		TeleplanAPI tAPI = null;
		try
		{
			tAPI = tService.getTeleplanAPI(userpass[0], userpass[1]);
		}
		catch(Exception e)
		{
			request.setAttribute("error", teleplan_login_failure_msgHdr + e.getMessage());
			return mapping.findForward("error");
		}

		TeleplanResponse tr = tAPI.getAsciiFile("2");

		log.debug("real filename " + tr.getRealFilename());

		File file = tr.getFile();

		TeleplanCodesManager tcm = new TeleplanCodesManager();
		tcm.parseICD9(file);

		tr = tAPI.getAsciiFile("I");
		log.debug("real filename " + tr.getRealFilename());
		File Ifile = tr.getFile();
		tcm.parseICD9(Ifile);

		return mapping.findForward("success");
	}

    /*
     *  2 = MSP ICD9 Codes (3 char)
	*      1 = MSP Explanatory Codes List
     */
	public ActionForward updateExplanatoryCodesList(ActionMapping mapping, ActionForm form,
	                                                HttpServletRequest request, HttpServletResponse response) throws IOException
	{

		TeleplanUserPassDAO dao = new TeleplanUserPassDAO();
		String[] userpass = dao.getUsernamePassword();
		TeleplanService tService = new TeleplanService();
		TeleplanAPI tAPI;
		try
		{
			tAPI = tService.getTeleplanAPI(userpass[0], userpass[1]);
		}
		catch(Exception e)
		{
			request.setAttribute("error", teleplan_login_failure_msgHdr + e.getMessage());
			return mapping.findForward("error");
		}

		TeleplanResponse tr = tAPI.getAsciiFile("1");

		log.debug("real filename " + tr.getRealFilename());

		File file = tr.getFile();
		BufferedReader buff = new BufferedReader(new FileReader(file));

		String line = null;

		boolean start = false;
		StringBuilder sb = new StringBuilder();
		MspErrorCodes errorCodes = new MspErrorCodes();

		while((line = buff.readLine()) != null)
		{
			line = line.trim();
			if(line != null && line.startsWith("--"))
			{
				start = true;
				continue;
			}
			if(start)
			{
				if(line.trim().equals(""))
				{
					String togo = sb.toString();
					sb = new StringBuilder();
					if(!togo.equals(""))
					{
						errorCodes.put(togo.substring(0, 2), togo.substring(4));
					}
				}
				else
				{
					sb.append(line);
				}
			}
		}

		if(sb.length() > 0)
		{ //still left in the buffer
			String togo = sb.toString();
			int i = togo.lastIndexOf("#TID");
			if(i != -1)
			{
				togo = togo.substring(0, i);
			}

			if(!togo.equals(""))
			{
				errorCodes.put(togo.substring(0, 2), togo.substring(4));
			}
		}

		errorCodes.save();

		//...I guess pass the errors back to jsp
		StringBuilder errorStr = new StringBuilder("");
		for(Entry error : errorCodes.entrySet())
		{
			errorStr.append("Error codes: \n");
			errorStr.append(error.getKey());
			errorStr.append(" -- ");
			errorStr.append(error.getValue());
			errorStr.append("\n<br/>");
		}
		request.setAttribute("error", errorStr);
		//---------------------------------------------------------------------------

		log.info("Msp error codes " + errorCodes.size());
		log.debug(sb.toString());
		return mapping.findForward("success");
	}


	public ActionForward commitUpdateBillingCodes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		String[] codes = request.getParameterValues("codes");
		if(codes != null)
		{
			for(String code : codes)
			{
				String nCode = code.substring(0, 5);
				String fee = code.substring(5, 13).trim();
				String desc = code.substring(13).trim();
				BillingCodeData bcd = new BillingCodeData();
				bcd.addBillingCode(nCode, desc, fee);
			}
		}
		return mapping.findForward("success");
	}

	public ActionForward getSequenceNumber(ActionMapping mapping, ActionForm form,
	                                       HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		log.debug("getSequenceNumber");
		OscarProperties prop = OscarProperties.getInstance();
		String datacenter = prop.getProperty("dataCenterId", "");
		if(datacenter.length() != 5)
		{
			//this.addMessages() //TODO-legacy:ADD MESSAGE ABOUT DATA CENTER NOT BEING CORRECT
			log.debug("returning because of datacenter #" + datacenter);
			return mapping.findForward("success");
		}
		TeleplanUserPassDAO dao = new TeleplanUserPassDAO();
		String[] userpass = dao.getUsernamePassword();
		TeleplanService tService = new TeleplanService();

		TeleplanAPI tAPI = null;
		try
		{
			tAPI = tService.getTeleplanAPI(userpass[0], userpass[1]);
		}
		catch(Exception e)
		{
			log.warn(e.getMessage());
			request.setAttribute("error", teleplan_login_failure_msgHdr + e.getMessage());
			return mapping.findForward("error");
		}


		int sequenceNumber = tService.getSequenceNumber(tAPI, datacenter);

		TeleplanSequenceDAO seq = new TeleplanSequenceDAO();
		seq.saveUpdateSequence(sequenceNumber);
		return mapping.findForward("success");
	}

	public ActionForward setSequenceNumber(ActionMapping mapping, ActionForm form,
	                                       HttpServletRequest request, HttpServletResponse response)
	{
		log.debug("setSequenceNumber");
		String sequence = request.getParameter("num");
		int sequenceNumber = -1;
		try
		{
			sequenceNumber = Integer.parseInt(sequence);
		}
		catch(Exception e)
		{
			//TODO-legacy: ADDED ERROR MESSAGE ABOUT THE NUMBER NOT BEING A NUMBER!
			return mapping.findForward("success");
		}

		if(sequenceNumber < 0 || sequenceNumber > 9999999)
		{
			//TODO-legacy: ADDED ERROR MESSAGE ABOUT NUMBER BEING OUT OF RANGE
			return mapping.findForward("success");
		}

		TeleplanSequenceDAO seq = new TeleplanSequenceDAO();
		seq.saveUpdateSequence(sequenceNumber);
		return mapping.findForward("success");
	}


	public ActionForward sendFile(ActionMapping mapping, ActionForm form,
	                              HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		log.debug("sendFile Start");
		String id = request.getParameter("id");

		TeleplanUserPassDAO dao = new TeleplanUserPassDAO();
		String[] userpass = dao.getUsernamePassword();
		TeleplanService tService = new TeleplanService();
		TeleplanAPI tAPI = null;
		try
		{
			tAPI = tService.getTeleplanAPI(userpass[0], userpass[1]);
		}
		catch(Exception e)
		{
			log.error(e.getMessage(), e);
			request.setAttribute("error", teleplan_login_failure_msgHdr + e.getMessage());
			return mapping.findForward("submission");
		}


		//TODO-legacy: validate username - make sure url is not null
		BillActivityDAO billActDAO = new BillActivityDAO();
		List l = billActDAO.getBillactivityByID(id);

		Billactivity b = (Billactivity) l.get(0);
		String filename = b.getOhipfilename();

		OscarProperties prop = OscarProperties.getInstance();
		String datacenter = prop.getProperty("HOME_DIR", "");

		File f = new File(datacenter, filename);


		if(f != null && log.isDebugEnabled())
		{
			log.debug("File is Readable: " + f.canRead());
			log.debug("File exists: " + f.exists());
			log.debug("File Path " + f.getCanonicalPath());
		}
		log.info("sending file " + f.getAbsolutePath());

		TeleplanResponse tr = tAPI.putMSPFile(f);
		log.debug("sendFile End" + tr.getResult());
		if(!tr.isSuccess())
		{
			request.setAttribute("error", tr.getMsgs());
		}
		else
		{
			billActDAO.setStatusToSent(b);
		}


		return mapping.findForward("submission");
	}

	public ActionForward remit(ActionMapping mapping, ActionForm form,
	                           HttpServletRequest request, HttpServletResponse response)
	{
		String remittanceFilename;
		// should be enabled for dev use/ bug fixing only
		boolean loadFromFile = props.isPropertyActive("billing.remit_load_from_file.enabled");
		if(loadFromFile)
		{
			remittanceFilename = props.getProperty("billing.remit_load_from_file.file");
			TeleplanS21Dao s21Dao = SpringUtils.getBean(TeleplanS21Dao.class);

			List<TeleplanS21> matches = s21Dao.findByFilename(remittanceFilename);
			if(!matches.isEmpty())
			{
				log.error("Aborted. Remittance file has already been loaded: " + remittanceFilename);
				return mapping.findForward("error");
			}
			else
			{
				log.warn("Loading remittance from file: " + remittanceFilename);
			}
		}
		else
		{
			TeleplanUserPassDAO dao = new TeleplanUserPassDAO();
			String[] userpass = dao.getUsernamePassword();
			TeleplanService tService = new TeleplanService();

			TeleplanAPI tAPI;
			try
			{
				tAPI = tService.getTeleplanAPI(userpass[0], userpass[1]);
			}
			catch(Exception e)
			{
				log.warn(e.getMessage());
				request.setAttribute("error", teleplan_login_failure_msgHdr + e.getMessage());
				return mapping.findForward("error");
			}

			TeleplanResponse tr = tAPI.getRemittance(true);
			log.debug(tr.toString());
			log.debug("real filename " + tr.getRealFilename());
			remittanceFilename = tr.getRealFilename();
		}

		request.setAttribute("filename", remittanceFilename);
		return mapping.findForward("remit");
	}

	public ActionForward setPass(ActionMapping mapping, ActionForm form,
	                             HttpServletRequest request, HttpServletResponse response)
	{
		String newpass = request.getParameter("newpass");
		TeleplanUserPassDAO dao = new TeleplanUserPassDAO();
		dao.saveUpdatePasssword(newpass);
		return mapping.findForward("success");
	}


	public ActionForward changePass(ActionMapping mapping, ActionForm form,
	                                HttpServletRequest request, HttpServletResponse response)
	{

		String newpass = request.getParameter("newpass");
		String confpass = request.getParameter("confpass");

		//TODO-legacy: validate username - make sure url is not null

		if(!newpass.equals(confpass))
		{
			return mapping.findForward("error");
		}
		//CHECK TELEPLAN

		TeleplanUserPassDAO dao = new TeleplanUserPassDAO();
		String[] userpass = dao.getUsernamePassword();


		TeleplanAPI tAPI = new TeleplanAPI();
		try
		{
			//THIS IS DIFFERENT BECAUSE THE NORMAL TELEPLAN SERVICE WILL THROW AN EXCEPTION BECAUSE IT CHECKS FOR RESULT OF SUCCESS.
			//IF PASSWORD HAS EXPIRED THE RESULTS is EXPIRED.PASSWORD
			TeleplanResponse tr = tAPI.login(userpass[0], userpass[1]);
		}
		catch(Exception e)
		{
			log.warn(e.getMessage());
			request.setAttribute("error", e.getMessage());
			return mapping.findForward("error");
		}

		TeleplanResponse tr = tAPI.changePassword(userpass[0], userpass[1], newpass, confpass);
		log.debug("change password " + tr.getResult());
		if(!tr.isSuccess())
		{
			request.setAttribute("error", tr.getMsgs());
		}
		else
		{
			dao.saveUpdatePasssword(newpass);
		}
		return mapping.findForward("success");
	}


	public ActionForward checkElig(ActionMapping mapping, ActionForm form,
	                               HttpServletRequest request, HttpServletResponse response)
	{
		log.debug("checkElig");
		String demographicNo = request.getParameter("demographic");
		OscarProperties oscarProperties = OscarProperties.getInstance();
		DemographicDao dDao = SpringUtils.getBean(DemographicDao.class);
		EligibilityCheckService eligibilityCheckService = SpringUtils.getBean(EligibilityCheckService.class);

		Demographic demo = dDao.getDemographic(demographicNo);

		EligibilityCheckTransfer transfer = null;
		if (oscarProperties.isClinicaidBillingType())
		{
			try
			{
				transfer = eligibilityCheckService.checkEligibility(demo);
				request.setAttribute("error", transfer.getError());
				request.setAttribute("Result", transfer.getResult());
				request.setAttribute("Msgs", transfer.getMessage());
			}
			catch(Exception e)
			{
				log.error("Failed to get eligibility status through ClinicAid API.", e);
				request.setAttribute("error", e.getMessage());
			}
		}
		else
		{
			try
			{
				transfer = eligibilityCheckService.checkEligibility(demo);

				request.setAttribute("Result", transfer.getResult());

				String realFile = transfer.getRealFilename();
				if(realFile != null && !realFile.trim().equals(""))
				{
					File file = FileFactory.getRemittanceFile(realFile).getFileObject();
					BufferedReader buff = new BufferedReader(new FileReader(file));
					StringBuilder sb = new StringBuilder();
					String line = null;

					while((line = buff.readLine()) != null)
					{

						if(line != null && line.startsWith("ELIG_ON_DOS:"))
						{
							String el = line.substring(12).trim();
							if(el.equalsIgnoreCase("no"))
							{
								request.setAttribute("Result", "Failure");

								line = "<span style=\"color:red; font-weight:bold;\">" + line + "</span>";
							}
							else if(el.equalsIgnoreCase("yes"))
							{
								Date lastEligCheck = new Date();
								demo.setHcRenewDate(lastEligCheck);
								dDao.save(demo);
							}
						}
						sb.append(line);
						sb.append("<br>");
					}
					request.setAttribute("Msgs", sb.toString());

				}
				else
				{
					request.setAttribute("Msgs", transfer.getMessage());
				}
			}
			catch(Exception e)
			{
				log.warn(e.getMessage());
				request.setAttribute("error", teleplan_login_failure_msgHdr + e.getMessage());
			}
		}
		return mapping.findForward("checkElig");
	}
}
