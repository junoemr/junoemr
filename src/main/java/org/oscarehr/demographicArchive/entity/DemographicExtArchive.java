/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package org.oscarehr.demographicArchive.entity;

import lombok.Getter;
import lombok.Setter;
import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.demographic.entity.DemographicExt;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "demographicExtArchive")
public class DemographicExtArchive extends AbstractModel<Integer> implements Serializable
{
	private static final long serialVersionUID = -2981357879423093412L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "demographic_no")
	private Integer demographicNo;
	@Column(name = "provider_no")
	private String providerNo;
	@Column(name = "key_val")
	private String key;
	private String value;
	@Column(name = "date_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;
	private boolean hidden;

	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="archiveId")
	private DemographicArchive demographicArchive;

	// constructors
	public DemographicExtArchive() {}

	public DemographicExtArchive(DemographicExt de) {
		if (de == null) {
			throw new IllegalArgumentException();
		}
		this.dateCreated = de.getDateCreated();
		this.demographicNo = de.getDemographicNo();
		this.providerNo = de.getProviderNo();
		this.key = de.getKey();
		this.value = de.getValue();
		this.hidden = de.isHidden();
	}

	/**
	 * Return the unique identifier of this class generator-class="native"
	 * column="id"
	 */
	@Override
	public Integer getId() {
		return id;
	}

	/**
	 * Return the value associated with the column: demographic_no
	 */
	public Integer getDemographicNo() {
		return demographicNo;
	}

	/**
	 * Set the value related to the column: demographic_no
	 * @param demographicNo the demographic_no value
	 */
	public void setDemographicNo(Integer demographicNo) {
		this.demographicNo = demographicNo;
	}

	/**
	 * Return the value associated with the column: provider_no
	 */
	public String getProviderNo() {
		return providerNo;
	}

	/**
	 * Set the value related to the column: provider_no
	 * @param providerNo the provider_no value
	 */
	public void setProviderNo(String providerNo) {
		this.providerNo = providerNo;
	}

	/**
	 * Return the value associated with the column: key_val
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Set the value related to the column: key_val
	 * @param key the key_val value
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Return the value associated with the column: value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the value related to the column: value
	 * @param value the value value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Return the value associated with the column: date_time
	 */
	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * Set the value related to the column: date_time
	 * @param dateCreated the date_time value
	 */
	public void setDateCreated(Date dateCreated)
	{
		this.dateCreated = dateCreated;
	}

	/**
	 * Return the value associated with the column: hidden
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * Set the value related to the column: hidden
	 * @param hidden the hidden value
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

}
