angular.module('Common.Components').component('iconBadge', {
	templateUrl: 'src/common/components/iconBadge/iconBadge.jsp',
	bindings: {
		icon: '@',
		click: '&?',
		disabled: "<?",
	},
	controller: [function ()
	{
		let ctrl = this;

		ctrl.$onInit = function ()
		{
			ctrl.icon = ctrl.icon || "icon-question";
			ctrl.disabled = ctrl.disabled || false;
		}

		ctrl.clickHandler = ($event) =>
		{
			if (!ctrl.disabled && ctrl.click)
			{
				ctrl.click({$event});
			}
		};
	}],
});