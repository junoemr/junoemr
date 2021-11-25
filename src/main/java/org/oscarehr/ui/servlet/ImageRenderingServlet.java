/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package org.oscarehr.ui.servlet;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.caisi_integrator.CaisiIntegratorManager;
import org.oscarehr.caisi_integrator.ws.DemographicTransfer;
import org.oscarehr.caisi_integrator.ws.DemographicWs;
import org.oscarehr.casemgmt.dao.ClientImageDAO;
import org.oscarehr.casemgmt.model.ClientImage;
import org.oscarehr.clinic.service.ClinicImageService;
import org.oscarehr.clinic.service.ClinicImageService.IMAGE_TYPE;
import org.oscarehr.common.dao.DigitalSignatureDao;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.model.DigitalSignature;
import org.oscarehr.common.model.Provider;
import org.oscarehr.util.DigitalSignatureUtils;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SessionConstants;
import org.oscarehr.util.SpringUtils;

import oscar.OscarProperties;

/**
 * This servlet requires a parameter called "source" which should signify where to get the image from. Examples include source=local_client, or source=hnr_client. Depending on the source, you may optionally need more parameters, as examples a local_client
 * may need a clientId=5 or a hnr_client may need linkingId=3. <br />
 * <br />
 * The structure of this class follows the structure of the Servlet class itself in the pattern of the service() -> (doPost/doGet/doDelete), from the doGet we fork to each specific source processor. <br />
 * <br />
 * This servlet assumes the image exists, for the most part this servlet is a "drop in" replacement for serving images from the HD directly, i.e. things like existence and appropriateness of the image should have already been checked. In general security
 * should also be checked before hand, we also check again here as security is a special case.
 * <br /> <br />
 * This servlet should no longer be extended, look at ContentRenderingServlet instead which is much more versatile
 */
public final class ImageRenderingServlet extends HttpServlet {
	private static Logger logger = MiscUtils.getLogger();
	private static ClientImageDAO clientImageDAO = (ClientImageDAO) SpringUtils.getBean("clientImageDAO");
	private static DigitalSignatureDao digitalSignatureDao = (DigitalSignatureDao) SpringUtils.getBean("digitalSignatureDao");

	public enum Source {
		local_client, hnr_client, integrator_client, signature_preview, signature_stored, clinic_logo, custom_nav_icon,
	}

