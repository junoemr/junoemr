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


package org.oscarehr.allergy.model;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

@Entity
@Table(name = "allergies")
public class Allergy extends AbstractModel<Integer>
{
	public static final String SEVERITY_CODE_MILD="1";
	public static final String SEVERITY_CODE_MODERATE="2";
	public static final String SEVERITY_CODE_SEVERE="3";
	public static final String SEVERITY_CODE_UNKNOWN="4";

	public static final String SEVERITY_DESC_MILD = "Mild";
	public static final String SEVERITY_DESC_MODERATE = "Moderate";
	public static final String SEVERITY_DESC_SEVERE = "Severe";
	public static final String SEVERITY_DESC_UNKNOWN = "Unknown";

	public static final String ONSET_CODE_IMMEDIATE="1";
	public static final String ONSET_CODE_GRADUAL="2";
	public static final String ONSET_CODE_SLOW="3";
	public static final String ONSET_CODE_UNKNOWN="4";

	public static final String ONSET_DESC_IMMEDIATE = "Immediate";
	public static final String ONSET_DESC_GRADUAL = "Gradual";
	public static final String ONSET_DESC_SLOW = "Slow";
	public static final String ONSET_DESC_UNKNOWN = "Unknown";

	public static final String LIFESTAGE_CODE_NEWBORN ="N";
	public static final String LIFESTAGE_CODE_INFANT ="I";
	public static final String LIFESTAGE_CODE_CHILD ="C";
	public static final String LIFESTAGE_CODE_ADOLESCENT ="T";
	public static final String LIFESTAGE_CODE_ADULT ="A";

	public static final String LIFESTAGE_DESC_NEWBORN = "Newborn";
	public static final String LIFESTAGE_DESC_INFANT = "Infant";
	public static final String LIFESTAGE_DESC_CHILD = "Child";
	public static final String LIFESTAGE_DESC_ADOLESCENT = "Adolescent";
	public static final String LIFESTAGE_DESC_ADULT = "Adult";

