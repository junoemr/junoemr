angular.module('Admin').controller('Admin.AdminController', [
	'$scope',
	'$http',
	'$location',
	'personaService',
	'$stateParams',
	function ($scope, $http, $location, personaService, $stateParams)
	{
		let controller = this;
		controller.navList = [];

		controller.onSideNaveClick = function(group, item)
		{

		};

		function generateTransition(newState)
		{
			return function()
			{
				angular.element(document.querySelector("html")).animate({scrollTop: 0}, 500);
				$location.url("/admin/"+newState);
			}
		}

		// translate transitionState property of results in to transition function.
		function processNavResults(results)
		{
			results.forEach(function (group) {
				group.items.forEach(function (item) {
					item.callback = generateTransition(item.transitionState);

					// restore accordion state on reload
					if ($stateParams.frameUrl !== undefined &&
						(item.transitionState.includes($stateParams.frameUrl) || item.transitionState.includes(encodeURIComponent($stateParams.frameUrl))))
					{
						group.expanded = true;
					}
				})
			});

			return results;
		}

		function loadNavItems()
		{
			personaService.getAdminNav().then(
				function success(result)
				{
					controller.navList = processNavResults(result);
				},
				function error(result)
				{
					console.error("failed to load admin nav bar, with error: " + result);
				}
			)
		}
		loadNavItems();
	}
]);
