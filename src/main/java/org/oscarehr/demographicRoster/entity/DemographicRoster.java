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

package org.oscarehr.demographicRoster.entity;

import lombok.Getter;
import lombok.Setter;
import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.rosterStatus.entity.RosterStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "demographic_roster")
public class DemographicRoster extends AbstractModel<Integer> implements Serializable
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "demographic_no")
	private Integer demographicId;

	@Column(name = "rostered_physician")
	private String rosteredPhysician;

	@Column(name = "ohip_no")
	private String ohipNo;

	@Column(name = "roster_date", columnDefinition = "TIMESTAMP")
	private LocalDateTime rosterDate;

	@Column(name = "roster_termination_date", columnDefinition = "TIMESTAMP")
	private LocalDateTime rosterTerminationDate;

	@Column(name = "roster_termination_reason")
	@Enumerated(EnumType.STRING)
	private RosterTerminationReason rosterTerminationReason;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="roster_status_id")
	private RosterStatus rosterStatus;

	@Column(name = "added_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime addedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "demographic_no", insertable = false, updatable = false)
	private Demographic demographic;
}
