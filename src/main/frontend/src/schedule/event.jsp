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
		<span ng-hide="edit_mode">Add {{label}}</span>
		<span ng-show="edit_mode">Modify {{label}}</span>
	</h3>
</div>


<form ng-submit="save()" class="schedule-modal event" ng-init="init()">

	<div class="modal-body">
		<div ng-show="!is_initialized() || is_working()" ng-include="'src/common/spinner.jsp'"></div>
		<div ng-show="is_initialized() && !is_working()">

			<div class="schedule-header">
				<h4 class="pull-left schedule-title">{{schedule.display_name}} schedule</h4>

				<div class="pull-right" ng-show="edit_mode">
					<span ng-repeat="tag in tag_names"
						  class="label label-default">
						<i class="fa fa-tag"></i>
						{{ tag }}
					</span>
				</div>

				<div class="pull-right" ng-show="edit_mode && num_invoices > 0">
					<button type="button"
							class="btn btn-sm btn-success"
							ng-click="view_invoices()">
						View Invoice<span ng-show="num_invoices > 1">s</span>
					</button>
				</div>
			</div>

			<ca-info-messages
					ca-errors-object="display_messages"
					ca-field-value-map="field_value_mapping"
			></ca-info-messages>

			<div class="row form-horizontal stacked-labels">
				<div class="col-sm-6">

					<div class="form-group"
						 ng-class="{ 'has-error': display_messages.field_errors()['start_date'] }">

						<label class="control-label col-sm-12">
							Start Time:
						</label>

						<div class="col-sm-6">
							<ca-field-date
									ca-template="bare"
									ca-date-picker-id="start-date-picker"
									ca-name="start_date"
									ca-model="start_date"
									ca-orientation="auto"
							></ca-field-date>
						</div>

						<div class="col-sm-6 time-wrapper">
							<ca-field-time
									ca-template="bare"
									ca-name="start_time"
									ca-model="start_time"
									ca-minute-step="parent_scope.time_interval_minutes()">
							</ca-field-time>
						</div>

					</div>

					<div class="form-group"
						 ng-class="{ 'has-error': display_messages.field_errors()['end_date'] }">

						<label class="control-label col-sm-12">
							End Time:
						</label>

						<div class="col-sm-6">
							<ca-field-date
									ca-template="bare"
									ca-date-picker-id="end-date-picker"
									ca-name="end_date"
									ca-model="end_date"
									ca-orientation="auto"
							></ca-field-date>
						</div>

						<div class="col-sm-6 time-wrapper">
							<ca-field-time
									ca-template="bare"
									ca-name="end_time"
									ca-model="end_time"
									ca-minute-step="parent_scope.time_interval_minutes()">
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
									ng-model="selected_event_status"
									ng-options="option as option.name for option in event_status_options">
							</select>
						</div>
						<span class="event-color"
							  style="background-color: {{selected_event_status.color}};"></span>
					</div>

					<div class="form-group" ng-class="{'has-error': display_messages.field_errors()['location']}">
						<label for="input-site" class="control-label col-sm-12">
							Site:
						</label>
						<div class="col-sm-10">
							<select id="input-site"
									class="form-control"
									ng-model="selected_site_name">
								<option
										ng-repeat="option in parent_scope.site_options"
										value="{{option.name}}"
										style="background-color: {{option.color}}">
									{{option.display_name}}
								</option>
							</select>
						</div>
					</div>

				</div>

				<div class="col-sm-6"
					 ng-show="active_template_events.length > 0">

					<label class="control-label">Availability during appointment:</label>

					<div class="availability"
						 ng-repeat="template_event in active_template_events | limitTo: 4">

						<div class="pull-left color">
							<div class="event-color"
								 style="background-color: {{template_event.color}};">
							</div>
						</div>
						<div class="pull-left info">
							<div>{{ template_event.availability_type.name }}</div>

							<div class="availability-detail">
								{{ template_event.start.format('h:mma') }} -
								{{ template_event.end.format('h:mma') }}
							</div>
							<a class="availability-detail"
							   ng-show="template_event.availability_type.preferred_event_length_minutes != null"
							   href=""
							   ng-click="set_event_length(template_event.availability_type.preferred_event_length_minutes)">
								({{ template_event.availability_type.preferred_event_length_minutes }} minutes)
							</a>
						</div>
					</div>

					<div class="availability" ng-show="active_template_events.length > 4">
						<div class="availability-detail more-availability">
							and {{ active_template_events.length - 4 }} more...
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
							ca-model="event_data.reason"
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
							ca-model="event_data.description"
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
								   ng-model="autocomplete_values.patient"
								   placeholder="Patient"
								   uib-typeahead="pt.data.full_name for pt in parent_scope.calendar_api_adapter.searchPatients($viewValue)"
								   typeahead-on-select="on_select_patient($item, $model, $label)"
								   class="form-control"
								   autocomplete="off"/>
						</div>

					</div>
				</div>
			</div>

			<div class="row">

				<div class="col-sm-3 patient-image" ng-show="is_patient_selected()">
					<div class="file-upload-drop-box">
						<span class="upload-text" ng-if="!patient.has_photo">Upload Photo</span>
						<img class="patient-photo" title="Click to change image"
							 ng-src="{{patient.patient_photo_url}}"
							 align="middle" />
					</div>
				</div>

				<div class="col-sm-9 form-horizontal form-compact" ng-show="is_patient_selected()">

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
					ng-show="edit_mode"
					ng-click="delete()"
					ng-disabled="is_working()">
				Delete
			</button>
		</div>

		<div class="pull-right">
			<button
					type="submit"
					class="btn btn-primary"
					tooltip-placement="top"
					tooltip-append-to-body="true"
					uib-tooltip="{{key_binding.get_tooltip(key_bind_settings, 'ctrl+enter')}}"
					ng-show="!edit_mode"
					ng-disabled="is_working()">Create</button>

			<button
					type="submit"
					class="btn btn-primary"
					tooltip-placement="top"
					tooltip-append-to-body="true"
					uib-tooltip="{{key_binding.get_tooltip(key_bind_settings, 'ctrl+enter')}}"
					ng-show="edit_mode"
					ng-disabled="is_working()">Modify</button>

			<button
					type="button"
					class="btn btn-success"
					tooltip-placement="top"
					tooltip-append-to-body="true"
					uib-tooltip="{{key_binding.get_tooltip(key_bind_settings, 'ctrl+shift+enter')}}"
					ng-click="save_and_bill()"
					ng-show="num_invoices == 0"
					ng-disabled="is_working()">Modify &amp; Bill</button>

			<button
					type="button"
					class="btn btn-default"
					ng-click="cancel()"
					ng-disabled="is_working()">Cancel</button>

		</div>
	</div>

	<div id="start-time-auto-wrapper"></div>
	<div id="end-time-auto-wrapper"></div>

</form>
