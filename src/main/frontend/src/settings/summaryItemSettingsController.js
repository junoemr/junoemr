import patientSummaryItems from "./partials/patientSummaryItems.html";

angular.module('Settings').component('patientSummaryItems', {
	template: patientSummaryItems,
	controller: function ()
	{
		var ctrl = this;
	},
	bindings:
	{
		pref: '='
	}
});