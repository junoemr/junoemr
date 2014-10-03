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

<html>
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title>PATIENT DETAIL INFO</title>
<link rel="stylesheet" href="../web.css" />
<script LANGUAGE="JavaScript">
<!--

function Attach(lname, fname, hin, yob,mob,dob, vercode, sex, effyear, effmonth, effdate, endyear, endmonth, enddate, address) {
        	 self.close();
        	 self.opener.document.adddemographic.last_name.value = lname;
        	 self.opener.document.adddemographic.first_name.value = fname;
        	 self.opener.document.adddemographic.hin.value = hin;
        	 self.opener.document.adddemographic.year_of_birth.value = yob;
        	 self.opener.document.adddemographic.month_of_birth.value = mob;
        	 self.opener.document.adddemographic.date_of_birth.value = dob;
           self.opener.document.adddemographic.ver.value = vercode;
        	 self.opener.document.adddemographic.sex.value = sex;
           self.opener.document.adddemographic.eff_date_year.value = effyear;
           self.opener.document.adddemographic.eff_date_month.value = effmonth;
           self.opener.document.adddemographic.eff_date_date.value = effdate;
           self.opener.document.adddemographic.hc_renew_date_year.value = endyear;
           self.opener.document.adddemographic.hc_renew_date_month.value = endmonth;
           self.opener.document.adddemographic.hc_renew_date_date.value = enddate;
           self.opener.document.adddemographic.address.value = address;
}
-->
</script>
</head>

<%@ page import="java.util.Arrays " %>

<body background="../images/gray_bg.jpg" bgproperties="fixed"
	topmargin="0" onLoad="setfocus()" leftmargin="0" rightmargin="0">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr bgcolor="#486ebd">
		<th align=CENTER NOWRAP><font face="Helvetica" color="#FFFFFF">PATIENT'S
		DETAIL RECORD</font></th>
	</tr>
</table>
<table BORDER="0" CELLPADDING="1" CELLSPACING="0" WIDTH="100%"
	BGCOLOR="#C4D9E7">

