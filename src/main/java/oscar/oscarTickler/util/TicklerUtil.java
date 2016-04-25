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


package oscar.oscarRx.util;

public class TicklerUtil 
{

					String selected = "";
			        proFirst = rslocal.getString("first_name");
			        proLast = rslocal.getString("last_name");
			        proOHIP = rslocal.getString("provider_no");
			        String ip="";
			        String provider ="";

	Map<String, String> hash = props.getIPProviderMap();
	if(!hash.isEmpty())
	{

		for (Map.Entry<String, String> entry : hash.entrySet()) {
			ip = entry.getKey();
			provider = entry.getValue();
			if(provider.equals(proOHIP)&&ip.equals(request.getParameter("docIp")))
			{
				selected = "selected";

			}
		}
	}
	else if(defaultProvider.equals(proOHIP))
	{
		selected = "selected";
	}
	else if(user_no.equals(proOHIP) && defaultProvider == "")
	{
		selected = "selected";
	}

}
