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
				<span class="body-normal">
					This is a new feature which can be used to manage a queue of patients (eg. for walk in clinics). Please contact
					<a href="mailto: {{$ctrl.supportEmail}}">{{$ctrl.supportEmail}}</a>
					for more information.
				</span>
			</h2>
			<ul dnd-list="$ctrl.currentQueue.items"
					dnd-drop="$ctrl.onDragDrop(index, item, event)">
				<li ng-if="$ctrl.currentQueue"
						ng-repeat="foobar in [].constructor($ctrl.currentQueue.queueLimit) track by $index"
						class="flex-fill-row"
						dnd-draggable="$ctrl.currentQueue.items[$index]"
						dnd-effect-allowed="move"
						dnd-moved="$ctrl.onDragMoved(event, $index)"
						dnd-dragstart="$ctrl.onDragStart(event)">

					<appointment-card ng-model="$ctrl.currentQueue.items[$index]"
														on-delete="$ctrl.deleteQueueItem($index);"
														on-add="$ctrl.addToSchedule($index)"
														component-style="$ctrl.componentStyle">
					</appointment-card>
				</li>
			</ul>
		</div>
	</div>

	<juno-round-button
			ng-if="$ctrl.currentQueue"
			class="add-button"
			component-style="$ctrl.compoentStyle"
			button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL"
			button-color="JUNO_BUTTON_COLOR.PRIMARY"
			title="add to queue"
			ng-click="$ctrl.openBookQueuedAppointmentModal()"
	>
		<i class="icon icon-plus"></i>
	</juno-round-button>

	<div ng-if="$ctrl.currentQueue" class="list-footer juno-text" ng-class="[$ctrl.componentStyle + '-background']">
		{{$ctrl.currentQueue.items.length}}
		<span class="middle-text juno-text-secondary">
			items in queue. max capacity
		</span>
		{{$ctrl.currentQueue.queueLimit}}
	</div>
</div>