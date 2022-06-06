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


package org.oscarehr.consultations.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.hospitalReportManager.model.HRMDocument;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "consultdocs")
public class ConsultDocs extends AbstractModel<Integer>
{
	public static final String DOCTYPE_DOC = "D";
	public static final String DOCTYPE_EFORM = "E";
	public static final String DOCTYPE_LAB = "L";
	public static final String DOCTYPE_HRM = "H";
	public static final String DELETED = "Y";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private int requestId;

	@Column(name = "document_no")
	private int documentNo;

	@Column(name = "doctype")
	private String docType;

	private String deleted;

	@Column(name = "attach_date")
	@Temporal(TemporalType.DATE)
	private Date attachDate;

	@Column(name = "provider_no")
	private String providerNo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_no", insertable=false, updatable=false)
	@Where(clause = ("doctype = '" + DOCTYPE_HRM + "'"))
	private HRMDocument hrmDocument;

	public ConsultDocs()
	{
	}

	public ConsultDocs(int requestId, int documentNo, String docType, String providerNo)
	{
		setRequestId(requestId);
		setDocumentNo(documentNo);
		setDocType(docType);
		setProviderNo(providerNo);
		setAttachDate(new Date());
	}
}
