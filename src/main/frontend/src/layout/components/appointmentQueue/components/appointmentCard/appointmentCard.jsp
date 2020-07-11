<div class="appointment-card" ng-class="$ctrl.getComponentClasses()">
	<div ng-if="$ctrl.ngModel" class="content juno-text">
		
		<!-- Echart -->
		<juno-button component-style="$ctrl.componentStyle" button-color="JUNO_BUTTON_COLOR.SECONDARY">
			E
		</juno-button>
		
		<!-- Billing -->
		<juno-button component-style="$ctrl.componentStyle" button-color="JUNO_BUTTON_COLOR.SECONDARY">
			B
		</juno-button>
		
		<!-- Master file -->
		<juno-button component-style="$ctrl.componentStyle" button-color="JUNO_BUTTON_COLOR.SECONDARY">
			M
		</juno-button>
		
		<!-- Prescriptions -->
		<juno-button component-style="$ctrl.componentStyle" button-color="JUNO_BUTTON_COLOR.SECONDARY">
			Rx
		</juno-button>
		
		<!-- telehealth icon -->
		<div class="telehealth-icon">
			<i ng-if="$ctrl.ngModel.isTelehealth"
				 class="icon icon-video"
				 title="This is a telehealth (video) appointment">
			</i>
		</div>
		
		<div class="name-reason" title="{{$ctrl.ngModel.reason}}">
			{{$ctrl.ngModel.patientName}} | {{$ctrl.ngModel.reason}}
		</div>
		
		<!-- view icon -->
		<juno-button class="icon-button" component-style="$ctrl.componentStyle" button-color="JUNO_BUTTON_COLOR.INVISIBLE">
			<i class="icon icon-view"></i>
		</juno-button>
		
		<!-- delete icon -->
		<juno-button class="icon-button" component-style="$ctrl.componentStyle" button-color="JUNO_BUTTON_COLOR.INVISIBLE">
			<i class="icon icon-delete"></i>
		</juno-button>
		
	</div>
</div>