	public static final String TYPECODE_DESC_GENERIC = "Generic Name";
	public static final String TYPECODE_DESC_COMPOUND = "Compound";
	public static final String TYPECODE_DESC_BRAND = "Brand Name";
	public static final String TYPECODE_DESC_ATC = "ATC Class";
	public static final String TYPECODE_DESC_AFHS = "AHFS Class";
	public static final String TYPECODE_DESC_INGREDIENT = "Ingredient";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "allergyid")
	private Integer id;

	@Column(name = "demographic_no")
	private Integer demographicNo;

	@Temporal(TemporalType.DATE)
	@Column(name = "entry_date")
	private Date entryDate;

	@Column(name = "DESCRIPTION")
	private String description;

	private String reaction;
	
	private boolean archived=false;

	@Column(name = "HICL_SEQNO")
	private Integer hiclSeqno;

	@Column(name = "HIC_SEQNO")
	private Integer hicSeqno;

	@Column(name = "AGCSP")
	private Integer agcsp;

	@Column(name = "AGCCS")
	private Integer agccs;

	@Column(name = "TYPECODE")
	private Integer typeCode;

	@Column(name = "drugref_id")
	private String drugrefId;

	@Column(name = "start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@Column(name = "age_of_onset")
	private String ageOfOnset;

	@Column(name = "severity_of_reaction")
	private String severityOfReaction;

	@Column(name = "onset_of_reaction")
	private String onsetOfReaction;

	@Column(name = "regional_identifier")
	private String regionalIdentifier;

	@Column(name = "life_stage")
	private String lifeStage;

	private int position=0;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdateDate;

	@Transient
	private boolean integratorResult;

	@Transient
	private String startDateFormat;
	/**
	 * This string is currently nullable because this field never use to exist, therefore all previous entries are null, all new entries should be populated though.
	 */
	private String providerNo;

	public boolean getArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public Integer getAllergyId() {
		return id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDemographicNo() {
		return demographicNo;
	}

	public void setDemographicNo(Integer demographic_no) {
		this.demographicNo = demographic_no;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = StringUtils.trimToNull(description);
	}

	public Date getEntryDate() {
    	return (entryDate);
    }

	public void setEntryDate(Date entryDate) {
    	this.entryDate = entryDate;
    }

	public String getReaction() {
		return reaction;
	}

	public void setReaction(String reaction) {
		this.reaction = StringUtils.trimToNull(reaction);
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Integer getHiclSeqno() {
		return hiclSeqno;
	}

	public void setHiclSeqno(Integer hiclSeqno) {
		this.hiclSeqno = hiclSeqno;
	}

	public Integer getHicSeqno() {
		return hicSeqno;
	}

	public void setHicSeqno(Integer hicSeqno) {
		this.hicSeqno = hicSeqno;
	}

	public Integer getAgcsp() {
		return agcsp;
	}

	public void setAgcsp(Integer agcsp) {
		this.agcsp = agcsp;
	}

	public Integer getAgccs() {
		return agccs;
	}

	public void setAgccs(Integer agccs) {
		this.agccs = agccs;
	}

	public Integer getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(Integer typeCode) {
		this.typeCode = typeCode;
	}

	public String getDrugrefId() {
		return drugrefId;
	}

	public void setDrugrefId(String drugrefId) {
		this.drugrefId = StringUtils.trimToNull(drugrefId);
	}

	public String getAgeOfOnset() {
		return ageOfOnset;
	}

	public void setAgeOfOnset(String ageOfOnset) {
		this.ageOfOnset = StringUtils.trimToNull(ageOfOnset);
	}

	public String getSeverityOfReaction() {
		return severityOfReaction;
	}

	public void setSeverityOfReaction(String severityOfReaction) {
		this.severityOfReaction = StringUtils.trimToNull(severityOfReaction);
	}

	public String getOnsetOfReaction() {
		return onsetOfReaction;
	}

	public void setOnsetOfReaction(String onsetOfReaction) {
		this.onsetOfReaction = StringUtils.trimToNull(onsetOfReaction);
	}

	public String getRegionalIdentifier() {
		return regionalIdentifier;
	}

	public void setRegionalIdentifier(String regionalIdentifier) {
		this.regionalIdentifier = StringUtils.trimToNull(regionalIdentifier);
	}

	public String getLifeStage() {
		return lifeStage;
	}

	public void setLifeStage(String lifeStage) {
		this.lifeStage = StringUtils.trimToNull(lifeStage);
	}

	public int getPosition() {
    	return (position);
    }

	public void setPosition(int position) {
    	this.position = position;
    }

	public Date getLastUpdateDate() {
    	return (lastUpdateDate);
    }

	public void setLastUpdateDate(Date lastUpdateDate) {
    	this.lastUpdateDate = lastUpdateDate;
    }

	public String getProviderNo() {
    	return (providerNo);
    }

	public void setProviderNo(String providerNo) {
    	this.providerNo = StringUtils.trimToNull(providerNo);
    }

	public boolean isIntegratorResult() {
    	return integratorResult;
    }

	public void setIntegratorResult(boolean integratorResult) {
    	this.integratorResult = integratorResult;
    }


	public String getStartDateFormat() {
    	return startDateFormat;
    }

	public void setStartDateFormat(String startDateFormat) {
    	this.startDateFormat = startDateFormat;
    }

	@PreUpdate
	@PrePersist
	protected void autoSetUpdateTime()
	{
		lastUpdateDate=new Date();
	}


    public String getShortDesc(int maxlength, int shorted, String added){
        String desc = this.getDescription();
        if( (maxlength > shorted) && (desc.length() > maxlength) ){
           desc = desc.substring(0, 8) + "..." ;
        }
        return desc;
	}

	public String getLifeStageDesc()
	{
		String s = getLifeStage();
		if(LIFESTAGE_CODE_NEWBORN.equals(s))
		{
			return LIFESTAGE_DESC_NEWBORN;
		}
		else if (LIFESTAGE_CODE_INFANT.equals(s))
		{
			return LIFESTAGE_DESC_INFANT;
		}
		else if (LIFESTAGE_CODE_CHILD.equals(s))
		{
			return LIFESTAGE_DESC_CHILD;
		}
		else if (LIFESTAGE_CODE_ADOLESCENT.equals(s))
		{
			return LIFESTAGE_DESC_ADOLESCENT;
		}
		else if (LIFESTAGE_CODE_ADULT.equals(s))
		{
			return LIFESTAGE_DESC_ADULT;
		}
		return "Not Set";
	}

	/**
	 * Expression language tags (${}) can't directly access static methods, and we'd like
	 * to be able to expose the mappings to our JSPs.
	 * @param lifeStage code for the life stage of the allergy
	 * @return oscar resource property corresponding to the code given
	 */
	public String getDescForLifeStageCode(String lifeStage, Locale locale)
	{
		ResourceBundle resourceBundle = ResourceBundle.getBundle("oscarResources", locale);

		switch(lifeStage)
		{
			case LIFESTAGE_CODE_NEWBORN:
				return resourceBundle.getString("oscarEncounter.lifestage.opt.newborn");
			case LIFESTAGE_CODE_INFANT:
				return resourceBundle.getString("oscarEncounter.lifestage.opt.infant");
			case LIFESTAGE_CODE_CHILD:
				return resourceBundle.getString("oscarEncounter.lifestage.opt.child");
			case LIFESTAGE_CODE_ADOLESCENT:
				return resourceBundle.getString("oscarEncounter.lifestage.opt.adolescent");
			case LIFESTAGE_CODE_ADULT:
				return resourceBundle.getString("oscarEncounter.lifestage.opt.adult");
			default:
				return resourceBundle.getString("oscarEncounter.lifestage.opt.notset");
		}
	}

	public String getOnSetOfReactionDesc() {
    	return Allergy.getOnSetOfReactionDesc(this.getOnsetOfReaction());
    }

	/**
	 * Expression language tags (${}) can't directly access static methods, and we'd like
	 * to be able to expose the mappings to our JSPs.
	 * @param onsetCode onset code that we want the textual description for
	 * @return textual description corresponding to onset code, Unknown if we can't find it
	 */
	public String getDescForOnsetCode(String onsetCode)
	{
		return Allergy.getOnSetOfReactionDesc(onsetCode);
	}

	public static String getOnSetOfReactionDesc(String onsetCode)
	{
		if (ONSET_CODE_IMMEDIATE.equals(onsetCode))
		{
			return ONSET_DESC_IMMEDIATE;
		}
		if (ONSET_CODE_GRADUAL.equals(onsetCode))
		{
			return ONSET_DESC_GRADUAL;
		}
		if (ONSET_CODE_SLOW.equals(onsetCode))
		{
			return ONSET_DESC_SLOW;
		}
		return ONSET_DESC_UNKNOWN;
	 }

    public String getTypeDesc() {
    	return Allergy.getTypeDesc(this.getTypeCode());
    }

	/*
	 *|  8 | anatomical class
	 *|  9 | chemical class
	 *| 10 | therapeutic class
	 *| 11 | generic
	 *| 12 | composite generic
	 *| 13 | branded product
	 *| 14 | ingredient
	 */
	public static String getTypeDesc(int typeCode) {
        String s;
        /** 6 |  1 | generic
            7 |  2 | compound
            8 |  3 | brandname
            9 |  4 | ther_class
           10 |  5 | chem_class
           13 |  6 | ingredient
        **/
        switch(typeCode) {
            case 11:
                s = TYPECODE_DESC_GENERIC;
                break;
            case 12:
                s = TYPECODE_DESC_COMPOUND;
                break;
            case 13:
                s = TYPECODE_DESC_BRAND;
                break;
            case 8:
                s = TYPECODE_DESC_ATC;
                break;
            case 10:
                s = TYPECODE_DESC_AFHS;
                break;
            case 14:
                s = TYPECODE_DESC_INGREDIENT;
                break;
            default:
                s = "";
        }
        return s;
    }

    public String getSeverityOfReactionDesc() {
    	return Allergy.getSeverityOfReactionDesc(this.getSeverityOfReaction());
    }

	/**
	 * Expression language tags (${}) can't directly access static methods, and we'd like
	 * to be able to expose the mappings to our JSPs.
	 * @param severityOfReaction severity code we want the textual description for
	 * @return the textual description for the severity code, or Unknown if we can't map it
	 */
	public String getDescForSeverityValue(String severityOfReaction)
	{
		return Allergy.getSeverityOfReactionDesc(severityOfReaction);
	}

	//TODO-legacy: NEEDS I18N
	public static String getSeverityOfReactionDesc(String severityCode)
	{
		if (SEVERITY_CODE_MILD.equals(severityCode))
		{
			return SEVERITY_DESC_MILD;
		}
		if (SEVERITY_CODE_MODERATE.equals(severityCode))
		{
			return SEVERITY_DESC_MODERATE;
		}
		if (SEVERITY_CODE_SEVERE.equals(severityCode))
		{
			return SEVERITY_DESC_SEVERE;
		}

		return SEVERITY_DESC_UNKNOWN;
	}

    public String getAuditString() {
        return getAllergyDisp();
    }

    public String getAllergyDisp() {
        return this.getDescription() + " (" + getTypeDesc(getTypeCode()) + ")";
    }

}
