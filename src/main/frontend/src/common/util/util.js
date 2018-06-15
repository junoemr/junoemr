'use strict';

if (!window.Juno) window.Juno = {};


if (!Juno.Common) Juno.Common = {};

Juno.Common.Util = {};

Juno.Common.Util.exists = function exists(object) {
	// not undefined and not null
	return angular.isDefined(object) && object !== null;
};

Juno.Common.Util.isBlank = function isBlank(object) {
	// undefined or null or empty string
	return !Juno.Common.Util.exists(object) || object === "";
};

Juno.Common.Util.toArray = function toArray(obj) { //convert single object to array
	if (obj instanceof Array) return obj;
	else if (obj == null) return [];
	else return [obj];
};

Juno.Common.Util.pad0 = function pad0(n) {
	var s = n.toString();
	if (s.length == 1) s = "0" + s;
	return s;
};

Juno.Common.Util.toTrimmedString = function toTrimmedString(s) {
	if (s == null) s = "";
	if (s instanceof String) s = s.trim();
	return s;
};

Juno.Common.Util.formatDate = function formatDate(d) {
	d = Juno.Common.Util.toTrimmedString(d);
	if (d) {
		if (!(d instanceof Date)) d = new Date(d);
		d = d.getFullYear() + "-" + Juno.Common.Util.pad0(d.getMonth() + 1) + "-" + Juno.Common.Util.pad0(d.getDate());
	}
	return d;
};

Juno.Common.Util.formatTime = function formatTime(d) {
	d = Juno.Common.Util.toTrimmedString(d);
	if (d) {
		if (!(d instanceof Date)) d = new Date(d);
		d = Juno.Common.Util.pad0(d.getHours()) + ":" + Juno.Common.Util.pad0(d.getMinutes());
	}
	return d;
};

Juno.Common.Util.addNewLine = function addNewLine(line, mssg) {
	if (line == null || line.trim() == "") return mssg;

	if (mssg == null || mssg.trim() == "") mssg = line.trim();
	else mssg += "\n" + line.trim();

	return mssg;
};

Juno.Common.Util.calcAge = function calcAge(dobYear, dobMonth, dobDay)
 {

	var dateOfBirth = new Date(parseInt(dobYear, 10), parseInt(dobMonth, 10), parseInt(dobDay, 10));
	var currDate = new Date();

	var years = (currDate.getFullYear() - dateOfBirth.getFullYear());

	if (currDate.getMonth() < dateOfBirth.getMonth() ||
		currDate.getMonth() == dateOfBirth.getMonth() && currDate.getDate() < dateOfBirth.getDate())
	{
		years--;
	}
	return years;
};

Juno.Common.Util.isInArray = function isInArray(value, array) 
{
	return array.indexOf(value) > -1;
};

/**
 * recursively merges two js hashes. the baseHash will be modified
 * @param baseHash
 * @param toMerge
 */
Juno.Common.Util.mergeHash = function mergeHash(baseHash, toMerge)
{
	if (toMerge === 'undefined') {
		return false;
	}
	$.extend(true, baseHash, toMerge);
	return true;
};

Juno.Common.Util.noNull = function noNull(val)
{
    if (typeof val === 'string')
    {
        val =  val.trim();
    }
    else if ( val === null)
    {
		val = "";
    }

    return val;
};

Juno.Common.Util.isUndefinedOrNull = function isUndefinedOrNull(val)
{
	return angular.isUndefined(val) || val === null;
};

Juno.Common.Util.isDefinedAndNotNull = function isDefinedAndNotNull(val)
{
	return (angular.isDefined(val) && val !== null);
};

Juno.Common.Util.objectArrayIndexOf = function objectArrayIndexOf(array, searchTerm, property)
{
	for(var i = 0, len = array.length; i < len; i++) {
		if (array[i][property] === searchTerm) return i;
	}
	return -1;
};

Juno.Common.Util.isIntegerString = function isIntegerString(string)
{
	var parsed_string = parseInt(string);

	if (/^-?\d+$/.test(string.toString()))
	{
		return true;
	}

	return false;
};
