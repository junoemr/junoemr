
<div class="form-group {{form_group_class}}"
		 ng-class="{ 'has-error' : error_message, 'has-warning' : warning_message }"
		 title="{{title_string}}"
		 ng-hide="hide">

	<label ng-if="no_label != 'true'"
	       for="input-{{name}}"
	       class="{{label_size}} control-label"
	       title="{{hint}}">
		{{title}}
	</label>

	<div class="{{input_size}} directive-appt-status-select-wrapper">
		<div class="directive-appt-status-select">
			<div class="icon-wrapper">
				<button class="btn btn-icon"
			        ng-disabled="disabled"
					ng-click="button_change_fn()">
					<i class="icon icon-status"></i>
				</button>
			</div>
			<select type="text"
			        id="input-{{name}}"
			        class="form-control"
			        ng-model="model"
			        style="background-color: transparent"
			        ng-focus="focus_fn()"
			        ng-change="select_change_fn()"
			        ng-disabled="disabled"
			        tabindex="{{tab_index}}">
				<option ng-if="include_empty_option == 'true'" value=""></option>
				<option ng-repeat="option in options"
				        value="{{option.displayLetter}}"
				        <%--style="background-color: {{option.color}}"--%>
				>
					{{option.name}}
				</option>
			</select>
		</div>
	</div>
</div>
