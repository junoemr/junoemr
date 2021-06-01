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

package org.oscarehr.site.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Data
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore properties that are not defined in this class
public class SiteTransfer implements Serializable
{

	private Integer siteId;
	private String name;
	private String shortName;
	private String phone;
	private String fax;
	private String bgColor;
	private String address;
	private String city;
	private String province;
	private String postal;
	private Integer providerIdFrom;
	private Integer providerIdTo;
	private byte status;
	private Integer siteLogoId = null;
	private String siteUrl = "";

	private String bcFacilityNumber;
	private String bcServiceLocationCode;
}
