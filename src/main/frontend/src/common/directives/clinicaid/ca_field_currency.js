angular.module('Common').directive(
	'caFieldCurrency', 

	[
		'fieldHelperService', 

	function(
		helper
	)
{
	
	return {
		restrict: 'EAC',
		scope: helper.default_scope,
		templateUrl: 'src/common/directives/clinicaid/ca_field_currency.jsp',
		replace: true,
		link: helper.default_link_function,
		controller: helper.default_controller 
	};
	
}]);
