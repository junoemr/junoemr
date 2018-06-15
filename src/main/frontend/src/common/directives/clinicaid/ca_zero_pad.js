angular.module('Common').directive(
	'zeroPad', 

	[
		'$filter',

	function(
		$filter
	)
{
    return {
		restrict: 'A',
		link: function (scope, element, attrs, ctrl) {
			ctrl.$formatters.unshift(function (data) {
				return $filter('numberFixedLength')(data, 4);
			});

			ctrl.$parsers.unshift(function (data) {
				return $filter('numberFixedLength')(data, 4);
			});            
		},
	};
}]);
