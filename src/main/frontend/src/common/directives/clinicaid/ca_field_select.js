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
		scope.no_label = '@caNoLabel';

		var template_function = function template_function(element, attributes)
		{
			return helper.resolve_template(attributes, 'src/common/directives/clinicaid/ca_field_select');
		};

		var linkFunction = function linkFunction($scope, element, attribute, controller) {
			$scope.select_change_fn = function select_change_fn(model, elementId)
			{
				// var element = $("#"+elementId);
				// var selectedOption = element.find(":selected");
				// var selectedColor = selectedOption.css('background-color');
				// var new_model = $scope.options[model.displayLetter];

				// change the background color to match the selected option
				// element.css('background-color', selectedColor);

				//remove existing icon classes & add selected one
				// element.find(".icon-status").removeClass(function (index, className)
				// {
				// 	return (className.match(/(^|\s)icon-status-\S+/g) || []).join(' ');
				// });
				// element.addClass("icon-status-todo")
			};
			helper.default_link_function($scope, element, attribute, controller);
		};

		return {
			restrict: 'EAC',
			scope: scope,
			templateUrl: template_function,
			replace: true,
			link: linkFunction,
			controller: helper.default_controller
		};
	}
]);
