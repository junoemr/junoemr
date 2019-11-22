angular.module('Common').directive(
	'caFieldRadio',

	[
		'fieldHelperService',

	function(
		helper
	)
{
	var scope = angular.copy(helper.default_scope);

	/* for radio groups, the values can be different, but the model should be the same inm order for only one to be selectable*/
	scope.value = '@caValue';

	var template_function = function template_function(element, attributes) {
		return helper.resolve_template(attributes, 'src/common/directives/clinicaid/ca_field_radio');
	};

	var link_function = function link_function(
			$scope, element, attribute, controller)
	{
		// run the default field link function
		helper.default_link_function($scope, element, attribute, controller);
	};

	return {
		restrict: 'EAC',
		scope: scope,
		templateUrl: template_function,
		replace: true,
		link: link_function,
		controller: helper.default_controller
	};

}]);
