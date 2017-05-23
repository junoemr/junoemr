<a class="patient-selector-option">
	<div ng-if="match.model.moreResults">
		<div class="name">More Results</div>
		<div class="more-total">{{ match.model.total }} total</div>
	</div>
	<div ng-if="!match.model.moreResults">
		<div class="name">
			<span>{{ match.model.lastName }}</span>,
			<span>{{ match.model.firstName }}</span>
		</div>
		<div ng-if="match.model.hin"
				 class="hin">
			{{ match.model.hin }}
		</div>
		<div ng-if="match.model.dob"
				 class="dob">
			{{ match.model.formattedDOB }}
		</div>
	</div>
</a>
