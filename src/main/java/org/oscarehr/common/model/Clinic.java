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
public class Clinic extends AbstractModel<Integer> implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    @Column(name = "clinic_no")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "clinic_name")
    private String clinicName;

    @Getter
    @Setter
    @Column(name = "clinic_address")
    private String clinicAddress;

    @Getter
    @Setter
    @Column(name = "clinic_city")
    private String clinicCity;

    @Getter
    @Setter
    @Column(name = "clinic_postal")
    private String clinicPostal;

    @Getter
    @Setter
    @Column(name = "clinic_phone")
    private String clinicPhone;

    @Getter
    @Setter
    @Column(name = "clinic_fax")
    private String clinicFax;

    @Getter
    @Setter
    @Column(name = "clinic_location_code")
    private String clinicLocationCode;

    @Getter
    @Setter
    private String status;

    @Getter
    @Setter
    @Column(name = "clinic_province")
    private String clinicProvince;

    @Getter
    @Setter
    @Column(name = "clinic_delim_phone")
    private String clinicDelimPhone;

    @Getter
    @Setter
    @Column(name = "clinic_delim_fax")
    private String clinicDelimFax;

    @Getter
    @Setter
    @Column(name = "email")
    private String clinicEmail;

    @Getter
    @Setter
    @Column(name = "alberta_connect_care_lab_id")
    private String albertaConnectCareLabId;

    @Getter
    @Setter
    @Column(name = "alberta_connect_care_department_id")
    private String albertaConnectCareDepartmentId;

    @Getter
    @Setter
    @Column(name = "bc_facility_number")
    private String bcFacilityNumber;

    @Getter
    @Setter
    @Column(name = "uuid")
    private String uuid;

    // foreign key
    @Getter
    @Setter
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
