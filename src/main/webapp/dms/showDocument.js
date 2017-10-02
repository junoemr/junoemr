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



Oscar.ShowDocument.popupPatient = function popupPatient(height, width, url, windowName, docId) {
	  d = document.getElementById('demofind'+ docId).value; //demog  //attachedDemoNo
	  var urlNew = url + d;
	
	  return popup2(height, width, 0, 0, urlNew, windowName);
};

Oscar.ShowDocument.popupPatientTickler = function popupPatientTickler(height, width, url, windowName,docId) {
  d = document.getElementById('demofind'+ docId).value; //demog  //attachedDemoNo
  n = document.getElementById('demofindName' + docId).value;
  var urlNew = url + "method=edit&tickler.demographic_webName=" + n + "&tickler.demographicNo=" +  d + "&docType=DOC&docId="+docId;
  	
  	  return popup2(height, width, 0, 0, urlNew, windowName);
};
Oscar.ShowDocument.setupDemoAutoCompletion = function setupDemoAutoCompletion(contextPath, docId,  linkDocsToProvider) {

	console.log("Oscar.ShowDocument.setupDemoAutoCompletion");

	var $autoCompleteDemo = jQuery("#autocompletedemo"+docId);

	if($autoCompleteDemo){

		var url;

		var activeOnly = jQuery("#activeOnly"+docId).is(":checked");

		url = contextPath + "/demographic/SearchDemographic.do?jqueryJSON=true&activeOnly=" + activeOnly;

		$autoCompleteDemo.autocomplete({
			source: url,
			minLength: 2,

			focus: function( event, ui ) {
				$autoCompleteDemo.val( ui.item.label );
				return false;
			},
			select: function(event, ui) {
				$autoCompleteDemo.val(ui.item.label);
				jQuery( "#demofind"+docId).val(ui.item.value);
				jQuery( "#demofindName"+docId).val(ui.item.formattedName);
				selectedDemos.push(ui.item.label);
				console.log(ui.item.providerNo);
				if( ui.item.providerNo != undefined && ui.item.providerNo != null &&ui.item.providerNo != "" && ui.item.providerNo != "null" && linkDocsToProvider) {
					addDocToList(ui.item.providerNo, ui.item.provider + " (MRP)", docId);
				}

				//enable Save button whenever a selection is made
				jQuery('#save'+docId).removeAttr('disabled');
				jQuery('#saveNext'+docId).removeAttr('disabled');

				jQuery('#msgBtn_'+docId).removeAttr('disabled');
				jQuery('#mainTickler_'+docId).removeAttr('disabled');
				jQuery('#mainEchart_'+docId).removeAttr('disabled');
				jQuery('#mainMaster_'+docId).removeAttr('disabled');
				jQuery('#mainApptHistory_'+docId).removeAttr('disabled');
				return false;
			}
		});
	}
};
Oscar.ShowDocument.setupProviderAutoCompletion = function setupProviderAutoCompletion(contextPath, docId) {
	var url = contextPath + "/provider/SearchProvider.do?method=labSearch";

	var $autoCompleteProvider = jQuery( "#autocompleteprov"+docId);
	var $provFind = jQuery("#provfind"+docId);

	$autoCompleteProvider.autocomplete({
		source: url,
		minLength: 2,

		focus: function( event, ui ) {
			$autoCompleteProvider.val( ui.item.label );
			return false;
		},
		select: function(event, ui) {
			$autoCompleteProvider.val("");
			$provFind.val(ui.item.value);
			addDocToList(ui.item.value, ui.item.label, docId);

			return false;
		}
	});
};