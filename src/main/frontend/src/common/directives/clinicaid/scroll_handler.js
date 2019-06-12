angular.module('Common').directive(
	'scrollHandler', [
	'$parse',
	function($parse)
	{
		// the scroll directive broadcasts scroll::scroll on window scroll
		// use this directive to listen and handle the scroll event
		return {
			restrict: 'A',
			link: function (scope, element, attrs) {
				scope.$on('scroll::scroll', function () {
					$parse(attrs.scrollHandler)(scope);
				});
			}
		}
	}]
);

