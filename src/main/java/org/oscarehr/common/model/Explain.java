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

package org.oscarehr.common.model;

import java.io.Serializable;
import java.math.BigInteger;

public class Explain implements Serializable
{
	private BigInteger id;
	private String selectType;
	private String table;
	private String type;
	private String possibleKeys;
	private String key;
	private String keyLen;
	private String ref;
	private BigInteger rows;
	private String extra;

	public BigInteger getId()
	{
		return id;
	}

	public void setId(BigInteger id)
	{
		this.id = id;
	}

	public String getSelectType()
	{
		return selectType;
	}

	public void setSelectType(String selectType)
	{
		this.selectType = selectType;
	}

	public String getTable()
	{
		return table;
	}

	public void setTable(String table)
	{
		this.table = table;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getPossibleKeys()
	{
		return possibleKeys;
	}

	public void setPossibleKeys(String possibleKeys)
	{
		this.possibleKeys = possibleKeys;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getKeyLen()
	{
		return keyLen;
	}

	public void setKeyLen(String keyLen)
	{
		this.keyLen = keyLen;
	}

	public String getRef()
	{
		return ref;
	}

	public void setRef(String ref)
	{
		this.ref = ref;
	}

	public BigInteger getRows()
	{
		return rows;
	}

	public void setRows(BigInteger rows)
	{
		this.rows = rows;
	}

	public String getExtra()
	{
		return extra;
	}

	public void setExtra(String extra)
	{
		this.extra = extra;
	}

	@Override
	public String toString() {
		return "{id:" + id +
				", select_type:" + selectType +
				", table:" + table +
				", type:" + type +
				", possible_keys:" + possibleKeys +
				", key:" + key +
				", key_len:" + keyLen +
				", ref:" + ref +
				", rows:" + rows +
				", Extra:" + extra +
				"}";
	}
}
