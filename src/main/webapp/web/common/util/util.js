'use strict';

if (!window.Juno) window.Juno = {};


if (!Juno.Common) Juno.Common = {};

Juno.Common.Util = {};

Juno.Common.Util.exists = function exists(object)
{
	// not undefined and not null
	return angular.isDefined(object) && object !== null;
};

Juno.Common.Util.isBlank = function isBlank(object)
{
	// undefined or null or empty string
	return !Juno.Common.Util.exists(object) || object === "";
};

Juno.Common.Util.toArray = function toArray(obj)
{ //convert single object to array
	if (obj instanceof Array) return obj;
	else if (obj == null) return [];
	else return [obj];
};

Juno.Common.Util.pad0 = function pad0(n)
{
	var s = n.toString();
	if (s.length == 1) s = "0" + s;
	return s;
};

Juno.Common.Util.noNull = function noNull(s)
{
	if (s == null) s = "";
	if (s instanceof String) s = s.trim();
	return s;
};

Juno.Common.Util.formatDate = function formatDate(d)
{
	d = Juno.Common.Util.noNull(d);
	if (d)
	{
		if (!(d instanceof Date)) d = new Date(d);
		d = d.getFullYear() + "-" + Juno.Common.Util.pad0(d.getMonth() + 1) + "-" + Juno.Common.Util.pad0(d.getDate());
	}
	return d;
};

Juno.Common.Util.formatTime = function formatTime(d)
{
	d = Juno.Common.Util.noNull(d);
	if (d)
	{
		if (!(d instanceof Date)) d = new Date(d);
		d = Juno.Common.Util.pad0(d.getHours()) + ":" + Juno.Common.Util.pad0(d.getMinutes());
	}
	return d;
};

Juno.Common.Util.addNewLine = function addNewLine(line, mssg)
{
	if (line == null || line.trim() == "") return mssg;

	if (mssg == null || mssg.trim() == "") mssg = line.trim();
	else mssg += "\n" + line.trim();

	return mssg;
};