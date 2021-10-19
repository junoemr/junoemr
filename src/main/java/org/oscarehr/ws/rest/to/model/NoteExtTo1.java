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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.oscarehr.dataMigration.model.common.PartialDate;
import oscar.util.Jackson.PartialDateDeserializer;
import oscar.util.Jackson.PartialDateSerializer;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name="groupNoteExt")
public class NoteExtTo1 implements Serializable{
	
	// Key Value constants ***All date value key must be in format "XXX Date"
	public static String STARTDATE	    = "Start Date"	;
	public static String RESOLUTIONDATE = "Resolution Date"	;
	public static String PROCEDUREDATE  = "Procedure Date"	;
	
	public static String AGEATONSET	    = "Age at Onset"	;
	public static String TREATMENT	    = "Treatment"	;
	public static String PROBLEMSTATUS  = "Problem Status"	;
	public static String EXPOSUREDETAIL = "Exposure Details";
	public static String RELATIONSHIP   = "Relationship"	;
	public static String LIFESTAGE	    = "Life Stage"	;
	public static String HIDECPP	    = "Hide Cpp"	;
	public static String PROBLEMDESC    = "Problem Description";
		
	//Class fields
	private Long id;
	private Long noteId;
	private String keyVal;
	private String value;
	@JsonSerialize(using = PartialDateSerializer.class)
	@JsonDeserialize(using = PartialDateDeserializer.class)
	private PartialDate dateValue;
	
	//keyVal's
	@JsonSerialize(using = PartialDateSerializer.class)
	@JsonDeserialize(using = PartialDateDeserializer.class)
	private PartialDate startDate;
	@JsonSerialize(using = PartialDateSerializer.class)
	@JsonDeserialize(using = PartialDateDeserializer.class)
	private PartialDate resolutionDate;
	@JsonSerialize(using = PartialDateSerializer.class)
	@JsonDeserialize(using = PartialDateDeserializer.class)
	private PartialDate procedureDate;

	private String ageAtOnset;
	private String treatment;
	private String problemStatus;
	private String exposureDetail;
	private String relationship;
	private String lifeStage;
	private String hideCpp;
	private String problemDesc;
	
	public Long getId() {
	    return this.id;
	}
	public void setId(Long id) {
	    this.id = id;
	}
	
	public Long getNoteId() {
	    return this.noteId;
	}
	public void setNoteId(Long noteId) {
	    this.noteId = noteId;
	}
	
	public String getKeyVal() {
	    return this.keyVal;
	}
	public void setKeyVal(String keyVal) {
	    this.keyVal = keyVal;
	}
	
	public String getValue() {
	    return this.value;
	}
	public void setValue(String value) {
	    this.value = value;
	}
	
	public PartialDate getDateValue() {
	    return this.dateValue;
	}

	public void setDateValue(PartialDate dateValue) {
	    this.dateValue = dateValue;
	}
	
	public PartialDate getStartDate() {
	    return startDate;
    }
	
	public void setStartDate(PartialDate startDate) {
	    this.startDate = startDate;
    }
	
	public PartialDate getResolutionDate() {
	    return resolutionDate;
    }
	
	public void setResolutionDate(PartialDate resolutionDate) {
	    this.resolutionDate = resolutionDate;
    }
	
	public PartialDate getProcedureDate() {
	    return procedureDate;
    }
	
	public void setProcedureDate(PartialDate procedureDate) {
	    this.procedureDate = procedureDate;
    }
	public String getAgeAtOnset() {
		return ageAtOnset;
	}
	public void setAgeAtOnset(String ageAtOnset) {
		this.ageAtOnset = ageAtOnset;
	}
	public String getTreatment() {
	    return treatment;
    }
	public void setTreatment(String treatment) {
	    this.treatment = treatment;
    }
	
	public String getProblemStatus() {
	    return problemStatus;
    }
	
	public void setProblemStatus(String problemStatus) {
	    this.problemStatus = problemStatus;
    }
	
	public String getExposureDetail() {
	    return exposureDetail;
    }
	
	public void setExposureDetail(String exposureDetail) {
	    this.exposureDetail = exposureDetail;
    }
	
	public String getRelationship() {
	    return relationship;
    }
	
	public void setRelationship(String relationship) {
	    this.relationship = relationship;
    }
	
	public String getLifeStage() {
	    return lifeStage;
    }
	
	public void setLifeStage(String lifeStage) {
	    this.lifeStage = lifeStage;
    }
	
	public String getHideCpp() {
	    return hideCpp;
    }
	
	public void setHideCpp(String hideCpp) {
	    this.hideCpp = hideCpp;
    }
	
	public String getProblemDesc() {
	    return problemDesc;
    }
	
	public void setProblemDesc(String problemDesc) {
	    this.problemDesc = problemDesc;
    }
}
