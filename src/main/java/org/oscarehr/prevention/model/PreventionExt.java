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


package org.oscarehr.prevention.model;

import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "preventionsExt")
public class PreventionExt extends AbstractModel<Integer> implements Serializable {

	public static final String KEY_NAME = "name";
	public static final String KEY_DOSE = "dose";
	public static final String KEY_MANUFACTURE = "manufacture";
	public static final String KEY_ROUTE = "route";
	public static final String KEY_LOT = "lot";
	public static final String KEY_LOCATION = "location";
	public static final String KEY_COMMENT = "comments";
	public static final String KEY_REASON = "reason";
	public static final String KEY_RESULT = "result";
	public static final String KEY_DIN = "din";

	public static final String KEY_RESULT_PENDING = "pending";
	public static final String KEY_RESULT_NORMAL = "normal";
	public static final String KEY_RESULT_ABNORMAL = "abnormal";
	public static final String KEY_RESULT_OTHER = "other";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id = null;

	private String keyval = null;
	private String val = null;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "prevention_id")
	private Prevention prevention;

	@Override
	public Integer getId() {
		return id;
	}
	
	public String getkeyval() {
		return keyval;
	}
	
	public void setKeyval(String keyval) {
		this.keyval = keyval;
	}
	
	public String getVal() {
		return val;
	}
	
	public void setVal(String val) {
		this.val = val;
	}

	public Prevention getPrevention()
	{
		return prevention;
	}

	public void setPrevention(Prevention prevention)
	{
		this.prevention = prevention;
	}

	/* quick methods for setting specific key-value pairs */

	private void setKeyValue(String key, String value)
	{
		setKeyval(key);
		setVal(value);
	}

	public void setNameKeyValue(String value)
	{
		setKeyValue(KEY_NAME, value);
	}

	public void setDoseKeyValue(String value)
	{
		setKeyValue(KEY_DOSE, value);
	}

	public void setManufactureKeyValue(String value)
	{
		setKeyValue(KEY_MANUFACTURE, value);
	}

	public void setRouteKeyValue(String value)
	{
		setKeyValue(KEY_ROUTE, value);
	}

	public void setLotKeyValue(String value)
	{
		setKeyValue(KEY_LOT, value);
	}

	public void setDLocationKeyValue(String value)
	{
		setKeyValue(KEY_LOCATION, value);
	}

	public void setCommentKeyValue(String value)
	{
		setKeyValue(KEY_COMMENT, value);
	}

	public void setReasonKeyValue(String value)
	{
		setKeyValue(KEY_REASON, value);
	}

	public void setResultKeyValue(String value)
	{
		if(KEY_RESULT_ABNORMAL.equals(value) ||
				KEY_RESULT_NORMAL.equals(value) ||
				KEY_RESULT_PENDING.equals(value) ||
				KEY_RESULT_OTHER.equals(value))
		{
			setKeyValue(KEY_RESULT, value);
		}
		else
		{
			throw new RuntimeException("Invalid prevention result value: " + value);
		}
	}
}
