'use strict';

if (!window.Oscar) window.Oscar = {};
if (!Oscar.Util) Oscar.Util = {};

Oscar.Util.Fax = {}

Oscar.Util.Fax.AddOtherFaxProvider = function AddOtherFaxProvider()
{
	var selected = jQuery("#otherFaxSelect option:selected");
	if (this.checkPhone(selected.val()))
	{
		this._AddOtherFax(selected.text(), selected.val());
	}
	else
	{
		alert("The fax number for this provider is invalid.");
	}
};

Oscar.Util.Fax.AddOtherFax = function AddOtherFax()
{
	var number = jQuery("#otherFaxInput").val();
	if (this.checkPhone(number))
	{
		this._AddOtherFax(number, number);
	}
	else
	{
		alert("The fax number you entered is invalid.");
	}
};

Oscar.Util.Fax._AddOtherFax = function _AddOtherFax(name, number)
{
	var remove = "<a href='javascript:void(0);' onclick='Oscar.Util.Fax.removeRecipient(this)'>remove</a>";
	var html = "<li>" + name + "<b>, Fax No: </b>" + number + " " + remove + "<input type='hidden' name='faxRecipients' value='" + number + "'></input></li>";
	jQuery("#faxRecipients").append(jQuery(html));
	this.updateFaxButton();
};

Oscar.Util.Fax.checkPhone = function checkPhone(str)
{
	var phone = str.replace(/\D/g, '');

	// phone number must be 10 digits, optionally with country code 1 at the front
	return phone.length === 10 || (phone.length === 11 && phone.charAt(0) === '1');
};

Oscar.Util.Fax.removeRecipient = function removeRecipient(element)
{
	var el = jQuery(element);
	if (el)
	{
		el.parent().remove();
		this.updateFaxButton();
	}
	else
	{
		alert("Unable to remove recipient.");
	}
};

Oscar.Util.Fax.updateFaxButton = function updateFaxButton()
{
	jQuery(".faxButton").attr('disabled', !this.hasFaxNumber());
};

Oscar.Util.Fax.hasFaxNumber = function hasFaxNumber()
{
	return jQuery("#faxRecipients").children().size() > 0;
};