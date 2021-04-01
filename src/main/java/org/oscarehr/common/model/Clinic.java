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

package org.oscarehr.common.model;

import lombok.Getter;
import lombok.Setter;
import org.oscarehr.clinic.model.ClinicBillingAddress;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Jay Gallagher
 */
@Entity
@Table(name = "clinic")
@Getter
@Setter
public class Clinic extends AbstractModel<Integer> implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clinic_no")
    private Integer id;

    @Column(name = "clinic_name")
    private String clinicName;

    @Column(name = "clinic_address")
    private String clinicAddress;

    @Column(name = "clinic_city")
    private String clinicCity;

    @Column(name = "clinic_postal")
    private String clinicPostal;

    @Column(name = "clinic_phone")
    private String clinicPhone;

    @Column(name = "clinic_fax")
    private String clinicFax;

    @Column(name = "clinic_location_code")
    private String clinicLocationCode;

    private String status;

    @Column(name = "clinic_province")
    private String clinicProvince;

    @Column(name = "clinic_delim_phone")
    private String clinicDelimPhone;

    @Column(name = "clinic_delim_fax")
    private String clinicDelimFax;

    @Column(name = "email")
    private String clinicEmail;

    @Column(name = "alberta_connect_care_lab_id")
    private String albertaConnectCareLabId;

    @Column(name = "alberta_connect_care_department_id")
    private String albertaConnectCareDepartmentId;

    @Column(name = "bc_facility_number")
    private String bcFacilityNumber;

    @Column(name = "uuid")
    private String uuid;

    // foreign key
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "clinic_billing_address_id", referencedColumnName = "id")
    private ClinicBillingAddress clinicBillingAddress;

    /**
     * Creates a new instance of UserProperty
     */
    public Clinic()
    {
    }

    @Override
    public String toString()
    {
        return "clinicName " + clinicName +
                " clinicAddress  " + clinicAddress +
                " clinicCity " + clinicCity +
                " clinicPostal " + clinicPostal +
                " clinicPhone " + clinicPhone +
                "  clinicFax " + clinicFax +
                " clinicLocationCode " + clinicLocationCode +
                " status " + status +
                " clinicProvince " + clinicProvince +
                " clinicDelimPhone " + clinicDelimPhone +
                " clinicDelimFax " + clinicDelimFax +
                " clinicBillingAddress " + clinicBillingAddress;
    }
}
