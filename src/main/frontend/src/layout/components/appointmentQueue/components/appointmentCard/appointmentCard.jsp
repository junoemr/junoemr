<div class="appointment-card" ng-class="$ctrl.getComponentClasses()">
	<div ng-if="$ctrl.ngModel" class="content juno-text">
		
		<div class="name-reason" title="{{$ctrl.ngModel.reason}}">
			<a ng-href="#!/record/{{$ctrl.ngModel.integrationPatientId}}/details"
			   title="Master File">{{$ctrl.ngModel.demographicName}}</a>
			<!-- when I move the above code to a new line, it underlines the space between the patient name and the "|"  -->
			<span>
				| {{$ctrl.ngModel.reason}}
			</span>
		</div>
		
		<!-- delete icon -->
		<juno-button ng-click="$ctrl.onDeleteBtnClick()"
		             class="icon-button"
		             component-style="$ctrl.componentStyle"
		             button-color="JUNO_BUTTON_COLOR.INVISIBLE">
			<i class="icon icon-delete"></i>
		</juno-button>
		
	</div>
</div>