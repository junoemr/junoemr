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

package oscar.util;

import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.SecObjectName;
import org.oscarehr.util.SpringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class BackupDownload extends GenericDownload {

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		HttpSession session = req.getSession(true);
		SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

		// check the rights
		String filename = req.getParameter("filename") == null ? "null" : req.getParameter("filename");
		String dir = (String) session.getAttribute("backupfilepath") == null ? "/home/mysql/" : (String) session.getAttribute("backupfilepath");

		boolean adminPrivs = securityInfoManager.hasPrivilege((String) req.getSession().getAttribute("user"),
				SecurityInfoManager.PRIVILEGE_LEVEL.READ, SecObjectName.OBJECT_NAME.ADMIN_BACKUP);

		boolean bDownload = false;
		if (filename != null && adminPrivs) {
			bDownload = true;
		}
		download(bDownload, res, dir, filename, null);
	}
}
