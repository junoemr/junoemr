<div class="messageable-search">
	<juno-typeahead
			model="$ctrl.selectedMessageableId"
			title="Recipient"
			placeholder="Search"
			options="$ctrl.options"
			filter-options="false"
			typeahead-min-length="3"
			get-options-callback="$ctrl.loadSearchOptions(value)"
			on-change="$ctrl.checkMessageSelection(value)"
			on-selected="$ctrl.onMessageableSelected(value)"
			disabled="$ctrl.disabled"
			component-style="$ctrl.componentStyle">
	</juno-typeahead>
</div>