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

package org.oscarehr.common.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.SpringUtils;

/**
 * This is the object class that relates to the provider table. Any customizations belong here.
 * @deprecated - use the JPA version instead
 */
@Deprecated
public class Provider implements Serializable, Comparable<Provider>
{
	/**
	 * default serial version id for serializable
	 */
	private static final long serialVersionUID = 1L;

	public static final String SYSTEM_PROVIDER_NO = "-1";
	public static final String UNCLAIMED_PROVIDER_NO = "0";

	// providerTypes
	public static final String PROVIDER_TYPE_DOCTOR 		= "doctor";
	public static final String PROVIDER_TYPE_RECEPTIONIST 	= "receptionist";
	public static final String PROVIDER_TYPE_NURSE			= "nurse";
	public static final String PROVIDER_TYPE_RESIDENT 		= "resident";
	public static final String PROVIDER_TYPE_MIDWIFE		= "midwife";
	public static final String PROVIDER_TYPE_ADMIN			= "admin";

	private String providerNo;
	private String comments;
	private String phone;
	private String billingNo;
	private String workPhone;
	private String address;
	private String team;

	/**
	 * "1"=active "0"=inactive
	 */
	private String status;
	private String lastName;
	private String providerType;
	private String sex;
	private String ohipNo;
	private String specialty;
	private Date dob;
	private String hsoNo;
	private String providerActivity;
	private String firstName;
	private String rmaNo;
	private Date SignedConfidentiality;
	private String practitionerNo;
	private String email;
	private String title;
	private String lastUpdateUser;
	private Date lastUpdateDate = new Date();
	private String supervisor;

	/* -- Province specific (AB) -- */
	private String albertaTakNo;
	private String albertaEDeliveryIds;
	private String albertaConnectCareId;

	/* -- Province specific (ON) -- */
	private String ontarioLifeLabsId;

	private Boolean superAdmin = false;
	private String imdHealthUuid;

	// constructors
	public Provider()
	{
	}

	/**
	 * Constructor for primary key
	 */
	public Provider(String providerNo) {
		this.setProviderNo(providerNo);
	}

	/**
	 * Constructor for required fields
	 */
	public Provider(String providerNo, String lastName, String providerType, String sex, String specialty, String firstName)
	{
		this.setProviderNo(providerNo);
		this.setLastName(lastName);
		this.setProviderType(providerType);
		this.setSex(sex);
		this.setSpecialty(specialty);
		this.setFirstName(firstName);
	}
	
	public Provider(Provider provider)
	{
		providerNo = provider.providerNo;
		comments = provider.comments;
		phone = provider.phone;
		billingNo = provider.billingNo;
		workPhone = provider.workPhone;
		address = provider.address;
		team = provider.team;
		status = provider.status;
		lastName = provider.lastName;
		providerType = provider.providerType;
		sex = provider.sex;
		ohipNo = provider.ohipNo;
		specialty = provider.specialty;
		dob = provider.dob;
		hsoNo = provider.hsoNo;
		providerActivity = provider.providerActivity;
		firstName = provider.firstName;
		rmaNo = provider.rmaNo;
		SignedConfidentiality = provider.SignedConfidentiality;
		practitionerNo = provider.practitionerNo;
		email = provider.email;
		title = provider.title;
		lastUpdateUser = provider.lastUpdateUser;
		lastUpdateDate = provider.lastUpdateDate;
		supervisor = provider.supervisor;
		albertaTakNo = provider.albertaTakNo;
		superAdmin = provider.superAdmin;
	}

	/** same as getDisplayName */
	public String getFormattedName() {
		return getDisplayName();
	}

