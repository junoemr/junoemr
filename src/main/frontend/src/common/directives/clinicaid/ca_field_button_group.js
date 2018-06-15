angular.module('Common').directive(
		'caFieldButtonGroup',

		[
			'fieldHelperService',

			function(
					helper
			)
			{

				var link_function = function link_function($scope, element, attribute, controller)
				{
					// run the default field link function
					helper.default_link_function($scope, element, attribute, controller);

					$scope.selected_values = [$scope.field_default];

					$scope.is_selected = function is_selected(value)
					{
						return $scope.selected_values.indexOf(value) != -1;
					};

					$scope.toggle_selected = function toggle_selected(value)
					{
						var value_index = $scope.selected_values.indexOf(value);
						if(value_index == -1)
						{
							$scope.selected_values.push(value);
						}
						else
						{
							$scope.selected_values.splice(value_index, 1);
						}
						$scope.model = $scope.selected_values.join(",");
					};
				};

				var scope = angular.copy(helper.default_scope);
				scope.field_settings = '=caFieldSettings';
				scope.field_default = '=caFieldDefault';

				return {
					restrict: 'EAC',
					scope: scope,
					templateUrl: 'src/common/directives/clinicaid/ca_field_button_group.jsp',
					replace: true,
					link: link_function,
					controller: helper.default_controller
				};

			}]);
