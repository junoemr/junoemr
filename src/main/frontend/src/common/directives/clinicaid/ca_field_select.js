angular.module('Common').directive(
	'caFieldSelect',

	[
		'fieldHelperService',

	function(
		helper
	)
{
	var scope = angular.copy(helper.default_scope);

	scope.options = '=caOptions';
	scope.include_empty_option = '@caEmptyOption';

	var template_function = function template_function(element, attributes) {
		return helper.resolve_template(attributes, 'src/common/directives/clinicaid/ca_field_select');
	};

  return {
		restrict: 'EAC',
		scope: scope,
		templateUrl: template_function,
		replace: true,
		link: helper.default_link_function,
		controller: helper.default_controller
	};
}]);
