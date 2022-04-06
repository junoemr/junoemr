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


package oscar.oscarReport.pageUtil;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import oscar.oscarReport.data.DemographicSetManager;

/**
 *
 * @author Jay Gallagher
 */
public class RptCreateDemographicSetAction extends Action
{

   public RptCreateDemographicSetAction()
   {
   }

   public ActionForward execute(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
   {
      String setName = request.getParameter("setName");
      int size = Integer.parseInt(request.getParameter("size"));

      if(StringUtils.isBlank(setName))
      {
         throw new IllegalArgumentException("setName cannot be blank");
      }

      List<String> demographicIds = new ArrayList<>(size);
      for(int i = 0; i < size; i++)
      {
         String demographicId = request.getParameter("demoNo" + i);
         if(StringUtils.isBlank(demographicId))
         {
            // may trigger when set sizes exceed parameter size restrictions
            throw new IllegalArgumentException("demoNo" + i + " is missing a value");
         }
         demographicIds.add(demographicId);
      }

      if(!demographicIds.isEmpty())
      {
         DemographicSetManager demoSet = new DemographicSetManager();
         demoSet.addDemographicSet(setName, demographicIds);
      }
      return (mapping.findForward("success"));
   }
}
