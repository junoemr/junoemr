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

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.caisi_integrator.CaisiIntegratorManager;
import org.oscarehr.caisi_integrator.ws.DemographicTransfer;
import org.oscarehr.caisi_integrator.ws.MatchingDemographicParameters;
import org.oscarehr.caisi_integrator.ws.MatchingDemographicTransferScore;
import org.oscarehr.common.dao.DemographicSetsDao;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.XMLFile;
import org.oscarehr.common.io.ZIPFile;
import org.oscarehr.common.io.ZipFileFactory;
import org.oscarehr.common.model.SecObjectName;
import org.oscarehr.dataMigration.pref.ExportPreferences;
import org.oscarehr.dataMigration.service.DataMigrationService;
import org.oscarehr.dataMigration.service.ImporterExporterFactory;
import org.oscarehr.dataMigration.service.PatientExportService;
import org.oscarehr.dataMigration.service.PatientImportWrapperService;
import org.oscarehr.dataMigration.service.context.PatientExportContextService;
import org.oscarehr.dataMigration.service.context.PatientImportContextService;
import org.oscarehr.dataMigration.transfer.ExportTransferOutbound;
import org.oscarehr.dataMigration.transfer.ImportTransferOutbound;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.search.DemographicCriteriaSearch;
import org.oscarehr.demographic.service.DemographicService;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.web.DemographicSearchHelper;
import org.oscarehr.ws.common.annotation.SkipContentLoggingInbound;
import org.oscarehr.ws.common.annotation.SkipContentLoggingOutbound;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.oscarehr.ws.rest.to.AbstractSearchResponse;
import org.oscarehr.ws.rest.to.model.DemographicSearchResult;
import org.oscarehr.ws.rest.to.model.StatusValueTo1;
import org.oscarehr.ws.rest.transfer.common.FileTransfer;
import org.oscarehr.ws.rest.transfer.common.ProgressBarPollingData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Defines a service contract for main operations on demographic.
 */
@Path("/demographics")
@Component("demographicsService")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "demographics")
public class DemographicsService extends AbstractServiceImpl
{
	private static final Logger logger = MiscUtils.getLogger();

	@Autowired
	private DemographicManager demographicManager;

	@Autowired
	private DemographicDao demographicDao;

	@Autowired
	private DemographicService demographicService;

	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	private PatientImportWrapperService patientImportWrapperService;

	@Autowired
	private PatientExportService patientExportService;

	@Autowired
	private DemographicSetsDao demographicSetsDao;

	@Autowired
	private DataMigrationService dataMigrationService;

	@Autowired
	private PatientImportContextService patientImportContextService;

	@Autowired
	private PatientExportContextService patientExportContextService;

