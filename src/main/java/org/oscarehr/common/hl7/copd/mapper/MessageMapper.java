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
package org.oscarehr.common.hl7.copd.mapper;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.model.MessageTbl;
import org.oscarehr.dataMigration.service.CoPDImportService;
import org.oscarehr.provider.model.ProviderData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class shares a lot with the encounter notes mapper, as there is no true place for messages in ToPD formats.
 * Wolf sends them in as notes with some specific formatting, so we do our best to map them
 */
public class MessageMapper extends AbstractMapper
{
	public MessageMapper(ZPD_ZTR message, int providerRep, CoPDImportService.IMPORT_SOURCE importSource)
	{
		super(message, providerRep, importSource);
	}

	public int getNumMessageNotes()
	{
		return provider.getZPVReps();
	}

	public List<MessageTbl> getEncounterNoteAsMessagesList() throws HL7Exception
	{
		int numNotes = getNumMessageNotes();
		List<MessageTbl> messageNoteList = new ArrayList<>(numNotes);
		for(int i=0; i< numNotes; i++)
		{
			if(isMessageNote(i))
			{
				messageNoteList.add(getMessageNote(i));
			}
		}
		return messageNoteList;
	}

	public MessageTbl getMessageNote(int rep) throws HL7Exception
	{
		MessageTbl message = new MessageTbl();
		message.setDate(getMessageDate(rep));
		message.setTime(getMessageDate(rep));
		message.setMessage(getNoteComment(rep).replaceAll("~crlf~", "\n"));
		message.setSubject(getNoteReason(rep).replaceAll("~crlf~", "\n"));

		return message;
	}

	public ProviderData getSigningProvider(int rep) throws HL7Exception
	{
		return null;
	}

	public ProviderData getRecipientProvider(int rep) throws HL7Exception
	{
		//TODO - parse message strings for provider data?
		return null;
	}

	public Date getMessageDate(int rep) throws HL7Exception
	{
		return getNullableDateTime(provider.getZPV(rep)
				.getZpv2_contactDate().getTs1_TimeOfAnEvent().getValue());
	}

	public String getNoteReason(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZPV(rep).getZpv3_contactReason().getValue());
	}

	public String getNoteComment(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZPV(rep).getZpv4_comment().getValue());
	}

	public String getNoteSignature(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZPV(rep).getZpv5_commentSignature().getValue());
	}

	/** messages may be sent as encounter notes. Here we try to separate them out. */
	public boolean isMessageNote(int rep) throws HL7Exception
	{
		return false;
	}
}
