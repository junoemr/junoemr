angular.module('Common').directive(
	'junoAppointmentStatusSelect',

	[
		'$timeout',
		'fieldHelperService',
		'scheduleService',

	function(
		$timeout,
		helper,
		scheduleService,
	)
	{
		var scope = angular.copy(helper.default_scope);

		scope.options = '=caOptions';
		scope.include_empty_option = '@caEmptyOption';
		scope.no_label = '@caNoLabel';

		var template_function = function template_function(element, attributes)
		{
			return helper.resolve_template(attributes, 'src/common/directives/appointment/juno_appointment_status_select');
		};

		var linkFunction = function linkFunction($scope, element, attribute, controller)
		{
			// We need a list of all possible appointment statuses (even if some may be inactive)
			// because the current appointment status might be one of those inactive ones.
			// If that's the case, we'll leave it in the menu (but disable it),
			// and remove all of the the other inactive ones.
			$scope.getValidOptions = () =>
			{
				// $scope.options is an object... not an array.
				const selectedStatus = $scope.model;
				const validOptions = [];

				Object.values($scope.options).forEach(status =>
				{
					if (status.enabled || status.displayLetter === selectedStatus)
					{
						validOptions.push(status);
					}
				});

				const validOptionsAsObj = validOptions.reduce((acc, status) => {
					acc[status.displayLetter] = status;
					return acc;
				}, {})

				return validOptionsAsObj;
			}

			$scope.select_change_fn = function select_change_fn()
			{
				$scope.setStatus();

				// call the custom ng-change function
				$timeout(function ()
				{
					$scope.change_fn();
				});
			};
			$scope.button_change_fn = function button_change_fn()
			{
				if(!scope.disabled)
				{
					$scope.model = scheduleService.getNextRotateStatus($scope.model);
					$scope.setStatus();

					// call the custom ng-change function
					$timeout(function ()
					{
						$scope.change_fn();
					});
				}
			};
			$scope.setStatus = function setStatus()
			{
				var model = $scope.model;
				var option = $scope.options[model];
				var statusElem = element.find(".icon");

				// remove old status icon class
				statusElem.removeClass(function (index, className)
				{
					return (className.match(/(^|\s)icon-\S+/g) || []).join(' ');
				});

				if(Juno.Common.Util.exists(option))
				{
					element.find(".directive-appt-status-select").css("background-color", option.color);
					var statusIcon = option.icon.substr(0, option.icon.indexOf('.'));
					statusElem.addClass("icon-" + statusIcon);
				}
			};

			$scope.setStatus();

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