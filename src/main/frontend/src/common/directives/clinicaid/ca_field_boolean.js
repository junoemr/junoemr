angular.module('Common').directive(
	'caFieldBoolean',

	[
		'fieldHelperService',

	function(
		helper
	)
{

	var template_function = function template_function(element, attributes) {
		return helper.resolve_template(attributes, 'src/common/directives/clinicaid/ca_field_boolean');
	};

	var link_function = function link_function(
			$scope, element, attribute, controller)
	{
		if(!Juno.Common.Util.exists($scope.model))
		{
			$scope.model = false;
		}

		// run the default field link function
		helper.default_link_function($scope, element, attribute, controller);
	};

	return {
		restrict: 'EAC',
		scope: helper.default_scope,
		templateUrl: template_function,
		replace: true,
		link: link_function,
		controller: helper.default_controller
	};

}]);
