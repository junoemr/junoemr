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
import org.apache.log4j.Logger;
import org.oscarehr.common.model.Security;

import oscar.OscarProperties;

/**
 * Class that tracks and determines user login attempts.
 */
public final class LoginCheckLogin {

	private final static OscarProperties oscarProps = OscarProperties.getInstance();
	// the maximum number of failures before a lockout
	private final static Integer maxLoginFailures = Integer.parseInt(oscarProps.getProperty("login_max_failed_times"));
	// the time (in min) before the user can log in again after being locked out
	private static final Integer maxLoginDuration = Integer.parseInt(oscarProps.getProperty("login_max_duration"));
	// string representing the local IP address prefix (ip starts with this string, but this isn't the full IP)
	private static final String loginLocalIp = oscarProps.getProperty("login_local_ip");
	// if this is set, lockout is based on usernames, otherwise IP addresses
	private static final Boolean localLoginLock = oscarProps.isPropertyActive("login_lock");

	private static Logger logger = Logger.getLogger(LoginCheckLogin.class);

	private LoginCheckLoginBean loginCheckLoginBean = null;
	private LoginList loginList = null;

	public LoginCheckLogin() {
	}
	/**
	 * check lock status by IP / userName
	 * @param ip - ip address string
	 * @param userName - provider name
	 * @return true if the entry is blocked, false otherwise
	 */
	public boolean isBlocked(String ip, String userName) {

		if (loginList == null)
		{
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
		if (localLoginLock) {
			return isBlocked(userName);
		}
		else {
			// the local login lock is not set, so the local IP cannot be locked out
			boolean isLocal = (ip.startsWith(loginLocalIp));
			return (!isLocal && isBlocked(ip));
		}
	}

	/**
	 * @param key - key to check locks against (username or ip)
	 * @return true if the entry is blocked, false otherwise
	 */
	private boolean isBlocked(String key) {
		return (loginList.containsKey(key) && (loginList.get(key)).isLockedStatus());
	}

	// authenticate is used to check password
	public String[] auth(String user_name, String password, String pin, String ip) {
		loginCheckLoginBean = new LoginCheckLoginBean();
		loginCheckLoginBean.ini(user_name, password, pin, ip);
		return loginCheckLoginBean.authenticate();
	}

	/**
	 * only works after you call auth successfully
	 * @return the Security object
	 */
	public Security getSecurity() {
		return (loginCheckLoginBean.getSecurity());
	}

	/**
	 * update login list if login failed. This will cause the lockout counter to increase
	 * @param ip - login IP address
	 * @param userName - the login userName
	 */
	public synchronized void updateLoginList(String ip, String userName) {
		// local locking enabled (most users on same IP, lock based on username)
		if(localLoginLock) {
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
		LoginInfoBean loginInfoBean;
		GregorianCalendar now = new GregorianCalendar();
		if (loginList.containsKey(key)) {
			loginInfoBean = loginList.get(key);
		}
		else {
			loginInfoBean = new LoginInfoBean(now, maxLoginFailures, maxLoginDuration);
			loginList.put(key, loginInfoBean);
		}
		loginInfoBean.updateLoginInfoBean(now);
		logger.debug(key + "  status: " + (loginList.get(key)).getStatus() + " attempts: " + loginInfoBean.getAttempts());
	}

	/**
	 * remove the entry in the loginList with the given userName
	 * @param key - username or ip
	 * @return true if an entry was removed, false otherwise
	 */
	public boolean unlock(String key) {

		if (loginList == null)
		{
			loginList = LoginList.getLoginListInstance();
		}

		// unlock the entry in the login list
		boolean unlocked = loginList.containsKey(key);
		loginList.remove(key);
		return unlocked;
	}

	/**
	 * @return a list of all locked login instances
	 */
	public ArrayList<String> findLockList() {
		LoginInfoBean loginInfoBean;

		if (loginList == null)
		{
			loginList = LoginList.getLoginListInstance();
		}

		ArrayList<String> lockedList = new ArrayList<String>();

		//Look through the list of failed and locked users to just find the locked ones
		for(String key: loginList.keySet()) {
			loginInfoBean = loginList.get(key);

			if(loginInfoBean.isLockedStatus()) {
				lockedList.add(key);
			}
		}

		return lockedList;
	}
}
