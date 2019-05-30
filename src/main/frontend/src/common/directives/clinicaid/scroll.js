angular.module('Common').directive('scroll',
	[
		'$window',
		function($window)
		{
			// this directive broadcasts scroll::scroll on window scroll
			// use the scroll-handler directive to listen for and handle the event

			return {
				link: function(scope) {
					angular.element($window).on('scroll', function(e)
					{
						// use name of directive + event to avoid collisions
						scope.$broadcast('scroll::scroll');
					});
				}
			}
		}
	]
);
