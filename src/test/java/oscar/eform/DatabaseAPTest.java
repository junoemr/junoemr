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
package oscar.eform;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.runner.RunWith;
import org.oscarehr.eform.dao.EFormDataDao;
import org.oscarehr.util.SpringUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import oscar.eform.data.DatabaseAP;
import oscar.eform.data.EForm;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler;

import java.util.Hashtable;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SpringUtils.class, EctMeasurementsDataBeanHandler.class, EFormLoader.class})
public class DatabaseAPTest
{
	private EForm eform;
	private String demographic_no = "999";

	@Before
	public void before()
	{
		// EFormDataDao is irrelevant to these tests but required for EForm initialization
		EFormDataDao eFormDataDao = mock(EFormDataDao.class);
		PowerMockito.mockStatic(SpringUtils.class);
		when(SpringUtils.getBean("EFormDataDao")).thenReturn(eFormDataDao);

		eform = new EForm();
		eform.setDemographicNo(demographic_no);
		PowerMockito.mockStatic(EctMeasurementsDataBeanHandler.class);

		// to start, set up so any measurement lookup returns empty result
		when(EctMeasurementsDataBeanHandler.getLast(anyString(), anyString(), anyInt()))
				.thenReturn(new Hashtable<String, String>() {}
				);

		PowerMockito.mockStatic(EFormLoader.class);
		when(EFormLoader.getMarker()).thenReturn("oscarDB");
	}

	public void setExpectedAP(String apName, String apValue)
	{
		DatabaseAP databaseAP = new DatabaseAP();
		databaseAP.setApOutput(apValue);
		when(EFormLoader.getAP(apName)).thenReturn(databaseAP);
	}

	public void setExpectedLastMeasurement(String type, String value)
	{
		setExpectedLastMeasurement(type, value, 1);
	}

	public void setExpectedLastMeasurement(String type, String value, int maxResults)
	{
		when(EctMeasurementsDataBeanHandler.getLast(demographic_no, type, maxResults)).
				thenReturn(new Hashtable<String, String>()
				{{
					put("value", value);
				}});
	}

	@Test
	public void testSetAP()
	{
		String formHtml = "<input name=\"test\" id=\"test\" oscarDB=patient_name>";
		eform.setFormHtml(formHtml);

		setExpectedAP("patient_name", "SMITH, JOHN");
		eform.setDatabaseAPs();

		String expected = "<input name=\"test\" id=\"test\" oscarDB=patient_name value=\"SMITH, JOHN\">";
		String actual = eform.getFormHtml();
		assertEquals(expected, actual);
	}

	@Test
	public void testSetMeasurementAP()
	{
		String formHtml = "<input name=\"test\" id=\"test\" oscarDB=m$testType#value>";
		eform.setFormHtml(formHtml);

		setExpectedAP("testType", null);
		setExpectedLastMeasurement("testType", "testValue");
		eform.setDatabaseAPs();

		String expected = "<input name=\"test\" id=\"test\" oscarDB=m$testType#value value=\"testValue\">";
		String actual = eform.getFormHtml();
		assertEquals(expected, actual);
	}

	@Test
	public void testSetMeasurementAPOffset()
	{
		String formHtml = "<input name=\"test\" id=\"test\" oscarDB=m$testType@3#value>";
		eform.setFormHtml(formHtml);

		setExpectedAP("testType", null);
		setExpectedLastMeasurement("testType", "test1,test2,test3", 3);
		eform.setDatabaseAPs();

		String expected = "<input name=\"test\" id=\"test\" oscarDB=m$testType@3#value value=\"test1,test2,test3\">";
		String actual = eform.getFormHtml();
		assertEquals(expected, actual);
	}

	@Test
	public void testSetMeasurementAPTypeWithSpaces()
	{
		String formHtml = "<input name=\"test\" id=\"test\" oscarDB=\"m$test type spaces#value\">";
		eform.setFormHtml(formHtml);

		setExpectedAP("testType", null);
		setExpectedLastMeasurement("test type spaces", "testValue");
		eform.setDatabaseAPs();

		String expected = "<input name=\"test\" id=\"test\" oscarDB=\"m$test type spaces#value\" value=\"testValue\">";
		String actual = eform.getFormHtml();
		assertEquals(expected, actual);
	}
}
