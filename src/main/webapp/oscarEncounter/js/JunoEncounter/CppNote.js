'use strict';

if (!window.Juno) window.Juno = {};
if (!Juno.OscarEncounter) Juno.OscarEncounter = {};
if (!Juno.OscarEncounter.JunoEncounter) Juno.OscarEncounter.JunoEncounter = {};
if (!Juno.OscarEncounter.JunoEncounter.CppNote) Juno.OscarEncounter.JunoEncounter.CppNote = {};

var me = Juno.OscarEncounter.JunoEncounter.CppNote;

/*
me.prepareExtraFields = function prepareExtraFields(cpp, extObj)
{
	var rowIDs = new Array(10);
	for (var i = 2; i < exFields.length; i++)
	{
		rowIDs[i] = "Item" + exFields[i];
		$(rowIDs[i]).hide();
	}
	if (cpp == cppNames[1]) $(rowIDs[2], rowIDs[4], rowIDs[8], rowIDs[9]).invoke("show");
	if (cpp == cppNames[2]) $(rowIDs[3], rowIDs[4], rowIDs[7], rowIDs[8], rowIDs[9]).invoke("show");
	if (cpp == cppNames[3]) $(rowIDs[5], rowIDs[8], rowIDs[9], rowIDs[10]).invoke("show");
	if (cpp == cppNames[4]) $(rowIDs[3], rowIDs[6], rowIDs[8], rowIDs[9]).invoke("show");

	for (var i = 0; i < exFields.length; i++)
	{
		$(exFields[i]).value = "";
	}

	for (var j = 0; j < exFields.length; j++)
	{
		if (extObj.hasOwnProperty(exFields[j]))
		{
			$(exFields[j]).value = extObj[exFields[j]];
			continue;
		}
	}
};
 */
