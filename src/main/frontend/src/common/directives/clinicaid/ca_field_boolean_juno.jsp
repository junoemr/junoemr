<div class="form-group {{form_group_class}}"
		 ng-class="{ 'has-error' : error_message, 'has-warning' : warning_message }"
		 title="{{title_string}}"
		 ng-hide="hide">

	<label for="input-{{name}}"
				 class="{{label_size}} control-label"
				 title="{{hint}}">
		{{title}}
	</label>

	<div class="{{input_size}} form-control-static juno-checkbox">
		<input type="checkbox"
					 bs-switch
					 switch-on-text="Yes"
					 switch-off-text="No"
					 switch-active="{{!disabled}}"
					 id="input-{{name}}"
					 ng-model="model"
					 ng-focus="focus_fn()"
					 ng-change="change_fn()"
					 tabindex="{{tab_index}}"/>
		<label for="input-{{name}}" class="form-control checkmark">
			<i class="icon icon-check"></i>
		</label>
	</div>

</div>
