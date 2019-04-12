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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%-- This JSP is the first page you see when you enter 'report by template' --%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>
<%
    String roleName$ = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed = true;
%>
<security:oscarSec roleName="<%=roleName$%>"
                   objectName="_admin,_admin.misc" rights="r" reverse="<%=true%>">
    <%authed = false; %>
    <%response.sendRedirect("../securityError.jsp?type=_admin&type=_admin.misc");%>
</security:oscarSec>
<%
    if (!authed)
    {
        return;
    }

    Boolean hasCustomBillingAddress = (Boolean) request.getAttribute("hasCustomBillingAddress");
%>

<%@ page import="java.util.*,oscar.oscarReport.reportByTemplate.*" %>
<%@ page import="org.oscarehr.rx.service.RxWatermarkService" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<html:html locale="true">
    <head>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
    <title>Clinic</title>
    <link rel="stylesheet" type="text/css"
          href="../css/bootstrap.css">
    <link rel="stylesheet" type="text/css"
          href="../css/bootstrap-toggle.min.css">
    <link rel="stylesheet" type="text/css"
          href="../share/css/OscarStandardLayout.css">
    <link rel="stylesheet" type="text/css" href="../share/css/OscarStandardLayout.css"/>
    <link rel="stylesheet" href="../css/clinicAdmin.css">

    <script type="text/javascript" language="JavaScript"
            src="../share/javascript/prototype.js"></script>
    <script type="text/javascript" language="JavaScript"
            src="../share/javascript/Oscar.js"></script>
    <script type="text/javascript" language="JavaScript"
            src="../js/jquery-1.9.1.js"></script>
    <script type="text/javascript" language="JavaScript"
            src="../js/bootstrap.js"></script>

    <script type="text/javascript" language="JavaScript"
            src="../js/bootstrap-toggle.min.js"></script>


    <div class="header">
        <h2>Manage Clinic Details</h2>
    </div>
    <div id="clinic">
        <div class="clinic-details">
            <div class="clinic-info">
                <fieldset>
                    <legend>Clinic Address</legend>
                    <html:form action="/admin/ManageClinic">
                        <html:hidden property="clinic.id"/>
                            <% if (hasCustomBillingAddress != null && hasCustomBillingAddress) { %>
                        <html:hidden property="clinicBillingAddress.id"/>
                            <% } %>
                        <html:hidden property="clinic.status" value="A"/>
                    <input type="hidden" name="method" value="update"/>
                    <div class="billing-check">
                        <input id="billingCheckbox" type="checkbox" name="billingCheck">
                        <span style="font-size: 0.8em;">Custom billing address</span>
                    </div>
                    <div class="address-fields hide-billing" id="address-fields">
                        <div class="address">
                            <div class="input-field">
                                <label for="clinic.clinicName">Clinic Name</label>
                                <html:text property="clinic.clinicName"/>
                            </div>
                            <div class="input-field">
                                <label for="clinic.clinicAddress">Clinic Address</label>
                                <html:text property="clinic.clinicAddress"/>
                            </div>
                            <div class="input-field">
                                <label for="clinic.clinicCity">Clinic City</label>
                                <html:text property="clinic.clinicCity"/>
                            </div>
                            <div class="input-field">
                                <label for="clinic.clinicProvince">Clinic Province</label>
                                <html:text property="clinic.clinicProvince"/>
                            </div>
                            <div class="input-field">
                                <label for="clinic.clinicPostal">Clinic Postal</label>
                                <html:text property="clinic.clinicPostal"/>
                            </div>
                            <div class="input-field">
                                <label for="clinic.clinicPhone">Clinic Phone</label>
                                <html:text property="clinic.clinicPhone"/>
                            </div>
                            <div class="input-field">
                                <label for="clinic.clinicFax">Clinic Fax</label>
                                <html:text property="clinic.clinicFax"/>
                            </div>
                            <div class="input-field">
                                <label for="clinic.clinicLocationCode">Clinic Location Code</label>
                                <html:text property="clinic.clinicLocationCode"/>
                            </div>
                            <div class="input-field">
                                <label for="clinic.clinicDelimPhone">
                                    Multi Phone -
                                    <span style="color: grey;">Delimited by |</span>
                                </label>
                                <html:text property="clinic.clinicDelimPhone"/>
                            </div>
                            <div class="input-field">
                                <label for="clinic.clinicDelimFax">
                                    Multi Fax -
                                    <span style="color: grey;">Delimited by |</span>
                                </label>
                                <html:text property="clinic.clinicDelimFax"/>
                            </div>
                        </div>
                        <div class="address" id="billing-fields">
                            <div class="title">
                                <h3>Billing Address</h3>
                            </div>
                            <div class="input-field">
                                <label for="clinicBillingAddress.billingName">Billing Name</label>
                                <html:text property="clinicBillingAddress.billingName"/>
                            </div>
                            <div class="input-field">
                                <label for="clinicBillingAddress.billingAddress">Billing Address</label>
                                <html:text property="clinicBillingAddress.billingAddress"/>
                            </div>
                            <div class="input-field">
                                <label for="clinicBillingAddress.billingCity">Billing City</label>
                                <html:text property="clinicBillingAddress.billingCity"/>
                            </div>
                            <div class="input-field">
                                <label for="clinicBillingAddress.billingProvince">Billing Province</label>
                                <html:text property="clinicBillingAddress.billingProvince"/>
                            </div>
                            <div class="input-field">
                                <label for="clinicBillingAddress.billingPostal">Billing Postal</label>
                                <html:text property="clinicBillingAddress.billingPostal"/>
                            </div>
                            <div class="input-field">
                                <label for="clinicBillingAddress.billingPhone">Billing Phone</label>
                                <html:text property="clinicBillingAddress.billingPhone"/>
                            </div>
                            <div class="input-field">
                                <label for="clinicBillingAddress.billingFax">Billing Fax</label>
                                <html:text property="clinicBillingAddress.billingFax"/>
                            </div>
                        </div>
                    </div>
                    <div class="submit">
                        <input class="submit-button" type="submit" value="Update">
                    </div>
                    <div class="update-success">
                        <p><%=(String) request.getAttribute("updateStatus")%>
                        </p>
                    </div>
            </div>
            </html:form>
            </fieldset>
        </div>
    </div>
    <div id="clinic">
        <div class="clinic-details">
            <div class="clinic-info">
                <fieldset>
                    <legend>Prescription Watermark</legend>
                    <form action="../RxWatermark.do" method="POST" enctype="multipart/form-data" id="watermark-form">
                        <input type="hidden" name="method" value="setWatermark">

                        <div class="input-field flex-fill-row">
                            <input id="watermark-toggle" <% if (RxWatermarkService.isWatermarkEnabled()) {%>checked<%}%> type="checkbox">
                            <script>
                                let toggle = $('#watermark-toggle');
                                toggle.bootstrapToggle({
                                    width: 50,
                                    height: 20,
                                    size: 'small'
                                });
                                toggle.change(function() {
                                    let enabled = jQuery(this).prop('checked');
                                    toggleWatermarkFields(enabled);
                                    enableWatermark(enabled);
                                })
                            </script>
                        </div>
                        <div class="watermark-fields" id="watermark-input-form">
                            <div class="watermark-input-field flex-fill-row">
                                <div style="display:flex; flex-direction:row;">
                                    <div style="margin-right: 5px;">
                                        <img id="current-watermark-preview" src="../RxWatermark.do?method=getWatermark" width="100" height="100" style="background-color: #fefefe;"/>
                                    </div>
                                    <div>
                                        <label><b>Rx Prescription watermark</b></label>
                                        <input id="watermark-file" type="file" name="watermarkFile" accept="image/png"/>
                                    </div>
                                </div>
                            </div>
                            <div class="watermark-input-field flex-fill-row">
                                <input class="submit-button" type="submit" value="Upload">
                            </div>
                        </div>
                    </form>
                </fieldset>
            </div>
        </div>
    </div>

    <script>
        const billingCheckbox = document.querySelector('#billingCheckbox');
        const billingFields = document.querySelector('#billing-fields');
        const addressFields = document.querySelector('#address-fields');

        function showBilling(showBilling) {
            if (showBilling) {
                billingFields.style.display = 'block';
                addressFields.classList.remove('hide-billing');
                addressFields.classList.add('show-billing');
            } else {
                billingFields.style.display = 'none';
                addressFields.classList.remove('show-billing');
                addressFields.classList.add('hide-billing');
            }
        }

        if (<%=hasCustomBillingAddress%>) {
            billingCheckbox.checked = "checked";
            showBilling(true);
        } else {
            billingCheckbox.checked = "";
            showBilling(false);
        }

        billingCheckbox.addEventListener('change', function (event) {
            if (event.target.checked) {
                showBilling(true);
            } else {
                showBilling(false);
            }
        });

        function enableWatermark(enable)
        {
            jQuery.ajax({
                url: "../RxWatermark.do",
                type: "post",
                data: {
                    method: "enableWatermark",
                    enable: enable
                },
                success: function() {
                    console.log("JOB DONE");
                }
            });
        }

        function toggleWatermarkFields(enable)
        {
            let watermarkForm = jQuery("#watermark-input-form");
            if (watermarkForm.length > 0)
            {
                if (enable)
                {
                    watermarkForm.css("display", "flex");
                }
                else
                {
                    watermarkForm.css("display", "none");
                }
            }
        }

        function submitWatermarkForm(event)
        {
            let formData = new FormData(this);
            event.preventDefault();
            jQuery.ajax({
                url: "../RxWatermark.do",
                type: "post",
                data: formData,
                contentType: false,
                processData: false,
                success: function() {
                    jQuery("#watermark-file").val("");
                    jQuery("#current-watermark-preview").attr("src", "../RxWatermark.do?method=getWatermark&rand=" + Math.random())
                }
            })
        }

        jQuery(document).ready(function ()
        {
            jQuery("#watermark-form").submit(submitWatermarkForm);
            <% if (!RxWatermarkService.isWatermarkEnabled()) { %>
                toggleWatermarkFields(false);
            <%}%>
        });

    </script>
    </body>
</html:html>
