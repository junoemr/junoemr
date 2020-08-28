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
	</div>

	<div class="list">
		<div class="list-content" ng-style="$ctrl.getListScrollHeightStyle()">
			<juno-loading-indicator ng-if="!$ctrl.currentQueue && !$ctrl.noQueues"
															class="loading-indicator-container"
															message = "Loading"
															message-alignment="vertical"
															indicator-type="dot-pulse">
			</juno-loading-indicator>
			<h2 class="no-queues-zero-state" ng-if="$ctrl.noQueues">
				Appointment Queue feature is not available at this time.
			</h2>
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
	</div>
</div>