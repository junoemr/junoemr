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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="demographicSets")
public class DemographicSets extends AbstractModel<Integer> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="demographic_no")
	private Demographic demographic;

	@Column(insertable = false, updatable = false)
	private Integer demographic_no;

	@Column(name="set_name")
	private String name;

	private String eligibility;

	private String archive;

	public Integer getId() {
    	return id;
    }

	public void setId(Integer id) {
    	this.id = id;
    }

	public int getDemographicNo() {
    	return demographic.getDemographicNo();
    }
	public Demographic getDemographic() {
		return demographic;
	}
	public void setDemographic(Demographic d) {
		demographic = d;
	}

	public String getName() {
    	return name;
    }

	public void setName(String name) {
    	this.name = name;
    }

	public String getEligibility() {
    	return eligibility;
    }

	public void setEligibility(String eligibility) {
    	this.eligibility = eligibility;
    }

	public String getArchive() {
    	return archive;
    }

	public void setArchive(String archive) {
    	this.archive = archive;
    }

	public Integer getDemographic_no()
	{
		return demographic_no;
	}

	public void setDemographic_no(Integer demographic_no)
	{
		this.demographic_no = demographic_no;
	}
}
