angular.module('Common.Components').component('availabilitySettings',
	{
		templateUrl: 'src/common/modals/appointmentQueueModal/components/availabilitySettings/availabilitySettings.jsp',
		bindings: {
			settingsModel: "=",
			componentStyle: "<?",
			disabled: "<?",
		},
		controller: [function ()
		{
			let ctrl = this;
		}],
	}
);