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
package org.oscarehr.ws.rest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.dao.BORNPathwayMappingDao;
import org.oscarehr.common.dao.ConsultationServiceDao;
import org.oscarehr.common.model.BORNPathwayMapping;
import org.oscarehr.common.model.ConsultResponseDoc;
import org.oscarehr.common.model.ConsultationRequest;
import org.oscarehr.common.model.ConsultationResponse;
import org.oscarehr.common.model.ConsultationServices;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.consultations.ConsultationRequestSearchFilter;
import org.oscarehr.consultations.ConsultationResponseSearchFilter;
import org.oscarehr.consultations.model.ConsultDocs;
import org.oscarehr.consultations.service.ConsultationAttachmentService;
import org.oscarehr.consultations.service.ConsultationService;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.fax.dao.FaxAccountDao;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.managers.ConsultationManager;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.rest.conversion.ConsultationRequestToDomainConverter;
import org.oscarehr.ws.rest.conversion.ConsultationRequestToTransferConverter;
import org.oscarehr.ws.rest.conversion.ConsultationResponseConverter;
import org.oscarehr.ws.rest.conversion.ConsultationServicesToTransferConverter;
import org.oscarehr.ws.rest.conversion.DemographicConverter;
import org.oscarehr.ws.rest.conversion.ProfessionalSpecialistToTransferConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.oscarehr.ws.rest.to.ReferralResponse;
import org.oscarehr.ws.rest.to.model.ConsultationAttachmentTo1;
import org.oscarehr.ws.rest.to.model.ConsultationRequestSearchResult;
import org.oscarehr.ws.rest.to.model.ConsultationRequestTo1;
import org.oscarehr.ws.rest.to.model.ConsultationResponseSearchResult;
import org.oscarehr.ws.rest.to.model.ConsultationResponseTo1;
import org.oscarehr.ws.rest.to.model.ConsultationServiceTo1;
import org.oscarehr.ws.rest.to.model.FaxConfigTo1;
import org.oscarehr.ws.rest.to.model.LetterheadTo1;
import org.oscarehr.ws.rest.to.model.ProfessionalSpecialistTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import oscar.eform.EFormUtil;
import oscar.oscarDemographic.data.RxInformation;
import oscar.oscarLab.ca.all.Hl7textResultsData;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.util.ConversionUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Path("/consults")
@Component("consultationWebService")
public class ConsultationWebService extends AbstractServiceImpl {

	private static Logger logger = Logger.getLogger(ConsultationWebService.class);

	@Autowired
	private ConsultationManager consultationManager;
	
	@Autowired
	private DemographicManager demographicManager;
	
	@Autowired
	private ProviderDao providerDao;
	
	@Autowired
	private FaxAccountDao faxAccountDao;
	
	@Autowired
	private ConsultationServiceDao consultationServiceDao;

	@Autowired
	private ConsultationService consultationService;

	@Autowired
	private ConsultationAttachmentService consultationAttachmentService;

	@Autowired
	private ConsultationRequestToTransferConverter consultationRequestToTransferConverter;

	@Autowired
	private ConsultationRequestToDomainConverter consultationRequestToDomainConverter;


	@Autowired
	private ProfessionalSpecialistToTransferConverter specialistToTransferConverter;

	private ConsultationResponseConverter responseConverter = new ConsultationResponseConverter();

	@Autowired
	private ConsultationServicesToTransferConverter servicesToTransferConverter;

