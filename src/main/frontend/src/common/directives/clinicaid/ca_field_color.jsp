<div class="form-group {{form_group_class}}"
		 ng-class="{ 'has-error': error_message}"
		 title="{{error_message}}"
		 ng-hide="hide">

	<label for="input-{{name}}"
				 class="{{label_size}} control-label"
				 title="{{hint}}">
		{{title}}:
	</label>

	<div class="{{input_size}}">

		<spectrum-colorpicker
			id="input-{{name}}"
			type="text"
			ng-model="model"
			ng-focus="focus_fn()"
			ng-change="change_fn()"
			tabindex="{{tab_index}}"
			format="'hex'"
			options="{showInput: true, preferredFormat: 'hex'}">
		</spectrum-colorpicker>

	</div>

</div>