	@Override
    public final void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		int httpResponseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		try {
			String source = request.getParameter("source");
			
			
			// for the most part each sub renderer is responsible for everything including
			// security checks. There's actually not too much point in having a shared
			// servlet except to save a little bit of work on registering servlets
			// and a little processing logic.
			if (Source.local_client.name().equals(source)) {
				httpResponseCode = renderLocalClient(request, response);
			} else if (Source.hnr_client.name().equals(source)) {
				httpResponseCode = renderHnrClient(request, response);
			} else if (Source.integrator_client.name().equals(source)) {
				httpResponseCode = renderIntegratorClient(request, response);
			} else if (Source.signature_preview.name().equals(source)) {
				httpResponseCode = renderSignaturePreview(request, response);
			} else if (Source.signature_stored.name().equals(source)) {
				httpResponseCode = renderSignatureStored(request, response);
			} else if (Source.clinic_logo.name().equals(source)) {
				httpResponseCode = renderClinicLogoStored(request, response);
			} else if (Source.custom_nav_icon.name().equals(source)) {
				httpResponseCode = renderCustomNavIcon(request, response);
			} else {
				throw (new IllegalArgumentException("Unknown source type : " + source));
			}
		}
		catch (Exception e) {
			if (e.getCause() instanceof SocketException) {
				logger.warn("An error we can't handle that's expected infrequently. " + e.getMessage());
			} else {
				logger.error("Unexpected error. qs=" + request.getQueryString(), e);
				httpResponseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			}
		}
		if(httpResponseCode != HttpServletResponse.SC_OK) {
			response.sendError(httpResponseCode);
		}
	}

	/**
	 * This convenience method is only suitable for small images as image is obviously not streamed since it's passed in.
	 *
	 * @deprecated
	 * use renderImage(HttpServletResponse, GenericFile) instead
	 * @param response
	 * @param image
	 * @param imageType image sub type of the contentType, i.e. "jpeg" "png"
	 * @throws IOException
	 */
	@Deprecated
	private static final int renderImage(HttpServletResponse response, byte[] image, String imageType) throws IOException {
		if(image == null) { // this method should never be called with a null image
			return HttpServletResponse.SC_NO_CONTENT;
		}
		response.setContentLength(image.length);
		response.setContentType("image/" + imageType);

		BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
		try {
			bos.write(image);
		}
		catch(IOException e) {
			// Once the output stream or writer has been created, errors should not be output using response.sendError,
			// as the response has already been committed and this is an illegal state
			logger.error("IO Error while streaming image", e);
		}
		finally {
			bos.flush();
			bos.close();
		}
		return HttpServletResponse.SC_OK;
	}

	private static byte[] getDefaultImage(HttpServletRequest request) {
		String defaultClientImage = "/images/defaultG_img.jpg";

        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        try {
            InputStream is = request.getSession().getServletContext().getResourceAsStream(defaultClientImage);
            byte[] byteChunk = new byte[1024];
            int n;
            while ( (n = is.read(byteChunk)) > 0 ) {
            	bais.write(byteChunk, 0, n);
        	}
        }
        catch (IOException e) {
	        logger.error("Error reading default image.", e);
        }
		return bais.toByteArray();
	}

	private static final int renderIntegratorClient(HttpServletRequest request, HttpServletResponse response) {
		// this expects integratorFacilityId and caisiClientId as a parameter

		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);

		if (!isLoggedIn(request))
		{
			return HttpServletResponse.SC_FORBIDDEN;
		}

		try {
			Integer integratorFacilityId = Integer.parseInt(request.getParameter("integratorFacilityId"));
			Integer caisiClientId = Integer.parseInt(request.getParameter("caisiDemographicId"));
			DemographicWs demographicWs = CaisiIntegratorManager.getDemographicWs(loggedInInfo, loggedInInfo.getCurrentFacility());
			DemographicTransfer demographicTransfer = demographicWs.getDemographicByFacilityIdAndDemographicId(integratorFacilityId, caisiClientId);

			if (demographicTransfer != null && demographicTransfer.getPhoto() != null) {
				return renderImage(response, demographicTransfer.getPhoto(), "jpeg");
			}
		}
		catch (Exception e) {
			logger.error("Unexpected error.", e);
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}

		return HttpServletResponse.SC_NOT_FOUND;
	}

	private static final int renderHnrClient(HttpServletRequest request, HttpServletResponse response) {
		// this expects linkingId as a parameter

		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);

		if (!isLoggedIn(request))
		{
			return HttpServletResponse.SC_FORBIDDEN;
		}

		try {
			Integer linkingId = Integer.parseInt(request.getParameter("linkingId"));
			org.oscarehr.hnr.ws.Client hnrClient = CaisiIntegratorManager.getHnrClient(loggedInInfo, loggedInInfo.getCurrentFacility(),linkingId);

			if (hnrClient != null && hnrClient.getImage() != null) {
				return renderImage(response, hnrClient.getImage(), "jpeg");
			}
		}
		catch (Exception e) {
			logger.error("Unexpected error.", e);
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}
		return HttpServletResponse.SC_NOT_FOUND;
	}

	private static final int renderLocalClient(HttpServletRequest request, HttpServletResponse response) {
		// this expects clientId as a parameter

		if (!isLoggedIn(request))
		{
			return HttpServletResponse.SC_FORBIDDEN;
		}

		try {
			ClientImage clientImage = clientImageDAO.getClientImage(Integer.parseInt(request.getParameter("clientId")));
			byte[] imageBytes;
			if (clientImage != null && "jpg".equalsIgnoreCase(clientImage.getImage_type())) {
				imageBytes = clientImage.getImage_data();
			}
			else {
				imageBytes = getDefaultImage(request);
			}
			return renderImage(response, imageBytes, "jpeg");
		}
		catch (Exception e) {
			logger.error("Unexpected error.", e);
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}
	}
	
	private int renderSignaturePreview(HttpServletRequest request, HttpServletResponse response) {
		// this expects signatureRequestId as a parameter

		if (!isLoggedIn(request))
		{
			return HttpServletResponse.SC_FORBIDDEN;
		}


		try {
			FileInputStream fileInputStream = null;
			try {
				String signatureRequestId = request.getParameter(DigitalSignatureUtils.SIGNATURE_REQUEST_ID_KEY);
				String tempFilePath = DigitalSignatureUtils.getTempFilePath(signatureRequestId);
				fileInputStream = new FileInputStream(tempFilePath);
				byte[] imageBytes = new byte[1024 * 256];
				fileInputStream.read(imageBytes);
				return renderImage(response, imageBytes, "jpeg");
			}
			catch (FileNotFoundException e) {
				// no image, render a blank gif, yes this breaks the concept 
				// of the image already exists, but it's difficult to implement the preview otherwise
				String tempFilePath = getServletContext().getRealPath("/images/1x1.gif");
				fileInputStream = new FileInputStream(tempFilePath);
				byte[] imageBytes = new byte[1024 * 32];
				fileInputStream.read(imageBytes);
				return renderImage(response, imageBytes, "gif");
			} finally {
				IOUtils.closeQuietly(fileInputStream);
			}
		}
		catch (Exception e) {
			logger.error("Unexpected error.", e);
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}
	}
	
	private static final int renderSignatureStored(HttpServletRequest request, HttpServletResponse response) {
		// this expects digitalSignatureId as a parameter

		if (!isLoggedIn(request))
		{
			return HttpServletResponse.SC_FORBIDDEN;
		}

		try {
			// get image
			DigitalSignature digitalSignature = digitalSignatureDao.find(Integer.parseInt(request.getParameter("digitalSignatureId")));
			if (digitalSignature != null) {
				return renderImage(response, digitalSignature.getSignatureImage(), "jpeg");
			}
		} catch (Exception e) {
			logger.error("Unexpected error.", e);
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}

		return HttpServletResponse.SC_NOT_FOUND;
	}
	
	private static final int renderClinicLogoStored(HttpServletRequest request, HttpServletResponse response) {

		if (!isLoggedIn(request))
		{
			return HttpServletResponse.SC_FORBIDDEN;
		}

		try {
			String filename = OscarProperties.getInstance().getProperty("CLINIC_LOGO_FILE");
			if(filename != null ){
				File f = new File(filename);
				if(f != null && f.exists()) {
					byte[] data = FileUtils.readFileToByteArray(f);
					
					if(data != null) {
						return renderImage(response, data, "jpeg");
					}
				}
			}
		} catch (Exception e) {
			logger.error("Unexpected error.", e);
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}

		return HttpServletResponse.SC_NOT_FOUND;
	}

	private static final int renderCustomNavIcon(HttpServletRequest request, HttpServletResponse response)
	{
		if (!isLoggedIn(request))
		{
			return HttpServletResponse.SC_FORBIDDEN;
		}
		try
		{
			GenericFile imageFile = ClinicImageService.getImage(IMAGE_TYPE.NAV_LOGO);
			return renderImage(response, imageFile);
		}
		catch (FileNotFoundException e)
		{
			return HttpServletResponse.SC_NOT_FOUND;
		}
		catch (Exception e)
		{
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}
	}

	private static final int renderImage(HttpServletResponse response, GenericFile imageFile) throws IOException
	{
		byte[] buffer = new byte[1024];

		try (InputStream fileStream = imageFile.asFileInputStream(); BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream()))
		{
			Long fileSize = imageFile.getFileSize();
			response.setContentLength(fileSize.intValue());
			response.setContentType(imageFile.getContentType());

			while(fileStream.read(buffer) != -1)
			{
				output.write(buffer);
			}

			output.flush();
			return HttpServletResponse.SC_OK;
		}
	}

	private static boolean isLoggedIn(HttpServletRequest request)
	{
		HttpSession session = request.getSession();
		Provider provider = (Provider) session.getAttribute(SessionConstants.LOGGED_IN_PROVIDER);
		return provider != null;
	}
}