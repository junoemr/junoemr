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

package oscar.oscarDemographic.pageUtil;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;
import oscar.OscarProperties;

public class JasperLabel
{
	private static final String JASPER_DEFAULT_FONT_SIZE_PROP = "net.sf.jasperreports.default.font.size";
	private static final String OSCAR_DEFAULT_LABEL_FONT_SIZE_PROP = "label.fontSize";

	/**
	 * getDefaultJasperLabelContext returns a default local jasper report context for PDF label creation.
	 */
	static public LocalJasperReportsContext getDefaultJasperLabelContext()
	{
		OscarProperties props = OscarProperties.getInstance();
		String fontSize = props.getProperty(OSCAR_DEFAULT_LABEL_FONT_SIZE_PROP);
		LocalJasperReportsContext rContext = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
		rContext.setProperty(JASPER_DEFAULT_FONT_SIZE_PROP, fontSize);
		return rContext;
	}
}
