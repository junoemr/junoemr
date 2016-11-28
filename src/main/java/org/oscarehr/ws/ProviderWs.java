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


package org.oscarehr.ws;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.oscarehr.common.model.Provider;
import org.oscarehr.managers.ProviderManager2;
import org.oscarehr.ws.transfer_objects.ProviderTransfer;
import org.oscarehr.ws.transfer_objects.ProviderSignatureTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import oscar.eform.EFormUtil;

@WebService
@Component
public class ProviderWs extends AbstractWs {
	@Autowired
	private ProviderManager2 providerManager;

	public ProviderTransfer[] getProviders(boolean active) {
		List<Provider> tempResults = providerManager.getProviders(active);

		ProviderTransfer[] results = ProviderTransfer.toTransfers(tempResults);

		return (results);
	}

	public ProviderSignatureTransfer getProviderSignature(Integer providerId) 
		throws Exception
	{
		if(providerId == null)
		{
			throw new Exception("ProviderId is required.");
		}

		// Make sure it's a valid provider id
		if(!providerManager.providerExists(providerId))
		{
			throw new Exception("ProviderId " + providerId + " does not exist.");
		}

		String filename = getImageFilename(providerId);

        File imageFile = EFormUtil.getImage(filename);

		// Make sure there's an image for that provider
		if(imageFile == null)	
		{
			throw new Exception("Image for provider " + providerId + " does not exist.");
		}

		// Base64 encode the image data
		byte[] imageData = getFileData(imageFile);
		String base64ImageData = base64Encode(imageData);

		String md5sum = DigestUtils.md5Hex(imageData);
		
		ProviderSignatureTransfer out = new ProviderSignatureTransfer();

		out.setProviderNo(providerId.toString());
		out.setFilename(filename);
		out.setMd5Sum(md5sum);
		out.setBase64ImageData(base64ImageData);

		return out;
	}

	private String base64Encode(byte[] data)
	{
		Base64 base64 = new Base64();
		return base64.encodeToString(data);
	}

	private byte[] getFileData(File file) throws Exception
	{
		int length = (int)file.length();
		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
		byte[] bytes = new byte[length];
		reader.read(bytes, 0, length);
		reader.close();

		return bytes;
	}

	private String getImageFilename(Integer providerId)
	{
		return providerId.toString() + ".png";
	}
}
