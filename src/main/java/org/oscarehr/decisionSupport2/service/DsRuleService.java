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
package org.oscarehr.decisionSupport2.service;


import org.drools.FactException;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.oscarehr.decisionSupport2.converter.DsRuleDbToModelConverter;
import org.oscarehr.decisionSupport2.converter.DsRuleTransferToEntityConverter;
import org.oscarehr.decisionSupport2.dao.DsRuleDao;
import org.oscarehr.decisionSupport2.model.DsInfoCache;
import org.oscarehr.decisionSupport2.model.DsInfoLookup;
import org.oscarehr.decisionSupport2.model.DsRule;
import org.oscarehr.decisionSupport2.transfer.DsRuleCreateInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.oscarEncounter.oscarMeasurements.MeasurementInfo;
import oscar.oscarPrevention.Prevention;

import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class DsRuleService
{
	@Autowired
	private DsRuleDao dsRuleDao;

	@Autowired
	private DsRuleDbToModelConverter dsRuleDbToModelConverter;

	@Autowired
	private DsRuleTransferToEntityConverter dsRuleTransferToEntityConverter;

	/** filter out rules that do not meet all conditions, and apply consequences for rules where all conditions are met.
	 * @param dsInfoLookup the object where conditions can look up facts. facts determine if a condition is met
	 * @param dsInfoCache the object where changes are made by consequences. Ex: a consequence will add an alert to the dsInfoCache
	 * @param typeCode the type code to check requirements for
	 * @param dsRules the set of rules to apply
	 */
	public void applyRules(DsInfoLookup dsInfoLookup, DsInfoCache dsInfoCache, String typeCode, List<DsRule> dsRules)
	{
		dsRules.stream()
			.filter((rule) -> rule.getConditions()
					.stream()
					.allMatch((condition) -> condition.meetsRequirements(typeCode, dsInfoLookup)))
			.forEach((rule) -> rule.getConsequences()
					.forEach((consequence) -> consequence.apply(typeCode, dsInfoCache))
			);
	}

	/**
	 * apply the rules defined in the drools rule base to the given object
	 * @param ruleBase the rule base
	 * @param mi measurement info object
	 * @throws FactException
	 */
	public void applyRuleBase(RuleBase ruleBase, MeasurementInfo mi) throws FactException
	{
		WorkingMemory workingMemory = ruleBase.newWorkingMemory();
		workingMemory.assertObject(mi);
		workingMemory.fireAllRules();
	}

	/**
	 * apply the rules defined in the drools rule base to the given object
	 * @param ruleBase the rule base
	 * @param prevention prevention info object
	 * @throws FactException
	 */
	public void applyRuleBase(RuleBase ruleBase, Prevention prevention) throws FactException
	{
		WorkingMemory workingMemory = ruleBase.newWorkingMemory();
		workingMemory.assertObject(prevention);
		workingMemory.fireAllRules();
	}

	public List<DsRule> getAllRules()
	{
		return dsRuleDbToModelConverter.convert(dsRuleDao.findAll());
	}

	public DsRule createRule(String creatingProviderId, DsRuleCreateInput input)
	{
		org.oscarehr.decisionSupport2.entity.DsRule entity = dsRuleTransferToEntityConverter.convert(input);
		entity.setCreatedBy(creatingProviderId);
		dsRuleDao.persist(entity);
		return dsRuleDbToModelConverter.convert(entity);
	}
}
