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
package org.oscarehr.dashboard.model;

import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "billingmaster_clinicaid")
public class BillingMasterClinicaid extends AbstractModel<Integer> implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="billingmaster_no")
	private Integer id;
	@Column(name="billing_no", nullable = false, columnDefinition="int default 0")
	private Integer 	billingNo;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="createdate")
	private Date createDate;
	@Column(name="billingstatus")
	private String 		billingStatus;
	@Column(name="demographic_no")
	private Integer 	demographicNo;
	@Column(name="appointment_no")
	private Integer 	appointmentNo;
	@Column(name="claimcode")
	private String 		claimCode;
	@Column(name="datacenter")
	private String 		dataCenter;
	@Column(name="payee_no")
	private String 		payeeNo;
	@Column(name="practitioner_no")
	private String		practitionerNo;
	@Column(name="phn")
	private String 		phn;
	@Column(name="name_verify")
	private String 		nameVerify;
	@Column(name="dependent_num")
	private String		dependentNum;
	@Column(name="billing_unit")
	private String		billingUnit;
	@Column(name="clarification_code")
	private String		clarificationCode;
	@Column(name="anatomical_area")
	private String		anatomicalArea;
	@Column(name="after_hour")
	private String		afterHour;
	@Column(name="new_program")
	private String		newProgram;
	@Column(name="billing_code")
	private String		billingCode;
	@Column(name="bill_amount")
	private String		billAmount;
	@Column(name="payment_mode")
	private String		paymentMode;
	@Column(name="service_date")
	private String		serviceDate;
	@Column(name="service_to_day")
	private String		serviceToDay;
	@Column(name="submission_code")
	private String		submissionCode;
	@Column(name="extended_submission_code")
	private String		extendedSubmissionCode;
	@Column(name="dx_code1")
	private String		dxCode1;
	@Column(name="dx_code2")
	private String		dxCode2;
	@Column(name="dx_code3")
	private String		dxCode3;
	@Column(name="dx_expansion")
	private String		dxExpansion;
	@Column(name="service_location")
	private String		serviceLocation;
	@Column(name="referral_flag1")
	private String		referralFlag1;
	@Column(name="referral_no1")
	private String		referralNo1;
	@Column(name="referral_flag2")
	private String	 	referralFlag2;
	@Column(name="referral_no2")
	private String		referralNo2;
	@Column(name="time_call")
	private String		timeCall;
	@Column(name="service_start_time")
	private String		serviceStartTime;
	@Column(name="service_end_time")
	private String		serviceEndTime;
	@Column(name="birth_date")
	private String 		birthDate;
	@Column(name="office_number")
	private String		officeNumber;
	@Column(name="correspondence_code")
	private String		correspondenceCode;
	@Column(name="claim_comment")
	private String		claimComment;
	@Column(name="mva_claim_code")
	private String		mvaClaimCode;
	@Column(name="icbc_claim_no")
	private String		icbcClaimNo;
	@Column(name="original_claim")
	private String		originalClaim;
	@Column(name="facility_no")
	private String		facilityNo;
	@Column(name="facility_sub_no")
	private String		facilitySubNo;
	@Column(name="filler_claim")
	private String		fillerClaim;
	@Column(name="oin_insurer_code")
	private String		oinInsurerCode;
	@Column(name="oin_registration_no")
	private String		oinRegistrationNo;
	@Column(name="oin_birthdate")
	private String 		oinBirthDate;
	@Column(name="oin_first_name")
	private String		oinFirstName;
	@Column(name="oin_second_name")
	private String		oinSecondName;
	@Column(name="oin_surname")
	private String		oinSurname;
	@Column(name="oin_sex_code")
	private String		oinSexCode;
	@Column(name="oin_address")
	private String		oinAddress;
	@Column(name="oin_address2")
	private String		oinAddress2;
	@Column(name="oin_address3")
	private String		oinAddress3;
	@Column(name="oin_address4")
	private String		oinAddress4;
	@Column(name="oin_postalcode")
	private String		oinPostalCode;
	@Column(name="paymentMethod")
	private Integer 	paymentMethod;
	@Column(name="wcb_id")
	private Integer		wcbId;
	@Column(name="sequence_no")
	private Integer 	sequenceNo;
	@Column(name="invoice_creation_year")
	private Integer		invoiceCreationYear;
	@Column(name="data_center_no")
	private String 		dataCenterNo;


	@Override
	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Integer getBillingNo()
	{
		return billingNo;
	}

	public void setBillingNo(Integer billingNo)
	{
		this.billingNo = billingNo;
	}

	public Date getCreateDate()
	{
		return createDate;
	}

	public void setCreateDate(Date createDate)
	{
		this.createDate = createDate;
	}

	public String getBillingStatus()
	{
		return billingStatus;
	}

	public void setBillingStatus(String billingStatus)
	{
		this.billingStatus = billingStatus;
	}

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public Integer getAppointmentNo()
	{
		return appointmentNo;
	}

	public void setAppointmentNo(Integer appointmentNo)
	{
		this.appointmentNo = appointmentNo;
	}

	public String getClaimCode()
	{
		return claimCode;
	}

	public void setClaimCode(String claimCode)
	{
		this.claimCode = claimCode;
	}

	public String getDataCenter()
	{
		return dataCenter;
	}

	public void setDataCenter(String dataCenter)
	{
		this.dataCenter = dataCenter;
	}

	public String getPayeeNo()
	{
		return payeeNo;
	}

	public void setPayeeNo(String payeeNo)
	{
		this.payeeNo = payeeNo;
	}

	public String getPractitionerNo()
	{
		return practitionerNo;
	}

	public void setPractitionerNo(String practitionerNo)
	{
		this.practitionerNo = practitionerNo;
	}

	public String getPhn()
	{
		return phn;
	}

	public void setPhn(String PHN)
	{
		this.phn = PHN;
	}

	public String getNameVerify()
	{
		return nameVerify;
	}

	public void setNameVerify(String nameVerify)
	{
		this.nameVerify = nameVerify;
	}

	public String getDependentNum()
	{
		return dependentNum;
	}

	public void setDependentNum(String dependentNum)
	{
		this.dependentNum = dependentNum;
	}

	public String getBillingUnit()
	{
		return billingUnit;
	}

	public void setBillingUnit(String billingUnit)
	{
		this.billingUnit = billingUnit;
	}

	public String getClarificationCode()
	{
		return clarificationCode;
	}

	public void setClarificationCode(String clarificationCode)
	{
		this.clarificationCode = clarificationCode;
	}

	public String getAnatomicalArea()
	{
		return anatomicalArea;
	}

	public void setAnatomicalArea(String anatomicalArea)
	{
		this.anatomicalArea = anatomicalArea;
	}

	public String getAfterHour()
	{
		return afterHour;
	}

	public void setAfterHour(String afterHour)
	{
		this.afterHour = afterHour;
	}

	public String getNewProgram()
	{
		return newProgram;
	}

	public void setNewProgram(String newProgram)
	{
		this.newProgram = newProgram;
	}

	public String getBillingCode()
	{
		return billingCode;
	}

	public void setBillingCode(String billingCode)
	{
		this.billingCode = billingCode;
	}

	public String getBillAmount()
	{
		return billAmount;
	}

	public void setBillAmount(String billAmount)
	{
		this.billAmount = billAmount;
	}

	public String getPaymentMode()
	{
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode)
	{
		this.paymentMode = paymentMode;
	}

	public String getServiceDate()
	{
		return serviceDate;
	}

	public void setServiceDate(String serviceDate)
	{
		this.serviceDate = serviceDate;
	}

	public String getServiceToDay()
	{
		return serviceToDay;
	}

	public void setServiceToDay(String serviceToDay)
	{
		this.serviceToDay = serviceToDay;
	}

	public String getSubmissionCode()
	{
		return submissionCode;
	}

	public void setSubmissionCode(String submissionCode)
	{
		this.submissionCode = submissionCode;
	}

	public String getExtendedSubmissionCode()
	{
		return extendedSubmissionCode;
	}

	public void setExtendedSubmissionCode(String extendedSubmissionCode)
	{
		this.extendedSubmissionCode = extendedSubmissionCode;
	}

	public String getDxCode1()
	{
		return dxCode1;
	}

	public void setDxCode1(String dxCode1)
	{
		this.dxCode1 = dxCode1;
	}

	public String getDxCode2()
	{
		return dxCode2;
	}

	public void setDxCode2(String dxCode2)
	{
		this.dxCode2 = dxCode2;
	}

	public String getDxCode3()
	{
		return dxCode3;
	}

	public void setDxCode3(String dxCode3)
	{
		this.dxCode3 = dxCode3;
	}

	public String getDxExpansion()
	{
		return dxExpansion;
	}

	public void setDxExpansion(String dxExpansion)
	{
		this.dxExpansion = dxExpansion;
	}

	public String getServiceLocation()
	{
		return serviceLocation;
	}

	public void setServiceLocation(String serviceLocation)
	{
		this.serviceLocation = serviceLocation;
	}

	public String getReferralFlag1()
	{
		return referralFlag1;
	}

	public void setReferralFlag1(String referralFlag1)
	{
		this.referralFlag1 = referralFlag1;
	}

	public String getReferralNo1()
	{
		return referralNo1;
	}

	public void setReferralNo1(String referralNo1)
	{
		this.referralNo1 = referralNo1;
	}

	public String getReferralFlag2()
	{
		return referralFlag2;
	}

	public void setReferralFlag2(String referralFlag2)
	{
		this.referralFlag2 = referralFlag2;
	}

	public String getReferralNo2()
	{
		return referralNo2;
	}

	public void setReferralNo2(String referralNo2)
	{
		this.referralNo2 = referralNo2;
	}

	public String getTimeCall()
	{
		return timeCall;
	}

	public void setTimeCall(String timeCall)
	{
		this.timeCall = timeCall;
	}

	public String getServiceStartTime()
	{
		return serviceStartTime;
	}

	public void setServiceStartTime(String serviceStartTime)
	{
		this.serviceStartTime = serviceStartTime;
	}

	public String getServiceEndTime()
	{
		return serviceEndTime;
	}

	public void setServiceEndTime(String serviceEndTime)
	{
		this.serviceEndTime = serviceEndTime;
	}

	public String getBirthDate()
	{
		return birthDate;
	}

	public void setBirthDate(String birthDate)
	{
		this.birthDate = birthDate;
	}

	public String getOfficeNumber()
	{
		return officeNumber;
	}

	public void setOfficeNumber(String officeNumber)
	{
		this.officeNumber = officeNumber;
	}

	public String getCorrespondenceCode()
	{
		return correspondenceCode;
	}

	public void setCorrespondenceCode(String correspondenceCode)
	{
		this.correspondenceCode = correspondenceCode;
	}

	public String getClaimComment()
	{
		return claimComment;
	}

	public void setClaimComment(String claimComment)
	{
		this.claimComment = claimComment;
	}

	public String getMvaClaimCode()
	{
		return mvaClaimCode;
	}

	public void setMvaClaimCode(String mvaClaimCode)
	{
		this.mvaClaimCode = mvaClaimCode;
	}

	public String getIcbcClaimNo()
	{
		return icbcClaimNo;
	}

	public void setIcbcClaimNo(String icbcClaimNo)
	{
		this.icbcClaimNo = icbcClaimNo;
	}

	public String getOriginalClaim()
	{
		return originalClaim;
	}

	public void setOriginalClaim(String originalClaim)
	{
		this.originalClaim = originalClaim;
	}

	public String getFacilityNo()
	{
		return facilityNo;
	}

	public void setFacilityNo(String facilityNo)
	{
		this.facilityNo = facilityNo;
	}

	public String getFacilitySubNo()
	{
		return facilitySubNo;
	}

	public void setFacilitySubNo(String facilitySubNo)
	{
		this.facilitySubNo = facilitySubNo;
	}

	public String getFillerClaim()
	{
		return fillerClaim;
	}

	public void setFillerClaim(String fillerClaim)
	{
		this.fillerClaim = fillerClaim;
	}

	public String getOinInsurerCode()
	{
		return oinInsurerCode;
	}

	public void setOinInsurerCode(String oinInsurerCode)
	{
		this.oinInsurerCode = oinInsurerCode;
	}

	public String getOinRegistrationNo()
	{
		return oinRegistrationNo;
	}

	public void setOinRegistrationNo(String oinRegistrationNo)
	{
		this.oinRegistrationNo = oinRegistrationNo;
	}

	public String getOinBirthDate()
	{
		return oinBirthDate;
	}

	public void setOinBirthDate(String oinBirthDate)
	{
		this.oinBirthDate = oinBirthDate;
	}

	public String getOinFirstName()
	{
		return oinFirstName;
	}

	public void setOinFirstName(String oinFirstName)
	{
		this.oinFirstName = oinFirstName;
	}

	public String getOinSecondName()
	{
		return oinSecondName;
	}

	public void setOinSecondName(String oinSecondName)
	{
		this.oinSecondName = oinSecondName;
	}

	public String getOinSurname()
	{
		return oinSurname;
	}

	public void setOinSurname(String oinSurname)
	{
		this.oinSurname = oinSurname;
	}

	public String getOinSexCode()
	{
		return oinSexCode;
	}

	public void setOinSexCode(String oinSexCode)
	{
		this.oinSexCode = oinSexCode;
	}

	public String getOinAddress()
	{
		return oinAddress;
	}

	public void setOinAddress(String oinAddress)
	{
		this.oinAddress = oinAddress;
	}

	public String getOinAddress2()
	{
		return oinAddress2;
	}

	public void setOinAddress2(String oinAddress2)
	{
		this.oinAddress2 = oinAddress2;
	}

	public String getOinAddress3()
	{
		return oinAddress3;
	}

	public void setOinAddress3(String oinAddress3)
	{
		this.oinAddress3 = oinAddress3;
	}

	public String getOinAddress4()
	{
		return oinAddress4;
	}

	public void setOinAddress4(String oinAddress4)
	{
		this.oinAddress4 = oinAddress4;
	}

	public String getOinPostalCode()
	{
		return oinPostalCode;
	}

	public void setOinPostalCode(String oinPostalCode)
	{
		this.oinPostalCode = oinPostalCode;
	}

	public Integer getPaymentMethod()
	{
		return paymentMethod;
	}

	public void setPaymentMethod(Integer paymentMethod)
	{
		this.paymentMethod = paymentMethod;
	}

	public Integer getWcbId()
	{
		return wcbId;
	}

	public void setWcbId(Integer wcbId)
	{
		this.wcbId = wcbId;
	}

	public Integer getSequenceNo()
	{
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo)
	{
		this.sequenceNo = sequenceNo;
	}

	public Integer getInvoiceCreationYear()
	{
		return invoiceCreationYear;
	}

	public void setInvoiceCreationYear(Integer invoiceCreationYear)
	{
		this.invoiceCreationYear = invoiceCreationYear;
	}

	public String getDataCenterNo()
	{
		return dataCenterNo;
	}

	public void setDataCenterNo(String dataCenterNumber)
	{
		this.dataCenterNo = dataCenterNumber;
	}
}
