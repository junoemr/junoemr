angular.module('PatientSearch').controller('PatientSearch.RemotePatientResultsController', [

	'$http',
	'$scope',
	'$uibModalInstance',
	'results',
	'total',

	function(
		$http,
		$scope,
		$uibModalInstance,
		results,
		total)
	{
		$scope.results = results;
		$scope.total = total;

		$scope.currentPage = 1;
		$scope.pageSize = 5;
		$scope.startIndex = 0;


		$scope.close = function()
		{
			$uibModalInstance.close("Someone Closed Me");
		};

		$scope.doImport = function(d)
		{
			var myUrl = '../appointment/copyRemoteDemographic.jsp?remoteFacilityId=' + d.remoteFacilityId + '&demographic_no=' + d.demographicNo;
			window.open(myUrl, "ImportDemo", "width=700, height=1027");
		};

		$scope.save = function()
		{
			$uibModalInstance.close("Someone Saved Me");
		};

		$scope.prevPage = function()
		{
			if ($scope.startIndex == 0)
			{
				return;
			}
			$scope.currentPage--;
			$scope.startIndex = ($scope.currentPage - 1) * $scope.pageSize;

		};

		$scope.nextPage = function()
		{
			if ($scope.startIndex + $scope.pageSize > $scope.total)
			{
				return;
			}
			$scope.currentPage++;
			$scope.startIndex = ($scope.currentPage - 1) * $scope.pageSize;
		};


	}
]);