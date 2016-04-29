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


package oscar.oscarPrevention;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.caisi_integrator.CaisiIntegratorManager;
import org.oscarehr.PMmodule.caisi_integrator.IntegratorFallBackManager;
import org.oscarehr.PMmodule.caisi_integrator.RemotePreventionHelper;
import org.oscarehr.caisi_integrator.ws.CachedDemographicPrevention;
import org.oscarehr.caisi_integrator.ws.CachedFacility;
import org.oscarehr.common.dao.PreventionDao;
import org.oscarehr.common.dao.PreventionExtDao;
import org.oscarehr.common.model.Prevention;
import org.oscarehr.common.model.PreventionExt;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.oscarDB.DBHandler;
import oscar.oscarDemographic.data.DemographicData;
import oscar.oscarProvider.data.ProviderData;
import oscar.util.UtilDateUtilities;

public class PreventionData {
	private static Logger log = MiscUtils.getLogger();
	private static PreventionDao preventionDao = (PreventionDao) SpringUtils.getBean("preventionDao");
	private static PreventionExtDao preventionExtDao = (PreventionExtDao) SpringUtils.getBean("preventionExtDao");

	private PreventionData() {
		// prevent instantiation
	}

	public static Integer insertPreventionData(String creator, String demoNo, String date, String providerNo, String providerName, String preventionType,
			String refused, String nextDate, String neverWarn, ArrayList<Map<String,String>> list) {
		Integer insertId = -1;
		try {
			Prevention prevention = new Prevention();
			prevention.setCreatorProviderNo(creator);
			prevention.setDemographicId(Integer.valueOf(demoNo));
			prevention.setPreventionDate(UtilDateUtilities.StringToDate(date, "yyyy-MM-dd"));
			prevention.setProviderNo(providerNo);
			prevention.setPreventionType(preventionType);
			prevention.setNextDate(UtilDateUtilities.StringToDate(nextDate, "yyyy-MM-dd"));
			prevention.setNever(neverWarn.trim().equals("1"));
			if (refused.trim().equals("1")) prevention.setRefused(true);
			else if (refused.trim().equals("2")) prevention.setIneligible(true);
			
			/* Only want to use this field if the 'other' option is selected, meaning -1 providerNo */
			if(providerName != null && !providerName.trim().isEmpty() && providerNo.equals("-1")) {
				prevention.setProviderName(providerName.trim());
			}

			preventionDao.persist(prevention);
			if (prevention.getId()==null) return insertId;

			insertId = prevention.getId();
			for (int i = 0; i < list.size(); i++) {
				Map<String,String> h = list.get(i);
				for (Map.Entry<String, String> entry : h.entrySet()) {
					if (entry.getKey() != null && entry.getValue() != null) {
						addPreventionKeyValue("" + insertId, entry.getKey(), entry.getValue());
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return insertId;
	}

	public static void addPreventionKeyValue(String preventionId, String keyval, String val) {
		try {
			PreventionExt preventionExt = new PreventionExt();
			preventionExt.setPreventionId(Integer.valueOf(preventionId));
			preventionExt.setKeyval(keyval);
			preventionExt.setVal(val);

			preventionExtDao.persist(preventionExt);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static Map<String,String> getPreventionKeyValues(String preventionId) {
		Map<String,String> h = new HashMap<String,String>();

		try {
			List<PreventionExt> preventionExts = preventionExtDao.findByPreventionId(Integer.valueOf(preventionId));
			for (PreventionExt preventionExt : preventionExts) {
				h.put(preventionExt.getkeyval(), preventionExt.getVal());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return h;
	}

	public static void deletePreventionData(String id) {
		try {
			Prevention prevention = preventionDao.find(Integer.valueOf(id));
			prevention.setDeleted(true);

			preventionDao.merge(prevention);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void setNextPreventionDate(String date, String id) {
		try {
			Prevention prevention = preventionDao.find(Integer.valueOf(id));
			prevention.setNextDate(UtilDateUtilities.StringToDate(date, "yyyy-MM-dd"));

			preventionDao.merge(prevention);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static String getProviderName(Map<String,Object> hash) {
		String name = "";
		if (hash != null) {
			String proNum =  (String) hash.get("provider_no");
			if (proNum == null || proNum.equals("-1")) {
				name = (String) hash.get("provider_name");
			}
			else {
				name = ProviderData.getProviderName(proNum);
			}
		}
		return name;
	}
	
	public static String getProviderName(Prevention prevention) {
		
		String prividerName = prevention.getProviderName();
		String providerNo = prevention.getProviderNo();
		
		/* special case where provider is not in the system and entered into the prevention record as 'other' provider
		 * In this case, rather than displaying 'system' name, load from the prevention directly.
		 * the prevention name should only be used if the provider is not in the system */
		if( providerNo.equals("-1") && prividerName != null) {
			return prividerName;
		}
		else {
			return ProviderData.getProviderName(providerNo);
		}
	}

	public static void updatetPreventionData(String id, String creator, String demoNo, String date, String providerNo, String providerName, String preventionType, String refused,
			String nextDate, String neverWarn, ArrayList<Map<String,String>> list) {
		deletePreventionData(id);
		insertPreventionData(creator, demoNo, date, providerNo, providerName, preventionType, refused, nextDate, neverWarn, list);
	}

	public static ArrayList<Map<String,Object>> getPreventionDataFromExt(String extKey, String extVal) {
		ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>();

		try {
			List<PreventionExt> preventionExts = preventionExtDao.findByKeyAndValue(extKey, extVal);
			for (PreventionExt preventionExt : preventionExts) {
				Map<String,Object> hash = getPreventionById(preventionDao.find(preventionExt.getPreventionId()).toString());
				if (hash.get("deleted") != null && ((String) hash.get("deleted")).equals("0")) {
					list.add(hash);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return list;
	}

    /*
     * Fetch one extended prevention key
     * Requires prevention id and keyval to return
     */
    public static String getExtValue(String id, String keyval) {
    	try {
    		List<PreventionExt> preventionExts = preventionExtDao.findByPreventionIdAndKey(Integer.valueOf(id), keyval);
    		for (PreventionExt preventionExt : preventionExts) {
    			return preventionExt.getVal();
    		}
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }

	// //////
	/**
	 *Method to get a list of (demographic #, prevention dates, and key values) of a certain type <injectionTppe> from a start Date to an end Date with a Ext key value EG get all
	 * Rh injection's product #, from 2006-12-12 to 2006-12-18
	 *
	 */
	public static ArrayList<Map<String,Object>> getExtValues(String injectionType, Date startDate, Date endDate, String keyVal) {
		ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>();

		List<Prevention> preventions = preventionDao.findByTypeAndDate(injectionType, startDate, endDate);
		for (Prevention prevention : preventions) {

			List<PreventionExt> preventionExts = preventionExtDao.findByPreventionIdAndKey(prevention.getId(), keyVal);
			try {
				for (PreventionExt preventionExt : preventionExts) {
					Map<String,Object> h = new HashMap<String,Object>();
	                h.put("preventions_id", prevention.getId().toString());
					h.put("demographic_no", prevention.getDemographicId().toString());
					h.put("val", preventionExt.getVal());
					h.put("prevention_date", prevention.getPreventionDate());
					list.add(h);
					break;
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return list;
	}

	public static Date getDemographicDateOfBirth(String demoNo)
	{
		DemographicData dd = new DemographicData();
		return(dd.getDemographicDOB(demoNo));
	}

	public static ArrayList<Map<String,Object>> getPreventionData(String demoNo) {
		return getPreventionData(null, demoNo);
	}

	public static ArrayList<Map<String,Object>> getPreventionData(String preventionType, String demoNo) {
		ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>();

		try {
			Date dob = getDemographicDateOfBirth(demoNo);
			Integer demographicId = Integer.valueOf(demoNo);
			List<Prevention> preventions = preventionType==null ? preventionDao.findNotDeletedByDemographicId(demographicId) : preventionDao.findByTypeAndDemoNo(preventionType, demographicId);
			for (Prevention prevention : preventions) {
				
				/*
				 * force case sensitive comparison of name; MySQL by default does case INsensitive
				 * DTaP and dTap are considered the same by MySQL
				 */
				if( preventionType != null && !prevention.getPreventionType().equals(preventionType) ) {
					continue;
				}
				
				Map<String,Object> h = new HashMap<String,Object>();
				h.put("id", prevention.getId().toString());
				h.put("refused", prevention.isRefused()?"1":prevention.isIneligible()?"2":"0");
				h.put("type", prevention.getPreventionType());
				h.put("provider_no", prevention.getProviderNo());
				h.put("provider_name", getProviderName(prevention));

				Date pDate = prevention.getPreventionDate();
				h.put("prevention_date", blankIfNull(UtilDateUtilities.DateToString(pDate, "yyyy-MM-dd")));
				h.put("prevention_date_asDate", pDate);

				String age = "N/A";
				if (pDate != null) {
					age = UtilDateUtilities.calcAgeAtDate(dob, pDate);
				}
				h.put("age", age);
				list.add(h);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return list;
	}

	public static ArrayList<HashMap<String,Object>> getLinkedRemotePreventionData(String preventionType, Integer localDemographicId) {
		ArrayList<HashMap<String, Object>> allResults=RemotePreventionHelper.getLinkedPreventionDataMap(localDemographicId);
		ArrayList<HashMap<String, Object>> filteredResults=new ArrayList<HashMap<String, Object>>();

		for (HashMap<String, Object> temp : allResults) {
			if (preventionType.equals(temp.get("type"))) {
				filteredResults.add(temp);
			}
		}

		return(filteredResults);
	}

	public static String getPreventionComment(String id) {
		log.debug("Calling getPreventionComment " + id);
		String comment = null;

		try {
			List<PreventionExt> preventionExts = preventionExtDao.findByPreventionIdAndKey(Integer.valueOf(id),	"comments");
			for (PreventionExt preventionExt : preventionExts) {
				comment = preventionExt.getVal();
				if (comment!=null && comment.trim().equals("")) comment = null;
				break;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return comment;
	}

	public static oscar.oscarPrevention.Prevention getPrevention(String demoNo) {
		DemographicData dd = new DemographicData();
		java.util.Date dob = getDemographicDateOfBirth(demoNo);
		String sex = dd.getDemographicSex(demoNo);
		String sql = null;
		oscar.oscarPrevention.Prevention p = new oscar.oscarPrevention.Prevention(sex, dob);

		try {

			ResultSet rs;
			sql = "Select * from preventions where  demographic_no = '" + demoNo + "'  and deleted != 1 order by prevention_type,prevention_date";
			log.debug(sql);
			rs = DBHandler.GetSQL(sql);
			while (rs.next()) {
				PreventionItem pi = new PreventionItem(oscar.Misc.getString(rs, "prevention_type"), rs.getDate("prevention_date"), oscar.Misc.getString(rs, "never"), rs
						.getDate("next_date"), rs.getString("refused"));
				p.addPreventionItem(pi);
			}
		}
		catch (SQLException e) {
			log.error(e.getMessage(), e);
			log.error(sql);
		}
		return p;
	}

	private static List<CachedDemographicPrevention> getRemotePreventions(Integer demographicId) {
		
		List<CachedDemographicPrevention> remotePreventions  = null;
		if (LoggedInInfo.loggedInInfo.get().currentFacility.isIntegratorEnabled()) {
			
			try {
				if (!CaisiIntegratorManager.isIntegratorOffline()){
					remotePreventions = CaisiIntegratorManager.getLinkedPreventions(demographicId);
				}
			} catch (Exception e) {
				MiscUtils.getLogger().error("Unexpected error.", e);
				CaisiIntegratorManager.checkForConnectionError(e);
			}
				
			if(CaisiIntegratorManager.isIntegratorOffline()){
				remotePreventions = IntegratorFallBackManager.getRemotePreventions(demographicId);
			} 
		}
		return (remotePreventions);
	}

	public static oscar.oscarPrevention.Prevention addRemotePreventions(oscar.oscarPrevention.Prevention prevention, Integer demographicId) {
		List<CachedDemographicPrevention> remotePreventions = getRemotePreventions(demographicId);

		if (remotePreventions != null) {
			for (CachedDemographicPrevention cachedDemographicPrevention : remotePreventions) {
				Date preventionDate = MiscUtils.toDate(cachedDemographicPrevention.getPreventionDate());

				PreventionItem pItem = new PreventionItem(cachedDemographicPrevention.getPreventionType(), preventionDate);
				pItem.setRemoteEntry(true);
				prevention.addPreventionItem(pItem);
			}
		}

		return (prevention);
	}

	public static ArrayList<Map<String,Object>> addRemotePreventions(ArrayList<Map<String,Object>> preventions, Integer demographicId, String preventionType, Date demographicDateOfBirth) {
		List<CachedDemographicPrevention> remotePreventions = getRemotePreventions(demographicId);

		if (remotePreventions != null) {
			for (CachedDemographicPrevention cachedDemographicPrevention : remotePreventions) {
				if (preventionType.equals(cachedDemographicPrevention.getPreventionType())) {

					Map<String,Object> h = new HashMap<String,Object>();
					h.put("integratorFacilityId", cachedDemographicPrevention.getFacilityPreventionPk().getIntegratorFacilityId());
					h.put("integratorPreventionId", cachedDemographicPrevention.getFacilityPreventionPk().getCaisiItemId());
					String remoteFacilityName="N/A";
					CachedFacility remoteFacility=null;
					try {
						remoteFacility = CaisiIntegratorManager.getRemoteFacility(cachedDemographicPrevention.getFacilityPreventionPk().getIntegratorFacilityId());
					}
					catch (Exception e) {
						log.error("Error", e);
					}
					if (remoteFacility!=null) remoteFacilityName=remoteFacility.getName();
					h.put("remoteFacilityName", remoteFacilityName);
					h.put("integratorDemographicId", cachedDemographicPrevention.getCaisiDemographicId());
					h.put("type", cachedDemographicPrevention.getPreventionType());
					h.put("provider_no", "remote:" + cachedDemographicPrevention.getCaisiProviderId());
					h.put("provider_name", "remote:" + cachedDemographicPrevention.getCaisiProviderId());
					h.put("prevention_date", DateFormatUtils.ISO_DATE_FORMAT.format(cachedDemographicPrevention.getPreventionDate()));
					h.put("prevention_date_asDate", cachedDemographicPrevention.getPreventionDate());

					if (demographicDateOfBirth != null) {
						String age = UtilDateUtilities.calcAgeAtDate(demographicDateOfBirth, MiscUtils.toDate(cachedDemographicPrevention.getPreventionDate()));
						h.put("age", age);
					}
					else
					{
						h.put("age", "N/A");
					}

					preventions.add(h);
				}
			}

			Collections.sort(preventions, new PreventionsComparator());
		}

		return(preventions);
	}

	public static class PreventionsComparator implements Comparator<Map<String,Object>>
	{
		public int compare(Map<String,Object> o1, Map<String,Object> o2) {
			Comparable date1=(Comparable)o1.get("prevention_date_asDate");
			Comparable date2=(Comparable)o2.get("prevention_date_asDate");

			if (date1!=null && date2!=null)
			{
				if (date1 instanceof Calendar)
				{
					date1=((Calendar)date1).getTime();
				}

				if (date2 instanceof Calendar)
				{
					date2=((Calendar)date2).getTime();
				}

				return(date1.compareTo(date2));
			}
			else
			{
				return(0);
			}
		}
	}

	public static Map<String,Object> getPreventionById(String id) {
		Map<String,Object> h = null;

		try {
			Prevention prevention = preventionDao.find(Integer.valueOf(id));
			if (prevention!=null) {
				h = new HashMap<String,Object>();
				String providerName = getProviderName(prevention);
				String preventionDate = UtilDateUtilities.DateToString(prevention.getPreventionDate(), "yyyy-MM-dd");


				addToHashIfNotNull(h, "id", prevention.getId().toString());
				addToHashIfNotNull(h, "demographicNo", prevention.getDemographicId().toString());
				addToHashIfNotNull(h, "provider_no", prevention.getProviderNo());
				addToHashIfNotNull(h, "providerName", providerName);
				addToHashIfNotNull(h, "creationDate", UtilDateUtilities.DateToString(prevention.getCreationDate(), "yyyy-MM-dd"));
				addToHashIfNotNull(h, "preventionDate", preventionDate);
				addToHashIfNotNull(h, "prevention_date_asDate", prevention.getPreventionDate());
				addToHashIfNotNull(h, "preventionType", prevention.getPreventionType());
				addToHashIfNotNull(h, "deleted", prevention.isDeleted()?"1":"0");
				addToHashIfNotNull(h, "refused", prevention.isRefused()?"1":prevention.isIneligible()?"2":"0");
				addToHashIfNotNull(h, "next_date", UtilDateUtilities.DateToString(prevention.getNextDate(), "yyyy-MM-dd"));
				addToHashIfNotNull(h, "never", prevention.isNever()?"1":"0");

				String summary = "Prevention " + prevention.getPreventionType() + " provided by " + providerName + " on " + preventionDate + "\n";
				Map<String,String> ext = getPreventionKeyValues(prevention.getId().toString());
				if (ext.containsKey("result")) {
					summary += "Result: " + ext.get("result");
					if (ext.containsKey("reason") && !ext.get("reason").equals("")) {
						summary += "\nReason: " + ext.get("reason");
					}
				} else {
					if (ext.containsKey("name") && !ext.get("name").equals("")) {
						addToHashIfNotNull(h, "name",  ext.get("name"));
						summary += "Name: " + ext.get("name") + "\n";
					}
					if (ext.containsKey("location") && !ext.get("location").equals("")) {
						addToHashIfNotNull(h, "location",  ext.get("location"));
						summary += "Location: " + ext.get("location") + "\n";
					}
					if (ext.containsKey("route") && !ext.get("route").equals("")) {
						addToHashIfNotNull(h, "route",  ext.get("route"));
						summary += "Route: " + ext.get("route") + "\n";
					}
					if (ext.containsKey("dose") && !ext.get("dose").equals("")) {
						addToHashIfNotNull(h, "dose",  ext.get("dose"));
						summary += "Dose: " + ext.get("dose") + "\n";
					}
					if (ext.containsKey("lot") && !ext.get("lot").equals("")) {
						addToHashIfNotNull(h, "lot",  ext.get("lot"));
						summary += "Lot: " + ext.get("lot") + "\n";
					}
					if (ext.containsKey("manufacture") && !ext.get("manufacture").equals("")) {
						addToHashIfNotNull(h, "manufacture",  ext.get("manufacture"));
						summary += "Manufacturer: " + ext.get("manufacture") + "\n";
					}
					if (ext.containsKey("comments") && !ext.get("comments").equals("")) {
						addToHashIfNotNull(h, "comments",  ext.get("comments"));
						summary += "Comments: " + ext.get("comments");
					}
				}
				addToHashIfNotNull(h, "summary", summary);
				log.debug("1" + h.get("preventionType") + " " + h.size());
				log.debug("id" + h.get("id"));

			}
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return h;
	}

	private static void addToHashIfNotNull(Map<String,Object> h, String key, String val) {
		if (val != null && !val.equalsIgnoreCase("null")) {
			h.put(key, val);
		}
	}

	private static void addToHashIfNotNull(Map<String,Object> h, String key, Date val) {
		if (val != null) {
			h.put(key, val);
		}
	}

	private static String blankIfNull(String s) {
		if (s == null) return "";
		return s;
	}
}
