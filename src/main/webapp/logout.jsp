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
<%@ page import="oscar.OscarProperties" %>
<%@ page import="oscar.log.LogAction" %>
<%@ page import="oscar.log.LogConst" %>
<%@ page import="java.net.HttpURLConnection" %>
<%@ page import="java.net.URL" %>
<%@ page import="org.oscarehr.util.MiscUtils" %>
<%@ page errorPage="errorpage.jsp" %>
<%
  OscarProperties props = OscarProperties.getInstance();

  if(oscar.oscarSecurity.CRHelper.isCRFrameworkEnabled()) net.sf.cookierevolver.CRFactory.getManager().recordLogout(request);
  if(session != null) {
    Object user = session.getAttribute("user");
    if (user != null) {
      //HashMap hash=(HashMap)application.getAttribute("monitor");
      session.invalidate();
      request.getSession();
      String ip = request.getRemoteAddr();
	  LogAction.addLogEntry((String)user, LogConst.ACTION_LOGOUT, LogConst.CON_LOGOUT, LogConst.STATUS_SUCCESS, null, ip);
    }
  }
  String param = "";
  if(request.getParameter("login")!=null ) {
	  param = "?login="+request.getParameter("login") ;
  }
  //response.sendRedirect("index.jsp"+param);
  String oscarhost_login = props.getProperty("oscarhost_login");
  String instance_id = "";
  Boolean remote_oscar_login = false;
  if(oscarhost_login != null){
    String non_secure_oscarhost_login = oscarhost_login.replace("https", "http");
    String splitted_string[] = oscarhost_login.split("/");
    instance_id = splitted_string[splitted_string.length-1];
        
    try{
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection con = (HttpURLConnection) new URL(non_secure_oscarhost_login).openConnection();
        con.setRequestMethod("HEAD");

        if(con.getResponseCode() != HttpURLConnection.HTTP_UNAVAILABLE &&
            con.getResponseCode() != HttpURLConnection.HTTP_UNAUTHORIZED &&
            con.getResponseCode() != HttpURLConnection.HTTP_PROXY_AUTH &&
            con.getResponseCode() != HttpURLConnection.HTTP_NOT_FOUND &&
            con.getResponseCode() != HttpURLConnection.HTTP_INTERNAL_ERROR &&
            con.getResponseCode() != HttpURLConnection.HTTP_GONE &&
            con.getResponseCode() != HttpURLConnection.HTTP_GATEWAY_TIMEOUT &&
            con.getResponseCode() != HttpURLConnection.HTTP_FORBIDDEN &&
            con.getResponseCode() != HttpURLConnection.HTTP_BAD_REQUEST &&
            con.getResponseCode() != HttpURLConnection.HTTP_BAD_GATEWAY ){

            remote_oscar_login = true;

        }
    }catch(Exception e){
        MiscUtils.getLogger().info("Exception "+e);
    }
  }
  if(remote_oscar_login){
    response.sendRedirect(oscarhost_login+param);
  }else if(oscarhost_login != null){
    MiscUtils.getLogger().info("can't load page");
    param += (param.length() == 0)? "?instance_id="+instance_id : "&instance_id="+instance_id;
    response.sendRedirect("oscarhost_login.jsp"+param);
  }else{
    response.sendRedirect("index.jsp"+param);
  }
%>
