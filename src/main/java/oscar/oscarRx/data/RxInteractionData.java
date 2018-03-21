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


package oscar.oscarRx.data;

import org.oscarehr.util.MiscUtils;

import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author Jay Gallagher
 */
public class RxInteractionData
{
	private static RxInteractionData rxInteractionData = new RxInteractionData();
	private static Hashtable<Integer, RxDrugData.Interaction[]> interactionHash = new Hashtable<>();

	public static RxInteractionData getInstance()
	{
		return rxInteractionData;
	}

	private RxInteractionData()
	{
	}

	public void addToHash(Vector atcCodes, RxDrugData.Interaction[] interact)
	{
		interactionHash.put(atcCodes.hashCode(), interact);
	}

	public RxDrugData.Interaction[] getInteractions(Vector atcCodes)
	{
		RxDrugData.Interaction[] interact = null;
		MiscUtils.getLogger().debug("hash table size " + interactionHash.size() + "RxInteractionData.getInteraction atc code val  " + atcCodes.hashCode());

		Integer atcHashCode = atcCodes.hashCode();
		if (interactionHash.containsKey(atcHashCode))
		{
			MiscUtils.getLogger().debug("Already been searched!");
			interact = interactionHash.get(atcHashCode);
		}
		else
		{
			MiscUtils.getLogger().debug("NEW ATC CODES");
			try
			{
				RxDrugData drugData = new RxDrugData();
				interact = drugData.getInteractions(atcCodes);
				if (interact != null)
				{
					addToHash(atcCodes, interact);
				}
			}
			catch (Exception e)
			{
				MiscUtils.getLogger().error("Error getting drug interactions. ", e);
			}
		}
		return interact;
	}
}
