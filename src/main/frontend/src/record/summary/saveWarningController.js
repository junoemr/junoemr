angular.module('Record.Summary').controller('Record.Summary.SaveWarningController', [

	'$rootScope',
	'$scope',
	'$uibModal',
	'$uibModalInstance',
	'saveSignNote',
	'cancelNoteEdit',

	function(
		$rootScope,
		$scope,
		$uibModal,
		$uibModalInstance,
		saveSignNote,
		cancelNoteEdit)
	{

		var controller = this;

		// Cancel warning and maintain note changes
		controller.cancel = function cancel()
		{
			$uibModalInstance.dismiss('cancel');
		};

		// Save changes to the note
		controller.save = function save()
		{
			if (angular.isFunction(saveSignNote))
			{
				saveSignNote();
			}
			$uibModalInstance.close(true);
		};

		// Clear changes to the note
		controller.clearChanges = function clearChanges()
		{
			if (angular.isFunction(cancelNoteEdit))
			{
				cancelNoteEdit();
			}
			$uibModalInstance.close(true);
		};
	}
]);