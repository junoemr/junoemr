function popupPatient(height, width, url, windowName, docId) {
	  d = document.getElementById('demofind'+ docId).value; //demog  //attachedDemoNo
	  urlNew = url + d;
	
	  return popup2(height, width, 0, 0, urlNew, windowName);
}

function popupPatientTickler(height, width, url, windowName,docId) {
  d = document.getElementById('demofind'+ docId).value; //demog  //attachedDemoNo
  n = document.getElementById('demofindName' + docId).value;
  urlNew = url + "method=edit&tickler.demographic_webName=" + n + "&tickler.demographicNo=" +  d + "&docType=DOC&docId="+docId;
  	
  	  return popup2(height, width, 0, 0, urlNew, windowName);
}
function setupDemoAutoCompletion(contextPath, docId,  linkDocsToProvider) {

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
}
function setupProviderAutoCompletion(contextPath, docId) {
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
}