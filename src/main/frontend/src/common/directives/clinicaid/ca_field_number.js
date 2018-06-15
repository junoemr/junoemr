angular.module('Common').directive(
	'caFieldNumber',

	[
		'fieldHelperService',

	function(
		helper
	)
{
	var scope = angular.copy(helper.default_scope);
	scope.pad_length = '@caPadLength';

	var link_function = function link_function($scope, element, attribute, controller)
	{
		if(!$scope.pad_length)
		{
			$scope.pad_length = 0;
		}

		// run the default field link function
		helper.default_link_function($scope, element, attribute, controller);
	};

	return {
		restrict: 'EAC',
		scope: scope,
		templateUrl: 'src/common/directives/clinicaid/ca_field_number.jsp',
		replace: true,
		link: link_function,
		controller: helper.default_controller
	};

}]);
