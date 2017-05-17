angular.module('PatientList').controller('PatientList.PatientListProgramController', [

	'$scope',
	'$http',

	function(
		$scope,
		$http)
	{


		$scope.$on('updatePatientList', function(event, data)
		{
			console.log('updatePatientList=' + JSON.stringify(data));
			$scope.updateData(data.currentPage, data.pageSize);
		});


		//the currentPage is 0 based
		$scope.updateData = function(currentPage, pageSize)
		{
			var startIndex = currentPage * pageSize;

			$http(
			{
				url: '../ws/rs/program/patientList?startIndex=' + startIndex + '&numToReturn=' + pageSize,
				method: "GET",
				headers:
				{
					'Content-Type': 'application/json'
				}
			}).then(
				function(response)
				{
					$scope.admissions = response.data.content;
					$scope.$emit('updatePatientListPagination', response.data.total);
				},
				function error(error)
				{
					alert('Failed to get sets lists.');
				});
		};

		//initialize..
		$scope.updateData(0, $scope.pageSize);
		$scope.$emit('togglePatientListFilter', false);

	}
]);