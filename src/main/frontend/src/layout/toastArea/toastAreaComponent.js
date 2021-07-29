import {toastStore} from "../../lib/alerts/store/ToastStore";

angular.module("Layout").component('toastArea', {
	templateUrl: 'src/layout/toastArea/toastArea.jsp',
	bindings: {
	},
	controller: [
		"$scope",
		function (
			$scope)
		{
			const ctrl = this;

			ctrl.getToasts = () =>
			{
				return toastStore.activeToasts;
			}
		}],
});
