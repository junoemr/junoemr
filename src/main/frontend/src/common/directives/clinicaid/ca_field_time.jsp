<div class="form-group ca-bootstrap-timepicker timepicker {{form_group_class}}"
		 ng-class="{ 'has-error' : error_message, 'has-warning' : warning_message }"
		 title="{{title_string}}"
		 ng-hide="hide">

	<label ng-if="no_label != 'true'"
	       for="input-{{name}}"
	       class="{{label_size}} control-label"
	       title="{{hint}}">
		{{title}}
	</label>

	<div class="{{input_size}}">
		<div class="input-group bootstrap-timepicker {{input_group_class}}">
			<input id="input-{{name}}"
						 type="text"
						 class="form-control"
						 autocomplete="{{autocompleteOff}}"
						 spellcheck="false"
						 placeholder="00:00 AM"
						 ng-focus="focus_fn()"
						 ng-change="change_fn()"
						 tabindex="{{tab_index}}"
						 ng-disabled="disabled"
						 ng-model="model"/>

		<span class="input-group-btn">
			<button type="button"
							aria-label="Set"
							title="Set"
							class="btn btn-addon"
							ng-click="toggle_widget()">
				<i class="fa fa-clock-o" aria-hidden="true"></i>
			</button>
		</span>
		</div>
	</div>

</div>
