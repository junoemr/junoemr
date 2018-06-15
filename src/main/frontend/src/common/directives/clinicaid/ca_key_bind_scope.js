angular.module('Common').directive(
		'caKeyBindScope',[

			'$rootScope',

			function($rootScope)
			{

				var scope = {
					identifier: '@caIdentifier',
					key_combo: '@caKeyCombo',
					callback_fn: '=caCallbackFn',
					key_bind_settings: '=caKeyBindSettings',
				};

				var build_hierarchy = function build_hierarchy(element)
				{
					var parents = element.parents("ca-key-bind-scope");

					var hierarchy = [];
					$.each( parents, function( index, value )
					{
						try
						{
							hierarchy.unshift(value.attributes['ca-identifier'].nodeValue);
						} catch(error)
						{
							console.log("This ca-key-bind-scope element has an invalid parent: ");
							console.log(element);
						}
					});
					return hierarchy
				};

				var link_function = function link_function($scope, element, attribute, controller)
				{
					// identifier is required
					if($scope.identifier == null)
					{
						console.log("The following ca_key_bind_scope element is missing the required " +
								"ca-identifier attribute");
						console.log(element);
						return;
					}

					var hierarchy = build_hierarchy(element);

					if($scope.key_bind_settings != null)
					{
						for (var key_combo in $scope.key_bind_settings)
						{
							$rootScope.ca_key_binding.bind_key_element(
									$scope, element, $scope.identifier, angular.copy(hierarchy),
									key_combo,
									$scope.key_bind_settings[key_combo]);
						}
					}
					else
					{
						console.log("No key bind settings set for ca-key-bind-scope directive (" +
								$scope.identifier + ")");
					}
				};

				return {
					scope: scope,
					restrict: 'EA',
					link: link_function
				}
			}]
);

