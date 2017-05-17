angular.module('Tickler').controller('Tickler.TicklerNoteController', [

	'$scope',
	'$uibModalInstance',
	'$http',
	'ticklerNote',
	'tickler',
	'noteService',

	function(
		$scope,
		$uibModalInstance,
		$http,
		ticklerNote,
		tickler,
		noteService)
	{

		$scope.ticklerNote = ticklerNote;
		$scope.originalNote = ticklerNote.note;
		$scope.tickler = tickler;

		$scope.close = function()
		{
			$uibModalInstance.close("Someone Closed Me");
		};
		$scope.save = function()
		{
			var updatedNote = $scope.ticklerNote.note;
			$scope.ticklerNote.tickler = $scope.tickler;

			noteService.saveTicklerNote($scope.ticklerNote).then(function(data)
			{
				$uibModalInstance.close("Someone Saved Me");
			}, function(reason)
			{
				alert(reason);
			});
		};
	}
]);