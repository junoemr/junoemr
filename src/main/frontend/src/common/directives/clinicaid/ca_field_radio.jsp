<div class="form-group {{form_group_class}}"
		 ng-class="{ 'has-error' : error_message, 'has-warning' : warning_message }"
		 title="{{title_string}}"
		 ng-hide="hide">

	<label for="input-{{name}}"
				 class="{{label_size}} control-label"
				 title="{{hint}}">
		{{title}}
	</label>

	<div class="{{input_size}} form-control-static juno-radio">
		<input type="radio"
		       id="input-{{name}}"
		       ng-model="model"
		       value="{{value}}"
		       ng-focus="focus_fn()"
		       ng-change="change_fn()"
		       tabindex="{{tab_index}}"
		       ng-disabled="disabled"
		/>
		<label for="input-{{name}}" class="radio">
		</label>
	</div>

</div>
