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

package org.oscarehr.PMmodule.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.hibernate.ObjectNotFoundException;
import org.oscarehr.PMmodule.model.Agency;
import org.oscarehr.PMmodule.model.IntakeAnswerElement;
import org.oscarehr.PMmodule.model.IntakeNode;
import org.oscarehr.PMmodule.model.IntakeNodeJavascript;
import org.oscarehr.PMmodule.model.IntakeNodeLabel;
import org.oscarehr.PMmodule.model.IntakeNodeTemplate;
import org.oscarehr.util.DbConnectionFilter;
import org.oscarehr.util.MiscUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of GenericIntakeNodeDAO interface
 */
public class GenericIntakeNodeDAO extends HibernateDaoSupport {

	private static final Logger LOG = MiscUtils.getLogger();

	/**
	 * @see org.oscarehr.PMmodule.dao.GenericIntakeNodeDAO#getIntakeNode(java.lang.Integer)
	 */
	public IntakeNode getIntakeNode(Integer intakeNodeId) {
		if (intakeNodeId == null || intakeNodeId < 1) {
			throw new IllegalArgumentException(
					"intakeNodeId must be non-null and greater than 0");
		}

		IntakeNode intakeNode = getHibernateTemplate().load(
				IntakeNode.class, intakeNodeId);
		getChildren(intakeNode);

		LOG.info("getIntakeNode : " + intakeNodeId);

		return intakeNode;
	}

	/**
	 *Returns a list of Intake Nodes of type "intake".
	 */
	public List<IntakeNode> getIntakeNodes() {
		// List l =
		// getHibernateTemplate().find("from IntakeNode i, IntakeNodeType iType, IntakeNodeTemplate iTemplate  where iType.type = 'intake'  and iType.intake_node_type_id = iTemplate.intake_node_type_id and i.intake_node_template_id = iTemplate.intake_node_template_id");
		List<IntakeNode> l = (List<IntakeNode>) getHibernateTemplate()
				.find(
						"select i from IntakeNode i, IntakeNodeType iType, IntakeNodeTemplate iTemplate  where iType.type = 'intake'  and iType.id = iTemplate.type and i.nodeTemplate = iTemplate.id");

		// from IntakeNode i where i.type = 'intake'");
		return l;
	}

	/**
	 *Returns a list of Intake Nodes of type "intake".
	 */
	public List<IntakeNode> getPublishedIntakeNodesByName(String name) {
		// List l =
		// getHibernateTemplate().find("from IntakeNode i, IntakeNodeType iType, IntakeNodeTemplate iTemplate  where iType.type = 'intake'  and iType.intake_node_type_id = iTemplate.intake_node_type_id and i.intake_node_template_id = iTemplate.intake_node_template_id");
		List<IntakeNode> l = (List<IntakeNode>) getHibernateTemplate()
				.find(
						"select i from IntakeNode i, IntakeNodeType iType, IntakeNodeTemplate iTemplate, IntakeNodeLabel iLabel  where iType.type = 'intake'  and iType.id = iTemplate.type and i.nodeTemplate = iTemplate.id and i.label = iLabel.id and i.publish_date != null and iLabel.label = ? order by i.form_version desc",
						new Object[] { name });

		// from IntakeNode i where i.type = 'intake'");
		return l;
	}

	public List<IntakeNode> getIntakeNodeByEqToId(Integer iNodeEqToId)
			throws SQLException {
		if (iNodeEqToId == null) {
			throw new IllegalArgumentException(
					"Parameters iNodeEqToId must be non-null");
		}

		List<IntakeNode> nwIntakeNodes = new ArrayList<IntakeNode>();
		Set<Integer> iNodeIds = getIntakeNodeIdByEqToId(iNodeEqToId);
		for (Integer id : iNodeIds) {
			nwIntakeNodes.add(getIntakeNode(id));
		}
		return nwIntakeNodes;
	}

	public Set<Integer> getIntakeNodeIdByEqToId(Integer iNodeEqToId)
			throws SQLException {
		if (iNodeEqToId == null) {
			throw new IllegalArgumentException(
					"Parameters intakdNodeId must be non-null");
		}
		Connection c = DbConnectionFilter.getThreadLocalDbConnection();
		Set<Integer> results = new TreeSet<Integer>();
		try {
			PreparedStatement ps = c
					.prepareStatement("select intake_node_id from intake_node where eq_to_id=?");
			ps.setInt(1, iNodeEqToId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				results.add(rs.getInt(1));
			}
		} finally {
			c.close();
		}
		return results;
	}

