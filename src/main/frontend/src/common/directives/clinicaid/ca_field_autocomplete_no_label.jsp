<div class="form-group {{form_group_class}}"
		 ng-class="{ 'has-error' : error_message, 'has-warning' : warning_message }"
		 title="{{title_string}}"
		 ng-hide="hide">

	<div class="{{input_size}}">
		<div class="input-group {{input_group_class}}">
			<input id="input-{{name}}"
						 type="text"
						 class="form-control"
						 autocomplete="{{autocompleteOff}}"
						 spellcheck="false"
						 placeholder="{{input_placeholder}}"
						 ng-model="search_field"
						 ng-change="on_change()"
						 ng-focus="on_focus()"
						 ng-blur="on_blur()"
						 ng-disabled="disabled"
						 tabindex="{{tab_index}}"
						 uib-typeahead="item as item.autocomplete_label for item in autocomplete_items($viewValue)"
						 typeahead-input-formatter="autocomplete_input_formatter($model)"
						 typeahead-on-select="on_select($item, $model, $label, $event)"
						 typeahead-min-length="autocomplete_min_length"
						 typeahead-editable="true"
						 typeahead-select-on-blur="false"
						 typeahead-focus-first="false"
						 typeahead-append-to-body="true"/>

		<span class="input-group-btn" ng-show="show_buttons()">
			<button type="button"
							aria-label="Modify"
							title="Modify"
							class="btn btn-addon"
							ng-if="show_modify()"
							ng-click="on_modify()">
				<i class="fa fa-pencil" aria-hidden="true"></i>
			</button>

			<button type="button"
							aria-label="Create"
							title="Create"
							class="btn btn-addon"
							ng-if="show_create()"
							ng-click="on_create()">
				<i class="fa fa-plus" aria-hidden="true"></i>
			</button>

			<button type="button"
							aria-label="Clear"
							title="Clear"
							class="btn btn-addon"
							ng-if="model"
							ng-click="change_fn(); clear_autocomplete_model(true); clear_autocomplete_search();">
				<i class="fa fa-times" aria-hidden="true"></i>
			</button>
		</span>
		</div>
	</div>

</div>
