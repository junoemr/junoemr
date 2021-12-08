<div class="appointment-card" ng-class="$ctrl.getComponentClasses()">
	<div ng-if="$ctrl.ngModel" class="content juno-text">

		<div class="leading-icon m-r-8">
			<i class="icon" ng-class="$ctrl.getLeadingIcon()" title="{{$ctrl.getLeadingIconTooltip()}}"></i>
		</div>

		<div class="name-reason" title="{{$ctrl.ngModel.reason}}">
			<a ng-href="#!/record/{{$ctrl.ngModel.demographicNo}}/details"
			   title="Master File">{{$ctrl.ngModel.demographicName}}</a>
			<span>
				| {{$ctrl.ngModel.reason}}
			</span>
		</div>

		<!-- add icon -->
		<juno-button ng-click="$ctrl.onAddBtnClick()"
								 class="icon-button"
								 component-style="$ctrl.componentStyle"
								 button-color="JUNO_BUTTON_COLOR.PRIMARY"
								 button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
								 title="Schedule Queued Appointment">
			<i class="icon icon-add"></i>
		</juno-button>

		<!-- delete icon -->
		<juno-button ng-click="$ctrl.onDeleteBtnClick()"
		             class="icon-button"
		             component-style="$ctrl.componentStyle"
		             button-color="JUNO_BUTTON_COLOR.DANGER"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
								 title="Cancel Queued Appointment">
			<i class="icon icon-delete"></i>
		</juno-button>
		
	</div>
</div>