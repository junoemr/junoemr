angular.module('Common').directive('resize',
	[
		'$window',
		function($window)
		{
			// this directive broadcasts resize::resize on window resize
			// use the resize-handler directive to listen for and handle the event

			return {
				link: function(scope) {
					angular.element($window).on('resize', function(e)
					{
						// use name of directive + event to avoid collisions
						scope.$broadcast('resize::resize');
					});
				}
			}
		}
	]
);
