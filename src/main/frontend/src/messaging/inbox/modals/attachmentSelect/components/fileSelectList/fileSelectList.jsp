<div class="file-select-list w-100 h-100 overflow-hidden">
	<div class="overflow-y-scroll flex-col w-100 h-100">
		<div ng-repeat="file in $ctrl.fileOptions | orderBy:'updatedAt':false:$ctrl.dateCompare"
		     ng-click="$ctrl.onFileSelect(file)"
		     class="file-row flex-item-no-shrink flex-row align-items-center">
			<!-- File Name -->
			<div class="m-l-32 flex-item-grow" title="{{file.name}}">
				<div class="text-ellipsis w-max-256">{{file.name}}</div>
			</div>

			<!-- Date -->
			<div class="file-date text-center">
				{{$ctrl.formatFileDate(file.updatedAt)}}
			</div>

			<!-- File type -->
			<div class="file-type text-center text-ellipsis" title="{{$ctrl.fileTypeHuman(file.type)}}">
				{{$ctrl.fileTypeHuman(file.type)}}
			</div>

			<!-- Loading indicator -->
			<div ng-if="$ctrl.fileCurrentlyBeingAdded === file"
			     class="flex-col justify-content-center h-100 h-100">
				<juno-loading-indicator indicator-type="spinner" title="Attaching...">
				</juno-loading-indicator>
			</div>
			<!-- Checkbox -->
			<div ng-if="$ctrl.fileCurrentlyBeingAdded !== file"
			     class="file-check flex-row justify-content-center align-items-center">
				<juno-check-box ng-model="file.selected"
				                dummy="true"
				                button-color="JUNO_BUTTON_COLOR.PRIMARY">
				</juno-check-box>
			</div>
		</div>
	</div>
</div>