<% String card = request.getParameter("card_no");

   String hin = "";
   String lastname = "";
   String firstname = "";
   String dobyear = "";
   String dobmonth = "";
   String dobdate = "";
   String vercode = "";
   String endyear = "";
   String endmonth = "";
   String enddate = "";
   String effyear = "";
   String effmonth = "";
   String effdate = "";
   String sex = "";
   String address = "";

   if(card.startsWith("%B610043")) {
     // BC standalone photo card, non-photo card, or care card
     // specs defined in http://www.health.gov.bc.ca/msp/infoprac/teleplanspecs/ch4.pdf
     hin = card.substring(8, card.indexOf("0^"));
     lastname = card.substring(card.indexOf("^")+1, card.indexOf("/")).toUpperCase();

     String subcard = card.substring(card.indexOf("/")+1);
     firstname = subcard.substring(0,subcard.indexOf("^")).toUpperCase();

     subcard = subcard.substring(subcard.indexOf("^")+1);
     String endYYMM = subcard.substring(0, 4);
     if(!"0000".equals(endYYMM)) {
       endyear = "20" + endYYMM.substring(0, 2);
       endmonth = endYYMM.substring(2, 4);
       enddate = "01";
     }

     String issuerYYMM = subcard.substring(4, 8);
     if(!"0000".equals(issuerYYMM)) {
       effyear = "20" + issuerYYMM.substring(0, 2);
       effmonth = issuerYYMM.substring(2, 4);
       effdate = "01";
     }

     String dobCCYYMMDD = subcard.substring(8, 16);
     dobyear = dobCCYYMMDD.substring(0, 4);
     dobmonth = dobCCYYMMDD.substring(4, 6);
     dobdate = dobCCYYMMDD.substring(6, 8);

   } else if(card.startsWith("%BC") && card.substring(card.indexOf("?")).startsWith("?;636028")) {
     // BC Combined Card
     // specs defined in http://www.health.gov.bc.ca/msp/infoprac/teleplanspecs/ch4.pdf
     // note: only handles "track 1" and "track 2" of the magnetic stripe
     lastname = card.substring(card.indexOf("^")+1, card.indexOf("$")-1).toUpperCase();

     String subcard = card.substring(card.indexOf("^")+1);
     firstname = subcard.substring(subcard.indexOf("$")+1, subcard.indexOf("^")).toUpperCase();

     subcard = subcard.substring(subcard.indexOf("^")+1);
     int endAddressIndex = subcard.indexOf("$") >= 0 ? subcard.indexOf("$") : subcard.indexOf("^");
     address = subcard.substring(0, endAddressIndex).toUpperCase();

     subcard = subcard.substring(subcard.indexOf(";636028"));
     subcard = subcard.substring(subcard.indexOf("=")+1);
     String endYYMM = subcard.substring(0, 4);
     if(!"0000".equals(endYYMM)) {
       endyear = "20" + endYYMM.substring(0, 2);
       endmonth = endYYMM.substring(2, 4);
       enddate = "01";
     }

     String dobCCYYMMDD = subcard.substring(4, 12);
     dobyear = dobCCYYMMDD.substring(0, 4);
     dobmonth = dobCCYYMMDD.substring(4, 6);
     dobdate = dobCCYYMMDD.substring(6, 8);

   } else {
     // default parsing
     hin = card.substring(8,card.indexOf("^"));
     lastname = card.substring(card.indexOf("^")+1, card.indexOf("/")).toUpperCase();

     String subcard = card.substring(card.indexOf("/")+1);
     firstname = subcard.substring(0,subcard.indexOf("^")).toUpperCase();
     dobyear = subcard.substring(subcard.indexOf("^")+9,subcard.indexOf("^")+13);
     dobmonth = subcard.substring(subcard.indexOf("^")+13, subcard.indexOf("^")+15);
     dobdate = subcard.substring(subcard.indexOf("^")+15, subcard.indexOf("^")+17);
     vercode = subcard.substring(subcard.indexOf("^")+17, subcard.indexOf("^")+19);
     vercode = vercode.toUpperCase();

     int monthInt = Integer.parseInt(subcard.substring(subcard.indexOf("^")+1, subcard.indexOf("^")+3));
     endyear = (monthInt > 30 ? "19" : "20") + subcard.substring(subcard.indexOf("^")+1, subcard.indexOf("^")+3);
     endmonth = subcard.substring(subcard.indexOf("^")+3, subcard.indexOf("^")+5);
     enddate = dobdate;

     monthInt = Integer.parseInt(subcard.substring(subcard.indexOf("^")+24, subcard.indexOf("^")+26));
     effyear = (monthInt > 30 ? "19" : "20") + subcard.substring(subcard.indexOf("^")+24, subcard.indexOf("^")+26);
     effmonth = subcard.substring(subcard.indexOf("^")+26, subcard.indexOf("^")+28);
     effdate = subcard.substring(subcard.indexOf("^")+28, subcard.indexOf("^")+30);

     sex = subcard.substring(subcard.indexOf("^")+8,subcard.indexOf("^")+9);
       if (sex.compareTo("2") == 0) {
     sex="F";
     } else{
     sex="M";
     }
   }

   // sex is required on the demographics form and must be either "M" or "F" to be selected in the dropdown ("M" is the default)
   if(!Arrays.asList(new String[]{"M", "F"}).contains(sex)) {
     sex = "M";
   }

   %>

   <tr><td>card:</td><td><%=card%></td></tr>
   <tr><td>lastname:</td><td><%=lastname%></td></tr>
   <tr><td>firstname:</td><td><%=firstname%></td></tr>
   <tr><td>hin:</td><td><%=hin%></td></tr>
   <tr><td>dobyear:</td><td><%=dobyear%></td></tr>
   <tr><td>dobmonth:</td><td><%=dobmonth%></td></tr>
   <tr><td>dobdate:</td><td><%=dobdate%></td></tr>
   <tr><td>vercode:</td><td><%=vercode%></td></tr>
   <tr><td>sex:</td><td><%=sex%></td></tr>
   <tr><td>endyear:</td><td><%=endyear%></td></tr>
   <tr><td>endmonth:</td><td><%=endmonth%></td></tr>
   <tr><td>enddate:</td><td><%=enddate%></td></tr>
   <tr><td>effyear:</td><td><%=effyear%></td></tr>
   <tr><td>effmonth:</td><td><%=effmonth%></td></tr>
   <tr><td>effdate:</td><td><%=effdate%></td></tr>
   <tr><td>address:</td><td><%=address%></td></tr>

	<script LANGUAGE="JavaScript">
<!--
 	Attach('<%=lastname%>','<%=firstname%>','<%=hin%>','<%=dobyear%>','<%=dobmonth%>','<%=dobdate%>', '<%=vercode%>','<%=sex%>', '<%=effyear%>', '<%=effmonth%>', '<%=effdate%>', '<%=endyear%>', '<%=endmonth%>', '<%=enddate%>', '<%=address%>');

-->

</script>
</table>

<br>
<br>
<form><input type="button" name="Button" value="Cancel"
	onclick=self.close();></form>
</body>
</html>
