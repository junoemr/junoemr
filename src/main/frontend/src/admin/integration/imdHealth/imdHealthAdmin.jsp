<%--
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
--%>
<div id="imdhealth-admin">
    <h1>iMD Health Administration</h1>
    <div id="imdhealth-credentials-container">
        <p>Lorem ipsum.... Marketing blurb</p>
        <juno-input
            ng-model="$ctrl.credentials.clientId"
            label="Client ID"
            label-position="$ctrl.LABEL_POSITION.TOP"
            component-style="$ctrl.componentStyle">
        </juno-input>
        <juno-input
            ng-model="$ctrl.credentials.clientSecret"
            label="Client Secret"
            label-position="$ctrl.LABEL_POSITION.TOP"
            component-style="$ctrl.componentStyle">
        </juno-input>
        <juno-button
                ng-click="$ctrl.onSave()"
                button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL"
                button-color="JUNO_BUTTON_COLOR.PRIMARY"
                component-style="$ctrl.componentStyle">
            Update Credentials
        </juno-button>
    </div>
</div>
