<div class="appointment-queue" ng-class="$ctrl.getComponentClasses()">
	<div class="header">
		<div ng-class="$ctrl.getPrimaryBackgroundClass()">
			<juno-tab ng-model="$ctrl.currentQueue"
							  tabs="$ctrl.tabOptions"
							  component-style="$ctrl.componentStyle"
								type="JUNO_TAB_TYPE.SWIM_LANE">
			</juno-tab>
			
			<juno-divider class="width-100"
			              component-style="$ctrl.componentStyle"
			              slim="true">
			</juno-divider>
		</div>
		
		<panel component-style="$ctrl.componentStyle" no-header="true">
			<panel-body class="juno-text">
				<div class="text">
					Drag here to add to my schedule
				</div>
			</panel-body>
		</panel>
	</div>
	
	<juno-divider class="width-100"
					      component-style="$ctrl.componentStyle"
					      slim="true">
	</juno-divider>

	<div class="list">
		<div class="list-content" ng-style="$ctrl.getListScrollHeightStyle()">
			<juno-loading-indicator ng-if="!$ctrl.currentQueue"
															class="loading-indicator-container"
															message = "Loading"
															message-alignment="vertical"
															indicator-type="dot-pulse">
			</juno-loading-indicator>
			<div ng-if="$ctrl.currentQueue" ng-repeat="foobar in [].constructor($ctrl.currentQueue.queueLimit) track by $index" class="flex-fill-row">
				<appointment-card ng-model="$ctrl.currentQueue.items[$index]"
				                  on-delete="$ctrl.deleteQueueItem($index);"
													on-add="$ctrl.addToSchedule($index)"
				                  component-style="$ctrl.componentStyle">
				</appointment-card>
			</div>
		</div>
	</div>

	<div class="list-footer juno-text" ng-class="[$ctrl.componentStyle + '-background']">
		{{$ctrl.currentQueue.items.length}}
		<span class="middle-text juno-text-secondary">
				items in queue. max capacity
			</span>
		{{$ctrl.currentQueue.queueLimit}}
		<juno-button component-style="$ctrl.componentStyle"
								 button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARK"
								 button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT">
			<i class="icon icon-linux"></i>
		</juno-button>
	</div>
</div>