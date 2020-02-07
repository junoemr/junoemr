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

package org.oscarehr.forms.service;

import org.oscarehr.common.dao.forms.FormsDao;
import org.oscarehr.forms.converter.FormBCAR2012Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormService
{
	@Autowired
	private FormsDao formsDao;

	/**
	 * For now, thin wrapper to avoid having to directly call the Dao at the application layer.
	 * @param beginEdd string representation of beginning expected delivery date
	 * @param endEdd string representation of end expected delivery date
	 * @param limit number of results we want
	 * @param offset where we want to start querying results from
	 * @return list of BC AR 2012 form entries that fall within [beginEdd, endEdd] range
	 */
	public List<FormBCAR2012Converter> getBCAR2012(String beginEdd, String endEdd, int limit, int offset)
	{
		return formsDao.selectBCAR2012(beginEdd, endEdd, limit, offset);
	}

}
