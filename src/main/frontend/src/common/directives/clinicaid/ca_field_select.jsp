<div class="form-group {{form_group_class}}"
		 ng-class="{ 'has-error' : error_message, 'has-warning' : warning_message }"
		 title="{{title_string}}"
		 ng-hide="hide">

	<label for="input-{{name}}"
				 class="{{label_size}} control-label"
				 title="{{hint}}">
		{{title}}:
	</label>

	<div class="{{input_size}}">
		<select type="text"
						id="input-{{name}}"
						class="form-control"
						ng-model="model"
						ng-options="option for option in options"
						ng-focus="focus_fn()"
						ng-change="change_fn()"
						ng-disabled="disabled"
						tabindex="{{tab_index}}">
			<option ng-if="include_empty_option == 'true'" value=""></option>
		</select>
	</div>

</div>

