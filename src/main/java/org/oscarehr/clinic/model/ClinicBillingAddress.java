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


package org.oscarehr.clinic.model;

import org.oscarehr.common.model.AbstractModel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Levi Murray
 */
@Entity
@Table(name = "clinic_billing_address")
public class ClinicBillingAddress extends AbstractModel<Integer> implements Serializable
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String billingName;
    @Column(name = "address")
    private String billingAddress;
    @Column(name = "city")
    private String billingCity;
    @Column(name = "province")
    private String billingProvince;
    @Column(name = "postal")
    private String billingPostal;
    @Column(name = "phone")
    private String billingPhone;
    @Column(name = "fax")
    private String billingFax;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    /**
     * Creates a new instance of UserProperty
     */
    public ClinicBillingAddress()
    {
    }

    public String getBillingName()
    {
        return billingName;
    }

    public void setBillingName(String billingName)
    {
        this.billingName = billingName;
    }

    public String getBillingAddress()
    {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress)
    {
        this.billingAddress = billingAddress;
    }

    public String getBillingCity()
    {
        return billingCity;
    }

    public void setBillingCity(String billingCity)
    {
        this.billingCity = billingCity;
    }

    public String getBillingProvince()
    {
        return billingProvince;
    }

    public void setBillingProvince(String billingProvince)
    {
        this.billingProvince = billingProvince;
    }

    public String getBillingPostal()
    {
        return billingPostal;
    }

    public void setBillingPostal(String billingPostal)
    {
        this.billingPostal = billingPostal;
    }

    public String getBillingPhone()
    {
        return billingPhone;
    }

    public void setBillingPhone(String billingPhone)
    {
        this.billingPhone = billingPhone;
    }

    public String getBillingFax()
    {
        return billingFax;
    }

    public void setBillingFax(String billingFax)
    {
        this.billingFax = billingFax;
    }

    public String toString()
    {
        return "\nid" + id +
                "\nname " + billingName +
                "\nAddress " + billingAddress +
                "\nCity " + billingCity +
                "\nPostal " + billingPostal +
                "\nPhone " + billingPhone +
                "\nFax " + billingFax +
                "\nProvince " + billingProvince;
    }

}
