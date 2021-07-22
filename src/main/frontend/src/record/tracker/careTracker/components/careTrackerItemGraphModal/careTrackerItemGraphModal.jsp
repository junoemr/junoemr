<script src="node_modules/chart.js/Chart.min.js"></script>
<script src="node_modules/angular-chart.js/dist/angular-chart.min.js"></script>

<juno-simple-modal modal-instance="$ctrl.modalInstance" modal-width="1024" modal-height="800">
	<div class="care-tracker-item-graph-modal flex-column width-100 height-100">
		<span>
			TEST TEST TEST
		</span>
		<div class="canvas-wrapper flex-row flex-grow align-items-end">
			<canvas id="line"
			        class="chart chart-line"
			        chart-data="$ctrl.data"
			        chart-labels="$ctrl.labels"
			        chart-series="$ctrl.series"
			        chart-options="$ctrl.options"
			        chart-dataset-override="$ctrl.datasetOverride"
			        chart-click="$ctrl.onClick">
			</canvas>
		</div>
	</div>
</juno-simple-modal>
