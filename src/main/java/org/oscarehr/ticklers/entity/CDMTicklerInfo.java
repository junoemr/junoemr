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

package org.oscarehr.ticklers.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CDMTicklerInfo
{
    private int demographicNo;
    private String providerNo;
    private String dxCode;
    private String billingCode;
    private LocalDate billingDate;
    private Integer ticklerNo;

    public CDMTicklerInfo(){}

    public CDMTicklerInfo(Object[] dbResult)
    {
        this.demographicNo = (Integer)dbResult[0];

        if (dbResult[1] != null) // check to avoid an unboxing NullPointerException if the demographic doesn't have a provider set.
        {

            this.providerNo = (String)dbResult[1];
        }

        this.dxCode = (String)dbResult[2];


        // Depending on if the CDM was billed or not, the following values may be null

        if (dbResult[3] != null)
        {
            this.billingCode = (String)dbResult[3];
        }

        if (dbResult[4] != null)
        {
            DateTimeFormatter dateFormat = DateTimeFormatter.BASIC_ISO_DATE;
            this.billingDate = LocalDate.parse((String)dbResult[4], dateFormat);
        }

        if (dbResult[5] != null)
        {
            this.ticklerNo = (Integer) dbResult[5];
        }
    }

    public int getDemographicNo()
    {
        return demographicNo;
    }

    public void setDemographicNo(int demographicNo)
    {
        this.demographicNo = demographicNo;
    }

    public String getProviderNo()
    {
        return providerNo;
    }

    public void setProviderNo(String providerNo)
    {
        this.providerNo = providerNo;
    }

    public String getDxCode()
    {
        return dxCode;
    }

    public void setDxCode(String dxCode)
    {
        this.dxCode = dxCode;
    }

    public String getBillingCode()
    {
        return billingCode;
    }

    public void setBillingCode(String billingCode)
    {
        this.billingCode = billingCode;
    }


    public LocalDate getBillingDate()
    {
        return billingDate;
    }

    public void setBillingDate(LocalDate billingDate)
    {
        this.billingDate = billingDate;
    }

    public Integer getTicklerNo()
    {
        return ticklerNo;
    }

    public void setTicklerNo(Integer ticklerNo)
    {
        this.ticklerNo = ticklerNo;
    }
}
