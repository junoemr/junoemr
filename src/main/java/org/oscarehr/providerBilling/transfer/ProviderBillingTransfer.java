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

package org.oscarehr.providerBilling.transfer;


import org.oscarehr.providerBilling.model.ProviderBilling;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

public class ProviderBillingTransfer implements Serializable
{
	private String providerNo;
	private String bcRuralRetentionCode;
	private String bcRuralRetentionName;
	private String bcServiceLocationCode;
	private Boolean bcBCPEligible;
	private String onMasterNumber;
	private String onServiceLocation;
	private String abSourceCode;
	private String abSkillCode;
	private String abLocationCode;
	private Integer abBANumber;
	private Integer abFacilityNumber;
	private String abFunctionalCenter;
	private String abTimeRoleModifier;
	private Integer skMode;
	private String  skLocation;
	private String skSubmissionType;
	private String skCorporationIndicator;

	public static ProviderBillingTransfer toTransferObj(ProviderBilling source)
	{
		if (source == null )
		{
			return null;
		}

		ProviderBillingTransfer transfer = new ProviderBillingTransfer();
		BeanUtils.copyProperties(source, transfer, "id", "provider");
		transfer.setProviderNo(source.getProvider().getId());

		return transfer;
	}

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
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


