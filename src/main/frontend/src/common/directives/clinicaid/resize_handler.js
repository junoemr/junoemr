angular.module('Common').directive(
	'resizeHandler',[
	'$parse',
	function($parse)
	{
		// the resize directive broadcasts resize::resize on window resize
		// use this directive to listen and handle the resize event
		return {
			restrict: 'A',
			link: function (scope, element, attrs) {
				scope.$on('resize::resize', function () {
					$parse(attrs.resizeHandler)(scope);
				});
			}
		}
	}]
);

