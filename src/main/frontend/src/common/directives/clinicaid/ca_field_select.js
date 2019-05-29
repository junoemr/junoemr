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

		var linkFunction = function linkFunction($scope, element, attribute, controller)
		{
			$scope.appt_status_change_fn = function select_change_fn(model)
			{
				var option = $scope.options[model];

				element.find(".directive-appt-status-select").css("background-color", option.color);
				var statusElem = element.find(".icon-status");

				var statusIcon = option.icon.substr(0, option.icon.indexOf('.'));

				// remove old status icon class
				statusElem.removeClass(function (index, className)
				{
					return (className.match(/(^|\s)icon-status-\S+/g) || []).join(' ');
				});
				if(statusIcon != null)
				{
					statusElem.addClass("icon-status-" + statusIcon);
				}
			};

			//call this to init the dropdown stuff
			if($scope.template === "appt_status")
			{
				$scope.appt_status_change_fn($scope.model);
			}
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
