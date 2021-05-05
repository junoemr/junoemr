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

package oscar.oscarMessenger.config.pageUtil;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.common.dao.GroupMembersDao;
import org.oscarehr.common.dao.GroupsDao;
import org.oscarehr.common.model.GroupMembers;
import org.oscarehr.common.model.Groups;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import oscar.oscarMessenger.data.MsgAddressBookMaker;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ResourceBundle;

public class MsgMessengerAdminAction extends Action {

	private GroupsDao groupsDao = SpringUtils.getBean(GroupsDao.class);
	private GroupMembersDao groupMembersDao = (GroupMembersDao) SpringUtils.getBean(GroupMembersDao.class);
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		String loggedInProviderNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(loggedInProviderNo, Permission.ADMIN_CREATE);

		String[] providers = ((MsgMessengerAdminForm) form).getProviders();
		String groupNoStr = ((MsgMessengerAdminForm) form).getGrpNo();
		Integer groupNo = Integer.parseInt(groupNoStr);
		String update = ((MsgMessengerAdminForm) form).getUpdate();
		String delete = ((MsgMessengerAdminForm) form).getDelete();

		ResourceBundle oscarR = ResourceBundle.getBundle("oscarResources", request.getLocale());

		if(update.equals(oscarR.getString("oscarMessenger.config.MessengerAdmin.btnUpdateGroupMembers")))
		{

			for(GroupMembers g : groupMembersDao.findByGroupId(groupNo))
			{
				groupMembersDao.remove(g.getId());
			}

			for(int i = 0; i < providers.length; i++)
			{
				GroupMembers gm = new GroupMembers();
				gm.setGroupId(groupNo);
				gm.setProviderNo(providers[i]);
				groupMembersDao.persist(gm);
			}

			MsgAddressBookMaker addMake = new MsgAddressBookMaker();
			addMake.updateAddressBook();
			request.setAttribute("groupNo", groupNoStr);
		}
		else if(delete.equals(oscarR.getString("oscarMessenger.config.MessengerAdmin.btnDeleteThisGroup")))
		{
			Groups group = groupsDao.find(groupNo);
			Integer parentId;

			if(group != null)
			{
				parentId = group.getParentId();
				Long childCount = groupsDao.countChildGroups(groupNo);

				// can't delete if this group has associated child groups
				if(childCount > 0)
				{
					request.setAttribute("groupNo", groupNoStr);
					request.setAttribute("fail", "This Group has Children, you must delete the children groups first");
					return (mapping.findForward("failure"));
				}

				for(GroupMembers member : groupMembersDao.findByGroupId(groupNo))
				{
					groupMembersDao.remove(member.getId());
				}
				groupsDao.remove(group.getId());
			}
			else
			{
				request.setAttribute("groupNo", groupNoStr);
				request.setAttribute("fail", "This Group does not exist");
				return (mapping.findForward("failure"));
			}

			MsgAddressBookMaker addMake = new MsgAddressBookMaker();
			addMake.updateAddressBook();
			request.setAttribute("groupNo", String.valueOf(parentId));
		}
		return (mapping.findForward("success"));
	}
}
