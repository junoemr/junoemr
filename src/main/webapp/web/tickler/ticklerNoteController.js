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

		var controller = this;

		controller.ticklerNote = ticklerNote;
		controller.originalNote = ticklerNote.note;
		controller.tickler = tickler;

		controller.close = function()
		{
			$uibModalInstance.close("Someone Closed Me");
		};
		controller.save = function()
		{
			var updatedNote = controller.ticklerNote.note;
			controller.ticklerNote.tickler = controller.tickler;

			noteService.saveTicklerNote(controller.ticklerNote).then(function(data)
			{
				$uibModalInstance.close("Someone Saved Me");
			}, function(reason)
			{
				alert(reason);
			});
		};
	}
]);