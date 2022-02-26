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
package org.oscarehr.fax.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.oscarehr.fax.model.FaxFileType;
import org.oscarehr.fax.model.FaxNotificationStatus;
import org.oscarehr.fax.model.FaxStatusCombined;
import org.oscarehr.fax.model.FaxStatusInternal;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore properties that are not defined in this class
public class FaxOutboxTransferOutbound implements Serializable
{

	private Long id;
	private Long faxAccountId;

	private String providerId;
	private String providerName;
	private Integer demographicId;
	private String toFaxNumber;
	/* file type: document, form, consult, etc. */
	private FaxFileType fileType;
	/* the sent status of the document as recorded in the system */
	private FaxStatusInternal systemStatus;
	/* a message sent along with the status, usually for error explanations */
	private String systemStatusMessage;
	/* the sent date of the document as recorded in the system */
	private LocalDateTime systemSentDateTime;
	private Boolean archived;
	private FaxNotificationStatus notificationStatus;

	/* the sent status of the document as retrieved from the api */
	private String integrationStatus;
	/* the received/queued date of the document as retrieved from the api.
	 * when the integration first learns of the document */
	private LocalDateTime integrationQueuedDateTime;
	/* the sent date of the document as retrieved from the api */
	private LocalDateTime integrationSentDateTime;

	/* the single combined state of the systemStatus and the integrationStatus */
	private FaxStatusCombined combinedStatus;
}
