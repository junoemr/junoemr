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


package org.oscarehr.provider.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.providerBilling.model.ProviderBilling;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "provider")
@Getter
@Setter
public class ProviderData extends AbstractModel<String> implements Serializable
{
	public static final String SYSTEM_PROVIDER_NO = "-1";

	// tags used to extend the provider record in the comments field.
	public static final String COMMENT_CELL_TAG 					= "xml_p_cell";
	public static final String COMMENT_FAX_TAG 						= "xml_p_fax";
	public static final String COMMENT_PAGER_TAG 					= "xml_p_pager";
	public static final String COMMENT_OTHER_PHONE_TAG 		= "xml_p_phone2";
	public static final String COMMENT_ON_SPECIALITY_CODE = "xml_p_specialty_code";
	public static final String COMMENT_ON_BILLING_GROUP_NO = "xml_p_billinggroup_no";

	public static final String PROVIDER_STATUS_ACTIVE		= "1";
	public static final String PROVIDER_STATUS_INACTIVE = "0";


	// providerTypes
	public static final String PROVIDER_TYPE_DOCTOR 		= "doctor";
	public static final String PROVIDER_TYPE_RECEPTIONIST 	= "receptionist";
	public static final String PROVIDER_TYPE_NURSE			= "nurse";
	public static final String PROVIDER_TYPE_RESIDENT 		= "resident";
	public static final String PROVIDER_TYPE_MIDWIFE		= "midwife";
	public static final String PROVIDER_TYPE_ADMIN			= "admin";

	/**
	 * default serial version id for serializable
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "provider_no")
	private String id = null;

	@Column(name = "last_name")
	private String lastName = null;

	@Column(name = "first_name")
	private String firstName = null;

	@Column(name = "provider_type")
	private String providerType = null;

	@Column(name = "specialty")
	private String specialty = null;

	@Column(name = "team")
	private String team = null;

	@Column(name = "sex")
	private String sex = null;

	@Column(name = "dob")
	@Temporal(TemporalType.DATE)
	private Date dob = null;

	@Column(name = "address")
	private String address = null;

	@Column(name = "phone")
	private String phone = null;

	@Column(name = "work_phone")
	private String workPhone = null;

	@Column(name = "ohip_no")
	private String ohipNo = null;

	@Column(name = "rma_no")
	private String rmaNo = null;

	@Column(name = "billing_no")
	private String billingNo = null;

	@Column(name = "hso_no")
	private String hsoNo = null;

	@Column(name = "status")
	private String status = null;

	@Column(name = "comments")
	private String comments = null;

	@Column(name = "provider_activity")
	private String providerActivity = null;

	@Column(name = "practitionerNo")
	private String practitionerNo = null;

	@Column(name = "init")
	private String init = null;

	@Column(name = "job_title")
	private String jobTitle = null;

	@Column(name = "email")
	private String email = null;

	@Column(name = "title")
	private String title = null;

	@Column(name = "lastUpdateUser")
	private String lastUpdateUser = null;

	@Column(name = "lastUpdateDate")
	@Temporal(TemporalType.DATE)
	private Date lastUpdateDate = null;

	@Column(name = "signed_confidentiality")
	@Temporal(TemporalType.DATE)
	private Date signedConfidentiality = null;

	@Column(name = "supervisor")
    private String supervisor = null;

    @Column(name = "super_admin")
	private boolean superAdmin = false;

	/* -- Province specific -- */
	/* AB */
	@Column(name = "alberta_tak_no")
	private String albertaTakNo = null;

	@Column(name = "alberta_e_delivery_ids")
	private String albertaEDeliveryIds = null;

	@Column(name = "alberta_connect_care_id")
	private String albertaConnectCareId;

	/* ON */
	@Column(name = "ontario_lifelabs_id")
	private String ontarioLifeLabsId;

	@OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="provider_billing_id")
	private ProviderBilling billingOpts;

	@Column(name = "booking_notification_numbers")
	private String bookingNotificationNumbers;

	@Column(name = "imd_health_uuid")
	private String imdHealthUuid = null;

	public Integer getProviderNo()
	{
		return Integer.parseInt(getId());
	}
	public void setProviderNo(Integer providerNo)
	{
		set(String.valueOf(providerNo));
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

	@Override
	public String getId()
	{
		return id;
	}
	public void set(String providerNo)
	{
		id = providerNo;
	}

	public String getFaxNumber()
	{
		String comments = getComments();
		String faxNo = null;
		if (comments != null && comments.contains("<xml_p_fax>"))
		{
			faxNo = StringUtils.substringBetween(getComments(), "<xml_p_fax>", "</xml_p_fax>");
		}
		return faxNo;
	}

	public boolean equals(Object object)
	{
		if (!(object instanceof ProviderData))
		{
			return false;
		}

		ProviderData other = (ProviderData) object;
		return ProviderData.ProviderNoComparator.compare(this, other) == 0;
	}

	/* -- Province specific getters/setters -- */
	public void setAlbertaEDeliveryIds(String albertaEDeliveryIds)
	{
		this.albertaEDeliveryIds = StringUtils.trimToNull(albertaEDeliveryIds);
	}
	public void setAlbertaEDeliveryIds(List<String> idList)
	{
		setAlbertaEDeliveryIds(StringUtils.join(idList, ","));
	}
        
	/* -- Comparators -- */
	public static final Comparator<ProviderData> LastNameComparator = new Comparator<ProviderData>()
	{
        public int compare(ProviderData pd1, ProviderData pd2)
		{
        	return pd1.getLastName().compareTo(pd2.getLastName());
        }
    };

	public static final Comparator<ProviderData> FirstNameComparator = new Comparator<ProviderData>()
	{
        public int compare(ProviderData pd1, ProviderData pd2)
		{
        	return pd1.getFirstName().compareTo(pd2.getFirstName());
        }
    };

	public static final Comparator<ProviderData> ProviderNoComparator = new Comparator<ProviderData>()
	{
        public int compare(ProviderData pd1, ProviderData pd2)
		{
        	return pd1.getId().compareTo(pd2.getId());
        }
    };

	public List<String> getBookingNotificationNumbersList()
	{
		if (this.bookingNotificationNumbers != null)
		{
			return Arrays.stream(this.bookingNotificationNumbers.split(","))
					.map(String::trim)
					.filter((str) -> !str.isEmpty())
					.collect(Collectors.toList());
		}
		return new ArrayList<>();
	}
	public void setBookingNotificationNumbersList(List<String> bookingNotificationNumbers)
	{
		this.bookingNotificationNumbers = bookingNotificationNumbers.stream().reduce("", (String acc, String str) -> acc + "," + str);
	}
}
