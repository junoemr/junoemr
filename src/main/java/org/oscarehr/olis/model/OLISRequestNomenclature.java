/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package org.oscarehr.olis.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import org.oscarehr.common.model.AbstractModel;

@Data
@Entity
public class OLISRequestNomenclature extends AbstractModel<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String nameId;
	private String name;
	private String altName1;
	private String sortKey;
	private String category;

	public OLISRequestNomenclature()
	{
		super();
	}

	@Override
	public Integer getId()
	{
		return id;
	}
}