	private DemographicConverter demographicConverter = new DemographicConverter();
	
	
	/********************************
	 * Consultation Request methods *
	 ********************************/
	@GET
	@Path("/searchRequests")
	@Produces(MediaType.APPLICATION_JSON)
	public RestSearchResponse<ConsultationRequestSearchResult> searchRequests(
			@QueryParam("demographicNo") Integer demographicNo,
			@QueryParam("mrpNo") Integer mrpNo,
			@QueryParam("status") final List<String> statusList,
			@QueryParam("page") @DefaultValue("1") Integer page,
			@QueryParam("perPage") @DefaultValue("10") Integer perPage,
			@QueryParam("referralStartDate") String referralStartDateString,
			@QueryParam("referralEndDate") String referralEndDate,
			@QueryParam("appointmentStartDate") String appointmentStartDate,
			@QueryParam("appointmentEndDate") String appointmentEndDate,
			@QueryParam("team") String team,
			@QueryParam("sortColumn") @DefaultValue("ReferralDate") String sortColumn,
			@QueryParam("sortDirection") @DefaultValue("desc") String sortDirection
			)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONSULTATION_READ);

		ConsultationRequestSearchFilter filter = new ConsultationRequestSearchFilter();
		List<ConsultationRequestSearchResult> resultList;

		if(page < 1) page = 1;
		int offset = perPage * (page-1);
		int resultTotal;

		filter.setDemographicNo(demographicNo);
		filter.setMrpNo(mrpNo);
		filter.setStatus(statusList);
		filter.setStartIndex(offset);
		filter.setNumToReturn(perPage);
		filter.setReferralStartDate(ConversionUtils.toNullableLegacyDate(ConversionUtils.toNullableZonedLocalDate(referralStartDateString)));
		filter.setReferralEndDate(ConversionUtils.toNullableLegacyDate(ConversionUtils.toNullableZonedLocalDate(referralEndDate)));
		filter.setAppointmentStartDate(ConversionUtils.toNullableLegacyDate(ConversionUtils.toNullableZonedLocalDate(appointmentStartDate)));
		filter.setAppointmentEndDate(ConversionUtils.toNullableLegacyDate(ConversionUtils.toNullableZonedLocalDate(appointmentEndDate)));
		filter.setTeam(team);

		filter.setSortMode(ConsultationRequestSearchFilter.SORTMODE.valueOf(sortColumn));
		filter.setSortDir(ConsultationRequestSearchFilter.SORTDIR.valueOf(sortDirection));

		resultTotal = consultationManager.getConsultationCount(filter);
		resultList = consultationManager.search(getLoggedInInfo(), filter);
		return RestSearchResponse.successResponse(resultList, page, perPage, resultTotal);
	}

	@GET
	@Path("/getTotalRequests")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Integer> getTotalRequests(
			@QueryParam("demographicNo") Integer demographicNo,
			@QueryParam("mrpNo") Integer mrpNo,
			@QueryParam("status") final List<String> statusList,
			@QueryParam("page") @DefaultValue("1") Integer page,
			@QueryParam("perPage") @DefaultValue("10") Integer perPage,
			@QueryParam("referralStartDate") String referralStartDateString,
			@QueryParam("referralEndDate") String referralEndDate,
			@QueryParam("appointmentStartDate") String appointmentStartDate,
			@QueryParam("appointmentEndDate") String appointmentEndDate,
			@QueryParam("team") String team,
			@QueryParam("sortColumn") @DefaultValue("ReferralDate") String sortColumn,
			@QueryParam("sortDirection") @DefaultValue("desc") String sortDirection,
			@QueryParam("invertStatus") boolean invertStatus
			)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONSULTATION_READ);
		ConsultationRequestSearchFilter filter = new ConsultationRequestSearchFilter();

		if (page < 1)
		{
			page = 1;
		}

		int offset = perPage * (page - 1);
		int resultTotal;

		filter.setDemographicNo(demographicNo);
		filter.setMrpNo(mrpNo);
		filter.setStatus(statusList);
		filter.setStartIndex(offset);
		filter.setNumToReturn(perPage);
		filter.setReferralStartDate(ConversionUtils.toNullableLegacyDate(ConversionUtils.toNullableZonedLocalDate(referralStartDateString)));
		filter.setReferralEndDate(ConversionUtils.toNullableLegacyDate(ConversionUtils.toNullableZonedLocalDate(referralEndDate)));
		filter.setAppointmentStartDate(ConversionUtils.toNullableLegacyDate(ConversionUtils.toNullableZonedLocalDate(appointmentStartDate)));
		filter.setAppointmentEndDate(ConversionUtils.toNullableLegacyDate(ConversionUtils.toNullableZonedLocalDate(appointmentEndDate)));
		filter.setTeam(team);
		filter.setInvertStatus(invertStatus);

		filter.setSortMode(ConsultationRequestSearchFilter.SORTMODE.valueOf(sortColumn));
		filter.setSortDir(ConsultationRequestSearchFilter.SORTDIR.valueOf(sortDirection));

		resultTotal = consultationManager.getConsultationCount(filter);

		return RestResponse.successResponse(resultTotal);
	}

	@GET
	@Path("/getRequest/{requestId}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<ConsultationRequestTo1> getRequest(@PathParam("requestId") Integer requestId)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(),
				Permission.CONSULTATION_READ,
				Permission.DOCUMENT_READ,
				Permission.EFORM_READ,
				Permission.LAB_READ,
				Permission.HRM_READ);

		ConsultationRequestTo1 request;
		try
		{

			ConsultationRequest consult = consultationManager.getRequest(getLoggedInInfo(), requestId);
			if(consult == null)
			{
				return RestResponse.errorResponse("No Consult found with id " + requestId);
			}
			request = consultationRequestToTransferConverter.convert(consult);
			request.setAttachments(getRequestAttachments(requestId, request.getDemographicId(), ConsultationAttachmentTo1.ATTACHED).getBody());

			request.setFaxList(getFaxList());
			List<ConsultationServices> consultationServices = consultationManager.getConsultationServices();
			List<ConsultationServiceTo1> serviceTransfers = new ArrayList<>();
			for (ConsultationServices consultationService : consultationServices)
			{
				serviceTransfers.add(servicesToTransferConverter.convert(consultationService));
			}
			request.setServiceList(serviceTransfers);
			request.setSendToList(providerDao.getActiveTeams());
			request.setProviderNo(getLoggedInInfo().getLoggedInProviderNo());
		}
		catch(Exception e)
		{
			logger.error("Unexpected Error", e);
			return RestResponse.errorResponse("Unexpected Error");
		}
		return RestResponse.successResponse(request);
	}

	@GET
	@Path("/getNewRequest")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<ConsultationRequestTo1> getNewRequest(@QueryParam("demographicNo") Integer demographicId)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONSULTATION_READ);
		ConsultationRequestTo1 request;
		try
		{
			if(demographicId == null || demographicId <= 0)
			{
				return RestResponse.errorResponse("Invalid demographicNo: " + demographicId);
			}
			String loggedInProviderNo = getLoggedInInfo().getLoggedInProviderNo();

			request = new ConsultationRequestTo1();
			request.setDemographicId(demographicId);

			RxInformation rx = new RxInformation();
			String info = rx.getAllergies(getLoggedInInfo(), demographicId.toString());
			if(StringUtils.isNotBlank(info)) request.setAllergies(info);
			info = rx.getCurrentMedication(demographicId.toString());
			if(StringUtils.isNotBlank(info)) request.setCurrentMeds(info);

			request.setFaxList(getFaxList());
			List<ConsultationServices> consultationServices = consultationManager.getConsultationServices();
			List<ConsultationServiceTo1> serviceTransfers = servicesToTransferConverter.convert(consultationServices);
			request.setServiceList(serviceTransfers);
			request.setSendToList(providerDao.getActiveTeams());
			request.setProviderNo(loggedInProviderNo);
			request.setPatientWillBook(true);

			LetterheadTo1 defaultLetterhead = consultationService.getDefaultLetterhead(loggedInProviderNo, demographicId);
			request.setLetterheadName(defaultLetterhead.getId());
			request.setLetterheadAddress(defaultLetterhead.getAddress());
			request.setLetterheadPhone(defaultLetterhead.getPhone());
		}
		catch(Exception e)
		{
			logger.error("Unexpected Error", e);
			return RestResponse.errorResponse("Unexpected Error");
		}
		return RestResponse.successResponse(request);
	}

	@GET
	@Path("/getRequestAttachments/{requestId}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<ConsultationAttachmentTo1>> getRequestAttachments(@PathParam("requestId") Integer requestId,
	                                                                           @QueryParam("demographicId") Integer demographicId,
	                                                                           @QueryParam("attached") boolean attached)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(),
				Permission.CONSULTATION_READ,
				Permission.DOCUMENT_READ,
				Permission.EFORM_READ,
				Permission.LAB_READ,
				Permission.HRM_READ);

		List<EDoc> edocs;
		List<EFormData> eforms;
		List<LabResultData> labs;
		List<HrmDocument> hrm;
		if(attached)
		{
			edocs = consultationAttachmentService.getAttachedDocuments(getLoggedInInfo(), demographicId, requestId);
			eforms = consultationAttachmentService.getAttachedEForms(demographicId, requestId);
			labs = consultationAttachmentService.getAttachedLabs(getLoggedInInfo(), demographicId, requestId);
			hrm = consultationAttachmentService.getAttachedHRMList(demographicId, requestId);
		}
		else
		{
			edocs = consultationAttachmentService.getUnattachedDocuments(getLoggedInInfo(), demographicId, requestId);
			eforms = consultationAttachmentService.getUnattachedEForms(demographicId, requestId);
			labs = consultationAttachmentService.getUnattachedLabs(getLoggedInInfo(), demographicId, requestId);
			hrm = consultationAttachmentService.getUnattachedHRMList(demographicId, requestId);
		}

		List<ConsultationAttachmentTo1> attachments = new ArrayList<>();

		getDocuments(edocs, attached, attachments);
		getEformsForRequest(eforms, attached, attachments);
		getLabs(labs, demographicId, attached, attachments);
		getHrm(hrm, demographicId, attached, attachments);

		return RestResponse.successResponse(attachments);
	}

	@POST
	@Path("/saveRequest")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<ConsultationRequestTo1> saveRequest(ConsultationRequestTo1 data)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(),
				Permission.CONSULTATION_CREATE,
				Permission.DOCUMENT_READ,
				Permission.EFORM_READ,
				Permission.LAB_READ,
				Permission.HRM_READ);

		ConsultationRequest request = consultationRequestToDomainConverter.convert(data);
		consultationManager.saveConsultationRequest(getLoggedInInfo(), request);
		if(data.getId() == null)
		{
			data.setId(request.getId());
		}

		//save attachments
		saveRequestAttachments(data);
		return RestResponse.successResponse(data);
	}
	
	@GET
	@Path("/eSendRequest/{requestId}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<String> eSendRequest(@PathParam("requestId") Integer requestId)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONSULTATION_READ);
		try
		{
			consultationManager.doHl7Send(getLoggedInInfo(), requestId);
			return RestResponse.successResponse("Referral Electronically Sent");
		}
		catch(Exception e)
		{
			logger.error("Error contacting remote server.", e);
			return RestResponse.errorResponse("There was an error sending electronically, please try again or manually process the referral.");
		}
	}
	
	
	/********************************
	 * Consultation Response methods *
	 ********************************/
	@GET
	@Path("/searchResponses")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestSearchResponse<ConsultationResponseSearchResult> searchResponses(
			@QueryParam("demographicNo") Integer demographicNo,
			@QueryParam("mrpNo") Integer mrpNo,
			@QueryParam("status") Integer status,
			@QueryParam("page") @DefaultValue("1") Integer page,
			@QueryParam("perPage") @DefaultValue("10") Integer perPage,
			@QueryParam("referralStartDate") String referralStartDateString,
			@QueryParam("referralEndDate") String referralEndDate,
			@QueryParam("appointmentStartDate") String appointmentStartDate,
			@QueryParam("appointmentEndDate") String appointmentEndDate,
			@QueryParam("team") String team,
			@QueryParam("sortColumn") @DefaultValue("ReferralDate") String sortColumn,
			@QueryParam("sortDirection") @DefaultValue("desc") String sortDirection
	) {
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONSULTATION_READ);

		ConsultationResponseSearchFilter filter = new ConsultationResponseSearchFilter();
		List<ConsultationResponseSearchResult> resultList;

		if(page < 1) page = 1;
		int offset = perPage * (page-1);
		int resultTotal;

		try {
			filter.setDemographicNo(demographicNo);
			filter.setMrpNo(mrpNo);
			filter.setStatus(status);
			filter.setStartIndex(offset);
			filter.setNumToReturn(perPage);
			filter.setReferralStartDate(ConversionUtils.toNullableLegacyDate(ConversionUtils.toNullableZonedLocalDate(referralStartDateString)));
			filter.setReferralEndDate(ConversionUtils.toNullableLegacyDate(ConversionUtils.toNullableZonedLocalDate(referralEndDate)));
			filter.setAppointmentStartDate(ConversionUtils.toNullableLegacyDate(ConversionUtils.toNullableZonedLocalDate(appointmentStartDate)));
			filter.setAppointmentEndDate(ConversionUtils.toNullableLegacyDate(ConversionUtils.toNullableZonedLocalDate(appointmentEndDate)));
			filter.setTeam(team);

			filter.setSortMode(ConsultationResponseSearchFilter.SORTMODE.valueOf(sortColumn));
			filter.setSortDir(ConsultationResponseSearchFilter.SORTDIR.valueOf(sortDirection));

			resultTotal = consultationManager.getConsultationCount(filter);
			resultList = consultationManager.search(getLoggedInInfo(), filter);
		}
		catch(DateTimeParseException e) {
			logger.error("Unparseable Date", e);
			return RestSearchResponse.errorResponse("Unparseable Date");
		}
		catch(Exception e) {
			logger.error("Search Error", e);
			return RestSearchResponse.errorResponse("Search Error");
		}

		return RestSearchResponse.successResponse(resultList, page, perPage, resultTotal);
	}
	
	@GET
	@Path("/getResponse")
	@Produces(MediaType.APPLICATION_JSON)
	public ConsultationResponseTo1 getResponse(@QueryParam("responseId") Integer responseId, @QueryParam("demographicNo") Integer demographicNo)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(),
				Permission.CONSULTATION_READ,
				Permission.DOCUMENT_READ,
				Permission.EFORM_READ,
				Permission.LAB_READ);

		ConsultationResponseTo1 response = new ConsultationResponseTo1();
		
		if (responseId>0) {
			ConsultationResponse responseD = consultationManager.getResponse(getLoggedInInfo(), responseId);
			response = responseConverter.getAsTransferObject(getLoggedInInfo(), responseD);
			
			demographicNo = responseD.getDemographicNo();
			
			ProfessionalSpecialist referringDoctorD = consultationManager.getProfessionalSpecialist(responseD.getReferringDocId());
			response.setReferringDoctor(specialistToTransferConverter.convert(referringDoctorD));
			
			response.setAttachments(getResponseAttachments(responseId, demographicNo, ConsultationAttachmentTo1.ATTACHED));
		} else {
			String loggedInProviderNo = getLoggedInInfo().getLoggedInProviderNo();
			response.setProviderNo(loggedInProviderNo);
			RxInformation rx = new RxInformation();
			String info = rx.getAllergies(getLoggedInInfo(), demographicNo.toString());
			if (StringUtils.isNotBlank(info)) response.setAllergies(info);
			info = rx.getCurrentMedication(demographicNo.toString());
			if (StringUtils.isNotBlank(info)) response.setCurrentMeds(info);


			LetterheadTo1 defaultLetterhead = consultationService.getDefaultLetterhead(loggedInProviderNo, demographicNo);
			response.setLetterheadName(defaultLetterhead.getId());
			response.setLetterheadAddress(defaultLetterhead.getAddress());
			response.setLetterheadPhone(defaultLetterhead.getPhone());
		}

		Demographic demographicD = demographicManager.getDemographicWithExt(getLoggedInInfo(), demographicNo);
		response.setDemographic(demographicConverter.getAsTransferObject(getLoggedInInfo(), demographicD));
		response.setReferringDoctorList(getReferringDoctorList());
		response.setFaxList(getFaxList());
		response.setSendToList(providerDao.getActiveTeams());
		
		return response;
	}
	
	@GET
	@Path("/getResponseAttachments")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ConsultationAttachmentTo1> getResponseAttachments(@QueryParam("responseId") Integer responseId, @QueryParam("demographicNo") Integer demographicNo, @QueryParam("attached") boolean attached)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(),
				Permission.CONSULTATION_READ,
				Permission.DOCUMENT_READ,
				Permission.EFORM_READ,
				Permission.LAB_READ);

		List<ConsultationAttachmentTo1> attachments = new ArrayList<ConsultationAttachmentTo1>();
		String demographicNoStr = demographicNo.toString();
		
		List<EDoc> edocList = EDocUtil.listResponseDocs(getLoggedInInfo(), demographicNoStr, responseId.toString(), attached);
		getDocuments(edocList, attached, attachments);
		
		List<EFormData> eformList = EFormUtil.listPatientEFormsShowLatestOnly(demographicNoStr);
		getEformsForResponse(eformList, attached, attachments, responseId);
		
		List<LabResultData> labs = new CommonLabResultData().populateLabResultsDataConsultResponse(getLoggedInInfo(), demographicNoStr, responseId.toString(), attached);
		getLabs(labs, demographicNo, attached, attachments);
		
		return attachments;
	}

	@POST
	@Path("/saveResponse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<ConsultationResponseTo1> saveRequest(ConsultationResponseTo1 data)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(),
				Permission.CONSULTATION_CREATE,
				Permission.DOCUMENT_READ,
				Permission.EFORM_READ,
				Permission.LAB_READ);
		try
		{
			ConsultationResponse response;

			if (data.getId()==null) { //new consultation response
				response = responseConverter.getAsDomainObject(getLoggedInInfo(), data);
			} else {
				response = responseConverter.getAsDomainObject(getLoggedInInfo(), data, consultationManager.getResponse(getLoggedInInfo(), data.getId()));
			}
			consultationManager.saveConsultationResponse(getLoggedInInfo(), response);
			if (data.getId()==null) data.setId(response.getId());

			//save attachments
			saveResponseAttachments(data);

			return RestResponse.successResponse(data);
		}
		catch(Exception e) {
			logger.error("Error saving Consult Request", e);
			return RestResponse.errorResponse("Failed to save Consult");
		}
	}

	@GET
	@Path("/getReferralPathwaysByService")
	@Produces(MediaType.APPLICATION_JSON)
	public ReferralResponse getReferralPathwaysByService(@QueryParam("serviceName") String serviceName)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONSULTATION_READ);

		ReferralResponse response = new ReferralResponse();
		
		//check for a mapping, or else just use the BORN service name.
		BORNPathwayMappingDao bornPathwayMappingDao = SpringUtils.getBean(BORNPathwayMappingDao.class);
		List<BORNPathwayMapping> mappings = bornPathwayMappingDao.findByBornPathway(serviceName);
		List<ProfessionalSpecialist> specs = new ArrayList<ProfessionalSpecialist>();
		
		if(mappings.isEmpty()) {
			specs = consultationManager.findByService(getLoggedInInfo(), serviceName);
			ConsultationServices cs = consultationServiceDao.findByDescription(serviceName);
			if(cs != null) {
				response.getServices().add(servicesToTransferConverter.convert(cs));
			}
		} else {
			for(BORNPathwayMapping mapping:mappings) {
				specs.addAll(consultationManager.findByServiceId(getLoggedInInfo(), mapping.getServiceId()));
				ConsultationServices cs = consultationServiceDao.find(mapping.getServiceId());
				if(cs != null) {
					response.getServices().add(servicesToTransferConverter.convert(cs));
				}
			}
		}

		List<ProfessionalSpecialistTo1> specialistTo1s = specialistToTransferConverter.convert(specs);
		response.setSpecialists(specialistTo1s);

		return response;
	}

	@GET
	@Path("/getProfessionalSpecialist")
	@Produces(MediaType.APPLICATION_JSON)
	public ProfessionalSpecialistTo1 getProfessionalSpecialist(@QueryParam("specId") Integer specId)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONSULTATION_READ);

		ProfessionalSpecialist specialist = consultationManager.getProfessionalSpecialist(specId);
		return specialistToTransferConverter.convert(specialist);
	}

	@GET
	@Path("/getLetterheadList")
	@Produces(MediaType.APPLICATION_JSON)
	public RestSearchResponse<LetterheadTo1> getLetterheadList()
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONSULTATION_READ);

		List<LetterheadTo1> letterheadList = consultationService.getLetterheadList();
		return RestSearchResponse.successResponse(letterheadList, 1, letterheadList.size(), letterheadList.size());
	}

	/*******************
	 * private methods *
	 *******************/
	
	private List<FaxConfigTo1> getFaxList()
	{
		List<FaxConfigTo1> faxList = new ArrayList<FaxConfigTo1>();
		List<FaxAccount> faxAccountList = faxAccountDao.findAll(null, null);
		for (FaxAccount faxAccount : faxAccountList) {
			FaxConfigTo1 faxConfigTo1 = new FaxConfigTo1();
			faxList.add(faxConfigTo1);
			faxConfigTo1.setFaxUser(faxAccount.getLoginId());
			faxConfigTo1.setFaxNumber(faxAccount.getReplyFaxNumber());
		}
		return faxList;
	}
	
	private List<ProfessionalSpecialistTo1> getReferringDoctorList()
	{
		List<ProfessionalSpecialist> list = consultationManager.getReferringDoctorList();

		return specialistToTransferConverter.convert(list);
	}

	private void getDocuments(List<EDoc> edocs, boolean attached, List<ConsultationAttachmentTo1> attachments)
	{
		for(EDoc edoc : edocs)
		{
			String url = "dms/ManageDocument.do?method=display&doc_no=" + edoc.getDocId();
			attachments.add(new ConsultationAttachmentTo1(ConversionUtils.fromIntString(edoc.getDocId()), ConsultationAttachmentTo1.TYPE_DOC, attached, edoc.getDescription(), url));
		}
	}

	private void getLabs(List<LabResultData> labs, Integer demographicNo, boolean attached, List<ConsultationAttachmentTo1> attachments)
	{
		for(LabResultData lab : labs)
		{
			String displayName = lab.getDiscipline() + " " + lab.getDateTime();

			String url = null;
			if(lab.isMDS()) url = "oscarMDS/SegmentDisplay.jsp?demographicId=" + demographicNo + "&segmentID=" + lab.getSegmentID();
			else if(lab.isCML()) url = "lab/CA/ON/CMLDisplay.jsp?demographicId=" + demographicNo + "&segmentID=" + lab.getSegmentID();
			else if(lab.isHL7TEXT())
			{
				if(!Hl7textResultsData.getMatchingLabs(lab.segmentID).endsWith(lab.segmentID)) continue;
				url = "lab/CA/ALL/labDisplay.jsp?demographicId=" + demographicNo + "&segmentID=" + lab.getSegmentID();
			}
			else url = "lab/CA/BC/labDisplay.jsp?demographicId=" + demographicNo + "&segmentID=" + lab.getSegmentID();

			attachments.add(new ConsultationAttachmentTo1(ConversionUtils.fromIntString(lab.getLabPatientId()), ConsultationAttachmentTo1.TYPE_LAB, attached, displayName, url));
		}
	}

	private void getHrm(List<HrmDocument> hrmDocuments, Integer demographicNo, boolean attached, List<ConsultationAttachmentTo1> attachments)
	{
		for(HrmDocument hrmDocument : hrmDocuments)
		{
			String displayName = hrmDocument.getDescription();
			String url = "/hospitalReportManager/displayHRMReport.jsp?id=" + hrmDocument.getId();
			attachments.add(new ConsultationAttachmentTo1(hrmDocument.getId(), ConsultationAttachmentTo1.TYPE_HRM, attached, displayName, url));
		}
	}
	
	private void getEformsForRequest(List<EFormData> eforms, boolean attached, List<ConsultationAttachmentTo1> attachments)
	{
		for (EFormData eform : eforms)
		{
			String url = "eform/efmshowform_data.jsp?fdid="+eform.getId();
			String displayName = eform.getFormName()+" "+eform.getFormDate();
			attachments.add(new ConsultationAttachmentTo1(ConversionUtils.fromIntString(eform.getId()), ConsultationAttachmentTo1.TYPE_EFORM, attached, displayName, url));
		}
	}
	
	private void getEformsForResponse(List<EFormData> eforms, boolean attached, List<ConsultationAttachmentTo1> attachments, Integer consultId)
	{
		//TODO-legacy move this logic to a consultation response attachment service
		List<ConsultResponseDoc> docs = consultationManager.getConsultResponseDocs(getLoggedInInfo(), consultId);
		List<Integer> docNos = new ArrayList<Integer>();
		if (docs!=null) {
			for (ConsultResponseDoc doc : docs) {
				if (doc.getDocType().equals(ConsultResponseDoc.DOCTYPE_EFORM)) docNos.add(doc.getDocumentNo());
			}
		}
		for (EFormData eform : eforms)
		{
			boolean found = false;
			for (Integer docNo : docNos) {
				if (eform.getId().equals(docNo)) {
					found = true; break;
				}
			}
			if (attached==found) {
				//if attached is wanted and attached found		OR
				//if detached is wanted and attached not found
				String url = "eform/efmshowform_data.jsp?fdid="+eform.getId();
				String displayName = eform.getFormName()+" "+eform.getFormDate();
				attachments.add(new ConsultationAttachmentTo1(ConversionUtils.fromIntString(eform.getId()), ConsultationAttachmentTo1.TYPE_EFORM, attached, displayName, url));
			}
		}
	}
	
	private void saveRequestAttachments(ConsultationRequestTo1 request)
	{
		List<ConsultationAttachmentTo1> newAttachments = request.getAttachments();

		List<Integer> eFormIdList = new ArrayList<>();
		List<Integer> documentIdList = new ArrayList<>();
		List<Integer> labIdList = new ArrayList<>();
		List<Integer> hrmIdList = new ArrayList<>();

		for(ConsultationAttachmentTo1 attachment : newAttachments)
		{
			switch(attachment.getDocumentType())
			{
				case ConsultDocs.DOCTYPE_EFORM: eFormIdList.add(attachment.getDocumentNo()); break;
				case ConsultDocs.DOCTYPE_DOC: documentIdList.add(attachment.getDocumentNo()); break;
				case ConsultDocs.DOCTYPE_LAB: labIdList.add(attachment.getDocumentNo()); break;
				case ConsultDocs.DOCTYPE_HRM: hrmIdList.add(attachment.getDocumentNo()); break;
				default: logger.error("Invalid attachment doctype: " + attachment.getDocumentType()); break;
			}
		}

		consultationAttachmentService.setAttachedEForms(request.getId(), getLoggedInInfo().getLoggedInProviderNo(), eFormIdList);
		consultationAttachmentService.setAttachedDocuments(request.getId(), getLoggedInInfo().getLoggedInProviderNo(), documentIdList);
		consultationAttachmentService.setAttachedLabs(request.getId(), getLoggedInInfo().getLoggedInProviderNo(), labIdList);
		consultationAttachmentService.setAttachedHRM(request.getId(), getLoggedInInfo().getLoggedInProviderNo(), hrmIdList);
	}
	
	private void saveResponseAttachments(ConsultationResponseTo1 response) {
		List<ConsultationAttachmentTo1> newAttachments = response.getAttachments();
		List<ConsultResponseDoc> currentDocs = consultationManager.getConsultResponseDocs(getLoggedInInfo(), response.getId());
		if (newAttachments==null || currentDocs==null) return;
		
		//first assume all current docs detached (set delete)
		for (ConsultResponseDoc doc : currentDocs) {
			doc.setDeleted(ConsultResponseDoc.DELETED);
		}
		
		//compare current & new, remove from current list the unchanged ones - no need to update them
		for (ConsultationAttachmentTo1 newAtth : newAttachments) {
			boolean isNew = true;
			for (ConsultResponseDoc doc : currentDocs) {
				if (doc.getDocType().equals(newAtth.getDocumentType()) && doc.getDocumentNo()==newAtth.getDocumentNo()) {
					currentDocs.remove(doc);
					isNew = false;
					break;
				}
			}
			if (isNew) { //save the new attachment
				consultationManager.saveConsultResponseDoc(getLoggedInInfo(), new ConsultResponseDoc(response.getId(), newAtth.getDocumentNo(), newAtth.getDocumentType(), getLoggedInInfo().getLoggedInProviderNo()));
			}
		}
		
		//update what remains in current docs, they are detached (set delete)
		for (ConsultResponseDoc doc : currentDocs) {
			consultationManager.saveConsultResponseDoc(getLoggedInInfo(), doc);
		}
	}
}
