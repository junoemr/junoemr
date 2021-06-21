<div class="inbox-select flex-col h-100 w-100 overflow-y-auto">

	<div ng-repeat="group in $ctrl.groups" class="message-group-container p-t-32 p-b-32">
		<juno-collapse-area label="$ctrl.groupNameToHuman(group)"
		                    collapsed="false"
		                    component-style="$ctrl.componentStyle"
		>
			<div class="source-item"
			     ng-repeat="source in $ctrl.sources"
			     ng-if="source.name"
			     ng-class="$ctrl.getSourceItemClass(source.id, group)"
			     ng-click="$ctrl.onSourceClick(source.id, group)"
			>
				<div class="text-ellipsis" title="{{source.name}}">{{source.name}}</div>
			</div>
		</juno-collapse-area>
	</div>
</div>