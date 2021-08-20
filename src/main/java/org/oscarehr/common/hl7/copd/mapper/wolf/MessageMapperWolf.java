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
package org.oscarehr.common.hl7.copd.mapper.wolf;

import ca.uhn.hl7v2.HL7Exception;
import org.oscarehr.common.hl7.copd.mapper.MessageMapper;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.dataMigration.service.ImporterExporterFactory;
import org.oscarehr.provider.model.ProviderData;

/**
 * This class shares a lot with the encounter notes mapper, as there is no true place for messages in ToPD formats.
 * Wolf sends them in as notes with some specific formatting, so we do our best to map them
 */
public class MessageMapperWolf extends MessageMapper
{
	public MessageMapperWolf(ZPD_ZTR message, int providerRep)
	{
		super(message, providerRep, ImporterExporterFactory.IMPORT_SOURCE.WOLF);
	}

	@Override
	public ProviderData getSigningProvider(int rep) throws HL7Exception
	{
		String noteProviderStr = getNoteSignature(rep);
		return getWOLFParsedProviderInfo(noteProviderStr, "ZPV.5");
	}

	@Override
	public ProviderData getRecipientProvider(int rep) throws HL7Exception
	{
		//TODO - parse message strings for provider data?
		return null;
	}

	/** Wolf sends their provider messages in encounter notes. Here we try to separate them out. */
	@Override
	public boolean isMessageNote(int rep) throws HL7Exception
	{
		String noteText = getNoteComment(rep);
		return (noteText != null && noteText.startsWith("Message:"));
	}
}
