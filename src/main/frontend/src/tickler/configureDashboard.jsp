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
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<div class="modal-header">
	<button type="button" class="close" ng-click="ticklerConfigureCtrl.close()" aria-label="Close">&times;</button>
	<h3>Configure Ticklers</h3>
</div>
<div class="modal-body">
	<div class="row">
		<form>
			<div class="form-group col-sm-4 col-sm-offset-4">
				<label >Display overdue ticklers only</label>
				<div class="controls">
					<label class="radio-inline" for="radios-per-0">
						<input ng-model="ticklerConfigureCtrl.prefs.expiredTicklersOnly" 
							name="radios-per-0" id="radios-per-0" 
							ng-value="true" 
							type="radio">
						Enable
					</label>
					<label class="radio-inline" for="radios-per-1">
						<input ng-model="ticklerConfigureCtrl.prefs.expiredTicklersOnly" 
							name="radios-per-0" 
							id="radios-per-1" 
							ng-value="false" 
							type="radio">
						Disable
					</label>  
				</div>		  
			</div>		
		</form>
	</div>		
</div>
  
<div class="modal-footer">
	<button class="btn btn-default" ng-click="ticklerConfigureCtrl.close()">
		<bean:message key="global.close" bundle="ui"/>
	</button>
	<button class="btn btn-success" ng-click="ticklerConfigureCtrl.save()">Save Changes</button>
</div>


