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

package org.oscarehr.ticklers.model;

import org.oscarehr.common.model.AbstractModel;

import javax.persistence.EmbeddedId;

public class CDMTicklerInfo extends AbstractModel<CDMTicklerInfoCompositeKey>
{
    @EmbeddedId
    private CDMTicklerInfoCompositeKey id;
    // demographic_no, dxResearchCode, billingStatus is a candidate key
    // demographic_no, billingServiceCode, billingStatus is another

    private String billingServiceCode;
    private String providerNo;
    private String date;
    private String ticklerNo;

    @Override
    public CDMTicklerInfoCompositeKey getId()
    {
        return id;
    }

    public void setId(CDMTicklerInfoCompositeKey id)
    {
        this.id = id;
    }

    public String getBillingServiceCode()
    {
        return billingServiceCode;
    }

    public void setBillingServiceCode(String billingServiceCode)
    {
        this.billingServiceCode = billingServiceCode;
    }

    public String getProviderNo()
    {
        return providerNo;
    }

    public void setProviderNo(String providerNo)
    {
        this.providerNo = providerNo;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getTicklerNo()
    {
        return ticklerNo;
    }

    public void setTicklerNo(String ticklerNo)
    {
        this.ticklerNo = ticklerNo;
    }

    public String getDemographicNo()
    {
        return this.id.getDemographicNo();
    }

    public void setDemographicNo(String demographicNo)
    {
        this.id.setDemographicNo(demographicNo);
    }

    public String getdxResearchCode()
    {
        return this.id.getDxResearchCode();
    }

    public void setDxResearchCode(String dxResearchCode)
    {
        this.id.setDxResearchCode(dxResearchCode);
    }

    public String getBillingStatus()
    {
        return this.id.getBillingStatus();
    }

    public void setBillingStatus(String billingStatus)
    {
        this.id.setBillingStatus(billingStatus);
    }
}
