<div class="file-select-list w-100 h-100 overflow-hidden">
	<div class="overflow-y-auto flex-col w-100 h-100">
		<div ng-repeat="file in $ctrl.fileOptions | orderBy:'updatedAt':false:$ctrl.dateCompare"
		     ng-click="$ctrl.onFileSelect(file)"
		     class="file-row flex-item-no-shrink flex-row align-items-center">
			<!-- File Name -->
			<div class="m-l-32 flex-item-grow text-ellipsis" title="{{file.name}}">
				{{file.name}}
			</div>

			<!-- Date -->
			<div class="file-date text-center">
				{{$ctrl.formatFileDate(file.updatedAt)}}
			</div>

			<!-- File type -->
			<div class="file-type text-center text-ellipsis" title="{{$ctrl.fileTypeHuman(file.type)}}">
				{{$ctrl.fileTypeHuman(file.type)}}
			</div>
		</div>
	</div>
</div>