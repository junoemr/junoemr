/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package org.oscarehr.integration.iceFall.service;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.oscarehr.common.exception.HtmlToPdfConversionException;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.model.Provider;
import org.oscarehr.demographic.dao.DemographicExtDao;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.demographic.entity.DemographicExt;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.eform.service.EFormDataService;
import org.oscarehr.integration.iceFall.dao.IceFallCredentialsDao;
import org.oscarehr.integration.iceFall.dao.IceFallLogDao;
import org.oscarehr.integration.iceFall.model.IceFallCredentials;
import org.oscarehr.integration.iceFall.model.IceFallLog;
import org.oscarehr.integration.iceFall.service.exceptions.IceFallCustomerLookupException;
import org.oscarehr.integration.iceFall.service.exceptions.IceFallEmailExistsException;
import org.oscarehr.integration.iceFall.service.exceptions.IceFallException;
import org.oscarehr.integration.iceFall.service.exceptions.IceFallNoSuchDoctorException;
import org.oscarehr.integration.iceFall.service.exceptions.IceFallPdfGenerationException;
import org.oscarehr.integration.iceFall.service.exceptions.IceFallPrescriptionException;
import org.oscarehr.integration.iceFall.service.exceptions.IceFallRESTException;
import org.oscarehr.integration.iceFall.service.transfer.IceFallCreateCustomerTo1;
import org.oscarehr.integration.iceFall.service.transfer.IceFallCreatePrescriptionResponseTo1;
import org.oscarehr.integration.iceFall.service.transfer.IceFallCreatePrescriptionTo1;
import org.oscarehr.integration.iceFall.service.transfer.IceFallDoctorListTo1;
import org.oscarehr.integration.iceFall.service.transfer.IceFallDoctorTo1;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.WKHtmlToPdfUtils;
import org.oscarehr.ws.rest.integrations.iceFall.transfer.IceFallSendFormTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class IceFallService
{
	@Autowired
	IceFallCredentialsDao iceFallCredentialsDao;

	@Autowired
	IceFallRESTService iceFallRESTService;

	@Autowired
	IceFallLogDao iceFallLogDao;

	@Autowired
	DemographicExtDao demographicExtDao;

	@Autowired
	EFormDataService eFormDataService;

	public static final String PDF_DOCUMENT_HEADER = "data:application/pdf;base64,";
	public static final String CANOPY_CUSTOMER_ID_KEY = "canopy_customer_id";

	/**
	 * get icefall credentials from DB
	 * @return - the icefall credentials
	 */
	public IceFallCredentials getCredentials()
	{
		return iceFallCredentialsDao.getCredentials();
	}

	/**
	 * save icefall credentials to the database
	 * @param creds - the credentials to save
	 * @return - the saved credentials object
	 */
	public IceFallCredentials updateCredentials(IceFallCredentials creds)
	{
		iceFallCredentialsDao.merge(creds);
		return creds;
	}

	public IceFallLog logIceFallError(String message, String sendingProviderNo, Integer formId, Integer demographicNo, boolean formInstance)
	{
		return logIceFall(message, IceFallLog.STATUS.ERROR, sendingProviderNo, formId, demographicNo, formInstance);
	}

	public IceFallLog logIceFallSent(String message, String sendingProviderNo, Integer formId, Integer demographicNo, boolean formInstance)
	{
		return logIceFall(message, IceFallLog.STATUS.SENT, sendingProviderNo, formId, demographicNo, formInstance);
	}

	public IceFallLog logIceFall(String message, IceFallLog.STATUS status, String sendingProviderNo, Integer formId, Integer demographicNo, boolean formInstance)
	{
		IceFallLog iceFallLog = new IceFallLog();
		iceFallLog.setMessage(message);
		iceFallLog.setStatus(status);
		iceFallLog.setSendingProviderNo(sendingProviderNo);
		iceFallLog.setFormId(formId);
		iceFallLog.setDemographicNo(demographicNo);
		iceFallLog.setFormInstance(formInstance);

		iceFallLogDao.persist(iceFallLog);
		return iceFallLog;
	}

	/**
	 * get the ice fall log entries that fit the search criteria
	 * @param startDate - the start date to return logs for (inclusive)
	 * @param endDate - the end date to return logs for (inclusive)
	 * @param page - the page to return
	 * @param pageSize - the size of each page
	 * @param status - the status type of the logs to return. can be '%' for all status types.
	 * @return - a list of logs
	 */
	public List<IceFallLog> getIceFallLogs (LocalDateTime startDate, LocalDateTime endDate, Integer page, Integer pageSize, String status, IceFallLogDao.SORT_BY sortBy, IceFallLogDao.SORT_DIRECTION sortDirection)
	{
		return iceFallLogDao.getLogsPaginated(startDate, endDate, page, pageSize, status, sortBy, sortDirection);
	}

	/**
	 * get the total record count of logs returned by the paginated query
	 * @param startDate - the start date to return logs for (inclusive)
	 * @param endDate - the end date to return logs for (inclusive)
	 * @param page - the page to return
	 * @param pageSize - the size of each page
	 * @param status - the status type of the logs to return. can be '%' for all status types.
	 * @return - a list of logs
	 */
	public Long getIceFallLogsCount (LocalDateTime startDate, LocalDateTime endDate, Integer page, Integer pageSize, String status)
	{
		return iceFallLogDao.getLogsPaginatedCount(startDate, endDate, page, pageSize, status);
	}

	/**
	 * submit eform to ice fall
	 * @param provider - the provider doing the submission
	 * @param demo - the demographic to whom this eform pertains
	 * @param pdfData - the pdf to send to iceFall (base64 encoded).
	 * @param prescriptionInformation - the prescription information to be submitted to icefall.
	 * @param pageCount - the number of pages in the prescription pdf.
	 */
	public void sendIceFallForm(Provider provider, Demographic demo, String pdfData, IceFallSendFormTo1 prescriptionInformation, Integer pageCount)
	{
		//login to api
		iceFallRESTService.authenticate();

		//get doctor id
		Integer iceFallDocId = findDoctorId(provider, iceFallRESTService.getDoctorList());

		//get customer id
		Integer canopyCustomerId = null;
		try
		{
			canopyCustomerId = getDemoCanopyInfo(demo);
		}
		catch(IceFallCustomerLookupException e)
		{// remote customer does not exist create one.
			canopyCustomerId = createIceFallCustomerForDemographic(demo);
		}

		sendPrescriptionToIceFall(iceFallDocId, canopyCustomerId, prescriptionInformation, pdfData, pageCount);
	}

	protected void sendPrescriptionToIceFall(Integer iceFallDocId, Integer canopyCustomerId, IceFallSendFormTo1 prescriptionInformation, String pdfData, Integer pageCount)
	{
		IceFallCreatePrescriptionTo1 iceFallCreatePrescriptionTo1 = new IceFallCreatePrescriptionTo1();

		// configure prescription submission to ice fall.
		iceFallCreatePrescriptionTo1.setCustomerId(canopyCustomerId);
		iceFallCreatePrescriptionTo1.setDosage(prescriptionInformation.getDosage());
		iceFallCreatePrescriptionTo1.setRegistrationExpiry(prescriptionInformation.getExpiryDate());
		iceFallCreatePrescriptionTo1.setType(prescriptionInformation.getType());
		iceFallCreatePrescriptionTo1.setThcLimit(prescriptionInformation.getThcLimit());
		iceFallCreatePrescriptionTo1.setDiagnosis(prescriptionInformation.getDiagnosis());
		iceFallCreatePrescriptionTo1.setClinicId(1);
		iceFallCreatePrescriptionTo1.setPages(pageCount);
		iceFallCreatePrescriptionTo1.setDocumentData(pdfData);
		iceFallCreatePrescriptionTo1.setDoctorId(iceFallDocId);

		try
		{
			IceFallCreatePrescriptionResponseTo1 responseTo1 = iceFallRESTService.sendPrescription(iceFallCreatePrescriptionTo1);
		}
		catch(IceFallRESTException e)
		{
			throw new IceFallPrescriptionException("Failed to create ice fall prescription", e);
		}
	}

	protected Integer getDemoCanopyInfo(Demographic demo)
	{
		DemographicExt demoExt = demographicExtDao.getDemographicExt(demo.getId(), CANOPY_CUSTOMER_ID_KEY);
		if (demoExt != null && !demoExt.getValue().isEmpty())
		{
			return iceFallRESTService.getCustomerInformation(Integer.parseInt(demoExt.getValue())).getCustomerId();
		}

		throw new IceFallCustomerLookupException("No Canopy Id for demographic [" + demo.getDemographicId() + "]",
						IceFallException.USER_ERROR_MESSAGE.NO_CUST_ID_OR_EMAIL);
	}

	protected Integer createIceFallCustomerForDemographic(Demographic demo)
	{
		if (demo.getEmail() != null && !demo.getEmail().isEmpty())
		{
			IceFallCreateCustomerTo1 iceFallCreateCustomerTo1 = new IceFallCreateCustomerTo1();

			iceFallCreateCustomerTo1.setFirstName(demo.getFirstName());
			iceFallCreateCustomerTo1.setLastName(demo.getLastName());
			iceFallCreateCustomerTo1.setEmail(demo.getEmail());
			iceFallCreateCustomerTo1.setDateOfBirth(demo.getDateOfBirth());
			iceFallCreateCustomerTo1.setGender(demo.getSex());
			iceFallCreateCustomerTo1.setPhone(demo.getPhone());

			Integer customerId = null;
			try
			{
				customerId = iceFallRESTService.createIceFallCustomer(iceFallCreateCustomerTo1).getCustomerId();
			}
			catch(IceFallEmailExistsException e)
			{
				throw new IceFallCustomerLookupException("Customer email for demographic [" + demo.getId() + "] already exists in icefall system",
								IceFallException.USER_ERROR_MESSAGE.CUST_EMAIL_ALREADY_EXISTS);
			}

			if (customerId != null)
			{
				demographicExtDao.saveDemographicExt(demo.getId(), CANOPY_CUSTOMER_ID_KEY, customerId.toString());
				return customerId;
			}
			else
			{
				throw new IceFallCustomerLookupException("Customer Id not returned by api when creating customer for demographic, [" + demo.getId() + "]",
								IceFallException.USER_ERROR_MESSAGE.USER_CREATION_ERROR);
			}
		}
		else
		{
			throw new IceFallCustomerLookupException("Cannot create new icefall customer for demographic [" + demo.getId() + "]. email is null or blank",
							IceFallException.USER_ERROR_MESSAGE.NO_CUST_ID_OR_EMAIL);
		}
	}


	/**
	 * locate the ice fall doctor id of the current provider
	 * @param doctorListTo1 - the list of ids to search
	 * @return - the id of the currently logged in provider
	 */
	protected Integer findDoctorId(Provider provider, IceFallDoctorListTo1 doctorListTo1)
	{
		IceFallCredentials credentials = iceFallCredentialsDao.getCredentials();
		for (IceFallDoctorTo1 doc : doctorListTo1.getResults())
		{
			if (
							doc.getFirstName().trim().equals(provider.getFirstName().trim()) &&
											doc.getLastName().trim().equals(provider.getLastName().trim()) &&
											doc.getEmail().trim().equals(credentials.getEmail())
			)
			{
				return doc.getId();
			}
		}

		throw new IceFallNoSuchDoctorException("Provider [" + provider.getProviderNo() + "] Not in ice fall doctors list", IceFallException.USER_ERROR_MESSAGE.DOCTOR_LOOKUP_ERROR);
	}

	/**
	 * get the eform as a base64 encoded pdf
	 * @param provider - the provider to print the eform under
	 * @param demographic - the demographic to whom the eform pertains
	 * @param formId - the eforms template id
	 * @param eformValues - the eform values
	 * @param httpSchema - the http schema to use
	 * @param context - the context path to use
	 * @return - a base64 encoded PDF
	 */
	public String getEformPDFDateForPrescriptionSend(Provider provider, Demographic demographic, Integer formId, Map<String, String> eformValues, String httpSchema, String context, boolean isInstance)
	{
		EFormData eFormData = saveEFormForPrint(provider, demographic, formId, eformValues, isInstance);
		return printToPDF(eFormData.getId(), provider.getProviderNo(), httpSchema, context);
	}

	/**
	 * save a new or update an existing eform. required before the form can be printed
	 * @param provider - the provider to print the eform under
	 * @param demographic - the demographic to whom the eform pertains
	 * @param formId - the eforms template id
	 * @param eformValues - the eform values
	 * @param existingEForm - is this a new eform or not
	 * @return eform data object for saved eform
	 */
	public EFormData saveEFormForPrint(Provider provider, Demographic demographic, Integer formId, Map<String, String> eformValues, boolean existingEForm)
	{
		Map<String,String> formOpenerMap = new HashMap<>();
		String subject = "Submitted to Ice Fall";
		String eformLink = "";
		if (existingEForm)
		{
			return eFormDataService.saveExistingEForm(formId, demographic.getDemographicId(), Integer.parseInt(provider.getProviderNo()), subject, formOpenerMap, eformValues, eformLink);
		}
		else
		{
			return eFormDataService.saveNewEForm(formId, demographic.getDemographicId(), Integer.parseInt(provider.getProviderNo()), subject, formOpenerMap, eformValues, eformLink);
		}
	}

	/**
	 * print an eform to pdf
	 * @param fdid - the instance id of the eform
	 * @param providerNo - the provider number that the form should be printed under
	 * @param httpSchema - the httpschema to use
	 * @param context - the context path of the oscar server.
	 * @return - base64 String of the eform data.
	 */
	public String printToPDF(Integer fdid, String providerNo, String httpSchema, String context)
	{
		return printToPDF(fdid, providerNo, httpSchema, context, new AtomicInteger(0));
	}

	/**
	 * print an eform to pdf
	 * @param fdid - the instance id of the eform
	 * @param providerNo - the provider number that the form should be printed under
	 * @param httpSchema - the httpschema to use
	 * @param context - the context path of the oscar server.
	 * @param pageCount - an output variable that indicates the number of pages in the pdf.
	 * @return - the base64 encoded string of the eform data.
	 */
	public String printToPDF(Integer fdid, String providerNo, String httpSchema, String context, AtomicInteger pageCount)
	{
		String localUrl = WKHtmlToPdfUtils.getEformRequestUrl(providerNo,
						"", httpSchema, context);

		GenericFile tmpFile = null;
		PDDocument pdDocument = null;
		try
		{
			tmpFile = FileFactory.createTempFile("pdf");

			//convert to pdf
			WKHtmlToPdfUtils.convertToPdf(localUrl + fdid, tmpFile.getFileObject());

			//get page count
			pdDocument = PDDocument.load(tmpFile.getFileObject());
			pageCount.set(pdDocument.getNumberOfPages());

			// encode, and return
			return PDF_DOCUMENT_HEADER + (new String(Base64.getEncoder().encode(IOUtils.toByteArray(tmpFile.asFileInputStream()))));
		}
		catch (IOException | HtmlToPdfConversionException e)
		{
			throw new IceFallPdfGenerationException("Failed to convert eform [" + fdid + "] to PDF", e);
		}
		finally
		{
			if (pdDocument != null)
			{
				try
				{
					pdDocument.close();
				}
				catch(IOException e)
				{
					MiscUtils.getLogger().error("Failed to close pdDocument file on pdf file [" + tmpFile.getName() + "]");
				}
			}

			if (tmpFile != null)
			{
				try
				{
					tmpFile.deleteFile();
				}
				catch (IOException e)
				{
					MiscUtils.getLogger().error("Failed to delete temp file [" + tmpFile.getName() + "] It may have been leaked!");
				}
			}
		}
	}


}
