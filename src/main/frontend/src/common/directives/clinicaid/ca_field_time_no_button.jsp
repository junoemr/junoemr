
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
		<input id="input-{{name}}"
		       type="text"
		       class="form-control input-group bootstrap-timepicker {{input_group_class}}"
		       autocomplete="{{autocompleteOff}}"
		       spellcheck="false"
		       placeholder="00:00 AM"
		       title="{{hint}}"
		       ng-click="toggle_widget()"
		       ng-focus="focus_fn()"
		       ng-change="change_fn()"
		       ng-disabled="disabled"
		       ng-model="model"
		       tabindex="{{tab_index}}"/>
	</div>
</div>
