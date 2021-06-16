package oscar.oscarPrevention.reports;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReportPatientInfo {

	public Integer demographicNo;
	public String lastName;
	public String firstName;
	
	private ReportPatientInfo(ArrayList<String> patientListTuple)
	{
		// None of these are actually guaranteed to be there... they just happen to be default checked values when creating a demographic query.
		// These could be totally random things depending on what "Search For" boxes happen to be checked.  Since all the reports require
		// this information anyways, AND they all naively assume that it's there, I've put it into a semi-readable format.
		// ... BUT, this whole prevention reports needs to be redone, because it's totally awful.
		
		this.demographicNo = NumberUtils.toInt(patientListTuple.get(0));
		this.lastName = patientListTuple.get(1);
		this.firstName = patientListTuple.get(2);
	}

	protected static List<ReportPatientInfo> fromList(ArrayList<ArrayList<String>> listOfPatientTuples)
	{
		return listOfPatientTuples.stream().map(ReportPatientInfo::new).collect(Collectors.toList());
	}
}
