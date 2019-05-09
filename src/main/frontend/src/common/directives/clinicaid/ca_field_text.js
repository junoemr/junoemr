angular.module('Common').directive(
	'caFieldText',

	[
		'fieldHelperService',

	function(
		helper
	)
{
	var scope = angular.copy(helper.default_scope);

	scope.rows = '@caRows';
	scope.max_characters = '@caMaxCharacters';

	var template_function = function template_function(element, attributes) {
		return helper.resolve_template(attributes, 'src/common/directives/clinicaid/ca_field_text');
	};

	var link_function = function link_function($scope, element, attribute, controller)
	{
		if(!$scope.model)
		{
			$scope.model = '';
		}

		$scope.$watch('model', function(new_value, old_value)
		{
			if(Juno.Common.Util.exists($scope.model) && $scope.model.length > $scope.max_characters)
			{
				$scope.error_message = "Character limit exceeded. Max allowed: " + $scope.max_characters;
			}
		});

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

