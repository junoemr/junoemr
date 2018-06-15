
angular.module('Common').directive(
	'caFocusField',
	[
		function()
{

	var link_function = function($scope, element, attr, field_controllers)
	{
		var field = attr[ 'caName' ];

		// Forget the whole thing if there is already a controller that tells us to
		var ignore = false;
		for(var i = 0; i < field_controllers.length; i++)
		{
			if (field_controllers[ i ] &&
				field_controllers[ i ].ignore_focus_field())
			{
				ignore = true;
				break;
			}
		}

		if(angular.isDefined($scope.form_nav) && !ignore)
		{
			var focus_field = attr['caFocusField'];

			// watch the focus field: when it matches, focus the element
			$scope.$watch(focus_field, function()
			{
				if(Juno.Common.Util.exists(field) && field == $scope.form_nav.focus_field)
				{
					element[0].focus().select();
				}
			});

			// listen for focusCaFocusField event: when it matches, focus the element
			$scope.$on('focusCaFocusField', function(e, focus)
			{
				if(Juno.Common.Util.exists(field) && field == focus)
				{
					element[0].focus().select();
				}
			});
		}
	};

	return {
		restrict: 'A',
		replace: false,
		priority: 100,
		require: [
			'?^caFieldAlpha',
			'?^caFieldAlphadate',
			'?^caFieldAutocomplete',
			'?^caFieldBoolean',
			'?^caFieldColor',
			'?^caFieldCurrency',
			'?^caFieldDate',
			'?^caFieldDate3',
			'?^caFieldNumber',
			'?^caFieldSelect',
			'?^caFieldText',
			'?^caFieldTime'
		],
		link: link_function
	};

}]);

