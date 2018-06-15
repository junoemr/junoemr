angular.module('Common').directive(
	'caFieldAlpha',

	[
		'fieldHelperService',

	function (
		helper
	)
{
	var template_function = function template_function(element, attributes) {
		return helper.resolve_template(attributes, 'src/common/directives/clinicaid/ca_field_alpha');
	};

	return {
		restrict: 'EAC',
		scope: helper.default_scope,
		templateUrl: template_function,
		replace: true,
		link: helper.default_link_function,
		controller: helper.default_controller
	};

}]);
