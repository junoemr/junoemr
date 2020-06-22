<juno-modal class="mha-patient-details-modal">

	<modal-ctl-buttons>
			<i class="icon icon-modal-ctl icon-close" ng-click="$ctrl.onCancel()"></i>
	</modal-ctl-buttons>

	<modal-title class="patient-title">
		<div class="header-start">
			<h3>MHA Profile</h3>
			<h5> for </h5>
			<h4>{{$ctrl.getCurrentPatientName()}}</h4>
		</div>
		<h5>{{$ctrl.getCurrentPatientHinAndProv()}}</h5>
	</modal-title>

	<modal-body class="patient-modal-body">
		<juno-tab ng-model="$ctrl.currentProfile"
						tabs="$ctrl.patientProfiles"
						component-style="$ctrl.resolve.style">
		</juno-tab>
	</modal-body>
</juno-modal>