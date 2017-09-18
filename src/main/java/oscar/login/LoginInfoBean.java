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

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Class LoginInfoBean : set login status when bWAN = true 2003-01-29
 */
public final class LoginInfoBean {

	public enum LOCKOUT_STATUS {OK,LOCKED}

	private GregorianCalendar starttime = null;
	private int attempts = 0;
	private LOCKOUT_STATUS status = LOCKOUT_STATUS.OK;

	private int maxtimes = 3;
	private int maxduration = 10;

	public LoginInfoBean() {
		this(new GregorianCalendar(), 3, 10);
	}

	public LoginInfoBean(GregorianCalendar starttime1, int maxtimes1, int maxduration1) {
		maxtimes = maxtimes1;
		maxduration = maxduration1;
		resetLoginInfoBean(starttime1);
	}

	private void resetLoginInfoBean(GregorianCalendar starttime1) {
		starttime = starttime1;
		attempts = 0;
		status = LOCKOUT_STATUS.OK;
	}

	/**
	 * updates the internal login attempts counter and lockout status.
	 * If called after the maximum lockout period it will reset the status and counter
	 * @param now - the time to base timeout period on
	 */
	public void updateLoginInfoBean(GregorianCalendar now) {
		//if time out, initial bean again.
		if (timeoutPeriodExceeded(now)) {
			resetLoginInfoBean(now);
			return;
		}
		//else attempts++. if attempts out, status block
		attempts++;
		if (attempts >= maxtimes) {
			status = LOCKOUT_STATUS.LOCKED;
		}
	}

	/**
	 * @param now current time representation
	 * @return true if 'now' time parameter is beyond the max timeout period (timeout has expired)
	 */
	public boolean timeoutPeriodExceeded(GregorianCalendar now) {
		//if time out and status is 1, return true
		GregorianCalendar cal = (GregorianCalendar) starttime.clone();
		cal.add(Calendar.MINUTE, maxduration);

		return (cal.getTimeInMillis() < now.getTimeInMillis());
	}

	public void setStarttime(GregorianCalendar starttime1) {
		starttime = starttime1;
	}

	public void setAttempts(int times1) {
		attempts = times1;
	}

	public void setStatus(LOCKOUT_STATUS status1) {
		status = status1;
	}

	public GregorianCalendar getStarttime() {
		return (starttime);
	}

	public int getAttempts() {
		return (attempts);
	}

	public LOCKOUT_STATUS getStatus() {
		return (status);
	}
	public boolean isLockedStatus() {
		return (status == LOCKOUT_STATUS.LOCKED);
	}
}
