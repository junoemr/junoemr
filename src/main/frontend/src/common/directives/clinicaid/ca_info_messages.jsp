<div>

	<!-- TODO-legacy remove -->
	<div class="alert alert-success" ng-show="success_message.length > 0">
		{{success_message}}
	</div>

	<div class="alert alert-info" ng-show="saving">
		{{errors.savingMessage}}
	</div>

	<div ng-if="errors.has_standard_success()"
			 class="alert alert-success">
		<div ng-repeat="success_message in errors.standard_success() track by $index">
			{{success_message}}
		</div>
	</div>

	<div ng-if="errors.has_standard_info()"
			 class="alert alert-info">
		<div ng-repeat="info_message in errors.standard_info() track by $index">
			{{info_message}}
		</div>
	</div>

	<div ng-if="errors.has_standard_warnings()"
			 class="alert alert-warning">
		<div ng-repeat="warning_message in errors.standard_warnings() track by $index">
			{{warning_message}}
		</div>
	</div>

	<div class="alert alert-danger"
			 ng-show="
			 should_display_field_errors() ||
			 errors.has_standard_errors() ||
			 errors.has_error_link()
			 ">

		<div ng-repeat="error_message in errors.standard_errors() track by $index">
			{{error_message}}
		</div>

		<div ng-show="should_display_field_errors()"
				 ng-repeat="(key, errors) in errors.field_errors() track by $index"
				 ng-init="key_label = get_label(key)">

			<div ng-repeat="error in force_array(errors) track by $index">

				<div ng-if="has_sub_errors(error)">

					<div ng-repeat="(sub_key, sub_errors) in error track by $index"
							 ng-init="sub_key_label = get_label(sub_key)">

						<div ng-repeat="sub_error in force_array(sub_errors) track by $index">

							<div ng-if="!prepend_name_to_field_errors">
								{{sub_error}}
							</div>

							<div ng-if="prepend_name_to_field_errors">
								<div>
									{{ key_label }}: {{ sub_key_label }} {{ sub_error }}
								</div>
							</div>
						</div>
					</div>
				</div>

				<div ng-if="!has_sub_errors(error)">

					<div ng-if="!prepend_name_to_field_errors">
						{{error}}
					</div>

					<div ng-if="prepend_name_to_field_errors">
						<div>
							{{ key_label }} {{ error }}
						</div>
					</div>
				</div>

			</div>
		</div>

		<div ng-repeat="error_link in errors.error_links() track by $index">
			<a ng-if="error_link_has_function($index)"
				 href=""
				 ng-click="execute_error_link_fn($index)">{{error_link.label}}</a>
			<a ng-if="!error_link_has_function($index)"
				 href="{{error_link.href}}">{{error_link.label}}</a>
		</div>
	</div>
</div>

