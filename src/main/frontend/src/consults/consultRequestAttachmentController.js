angular.module('Consults').controller('Consults.ConsultRequestAttachmentController', [

	'$uibModalInstance',
	'consultService',
	'consult',

	function(
		$uibModalInstance,
		consultService,
		consult)
	{

		var controller = this;

		controller.atth = {};
		controller.atth.patientName = consult.demographic.lastName + ", " + consult.demographic.firstName;
		controller.atth.attachedDocs = consult.attachments;
		if (controller.atth.attachedDocs[0] != null) controller.atth.selectedAttachedDoc = controller.atth.attachedDocs[0];

		var consultId = 0;

		if (consult.id != null) consultId = consult.id;
		consultService.getRequestAttachments(consultId, consult.demographicId).then(
			function success(results)
			{
				if (consult.availableDocs == null) consult.availableDocs = Juno.Common.Util.toArray(results);
				controller.atth.availableDocs = consult.availableDocs;
				if (controller.atth.availableDocs[0] != null) controller.atth.selectedAvailableDoc = controller.atth.availableDocs[0];
			},
			function error(errors)
			{
				console.log(errors);
			});

		controller.openDoc = function openDoc(doc)
		{
			window.open("../" + doc.url);
		};

		controller.attach = function attach()
		{
			if (controller.atth.selectedAvailableDoc == null) return;

			controller.atth.attachedDocs.push(controller.atth.selectedAvailableDoc);
			controller.atth.selectedAttachedDoc = controller.atth.selectedAvailableDoc;
			controller.atth.selectedAttachedDoc.attached = true;

			//Get index to remove by document id and document type
			var removeIndex = controller.atth.availableDocs.map(function(item)
			{
				return item.documentNo+item.documentType;
			}).indexOf(controller.atth.selectedAvailableDoc.documentNo+controller.atth.selectedAvailableDoc.documentType);
			controller.atth.availableDocs.splice(removeIndex, 1);

			var selectionIndex = $("#selAvailDoc")[0].selectedIndex;
			if (selectionIndex >= controller.atth.availableDocs.length) selectionIndex = controller.atth.availableDocs.length - 1;
			controller.atth.selectedAvailableDoc = controller.atth.availableDocs[selectionIndex];

			consult.attachmentsChanged = true;
		};

		controller.detach = function detach()
		{
			if (controller.atth.selectedAttachedDoc == null) return;

			controller.atth.availableDocs.push(controller.atth.selectedAttachedDoc);
			controller.atth.selectedAvailableDoc = controller.atth.selectedAttachedDoc;
			controller.atth.selectedAvailableDoc.attached = false;

			//Get index to remove by document id and document type
			var removeIndex = controller.atth.attachedDocs.map(function(item)
			{
				return item.documentNo+item.documentType;
			}).indexOf(controller.atth.selectedAttachedDoc.documentNo+controller.atth.selectedAttachedDoc.documentType);
			controller.atth.attachedDocs.splice(removeIndex, 1);

			var selectionIndex = $("#selAttachDoc")[0].selectedIndex;
			if (selectionIndex >= controller.atth.attachedDocs.length) selectionIndex = controller.atth.attachedDocs.length - 1;
			controller.atth.selectedAttachedDoc = controller.atth.attachedDocs[selectionIndex];

			consult.attachmentsChanged = true;
		};

		controller.done = function done()
		{
			$uibModalInstance.close();
		};
	}
]);