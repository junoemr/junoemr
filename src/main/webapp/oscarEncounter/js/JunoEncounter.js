'use strict';

if (!window.Juno) window.Juno = {};
if (!Juno.OscarEncounter) Juno.OscarEncounter = {};
if (!Juno.OscarEncounter.JunoEncounter) Juno.OscarEncounter.JunoEncounter = {};

var me = Juno.OscarEncounter.JunoEncounter;


me.checkLengthOfObject = function checkLengthOfObject(o)
{
	var c = 0;
	for (var attr in o)
	{
		if (o.hasOwnProperty(attr))
		{
			++c;
		}
	}

	return c;
};

me.popupUploadPage = function popupUploadPage(varpage, dn)
{
	var page = "" + varpage + "?demographicNo=" + dn;
	windowprops = "height=500,width=500,location=no,"
		+ "scrollbars=no,menubars=no,toolbars=no,resizable=yes,top=50,left=50";
	var popup = window.open(page, "", windowprops);
	popup.focus();

};
