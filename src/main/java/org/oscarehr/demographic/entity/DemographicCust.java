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


package org.oscarehr.demographic.entity;

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.common.model.AbstractModel;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@Entity
@Table(name="demographiccust")
public class DemographicCust extends AbstractModel<Integer>
{

	@Id
	@Column(name="demographic_no")
	private Integer id;

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
	@JoinColumn(name="demographic_no", insertable=false, updatable=false)
	private Demographic demographic;

	public DemographicCust()
	{
	}
	public DemographicCust(Integer id)
	{
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNurse() {
    	return nurse;
    }

	public void setNurse(String nurse) {
    	this.nurse = nurse;
    }

	public String getResident() {
    	return resident;
    }

	public void setResident(String resident) {
    	this.resident = resident;
    }

	public String getAlert() {
    	return alert;
    }

	public void setAlert(String alert) {
    	this.alert = alert;
    }

	public String getMidwife() {
    	return midwife;
    }

	public void setMidwife(String midwife) {
    	this.midwife = midwife;
    }

	public String getNotes() {
    	return notes;
    }

	public void setNotes(String notes) {
    	this.notes = notes;
    }

	public String getParsedNotes()
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(notes)));

			NodeList list = document.getElementsByTagName("unotes");

			if (list != null && list.getLength() > 0)
			{
				return list.item(0).getTextContent();
			}

		} catch (Exception e)
		{
		}

		return null;
	}

	public void setParsedNotes(String notes)
	{
		this.notes = "<unotes>" + StringUtils.trimToEmpty(notes) + "</unotes>";
	}

	public Demographic getDemographic()
	{
		return demographic;
	}

	public void setDemographic(Demographic demographic)
	{
		this.demographic = demographic;
	}
}
