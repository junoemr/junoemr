<div class="file-source-select flex-col h-100 w-100">

	<div class="body-extra-small-bold m-l-8 m-b-8">Patient Chart</div>

	<div class="flex-col flex-item-grow">
		<!-- Documents -->
		<div class="source flex-row align-items-center"
		     ng-class="$ctrl.sourceClasses(FileSource.DOCUMENTS)"
		     ng-click="$ctrl.selectSource(FileSource.DOCUMENTS)"
		     title="{{$ctrl.hideChartSources ? stringResources.getString('inbox.messageCompose.attachmentSelect.eChartNotAvailableTooltip') : ''}}">
			<span> Documents </span>
		</div>

		<!-- eForms -->
		<div class="source flex-row align-items-center"
		     ng-class="$ctrl.sourceClasses(FileSource.EFORMS)"
		     ng-click="$ctrl.selectSource(FileSource.EFORMS)"
		     title="{{$ctrl.hideChartSources ? stringResources.getString('inbox.messageCompose.attachmentSelect.eChartNotAvailableTooltip') : ''}}">
			<span> eForms </span>
		</div>

	</div>
</div>