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


package org.oscarehr.ws.transfer_objects;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public final class ProviderSignatureTransfer {
	private String providerNo;
	private String filename;
	private String md5sum;
	private String base64ImageData;

	public String getProviderNo() {
		return (providerNo);
	}

	public void setProviderNo(String providerNo) {
		this.providerNo = providerNo;
	}

	public String getFilename() {
		return (filename);
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getMd5Sum() {
		return (md5sum);
	}

	public void setMd5Sum(String md5sum) {
		this.md5sum = md5sum;
	}

	public String getBase64ImageData() {
		return (base64ImageData);
	}

	public void setBase64ImageData(String base64ImageData) {
		this.base64ImageData = base64ImageData;
	}

	@Override
	public String toString() {
		return (ReflectionToStringBuilder.toString(this));
	}
}

