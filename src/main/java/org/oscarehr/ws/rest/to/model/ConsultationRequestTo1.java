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


package org.oscarehr.ws.rest.to.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore properties that are not defined in this class
public class ConsultationRequestTo1 implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_NOT_COMPLETE = "1";
	private static final String DEFAULT_NON_URGENT = "2";
	
	private Integer id;
	private LocalDate referralDate = LocalDate.now();
	private Integer serviceId;
	private ProfessionalSpecialistTo1 professionalSpecialist;
	private ZonedDateTime appointmentDateTime;
	private String reasonForReferral;
	private String clinicalInfo;
	private String currentMeds;
	private String allergies;
	private String providerNo;
	private Integer demographicId;
	private String status = DEFAULT_NOT_COMPLETE;
	private String statusText;
	private String sendTo;
	private String concurrentProblems;
	private String urgency = DEFAULT_NON_URGENT;
	private boolean patientWillBook;
	private String siteName;
	private LocalDate followUpDate;
	private String signatureImg;
	private String letterheadName;
	private String letterheadAddress;
	private String letterheadPhone;
	private String letterheadFax;
    private List<ConsultationAttachmentTo1> attachments;
	
	private List<FaxConfigTo1> faxList;
	private List<String> sendToList;
}
