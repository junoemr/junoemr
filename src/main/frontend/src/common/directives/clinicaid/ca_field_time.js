angular.module('Common').directive(
	'caFieldTime',

	[
		'fieldHelperService',
		'$timeout',

	function (
		helper,
		$timeout)
{

	var scope = angular.copy(helper.default_scope);

	scope.minute_step = '=caMinuteStep';
	scope.no_label = '@caNoLabel';
	scope.disable_widget = '=caDisableWidget';

	/* These are all available for the timepicker, but are not wired up yet
	 scope.input_size = '@caInputSize';
	 scope.template = '@caTemplate';
	 scope.max_hours = '@caMaxHours';
	 scope.snap_to_step = '@caSnapToStep';
	 scope.show_seconds = '@caShowSeconds';
	 scope.default_time = '@caDefaultTime';
	 scope.show_meridian = '@caShowMeridian';
	 scope.show_inputs = '@caShowInputs';
	 scope.disable_focus = '@caDisableFocus';
	 scope.disable_mousewheel = '@caDisableMousewheel';
	 scope.disable_unit_hightlight = '@disableUnitHightlight';
	 scope.modal_backdrop = '@caModalBackdrop';
	 scope.append_widget_to = '@caAppendWidgetTo';
	 scope.explicit_mode = '@caExplicitMode';
	 scope.icons = '@caIcons';
	*/

	var template_function = function template_function(element, attributes) {
		return helper.resolve_template(attributes, 'src/common/directives/clinicaid/ca_field_time');
	};

	var link_function = function link_function($scope, element, attribute, controller)
	{
		$scope.picker_container = null;
		$scope.toggle_widget = function toggle_widget()
		{
			if(!$scope.disable_widget)
			{
				$scope.picker_container.timepicker('showWidget');
			}
		};

		$timeout(function()
		{
			$scope.picker_container = $('#input-' + $scope.name);
			$scope.picker_container.timepicker(
				{
					defaultTime: false,
					minuteStep: $scope.minute_step,
					template: ($scope.disable_widget)? false: 'dropdown',
					//disableFocus: true,
					//showInputs: false,
					icons: {
						up: "fa fa-chevron-up",
						down: "fa fa-chevron-down"
					}
				}
			);
		});

		// run the default field link function
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

