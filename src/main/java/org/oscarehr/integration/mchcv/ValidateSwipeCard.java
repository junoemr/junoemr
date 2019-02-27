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
package org.oscarehr.integration.mchcv;

public class ValidateSwipeCard extends org.apache.struts.action.ActionForm {

    private String magneticStripe;
    private String lastName;
    private String firstName;
    private String hin;
    private String hinVer;
    private String sex;

    private String dobYear;
    private String dobMonth;
    private String dobDay;

    private String effYear;
    private String effMonth;
    private String effDay;

    private String endYear;
    private String endMonth;
    private String endDay;

    public ValidateSwipeCard() {
        super();
    }

    public String getMagneticStripe() {
        return magneticStripe;
    }

    public void setMagneticStripe(String magneticStripe) {
        this.magneticStripe = magneticStripe;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getHin()
    {
        return hin;
    }

    public void setHin(String hin)
    {
        this.hin = hin;
    }

    public String getHinVer()
    {
        return hinVer;
    }

    public void setHinVer(String hinVer)
    {
        this.hinVer = hinVer;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getDobYear()
    {
        return dobYear;
    }

    public String getDobYYYYMMdd()
    {
        return this.dobYear + this.dobMonth + this.dobDay;
    }

    public void setDobYear(String dobYear)
    {
        this.dobYear = dobYear;
    }

    public String getDobMonth()
    {
        return dobMonth;
    }

    public void setDobMonth(String dobMonth)
    {
        this.dobMonth = dobMonth;
    }

    public String getDobDay()
    {
        return dobDay;
    }

    public void setDobDay(String dobDay)
    {
        this.dobDay = dobDay;
    }

    public String getEffYYYYMMdd()
    {
        return this.getEffYear() + this.getEffMonth() + this.getEffDay();
    }

    public String getEffYear()
    {
        return effYear;
    }

    public void setEffYear(String effYear)
    {
        this.effYear = effYear;
    }

    public String getEffMonth()
    {
        return effMonth;
    }

    public void setEffMonth(String effMonth)
    {
        this.effMonth = effMonth;
    }

    public String getEffDay()
    {
        return effDay;
    }

    public void setEffDay(String effDay)
    {
        this.effDay = effDay;
    }

    public String getEndYYYYMMdd()
    {
        return this.getEndYear() + this.getEndMonth() + this.getEndDay();
    }

    public String getEndYear()
    {
        return endYear;
    }

    public void setEndYear(String endYear)
    {
        this.endYear = endYear;
    }

    public String getEndMonth()
    {
        return endMonth;
    }

    public void setEndMonth(String endMonth)
    {
        this.endMonth = endMonth;
    }

    public String getEndDay()
    {
        return endDay;
    }

    public void setEndDay(String endDay)
    {
        this.endDay = endDay;
    }
}