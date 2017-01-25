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


package org.oscarehr.appointment.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.dao.ScheduleDateDao;
import org.oscarehr.common.dao.ScheduleTemplateCodeDao;
import org.oscarehr.common.dao.ScheduleTemplateDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.ScheduleDate;
import org.oscarehr.common.model.ScheduleTemplate;
import org.oscarehr.common.model.ScheduleTemplateCode;
import org.oscarehr.common.model.ScheduleTemplatePrimaryKey;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

public class NextAppointmentSearchHelper {
	static final int MAX_DAYS_TO_SEARCH = 180;

	static Logger logger = MiscUtils.getLogger();
	static ProviderDao providerDao = (ProviderDao)SpringUtils.getBean("providerDao");			 	
	static ScheduleDateDao scheduleDateDao = (ScheduleDateDao)SpringUtils.getBean("scheduleDateDao");
	static ScheduleTemplateDao scheduleTemplateDao = (ScheduleTemplateDao)SpringUtils.getBean("scheduleTemplateDao");
	static ScheduleTemplateCodeDao scheduleTemplateCodeDao = (ScheduleTemplateCodeDao)SpringUtils.getBean("scheduleTemplateCodeDao");
	static OscarAppointmentDao oscarAppointmentDao = (OscarAppointmentDao)SpringUtils.getBean("oscarAppointmentDao");
	
	/**
	 * Search against schedule for next appointment.
	 * 
	 * This implementation searches day by day until searchBean.numberOfResults is realized or MAX_DAYS_TO_SEARCH is reached
	 * 
	 * @param searchBean
	 * @return List of type NextAppointmentSearchResult
	 */
	public static List<NextAppointmentSearchResult> search(NextAppointmentSearchBean searchBean) {
		List<NextAppointmentSearchResult> results = new ArrayList<NextAppointmentSearchResult>();
		
		String provider_no = searchBean.getProviderNo().trim();
		boolean searchAllProviders = provider_no.equals("");
		logger.info("SEARCH AVAILABLE APPOINTMENTS: " + provider_no);
				
		Calendar c = Calendar.getInstance();
		/* hard limit on the number of templates we retrieve from the database
		 * if we can't find any free appointments before reaching this limit they probably don't have any. */
		int maxResults = 500 ; 
		
		// build a list of provider numbers to include in the search
		ArrayList<String> providerNos = new ArrayList<String>();
		
		// search all providers
		if(searchAllProviders) {
			List<Provider> providers = providerDao.getActiveProviders();
			for(Provider p:providers) {
				providerNos.add(p.getProviderNo());
			}
		}
		// search specific provider
		else {
			providerNos.add(provider_no);
		}
		
		ArrayList<Integer> daysOfWeek = getDaysOfWeek(searchBean);
		
		Date currentTime = c.getTime();
		c.add(Calendar.DAY_OF_MONTH, MAX_DAYS_TO_SEARCH);
		List<ScheduleDate> scheduleDateList = scheduleDateDao.findByProviderListAndDateRange(providerNos, currentTime, c.getTime(), maxResults, daysOfWeek);
		
		/* the calendar comparison is a hack to ensure all provider templates for each day are loaded before checking the break condition.
		 * this ensures all templates are included when sorting by appointment time. only effects multiple provider search results */
		Calendar sdCal = Calendar.getInstance();
		Calendar lastSdCal = Calendar.getInstance();
		if(!scheduleDateList.isEmpty()) {
			lastSdCal.setTime(scheduleDateList.get(0).getDate());
		}
		int templatesUsed = 0;
		for(ScheduleDate sd : scheduleDateList) {
			results.addAll(searchTemplate(sd.getProviderNo(), sd.getHour(), sd.getDate(), searchBean));
			templatesUsed++;
			/* since each DB result can have multiple openings, we can skip the later ones if there are 
			 * more than the max amount being displayed. Can't do this with multiple providers since this causes
			 * only a single provider to show up; because all free times were from the first template loaded.*/
			sdCal.setTime(sd.getDate());
			if( !(searchAllProviders && lastSdCal.get(Calendar.DAY_OF_YEAR) != sdCal.get(Calendar.DAY_OF_YEAR)) 
					&& results.size() >= searchBean.getNumResults()) {
				break;
			}
			lastSdCal.setTime(sd.getDate());
		}
		logger.info(templatesUsed + " schedule templates searched.");
		logger.info(results.size() + " available appointments found.");
		
		Collections.sort(results, new NextAppointmentSearchResultDateComparator());
		
		// trim the sorted list to the proper size
		if (searchBean.getNumResults() < results.size()) {
			return results.subList(0, searchBean.getNumResults());
		}
		return results;
	}
	
