angular.module('Common').directive(
	'caFieldColor', 

	[
		'fieldHelperService', 

	function(
		helper
	)
{
    return {
		restrict: 'EAC',
		scope: helper.default_scope,
		templateUrl: 'src/common/directives/clinicaid/ca_field_color.jsp',
		replace: true,
		link: helper.default_link_function,
		controller: helper.default_controller, 
	};
}]);
