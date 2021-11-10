<div class="form-group {{form_group_class}}"
		 ng-class="{ 'has-error' : error_message, 'has-warning' : warning_message }"
		 title="{{title_string}}"
		 ng-hide="hide">

	<div class="{{input_size}} ca-field-date3">

		<input type="text"
					 class="form-control yyyy"
					 id="input-{{name}}[year]"
					 name="{{name}}[year]"
					 value="{{model | dateString:'yyyy'}}"
					 ng-model="field_year"
					 ng-focus="focus_fn_year()"
					 ng-change="change_fn()"
					 ng-disabled="disabled"
					 tabindex="{{tab_index}}"
					 placeholder="yyyy"
					 autocomplete="{{autocompleteOff}}"
					 spellcheck="off"
					 maxlength="4"/>-

		<input name="{{name}}[month]"
					 class="form-control mmdd"
					 type="text"
					 value="{{model | dateString:'MM'}}"
					 ng-model="field_month"
					 ng-focus="focus_fn_month()"
					 ng-change="change_fn()"
					 ng-disabled="disabled"
					 tabindex="{{tab_index}}"
					 placeholder="mm"
					 autocomplete="{{autocompleteOff}}"
					 spellcheck="off"
					 maxlength="2"/>-

		<input type="text"
					 name="{{name}}[day]"
					 class="form-control mmdd"
					 value="{{model | dateString:'dd'}}"
					 ng-model="field_day"
					 ng-focus="focus_fn_day()"
					 ng-change="change_fn()"
					 ng-disabled="disabled"
					 tabindex="{{tab_index}}"
					 placeholder="dd"
					 autocomplete="{{autocompleteOff}}"
					 spellcheck="off"
					 maxlength="2"/>
	</div>

</div>

