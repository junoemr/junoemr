angular.module('Common.Components').component('onDemandBookingSettings',
	{
		templateUrl: 'src/common/modals/appointmentQueueModal/components/onDemandBookingSettings/onDemandBookingSettings.jsp',
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