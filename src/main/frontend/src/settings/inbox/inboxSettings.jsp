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
<div class="row">
	<div class="col-lg-4 col-sm-6 md-margin-top">
		<div class="form-group col-sm-12">
			<label>Disable comment on acknowledgment:</label>
			<div class="controls">
				<label class="radio-inline" for="radios-inb-0">
					<input name="radios-inb-0" id="radios-inb-0" ng-model="$ctrl.pref.disableCommentOnAck" ng-value="true" type="radio">
					Enable
				</label>
				<label class="radio-inline" for="radios-inb-1">
					<input name="radios-inb-1" id="radios-inb-1" ng-model="$ctrl.pref.disableCommentOnAck" ng-value="false" type="radio">
					Disable
				</label>
			</div>
		</div>
	</div>
	<div class="form-group col-sm-12 m-t-16">
		<label>Top Bar Unread Message Count:</label>
		<div class="controls">
			<label class="radio m-l-24" for="radios-mcm-0">
				<input name="radios-mcm-0" id="radios-mcm-0" ng-model="$ctrl.pref.messageCountMode" ng-value="$ctrl.MessageCountMode.Mha" type="radio">
				Count only MHA messages.
			</label>
			<label class="radio m-l-24" for="radios-inb-1">
				<input name="radios-mcm-1" id="radios-mcm-1" ng-model="$ctrl.pref.messageCountMode" ng-value="$ctrl.MessageCountMode.Internal" type="radio">
				Count only internal messages.
			</label>
			<label class="radio m-l-24" for="radios-mcm-2">
				<input name="radios-mcm-2" id="radios-mcm-2" ng-model="$ctrl.pref.messageCountMode" ng-value="$ctrl.MessageCountMode.MhaInternal" type="radio">
				Sum MHA and internal message count.
			</label>
		</div>
	</div>

</div>


