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


package oscar.login;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.oscarehr.util.MiscUtils;

import com.quatro.model.security.Security;

import oscar.OscarProperties;

public final class LoginCheckLogin {
	
	private LoginCheckLoginBean lb = null;
	private LoginList loginList = null;

	/**
	 * Empty constructor
	 */
	public LoginCheckLogin() {
	}

	/**
	 * check lock status by IP / userName
	 * @param ip
	 * @param userName
	 * @return true if the entry is blocked, false otherwise
	 */
	public boolean isBlocked(String ip, String userName) {
		
		// judge the local network
		OscarProperties p = OscarProperties.getInstance();
		
		while (loginList == null) {
			loginList = LoginList.getLoginListInstance();
		}
		GregorianCalendar now = new GregorianCalendar();
		// delete the old entry in the login list if time out
		ArrayList<String> toDelete = new ArrayList<String>();
		for(String key: loginList.keySet()) {
			if (loginList.get(key).timeoutPeriodExceeded(now)) { 
				toDelete.add(key);
			}
		}
		// delete with second array to avoid concurrent modification of loginList
		for(String key : toDelete) {
			loginList.remove(key);
		}
		
		// check if it is blocked
		if (p.isPropertyActive("login_lock")) {
			return isBlocked(userName);
		}
		else {
			boolean isLocal = (ip.startsWith(p.getProperty("login_local_ip")));
			return (!isLocal && isBlocked(ip));
		}
	}
	
	/**
	 * @param ip
	 * @return true if the entry is blocked, false otherwise
	 */
	private boolean isBlocked(String key) {
		return (loginList.containsKey(key) && (loginList.get(key)).getStatus() == 0);
	}

	// authenticate is used to check password
	public String[] auth(String user_name, String password, String pin, String ip) {
		lb = new LoginCheckLoginBean();
		lb.ini(user_name, password, pin, ip);
		return lb.authenticate();
	}

	/**
	 * only works after you call auth successfully
	 * @return Security object
	 */
	public Security getSecurity() {
		return (lb.getSecurity());
	}

	/**
	 * update login list if login failed. This will cause the lockout counter to increase
	 * @param ip - login IP address
	 * @param userName - the login userName
	 */
	public synchronized void updateLoginList(String ip, String userName) {
		OscarProperties p = OscarProperties.getInstance();
		if(p.isPropertyActive("login_lock")) {
			updateLoginList(userName);
		}
		else {
			updateLoginList(ip);
		}
	}

	/**
	 * update login list if login failed
	 * @param key
	 */
	private synchronized void updateLoginList(String key) {
		OscarProperties p = OscarProperties.getInstance();
		LoginInfoBean linfo;
		GregorianCalendar now = new GregorianCalendar();
		if (loginList.containsKey(key)) {
			linfo = loginList.get(key);
			linfo.updateLoginInfoBean(now);
		}
		else {
			linfo = new LoginInfoBean(now, Integer.parseInt(p.getProperty("login_max_failed_times")), Integer.parseInt(p.getProperty("login_max_duration")));
			loginList.put(key, linfo);
		}
		MiscUtils.getLogger().debug(key + "  status: " + (loginList.get(key)).getStatus() + " times: " + linfo.getTimes() + " time: ");
	}

	/**
	 * remove the entry in the loginList with the given userName
	 * @param key - username or ip
	 * @return true if an entry was removed, false otherwise
	 */
	public boolean unlock(String key) {
	
		while (loginList == null) {
			loginList = LoginList.getLoginListInstance();
		}
		
		// unlock the entry in the login list
		boolean unlocked = loginList.containsKey(key);
		loginList.remove(key);
		return unlocked;
	}

	/**
	 * @return a list of all login instances
	 */
	public ArrayList<String> findLockList() {
	
		while (loginList == null) {
			loginList = LoginList.getLoginListInstance();
		}
		return new ArrayList<String>(loginList.keySet());
	}
}
