<%--

    Copyright (c) 2006-. OSCARservice, OpenSoft System. All Rights Reserved.
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

--%>
<%@ page isErrorPage="true"%><!-- only true can access exception object -->

<%@page import="org.oscarehr.util.MiscUtils"%><html>
<body>
<h1>Error Page</h1>
<hr>
<p>
<center>OSCAR has encountered a fatal error and is unable to
continue. <br>
<a href="index.html">Back to Home</a></center>
<p>
<hr>
Received the exception:
<br>
<font color=red> <%= exception.toString() %><br>
<%= exception.getMessage() %> <% MiscUtils.getLogger().error("Error", exception); %>
</font>
</body>
</html>
