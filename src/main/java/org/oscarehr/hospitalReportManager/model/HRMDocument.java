/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.hospitalReportManager.model;

import lombok.Data;
import org.oscarehr.common.model.AbstractModel;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class HRMDocument extends AbstractModel<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date timeReceived;
	private String reportType;
	private String reportHash;
	private String reportLessTransactionInfoHash;
	private String reportLessDemographicInfoHash;
	private String reportStatus;
	private String reportFile;
	private String reportFileSchemaVersion;
	private String sourceFacility;
	private String sendingFacilityId;
	private String sendingFacilityReportId;
	private String messageUniqueId;
	private String deliverToUserId;

	private String unmatchedProviders;
	private Integer numDuplicatesReceived;
	private Date reportDate;

	private Integer parentReport;

	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hrmCategoryId")
	private HRMCategory hrmCategory;

	@OneToMany(fetch= FetchType.LAZY, mappedBy = "hrmDocument", cascade = CascadeType.PERSIST)
	private List<HRMDocumentComment> commentList;

	@OneToMany(fetch= FetchType.LAZY, mappedBy = "hrmDocument", cascade = CascadeType.PERSIST)
	private List<HRMDocumentSubClass> documentSubClassList;

	@OneToMany(fetch= FetchType.LAZY, mappedBy = "hrmDocument")
	private List<HRMDocumentToDemographic> documentToDemographicList;

	@OneToMany(fetch= FetchType.LAZY, mappedBy = "hrmDocument")
	private List<HRMDocumentToProvider> documentToProviderList;

	@Override
	public Integer getId()
	{
		return id;
	}

	/**
	 * This comparator sorts HRM Docs ascending based on the time received
	 */
	public static final Comparator<HRMDocument> HRM_DATE_COMPARATOR = Comparator.comparing(o -> o.timeReceived);

	/**
	 * This comparator sorts EFormData ascending based on the formName
	 */
	public static final Comparator<HRMDocument> HRM_TYPE_COMPARATOR = Comparator.comparing(o -> o.reportType);

}