	/** returns a formatted name String in the form of 'first_name, last_name' */
	public String getDisplayName()
	{
		String lastName  = (getLastName()  == null) ? "" : getLastName().trim();
		String firstName = (getFirstName() == null) ? "" : getFirstName().trim();
		if(!lastName.isEmpty() && !firstName.isEmpty())
		{
			lastName += ", ";
		}
		return lastName + firstName;
	}

	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}

	public String getPractitionerNo() {
		return practitionerNo;
	}
	public void setPractitionerNo(String practitionerNo) {
		this.practitionerNo = practitionerNo;
	}

	public String getProviderNo() {
		return providerNo;
	}
	public void setProviderNo(String providerNo) {
		this.providerNo = providerNo;
	}

	public ProviderData convertToProviderData()
	{
		ProviderDataDao providerDataDao = SpringUtils.getBean(ProviderDataDao.class);
		return providerDataDao.findByProviderNo(this.providerNo);
	}


	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBillingNo() {
		return billingNo;
	}
	public void setBillingNo(String billingNo) {
		this.billingNo = billingNo;
	}

	public String getWorkPhone() {
		return workPhone;
	}
	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isActive() {
		return this.status.equals("1");
	}

	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
    * @deprecated no longer is use 2010-04-23, marked for future removal
	 */
	public String getProviderType() {
		return providerType;
	}
	public void setProviderType(String providerType) {
		this.providerType = providerType;
	}

	public boolean isProviderTypeDoctor()
	{
		return this.providerType.equals(PROVIDER_TYPE_DOCTOR);
	}
	public boolean isProviderTypeReceptionist()
	{
		return this.providerType.equals(PROVIDER_TYPE_RECEPTIONIST);
	}
	public boolean isProviderTypeNurse()
	{
		return this.providerType.equals(PROVIDER_TYPE_NURSE);
	}
	public boolean isProviderTypeResident()
	{
		return this.providerType.equals(PROVIDER_TYPE_RESIDENT);
	}
	public boolean isProviderTypeMidwife()
	{
		return this.providerType.equals(PROVIDER_TYPE_MIDWIFE);
	}
	public boolean isProviderTypeAdmin()
	{
		return this.providerType.equals(PROVIDER_TYPE_ADMIN);
	}

	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getOhipNo() {
		return ohipNo;
	}
	public void setOhipNo(String ohipNo) {
		this.ohipNo = ohipNo;
	}

	public String getSpecialty() {
		return specialty;
	}
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	public java.util.Date getDob() {
		return dob;
	}
	public void setDob(java.util.Date dob) {
		this.dob = dob;
	}

	public String getHsoNo() {
		return hsoNo;
	}
	public void setHsoNo(String hsoNo) {
		this.hsoNo = hsoNo;
	}

	public String getProviderActivity() {
		return providerActivity;
	}
	public void setProviderActivity(String providerActivity) {
		this.providerActivity = providerActivity;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getRmaNo() {
		return rmaNo;
	}
	public void setRmaNo(String rmaNo) {
		this.rmaNo = rmaNo;
	}

    public String getEmail() {
    	return email;
    }
	public void setEmail(String email) {
    	this.email = email;
    }

	public Date getSignedConfidentiality() {
        return this.SignedConfidentiality;
    }
    public void setSignedConfidentiality( Date SignedConfidentiality )
	{
        this.SignedConfidentiality = SignedConfidentiality;
    }

	public String getTitle() {
    	return title;
    }
	public void setTitle(String title) {
    	this.title = title;
    }

	public String getLastUpdateUser() {
    	return lastUpdateUser;
    }
	public void setLastUpdateUser(String lastUpdateUser) {
    	this.lastUpdateUser = lastUpdateUser;
    }

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	public String getSupervisor() {
		return this.supervisor;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public void setSuperAdmin(boolean superAdminStatus) {
		superAdmin = superAdminStatus;
	}
	public Boolean getSuperAdmin() {
		return superAdmin;
	}
	
	/* -- Province specific getters/setters -- */
	
	public String getAlbertaTakNo() {
		return albertaTakNo;
	}
	public void setAlbertaTakNo(String takNo) {
		albertaTakNo = takNo;
	}

	public String getAlbertaEDeliveryIds() {
		return albertaEDeliveryIds;
	}
	public void setAlbertaEDeliveryIds(String albertaEDeliveryIds)
	{
		this.albertaEDeliveryIds = StringUtils.trimToNull(albertaEDeliveryIds);
	}

	public String getAlbertaConnectCareId()
	{
		return albertaConnectCareId;
	}
	public void setAlbertaConnectCareId(String albertaConnectCareId)
	{
		this.albertaConnectCareId = albertaConnectCareId;
	}

	public String getOntarioLifeLabsId()
	{
		return ontarioLifeLabsId;
	}
	public void setOntarioLifeLabsId(String ontarioLifeLabsId)
	{
		this.ontarioLifeLabsId = ontarioLifeLabsId;
	}

	public String getImdHealthUuid()
	{
		return imdHealthUuid;
	}
	public void setImdHealthUuid(String imdHealthUuid)
	{
		this.imdHealthUuid = imdHealthUuid;
	}

	/* -- other methods --*/
	public ComparatorName ComparatorName() {
		return new ComparatorName();
	}

	public boolean equals(Provider provider)
	{
		try
		{
			return (providerNo.equals(provider.providerNo));
		}
		catch (Exception e)
		{
			return (false);
		}
	}

	@Override
    public int hashCode()
	{
		if (providerNo == null)
		{
			return(super.hashCode());
		}
		else
		{
			return(providerNo.hashCode());
		}
	}

	public class ComparatorName implements Comparator<Provider>, Serializable
	{
		/**
		 * default serial version id for serializable
		 */
		private static final long serialVersionUID = 1L;

		public int compare(Provider o1, Provider o2)
		{
			Provider bp1 = o1;
			Provider bp2 = o2;
			String lhs = bp1.getLastName() + bp1.getFirstName();
			String rhs = bp2.getLastName() + bp2.getFirstName();

			return lhs.compareTo(rhs);
		}
	}

	public int compareTo(Provider o)
	{
		if (providerNo == null)
		{
			return(0);
		}
	    return (providerNo.compareTo(o.providerNo));
    }
}
