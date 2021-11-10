
<div id="{{ date_picker_id }}"
		 class="form-group {{form_group_class}}"
		 ng-class="{ 'has-error' : error_message, 'has-warning' : warning_message }"
		 title="{{title_string}}"
		 ng-hide="hide">

	<label ng-if="no_label != 'true'"
	       for="input-{{name}}"
	       class="{{label_size}} control-label"
	       title="{{hint}}">
		{{title}}
		<span ng-if="requiredField" class="required-field-marker">*</span>
	</label>

	<div class="{{input_size}} {{ date_picker_id }}-body">
		<input type="text"
					 id="input-{{name}}"
					 class="form-control"
					 ng-model="model"
					 ng-focus="on_focus($event)"
					 ng-change="delayed_change_fn()"
					 ng-disabled="disabled"
					 ng-click="on_click($event)"
					 ng-keyup="key_up($event)"
					 ng-keydown="key_down($event)"
					 tabindex="{{tab_index}}"
					 autocomplete="{{autocompleteOff}}"
					 placeholder="yyyy-mm-dd">
	</div>
</div>

