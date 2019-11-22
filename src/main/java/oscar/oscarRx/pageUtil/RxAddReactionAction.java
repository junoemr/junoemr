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

package oscar.oscarRx.pageUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.allergy.dao.AllergyDao;
import org.oscarehr.allergy.model.Allergy;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;


public final class RxAddReactionAction extends Action
{
	private static AllergyDao allergyDao = SpringUtils.getBean(AllergyDao.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
			// Setup variables

			String drugrefId = request.getParameter("ID");
			String name = request.getParameter("name");
			String type = request.getParameter("type");
			String allergyToArchive = request.getParameter("allergyToArchive");

			Allergy allergy = new Allergy();
			if (allergyToArchive != null && !allergyToArchive.isEmpty())
			{
				try
				{
					allergy = allergyDao.find(Integer.parseInt(allergyToArchive));
				}
				catch (NumberFormatException ex)
				{
					MiscUtils.getLogger().warn("Allergy id " + allergyToArchive + " has no database entry");
				}
			}

			request.setAttribute("allergyId", drugrefId);
			request.setAttribute("name", name);
			request.setAttribute("type", type);
			request.setAttribute("allergyToArchive", allergy);

			return (mapping.findForward("success"));
	}
}
