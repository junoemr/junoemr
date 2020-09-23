/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * <p>
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.managers;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.velocity.VelocityContext;
import org.oscarehr.PMmodule.dao.ProgramAccessDAO;
import org.oscarehr.PMmodule.dao.ProgramProviderDAO;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.PMmodule.model.ProgramAccess;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.casemgmt.service.CaseManagementManager;
import org.oscarehr.common.dao.ClinicDAO;
import org.oscarehr.common.dao.CustomFilterDao;
import org.oscarehr.common.dao.TicklerCategoryDao;
import org.oscarehr.common.dao.TicklerCommentDao;
import org.oscarehr.common.dao.TicklerDao;
import org.oscarehr.common.dao.TicklerTextSuggestDao;
import org.oscarehr.common.dao.TicklerUpdateDao;
import org.oscarehr.common.model.Clinic;
import org.oscarehr.common.model.CustomFilter;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.Tickler;
import org.oscarehr.common.model.TicklerCategory;
import org.oscarehr.common.model.TicklerComment;
import org.oscarehr.common.model.TicklerLink;
import org.oscarehr.common.model.TicklerTextSuggest;
import org.oscarehr.common.model.TicklerUpdate;
import org.oscarehr.ticklers.search.TicklerCriteriaSearch;
import org.oscarehr.ticklers.service.TicklerService;
import org.oscarehr.util.EmailUtilsOld;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.VelocityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

import oscar.OscarProperties;
import oscar.log.LogAction;

import com.quatro.model.security.Secrole;

@Service
public class TicklerManager
{

    public static String DEMOGRAPHIC_NAME = "demographic_name";
    public static String CREATOR = "creator";
    public static String SERVICE_DATE = "service_date";
    public static String CREATION_DATE = "creation_date";
    public static String PRIORITY = "priority";
    public static String TASK_ASSIGNED_TO = "task_assigned_to";
    public static String SORT_ASC = "asc";
    public static String SORT_DESC = "desc";

    private static final String TICKLER_EMAIL_TEMPLATE_FILE = "/tickler_email_notification_template.txt";
    private static final String PRIVILEGE_READ = "r";
    private static final String PRIVILEGE_WRITE = "w";
    private static final String PRIVILEGE_UPDATE = "u";

    @Autowired
    private ProgramAccessDAO programAccessDAO;

    @Autowired
    private ProgramProviderDAO programProviderDAO;

    @Autowired
    private TicklerDao ticklerDao;

    @Autowired
    private TicklerCommentDao ticklerCommentDao;

    @Autowired
    private TicklerUpdateDao ticklerUpdateDao;

    @Autowired
    private ProviderDao providerDao;

    @Autowired
    private ProgramManager programManager;

    @Autowired
    private CaseManagementManager caseManagementManager;

    @Autowired
    private CustomFilterDao customFilterDao;

    @Autowired
    private TicklerTextSuggestDao ticklerTextSuggestDao;

    @Autowired
    private SecurityInfoManager securityInfoManager;

    @Autowired
    private TicklerCategoryDao ticklerCategoryDao;

    @Autowired
    private TicklerService ticklerService;

