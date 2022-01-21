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

package org.oscarehr.integration.imdhealth.transfer.outbound;

import lombok.Data;
import org.apache.commons.validator.EmailValidator;
import org.oscarehr.common.Gender;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.integration.imdhealth.exception.SSOLoginException;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;

@Data
public class SSOPatient implements Serializable {

    private Integer age;        // optional
    private boolean consent;    // optional
    private String email;       // required
    private String gender;      // optional

    // Gender types permitted by IMDHealth
    private enum AllowedGenderTypes
    {
        Female("female"),
        Male("male"),
        Other("other");

        private final String text;

        AllowedGenderTypes(String text)
        {
            this.text = text;
        }

        public String toText()
        {
            return this.text;
        }
    }

    /**
     * Initialize an SSO Patient from a demographic.
     *
     * @param demographic Demographic to instantiate from
     * @return SSOPatient suitable for SSO login
     * @throws SSOLoginException if this object is instantiated from a demographic without an email
     */
    public static SSOPatient fromDemographic(Demographic demographic) throws SSOLoginException
    {
        SSOPatient patient = new SSOPatient();

        if (!EmailValidator.getInstance().isValid(demographic.getEmail())) {
            throw new SSOLoginException("Demographic must have an valid email address: [" + demographic.getEmail() + "]");
        }

        patient.email = demographic.getEmail();

        patient.consent = demographic.getElectronicMessagingConsentStatus()
                .equals(Demographic.ELECTRONIC_MESSAGING_CONSENT_STATUS.CONSENTED);

        if (demographic.getDateOfBirth() != null)
        {
            patient.age = Period.between(demographic.getDateOfBirth(), LocalDate.now()).getYears();
        }

        Gender gender = Gender.fromLetterCode(demographic.getSex());

        if (gender.equals(Gender.M))
        {
            patient.gender = AllowedGenderTypes.Male.toText();
        }
        else if (gender.equals(Gender.F))
        {
            patient.gender = AllowedGenderTypes.Female.toText();
        }
        else
        {
            patient.gender = AllowedGenderTypes.Other.toText();
        }

        return patient;
    }

    /**
     * Check if the demographic can be converted into a valid SSODemographic object.
     * A demographic is valid if it has an email address with a valid format.
     *
     * @param demographic demographic to check
     * @return true if demographic can be converted
     */
    public static boolean canMapDemographic(Demographic demographic)
    {
        return demographic != null && EmailValidator.getInstance().isValid(demographic.getEmail());
    }

}
