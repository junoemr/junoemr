"use strict"
console.log("loaded");

var Juno = Juno || {};

Juno.Demographic = Juno.Demographic || {};

Juno.Demographic.InputCtrl = Juno.Demographic.InputCtrl || {};

Juno.Demographic.InputCtrl.formatPhoneNumber = function formatPhoneNumber(phone)
{
	console.log("get here");
	if (phone.value.length === 10) {
		phone.value = phone.value.substring(0,3) + "-" + phone.value.substring(3,6) + "-" + phone.value.substring(6);
	}
	if (phone.value.length === 11 && phone.value.charAt(3) === '-') {
		phone.value = phone.value.substring(0,3) + "-" + phone.value.substring(4,7) + "-" + phone.value.substring(7);
	}
};

Juno.Demographic.InputCtrl.formatPhoneOnLoad = function formatPhoneOnLoad (phone1,phone2,cellPhone)
{
	Juno.Demographic.InputCtrl.formatPhoneNumber(phone1);
	Juno.Demographic.InputCtrl.formatPhoneNumber(phone2);
	Juno.Demographic.InputCtrl.formatPhoneNumber(cellPhone);
};