	/**
	 * helper method for finding days of the week. 
	 * @param searchBean
	 * @return an array of integer values (1 - 7) representing days of the week to search.
	 * Where 1=Sunday and 7=Saturday
	 */
	private static ArrayList<Integer> getDaysOfWeek(NextAppointmentSearchBean searchBean) {
		ArrayList<Integer> daysOfTheWeek = new ArrayList<Integer>();
		
		if(!searchBean.getDayOfWeek().isEmpty() && searchBean.getDayOfWeek().equals("daily")) {
			// Add all days of the week except Saturday(7) & Sunday(1)
			for(int i=2; i<=6; i++) {
				daysOfTheWeek.add(i);
			}
		}
		else {
			try {
				daysOfTheWeek.add(Integer.parseInt(searchBean.getDayOfWeek()));
			}
			catch (NumberFormatException e) {
				logger.error("Error", e);
				
				// all days of the week 1 -7
				for(int i=1; i<=7; i++) {
					daysOfTheWeek.add(i);
				}
			}
		}
		return daysOfTheWeek;
	}
	
	/**
	 * Retrieve template formatted NextAppointmentSearchResult list for the given provider and schedule date
	 * @param providerNo
	 * @param templateName
	 * @param day
	 * @param searchBean
	 * @return
	 */
	private static List<NextAppointmentSearchResult> searchTemplate(String providerNo, String templateName, Date day, NextAppointmentSearchBean searchBean) {
		
		//we have a schedule..lets check what template to use
		ScheduleTemplate template = scheduleTemplateDao.find(new ScheduleTemplatePrimaryKey(providerNo,templateName));
		
		/* hack to look for public templates */
		if(template == null) {
			logger.debug("No Private template found for provider " + providerNo + ". Search for public template '" + templateName + "'");
			template = scheduleTemplateDao.find(new ScheduleTemplatePrimaryKey(ScheduleTemplatePrimaryKey.DODGY_FAKE_PROVIDER_NO_USED_TO_HOLD_PUBLIC_TEMPLATES,templateName));
		}
		if(template == null) {
			logger.warn("no template found for provider " + providerNo + " and template name '" + templateName + "'");
			return new ArrayList<NextAppointmentSearchResult>();
		}
		return formatTemplateResults(providerNo, template, day, searchBean);
	}
	
