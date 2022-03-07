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
package oscar.oscarFax.client.tld;

import org.oscarehr.fax.dao.FaxAccountDao;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.search.FaxAccountCriteriaSearch;
import org.oscarehr.fax.service.FaxUploadService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.fax.transfer.FaxOutboxTransferOutbound;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.List;

public class FailedOutboxFaxTag extends TagSupport
{
	private int numFailures;

	private final FaxAccountDao faxAccountDao;

	private final FaxUploadService faxUploadService;

	public FailedOutboxFaxTag()
	{
		numFailures = 0;
		faxAccountDao = SpringUtils.getBean(FaxAccountDao.class);
		faxUploadService = SpringUtils.getBean(FaxUploadService.class);
	}

	public int doStartTag() throws JspException
	{
		try
		{
			numFailures = 0;

			FaxAccountCriteriaSearch criteriaSearch = new FaxAccountCriteriaSearch();
			criteriaSearch.setLimit(10);

			List<FaxAccount> accounts = faxAccountDao.criteriaSearch(criteriaSearch);
			for (FaxAccount account : accounts)
			{
				numFailures += faxUploadService
					.getOutboxNotificationCount(account.getId(), null, null, FaxOutboxTransferOutbound.CombinedStatus.ERROR.toString(), null);
				numFailures += faxUploadService
					.getOutboxNotificationCount(account.getId(), null, null, FaxOutboxTransferOutbound.CombinedStatus.INTEGRATION_FAILED.toString(), null);
			}

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