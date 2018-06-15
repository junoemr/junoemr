angular.module('Common').directive(
	'caPagination',

	[ '$timeout',

	function($timeout)
{

	var scope = {
		pagination: '=caModel',
		change_page_callback: '=caChangePage'
	};

	var link_function = function link_function($scope, element, attribute, controller)
	{
		$scope.mode = 'pages';

		$scope.change_page = function change_page(page_number)
		{
			if(Juno.Common.Util.exists(page_number) &&
				page_number != $scope.pagination.current_page &&
				page_number > 0 &&
				page_number <= $scope.pagination.total_pages)
			{
				$scope.change_page_callback(page_number);
			}
		};

		$scope.expand_ellipsis = function expand_ellipsis()
		{
			$scope.go_to_page_num = $scope.pagination.current_page;
			$scope.mode = 'goto';
			$timeout(function()
			{
				element.find("input").focus().select();
			});
		};

		$scope.close_ellipsis = function close_ellipsis()
		{
			$scope.mode = 'pages';
		};

		$scope.go_to_page = function go_to_page()
		{
			if(Juno.Common.Util.isIntegerString($scope.go_to_page_num))
			{
				var page_int = parseInt($scope.go_to_page_num);
				if(page_int <= 0)
				{
					page_int = 1;
				}
				else if (page_int > $scope.pagination.total_pages)
				{
					page_int = $scope.pagination.total_pages;
				}
				$scope.change_page(page_int);

				$scope.close_ellipsis();
			}
		}
	};

	return {
		restrict: 'E',
		scope: scope,
		templateUrl: 'src/common/directives/clinicaid/ca_pagination.jsp',
		replace: true,
		link: link_function
	};

}]);

