angular.module('Common').directive(
		'caFieldToggle',

		[
			'fieldHelperService',

			function(
					helper
			)
			{

				var template_function = function template_function(element, attributes) {
					return helper.resolve_template(attributes, 'src/common/directives/clinicaid/ca_field_toggle');
				};

				var link_function = function link_function($scope, element, attribute, controller)
				{
					// run the default field link function
					helper.default_link_function($scope, element, attribute, controller);
				};

				var scope = angular.copy(helper.default_scope);
				scope.true_text = '@caTrueText';
				scope.false_text = '@caFalseText';
				scope.true_value = '@caTrueValue';
				scope.false_value = '@caFalseValue';

				return {
					restrict: 'EAC',
					scope: scope,
					templateUrl: template_function,
					replace: true,
					link: link_function,
					controller: helper.default_controller
				};

			}]);
