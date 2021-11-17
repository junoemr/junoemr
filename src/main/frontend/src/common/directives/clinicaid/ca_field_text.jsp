
<div class="form-group {{form_group_class}}"
		 ng-class="{ 'has-error': error_message, 'has-warning': warning_message}"
		 title="{{error_message}}"
		 ng-hide="hide">

	<label ng-if="no_label != 'true'"
	       for="input-{{name}}"
	       class="{{label_size}} control-label"
	       title="{{hint}}">
		{{title}}
		<span ng-if="requiredField" class="required-field-marker">*</span>
	</label>

	<div class="{{input_size}}">

		<input id="input-{{name}}"
		       type="{{ hideText ? 'password' : 'text'}}"
		       class="form-control"
		       autocomplete="{{autocompleteOff}}"
		       placeholder="{{text_placeholder}}"
		       ng-class="{'no_scroll_bar': max_characters > 0}"
		       maxlength="{{max_characters}}"
		       ng-model="model"
		       ng-focus="focus_fn()"
		       ng-change="change_fn()"
		       ng-disabled="disabled"
		       tabindex="{{tab_index}}"
		       maxlength="{{text_length}}">
		</input>
		<span class="label label-default textarea-char-counter"
					ng-class="{'hidden': max_characters == null}">
			{{model.length}}/{{max_characters}}
		</span>
	</div>

</div>

