<span class="btn-group">
		<button type="button" class="btn btn-default"
						ng-repeat="button_options in field_settings.options"
						ng-class="{'active': is_selected(button_options.value)}"
						ng-click="toggle_selected(button_options.value)" >
			{{button_options.label}}
		</button>
</span>
