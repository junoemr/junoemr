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
<div id="admin-system-properties">
    <div class="navbar">
        <ul class="nav navbar-nav">
            <li ng-class="{'active': $ctrl.isTabActive('admin.systemProperties.rx')}">
                <a href="javascript:void(0)" ng-click="$ctrl.changeTab('admin.systemProperties.rx')">Rx</a>
            </li>
        </ul>
    </div>
    <hr>
    <div class="admin-system-properties content">
        <ui-view></ui-view>
    </div>
</div>