<div class="appointment-card" ng-class="$ctrl.getComponentClasses()">
	<div ng-if="$ctrl.ngModel" class="content juno-text">
		
		<div class="name-reason" title="{{$ctrl.ngModel.reason}}">
			<a ng-href="#!/record/{{$ctrl.ngModel.demographicNo}}/details"
			   title="Master File">{{$ctrl.ngModel.patientName}}</a>
			<!-- when I move the above code to a new line, it underlines the space between the patient name and the "|"  -->
			<span>
				| {{$ctrl.ngModel.reason}}
			</span>
		</div>
		
		<!-- delete icon -->
		<juno-button ng-click="$ctrl.onDeleteBtnClick()"
		             class="icon-button"
		             component-style="$ctrl.componentStyle"
		             button-color="JUNO_BUTTON_COLOR.PRIMARY"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT">
			<i class="icon icon-delete"></i>
		</juno-button>
		
	</div>
</div>