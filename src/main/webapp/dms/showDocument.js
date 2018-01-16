/*

    Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for the
    Department of Family Medicine
    McMaster University
    Hamilton
    Ontario, Canada

*/
'use strict';

if (!window.Oscar) { window.Oscar = {} }
if (!Oscar.ShowDocument) { Oscar.ShowDocument = {} }

Oscar.ShowDocument.popupPatient = function popupPatient(height, width, url, windowName, docId)
{
	var demographicNo = document.getElementById('demofind' + docId).value;
	var urlNew = url + demographicNo;

	return popup2(height, width, 0, 0, urlNew, windowName);
};

Oscar.ShowDocument.popupPatientTickler = function popupPatientTickler(height, width, url, windowName, docId)
{
	var demographicNo = document.getElementById('demofind' + docId).value;
	var demographicName = document.getElementById('demofindName' + docId).value;
	var urlNew = url + "method=edit" +
		"&tickler.demographic_webName=" + demographicName +
		"&tickler.demographicNo=" + demographicNo +
		"&docType=DOC&docId=" + docId;

	return popup2(height, width, 0, 0, urlNew, windowName);
};
Oscar.ShowDocument.setupDemoAutoCompletion = function setupDemoAutoCompletion(contextPath, docId, linkDocsToProvider)
{

	console.log("Oscar.ShowDocument.setupDemoAutoCompletion");

	var $autoCompleteDemo = jQuery("#autocompletedemo" + docId);

	if ($autoCompleteDemo)
	{
		var activeOnly = jQuery("#activeOnly" + docId).is(":checked");
		var url = contextPath + "/demographic/SearchDemographic.do?jqueryJSON=true&activeOnly=" + activeOnly;

		$autoCompleteDemo.autocomplete({
			source: url,
			minLength: 2,

			focus: function (event, ui)
			{
				$autoCompleteDemo.val(ui.item.label);
				return false;
			},
			select: function (event, ui)
			{
				$autoCompleteDemo.val(ui.item.label);
				jQuery("#demofind" + docId).val(ui.item.value);
				jQuery("#demofindName" + docId).val(ui.item.formattedName);
				selectedDemos.push(ui.item.label);
				console.log(ui.item.providerNo);
				if (ui.item.providerNo != undefined && ui.item.providerNo != null &&
					ui.item.providerNo != "" && ui.item.providerNo != "null" && linkDocsToProvider)
				{
					addDocToList(ui.item.providerNo, ui.item.provider + " (MRP)", docId);
				}

				//enable Save button whenever a selection is made
				jQuery('#save' + docId).removeAttr('disabled');
				jQuery('#saveNext' + docId).removeAttr('disabled');

				jQuery('#msgBtn_' + docId).removeAttr('disabled');
				jQuery('#mainTickler_' + docId).removeAttr('disabled');
				jQuery('#mainEchart_' + docId).removeAttr('disabled');
				jQuery('#mainMaster_' + docId).removeAttr('disabled');
				jQuery('#mainApptHistory_' + docId).removeAttr('disabled');
				return false;
			}
		});
	}
};
Oscar.ShowDocument.setupProviderAutoCompletion = function setupProviderAutoCompletion(contextPath, docId)
{
	var url = contextPath + "/provider/SearchProvider.do?method=labSearch";

	var $autoCompleteProvider = jQuery("#autocompleteprov" + docId);
	var $provFind = jQuery("#provfind" + docId);

	$autoCompleteProvider.autocomplete({
		source: url,
		minLength: 2,

		focus: function (event, ui)
		{
			$autoCompleteProvider.val(ui.item.label);
			return false;
		},
		select: function (event, ui)
		{
			$autoCompleteProvider.val("");
			$provFind.val(ui.item.value);
			addDocToList(ui.item.value, ui.item.label, docId);

			return false;
		}
	});
};
Oscar.ShowDocument.checkObservationDate = function checkObservationDate(formId)
{
	var formElem = document.getElementById(formId);
	var dateElem = formElem.elements["observationDate"];

	if (!Oscar.Util.Common.validateInputNotEmpty(dateElem))
	{
		alert("Blank Date.");
		dateElem.focus();
		return false;
	}

	if (!Oscar.Util.Date.validateDateInput(dateElem))
	{
		alert("Invalid date format: " + dateElem.value);
		dateElem.focus();
		return false;
	}
	return true;
};
Oscar.ShowDocument.updateDocument = function updateDocument(eleId)
{
	var url="../dms/ManageDocument.do",
		data=$(eleId).serialize(true);
	new Ajax.Request(url, {
		method:'post',
		parameters:data,
		onSuccess: function (transport)
		{
			var json = transport.responseText.evalJSON();
			var patientId;
			if (json != null)
			{
				patientId = json.patientId;

				var ar = eleId.split("_");
				var num = ar[1];
				num = num.replace(/\s/g, '');
				$("saveSucessMsg_" + num).show();
				$('saved' + num).value = 'true';
				$("msgBtn_" + num).onclick = function ()
				{
					popup(700, 960, contextpath + '/oscarMessenger/SendDemoMessage.do?demographic_no=' + patientId, 'msg');
				};

				Oscar.ShowDocument.updateDocStatusInQueue(num);
				var success = updateGlobalDataAndSideNav(num, patientId);

				if (success)
				{
					success = updatePatientDocLabNav(num, patientId);
					if (success)
					{
						//disable demo input
						$('autocompletedemo' + num).disabled = true;
					}
				}
			}
		}
	});
	return false;
};
//change status of queue document link row to I=inactive
Oscar.ShowDocument.updateDocStatusInQueue = function updateDocStatusInQueue(docid)
{
	var url="../dms/inboxManage.do",
		data="docid="+docid+"&method=updateDocStatusInQueue";
	new Ajax.Request(url,{
		method:'post',
		parameters:data,
		onSuccess:function(transport){}
	});
};