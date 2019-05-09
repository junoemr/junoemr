<div class="form-group {{form_group_class}}"
		 ng-class="{ 'has-error' : error_message, 'has-warning' : warning_message }"
		 title="{{title_string}}"
		 ng-hide="hide">

	<div class="{{input_size}}">
		<input type="text"
					 id="input-{{name}}"
					 class="form-control"
					 ng-model="model"
					 ng-focus="focus_fn()"
					 ng-change="change_fn()"
					 ng-disabled="disabled"
					 tabindex="{{tab_index}}"/>
	</div>

</div>
