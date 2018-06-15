angular.module('Common').directive(
	'caFieldDate3',

	[
		'$timeout',
		'fieldHelperService',

	function(
		$timeout, helper
	)
{
	var default_scope = angular.copy(helper.default_scope);

	delete default_scope.focus_fn;
	default_scope.on_focus_fn_year = '&caFocusYear';
	default_scope.on_focus_fn_month = '&caFocusMonth';
	default_scope.on_focus_fn_day = '&caFocusDay';

	var get_date = function get_date(new_value, old_value, $scope)
	{
		$scope.field_year = '';
		$scope.field_month = '';
		$scope.field_day = '';

		if(!new_value)
		{
			return;
		}

		var date_part_array = new_value.split('-');

		if(angular.isDefined(date_part_array[0]))
		{
			$scope.field_year = date_part_array[0];
		}

		if(angular.isDefined(date_part_array[1]))
		{
			$scope.field_month = date_part_array[1];
		}

		if(angular.isDefined(date_part_array[2]))
		{
			$scope.field_day = date_part_array[2];
		}
	};

	var put_date = function put_date(new_value, old_value, $scope)
	{
		if(!$scope.field_year && !$scope.field_month && !$scope.field_day)
		{
			$scope.model = null;
		}
		else
		{
			$scope.model = $scope.field_year + '-' +
				$scope.field_month + '-' +
				$scope.field_day;
		}
	};

	var template_function = function template_function(element, attributes) {
		return helper.resolve_template(attributes, 'src/common/directives/clinicaid/ca_field_date3');
	};

	var link_function = function link_function($scope, element, attribute, controller)
	{
		// Don't call the helper default link function, we want to do it differently
		// We do still need to merge the title messages though
		helper.merge_title_messages($scope);

		$scope.field_year = '';
		$scope.field_month = '';
		$scope.field_day = '';

		// wrap each of the focus functions the same way as the field_helper

		$scope.focus_fn_year = function focus_fn_year()
		{
			if(angular.isFunction($scope.on_focus_fn_year))
			{
				// only call the focus function if the field still has focus after a timeout
				$timeout(function() {
					if($(element).find(" input:enabled, select:enabled, textarea:enabled ").is(':focus'))
					{
						$scope.on_focus_fn_year();
					}
					else
					{
						console.log('ignoring on focus function - element is no longer in focus', $scope.name)
					}
				});
			}
		};

		$scope.focus_fn_month = function focus_fn_month()
		{
			if(angular.isFunction($scope.on_focus_fn_month))
			{
				// only call the focus function if the field still has focus after a timeout
				$timeout(function() {
					if($(element).find(" input:enabled, select:enabled, textarea:enabled ").is(':focus'))
					{
						$scope.on_focus_fn_month();
					}
					else
					{
						console.log('ignoring on focus function - element is no longer in focus', $scope.name)
					}
				});
			}
		};

		$scope.focus_fn_day = function focus_fn_day()
		{
			if(angular.isFunction($scope.on_focus_fn_day))
			{
				// only call the focus function if the field still has focus after a timeout
				$timeout(function() {
					if($(element).find(" input:enabled, select:enabled, textarea:enabled ").is(':focus'))
					{
						$scope.on_focus_fn_day();
					}
					else
					{
						console.log('ignoring on focus function - element is no longer in focus', $scope.name)
					}
				});
			}
		};

		$scope.$watch('focus_field',
			function update_focus(new_value, old_value, $scope)
		{
			if($scope.focus_field == $scope.name + "[year]")
			{
				// Focus the input element
				$(element).find(".ca-field-date3 input").get(0).focus().select();
			}
			else if($scope.focus_field == $scope.name + "[month]")
			{
				// Focus the input element
				$(element).find(".ca-field-date3 input").get(1).focus().select();
			}
			else if($scope.focus_field == $scope.name + "[day]")
			{
				// Focus the input element
				$(element).find(".ca-field-date3 input").get(2).focus().select();
			}
		});

		// listen for focusCaFocusField event: when it matches, focus the element
		$scope.$on('focusCaFocusField', function(e, focus)
		{
			if(Juno.Common.Util.exists($scope.name) && $scope.name == focus)
			{
				$(element).find(".ca-field-date3 input").get(0).focus().select();
			}
		});

		$scope.$watch('model', get_date);

		$scope.$watch('field_year', put_date);
		$scope.$watch('field_month', put_date);
		$scope.$watch('field_day', put_date);
	};

	return {
		restrict: 'E',
		scope: default_scope,
		templateUrl: template_function,
		replace: true,
		link: link_function,
		controller: helper.default_controller
	};

}]);