	public Set<Integer> getEqToIdByIntakeNodeId(Integer intakeNodeId)
			throws SQLException {
		if (intakeNodeId == null) {
			throw new IllegalArgumentException(
					"Parameters intakdNodeId must be non-null");
		}
		Connection c = DbConnectionFilter.getThreadLocalDbConnection();
		Set<Integer> results = new TreeSet<Integer>();
		try {
			PreparedStatement ps = c
					.prepareStatement("select eq_to_id from intake_node where intake_node_id=?");
			ps.setInt(1, intakeNodeId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				results.add(rs.getInt(1));
			}
		} finally {
			c.close();
		}
		return results;
	}

	public List<IntakeNode> getIntakeNodeByEqToId(Set<IntakeNode> iNodes)
			 {
		if (iNodes == null) {
			throw new IllegalArgumentException(
					"Parameters iNodes must be non-null");
		}

		List<IntakeNode> nwIntakeNodes = new ArrayList<IntakeNode>();
		for (IntakeNode iN : iNodes) {
			nwIntakeNodes.add(getIntakeNode(iN.getEq_to_id()));
		}
		return nwIntakeNodes;
	}

	public void saveNodeLabel(IntakeNodeLabel intakeNodeLabel) {
		getHibernateTemplate().save(intakeNodeLabel);
	}

	public void updateIntakeNode(IntakeNode intakeNode) {
		getHibernateTemplate().update(intakeNode);
	}

	public void updateNodeLabel(IntakeNodeLabel intakeNodeLabel) {
		getHibernateTemplate().update(intakeNodeLabel);
	}

	public void updateAgencyIntakeQuick(Agency agency) {
		getHibernateTemplate().update(agency);
	}

	public IntakeNodeLabel getIntakeNodeLabel(Integer intakeNodeLabelId) {
		IntakeNodeLabel intakeNodeLabel = null;
		if (intakeNodeLabelId == null || intakeNodeLabelId < 1) {
			throw new IllegalArgumentException(
					"intakeNodeLabelId must be non-null and greater than 0");
		}
		//in case the node with intakenodelabel id doesn't exist
		try {
			intakeNodeLabel = getHibernateTemplate().get(
					IntakeNodeLabel.class, intakeNodeLabelId);
		} catch (ObjectNotFoundException onfe) {
			LOG.warn("no node found for : " + intakeNodeLabelId);
		}
		return intakeNodeLabel;
	}

	public IntakeNodeTemplate getIntakeNodeTemplate(Integer intakeNodeTemplateId) {
		if (intakeNodeTemplateId == null || intakeNodeTemplateId < 1) {
			throw new IllegalArgumentException(
					"intakeNodeTemplateId must be non-null and greater than 0");
		}
		IntakeNodeTemplate intakeNodeTemplate = getHibernateTemplate()
				.get(IntakeNodeTemplate.class, intakeNodeTemplateId);
		return intakeNodeTemplate;
	}

	public void saveIntakeNode(IntakeNode intakeNode) {
		getHibernateTemplate().save(intakeNode);
	}

	public void saveIntakeNodeTemplate(IntakeNodeTemplate intakeNodeTemplate) {
		getHibernateTemplate().save(intakeNodeTemplate);
	}

	public void saveIntakeAnswerElement(IntakeAnswerElement intakeAnswerElement) {
		getHibernateTemplate().save(intakeAnswerElement);
	}

	private void getChildren(IntakeNode intakeNode) {
		HashSet<Integer> nodeIds = new HashSet<Integer>();
		nodeIds.add(intakeNode.getId());

		getChildren(nodeIds, intakeNode.getChildren());
	}

	private void getChildren(Set<Integer> nodeIds, List<IntakeNode> children) {
		for (IntakeNode child : children) {
			if (child == null)
				continue;
			Integer childId = child.getId();

			if (nodeIds.contains(childId)) {
				throw new IllegalStateException("intake node with id : "
						+ childId + " is an ancestor of itself");
			} else {
				nodeIds.add(childId);
			}

			// load children
			getChildren(nodeIds, child.getChildren());
		}
	}

	public void deleteIntakeNode(IntakeNode intakeNode) {
		getHibernateTemplate().delete(intakeNode);
	}

	public List<IntakeNodeJavascript> getIntakeNodeJavascriptLocation(String questionId) {
		List<IntakeNodeJavascript> js = (List<IntakeNodeJavascript>) getHibernateTemplate().find("FROM IntakeNodeJavascript j where j.questionId=?",questionId);
		return js;
	}
}