	private static List<NextAppointmentSearchResult> formatTemplateResults(String providerNo, ScheduleTemplate template, Date day, NextAppointmentSearchBean searchBean) {
		List<NextAppointmentSearchResult> results = new ArrayList<NextAppointmentSearchResult>();
		
		String timecode = template.getTimecode();   //length=96
		int slotsPerHour = (timecode.length()/24);  //4
		int slotSize = (60/(timecode.length()/24)); //15
		
		//check to see which slots are available between the start/end times - build a map.
		int startHour = Integer.parseInt(searchBean.getStartTimeOfDay());
		int startMin = 0;
		int endHour = Integer.parseInt(searchBean.getEndTimeOfDay());
		
		for(int x=0;x<timecode.length();x++) {
			char slot = timecode.charAt(x);
			int hour = (int)Math.floor(x/slotsPerHour);
			int min = (x%slotsPerHour)*slotSize;
			if(( hour >= startHour ) && ( hour < endHour )) {
				if(hour==startHour && min<startMin) {
					continue;
				}
				//logger.info("currently at position " + x + " which is hour " + hour + " and min " + min);
				if(slot != '_') {
					//filter by code
					if(searchBean.getCode().length()>0) {
						if(slot != searchBean.getCode().charAt(0)) {
							logger.debug("skipping because code doesn't match, slot=" + slot + ",code=" + searchBean.getCode().charAt(0) + ".");
							continue;
						}
					}
					
					//TODO: is there a default appt length somewhere?					
					int duration = 15;
					if(searchBean.getCode().length()>0) {
						//load the template code
						ScheduleTemplateCode stc = scheduleTemplateCodeDao.getByCode(searchBean.getCode().charAt(0));
						if(stc == null) {
							logger.error("Error - ScheduleTemplateCode not found!!!");
							continue;
						}
						//check the duration						
						if(stc.getDuration() != null && stc.getDuration().length()>0 ) {
							duration = Integer.parseInt(stc.getDuration());
						}
					}
					
					//ready to check appointments
					//logger.info("schedule availability found at hour " + hour + ", min = " + min + " duration = " + duration);
					
					Calendar currentTimeCal = Calendar.getInstance();
					Calendar appointmentCal = Calendar.getInstance();
					appointmentCal.setTime(day);
					appointmentCal.set(Calendar.HOUR_OF_DAY, hour);
					appointmentCal.set(Calendar.MINUTE, min);
					appointmentCal.set(Calendar.SECOND,0);
					appointmentCal.set(Calendar.MILLISECOND, 0);
					
					// skip time slots that are in the past
					if ( currentTimeCal.after(appointmentCal) ) {
						continue;
					}
					if(checkAvailability(appointmentCal.getTime(), duration, providerNo)) {
						//logger.info("spot available at " + cal2.getTime() + " for " + duration + " mins with provider " + providerNo);
						NextAppointmentSearchResult result = new NextAppointmentSearchResult();
						result.setProviderNo(providerNo);
						result.setProvider(providerDao.getProvider(providerNo));
						result.setDate(appointmentCal.getTime());
						result.setDuration(duration);
						results.add(result);
					} 
				}
			}
		}
		return results;
	}
	
    private static boolean checkAvailability(Date date, int duration, String providerNo) {
    	List<Appointment> rs = oscarAppointmentDao.getByProviderAndDay(date,providerNo);
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	Date startTime = cal.getTime();
    	cal.add(Calendar.MINUTE, (duration-1));
    	Date endTime = cal.getTime();
    	
    	//MiscUtils.getLogger().info("checking availability - startTime:"+startTime + ",endTime="+endTime);
    	//startTime + duration = endTime
    	//we are wanting to make sure no appointments have overlapping time
    	boolean booked=false;
    	for(Appointment a:rs) {
    		Date apptStartDate = fixDate(date,a.getStartTime());
    		Date apptEndDate = fixDate(date,a.getEndTime());
    		//MiscUtils.getLogger().info("\tappt found @ startTime:"+apptStartDate + ",endTime="+apptEndDate);
        	
    		if(endTime.before(apptStartDate)) {
    			continue;
    		}
    		if(startTime.after(apptEndDate)) {
    			continue;
    		}
    		booked=true;
    	}
    	if(booked) {
    		return false;
    	}
    	
    	//available
    	return true;    	
    }
    
    private static Date fixDate(Date day, Date time) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(day);
    	Calendar acal = Calendar.getInstance();
    	acal.setTime(time);
    	cal.set(Calendar.HOUR_OF_DAY, acal.get(Calendar.HOUR_OF_DAY));
    	cal.set(Calendar.MINUTE, acal.get(Calendar.MINUTE));
    	cal.set(Calendar.SECOND,0);
    	cal.set(Calendar.MILLISECOND, 0);
    	return cal.getTime();
    }
}
