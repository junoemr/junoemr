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


package org.oscarehr.ws.transfer_objects;

import java.util.ArrayList;
import java.util.List;
//import java.util.Map;
import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
//import org.apache.log4j.Logger;
import org.oscarehr.common.model.EForm;
//import org.oscarehr.util.SpringUtils;
import org.springframework.beans.BeanUtils;

public final class EFormTransfer {

	private Integer id;
	private String formName;
	private String fileName;
	private String subject;
	private boolean current;
	private Date formDate;
	private Date formTime;
	private String creator;
	private String formHtml;
	private boolean patientIndependent;



	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getFormName()
	{
		return formName;
	}

	public void setFormName(String formName)
	{
		this.formName = formName;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public boolean getCurrent()
	{
		return current;
	}

	public void setCurrent(boolean current)
	{
		this.current = current;
	}

	public Date getFormDate()
	{
		return formDate;
	}

	public void setFormDate(Date formDate)
	{
		this.formDate = formDate;
	}

	public Date getFormTime()
	{
		return formTime;
	}

	public void setFormTime(Date formTime)
	{
		this.formTime = formTime;
	}

	public String getCreator()
	{
		return creator;
	}

	public void set(String creator)
	{
		this.creator = creator;
	}

	public String getFormHtml()
	{
		return formHtml;
	}

	public void setFormHtml(String formHtml)
	{
		this.formHtml = formHtml;
	}

	public boolean getPatientIndependent()
	{
		return patientIndependent;
	}

	public void setPatientIndependent(boolean patientIndependent)
	{
		this.patientIndependent = patientIndependent;
	}



	public static EFormTransfer toTransfer(EForm eform) 
	{
		if (eform==null) return(null);
		
		EFormTransfer eformTransfer = new EFormTransfer();

		BeanUtils.copyProperties(eform, eformTransfer);
		
		return (eformTransfer);
	}

	public static EFormTransfer[] toTransfers(List<EForm> eforms) {
		ArrayList<EFormTransfer> results = new ArrayList<EFormTransfer>();

		for (EForm eform : eforms) {
			results.add(toTransfer(eform));
		}

		return (results.toArray(new EFormTransfer[0]));
	}



	@Override
	public String toString() {
		return (ReflectionToStringBuilder.toString(this));
	}

	public EForm copyTo(EForm eform) {
		BeanUtils.copyProperties(this, eform);

		return (eform);
	}
}

