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
package org.oscarehr.labs.alberta;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.oscarehr.JunoApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import oscar.oscarLab.ca.all.parsers.AHS.v23.CLSHandler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class)
public class CLSComplianceTest {

	private static final Logger logger = Logger.getLogger(CLSComplianceTest.class);
	
	private static final String LAB02 = "MSH|^~\\&|OPEN ENGINE|CLS|Egate|POSP|20101203122425||ORU^R01|Q199816389T198313506|P|2.3\r" + 
			"PID|1|798274114^^^AB|2250008675^^^88000||MillMCK CB FSI||19701027|F||||83||\r" + 
			"PV1|1|E|05031^^^88000|||||||||||||||E\r" + 
			"OBR|1||0176439413^101LA|4356952^URINALYSIS^L01N|||20101203122200|||||||20101203122200|^^|1001745^Test, Physician - p-Test Physician||||10-337-300046||20101203122421||LA|F||1^^^20101203122100^^RT~^^^^^RT|\r" + 
			"OBX|1|ST|4669021^COLOR^L01N||Yellow||||||F|||20101203122419";

	private static final String LAB03 = "MSH|^~\\&|OPEN ENGINE|CLS|Egate|POSP|20101203122425||ORU^R01|Q199816389T198313506|P|2.3\r" + 
			"PID|1|798274114^^^AB|2250008675^^^88000||MillMCK CB FSI, Karla Bruni||19701027|F||||83||\r" + 
			"PV1|1|E|05031^^^88000|||||||||||||||E\r" + 
			"OBR|1||0176439413^101LA|4356952^URINALYSIS^L01N|||20101203122200|||||||20101203122200|^^|1001745^Test, Physician - p-Test Physician||||10-337-300046||20101203122421||LA|F||1^^^20101203122100^^RT~^^^^^RT|\r" + 
			"OBX|1|ST|4669021^COLOR^L01N||Yellow||||||F|||20101203122419";

	private static final String LAB04 = "MSH|^~\\&|OPEN ENGINE|CLS|Egate|POSP|20101203122425||ORU^R01|Q199816389T198313506|P|2.3\r" + 
			"PID|1|798274114^^^AB|2250008675^^^88000||MillMCK CB FSI, Karla Marla Darla||19701027|F||||83||\r" + 
			"PV1|1|E|05031^^^88000|||||||||||||||E\r" + 
			"OBR|1||0176439413^101LA|4356952^URINALYSIS^L01N|||20101203122200|||||||20101203122200|^^|1001745^Test, Physician - p-Test Physician||||10-337-300046||20101203122421||LA|F||1^^^20101203122100^^RT~^^^^^RT|\r" + 
			"OBX|1|ST|4669021^COLOR^L01N||Yellow||||||F|||20101203122419";

	private static final String LAB05 = "MSH|^~\\&|OPEN ENGINE|CLS|Egate|POSP|20101203122425||ORU^R01|Q199816389T198313506|P|2.3\r" + 
			"PID|1|798274114^^^AB|2250008675^^^88000||, Karla||19701027|F||||83||\r" + 
			"PV1|1|E|05031^^^88000|||||||||||||||E\r" + 
			"OBR|1||0176439413^101LA|4356952^URINALYSIS^L01N|||20101203122200|||||||20101203122200|^^|1001745^Test, Physician - p-Test Physician||||10-337-300046||20101203122421||LA|F||1^^^20101203122100^^RT~^^^^^RT|\r" + 
			"OBX|1|ST|4669021^COLOR^L01N||Yellow||||||F|||20101203122419";
	
	private static final String LAB06 = "MSH|^~\\&|OPEN ENGINE|CLS|Egate|POSP|20101203122425||ORU^R01|Q199816389T198313506|P|2.3\r" + 
			"PID|1|798274114^^^AB|2250008675^^^88000||,||19701027|F||||83||\r" + 
			"PV1|1|E|05031^^^88000|||||||||||||||E\r" + 
			"OBR|1||0176439413^101LA|4356952^URINALYSIS^L01N|||20101203122200|||||||20101203122200|^^|1001745^Test, Physician - p-Test Physician||||10-337-300046||20101203122421||LA|F||1^^^20101203122100^^RT~^^^^^RT|\r" + 
			"OBX|1|ST|4669021^COLOR^L01N||Yellow||||||F|||20101203122419";
	
