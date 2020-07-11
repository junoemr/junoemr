<div class="appointment-queue" ng-class="$ctrl.getComponentClasses()">
	<div class="header">
		<juno-tab ng-model="$ctrl.currentQueue"
						  tabs="$ctrl.tabOptions"
						  component-style="$ctrl.componentStyle"
							type="JUNO_TAB_TYPE.SWIM_LANE">
		</juno-tab>
		
		<juno-divider class="width-100"
		              component-style="$ctrl.componentStyle"
		              slim="true">
		</juno-divider>
		
		<panel component-style="$ctrl.componentStyle" no-header="true">
			<panel-body class="juno-text">
				<i class="icon icon-plus"></i>
				<div class="text">
					Add to my schedule
				</div>
				<i class="icon icon-reply"></i>
			</panel-body>
		</panel>
	</div>
	
	<juno-divider class="width-100"
					      component-style="$ctrl.componentStyle"
					      slim="true">
	</juno-divider>
	
	<div class="list">
		<div class="list-content" ng-style="$ctrl.getListScrollHeightStyle()">
			<div ng-repeat="foobar in [].constructor($ctrl.maxQueueLength) track by $index" class="flex-fill-row">
				<appointment-card component-style="$ctrl.componentStyle" ng-model="$ctrl.currentQueue.items[$index]"></appointment-card>
			</div>
		</div>
		
		<div class="list-footer juno-text" ng-class="[$ctrl.componentStyle + '-background']">
			<panel component-style="$ctrl.componentStyle" no-header="true">
				<panel-body class="juno-text">
					{{$ctrl.currentQueue.items.length}}
				</panel-body>
			</panel>
			Appointments In Queue. Max Capacity
			<panel component-style="$ctrl.componentStyle" no-header="true">
				<panel-body class="juno-text">
					{{$ctrl.maxQueueLength}}
				</panel-body>
			</panel>
			Appointments
			<juno-button component-style="$ctrl.componentStyle"
			             button-color="JUNO_BUTTON_COLOR.INVISIBLE">
				<i class="icon icon-linux"></i>
			</juno-button>
		</div>
	</div>
</div>