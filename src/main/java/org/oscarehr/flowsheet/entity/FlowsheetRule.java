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
package org.oscarehr.flowsheet.entity;

import lombok.Data;
import org.oscarehr.common.model.AbstractModel;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity(name = "entity.FlowsheetRule")
@Table(name = "flowsheet_rule")
public class FlowsheetRule extends AbstractModel<Integer>
{
	public enum SeverityLevel {
		RECOMMENDATION,
		WARNING,
		DANGER,
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "rule_name")
	private String name;

	@Column(name = "rule_severity")
	@Enumerated(value = EnumType.STRING)
	private SeverityLevel severityLevel;

	@Column(name = "rule_message")
	private String message;

	@Column(name = "description")
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flowsheet_id")
	private Flowsheet flowsheet;

	@OneToMany(fetch= FetchType.LAZY, mappedBy = "flowsheetRule", cascade = CascadeType.ALL)
	private List<FlowsheetRuleCondition> conditions;
}
