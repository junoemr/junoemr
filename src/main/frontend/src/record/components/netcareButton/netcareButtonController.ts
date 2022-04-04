import NetcareService from "../../../lib/integration/netcare/service/NetcareService";
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN} from "../../../common/components/junoComponentConstants";

angular.module("Record.Components").component('netcareButton', {
	templateUrl: 'src/record/components/netcareButton/netcareButton.jsp',
	bindings: {
		demographic: "<",
	},
	controller: [
		"$scope",
		function (
			$scope)
		{
			const ctrl = this;

			$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

			ctrl.netcareService = null;

			ctrl.$onInit = () =>
			{
				ctrl.netcareService = new NetcareService();
			}

			ctrl.onOpenNetcare = (): void =>
			{
				ctrl.netcareService.submitLoginForm(ctrl.demographic.healthNumber);
			}

			ctrl.netcareDisabled = (): string =>
			{
				return Juno.Common.Util.isBlank(ctrl.demographic.healthNumber);
			}

			ctrl.netcareTitle = (): string =>
			{
				if(Juno.Common.Util.isBlank(ctrl.demographic.healthNumber))
				{
					return "Netcare requires patients to have a valid health number";
				}
				return "Open Netcare in a new tab";
			}
		}
	],
});