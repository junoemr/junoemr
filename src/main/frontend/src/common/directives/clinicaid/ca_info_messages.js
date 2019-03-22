angular.module('Common').directive(
	'caInfoMessages',

	[ function()
{

	var scope = {
		saving: '=?caSavingFlag',
		errors: '=caErrorsObject',
		field_value_map: '=?caFieldValueMap',
		success_message: '@caSuccessMessage',
		field_value_map_label_key: '@caFieldValueMapLabelKey',
		displayFieldErrors: '=?caDisplayFieldErrors',
		prepend_name_to_field_errors: '=?caPrependNameToFieldErrors',
		error_link_functions: '=?caErrorLinkFunctions'
	};

	var link_function = function link_function($scope, element, attribute, controller)
	{
		if(!Juno.Common.Util.exists($scope.displayFieldErrors) ||
			$scope.displayFieldErrors !== false)
		{
			$scope.displayFieldErrors = true;
		}

		if(!Juno.Common.Util.exists($scope.prepend_name_to_field_errors) ||
			$scope.prepend_name_to_field_errors !== false)
		{
			$scope.prepend_name_to_field_errors = true;
		}

		// get a field label given a key
		$scope.get_label = function get_label(key)
		{
			var label = key;
			if(Juno.Common.Util.exists($scope.field_value_map) && $scope.field_value_map.hasOwnProperty(key))
			{
				if(Juno.Common.Util.exists($scope.field_value_map_label_key))
				{
					label = $scope.field_value_map[key][$scope.field_value_map_label_key];
				}
				else
				{
					label = $scope.field_value_map[key];
				}
			}
			return label;
		};

		// Ensure the item is an array of elements
		$scope.force_array = function force_array(item)
		{
			if(!angular.isArray(item))
			{
				return [item];
			}
			return item;
		};

		$scope.has_sub_errors = function has_sub_errors(errors)
		{
			return !angular.isString(errors) && angular.isObject(errors);
		};

		$scope.error_link_has_function = function error_link_has_function(index)
		{
			var error_link = $scope.errors.error_links()[index];

			// error link must have a js function in its data
			// and the function is implemented in error_link_functions
			return $scope.error_link_functions &&
				error_link['js_fn'] &&
				angular.isFunction($scope.error_link_functions[error_link['js_fn']]);
		};

		// execute the ng-click action on error links
		$scope.execute_error_link_fn = function execute_error_link_fn(index)
		{
			var error_link = $scope.errors.error_links()[index];

			// shouldn't be called unless the error link function is
			if(!$scope.error_link_has_function(index))
			{
				console.log("error link does not have a function!", error_link);
				return;
			}

			// basic implementation: always pass the 'uuid' field in as a parameter
			// (may require other options in the future)
			$scope.error_link_functions[error_link['js_fn']](error_link['uuid']);
		};

		$scope.should_display_field_errors = function should_display_field_errors()
		{
			return Juno.Common.Util.exists($scope.errors) &&
				$scope.errors.has_field_errors() &&
				$scope.displayFieldErrors &&
				$scope.errors.displayFieldErrors;
		};
	};

	return {
		restrict: 'E',
		scope: scope,
		templateUrl: 'src/common/directives/clinicaid/ca_info_messages.jsp',
		link: link_function
	};

}]);

