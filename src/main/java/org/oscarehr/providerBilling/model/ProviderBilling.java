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
package org.oscarehr.providerBilling.model;


import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.provider.model.ProviderData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "provider_billing")
public class ProviderBilling extends AbstractModel<Integer>
{
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id = null;

	@OneToOne(mappedBy="billingOpts")
	private ProviderData provider;

	@Column(name = "bc_rural_retention_code")
	private String bcRuralRetentionCode;

	@Column(name = "bc_rural_retention_name")
	private String bcRuralRetentionName;

	@Column(name = "bc_service_location_code")
	private String bcServiceLocationCode;

	@Column(name = "bc_bcp_eligible")
	private Boolean bcBCPEligible = false;

	@Column(name = "on_master_number")
	private String onMasterNumber;

	@Column(name = "on_service_location")
	private String onServiceLocation;

	@Column(name = "ab_source_code")
	private String abSourceCode;

	@Column(name = "ab_skill_code")
	private String abSkillCode;

	@Column(name = "ab_location_code")
	private String abLocationCode;

	@Column(name = "ab_BA_number")
	private Integer abBANumber;

	@Column(name = "ab_facility_number")
	private Integer abFacilityNumber;

	@Column(name = "ab_functional_center")
	private String abFunctionalCenter;

	@Column(name = "ab_time_role_modifier")
	private String abTimeRoleModifier;

	@Column(name = "sk_mode")
	private Integer skMode;

	@Column(name = "sk_location")
	private String  skLocation;

	@Column(name = "sk_submission_type")
	private String skSubmissionType;

	@Column(name = "sk_corporation_indicator")
	private String skCorporationIndicator;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Integer getProviderNo()
	{
		return this.provider.getProviderNo();
	}

	public ProviderData getProvider()
	{
		return provider;
	}

	public void setProvider(ProviderData provider)
	{
		this.provider = provider;
	}


	public String getBcRuralRetentionCode()
	{
		return bcRuralRetentionCode;
	}

	public void setBcRuralRetentionCode(String bcRuralRetentionCode)
	{
		this.bcRuralRetentionCode = bcRuralRetentionCode;
	}

	public String getBcRuralRetentionName()
	{
		return bcRuralRetentionName;
	}

	public void setBcRuralRetentionName(String bcRuralRetentionName)
	{
		this.bcRuralRetentionName = bcRuralRetentionName;
	}

	public String getBcServiceLocationCode()
	{
		return bcServiceLocationCode;
	}

	public void setBcServiceLocationCode(String bcServiceLocationCode)
	{
		this.bcServiceLocationCode = bcServiceLocationCode;
	}

	public Boolean getBcBCPEligible()
	{
		return bcBCPEligible;
	}

	public void setBcBCPEligible(Boolean bcBCPEligible)
	{
		this.bcBCPEligible = bcBCPEligible;
	}

	public String getOnMasterNumber()
	{
		return onMasterNumber;
	}

	public void setOnMasterNumber(String onMasterNumber)
	{
		this.onMasterNumber = onMasterNumber;
	}

	public String getOnServiceLocation()
	{
		return onServiceLocation;
	}

	public void setOnServiceLocation(String onServiceLocation)
	{
		this.onServiceLocation = onServiceLocation;
	}

	public String getAbSourceCode()
	{
		return abSourceCode;
	}

	public void setAbSourceCode(String abSourceCode)
	{
		this.abSourceCode = abSourceCode;
	}

	public String getAbSkillCode()
	{
		return abSkillCode;
	}

	public void setAbSkillCode(String abSkillCode)
	{
		this.abSkillCode = abSkillCode;
	}

	public String getAbLocationCode()
	{
		return abLocationCode;
	}

	public void setAbLocationCode(String abLocationCode)
	{
		this.abLocationCode = abLocationCode;
	}

	public Integer getAbBANumber()
	{
		return abBANumber;
	}

	public void setAbBANumber(Integer abBANumber)
	{
		this.abBANumber = abBANumber;
	}

	public Integer getAbFacilityNumber()
	{
		return abFacilityNumber;
	}

	public void setAbFacilityNumber(Integer abFacilityNumber)
	{
		this.abFacilityNumber = abFacilityNumber;
	}

	public String getAbFunctionalCenter()
	{
		return abFunctionalCenter;
	}

	public void setAbFunctionalCenter(String abFunctionalCenter)
	{
		this.abFunctionalCenter = abFunctionalCenter;
	}

	public String getAbTimeRoleModifier()
	{
		return abTimeRoleModifier;
	}

	public void setAbTimeRoleModifier(String abTimeRoleModifier)
	{
		this.abTimeRoleModifier = abTimeRoleModifier;
	}

	public Integer getSkMode()
	{
		return skMode;
	}

	public void setSkMode(Integer skMode)
	{
		this.skMode = skMode;
	}

	public String getSkLocation()
	{
		return skLocation;
	}

	public void setSkLocation(String skLocation)
	{
		this.skLocation = skLocation;
	}

	public String getSkSubmissionType()
	{
		return skSubmissionType;
	}

	public void setSkSubmissionType(String skSubmissionType)
	{
		this.skSubmissionType = skSubmissionType;
	}

	public String getSkCorporationIndicator()
	{
		return skCorporationIndicator;
	}

	public void setSkCorporationIndicator(String skCorporationIndicator)
	{
		this.skCorporationIndicator = skCorporationIndicator;
	}
}
