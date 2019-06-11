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
package org.oscarehr.ws.external.soap.v1.transfer;

import org.oscarehr.dashboard.model.BillingMasterClinicaid;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * transfer object for importing billing data from Clinicaid vai SOAP api for use in dashboard reports.
 */
public final class BillingTransfer extends TransferBase<BillingMasterClinicaid>
{
	private Integer 	id;
	private Integer 	billingNo = 0;
	private Date 		createDate;
	private String 		billingStatus;
	private Integer 	demographicNo;
	private Integer 	appointmentNo;
	private String 		claimCode;
	private String 		dataCenter;
	private String 		payeeNo;
	private String		practitionerNo;
	private String 		phn;
	private String 		nameVerify;
	private String		dependentNum;
	private String		billingUnit;
	private String		clarificationCode;
	private String		anatomicalArea;
	private String		afterHour;
	private String		newProgram;
	private String		billingCode;
	private String		billAmount;
	private String		paymentMode;
	private String		serviceDate;
	private String		serviceToDay;
	private String		submissionCode;
	private String		extendedSubmissionCode;
	private String		dxCode1;
	private String		dxCode2;
	private String		dxCode3;
	private String		dxExpansion;
	private String		serviceLocation;
	private String		referralFlag1;
	private String		referralNo1;
	private String	 	referralFlag2;
	private String		referralNo2;
	private String		timeCall;
	private String		serviceStartTime;
	private String		serviceEndTime;
	private String 		birthDate;
	private String		officeNumber;
	private String		correspondenceCode;
	private String		claimComment;
	private String		mvaClaimCode;
	private String		icbcClaimNo;
	private String		originalClaim;
	private String		facilityNo;
	private String		facilitySubNo;
	private String		fillerClaim;
	private String		oinInsurerCode;
	private String		oinRegistrationNo;
	private String 		oinBirthDate;
	private String		oinFirstName;
	private String		oinSecondName;
	private String		oinSurname;
	private String		oinSexCode;
	private String		oinAddress;
	private String		oinAddress2;
	private String		oinAddress3;
	private String		oinAddress4;
	private String		oinPostalCode;
	private Integer 	paymentMethod;
	private Integer		wcbId;
	private Integer		sequenceNo;
	private Integer 	invoiceCreationYear;
	private String 		dataCenterNo;


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

	public void setPhn(String phn)
	{
		this.phn = phn;
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

	public void setDataCenterNo(String dataCenterNo)
	{
		this.dataCenterNo = dataCenterNo;
	}


	@Override
	public BillingMasterClinicaid copyTo(BillingMasterClinicaid copyTo)
	{
		String[] ignored = {"id"};
		BeanUtils.copyProperties(this, copyTo, ignored);

		return copyTo;
	}
}
