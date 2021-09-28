<page-wrapper show-header="false">
	<page-body>
		<h6>Lab Configuration</h6>
		<div>
			<juno-tab ng-model="$ctrl.currentTab"
			          tabs="$ctrl.tabList"
			          change="$ctrl.tabChange(activeTab)"
			          component-style="$ctrl.componentStyle">
			</juno-tab>
			<ui-view></ui-view>
		</div>
	</page-body>
</page-wrapper>