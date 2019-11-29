/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package org.oscarehr.integration.myhealthaccess.model;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserLoginTokenTo1;
import oscar.util.StringUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class MHAUserToken
{
	private static final int EXPIRE_RENEWAL_DAYS = 7;

	private String token;
	private Date expiryDate;
	private String clinicUserID;
	private String clinicID;
	private TYPE type;

	public enum TYPE {
		LOGIN,
		ACCESS
	}

	private MHAUserToken(ClinicUserLoginTokenTo1 transfer)
	{
		this.token = transfer.getToken();

		DecodedJWT decodedToken = JWT.decode(transfer.getToken());
		this.expiryDate = decodedToken.getExpiresAt();
		this.clinicUserID = decodedToken.getClaim("clinic_user_id").asString();
		this.clinicID = decodedToken.getClaim("clinic_id").asString();
		this.type = TYPE.valueOf(decodedToken.getClaim("type").asString());
	}

	public static MHAUserToken decodeToken(String token)
	{
		if (StringUtils.isNullOrEmpty(token))
		{
			return null;
		}

		ClinicUserLoginTokenTo1 accessTokenTo1 = new ClinicUserLoginTokenTo1(token);
		return new MHAUserToken(accessTokenTo1);
	}

	@Override
	public final String toString() {
		return this.token;
	}

	public String getToken()
	{
		return this.toString();
	}

	public String getClinicUserID()
	{
		return this.clinicUserID;
	}

	public String getClinicID()
	{
		return this.clinicID;
	}

	public TYPE getType()
	{
		return this.type;
	}

	public boolean isExpired() {
		return new Date().after(this.expiryDate);
	}

	public boolean expiresWithinDays(int days)
	{
		Duration duration = Duration.of(days, ChronoUnit.DAYS);
		Duration timeToExpire = Duration.between(new Date().toInstant(), expiryDate.toInstant());

		return duration.compareTo(timeToExpire) >= 0;
	}

	public boolean shouldRenew()
	{
		return this.expiresWithinDays(EXPIRE_RENEWAL_DAYS);
	}
}
