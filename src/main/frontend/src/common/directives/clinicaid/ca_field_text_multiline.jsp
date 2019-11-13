
<div class="form-group {{form_group_class}}"
		 ng-class="{ 'has-error': error_message}"
		 title="{{error_message}}"
		 ng-hide="hide">

	<label ng-if="no_label != 'true'"
	       for="input-{{name}}"
	       class="{{label_size}} control-label"
	       title="{{hint}}">
		{{title}}
	</label>

	<div class="{{input_size}}">

		<textarea id="input-{{name}}"
		          class="form-control"
		          placeholder="{{text_placeholder}}"
		          ng-class="{'no_scroll_bar': max_characters > 0}"
		          rows="{{rows}}"
		          ng-model="model"
		          ng-focus="focus_fn()"
		          ng-change="change_fn()"
		          ng-disabled="disabled"
		          tabindex="{{tab_index}}">
		</textarea>
		<span class="label label-default textarea-char-counter"
					ng-class="{'hidden': max_characters == null}">
			{{model.length}}/{{max_characters}}
		</span>
	</div>

</div>

