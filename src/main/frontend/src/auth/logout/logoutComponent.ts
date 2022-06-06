import {authService} from "../../lib/auth/AuthService";

angular.module("Auth").component('logout', {
	templateUrl: 'src/auth/logout/logout.jsp',
	bindings: {
		componentStyle: "<?",
	},
	controller: [
		function ()
		{
			const ctrl = this;

			ctrl.$onInit = () =>
			{
				authService.logout();
			}
			ctrl.triggerLogout = () =>
			{
				authService.logout();
			}
		}
	]
});