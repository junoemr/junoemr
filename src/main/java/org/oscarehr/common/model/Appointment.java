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

package org.oscarehr.common.model;

import lombok.Getter;
import lombok.Setter;
import org.oscarehr.common.annotation.SiteLocation;
import org.oscarehr.common.listeners.BeanValidationEventListener;
import org.oscarehr.integration.aqs.model.QueuedAppointmentLink;
import org.oscarehr.provider.model.ProviderData;
import oscar.util.ConversionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;

@Entity
@EntityListeners(BeanValidationEventListener.class)
@Table(name = "appointment")
public class Appointment extends AbstractModel<Integer> implements Serializable
{
	public static final int DEFAULT_APPOINTMENT_DURATION_MIN = 15;

	public static final String TODO = "t";
	public static final String CANCELLED = "C";
	public static final String BILLED = "B";
	public static final String NO_SHOW = "N";

	public static final String URGENCY_CRITICAL = "critical";

	public enum BookingSource
	{
		OSCAR,
		MYOSCAR_SELF_BOOKING
	}

	public enum ConfirmedByType
	{
		MHA_PATIENT_USER_ID,
		SECURITY_NO
	}

	public static final String DONOTBOOK = "Do_Not_Book";

	public static final String DEFAULT_REASON = "Others";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "appointment_no")
	private Integer id;

	@Column(name = "provider_no")
	private String providerNo;

	@Temporal(TemporalType.DATE)
	@Column(name = "appointment_date")
	private Date appointmentDate;

	@Temporal(TemporalType.TIME)
	@Column(name = "start_time")
	private Date startTime;

	@Temporal(TemporalType.TIME)
	@Column(name = "end_time")
	private Date endTime;

	private String name = "";

	@Column(name = "demographic_no")
	private int demographicNo;

	@Column(name = "program_id")
	private int programId;

	private String notes;
	private String reason;

	@SiteLocation
	private String location;

	private String resources = "";
	private String type = "";
	private String style;
	private String billing;
		private String status;

	@Column(name = "imported_status")
	private String importedStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createdatetime")
	private Date createDateTime = new Date();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updatedatetime")
	private Date updateDateTime = new Date();

	private String creator;

	@Column(name = "lastupdateuser")
	private String lastUpdateUser;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="lastupdateuser", referencedColumnName="provider_no", insertable=false, updatable=false)
	private ProviderData lastUpdateUserRecord;

	@Getter
	@Setter
	@OneToOne(mappedBy="appointment", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
	private QueuedAppointmentLink queuedAppointmentLink;

	private String remarks = "";
	private String urgency = "";
	private boolean isVirtual;
	private Integer creatorSecurityId;
	
	@Enumerated(EnumType.STRING)
	private BookingSource bookingSource;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "confirmed_at")
	private Date confirmedAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "confirmed_by_type")
	private ConfirmedByType confirmedByType;

	@Column(name = "confirmed_by")
	private String confirmedBy;

	private Integer reasonCode;

	/** default constructor */
	public Appointment()
	{
	}

	/** copy constructor */
	public Appointment(Appointment appointmentToCopy)
	{
		this.id = null;
		this.providerNo = appointmentToCopy.providerNo;
		this.appointmentDate = appointmentToCopy.appointmentDate;
		this.startTime = appointmentToCopy.startTime;
		this.endTime = appointmentToCopy.endTime;
		this.name = appointmentToCopy.name;
		this.demographicNo = appointmentToCopy.demographicNo;
		this.programId = appointmentToCopy.programId;
		this.notes = appointmentToCopy.notes;
		this.reason = appointmentToCopy.reason;
		this.location = appointmentToCopy.location;
		this.resources = appointmentToCopy.resources;
		this.type = appointmentToCopy.type;
		this.style = appointmentToCopy.style;
		this.billing = appointmentToCopy.billing;
		this.status = appointmentToCopy.status;
		this.importedStatus = appointmentToCopy.importedStatus;
		this.createDateTime = appointmentToCopy.createDateTime;
		this.updateDateTime = appointmentToCopy.updateDateTime;
		this.creator = appointmentToCopy.creator;
		this.lastUpdateUser = appointmentToCopy.lastUpdateUser;
		this.lastUpdateUserRecord = appointmentToCopy.lastUpdateUserRecord;
		this.remarks = appointmentToCopy.remarks;
		this.urgency = appointmentToCopy.urgency;
		this.isVirtual = appointmentToCopy.isVirtual;
		this.creatorSecurityId = appointmentToCopy.creatorSecurityId;
		this.bookingSource = appointmentToCopy.bookingSource;
		this.reasonCode = appointmentToCopy.reasonCode;
	}

	public Integer getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(Integer reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getProviderNo() {
		return providerNo;
	}

	public void setProviderNo(String providerNo) {
		this.providerNo = providerNo;
	}

	public Date getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDemographicNo() {
		return demographicNo;
	}

	public boolean hasDemographic()
	{
		return this.demographicNo != 0;
	}

	public void setDemographicNo(int demographicNo) {
		this.demographicNo = demographicNo;
	}

	public int getProgramId() {
		return programId;
	}

	public void setProgramId(int programId) {
		this.programId = programId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getResources() {
		return resources;
	}

	public void setResources(String resources) {
		this.resources = resources;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getBilling() {
		return billing;
	}

	public void setBilling(String billing) {
		this.billing = billing;
	}

	public String getStatus() {
		return status;
	}

	public String getAppointmentStatus()
	{
		if(status != null && status.length() > 0)
		{
			return status.substring(0, 1);
		}

		return null;
	}

	public String getAppointmentStatusModifier()
	{
		if(status != null && status.length() > 1)
		{
			return status.substring(1,2);
		}

		return null;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Date getUpdateDateTime() {
		return updateDateTime;
	}

	public void setUpdateDateTime(Date updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getLastUpdateUser() {
		return lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

	public ProviderData getLastUpdateUserRecord()
	{
		return lastUpdateUserRecord;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getImportedStatus() {
		return (importedStatus);
	}

	public void setImportedStatus(String importedStatus) {
		this.importedStatus = importedStatus;
	}

	public String getUrgency() {
		return (urgency);
	}

	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}

	public boolean getIsVirtual()
	{
		return isVirtual;
	}

	public void setIsVirtual(boolean isVirtual)
	{
		this.isVirtual = isVirtual;
	}

	public Integer getCreatorSecurityId() {
    	return (creatorSecurityId);
    }

	public void setCreatorSecurityId(Integer creatorSecurityId) {
    	this.creatorSecurityId = creatorSecurityId;
    }

	public BookingSource getBookingSource() {
    	return (bookingSource);
    }

	public void setBookingSource(BookingSource bookingSource) {
    	this.bookingSource = bookingSource;
    }

	public Optional<Date> getConfirmedAt()
	{
		return Optional.ofNullable(this.confirmedAt);
	}

	public void setConfirmedAt(Date confirmedAt)
	{
		this.confirmedAt = confirmedAt;
	}

	public Optional<ConfirmedByType> getConfirmedByType()
	{
		return Optional.ofNullable(confirmedByType);
	}

	public void setConfirmedByType(ConfirmedByType confirmedByType)
	{
		this.confirmedByType = confirmedByType;
	}

	public Optional<String> getConfirmedBy()
	{
		return Optional.ofNullable(confirmedBy);
	}

	public void setConfirmedBy(String confirmedBy)
	{
		this.confirmedBy = confirmedBy;
	}

	public void confirm(Date confirmedAt, ConfirmedByType confirmedByType, String confirmedBy)
	{
		this.confirmedAt = confirmedAt;
		this.confirmedBy = confirmedBy;
		this.confirmedByType = confirmedByType;
	}

	@Override
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@PrePersist
	@PreUpdate
	protected void jpaUpdateLastUpdateTime() {
		this.updateDateTime = new Date();
	}

	public boolean isConfirmed()
	{
		return this.getConfirmedAt().isPresent();
	}
	
    public static final Comparator<Appointment> APPT_DATE_COMPARATOR =new Comparator<Appointment>()
    {
        public int compare(Appointment o1, Appointment o2) {
        	if (o1==null && o2!=null) return -1;
        	if (o1==null && o2==null) return 0;
        	if (o1!=null && o2==null) return 1;
        					
        	Date d1 = o1.getAppointmentDate();
        	Date d2 = o2.getAppointmentDate();
        	int tmp = d1.compareTo(d2);
        	if(tmp == 0) {
        		Date t1 = o1.getStartTime();
        		Date t2 = o2.getStartTime();
        		return t1.compareTo(t2);
        	} else {
        		return tmp;
        	}     
        }       
    };


	public Date getStartTimeAsFullDate()
	{
		try
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(getAppointmentDate());
			Calendar acal = Calendar.getInstance();
			acal.setTime(getStartTime());
			cal.set(Calendar.HOUR_OF_DAY, acal.get(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, acal.get(Calendar.MINUTE));
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTime();
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public Date getEndTimeAsFullDate()
	{
		try
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(getAppointmentDate());
			Calendar acal = Calendar.getInstance();
			acal.setTime(getEndTime());
			cal.set(Calendar.HOUR_OF_DAY, acal.get(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, acal.get(Calendar.MINUTE));
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTime();
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public LocalDateTime getStartDateTime()
	{
		return ConversionUtils.toLocalDateTime(this.getStartTime());
	}
}
