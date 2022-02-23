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
package org.oscarehr.fax.service;

import org.apache.commons.codec.binary.Base64;
import org.oscarehr.document.model.Document;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.fax.dao.FaxInboundDao;
import org.oscarehr.fax.externalApi.srfax.result.GetFaxInboxResult;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxInbound;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * This service should be responsible for handling all transactional logic around receiving faxes
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class IncomingFaxService
{
	@Autowired
	private FaxInboundDao faxInboundDao;

	@Autowired
	private DocumentService documentService;

	public FaxInbound saveFaxDocument(final FaxAccount faxAccount, final GetFaxInboxResult inboxMeta, String result) throws IOException, InterruptedException
	{
		InputStream documentStream = base64ToStream(result);
		Long referenceId = Long.parseLong(inboxMeta.getDetailsId());

		// upload a new document through document service
		Document document = new Document();
		document.setSource(faxAccount.getIntegrationType().name());
		document.setDocCreator(ProviderData.SYSTEM_PROVIDER_NO);
		document.setResponsible(ProviderData.SYSTEM_PROVIDER_NO);
		document.setDocdesc("");
		document.setDocfilename("-" + faxAccount.getIntegrationType() + "-" + referenceId + ".pdf");

		document = documentService.uploadNewDemographicDocument(document, documentStream, null);
		documentService.routeToGeneralInbox(document.getDocumentNo());

		// create a record in the fax_inbound table
		FaxInbound faxInbound = new FaxInbound();
		faxInbound.setCreatedAt(new Date());
		faxInbound.setSentFrom(inboxMeta.getCallerId());
		faxInbound.setDocument(document);
		faxInbound.setFaxAccount(faxAccount);
		faxInbound.setExternalAccountId(faxAccount.getLoginId());
		faxInbound.setExternalAccountType(faxAccount.getIntegrationType());
		faxInbound.setExternalReferenceId(referenceId);
		faxInboundDao.persist(faxInbound);
		return faxInbound;
	}

	private InputStream base64ToStream(String base64String)
	{
		return new ByteArrayInputStream(Base64.decodeBase64(base64String));
	}
}