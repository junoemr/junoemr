<%--

	Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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

	This software was written for
	CloudPractice Inc.
	Victoria, British Columbia
	Canada

--%>
<div class="modal-header">
	<button type="button" class="close" aria-label="Close" ng-click="cancel()">
		<span aria-hidden="true">&times;</span>
	</button>
	<h3 class="modal-title">
		<span ng-hide="editMode">Add {{label}}</span>
		<span ng-show="editMode">Modify {{label}}</span>
	</h3>
</div>


<form ng-submit="save()" class="schedule-modal event" ng-init="init()">

	<div class="modal-body">
		<div ng-show="!isInitialized() || isWorking()" ng-include="'src/common/spinner.jsp'"></div>
		<div ng-show="isInitialized() && !isWorking()">

			<div class="schedule-header">
				<h4 class="pull-left schedule-title">{{schedule.display_name}}</h4>

				<div class="pull-right" ng-show="editMode">
					<span ng-repeat="tag in tagNames"
						  class="label label-default">
						<i class="fa fa-tag"></i>
						{{ tag }}
					</span>
				</div>

				<div class="pull-right" ng-show="editMode && numInvoices > 0">
					<button type="button"
							class="btn btn-sm btn-success"
							ng-click="viewInvoices()">
						View Invoice<span ng-show="numInvoices > 1">s</span>
					</button>
				</div>
			</div>

			<ca-info-messages
					ca-errors-object="displayMessages"
					ca-field-value-map="fieldValueMapping"
					ca-prepend-name-to-field-errors="false"
			></ca-info-messages>

			<div class="row form-horizontal stacked-labels">
				<div class="col-sm-6">

					<div class="form-group"
						 ng-class="{ 'has-error': displayMessages.fieldErrors()['start_date'] }">

						<label class="control-label col-sm-12">
							Start Time:
						</label>

						<div class="col-sm-6">
							<ca-field-date
									ca-template="bare"
									ca-date-picker-id="start-date-picker"
									ca-name="start_date"
									ca-model="startDate"
									ca-orientation="auto"
							></ca-field-date>
						</div>

						<div class="col-sm-6 time-wrapper">
							<ca-field-time
									ca-template="bare"
									ca-name="start_time"
									ca-model="startTime"
									ca-minute-step="parentScope.timeIntervalMinutes()">
							</ca-field-time>
						</div>

					</div>

					<div class="form-group"
						 ng-class="{ 'has-error': displayMessages.fieldErrors()['end_date'] }">

						<label class="control-label col-sm-12">
							End Time:
						</label>

						<div class="col-sm-6">
							<ca-field-date
									ca-template="bare"
									ca-date-picker-id="end-date-picker"
									ca-name="end_date"
									ca-model="endDate"
									ca-orientation="auto"
							></ca-field-date>
						</div>

						<div class="col-sm-6 time-wrapper">
							<ca-field-time
									ca-template="bare"
									ca-name="end_time"
									ca-model="endTime"
									ca-minute-step="parentScope.timeIntervalMinutes()">
							</ca-field-time>
						</div>
					</div>

					<div class="form-group">
						<label for="input-event-status" class="control-label col-sm-12">
							Status:
						</label>
						<div class="col-sm-10">
							<select id="input-event-status"
									class="form-control"
									ng-model="selectedEventStatus"
									ng-options="option as option.name for option in eventStatusOptions">
							</select>
						</div>
						<span class="event-color"
							  style="background-color: {{selectedEventStatus.color}};"></span>
					</div>

					<div class="form-group" ng-class="{'has-error': displayMessages.fieldErrors()['location']}">
						<label for="input-site" class="control-label col-sm-12">
							Site:
						</label>
						<div class="col-sm-10">
							<select id="input-site"
									class="form-control"
									ng-model="selectedSiteName">
								<option
										ng-repeat="option in parentScope.siteOptions"
										value="{{option.name}}"
										style="background-color: {{option.color}}">
									{{option.display_name}}
								</option>
							</select>
						</div>
					</div>

				</div>

				<div class="col-sm-6"
					 ng-show="activeTemplateEvents.length > 0">

					<label class="control-label">Availability during appointment:</label>

					<div class="availability"
						 ng-repeat="templateEvent in activeTemplateEvents | limitTo: 4">

						<div class="pull-left color">
							<div class="event-color"
								 style="background-color: {{templateEvent.color}};">
							</div>
						</div>
						<div class="pull-left info">
							<div>{{ templateEvent.availability_type.name }}</div>

							<div class="availability-detail">
								{{ templateEvent.start.format('h:mma') }} -
								{{ templateEvent.end.format('h:mma') }}
							</div>
							<a class="availability-detail"
							   ng-show="templateEvent.availability_type.preferred_event_length_minutes != null"
							   href=""
							   ng-click="setEventLength(templateEvent.availability_type.preferred_event_length_minutes)">
								({{ templateEvent.availability_type.preferred_event_length_minutes }} minutes)
							</a>
						</div>
					</div>

					<div class="availability" ng-show="activeTemplateEvents.length > 4">
						<div class="availability-detail more-availability">
							and {{ activeTemplateEvents.length - 4 }} more...
						</div>
					</div>

				</div>
			</div>

			<div class="row form-horizontal stacked-labels">
				<div class="col-sm-12">
					<ca-field-text
							ca-label-size="col-sm-12"
							ca-input-size="col-sm-12"
							ca-title="Reason"
							ca-name="event_reason"
							ca-model="eventData.reason"
							ca-max-characters="300"
							ca-rows="1">
					</ca-field-text>
				</div>
			</div>

			<div class="row form-horizontal stacked-labels">
				<div class="col-sm-12">
					<ca-field-text
							ca-label-size="col-sm-12"
							ca-input-size="col-sm-12"
							ca-title="Notes"
							ca-name="event_description"
							ca-model="eventData.description"
							ca-max-characters="600"
							ca-rows="2">
					</ca-field-text>
				</div>
			</div>

			<div class="row form-horizontal stacked-labels">
				<div class="col-sm-12">
					<div class="form-group"
						 title="Patient">

						<label for="input-patient"
							   class="col-sm-12 control-label">
							Patient:
						</label>

						<div class="col-sm-12">
							<input type="text"
								   id="input-patient"
								   ng-model="autocompleteValues.patient"
								   placeholder="Patient"
								   uib-typeahead="pt.data.full_name for pt in parentScope.searchPatients($viewValue)"
								   typeahead-on-select="onSelectPatient($item, $model, $label)"
								   class="form-control"
								   autocomplete="off"/>
						</div>

					</div>
				</div>
			</div>

			<div class="row">

				<div class="col-sm-3 patient-image" ng-show="isPatientSelected()">
					<div class="file-upload-drop-box">
						<span class="upload-text" ng-if="!patient.has_photo">Upload Photo</span>
						<img class="patient-photo" title="Click to change image"
							 ng-src="{{patient.patient_photo_url}}"
							 align="middle" />
					</div>
				</div>

				<div class="col-sm-9 form-horizontal form-compact" ng-show="isPatientSelected()">

					<div class="form-group">
						<label class="control-label col-sm-4">
							Selected Patient:
						</label>
						<div class="col-sm-8">
							<div class="form-control-static">
								<a ng-href="#!/record/{{ patient.uuid }}/summary" target="_blank">{{ patient.full_name }}</a>
							</div>
						</div>
					</div>

					<div class="form-group">
						<label class="control-label col-sm-4">
							Health Number:
						</label>
						<div class="col-sm-8">

							<span ng-if="patient.data.health_number">
								<span class="patient-health-number">
									{{patient.data.health_number}}
									{{patient.data.ontario_version_code}}
								</span>
								<button type="button"
										aria-label="Check Eligibility"
										title="{{patient.eligibility_text}}"
										class="btn"
										ng-class="{
														'btn-addon': (patient.checking_eligibility || patient.eligibility == null) && !patient.polling_eligibility,
														'btn-warning': patient.polling_eligibility,
														'btn-success': patient.eligibility == 'eligible',
														'btn-danger': patient.eligibility == 'ineligible' }"
										ng-click="patient.get_eligibility(true, true)">
									<i class="fa fa-user" aria-hidden="true"></i>
								</button>
							</span>
							<p ng-if="!patient.data.health_number"
							   class="form-control-static"></p>
						</div>
					</div>

					<div class="form-group">
						<label class="control-label col-sm-4">
							Birth Date:
						</label>
						<div class="col-sm-8">
							<div class="form-control-static">
								{{ patient.data.birth_date }}
							</div>
						</div>
					</div>

					<div class="form-group">
						<label class="control-label col-sm-4">
							Phone Number:
						</label>
						<div class="col-sm-8">
							<div class="form-control-static">
								{{ patient.data.phone_number_primary }}
							</div>
						</div>
					</div>

				</div>

			</div>

		</div>
	</div>

	<div class="modal-footer">

		<div class="pull-left">
			<button type="button"
					class="btn btn-danger"
					ng-show="editMode"
					ng-click="delete()"
					ng-disabled="isWorking()">
				Delete
			</button>
		</div>

		<div class="pull-right">
			<button
					type="submit"
					class="btn btn-primary"
					tooltip-placement="top"
					tooltip-append-to-body="true"
					uib-tooltip="{{keyBinding.getTooltip(keyBindSettings, 'ctrl+enter')}}"
					ng-show="!editMode"
					ng-disabled="isWorking()">Create</button>

			<button
					type="submit"
					class="btn btn-primary"
					tooltip-placement="top"
					tooltip-append-to-body="true"
					uib-tooltip="{{keyBinding.getTooltip(keyBindSettings, 'ctrl+enter')}}"
					ng-show="editMode"
					ng-disabled="isWorking()">Modify</button>

			<button
					type="button"
					class="btn btn-success"
					tooltip-placement="top"
					tooltip-append-to-body="true"
					uib-tooltip="{{keyBinding.getTooltip(keyBindSettings, 'ctrl+shift+enter')}}"
					ng-click="saveAndBill()"
					ng-show="numInvoices == 0"
					ng-disabled="isWorking()">Modify &amp; Bill</button>

			<button
					type="button"
					class="btn btn-default"
					ng-click="cancel()"
					ng-disabled="isWorking()">Cancel</button>

		</div>
	</div>

	<div id="start-time-auto-wrapper"></div>
	<div id="end-time-auto-wrapper"></div>

</form>
