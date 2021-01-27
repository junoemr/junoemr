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


/*
 * RxPharmacyData.java
 *
 * Created on September 29, 2004, 3:41 PM
 */

package oscar.oscarRx.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.oscarehr.common.dao.DemographicPharmacyDao;
import org.oscarehr.common.dao.PharmacyInfoDao;
import org.oscarehr.common.model.DemographicPharmacy;
import org.oscarehr.common.model.PharmacyInfo;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.log.LogAction;
import oscar.log.LogConst;

/**
 *
 * @author  Jay Gallagher
 *
 *
 */
public class RxPharmacyData {

	private PharmacyInfoDao pharmacyInfoDao = SpringUtils.getBean(PharmacyInfoDao.class);
	private DemographicPharmacyDao demographicPharmacyDao = SpringUtils.getBean(DemographicPharmacyDao.class);

   /** Creates a new instance of RxPharmacyData */
   public RxPharmacyData() {
   }

	/**
	 * Part of an effort to clean up this intermediate layer.
	 * Given a model representing a pharmacy, attempt to persist it.
	 * @param pharmacyInfo pharmacy model we want to save
	 */
	synchronized public void addPharmacy(PharmacyInfo pharmacyInfo, LoggedInInfo loggedInInfo)
	{
		pharmacyInfoDao.persist(pharmacyInfo);
		LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(),
				null,
				LogConst.ACTION_ADD,
				LogConst.CON_PHARMACY,
				LogConst.STATUS_SUCCESS,
				"Pharmacy ID: " + pharmacyInfo.getId(),
				loggedInInfo.getIp(),
				"Added pharmacy: " + pharmacyInfo.getName());
	}

	/**
	 * Update an existing pharmacy entry, and log every relevant change.
	 * The log should be detailed enough so that someone can go looking for pharmacy edits
	 * and fully reverse the changes if they were applied to the wrong entry.
	 * @param pharmacyInfo pharmacy model that contains all info that was requested to be updated
	 * @param loggedInInfo session information that we can use to determine who made this change
	 */
	public void updatePharmacy(PharmacyInfo pharmacyInfo, LoggedInInfo loggedInInfo)
	{
		PharmacyInfo oldEntry = pharmacyInfoDao.getPharmacy(pharmacyInfo.getId());
		// Build a list of all things that are different for logging purposes
		String differences = "";

		// UI doesn't allow for a null or 0-length name
		if (!oldEntry.getName().equals(pharmacyInfo.getName()))
		{
			differences += "Old name: " + oldEntry.getName() + " | New name: " + pharmacyInfo.getName() + "\n";
		}
		if (oldEntry.getAddress() != null && !oldEntry.getAddress().equals(pharmacyInfo.getAddress()))
		{
			differences += "Old address: " + oldEntry.getAddress() + " | New address: " + pharmacyInfo.getAddress() + "\n";
		}
		if (oldEntry.getCity() != null && !oldEntry.getCity().equals(pharmacyInfo.getCity()))
		{
			differences += "Old city: " + oldEntry.getCity() + " | New city: " + pharmacyInfo.getCity() + "\n";
		}
		if (oldEntry.getProvince() != null && !oldEntry.getProvince().equals(pharmacyInfo.getProvince()))
		{
			differences += "Old province: " + oldEntry.getProvince() + " | New province: " + pharmacyInfo.getProvince() + "\n";
		}
		if (oldEntry.getPostalCode() != null && !oldEntry.getPostalCode().equals(pharmacyInfo.getPostalCode()))
		{
			differences += "Old postal code: " + oldEntry.getPostalCode()
					+ " | New postal code: " + pharmacyInfo.getPostalCode() + "\n";
		}
		if (oldEntry.getPhone1() != null && !oldEntry.getPostalCode().equals(pharmacyInfo.getPostalCode()))
		{
			differences += "Old phone1: " + oldEntry.getPhone1()
					+ " | New phone1: " + pharmacyInfo.getPhone1() + "\n";
		}
		if (oldEntry.getPhone2() != null && !oldEntry.getPhone2().equals(pharmacyInfo.getPhone2()))
		{
			differences += "Old phone2: " + oldEntry.getPhone2()
					+ " | New phone2: " + pharmacyInfo.getPhone2() + "\n";
		}
		if (!oldEntry.getFax().equals(pharmacyInfo.getFax()))
		{
			differences += "Old fax: " + oldEntry.getFax() + " | New fax: " + pharmacyInfo.getFax() + "\n";
		}
		if (oldEntry.getEmail() != null && !oldEntry.getEmail().equals(pharmacyInfo.getEmail()))
		{
			differences += "Old email: " + oldEntry.getEmail() + " | New email: "+ pharmacyInfo.getEmail() + "\n";
		}

		pharmacyInfoDao.merge(pharmacyInfo);
		LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(),
				null,
				LogConst.ACTION_UPDATE,
				LogConst.CON_PHARMACY,
				LogConst.STATUS_SUCCESS,
				"Pharmacy ID: " + pharmacyInfo.getId(),
				loggedInInfo.getIp(),
				differences);
	}

   /**
    * set the status of the pharmacy to 0, this will not be found in the getAllPharmacy queries
    * @param ID
    */
   public void deletePharmacy(String ID){
	   
	   List<DemographicPharmacy> demographicPharmacies = demographicPharmacyDao.findAllByPharmacyId(Integer.parseInt(ID));
	   
	   for( DemographicPharmacy demographicPharmacy : demographicPharmacies ) {
		   demographicPharmacyDao.unlinkPharmacy(Integer.parseInt(ID), demographicPharmacy.getDemographicNo());
	   }
	   
	   pharmacyInfoDao.deletePharmacy(Integer.parseInt(ID));
   }

   /**
    * Returns the latest data about a pharmacy.
    * @param ID pharmacy id
    * @return returns a pharmacy class corresponding latest data from the pharmacy ID
    */
   public PharmacyInfo getPharmacy(String ID){
      PharmacyInfo pharmacyInfo = pharmacyInfoDao.getPharmacy(Integer.parseInt(ID));
      return pharmacyInfo;
   }

   /**
    * Returns the data about a pharmacy record.  This would be used to see prior addresses or phone numbers of a pharmacy.
    * @param recordID pharmacy Record ID
    * @return Pharmacy data class
    */
   public PharmacyInfo getPharmacyByRecordID(String recordID){
      return pharmacyInfoDao.getPharmacyByRecordID(Integer.parseInt(recordID));
   }


   /**
    * Used to get a list of all the active pharmacies with their latest data
    * @return ArrayList of Pharmacy classes
    */
   public List<PharmacyInfo> getAllPharmacies(){
      return pharmacyInfoDao.getAllPharmacies();
   }

   /**
    * Used to link a patient with a pharmacy.
    * @param pharmacyId Id of the pharmacy
    * @param demographicNo Patient demographic number
    */
   public PharmacyInfo addPharmacyToDemographic(String pharmacyId,String demographicNo, String preferredOrder){
      demographicPharmacyDao.addPharmacyToDemographic(Integer.parseInt(pharmacyId), Integer.parseInt(demographicNo), Integer.parseInt(preferredOrder));
      
      PharmacyInfo pharmacyInfo = pharmacyInfoDao.find(Integer.parseInt(pharmacyId));
      pharmacyInfo.setPreferredOrder(Integer.parseInt(preferredOrder));
      
      return pharmacyInfo;
      
   }

	/**
	 * Used to get the most recent pharmacy associated with this patient.  Returns a Pharmacy object with the latest data about that pharmacy.
	 * @param demographicNo patients demographic number
	 *
	 * @return Pharmacy data object
	 */
	public List<PharmacyInfo> getPharmacyFromDemographic(String demographicNo) {
		List<DemographicPharmacy> dpList = demographicPharmacyDao.findByDemographicId(Integer.parseInt(demographicNo));
		if (dpList.isEmpty()) {
			return null;
		}

		List<Integer> pharmacyIds = new ArrayList<Integer>(); 
		for( DemographicPharmacy demoPharmacy : dpList ) {
			pharmacyIds.add(demoPharmacy.getPharmacyId());
			MiscUtils.getLogger().debug("ADDING ID " + demoPharmacy.getPharmacyId());
		}
		
		List<PharmacyInfo> pharmacyInfos = pharmacyInfoDao.getPharmacies(pharmacyIds);
		
		for( DemographicPharmacy demographicPharmacy : dpList ) {
			for( PharmacyInfo pharmacyInfo : pharmacyInfos ) {
				if( demographicPharmacy.getPharmacyId() == pharmacyInfo.getId() ) {
					pharmacyInfo.setPreferredOrder(demographicPharmacy.getPreferredOrder());
					break;
				}
			}
		}
		
		Collections.sort(pharmacyInfos);
		return pharmacyInfos;
	}
	
	public List<String> searchPharmacyCity( String searchTerm ) {
		
		return pharmacyInfoDao.searchPharmacyByCity(searchTerm);
		
	}
	
	public List<PharmacyInfo> searchPharmacy( String searchTerm ) {
		
		String[] terms;
		String name = "", city = "";
		
		if( searchTerm.indexOf(",") > -1 ) {
			terms = searchTerm.split(",",-1);
			
			switch(terms.length) {						
			case 2:
				city = terms[1];
			case 1:
				name = terms[0];
			}
		}
		else {
			name = searchTerm;
		}
		
		return pharmacyInfoDao.searchPharmacyByNameAddressCity(name, city);
		
	}
	
	public void unlinkPharmacy( String pharmacyId, String demographicNo ) {
		
		demographicPharmacyDao.unlinkPharmacy(Integer.parseInt(pharmacyId), Integer.parseInt(demographicNo));
		
	}
}
