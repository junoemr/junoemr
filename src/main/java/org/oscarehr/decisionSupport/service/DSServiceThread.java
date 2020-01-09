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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.oscarehr.decisionSupport.service;

import org.apache.log4j.Logger;
import org.oscarehr.util.DbConnectionFilter;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;

/**
 * @author apavel
 * Don't use this, relies on periodically requesting guidelines from know2act.org
 */
@Deprecated
public class DSServiceThread extends Thread {
	private static final Logger logger = MiscUtils.getLogger();

	private DSService service;
	private LoggedInInfo loggedInInfo;

	public DSServiceThread(DSService service, LoggedInInfo loggedInInfo) {
		this.service = service;
		this.loggedInInfo = loggedInInfo;
	}

	@Override
	public void run() {
		try {
			service.fetchGuidelinesFromService(loggedInInfo);
		} catch (Exception e) {
			logger.error("Error", e);
		} finally {
			DbConnectionFilter.releaseAllThreadDbResources();
		}
	}
}
