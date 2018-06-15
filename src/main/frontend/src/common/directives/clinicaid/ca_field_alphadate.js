angular.module('Common').directive(
	'caFieldAlphadate',

	[
		'fieldHelperService',

	function (
		helper
	)
{
	var template_function = function template_function(element, attributes) {
		return helper.resolve_template(attributes, 'src/common/directives/clinicaid/ca_field_alphadate');
	};

	var scope = angular.copy(helper.default_scope);
	scope.placeholder = '@caPlaceholder';

	var alphadate_link_function = function alphadate_link_function(
		$scope, element, attribute, controller)
	{
		// use the passed-in placeholder or default to 'yyyy-mm-dd'
		$scope.input_placeholder = $scope.placeholder;
		if(!angular.isDefined($scope.input_placeholder))
		{
			$scope.input_placeholder = 'yyyy-mm-dd';
		}

		$scope.on_blur = function on_blur()
		{
			// parse the input as a date, then format as YYYY-MM-DD (or clear it)
			var momentDate = moment($scope.model, ["YYYY-MM-DD", "YYYYMMDD"], true);
			if(momentDate.isValid())
			{
				$scope.model = momentDate.format("YYYY-MM-DD");
			}
			else
			{
				$scope.model = null;
			}
		};

		// run the default field link function
		helper.default_link_function($scope, element, attribute, controller);
	};

	return {
		restrict: 'EAC',
		scope: scope,
		templateUrl: template_function,
		replace: true,
		link: alphadate_link_function,
		controller: helper.default_controller
	};

}]);
