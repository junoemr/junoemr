"use strict";

var Juno = Juno || {};

Juno.Demographic = Juno.Demographic || {};

Juno.Demographic.InputCtrl = Juno.Demographic.InputCtrl || {};

Juno.Demographic.InputCtrl.formatPhoneNumber = function formatPhoneNumber(phone)
{
	if (phone.value.length === 10)
	{
		phone.value = phone.value.substring(0,3) + "-" + phone.value.substring(3,6) + "-" + phone.value.substring(6);
	}
	if (phone.value.length === 11 && phone.value.charAt(3) === '-')
	{
		phone.value = phone.value.substring(0,3) + "-" + phone.value.substring(4,7) + "-" + phone.value.substring(7);
	}
};
