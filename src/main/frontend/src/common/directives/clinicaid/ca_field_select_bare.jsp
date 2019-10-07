
<div class="{{input_size}}">
	<select type="text"
					id="input-{{name}}"
					class="form-control"
					ng-model="model"
					ng-options="option.value as option.label for option in options"
					ng-focus="focus_fn()"
					ng-change="change_fn()"
					ng-disabled="disabled"
					tabindex="{{tab_index}}">
		<option ng-if="include_empty_option == 'true'" value="">{{text_placeholder}}</option>
	</select>
</div>

