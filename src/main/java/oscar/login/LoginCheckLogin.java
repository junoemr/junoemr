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
	
	boolean bWAN = true;
	private LoginCheckLoginBean lb = null;
	private LoginList loginList = null;

	/**
	 * Empty constructor
	 */
	public LoginCheckLogin() {
	}

	/**
	 * lock username and ip
	 * @param ip
	 * @param userName
	 * @return true if the entry is blocked, false otherwise
	 */
	public boolean isBlock(String ip, String userName) {
		OscarProperties p = OscarProperties.getInstance();
		if (!p.isPropertyActive("login_lock")) {
			return isBlock(ip);
		}

		// the following meets the requirment of epp
		boolean bBlock = false;
		// judge the local network
		if (ip.startsWith(p.getProperty("login_local_ip"))) bWAN = false;

		while (loginList == null) {
			loginList = LoginList.getLoginListInstance();
		}

		// check if it is blocked
		if (loginList.containsKey(userName) && (loginList.get(userName)).getStatus() == 0) bBlock = true;

		return bBlock;
	}
	
	/**
	 * lock ip
	 * @param ip
	 * @return true if the entry is blocked, false otherwise
	 */
	private boolean isBlock(String ip) {
		boolean bBlock = false;

		// judge the local network
		OscarProperties p = OscarProperties.getInstance();
		if (ip.startsWith(p.getProperty("login_local_ip"))) bWAN = false;

		GregorianCalendar now = new GregorianCalendar();
		while (loginList == null) {
			loginList = LoginList.getLoginListInstance(); // LoginInfoBean info =
		}
		// delete the old entry in the loginlist if time out
		if (bWAN && !loginList.isEmpty()) {
			for(String key: loginList.keySet()) {
				if (loginList.get(key).timeoutPeriodExceeded(now)) { 
					loginList.remove(key);
				}
			}
			// check if it is blocked
			if (loginList.containsKey(ip) && (loginList.get(ip)).getStatus() == 0) bBlock = true;
		}
		return bBlock;
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

	public synchronized void updateLoginList(String ip, String userName) {
		OscarProperties p = OscarProperties.getInstance();
		if(!p.isPropertyActive("login_lock")) {
			updateLoginList(ip);
		}
		else {
			updateLoginList(userName);
		}
	}

	/**
	 * update login list if login failed
	 * @param key
	 */
	private synchronized void updateLoginList(String key) {
		OscarProperties p = OscarProperties.getInstance();
		if (bWAN) {
			LoginInfoBean linfo;
			GregorianCalendar now = new GregorianCalendar();
			if (loginList.containsKey(key)) {
				linfo = loginList.get(key);
				linfo.updateLoginInfoBean(now);
			}
			else {
				linfo = new LoginInfoBean(now, Integer.parseInt(p.getProperty("login_max_failed_times")), Integer.parseInt(p.getProperty("login_max_duration")));
			}
			loginList.put(key, linfo);
			MiscUtils.getLogger().debug(key + "  status: " + (loginList.get(key)).getStatus() + " times: " + linfo.getTimes() + " time: ");
		}
	}

	/**
	 * remove the entry in the loginList with the given userName
	 * @param userName
	 * @return true if an entry was removed, false otherwise
	 */
	public boolean unlock(String userName) {
	
		while (loginList == null) {
			loginList = LoginList.getLoginListInstance();
		}
		
		// unlock the entry in the login list
		boolean unlocked = loginList.containsKey(userName);
		loginList.remove(userName);
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
