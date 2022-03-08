<page-wrapper show-header="false">
	<page-body>
		<juno-tab ng-model="$ctrl.currentTab"
		          tabs="$ctrl.tabList"
		          change="$ctrl.tabChange(activeTab)"
		          component-style="$ctrl.componentStyle">
		</juno-tab>
		<ui-view></ui-view>
	</page-body>
</page-wrapper>