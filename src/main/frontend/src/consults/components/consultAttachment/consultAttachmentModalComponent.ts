
angular.module('Consults').component('consultAttachmentModalComponent',
	{
		templateUrl: 'src/consults/components/consultAttachment/consultAttachment.jsp',
		bindings: {
			modalInstance: "<",
			resolve: "<",
		},
		controller: [
			'consultService',

			function(consultService)
			{
				const ctrl = this;
				ctrl.consult = null;

				ctrl.atth = {};


				var consultId = 0;

				ctrl.$onInit = () =>
				{
					ctrl.consult = ctrl.resolve.consult;
					ctrl.atth.patientName = ctrl.resolve.demographic.displayName;

					ctrl.atth.attachedDocs = ctrl.consult.attachments;
					if (ctrl.atth.attachedDocs[0] != null) ctrl.atth.selectedAttachedDoc = ctrl.atth.attachedDocs[0];

					if (ctrl.consult.id != null) consultId = ctrl.consult.id;
					consultService.getRequestAttachments(consultId, ctrl.consult.demographicId).then(
						function success(results)
						{
							if (ctrl.consult.availableDocs == null) ctrl.consult.availableDocs = Juno.Common.Util.toArray(results);
							ctrl.atth.availableDocs = ctrl.consult.availableDocs;
							if (ctrl.atth.availableDocs[0] != null) ctrl.atth.selectedAvailableDoc = ctrl.atth.availableDocs[0];
						},
						function error(errors)
						{
							console.log(errors);
						});
				}

				ctrl.openDoc = function openDoc(doc)
				{
					window.open("../" + doc.url);
				};

				ctrl.attach = function attach()
				{
					if (ctrl.atth.selectedAvailableDoc == null) return;

					ctrl.atth.attachedDocs.push(ctrl.atth.selectedAvailableDoc);
					ctrl.atth.selectedAttachedDoc = ctrl.atth.selectedAvailableDoc;
					ctrl.atth.selectedAttachedDoc.attached = true;

					//Get index to remove by document id and document type
					var removeIndex = ctrl.atth.availableDocs.map(function(item)
					{
						return item.documentNo+item.documentType;
					}).indexOf(ctrl.atth.selectedAvailableDoc.documentNo+ctrl.atth.selectedAvailableDoc.documentType);
					ctrl.atth.availableDocs.splice(removeIndex, 1);

					//@ts-ignore
					var selectionIndex = $("#selAvailDoc")[0].selectedIndex;
					if (selectionIndex >= ctrl.atth.availableDocs.length) selectionIndex = ctrl.atth.availableDocs.length - 1;
					ctrl.atth.selectedAvailableDoc = ctrl.atth.availableDocs[selectionIndex];

					ctrl.consult.attachmentsChanged = true;
				};

				ctrl.detach = function detach()
				{
					if (ctrl.atth.selectedAttachedDoc == null) return;

					ctrl.atth.availableDocs.push(ctrl.atth.selectedAttachedDoc);
					ctrl.atth.selectedAvailableDoc = ctrl.atth.selectedAttachedDoc;
					ctrl.atth.selectedAvailableDoc.attached = false;

					//Get index to remove by document id and document type
					var removeIndex = ctrl.atth.attachedDocs.map(function(item)
					{
						return item.documentNo+item.documentType;
					}).indexOf(ctrl.atth.selectedAttachedDoc.documentNo+ctrl.atth.selectedAttachedDoc.documentType);
					ctrl.atth.attachedDocs.splice(removeIndex, 1);

					//@ts-ignore
					var selectionIndex = $("#selAttachDoc")[0].selectedIndex;
					if (selectionIndex >= ctrl.atth.attachedDocs.length) selectionIndex = ctrl.atth.attachedDocs.length - 1;
					ctrl.atth.selectedAttachedDoc = ctrl.atth.attachedDocs[selectionIndex];

					ctrl.consult.attachmentsChanged = true;
				};

				ctrl.done = function done()
				{
					ctrl.modalInstance.close();
				};
			}
		]
});