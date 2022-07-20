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
package org.oscarehr.demographicArchive.entity;

import lombok.Getter;
import lombok.Setter;
import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.demographic.entity.DemographicCust;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name="demographiccustArchive")
public class DemographicCustArchive extends AbstractModel<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="demographic_no")
	private Integer demographicNo;

	@Column(name="cust1")
	private String nurse;

	@Column(name="cust2")
	private String resident;

	@Column(name="cust3")
	private String alert;

	@Column(name="cust4")
	private String midwife;

	@Column(name="content")
	private String notes;

	@OneToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="archive_id")
	private DemographicArchive demographicArchive;

	
	@Override
	public Integer getId() {
		return id;
	}

	//constructors
	public DemographicCustArchive()
	{
	}

	public DemographicCustArchive(DemographicCust dc)
	{
		this.demographicNo = dc.getId();
		this.alert = dc.getAlert();
		this.midwife = dc.getMidwife();
		this.nurse = dc.getNurse();
		this.resident = dc.getResident();
		this.notes = dc.getNotes();
	}
}
