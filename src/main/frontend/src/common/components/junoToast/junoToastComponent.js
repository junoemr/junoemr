
angular.module('Common.Components').component('junoToast', {
	templateUrl: 'src/common/components/junoToast/junoToast.jsp',
	bindings: {
		toast: "<"// Type Toast.
	},
	controller: [
		"$scope",
		function (
			$scope)
		{
			const ctrl = this;

			ctrl.getComponentClasses = () =>
			{
				return [
					ctrl.toast.visible ? "toast-visible" : "toast-invisible",
					ctrl.toast.clickable ? "clickable" : "",
					...ctrl.toast.cssClasses
				];
			};

			ctrl.onClick = () =>
			{
				ctrl.toast.onClick();
			};

		}],
});
