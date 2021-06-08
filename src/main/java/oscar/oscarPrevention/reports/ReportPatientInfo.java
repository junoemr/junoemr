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
		this.demographicNo = NumberUtils.toInt(patientListTuple.get(0));
		this.lastName = patientListTuple.get(1);
		this.firstName = patientListTuple.get(2);
	}

	protected static List<ReportPatientInfo> fromList(ArrayList<ArrayList<String>> listOfPatientTuples)
	{
		return listOfPatientTuples.stream().map(ReportPatientInfo::new).collect(Collectors.toList());
	}
}
