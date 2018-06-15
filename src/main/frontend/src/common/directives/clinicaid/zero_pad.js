angular.module('Common').directive(
	'caZeroPad', 

	[
		'$filter',

	function(
		$filter
	)
{
    return {
		restrict: 'A',
		require: 'ngModel',
		link: function (scope, element, attrs, ctrl) {

			ctrl.$formatters.unshift(function (data) {
				var pad_length = attrs.caZeroPad;
				if(!pad_length)
				{
					pad_length = 0;
				}
				return $filter('numberFixedLength')(data, pad_length);
			});

			ctrl.$parsers.unshift(function (data) {
				var pad_length = attrs.caZeroPad;
				if(!pad_length)
				{
					pad_length = 0;
				}
				return $filter('numberFixedLength')(data, pad_length);
			});            
		},
	};
}]);
