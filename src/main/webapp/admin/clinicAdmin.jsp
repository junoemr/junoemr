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
    UserPropertyDAO userPropertyDAO = SpringUtils.getBean(UserPropertyDAO.class);
%>

<%@ page import="org.oscarehr.rx.service.RxWatermarkService" %>
<%@ page import="org.oscarehr.common.dao.UserPropertyDAO" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.common.model.UserProperty" %>
<%@ page import="oscar.OscarProperties" %>
<%@ taglib uri="/oscarPropertiestag" prefix="oscarprop" %>
<%@ include file="/taglibs.jsp" %>
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
    </head>

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
                            <oscarprop:oscarPropertiesCheck property="instance_type" value="BC">
                                <div class="input-field">
                                    <label for="clinic.bcFacilityNumber" maxlength="5">Clinic Facility Number</label>
                                    <html:text property="clinic.bcFacilityNumber"/>
                                </div>
                                <div class="input-field">
                                    <label for="clinicServiceLocationCode">Service Location Code</label>
                                    <html:select property="clinicServiceLocationCode">
                                        <html:option value="">None</html:option>
                                        <c:forEach items="${serviceLocationCodes}" var="code">
                                            <html:option value="${code.visitType}|${code.visitDescription}">(${code.visitType}) ${code.visitDescription}</html:option>
                                        </c:forEach>
                                    </html:select>
                                </div>
                            </oscarprop:oscarPropertiesCheck>
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
                            <div class="input-field">
                                <label for="clinic.clinicEmail">
                                    Email
                                </label>
                                <html:text property="clinic.clinicEmail"/>
                            </div>
                            <oscarprop:oscarPropertiesCheck property="instance_type" value="AB">
                                <div class="input-field">
                                    <label for="clinic.albertaConnectCareLabId">
                                        Connect Care Lab Id
                                    </label>
                                    <html:text property="clinic.albertaConnectCareLabId"/>
                                </div>
                                <div class="input-field">
                                    <label for="clinic.albertaConnectCareDepartmentId">
                                        Connect Care Department Id
                                    </label>
                                    <html:text property="clinic.albertaConnectCareDepartmentId"/>
                                </div>
                            </oscarprop:oscarPropertiesCheck>
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
                <fieldset class="rx-settings-panel">
                    <legend>Clinic Logos</legend>
                    <form  id="card-logo-form" action="../RxSettings.do" method="POST">
                        <input id="card-logo-upload-action" type="hidden" name="method" value="uploadImage">
                        <input type="hidden" name="image_type" value="CARD_LOGO">
                        <div class="flex-fill-row">
                            <h3>Appointment Card Logo</h3>
                            <hr>
                        </div>
                        <div>
                            <div style="display:flex; flex-direction:row; align-items:end;">
                                <div style="margin-right: 5px;">
                                    <img id="card-logo-preview" src="../ClinicImage.do?method=getImage&image_type=CARD_LOGO" width="100" height="100" style="background-color: #fefefe;" onerror="this.style.display='none';"/>
                                </div>
                                <div>
                                    <label><b>Appointment Card Logo Image</b></label>
                                    <input id="image_file" type="file" name="image_file" accept="image/png"/>
                                </div>
                            </div>
                            <div class="clinic-logo-input-field flex-fill-row" style="flex-direction: row; justify-content: center;">
                                <input id="card-logo-submit-upload" class="submit-button" style="display:flex; flex: 0 1 auto; margin-right: 10px" type="submit" value="Upload">
                                <input id="card-logo-submit-delete" class="submit-button" style="display:flex; flex: 0 1 auto;" type="submit" value="Delete">
                            </div>
                        </div>
                    </form>
                </fieldset>
            </div>
        </div>
    </div>

    <div id="clinic">
        <div class="clinic-details">
            <div class="clinic-info">
                <fieldset class="rx-settings-panel">
                    <legend>Rx Settings</legend>
                    <form  id="rx-form" action="../RxSettings.do" method="POST">
                        <h3>General Settings</h3>
                        <hr>
                        <input id="rx-settings-action" name="method" type="hidden" value="setSettings">
                        <div class="rx-fields">
                            <div class="input-field">
                                <label>Rx Footer - <span style="color: grey;">95 character max</span></label>
                                <%
                                    UserProperty promoProp = userPropertyDAO.getProp(UserProperty.RX_PROMO_TEXT);
                                    String promoText = "";
                                    if (promoProp != null)
                                    {
                                        promoText = promoProp.getValue();
                                    }
                                    else
                                    {
                                        promoText = OscarProperties.getInstance().getProperty("FORMS_PROMOTEXT");
                                    }
                                %>
                                <input name="rx_promo_text" type="text" maxlength="95" value="<%=promoText%>">
                            </div>

                            <div class="submit flex-fill-row">
                                <input class="submit-button" type="submit" value="Update">
                            </div>
                            <div class="flex-fill-row">
                                <span id="rx-general-success-msg">Settings updated</span>
                                <span id="rx-general-error-msg">Error updating settings</span>
                            </div>
                        </div>
                    </form>
                    <form action="../ClinicImage.do" method="POST" enctype="multipart/form-data" id="watermark-form">
                        <h3>Rx Watermark</h3>
                        <hr>
                        <input id="watermark-upload-action" type="hidden" name="method" value="uploadImage">
                        <input type="hidden" name="image_type" value="WATERMARK">

                        <div class="input-field flex-fill-row">
                            <input id="watermark-toggle" <% if (RxWatermarkService.isWatermarkEnabled()) {%>checked<%}%> type="checkbox">
                        </div>
                        <div class="watermark-fields" id="watermark-input-form">
                            <div class="watermark-input-field flex-fill-row">
                                <div style="display:flex; flex-direction:row; align-items:end;">
                                    <div style="margin-right: 5px;">
                                        <img id="current-watermark-preview" src="../ClinicImage.do?method=getImage&image_type=WATERMARK" width="100" height="100" style="background-color: #fefefe;" onerror="this.style.display='none';"/>
                                    </div>
                                    <div>
                                        <label><b>Rx Prescription watermark</b></label>
                                        <input id="watermark-file" type="file" name="image_file" accept="image/png"/>
                                    </div>
                                </div>
                            </div>
                            <div class="watermark-input-field flex-fill-row" style="flex-direction: row; justify-content: center;">
                                <input id="watermark-submit-upload" class="submit-button" style="display:flex; flex: 0 1 auto; margin-right: 10px" type="submit" value="Upload">
                                <input id="watermark-submit-delete" class="submit-button" style="display:flex; flex: 0 1 auto;" type="submit" value="Delete">
                            </div>
                            <div class="watermark-input-field flex-fill-row" style="margin-top: 20px;">
                                <label><b>Watermark Position</b></label>
                                <div class="watermark-background-selector">
                                <input id="watermark-background-toggle" <% if (!RxWatermarkService.isWatermarkBackground()) {%>checked<%}%> type="checkbox" data-toggle="toggle" data-on="Foreground" data-off="Background">
                                </div>
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

        // setup toggle switches

        // watermark on / off toggle
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

        // watermark position toggle
        let backgroundToggle = $('#watermark-background-toggle');
        backgroundToggle.bootstrapToggle({
            width: 90,
            height: 20,
            size: 'small'
        });
        backgroundToggle.change(function() {
            let state = !jQuery(this).prop('checked');
            setWatermarkBackground(state);
        })


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
                    console.log("watermark on");
                }
            });
        }

        function setWatermarkBackground(isBackground)
        {
            jQuery.ajax({
                url: "../RxWatermark.do",
                type: "post",
                data: {
                    method: "setWatermarkBackground",
                    isBackground: isBackground
                },
                success: function() {
                    if (isBackground)
                    {
                        console.log("watermark background");
                    }
                    else
                    {
                        console.log("watermark foreground");
                    }
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

        function submitWatermarkForm(event, action)
        {
            event.preventDefault();
            if (action === "deleteImage")
            {
                if(!confirm("are you sure you want to delete your Rx watermark image"))
                {
                    return;
                }
            }

            jQuery("#watermark-upload-action").val(action);
            let formData = new FormData(event.target);

            jQuery.ajax({
                url: "../ClinicImage.do",
                type: "post",
                data: formData,
                contentType: false,
                processData: false,
                success: function() {
                    jQuery("#watermark-file").val("");
                    let watermarkPreview = jQuery("#current-watermark-preview");
                    watermarkPreview.css("display", "flex");
                    watermarkPreview.attr("src", "../ClinicImage.do?method=getImage&image_type=WATERMARK&rand=" + Math.random())
                }
            })
        }

        function submitRxSettings(event, action)
        {

            let formData = new FormData(event.target);

            jQuery.ajax({
                url: "../RxSettings.do",
                type: "post",
                data: formData,
                contentType: false,
                processData: false,
                success: function() {
                    jQuery("#rx-general-success-msg").css('display', 'block');
                    jQuery("#rx-general-error-msg").css('display', 'none');
                },
                error: function () {
                    jQuery("#rx-general-error-msg").css('display', 'block');
                    jQuery("#rx-general-success-msg").css('display', 'none');
                }
            })
        }

        function submitCardLogoForm(event, action)
        {
            event.preventDefault();
            if (action === "deleteImage")
            {
                if(!confirm("Are you sure you want to delete your Card Logo Image"))
                {
                    return;
                }
            }

            jQuery("#card-logo-upload-action").val(action);
            let formData = new FormData(event.target);

            jQuery.ajax({
                url: "../ClinicImage.do",
                type: "post",
                data: formData,
                contentType: false,
                processData: false,
                success: function() {
                    let cardLogoPreview =  jQuery("#card-logo-preview");
                    cardLogoPreview.css("display", "inline-block");
                    cardLogoPreview.attr("src", "../ClinicImage.do?method=getImage&image_type=CARD_LOGO&rand=" + Math.random())
                }
            })
        }

        jQuery(document).ready(function ()
        {
            // watermark
            let submitAction = "uploadImage";
            jQuery("#watermark-submit-upload").click(function()
            {
                submitAction = "uploadImage";
            });
            jQuery("#watermark-submit-delete").click(function()
            {
                submitAction = "deleteImage";
            });

            jQuery("#watermark-form").submit(function(event)
            {
                submitWatermarkForm(event, submitAction);
            });

            jQuery("#rx-form").submit(function(event) {
                event.preventDefault();
                submitRxSettings(event, "setSettings");
            });

            <% if (!RxWatermarkService.isWatermarkEnabled()) { %>
                toggleWatermarkFields(false);
            <%}%>

            // clinic logos
            let clinicLogoSubmitAction = "uploadImage";
            jQuery("#card-logo-submit-upload").click(function ()
            {
                clinicLogoSubmitAction = "uploadImage"
            });
            jQuery("#card-logo-submit-delete").click(function ()
            {
                clinicLogoSubmitAction = "deleteImage"
            });

            jQuery("#card-logo-form").submit(function (event)
            {
               submitCardLogoForm(event, clinicLogoSubmitAction);
            });
        });

    </script>
    </body>
</html:html>