    public List<TicklerCategory> getActiveTicklerCategories(LoggedInInfo loggedInInfo)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_READ);

        LogAction.addLogSynchronous(loggedInInfo, "TicklerManager.getActiveTicklerCategories", "All active categories");

        return ticklerCategoryDao.getActiveCategories();
    }


    public boolean validateTicklerIsValid(Tickler tickler)
    {
        if (tickler == null)
        {
            return false;
        }
        if (StringUtils.isEmpty(tickler.getCreator()))
        {
            return false;
        }
        if (StringUtils.isEmpty(tickler.getTaskAssignedTo()))
        {
            return false;
        }
        if (tickler.getDemographicNo() == null || tickler.getDemographicNo().intValue() == 0)
        {
            return false;
        }
        return true;
    }

    public boolean addTicklerLink(LoggedInInfo loggedInInfo, TicklerLink ticklerLink)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_WRITE);
        ticklerDao.persist(ticklerLink);

        //--- log action ---
        LogAction.addLogSynchronous(loggedInInfo, "TicklerManager.addTicklerLink", "ticklerLinkId=" + ticklerLink.getId());

        return true;
    }

    public boolean addTickler(LoggedInInfo loggedInInfo, Tickler tickler)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_WRITE);

        if (!validateTicklerIsValid(tickler))
        {
            return false;
        }

        ticklerDao.persist(tickler);

        //--- log action ---
        LogAction.addLogSynchronous(loggedInInfo, "TicklerManager.addtickler", "ticklerId=" + tickler.getId());

        return true;
    }

    public boolean addUpdateToTickler(Tickler tickler, TicklerUpdate update)
    {
        ticklerUpdateDao.persist(update);
        tickler.getUpdates().add(update);
        return false;
    }

    public boolean addCommentToTickler(Tickler tickler, TicklerComment comment)
    {
        ticklerCommentDao.persist(comment);
        tickler.getComments().add(comment);
        return false;
    }

    public boolean updateTickler(LoggedInInfo loggedInInfo, Tickler tickler)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_UPDATE);

        if (!validateTicklerIsValid(tickler))
        {
            return false;
        }
        for (TicklerUpdate tu : tickler.getUpdates())
        {
            if (tu.getId() == null || tu.getId().intValue() == 0)
            {
                ticklerUpdateDao.persist(tu);
            }
        }
        for (TicklerComment tc : tickler.getComments())
        {
            if (tc.getId() == null || tc.getId().intValue() == 0)
            {
                ticklerCommentDao.persist(tc);
            }
        }
        ticklerDao.merge(tickler);

        //--- log action ---
        LogAction.addLogSynchronous(loggedInInfo, "TicklerManager.updatetickler", "ticklerId=" + tickler.getId());

        return true;
    }


    public List<Tickler> getTicklers(LoggedInInfo loggedInInfo, CustomFilter filter, String providerNo, String programId)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_READ);

        List<Tickler> results = ticklerDao.getTicklers(filter);


        if (OscarProperties.getInstance().getBooleanProperty("FILTER_ON_FACILITY", "true"))
        {
            //filter based on facility
            results = ticklerFacilityFiltering(loggedInInfo, results);

            //filter based on caisi role access
            results = filterTicklersByAccess(results, providerNo, programId);
        }

        return (results);
    }

    public List<Tickler> getTicklers(LoggedInInfo loggedInInfo, CustomFilter filter)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_READ);

        List<Tickler> results = ticklerDao.getTicklers(filter);

        return (results);
    }

    public List<Tickler> getTicklers(LoggedInInfo loggedInInfo, TicklerCriteriaSearch filter)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_READ);

        return (ticklerService.getSearchResponse(filter));
    }

    public List<Tickler> getTicklers(LoggedInInfo loggedInInfo, CustomFilter filter, int offset, int limit)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_READ);

        return ticklerDao.getTicklers(filter, offset, limit);
    }

    protected List<Tickler> ticklerFacilityFiltering(LoggedInInfo loggedInInfo, List<Tickler> ticklers)
    {
        ArrayList<Tickler> results = new ArrayList<Tickler>();

        for (Tickler tickler : ticklers)
        {
            Integer programId = tickler.getProgramId();

            if (programManager.hasAccessBasedOnCurrentFacility(loggedInInfo, programId))
            {
                results.add(tickler);
            }
        }

        return results;
    }

    protected List<Tickler> filterTicklersByAccess(List<Tickler> ticklers, String providerNo, String programNo)
    {
        List<Tickler> filteredTicklers = new ArrayList<Tickler>();

        if (ticklers.isEmpty())
        {
            return ticklers;
        }

        String programId = "";
        //iterate through the tickler list
        for (Iterator<Tickler> iter = ticklers.iterator(); iter.hasNext(); )
        {
            Tickler t = iter.next();
            boolean add = false;
            List<ProgramProvider> ppList = new ArrayList<ProgramProvider>();

            programId = String.valueOf(t.getProgramId());

            //If the ticklers are not in any program (old ticklers), show them.
            //They will not be filtered by the role access.
            if (programId == null || "".equals(programId) || "null".equals(programId))
            {
                filteredTicklers.add(t);
                continue;
            }

            //load up the program_provider entry to get the role for this provider in the tickler's program
            ppList = programProviderDAO.getProgramProviderByProviderProgramId(providerNo, new Long(programId));
            if (ppList == null || ppList.isEmpty())
            {
                continue;
            }
            ProgramProvider pp = ppList.get(0);
            Secrole role = pp.getRole();

            //Get the tickler assigned to provider's role in the tickler's program
            String ticklerRole = null;
            List<ProgramProvider> ppList2 = new ArrayList<ProgramProvider>();
            ppList2 = this.programProviderDAO.getProgramProviderByProviderProgramId(t.getTaskAssignedTo(), new Long(t.getProgramId()));
            if (ppList2 != null && !ppList2.isEmpty())
            {
                ticklerRole = ppList2.get(0).getRole().getRoleName().toLowerCase();
            }

            ProgramAccess pa = null;

            //Load up access list from program
            List<ProgramAccess> programAccessList = programAccessDAO.getAccessListByProgramId(new Long(programId));
            Map<String, ProgramAccess> programAccessMap = caseManagementManager.convertProgramAccessListToMap(programAccessList);

            //read

            //if the provider's role is allowed to read tickler's assigned to "ticklerRole". add.
            //if no entry exists, but the the provider has same role as assigned to role, then we add
            pa = programAccessMap.get("read ticklers assigned to a " + ticklerRole);
            if (pa != null)
            {
                if (pa.isAllRoles() || caseManagementManager.isRoleIncludedInAccess(pa, role))
                {
                    add = true;
                }
            }
            else
            {
                if (ticklerRole.equals(role.getRoleName()))
                {
                    add = true;
                }
            }
            pa = null;

            //if this provider wrote the tickler, they should see it..doesn't matter
            //about the role based access
            if (!add)
            {
                if (t.getProvider().getProviderNo().equals(providerNo))
                {
                    add = true;
                }

            }

            //apply defaults - i think this is already added above
            if (!add)
            {
                if (ticklerRole.equals(role.getRoleName()))
                {
                    add = true;
                }
            }

            //did it pass the test?
            if (add)
            {
                filteredTicklers.add(t);
            }
        }
        return filteredTicklers;
    }


    public int getActiveTicklerCount(LoggedInInfo loggedInInfo, String providerNo)
    {
        int result = ticklerDao.getActiveTicklerCount(providerNo);

        //--- log action ---
        LogAction.addLogSynchronous(loggedInInfo, "TicklerManager.getActiveTicklerCount", "");

        return result;
    }


    public int getNumTicklers(LoggedInInfo loggedInInfo, CustomFilter filter)
    {
        int result = ticklerDao.getNumTicklers(filter);

        //--- log action ---
        LogAction.addLogSynchronous(loggedInInfo, "TicklerManager.getNumTicklers", "");

        return result;
    }


    public Tickler getTickler(LoggedInInfo loggedInInfo, String tickler_no)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_READ);

        Integer id = Integer.valueOf(tickler_no);
        Tickler tickler = ticklerDao.find(id);

        //--- log action ---
        LogAction.addLogSynchronous(loggedInInfo, "TicklerManager.getTickler", (tickler != null) ? "id=" + tickler.getId() : "");

        return tickler;
    }

    public Tickler getTickler(LoggedInInfo loggedInInfo, Integer id)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_READ);

        Tickler tickler = ticklerDao.find(id);

        //--- log action ---
        LogAction.addLogSynchronous(loggedInInfo, "TicklerManager.getTickler", (tickler != null) ? "id=" + tickler.getId() : "");

        return tickler;
    }

    public void addComment(LoggedInInfo loggedInInfo, Integer tickler_id, String provider, String message)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_UPDATE);

        Tickler tickler = ticklerDao.find(tickler_id);
        if (tickler != null && message != null && !"".equals(message))
        {
            TicklerComment comment = new TicklerComment();
            comment.setTicklerNo(tickler_id.intValue());
            comment.setProviderNo(provider);
            comment.setMessage(message);
            ticklerCommentDao.persist(comment);

            //--- log action ---
            LogAction.addLogSynchronous(loggedInInfo, "TicklerManager.addComment", "ticklerId=" + tickler_id + ",provider=" + provider + ",message=" + message + ",id=" + comment.getId());
        }
    }

    public void reassign(LoggedInInfo loggedInInfo, Integer tickler_id, String provider, String task_assigned_to)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_UPDATE);

        Tickler tickler = ticklerDao.find(tickler_id);
        if (tickler != null && !task_assigned_to.equals(tickler.getTaskAssignedTo()))
        {
            String message;
            String former_assignee = tickler.getAssignee().getFormattedName();
            String current_assignee;
            tickler.setTaskAssignedTo(task_assigned_to);

            TicklerComment comment = new TicklerComment();
            comment.setTicklerNo(tickler_id.intValue());
            comment.setProviderNo(provider);

            current_assignee = providerDao.getProvider(task_assigned_to).getFormattedName();

            message = "RE-ASSIGNMENT RECORD: [Tickler \"" + tickler.getDemographic().getFormattedName() + "\" was reassigned from \"" + former_assignee + "\"  to \"" + current_assignee + "\"]";
            comment.setMessage(message);

            ticklerCommentDao.persist(comment);
            ticklerDao.merge(tickler);

            //--- log action ---
            LogAction.addLogSynchronous(loggedInInfo, "TicklerManager.reassign", "ticklerId=" + tickler_id + ",provider=" + provider + ",task_assigned_to=" + task_assigned_to);
        }
    }

    public void updateStatus(LoggedInInfo loggedInInfo, Integer tickler_id, String provider, Tickler.STATUS status)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_UPDATE);

        Tickler tickler = ticklerDao.find(tickler_id);
        if (tickler != null && tickler.getStatus() != null && tickler.getStatus() != null && !status.equals(tickler.getStatus()))
        {
            tickler.setStatus(status);
            TicklerUpdate update = new TicklerUpdate();
            update.setProviderNo(provider);
            update.setStatus(status);
            update.setTicklerNo(tickler_id.intValue());
            update.setUpdateDate(new Date());

            ticklerUpdateDao.persist(update);
            ticklerDao.merge(tickler);

            //--- log action ---
            LogAction.addLogSynchronous(loggedInInfo, "TicklerManager.updateStatus", "ticklerId=" + tickler_id + ",provider=" + provider + ",status=" + status);
        }
    }

    public void sendNotification(LoggedInInfo loggedInInfo, Tickler t) throws EmailException, IOException
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_READ);

        if (t == null)
        {
            throw new IllegalArgumentException("Tickler object required to send tickler email");
        }

        boolean ticklerEditEnabled = Boolean.parseBoolean(OscarProperties.getInstance().getProperty("tickler_edit_enabled"));
        boolean ticklerEmailEnabled = Boolean.parseBoolean(OscarProperties.getInstance().getProperty("tickler_email_enabled"));

        if (ticklerEditEnabled & ticklerEmailEnabled)
        {
            String emailTo = t.getDemographic().getEmail();
            if (EmailUtilsOld.isValidEmailAddress(emailTo))
            {

                InputStream is = TicklerManager.class.getResourceAsStream(TICKLER_EMAIL_TEMPLATE_FILE);
                String emailTemplate = IOUtils.toString(is);
                String emailSubject = OscarProperties.getInstance().getProperty("tickler_email_subject");
                String emailFrom = OscarProperties.getInstance().getProperty("tickler_email_from_address");

                ClinicDAO clinicDao = (ClinicDAO) SpringUtils.getBean("clinicDAO");
                Clinic c = clinicDao.getClinic();

                VelocityContext velocityContext = VelocityUtils.createVelocityContextWithTools();
                velocityContext.put("tickler", t);
                velocityContext.put("clinic", c);

                String mergedSubject = VelocityUtils.velocityEvaluate(velocityContext, emailSubject);
                String mergedBody = VelocityUtils.velocityEvaluate(velocityContext, emailTemplate);

                EmailUtilsOld.sendEmail(emailTo, null, emailFrom, null, mergedSubject, mergedBody, null);

                //--- log action ---
                LogAction.addLogSynchronous(loggedInInfo, "TicklerManager.sendNotification", "ticklerId=" + t.getId());
            }
            else
            {
                throw new EmailException("Email Address is invalid");
            }
        }
    }

    public void completeTickler(LoggedInInfo loggedInInfo, Integer tickler_id, String provider)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_UPDATE);

        updateStatus(loggedInInfo, tickler_id, provider, Tickler.STATUS.C);
    }

    public void deleteTickler(LoggedInInfo loggedInInfo, Integer tickler_id, String provider)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_UPDATE);

        updateStatus(loggedInInfo, tickler_id, provider, Tickler.STATUS.D);
    }

    public void activateTickler(LoggedInInfo loggedInInfo, Integer tickler_id, String provider)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_UPDATE);

        updateStatus(loggedInInfo, tickler_id, provider, Tickler.STATUS.A);
    }

    public void resolveTicklersBySubstring(LoggedInInfo loggedInInfo, String providerNo, List<String> demographicIds, String remString)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_UPDATE);

        List<Integer> tmp = new ArrayList<Integer>();
        for (String str : demographicIds)
        {
            tmp.add(Integer.parseInt(str));
        }
        List<Tickler> ticklers = ticklerDao.findActiveByMessageForPatients(tmp, remString);
        for (Tickler t : ticklers)
        {
            deleteTickler(loggedInInfo, t.getId(), providerNo);
        }
    }


    public List<CustomFilter> getCustomFilters()
    {
        return customFilterDao.getCustomFilters();
    }

    public List<CustomFilter> getCustomFilters(String provider_no)
    {
        return customFilterDao.findByProviderNo(provider_no);
    }

    public List<CustomFilter> getCustomFilterWithShortCut(String providerNo)
    {
        return customFilterDao.getCustomFilterWithShortCut(providerNo);
    }

    public CustomFilter getCustomFilter(String name)
    {
        return customFilterDao.findByName(name);
    }

    public CustomFilter getCustomFilter(String name, String providerNo)
    {
        return customFilterDao.findByNameAndProviderNo(name, providerNo);
    }

    public CustomFilter getCustomFilterById(Integer id)
    {
        return customFilterDao.find(id);
    }

    public void saveCustomFilter(CustomFilter filter)
    {
        if (filter.getId() == null || filter.getId().intValue() == 0)
        {
            customFilterDao.persist(filter);
        }
        else
        {
            customFilterDao.merge(filter);
        }
    }

    public void deleteCustomFilter(String name)
    {
        customFilterDao.deleteCustomFilter(name);
    }

    public void deleteCustomFilterById(Integer id)
    {
        customFilterDao.remove(id);
    }


    public void addTickler(String demographic_no, String message, Tickler.STATUS status, String service_date, String creator, Tickler.PRIORITY priority, String task_assigned_to)
    {

        String date = service_date;
        if (date != null && !date.equals("now()"))
        {          //Just a hack for now.
            date = "'" + StringEscapeUtils.escapeSql(service_date) + "'";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Tickler t = new Tickler();
        t.setDemographicNo(Integer.parseInt(demographic_no));
        t.setMessage(message);
        t.setStatus(status);
        t.setUpdateDate(new Date());
        try
        {
            t.setServiceDate(formatter.parse(service_date));
        }
        catch (ParseException e)
        {
            MiscUtils.getLogger().error("Error", e);
            t.setServiceDate(new Date());
        }
        t.setCreator(creator);
        t.setPriority(priority);
        t.setTaskAssignedTo(task_assigned_to);

        ticklerDao.persist(t);
    }

    public boolean hasTickler(String demographic, String task_assigned_to, String message)
    {
        List<Tickler> ticklers = ticklerDao.findByDemographicIdTaskAssignedToAndMessage(Integer.parseInt(demographic), task_assigned_to, message);
        return !ticklers.isEmpty();
    }

    /*
    public void createTickler(String demoNo, String provNo, String message) {
           if (!ticklerExists(demoNo, message)) {
               Tickler t = new Tickler();
               t.setDemographicNo(Integer.valueOf(demoNo));
               t.setMessage(message);
               t.setStatus("A");
               t.setCreator(provNo);
               t.setPriority("4");
               t.setTaskAssignedTo(provNo);
               ticklerDao.persist(t);
           }
         }

       */
    public void createTickler(String demoNo, String provNo, String message, String assignedTo)
    {
        Tickler t = new Tickler();
        t.setDemographicNo(Integer.parseInt(demoNo));
        t.setMessage(message);
        t.setCreator(provNo);
        t.setTaskAssignedTo(assignedTo);
        ticklerDao.persist(t);
    }
    	  
    	 
    	  /*
    	  public boolean ticklerExists(String demoNo, String message) {
    		  CustomFilter filter=  new CustomFilter();
    		  filter.setDemographicNo(demoNo);
    		  filter.setMessage(message);
    		  filter.setStatus("A");
    		  List<Tickler> ticklers = ticklerDao.getTicklers(filter);
    		  return !ticklers.isEmpty();
    	  }
    	  */


    public void resolveTicklers(LoggedInInfo loggedInInfo, String providerNo, List<String> cdmPatientNos, String remString)
    {
        resolveTicklersBySubstring(loggedInInfo, providerNo, cdmPatientNos, remString);
    }

    public List<Tickler> listTicklers(LoggedInInfo loggedInInfo, Integer demographicNo, Date beginDate, Date endDate)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_READ);

        List<Tickler> result = ticklerDao.listTicklers(demographicNo, beginDate, endDate);

        for (Tickler tmp : result)
        {
            //--- log action ---
            LogAction.addLogSynchronous(loggedInInfo, "TicklerManager.listTicklers", "ticklerId=" + tmp.getId());
        }

        return result;
    }

    public List<Tickler> findActiveByDemographicNo(LoggedInInfo loggedInInfo, Integer demographicNo)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_READ);

        List<Tickler> result = ticklerDao.findActiveByDemographicNo(demographicNo);

        for (Tickler tmp : result)
        {
            //--- log action ---
            LogAction.addLogSynchronous(loggedInInfo, "TicklerManager.listTicklers", "ticklerId=" + tmp.getId());
        }

        return result;
    }

    public List<Tickler> search_tickler_bydemo(LoggedInInfo loggedInInfo, Integer demographicNo, String status, Date beginDate, Date endDate)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_READ);

        List<Tickler> result = ticklerDao.search_tickler_bydemo(demographicNo, status, beginDate, endDate);

        for (Tickler tmp : result)
        {
            //--- log action ---
            LogAction.addLogSynchronous(loggedInInfo, "TicklerManager.search_tickler_bydemo", "ticklerId=" + tmp.getId());
        }

        return result;
    }

    public List<Tickler> search_tickler(LoggedInInfo loggedInInfo, Integer demographicNo, Date endDate)
    {
        checkPrivilege(loggedInInfo, PRIVILEGE_READ);

        List<Tickler> result = ticklerDao.search_tickler(demographicNo, endDate);

        for (Tickler tmp : result)
        {
            //--- log action ---
            LogAction.addLogSynchronous(loggedInInfo, "TicklerManager.search_tickler", "ticklerId=" + tmp.getId());
        }

        return result;
    }

    public List<TicklerTextSuggest> getActiveTextSuggestions(LoggedInInfo loggedInInfo)
    {
        return this.ticklerTextSuggestDao.getActiveTicklerTextSuggests();
    }

    public List<TicklerTextSuggest> getAllTextSuggestions(LoggedInInfo loggedInInfo, int offset, int itemsToReturn)
    {
        return this.ticklerTextSuggestDao.findAll(offset, itemsToReturn);
    }

    public List<Tickler> sortTicklerList(Boolean isSortAscending, String sortColumn, List<Tickler> ticklers)
    {

        if (isSortAscending)
        {
            if (sortColumn.equals(DEMOGRAPHIC_NAME))
            {
                Collections.sort(ticklers, Tickler.DemographicNameAscComparator);
            }
            else if (sortColumn.equals(CREATOR))
            {
                Collections.sort(ticklers, Tickler.CreatorAscComparator);
            }
            else if (sortColumn.equals(SERVICE_DATE))
            {
                Collections.sort(ticklers, Tickler.ServiceDateAscComparator);
            }
            else if (sortColumn.equals(CREATION_DATE))
            {
                Collections.sort(ticklers, Tickler.CreationDateAscComparator);
            }
            else if (sortColumn.equals(PRIORITY))
            {
                Collections.sort(ticklers, Tickler.PriorityAscComparator);
            }
            else if (sortColumn.equals(TASK_ASSIGNED_TO))
            {
                Collections.sort(ticklers, Tickler.TaskAssignedToAscComparator);
            }
        }
        else
        {
            if (sortColumn.equals(DEMOGRAPHIC_NAME))
            {
                Collections.sort(ticklers, Tickler.DemographicNameDescComparator);
            }
            else if (sortColumn.equals(CREATOR))
            {
                Collections.sort(ticklers, Tickler.CreatorDescComparator);
            }
            else if (sortColumn.equals(SERVICE_DATE))
            {
                Collections.sort(ticklers, Tickler.ServiceDateDescComparator);
            }
            else if (sortColumn.equals(CREATION_DATE))
            {
                Collections.sort(ticklers, Tickler.CreationDateDescComparator);
            }
            else if (sortColumn.equals(PRIORITY))
            {
                Collections.sort(ticklers, Tickler.PriorityDescComparator);
            }
            else if (sortColumn.equals(TASK_ASSIGNED_TO))
            {
                Collections.sort(ticklers, Tickler.TaskAssignedToDescComparator);
            }
        }

        return ticklers;
    }


    private void checkPrivilege(LoggedInInfo loggedInInfo, String privilege)
    {
        if (!Provider.SYSTEM_PROVIDER_NO.equals(loggedInInfo.getLoggedInProviderNo()))
        {
            if (!securityInfoManager.hasPrivilege(loggedInInfo, "_tickler", privilege, null))
            {
                throw new RuntimeException("missing required security object (_tickler)");
            }
        }
    }
}
