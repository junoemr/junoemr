
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
		<%--<select type="text"--%>
		        <%--id="input-{{name}}"--%>
		        <%--class="form-control"--%>
		        <%--ng-model="model"--%>
		        <%--ng-options="option.name for option in options"--%>
		        <%--&lt;%&ndash;style="background-color: {{model.color}}"&ndash;%&gt;--%>
		        <%--ng-focus="focus_fn()"--%>
		        <%--ng-change="select_change_fn({{model}}, 'input-{{name}}')"--%>
		        <%--ng-disabled="disabled"--%>
		        <%--tabindex="{{tab_index}}">--%>
		<%--</select>--%>
		<select type="text"
		        id="input-{{name}}"
		        class="form-control"
		        ng-model="model"
		        <%--style="background-color: {{model.color}}"--%>
		        ng-focus="focus_fn()"
		        ng-change="select_change_fn({{model}}, 'input-{{name}}')"
		        ng-disabled="disabled"
		        tabindex="{{tab_index}}">
			<option ng-if="include_empty_option == 'true'" value=""></option>
			<option ng-repeat="option in options"
			        value="{{option.displayLetter}}"
			        style="background-color: {{option.color}}"
			>
				{{option.name}}
			</option>
		</select>
	<%--</div>--%>
	</div>
</div>
