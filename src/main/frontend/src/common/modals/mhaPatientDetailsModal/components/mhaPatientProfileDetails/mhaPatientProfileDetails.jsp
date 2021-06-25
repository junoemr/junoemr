<div class="mha-patient-profile-details flex-col w-100 h-100 p-8">
	<div class="flex-item-grow m-l-24 m-t-16">
		<div ng-if="$ctrl.profile && !$ctrl.profile.isRejected"
		     class="flex-col flex-gap-16">
			<!-- Name, Birthdate, Sex -->
			<div>
				<span class="body-normal">{{$ctrl.profile.displayName}}</span>
				<span class="m-l-8">{{$ctrl.formatBirthdate($ctrl.profile)}}</span>
				<span>({{$ctrl.profile.age}})</span>
				<span class="m-l-16">{{$ctrl.formatSex($ctrl.profile)}}</span>
			</div>

			<!-- Email -->
			<div>
				{{$ctrl.profile.email}}
			</div>

			<!-- Phone -->
			<div>
				{{$ctrl.formatPhoneNumber($ctrl.profile)}}
			</div>

			<!-- Address -->
			<div>
				{{$ctrl.profile.fullAddress}}
			</div>

			<!-- Postal Code -->
			<div>
				{{$ctrl.profile.postalCode}}
			</div>
		</div>

		<!-- Patient Rejected -->
		<div ng-if="$ctrl.profile && $ctrl.profile.isRejected"
		     class="flex-col justify-content-center">
			<h6 class="patient-rejected-text m-t-0">Patient Rejected.</h6>
		</div>
	</div>
</div>