	private static final String LAB07 = "MSH|^~\\&|OPEN ENGINE|CLS|Egate|POSP|20101203122425||ORU^R01|Q199816389T198313506|P|2.3\r" + 
			"PID|1|798274114^^^AB|2250008675^^^88000||||19701027|F||||83||\r" + 
			"PV1|1|E|05031^^^88000|||||||||||||||E\r" + 
			"OBR|1||0176439413^101LA|4356952^URINALYSIS^L01N|||20101203122200|||||||20101203122200|^^|1001745^Test, Physician - p-Test Physician||||10-337-300046||20101203122421||LA|F||1^^^20101203122100^^RT~^^^^^RT|\r" + 
			"OBX|1|ST|4669021^COLOR^L01N||Yellow||||||F|||20101203122419";

	
	@Test
	public void testNameParsing() throws Exception {
		//CLSHandler handler = new CLSHandler();
		//handler.init(LAB02);
		CLSHandler handler1 = new CLSHandler(LAB02);
		assertEquals("", handler1.getFirstName());
		assertEquals("MillMCK CB FSI", handler1.getLastName());
		assertEquals("", handler1.getMiddleName());
		
		//handler.init(LAB03);
		CLSHandler handler2 = new CLSHandler(LAB03);
		assertEquals("Karla", handler2.getFirstName());
		assertEquals("MillMCK CB FSI", handler2.getLastName());
		assertEquals("Bruni", handler2.getMiddleName());
		
		//handler.init(LAB04);
		CLSHandler handler3 = new CLSHandler(LAB04);
		assertEquals("Karla Marla", handler3.getFirstName());
		assertEquals("MillMCK CB FSI", handler3.getLastName());
		assertEquals("Darla", handler3.getMiddleName());
		
		//handler.init(LAB05);
		CLSHandler handler4 = new CLSHandler(LAB05);
		assertEquals("Karla", handler4.getFirstName());
		assertEquals("", handler4.getLastName());
		assertEquals("", handler4.getMiddleName());
		
		//handler.init(LAB06);
		CLSHandler handler5 = new CLSHandler(LAB06);
		assertEquals("", handler5.getFirstName());
		assertEquals("", handler5.getLastName());
		assertEquals("", handler5.getMiddleName());
		
		//handler.init(LAB07);
		CLSHandler handler6 = new CLSHandler(LAB07);
		assertEquals("", handler6.getFirstName());
		assertEquals("", handler6.getLastName());
		assertEquals("", handler6.getMiddleName());
	}
	
	/**
	 * REQ-ALBT-01 - Patient Demographic Compliance
	 */
	@Test
	public void testNameDemographicCompliance() throws Exception {
		//CLSHandler handler = new CLSHandler();
		
		int i = 0;
		for(String lab : TestLabs.ALL_LABS) {			
			//handler.init(lab);
			CLSHandler handler = new CLSHandler(lab);
			assertEquals("Karla", handler.getFirstName());
			assertEquals("MillMCK CB FSI", handler.getLastName());
			assertEquals("", handler.getMiddleName());
			assertEquals("798274114", handler.getHealthNum());
			assertEquals("AB", handler.getAssigningAuthority());
			assertEquals("F", handler.getSex());
			assertEquals("1970-10-27", handler.getDOB());
			assertTrue(handler.getAge() != null);
			
			i++;
			logger.info("Completed demo name compliance check for lab " + i + " successfully");
		}
	}
	
	/**
	 * REQ-ALBT-02 - Physician Information Compliance
	 */
	@Test
	public void testPhysicianInformationCompliance() throws Exception {
		//CLSHandler handler = new CLSHandler();

		int i = 0;
		for(String lab : TestLabs.ALL_LABS) {			
			//handler.init(lab);
			CLSHandler handler = new CLSHandler(lab);

			String orderingProvider = handler.getOrderingProvider();
			assertTrue("Test, Physician - p-Test Physician".equals(orderingProvider) ||
					"Unknown1, Physician, MD".equals(orderingProvider));
			
			String orderingProviderId = handler.getOrderingProviderId();
			assertTrue("1001745".equals(orderingProviderId) || 
					"1000000".equals(orderingProviderId));
			
			// FIXME-legacy verify how to obtain copy-to provider info -- handler.getCCDocs()
			// FIXME-legacy verify if the name / id breakdown is correct
			
			i++;
			logger.info("Completed physician info compliance check for lab " + i + " successfully");
		}
	}
}
