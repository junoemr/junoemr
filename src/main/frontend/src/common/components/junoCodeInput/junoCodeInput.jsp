<div class="juno-code-input">
	<input ng-style="$ctrl.inputStyles()" type="text" maxlength="{{$ctrl.codeLength}}" />

	<div class="hole-punch-overlay">
		<div ng-repeat="i in [].constructor($ctrl.codeLength) track by $index" class="punch-out">
			<div class="inner-punch">
				<div class="inner-border"></div>
			</div>
		</div>
	</div>
</div>