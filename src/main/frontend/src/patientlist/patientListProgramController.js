angular.module('PatientList').controller('PatientList.PatientListProgramController', [

	'$scope',
	'$http',

	function(
		$scope,
		$http)
	{

		var controller = this;


		$scope.$on('updatePatientList', function(event, data)
		{
			console.log('updatePatientList=' + JSON.stringify(data));
			controller.updateData(data.currentPage, data.pageSize);
		});


		//the currentPage is 0 based
		controller.updateData = function updateData(currentPage, pageSize)
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
				function success(results)
				{
					controller.admissions = results.data.content;
					$scope.$emit('updatePatientListPagination', results.data.total);
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		//initialize..
		controller.updateData(0, controller.pageSize);
		$scope.$emit('togglePatientListFilter', false);

	}
]);