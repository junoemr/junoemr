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
package org.oscarehr.integration.iceFall.service.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IceFallCustomerTo1 implements Serializable
{
	@JsonProperty("customerid")
	private Integer customerId;
	@JsonProperty("registrationno")
	private String registrationNo;
	@JsonProperty("firstname")
	private String firstName;
	@JsonProperty("lastname")
	private String lastName;
	@JsonProperty("dobirth")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dateOfBirth;
	private String gender;
	@JsonProperty("raddress1")
	private String address1;
	@JsonProperty("raddress2")
	private String address2;
	@JsonProperty("rcity")
	private String city;
	@JsonProperty("rprov")
	private String province;
	@JsonProperty("rpostalcode")
	private String postalCode;
	@JsonProperty("saddress1")
	private String shippingAddress1;
	@JsonProperty("saddress2")
	private String shippingAddress2;
	@JsonProperty("scity")
	private String shippingCity;
	@JsonProperty("sprov")
	private String shippingProvince;
	@JsonProperty("spostalcode")
	private String shippingPostalCode;
	@JsonProperty("ship_address_type")
	private String shipAddressType;
	@JsonProperty("establishment_id")
	private String establishmentId;
	private String email;
	private String phone;
	@JsonProperty("maddress1")
	private String mailingAddress1;
	@JsonProperty("maddress2")
	private String mailingAddress2;
	@JsonProperty("mcity")
	private String mailingCity;
	@JsonProperty("mprov")
	private String mailingProvince;
	@JsonProperty("mpostalcode")
	private String mailingPostalCode;
	private String sdmid;// wut this?
	@JsonProperty("registration_status")
	private List<String> registrationStatuses;
	private String status;
	@JsonProperty("prescription")
	private List<IceFallPrescriptionTo1> prescriptions;
	@JsonProperty("order_history")
	private List<IceFallOrderHistoryTo1> orders;

	public Integer getCustomerId()
	{
		return customerId;
	}

	public void setCustomerId(Integer customerId)
	{
		this.customerId = customerId;
	}

	public String getRegistrationNo()
	{
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo)
	{
		this.registrationNo = registrationNo;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public LocalDate getDateOfBirth()
	{
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	public String getAddress1()
	{
		return address1;
	}

	public void setAddress1(String address1)
	{
		this.address1 = address1;
	}

	public String getAddress2()
	{
		return address2;
	}

	public void setAddress2(String address2)
	{
		this.address2 = address2;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getProvince()
	{
		return province;
	}

	public void setProvince(String province)
	{
		this.province = province;
	}

	public String getPostalCode()
	{
		return postalCode;
	}

	public void setPostalCode(String postalCode)
	{
		this.postalCode = postalCode;
	}

	public String getShippingAddress1()
	{
		return shippingAddress1;
	}

	public void setShippingAddress1(String shippingAddress1)
	{
		this.shippingAddress1 = shippingAddress1;
	}

	public String getShippingAddress2()
	{
		return shippingAddress2;
	}

	public void setShippingAddress2(String shippingAddress2)
	{
		this.shippingAddress2 = shippingAddress2;
	}

	public String getShippingCity()
	{
		return shippingCity;
	}

	public void setShippingCity(String shippingCity)
	{
		this.shippingCity = shippingCity;
	}

	public String getShippingProvince()
	{
		return shippingProvince;
	}

	public void setShippingProvince(String shippingProvince)
	{
		this.shippingProvince = shippingProvince;
	}

	public String getShippingPostalCode()
	{
		return shippingPostalCode;
	}

	public void setShippingPostalCode(String shippingPostalCode)
	{
		this.shippingPostalCode = shippingPostalCode;
	}

	public String getShipAddressType()
	{
		return shipAddressType;
	}

	public void setShipAddressType(String shipAddressType)
	{
		this.shipAddressType = shipAddressType;
	}

	public String getEstablishmentId()
	{
		return establishmentId;
	}

	public void setEstablishmentId(String establishmentId)
	{
		this.establishmentId = establishmentId;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getMailingAddress1()
	{
		return mailingAddress1;
	}

	public void setMailingAddress1(String mailingAddress1)
	{
		this.mailingAddress1 = mailingAddress1;
	}

	public String getMailingAddress2()
	{
		return mailingAddress2;
	}

	public void setMailingAddress2(String mailingAddress2)
	{
		this.mailingAddress2 = mailingAddress2;
	}

	public String getMailingCity()
	{
		return mailingCity;
	}

	public void setMailingCity(String mailingCity)
	{
		this.mailingCity = mailingCity;
	}

	public String getMailingProvince()
	{
		return mailingProvince;
	}

	public void setMailingProvince(String mailingProvince)
	{
		this.mailingProvince = mailingProvince;
	}

	public String getMailingPostalCode()
	{
		return mailingPostalCode;
	}

	public void setMailingPostalCode(String mailingPostalCode)
	{
		this.mailingPostalCode = mailingPostalCode;
	}

	public String getSdmid()
	{
		return sdmid;
	}

	public void setSdmid(String sdmid)
	{
		this.sdmid = sdmid;
	}

	public List<String> getRegistrationStatuses()
	{
		return registrationStatuses;
	}

	public void setRegistrationStatuses(List<String> registrationStatuses)
	{
		this.registrationStatuses = registrationStatuses;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public List<IceFallPrescriptionTo1> getPrescriptions()
	{
		return prescriptions;
	}

	public void setPrescriptions(List<IceFallPrescriptionTo1> prescriptions)
	{
		this.prescriptions = prescriptions;
	}

	public List<IceFallOrderHistoryTo1> getOrders()
	{
		return orders;
	}

	public void setOrders(List<IceFallOrderHistoryTo1> orders)
	{
		this.orders = orders;
	}
}
