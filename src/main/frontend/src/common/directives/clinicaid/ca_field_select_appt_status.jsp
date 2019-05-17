
<div class="form-group {{form_group_class}}"
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
		<select type="text"
		        id="input-{{name}}"
		        class="form-control"
		        ng-model="model"
		        style="background-color: {{model.color}}"
			<%--ng-options="option.value as option.label for option in options"--%>
		        ng-focus="focus_fn()"
		        ng-change="select_change_fn('input-{{name}}')"
		        ng-disabled="disabled"
		        tabindex="{{tab_index}}">
			<option ng-if="include_empty_option == 'true'" value=""></option>
			<option ng-repeat="option in options"
			        data-icon="fa-heart"
			        data-content="<span class='icon icon-status icon-status-here'>MEEEP</span>{{option.name}}"
			        value="{{option.displayLetter}}"
			        style="background-color: {{option.color}}"
			>
				{{option.name}}
			</option>
		</select>
	</div>

</div>
