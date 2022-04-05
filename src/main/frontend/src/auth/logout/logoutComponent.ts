import {netcareService} from "../../lib/integration/netcare/service/NetcareService";

angular.module("Auth").component('logout', {
	templateUrl: 'src/auth/logout/logout.jsp',
	bindings: {
		componentStyle: "<?",
	},
	controller: [
		function ()
		{
			const ctrl = this;
			ctrl.netcareService = netcareService;

			ctrl.$onInit = () =>
			{
				netcareService.submitLogoutForm();
				window.location.href = "../logout.jsp";
			}
		}
	]
});