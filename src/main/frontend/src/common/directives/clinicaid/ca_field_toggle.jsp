<div class="juno-toggle-switch form-group {{form_group_class}}"
		 ng-class="{ 'has-error' : error_message, 'has-warning' : warning_message }"
		 title="{{title_string}}"
		 ng-hide="hide">

	<label for="input-{{name}}"
				 class="{{label_size}} control-label"
				 title="{{hint}}">
		{{title}}
	</label>

	<div class="form-control-static vertical-align {{input_size}}">
		<label ng-if="false_text && false_text.length > 0"
		       for="input-{{name}}"
		       class="state-label">
			{{false_text}}
		</label>
		<label class="switch">
			<input type="checkbox"
			       bs-switch
			       switch-on-text="{{true_text}}"
			       switch-off-text="{{false_text}}"
			       ng-true-value="'{{true_value}}'"
			       ng-false-value="'{{false_value}}'"
			       switch-active="{{!disabled}}"
			       id="input-{{name}}"
			       ng-model="model"
			       ng-focus="focus_fn()"
			       ng-change="change_fn()"
			       tabindex="{{tab_index}}"/>
			<span class="slider round"></span>
		</label>
		<label ng-if="true_text && true_text.length > 0"
		       for="input-{{name}}"
		       class="state-label">
			{{true_text}}
		</label>
	</div>
</div>
