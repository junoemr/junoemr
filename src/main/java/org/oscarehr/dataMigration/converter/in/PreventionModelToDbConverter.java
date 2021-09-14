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
package org.oscarehr.dataMigration.converter.in;

import org.apache.commons.lang.BooleanUtils;
import org.oscarehr.dataMigration.model.immunization.Immunization;
import org.oscarehr.prevention.model.Prevention;
import org.oscarehr.prevention.model.PreventionExt;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import static org.oscarehr.prevention.model.PreventionExt.KEY_COMMENT;
import static org.oscarehr.prevention.model.PreventionExt.KEY_DOSE;
import static org.oscarehr.prevention.model.PreventionExt.KEY_LOCATION;
import static org.oscarehr.prevention.model.PreventionExt.KEY_LOT;
import static org.oscarehr.prevention.model.PreventionExt.KEY_MANUFACTURE;
import static org.oscarehr.prevention.model.PreventionExt.KEY_NAME;
import static org.oscarehr.prevention.model.PreventionExt.KEY_REASON;
import static org.oscarehr.prevention.model.PreventionExt.KEY_RESULT;
import static org.oscarehr.prevention.model.PreventionExt.KEY_ROUTE;
import static org.oscarehr.prevention.model.PreventionExt.KEY_DIN;

@Component
public class PreventionModelToDbConverter extends BaseModelToDbConverter<Immunization, Prevention>
{
	@Override
	public Prevention convert(Immunization input)
	{
		Prevention prevention = new Prevention();
		prevention.setId(input.getId());
		prevention.setPreventionDate(ConversionUtils.toNullableLegacyDateTime(input.getAdministrationDate()));
		prevention.setNextDate(ConversionUtils.toNullableLegacyDate(input.getNextDate()));
		prevention.setPreventionType(input.getPreventionType());

		prevention.setProviderNo(findOrCreateProviderRecord(input.getProvider(), false).getId());
		prevention.setProviderName(findOrCreateProviderRecord(input.getProvider(), false).getDisplayName());
		prevention.setCreatorProviderNo(findOrCreateProviderRecord(input.getCreatedBy(), false).getId());

		prevention.setRefused(BooleanUtils.toBooleanDefaultIfNull(input.getRefused(), false));
		prevention.setNever(BooleanUtils.toBooleanDefaultIfNull(input.getNever(), false));

		if(input.getName() != null)
		{
			prevention.addExtension(getExt(prevention, KEY_NAME, input.getName()));
		}
		if(input.getDose() != null)
		{
			prevention.addExtension(getExt(prevention, KEY_DOSE, input.getDose()));
		}
		if(input.getManufacture() != null)
		{
			prevention.addExtension(getExt(prevention, KEY_MANUFACTURE, input.getManufacture()));
		}
		if(input.getRoute() != null)
		{
			prevention.addExtension(getExt(prevention, KEY_ROUTE, input.getRoute()));
		}
		if(input.getLot() != null)
		{
			prevention.addExtension(getExt(prevention, KEY_LOT, input.getLot()));
		}
		if(input.getLocation() != null)
		{
			prevention.addExtension(getExt(prevention, KEY_LOCATION, input.getLocation()));
		}
		if(input.getComments() != null)
		{
			prevention.addExtension(getExt(prevention, KEY_COMMENT, input.getComments()));
		}
		if(input.getReason() != null)
		{
			prevention.addExtension(getExt(prevention, KEY_REASON, input.getReason()));
		}
		if(input.getResult() != null)
		{
			prevention.addExtension(getExt(prevention, KEY_RESULT, input.getResult()));
		}
		if (input.getDrugIdentificationNumber() != null)
		{
			prevention.addExtension(getExt(prevention, KEY_DIN, input.getDrugIdentificationNumber()));
		}

		return prevention;
	}

	protected PreventionExt getExt(Prevention prevention, String key, String value)
	{
		PreventionExt ext = new PreventionExt();
		ext.setKeyval(key);
		ext.setVal(value);
		ext.setPrevention(prevention);
		return ext;
	}
}
