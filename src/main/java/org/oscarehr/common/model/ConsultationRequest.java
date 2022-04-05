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


package org.oscarehr.common.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "consultationRequests")
public class ConsultationRequest extends AbstractModel<Integer> implements Serializable {

	private static final Logger logger = MiscUtils.getLogger();

	public static final String STATUS_ACTIVE = "1";
	public static final String STATUS_PEND_SPECIAL = "2";
	public static final String STATUS_PEND_PATIENT = "3";
	public static final String STATUS_COMPLETE = "4";
	public static final String STATUS_CANCEL = "5";
	public static final String STATUS_APPT_BOOK = "6";
	public static final String STATUS_DELETE = "7";

	public static final String URGENCY_URGENT = "1";
	public static final String URGENCY_NON_URGENT = "2";
	public static final String URGENCY_RETURN = "3";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "requestId")
	@Getter
	@Setter
	private Integer id;
	
	@Column(name = "referalDate")
        @Temporal(TemporalType.DATE)
	private Date referralDate;
	
	private Integer serviceId;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "specId")
	private ProfessionalSpecialist professionalSpecialist;

	@Temporal(TemporalType.DATE)
	private Date appointmentDate;	
	@Temporal(TemporalType.TIME)
	private Date appointmentTime;

	@Column(name = "reason")
	private String reasonForReferral;
	
	private String clinicalInfo;
	private String currentMeds;
	private String allergies;
	private String providerNo;

	@Column(name = "demographicNo")
	private Integer demographicId;

	private String status = STATUS_ACTIVE;
	private String statusText;
	private String sendTo;
	private String concurrentProblems;
	private String urgency;
	private boolean patientWillBook;	
	
	@Column(name = "site_name")
	private String siteName;
        
    @Temporal(TemporalType.DATE)
    private Date followUpDate;
    @Column(name = "signature_img")
    private String signatureImg;
    private String letterheadName;
    private String letterheadAddress;
    private String letterheadPhone;
    private String letterheadFax;
	@Column(name = "notification_sent", columnDefinition = "TINYINT(1)", nullable = false)
	private boolean notificationSent = false;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateDate;
    
    private Integer fdid = null;
    private String source;
    

	public Date getReferralDate() {
    	return referralDate;
    }

	public void setReferralDate(Date referralDate) {
    	this.referralDate = referralDate;
    }

	public Integer getServiceId() {
    	return serviceId;
    }

	public void setServiceId(Integer serviceId) {
    	this.serviceId = serviceId;
    }

	public Date getAppointmentDate() {
    	return appointmentDate;
    }

	public void setAppointmentDate(Date appointmentDate) {
    	this.appointmentDate = appointmentDate;
    }

	public Date getAppointmentTime() {
    	return appointmentTime;
    }

	public void setAppointmentTime(Date appointmentTime) {
    	this.appointmentTime = appointmentTime;
    }

	public String getReasonForReferral() {
    	return reasonForReferral;
    }

	public void setReasonForReferral(String reasonForReferral) {
    	this.reasonForReferral = StringUtils.trimToNull(reasonForReferral);
    }

	public String getClinicalInfo() {
    	return clinicalInfo;
    }

	public void setClinicalInfo(String clinicalInfo) {
    	this.clinicalInfo = StringUtils.trimToNull(clinicalInfo);
    }

	public String getCurrentMeds() {
    	return currentMeds;
    }

	public void setCurrentMeds(String currentMeds) {
    	this.currentMeds = StringUtils.trimToNull(currentMeds);
    }

	public String getAllergies() {
    	return allergies;
    }

	public void setAllergies(String allergies) {
    	this.allergies = StringUtils.trimToNull(allergies);
    }

	public String getProviderNo() {
    	return providerNo;
    }

	public void setProviderNo(String providerNo) {
    	this.providerNo = StringUtils.trimToNull(providerNo);
    }

	public Integer getDemographicId() {
    	return demographicId;
    }

	public void setDemographicId(Integer demographicId) {
    	this.demographicId = demographicId;
    }

	public String getStatus() {
    	return status;
    }

	public void setStatus(String status) {
    	this.status = StringUtils.trimToNull(status);
    }

	public String getStatusText() {
    	return statusText;
    }

	public void setStatusText(String statusText) {
    	this.statusText = StringUtils.trimToNull(statusText);
    }

	public String getSendTo() {
    	return sendTo;
    }

	public void setSendTo(String sendTo) {
    	this.sendTo = StringUtils.trimToNull(sendTo);
    }

	public String getConcurrentProblems() {
    	return concurrentProblems;
    }

	public void setConcurrentProblems(String concurrentProblems) {
    	this.concurrentProblems = StringUtils.trimToNull(concurrentProblems);
    }

	public String getUrgency() {
    	return urgency;
    }

	public void setUrgency(String urgency) {
    	this.urgency = StringUtils.trimToNull(urgency);
    }
	
	public String getSiteName() {
    	return siteName;
    }

	public void setSiteName(String siteName) {
    	this.siteName = siteName;
    }

	public boolean isPatientWillBook() {
    	return patientWillBook;
    }

	public void setPatientWillBook(boolean patientWillBook) {
    	this.patientWillBook = patientWillBook;
    }

    /**
     * @return the followUpDate
     */
    public Date getFollowUpDate() {
        return followUpDate;
    }

    /**
     * @param followUpDate the followUpDate to set
     */
    public void setFollowUpDate(Date followUpDate) {
        this.followUpDate = followUpDate;
    }

    /**
     * @return the professionalSpecialist
     */
    public ProfessionalSpecialist getProfessionalSpecialist() {
        return professionalSpecialist;
}

    /**
     * @param professionalSpecialist the professionalSpecialist to set
     */
    public void setProfessionalSpecialist(ProfessionalSpecialist professionalSpecialist) {
        this.professionalSpecialist = professionalSpecialist;
    }

    public Integer getSpecialistId() {
    	if(professionalSpecialist != null)
    		return this.professionalSpecialist.getId();
    	else
    		return null;
    }

	public String getSignatureImg() {
	    return signatureImg;
    }

	public void setSignatureImg(String signatureImg) {
	    this.signatureImg = signatureImg;
    }

	public String getLetterheadName() {
	    return letterheadName;
    }

	public void setLetterheadName(String letterheadName) {
	    this.letterheadName = letterheadName;
    }

	public String getLetterheadAddress() {
	    return letterheadAddress;
    }

	public void setLetterheadAddress(String letterheadAddress) {
	    this.letterheadAddress = letterheadAddress;
    }

	public String getLetterheadPhone() {
	    return letterheadPhone;
    }

	public void setLetterheadPhone(String letterheadPhone) {
	    this.letterheadPhone = letterheadPhone;
    }

	public String getLetterheadFax() {
	    return letterheadFax;
    }

	public void setLetterheadFax(String letterheadFax) {
	    this.letterheadFax = letterheadFax;
    }
	
	
	public Integer getFdid() {
		return fdid;
	}

	public void setFdid(Integer fdid) {
		this.fdid = fdid;
	}
	
	

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public boolean isNotificationSent()
	{
		return notificationSent;
	}

	public void setNotificationSent(boolean notificationSent)
	{
		this.notificationSent = notificationSent;
	}

	@PrePersist
	@PreUpdate
	protected void jpa_updateLastDateUpdated() {
		lastUpdateDate = new Date();
	}

	public Date getAppointmentDateTime()
	{
		Date appointmentDateTime = null;
		try
		{
			Date appointmentDate = getAppointmentDate();
			Date appointmentTime = getAppointmentTime();
			if(appointmentDate != null)
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(appointmentDate);

				// if there is no appointment time, still want to return the date
				if(appointmentTime != null)
				{
					Calendar acal = Calendar.getInstance();
					acal.setTime(appointmentTime);
					cal.set(Calendar.HOUR_OF_DAY, acal.get(Calendar.HOUR_OF_DAY));
					cal.set(Calendar.MINUTE, acal.get(Calendar.MINUTE));
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
				}
				appointmentDateTime = cal.getTime();
			}
		}
		catch(Exception e)
		{
			logger.error("Invalid appointment datetime", e);
		}
		return appointmentDateTime;
	}

	public static List<String> getCompletedStatusList()
	{
		return Arrays.asList(STATUS_COMPLETE, STATUS_CANCEL, STATUS_DELETE);
	}
}
