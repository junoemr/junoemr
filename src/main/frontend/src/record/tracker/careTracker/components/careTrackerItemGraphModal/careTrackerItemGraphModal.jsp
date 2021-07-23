<script src="node_modules/chart.js/Chart.min.js"></script>
<script src="node_modules/angular-chart.js/dist/angular-chart.min.js"></script>

<juno-simple-modal modal-instance="$ctrl.modalInstance" modal-width="1024" modal-height="632">
	<div class="care-tracker-item-graph-modal flex-column width-100 height-100">
		<div class="flex-column align-items-center justify-content-center">
			<h6>{{$ctrl.model.name}} ({{$ctrl.model.typeCode}})</h6>
			<div class="item-description">{{$ctrl.model.description}}</div>
		</div>
		<div class="flex-row flex-grow">
			<div class="y-axis-label flex-column justify-content-center align-items-center">
				<span>Value</span>
			</div>
			<div class="canvas-wrapper flex-row flex-grow align-items-end">
				<canvas ng-if="!$ctrl.isLoading"
				        id="line"
				        class="chart chart-line"
				        chart-data="$ctrl.data"
				        chart-labels="$ctrl.labels"
				        chart-series="$ctrl.series"
				        chart-options="$ctrl.options"
				        chart-colors="$ctrl.colours"
				        chart-dataset-override="$ctrl.datasetOverride">
				</canvas>
			</div>
		</div>
		<div class="x-axis-label flex-row justify-content-center align-items-center">
			<span>Date & Time</span>
		</div>
	</div>
</juno-simple-modal>