	/**
	 * quick search demographics, performs an OR on the restrictions rather than an AND.
	 * this provides more result matches but with less accuracy on which field is wanted
	 *
	 * @return
	 * 		Returns data for the demographic provided
	 */
	@GET
	@Path("/quickSearch")
	public RestSearchResponse<DemographicSearchResult> quickSearch(
			@QueryParam("page")
			@DefaultValue("1")
			@Parameter(description = "Requested result page")
					Integer page,
			@QueryParam("perPage")
			@DefaultValue("10")
			@Parameter(description = "Number of results per page")
					Integer perPage,
			@QueryParam("query") String query)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInInfo().getLoggedInProviderNo(),
				SecurityInfoManager.READ, null, "_demographic");
		if (query == null)
		{
			return RestSearchResponse.errorResponse("No Query Parameter Sent");
		}

		// set up the criteria
		DemographicCriteriaSearch criteriaSearch = new DemographicCriteriaSearch();
		criteriaSearch.setStatusMode(DemographicCriteriaSearch.STATUS_MODE.active);
		criteriaSearch.setJunctionTypeOR();
		criteriaSearch.setMatchModeStart();
		criteriaSearch.setSortDirAscending();

		if (query.contains("*"))
		{
			criteriaSearch.setCustomWildcardsEnabled(true);
		}

		String [] names = query.split(",");
		if (names.length >= 2)
		{
			// first and last name searching case. force an and on the name filter
			criteriaSearch.setFirstName(names[1].trim());
			criteriaSearch.setLastName(names[0].trim());
			criteriaSearch.setForceConjoinOnNames(true);
		}
		else if (names.length == 1)
		{
			criteriaSearch.setFirstName(names[0].trim());
			criteriaSearch.setLastName(names[0].trim());
		}

		criteriaSearch.setHin(query.trim());

		return getSearchResponse(criteriaSearch, page, perPage);
	}

	@GET
	@Path("/search")
	public RestSearchResponse<DemographicSearchResult> search(@QueryParam("page")
	                                                          @DefaultValue("1")
	                                                          @Parameter(description = "Requested result page")
			                                                          Integer page,
	                                                          @QueryParam("perPage")
	                                                          @DefaultValue("10")
	                                                          @Parameter(description = "Number of results per page")
			                                                          Integer perPage,
	                                                          @QueryParam("jsonData") String jsonStr)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInInfo().getLoggedInProviderNo(),
				SecurityInfoManager.READ, null, "_demographic");

		JSONObject json = JSONObject.fromObject(jsonStr);

		// set up the search criteria
		DemographicCriteriaSearch searchQuery = convertFromJSON(json);
		return getSearchResponse(searchQuery, page, perPage);
	}
	private RestSearchResponse<DemographicSearchResult> getSearchResponse(DemographicCriteriaSearch searchQuery, Integer page, Integer perPage)
	{
		page = validPageNo(page);
		perPage = limitedResultCount(perPage);
		int offset = calculatedOffset(page, perPage);

		searchQuery.setOffset(offset);
		searchQuery.setLimit(perPage);
		int total = demographicDao.criteriaSearchCount(searchQuery);

		List<DemographicSearchResult> results = new ArrayList<>(0);
		if(total > 0)
		{
			results = demographicService.toSearchResultTransferList(searchQuery);
		}
		return RestSearchResponse.successResponse(results, page, perPage, total);
	}

	@GET
	@Path("/searchIntegrator")
	public RestResponse<AbstractSearchResponse<DemographicSearchResult>> searchIntegrator(@QueryParam("jsonData") String jsonStr,
	                                                                                      @QueryParam("itemsToReturn") Integer itemsToReturn)
	{
		AbstractSearchResponse<DemographicSearchResult> response = new AbstractSearchResponse<>();
		try
		{
			JSONObject json = JSONObject.fromObject(jsonStr);

			securityInfoManager.requireAllPrivilege(getLoggedInInfo().getLoggedInProviderNo(),
					SecurityInfoManager.READ, null, "_demographic");

			List<DemographicSearchResult> results = new ArrayList<>();

			if (json.getString("term").length() >= 1)
			{
				String searchType = json.getString("type");
				MatchingDemographicParameters matches = CaisiIntegratorManager.getMatchingDemographicParameters(getSearchMode(searchType), convertFromJSON(json));
				List<MatchingDemographicTransferScore> integratorSearchResults = null;
				try
				{
					matches.setMaxEntriesToReturn(itemsToReturn);
					matches.setMinScore(7);
					integratorSearchResults = DemographicSearchHelper.getIntegratedSearchResults(getLoggedInInfo(), matches);
					MiscUtils.getLogger().info("Integrator search results : " + (integratorSearchResults == null ? "null" : String.valueOf(integratorSearchResults.size())));
				}
				catch (Exception e)
				{
					MiscUtils.getLogger().error("error searching integrator", e);
				}

				if (integratorSearchResults != null)
				{
					for (MatchingDemographicTransferScore matchingDemographicTransferScore : integratorSearchResults)
					{
						if (isLocal(matchingDemographicTransferScore))
						{
							MiscUtils.getLogger().warn("ignoring remote demographic since we already have them locally");
							continue;
						}
						if (matchingDemographicTransferScore.getDemographicTransfer() != null)
						{
							DemographicTransfer obj = matchingDemographicTransferScore.getDemographicTransfer();
							DemographicSearchResult item = new DemographicSearchResult();
							item.setLastName(obj.getLastName());
							item.setFirstName(obj.getFirstName());
							item.setSex(obj.getGender().toString());
							item.setDob(obj.getBirthDate().getTime());
							item.setRemoteFacilityId(obj.getIntegratorFacilityId());
							item.setDemographicNo(obj.getCaisiDemographicId());
							results.add(item);
						}
					}
				}
			}

			response.setContent(results);
			response.setTotal((response.getContent() != null) ? response.getContent().size() : 0);

			return RestResponse.successResponse(response);
		}
		catch (Exception e)
		{
			logger.error("Error", e);
		}
		return RestResponse.errorResponse("Error");
	}

	@GET
	@Path("/statusList")
	public RestResponse<List<StatusValueTo1>> getStatusList(@QueryParam("type") String listType)
	{
		try
		{
			// get the list
			List<String> statusList = demographicManager.getPatientStatusList();

			// create transfer objects list
			List<StatusValueTo1> resultList = new ArrayList<>(statusList.size());
			for(String status : statusList)
			{
				resultList.add(new StatusValueTo1(status));
			}

			return RestResponse.successResponse(resultList);
		}
		catch (Exception e)
		{
			logger.error("Error", e);
		}
		return RestResponse.errorResponse("Error");
	}

	@POST
	@Path("/import")
	@SkipContentLoggingInbound
	public RestResponse<String> demographicImport(
			@QueryParam("type") String type,
			@QueryParam("source") String importSource,
			@QueryParam("merge") String mergeStrategy,
			@QueryParam("site") String defaultSiteName,
			List<FileTransfer> fileListTransfer) throws IOException, InterruptedException, TimeoutException
	{
		String loggedInProviderNo = getLoggedInInfo().getLoggedInProviderNo();
		securityInfoManager.requireOnePrivilege(loggedInProviderNo, SecurityInfoManager.WRITE, null, SecObjectName._ADMIN);

		String processId = UUID.randomUUID().toString();

		// run the main process in a new thread so this one can return a response
		Thread thread = new Thread(() ->
		{
			List<GenericFile> temporaryFileList = new ArrayList<>();
			List<GenericFile> importFileList = new ArrayList<>();
			java.nio.file.Path tempDir = null;
			try
			{
				tempDir = FileFactory.createTempDirectory();

				for(FileTransfer file : fileListTransfer)
				{
					GenericFile tempfile = FileFactory.createTempFile(file.toInputStream(), tempDir, "_" + file.getName());
					temporaryFileList.add(tempfile);
					tempfile.rename(file.getName()); //might be a referenced document. make sure it uses the original name

					// unpack zip files, and treat their contents similar to any attached files
					if(tempfile instanceof ZIPFile)
					{
						List<GenericFile> zipContents = ((ZIPFile) tempfile).unzip();
						temporaryFileList.addAll(zipContents);

						for(GenericFile zipContentFile : zipContents)
						{
							if(zipContentFile instanceof XMLFile)
							{
								importFileList.add(zipContentFile);
							}
						}
					}
					// only attempt to import from xml files
					else if(tempfile instanceof XMLFile)
					{
						importFileList.add(tempfile);
					}
				}

				patientImportWrapperService.importDemographics(
						type,
						importSource,
						mergeStrategy,
						importFileList,
						tempDir.toString(),
						false,
						false,
						defaultSiteName);
			}
			catch(Exception e)
			{
				logger.error("Import thread exception", e);
			}
			finally
			{
				try
				{
					// clean up temp files
					for(GenericFile tempFile : temporaryFileList)
					{
						tempFile.deleteFile();
					}
					if(tempDir != null)
					{
						logger.info("delete temp directory: " + tempDir.toString());
						Files.deleteIfExists(tempDir);
					}
				}
				catch(IOException e)
				{
					logger.error("Error cleaning import temp files", e);
				}
			}
		});
		thread.setName(processId);
		thread.start();

		// wait for the context to exist before returning, with an emergency timeout just in case
		patientImportContextService.waitForContext(processId, 60000);
		return RestResponse.successResponse(processId);
	}

	@GET
	@Path("/import/{processId}")
	@SkipContentLoggingOutbound
	public RestResponse<ImportTransferOutbound> demographicImportResults(@PathParam("processId") String processId) throws IOException
	{
		securityInfoManager.requireAllPrivilege(getLoggedInInfo().getLoggedInProviderNo(),
				SecurityInfoManager.READ, null, SecObjectName._ADMIN);
		return RestResponse.successResponse(dataMigrationService.getImportTransfer(processId));
	}

	@GET
	@Path("/import/{processId}/progress")
	public RestResponse<ProgressBarPollingData> demographicImportProgress(@PathParam("processId") String processId) throws IOException
	{
		securityInfoManager.requireAllPrivilege(getLoggedInInfo().getLoggedInProviderNo(),
				SecurityInfoManager.READ, null, SecObjectName._ADMIN);
		return RestResponse.successResponse(dataMigrationService.getImportStatus(processId));
	}

	@GET
	@Path("/import/{processId}/logs/download")
	@Produces("application/zip")
	@SkipContentLoggingOutbound
	public Response downloadImportLogs(@PathParam("processId") String processId,
	                                   @QueryParam("logName") final List<String> logFileNames) throws IOException
	{
		securityInfoManager.requireAllPrivilege(getLoggedInInfo().getLoggedInProviderNo(),
				SecurityInfoManager.READ, null, SecObjectName._ADMIN);

		List<GenericFile> logFiles = new ArrayList<>(logFileNames.size());
		for(String fileName : logFileNames)
		{
			logFiles.add(FileFactory.getImportLogFile(processId, fileName));
		}
		ZIPFile zipFile = ZipFileFactory.packageZipFile(logFiles);

		String filename = "import-logs.zip";
		Response.ResponseBuilder response = Response.ok(zipFile.toFileInputStream());

		response.header("Content-Disposition", "filename="+filename);
		response.type("application/zip");
		return response.build();
	}


	@POST
	@Path("/export")
	public RestResponse<String> demographicExport(
			@QueryParam("type") String type,
			@QueryParam("patientSet") String patientSet,
			ExportPreferences exportPreferences) throws Exception
	{
		securityInfoManager.requireAllPrivilege(getLoggedInInfo().getLoggedInProviderNo(),
				SecurityInfoManager.READ, null, SecObjectName._ADMIN);

		String processId = UUID.randomUUID().toString();

		// run the main process in a new thread so this one can return a response
		Thread thread = new Thread(() ->
		{
			try
			{
				patientExportService.exportDemographicsToZip(
						patientSet,
						ImporterExporterFactory.EXPORTER_TYPE.valueOf(type),
						exportPreferences);
			}
			catch(Exception e)
			{
				logger.error("Export thread error", e);
			}
		});
		thread.setName(processId);
		thread.start();

		// wait for the context to exist before returning, with an emergency timeout just in case
		patientExportContextService.waitForContext(processId, 60000);
		return RestResponse.successResponse(processId);
	}

	@GET
	@Path("/export/{processId}")
	@Produces("application/zip")
	@SkipContentLoggingOutbound
	public Response demographicExportResults(@PathParam("processId") String processId) throws IOException
	{
		securityInfoManager.requireAllPrivilege(getLoggedInInfo().getLoggedInProviderNo(),
				SecurityInfoManager.READ, null, SecObjectName._ADMIN);
		ExportTransferOutbound transfer = dataMigrationService.getExportTransfer(processId);

		Response.ResponseBuilder response = Response.ok(transfer.getExportFile().toFileInputStream());
		response.header("Content-Disposition", "filename=" + transfer.getPatientSet() + ".zip");
		response.type("application/zip");
		return response.build();
	}

	@GET
	@Path("/export/{processId}/progress")
	public RestResponse<ProgressBarPollingData> demographicExportProgress(@PathParam("processId") String processId) throws IOException
	{
		securityInfoManager.requireAllPrivilege(getLoggedInInfo().getLoggedInProviderNo(),
				SecurityInfoManager.READ, null, SecObjectName._ADMIN);

		return RestResponse.successResponse(dataMigrationService.getExportStatus(processId));
	}

	@GET
	@Path("/sets")
	public RestSearchResponse<String> getDemographicSetNames()
	{
		securityInfoManager.requireAllPrivilege(getLoggedInInfo().getLoggedInProviderNo(),
				SecurityInfoManager.READ, null, SecObjectName._DEMOGRAPHIC);

		List<String> demographicSetNames = demographicSetsDao.findSetNames();
		return RestSearchResponse.successResponseOnePage(demographicSetNames);
	}

	private DemographicService.SEARCH_MODE getSearchMode(String searchType)
	{
		DemographicService.SEARCH_MODE searchMode = demographicService.searchModeStringToEnum(searchType);
		if(searchMode == null)
		{
			searchMode = DemographicService.SEARCH_MODE.name;
		}
		return searchMode;
	}

	private DemographicCriteriaSearch convertFromJSON(JSONObject json) {
		if(json ==null)return null;


		String searchType = json.getString("type");
		DemographicService.SEARCH_MODE searchMode = getSearchMode(searchType);

		String keyword = json.getString("term");

		DemographicService.STATUS_MODE statusMode;
		switch(json.getString("status"))
		{
			default:
			case "active": statusMode = DemographicService.STATUS_MODE.active; break;
			case "inactive": statusMode = DemographicService.STATUS_MODE.inactive; break;
			case "all": statusMode = DemographicService.STATUS_MODE.all; break;
		}
		DemographicCriteriaSearch.SORT_MODE sortMode = DemographicCriteriaSearch.SORT_MODE.DemographicName;
		DemographicCriteriaSearch.SORTDIR sortDir = DemographicCriteriaSearch.SORTDIR.asc;
//		req.setIntegrator(Boolean.valueOf(json.getString("integrator")));

		Pattern namePtrn = Pattern.compile("sorting\\[(\\w+)\\]");

		JSONObject params = json.getJSONObject("params");
		if(params != null)
		{
			for(Object key : params.keySet())
			{
				Matcher nameMtchr = namePtrn.matcher((String) key);
				if(nameMtchr.find())
				{
					String var = nameMtchr.group(1);
					sortMode = DemographicCriteriaSearch.SORT_MODE.valueOf(var);
					sortDir = DemographicCriteriaSearch.SORTDIR.valueOf(params.getString((String) key));
				}
			}
		}

		// set up the search criteria
		DemographicCriteriaSearch searchQuery = demographicService.buildDemographicSearch(keyword,
				searchMode,
				statusMode,
				sortMode);
		searchQuery.setSortDir(sortDir);
		return searchQuery;
	}

	private boolean isLocal(MatchingDemographicTransferScore matchingDemographicTransferScore)
	{
		String hin = matchingDemographicTransferScore.getDemographicTransfer().getHin();

		if(hin != null && !hin.isEmpty())
		{
			DemographicCriteriaSearch searchQuery = demographicService.buildDemographicSearch(hin,
					DemographicService.SEARCH_MODE.hin,
					DemographicService.STATUS_MODE.active,
					DemographicCriteriaSearch.SORT_MODE.DemographicName);

			int count = demographicDao.criteriaSearchCount(searchQuery);
			return count > 0;
		}
		return false;
	}
}
