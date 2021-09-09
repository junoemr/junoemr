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

import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

@Entity
@Table(name = "measurements")
public class Measurement extends AbstractModel<Integer> implements Serializable {

	public static final int RESULT_LENGTH = 500;

	public static final String MEASUREMENT_TYPE_HEIGHT = "HT";
	public static final String MEASUREMENT_TYPE_WEIGHT = "WT";
	public static final String MEASUREMENT_TYPE_WAIST = "WAIS";
	public static final String MEASUREMENT_TYPE_WC = "WC";
	public static final String MEASUREMENT_TYPE_BLOOD_PRESSURE = "BP";
	public static final String MEASUREMENT_TYPE_MINI_MENTAL_STATE_EXAM = "MMSE";
	public static final String MEASUREMENT_TYPE_SMOKING_STATUS = "SKST";
	public static final String MEASUREMENT_TYPE_SMOKING_PACKS_PER_DAY = "POSK";
	public static final String MEASUREMENT_TYPE_SELF_MONITOR_BLOOD_GLUCOSE = "SMBG";
	public static final String MEASUREMENT_TYPE_DIABETES_EDUCATION = "DMME";
	public static final String MEASUREMENT_TYPE_DIABETES_SELF_MANAGEMENT_CHALLENGES = "SMCD";
	public static final String MEASUREMENT_TYPE_DIABETES_MOTIVATION_COUNSELING_COMPLETED_NUTRITION = "MCCN";
	public static final String MEASUREMENT_TYPE_DIABETES_MOTIVATION_COUNSELING_COMPLETED_EXERCISE = "MCCE";
	public static final String MEASUREMENT_TYPE_DIABETES_MOTIVATION_COUNSELING_COMPLETED_SMOKING = "MCCS";
	public static final String MEASUREMENT_TYPE_DIABETES_MOTIVATION_COUNSELING_COMPLETED_OTHER = "MCCO";
	public static final String MEASUREMENT_TYPE_DILATED_EYE_EXAM = "EYEE";
	public static final String MEASUREMENT_TYPE_FOOT_EXAM = "FTE";
	public static final String MEASUREMENT_TYPE_FOOT_EXAM_TEST_LOSS_OF_SENSATION = "FTLS";
	public static final String MEASUREMENT_TYPE_COLLABORATIVE_GOAL_SETTING = "CGSD";
	public static final String MEASUREMENT_TYPE_HYPOGLYCEMIC_EPISODES = "HYPE";

	public static final String MEASUREMENT_UNIT_CM = "cm";
	public static final String MEASUREMENT_UNIT_KG = "kg";
	public static final String MEASUREMENT_UNIT_MMHG = "mmHg";
	public static final String MEASUREMENT_CODE_DIABETES_SELF_MANAGEMENT_COLLABORATIVE = "44943-9";
	public static final String MEASUREMENT_CODE_DIABETES_SELF_MANAGEMENT_CHALLENGES = "44941-3";
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "type")
	private String type;

	@Column(name = "demographicNo")
	private Integer demographicId;
	
	@Column(name = "providerNo")	
	private String providerNo;
	
	@Column(name = "dataField", nullable=false, length=255)
	private String dataField = "";
	
	@Column(name = "measuringInstruction", length=255)
	private String measuringInstruction = "";
	
	@Column(name = "comments", length=255)
	private String comments = "";

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dateObserved")
	private Date dateObserved = new Date();

	@Column(name = "appointmentNo")
	private Integer appointmentNo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dateEntered")
	private Date createDate = new Date();

	@PreUpdate
	protected void jpaPreventChange() {
		throw (new UnsupportedOperationException("This action is not allowed for this type of item."));
	}

	@Override
	public Integer getId() {
		return id;
	}

	public String getType() {
		return (type);
	}

	public void setType(String type) {
		this.type = StringUtils.trimToEmpty(type);
	}

	public Integer getDemographicId() {
		return (demographicId);
	}

	public void setDemographicId(Integer demographicId) {
		this.demographicId = demographicId;
	}

	public String getProviderNo() {
		return (providerNo);
	}

	public void setProviderNo(String providerNo) {
		this.providerNo = StringUtils.trimToNull(providerNo);
	}

	public String getDataField() {
		return (dataField);
	}

	public void setDataField(String dataField) {
		this.dataField = dataField;
	}

	public String getMeasuringInstruction() {
		return (measuringInstruction);
	}

	public void setMeasuringInstruction(String measuringInstruction) {
		this.measuringInstruction = StringUtils.trimToEmpty(measuringInstruction);
	}

	public String getComments() {
		return (comments);
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getDateObserved() {
		return (dateObserved);
	}

	public void setDateObserved(Date dateObserved) {
		this.dateObserved = dateObserved;
	}

	public Integer getAppointmentNo() {
		return (appointmentNo);
	}

	public void setAppointmentNo(Integer appointmentNo) {
		this.appointmentNo = appointmentNo;
	}

	public Date getCreateDate() {
		return (createDate);
	}


	public void setCreateDate(Date createDate)
	{
		this.createDate = createDate;
	}

	public static final Comparator<Measurement> DateObservedComparator = new Comparator<Measurement>()
	{
		public int compare(Measurement o1, Measurement o2)
		{
			if(o1.getId() != null && o2.getId() != null)
			{
				return o1.getDateObserved().compareTo(o2.getDateObserved());
			}
			return 0;
		}
	};
}
