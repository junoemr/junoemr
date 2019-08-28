angular.module('Layout').component('junoModal', {
	bindings: {},
	templateUrl: "src/layout/modalTemplate.jsp",
	transclude: {
		// 'header': '?modalHeader',
		'title': '?modalTitle',
		'ctlButtons': '?modalCtlButtons',
		'body' : '?modalBody',
		'footer': '?modalFooter',
	},
	controller: function ()
	{

	}
});