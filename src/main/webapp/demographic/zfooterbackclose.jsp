<%--

    Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for the
    Department of Family Medicine
    McMaster University
    Hamilton
    Ontario, Canada

--%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>


<script>
    /**
	 * If the back button doesn't bring you to a new window (you are are at the first point in your history),
	 * then it will close the window instead.  This is an alternative to determining the length of the window.history object
	 * because different browsers start the count at 0 or 1 (in both cases the current page will add +1 to the length).
     */
	function goBackOrClose() {
		var close = true;

		window.addEventListener("beforeunload", function() {
			close = false;
		});

		history.go(-1);

		setTimeout(function() {
			if (close) {
				self.close()
			}
		}, 100);
	}
</script>

<hr width="100%">
<table border="0" cellspacing="0" cellpadding="0" width="100%" class="noprint">
	<tr>
		<td><a href=# onClick="javascript:goBackOrClose(); return false;">
		<img src="../images/leftarrow.gif" border="0" width="25" height="20"
			align="absmiddle"> <bean:message
			key="demographic.zfooterbackclose.btnBack" /> </a></td>
		<td align="right"><a href="" onClick="self.close();"><bean:message
			key="demographic.zfooterbackclose.btnClose" /><img
			src="../images/rightarrow.gif" border="0" width="25" height="20"
			align="absmiddle"></a></td>
	</tr>
</table>
