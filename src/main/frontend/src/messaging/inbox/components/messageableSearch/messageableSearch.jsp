<div class="messageable-search">
	<juno-typeahead
			model="$ctrl.selectedMessageableId"
			title="{{$ctrl.label}}"
			placeholder="{{$ctrl.placeholder}}"
			icon="{{$ctrl.icon}}"
			options="$ctrl.options"
			filter-options="false"
			typeahead-min-length="3"
			get-options-callback="$ctrl.loadSearchOptions(value)"
			on-change="$ctrl.checkMessageableSelection(value)"
			on-selected="$ctrl.onMessageableSelected(value)"
			disabled="$ctrl.disabled"
			component-style="$ctrl.componentStyle">
	</juno-typeahead>

	<div ng-if="$ctrl.showHighConfidenceCheckmark"
	     title="{{$ctrl.patientConfidenceMessage}}"
	     class="high-confidence-checkmark">
		<i class="icon icon-check"></i>
	</div>
</div>