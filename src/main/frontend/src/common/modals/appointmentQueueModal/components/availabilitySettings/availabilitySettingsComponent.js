angular.module('Common.Components').component('availabilitySettings',
	{
		templateUrl: 'src/common/modals/appointmentQueueModal/components/availabilitySettings/availabilitySettings.jsp',
		bindings: {
			settingsModel: "=",
			componentStyle: "<?",
			disabled: "<?",
		},
		controller: [
			'$scope',
			function ($scope)
		{
			let ctrl = this;

			ctrl.availableDaysList = [];

			ctrl.$onInit = () =>
			{
				ctrl.buildDayAvailabilityList();
				$scope.$watch("$ctrl.settingsModel", (newValue, oldValue) =>
				{
					// if the model changes, regenerate the availability list
					if (newValue !== oldValue)
					{
						ctrl.buildDayAvailabilityList();
					}
				});
			}

			ctrl.onStartDateChange = (moment, day) =>
			{
				day.startTimeMoment = moment;
				ctrl.updateDaySettings(day.model, day);
			}

			ctrl.onEndDateChange = (moment, day) =>
			{
				day.endTimeMoment = moment;
				ctrl.updateDaySettings(day.model, day);
			}

			ctrl.onEnabledChange = (value, day) =>
			{
				day.enabled = value;
				ctrl.updateDaySettings(day.model, day);
			}

			ctrl.getDayLabel = (weekday) =>
			{
				return Juno.Common.Util.ISODayString(weekday);
			}

			ctrl.buildDayAvailabilityList = () =>
			{
				ctrl.availableDaysList = [
					ctrl.buildDayAvailability(ctrl.settingsModel.sunday),
					ctrl.buildDayAvailability(ctrl.settingsModel.monday),
					ctrl.buildDayAvailability(ctrl.settingsModel.tuesday),
					ctrl.buildDayAvailability(ctrl.settingsModel.wednesday),
					ctrl.buildDayAvailability(ctrl.settingsModel.thursday),
					ctrl.buildDayAvailability(ctrl.settingsModel.friday),
					ctrl.buildDayAvailability(ctrl.settingsModel.saturday),
				];
			}

			ctrl.buildDayAvailability = (model) =>
			{
				return {
					enabled: model.enabled,
					weekdayNumber: model.weekdayNumber,
					startTimeMoment: moment(model.startTime, Juno.Common.Util.settings.defaultTimeFormat),
					endTimeMoment: moment(model.endTime, Juno.Common.Util.settings.defaultTimeFormat),
					model: model,
				};
			}

			ctrl.updateDaySettings = (transfer, localModel) =>
			{
				transfer.enabled = localModel.enabled;
				transfer.weekdayNumber = localModel.weekdayNumber;
				transfer.startTime = localModel.startTimeMoment.format(Juno.Common.Util.settings.defaultTimeFormat);
				transfer.endTime = localModel.endTimeMoment.format(Juno.Common.Util.settings.defaultTimeFormat);
			}
		}],
	}
);