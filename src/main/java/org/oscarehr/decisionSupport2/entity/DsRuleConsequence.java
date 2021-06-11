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
package org.oscarehr.decisionSupport2.entity;

import lombok.Data;
import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.decisionSupport2.model.consequence.SeverityLevel;

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
import javax.persistence.Table;

@Data
@Entity
@Table(name = "ds_rule_consequence")
public class DsRuleConsequence extends AbstractModel<Integer>
{
	public enum ConsequenceType {
		ALERT,
		HIDDEN
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "consequence_name")
	private String name;

	@Column(name = "consequence_type")
	@Enumerated(value = EnumType.STRING)
	private ConsequenceType type;

	@Column(name = "consequence_severity")
	@Enumerated(value = EnumType.STRING)
	private SeverityLevel severityLevel;

	@Column(name = "consequence_message")
	private String message;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ds_rule_id")
	private DsRule dsRule;
}
