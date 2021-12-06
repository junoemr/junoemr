package org.oscarehr.labs.transfer;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import oscar.oscarLab.ca.on.LabResultData;

@Data
public class BasicLabInfo implements Serializable
{
	int labId;
	private String demographicId;
	private String label;
	private LocalDateTime observationDateTime;
	private Boolean abnormal;
	private String reportStatus;
	private String type;

	/**
	 * Basic lab info to display lists of labs. Specifically for the old encounter page lab section
	 * @param labId
	 * @param demographicId
	 * @param label
	 * @param observationDateTime Observation date or OBR date in the HL7 format. Typicaly the date the lab test was performed
	 * @param abnormal Flag indicating if a lab has abnormal results
	 * @param reportStatus Is 'A' if abnormal
	 * @param type Lab type from the hl7TextMessage table
	 */
	public BasicLabInfo(int labId, String demographicId, String label, LocalDateTime observationDateTime, Boolean abnormal, String reportStatus, String type)
	{
		this.labId = labId;
		this.demographicId = demographicId;
		this.label = label;
		this.observationDateTime = observationDateTime;
		this.abnormal = abnormal;
		if(this.abnormal == null)
		{
			this.abnormal = false;
		}
		this.reportStatus = reportStatus;
		this.type = type;
	}

	public boolean isMDS()
	{
		return LabResultData.MDS.equals(this.type);
	}

	public boolean isCML()
	{
		return LabResultData.CML.equals(this.type);
	}

	public boolean isHL7TEXT()
	{
		return LabResultData.HL7TEXT.equals(this.type);
	}
}
