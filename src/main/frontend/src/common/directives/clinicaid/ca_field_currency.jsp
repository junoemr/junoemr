<div class="form-group {{form_group_class}}"
		 ng-class="{ 'has-error': error_message}"
		 title="{{error_message}}"
		 ng-hide="hide">

	<label for="input-{{name}}"
				 class="{{label_size}} control-label"
				 title="{{hint}}">
		{{title}}<span ng-if="!hide_label_colon">:</span>
	</label>

	<div class="{{input_size}}">
		<input ca-field-currency-filter
					 type="text"
					 id="input-{{name}}"
					 class="form-control"
					 ng-model="model"
					 ng-focus="focus_fn()"
					 ng-change="change_fn()"
					 ng-disabled="disabled"
					 tabindex="{{tab_index}}"/>
	</div>

</div>
