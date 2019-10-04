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
package oscar.oscarFax.client.tld;

import org.oscarehr.fax.service.FaxAccountService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.rest.transfer.fax.FaxAccountTransferOutbound;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.List;

public class FailedOutboxFaxTag extends TagSupport
{
	int numFailures;

	FaxAccountService faxService;

	public FailedOutboxFaxTag()
	{
		numFailures = 0;
		faxService = SpringUtils.getBean(FaxAccountService.class);
	}

	public int doStartTag() throws JspException
	{
		try
		{
			numFailures = 0;

			List<FaxAccountTransferOutbound> accounts;
			do
			{
				accounts = faxService.listAccounts(1, 10).getBody();
				for (FaxAccountTransferOutbound account : accounts)
				{
					long count;
					do
					{
						count = faxService.getOutboxNotificationCount(account.getId(), 1, 10, null, null, "ERROR", null).getBody();
						numFailures += count;
					}
					while(count >= 10);
					do{
						count =faxService.getOutboxNotificationCount(account.getId(), 1, 10, null, null, "INTEGRATION_FAILED", null).getBody();
						numFailures += count;
					}
					while(count >= 10);
				}
			}while(accounts.size() >= 10);

			JspWriter out = super.pageContext.getOut();
			if (numFailures > 0)
			{
				out.print("<span class='tabalert'>");
			}
			else
			{
				out.print("<span>");
			}
		}
		catch (Exception ex)
		{
			MiscUtils.getLogger().error("Error", ex);
		}

		if (numFailures > 0)
		{
			return EVAL_BODY_INCLUDE;
		}
		else
		{
			return SKIP_BODY;
		}
	}

	public int doEndTag() throws JspException
	{
		try
		{
			JspWriter out = super.pageContext.getOut();
			if (numFailures > 0)
			{
				out.print("<sup>" + numFailures + "</sup></span>  ");
			}
			else
			{
				out.print("</span>  ");
			}
		}
		catch (Exception p)
		{
			MiscUtils.getLogger().error("Error", p);
		}
		return EVAL_PAGE;
	}
}
