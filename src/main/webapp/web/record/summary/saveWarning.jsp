<div class="modal-content">

	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-label="Close" ng-click="saveWarningCtrl.cancel()">
			<span aria-hidden="true" >&times;</span>
		</button>
		<h3 class="modal-title">Warning</h3>
	</div>

	<div class="modal-body">
		<p>You have unsaved changes to a note! Do you want to save them?</p>
	</div>
		
	<div class="modal-footer">
		<button ng-click="saveWarningCtrl.cancel()" type="button" class="btn btn-default">Cancel</button>
		<button ng-click="saveWarningCtrl.clearChanges()" type="button" class="btn btn-danger">No</button>	
		<button ng-click="saveWarningCtrl.save()" type="button" class="btn btn-success">Yes</button>
	</div>
</div>
