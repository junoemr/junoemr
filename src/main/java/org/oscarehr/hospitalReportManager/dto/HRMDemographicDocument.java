package org.oscarehr.hospitalReportManager.dto;

import java.util.List;
import lombok.Data;
import org.oscarehr.hospitalReportManager.model.HRMDocument;

@Data
public class HRMDemographicDocument
{
	HRMDocument hrmDocument;
	List<Integer> duplicateIds;
}
