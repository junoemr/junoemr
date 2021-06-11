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
import org.oscarehr.flowsheet.entity.FlowsheetItem;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "ds_rule")
public class DsRule extends AbstractModel<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "rule_name")
	private String name;

	@Column(name = "description")
	private String description;

	@OneToMany(fetch= FetchType.LAZY, mappedBy = "flowsheetRule", cascade = CascadeType.ALL)
	private List<DsRuleCondition> conditions;

	@OneToMany(fetch= FetchType.LAZY, mappedBy = "flowsheetRule", cascade = CascadeType.ALL)
	private List<DsRuleConsequence> consequences;

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "flowsheet_item_ds_rule", joinColumns = @JoinColumn(name="ds_rule_id"), inverseJoinColumns = @JoinColumn(name="flowsheet_item_id"))
	private Set<FlowsheetItem> flowsheetItems = new HashSet<>();
}
