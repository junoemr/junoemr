if (typeof jQuery == "undefined") { alert("The faxControl library requires jQuery. Please ensure that it is loaded first"); }

var faxControlPlaceholder = "<br/>Fax Recipients:<br/><div id='faxForm'>Loading fax options..</div>";
var faxControlFaxButton     = "<span>&nbsp;</span><input value='Fax' name='FaxButton' id='fax_button' class='faxButton' disabled type='button' onclick='submitFax(false)'>";
var faxControlFaxSaveButton = "<span>&nbsp;</span><input value='Submit & Fax' name='FaxSaveButton' class='faxButton' id='faxSave_button' disabled type='button' onclick='submitFax(true)'>";
var faxControlMemoryInput = "<input value='false' name='fax' id='fax' type='hidden' />";
var faxControl = {
	initialize: function () {
		var placeholder = jQuery("#faxControl");
		if (placeholder == null || placeholder.size() == 0) { 
			if (jQuery(".DoNotPrint").size() > 0) { 
				placeholder = jQuery("<div id='faxControl'>&nbsp;</div>");
				jQuery(".DoNotPrint").append(placeholder);				
			}
			else {
				alert("Missing placeholder please ensure a div with the id faxControl or a div with class DoNotPrint exists on the page."); 
				return;
			}
		}
		
		var demoNo ="";			
		demoNo = getSearchValue("demographic_no");
		if (demoNo == "") { demoNo = getSearchValue("efmdemographic_no", jQuery("form").first().attr('action')); }
		placeholder.html(faxControlPlaceholder);
		var faxEnabled = true;

		$.ajax({
			url:"../eform/efmformfax_form.jsp",
			data:"demographicNo=" + demoNo,
			success: function(data) {
				
				if (data == null || data.trim() == "") {
					placeholder.html("");
					console.log("Error loading fax control, please contact an administrator.");
				}
				else {
					placeholder.html(data);					
					var buttonLocation = jQuery("input[name='SubmitButton']");
					faxEnabled = (jQuery("#faxControl_faxEnabled").val() == "true");
					if (buttonLocation.size() != 0) { 
						buttonLocation = jQuery(buttonLocation[buttonLocation.size() -1]);
						jQuery(faxControlFaxButton).insertAfter(buttonLocation);
						jQuery(faxControlFaxSaveButton).insertAfter(buttonLocation);
						jQuery(faxControlMemoryInput).insertAfter(buttonLocation);
					}
					else {
						buttonLocation = jQuery(".DoNotPrint");
						if (buttonLocation == null || buttonLocation.size() == 0) {
							buttonLocation = jQuery("form").first();
						}
						if (buttonLocation != null) {
							buttonLocation.append(jQuery(faxControlFaxButton));
							buttonLocation.append(jQuery(faxControlFaxSaveButton));
							buttonLocation.append(jQuery(faxControlMemoryInput));
						}
					}
					if (buttonLocation == null) { alert("Unable to find form or save button please check this is a proper eform."); return; }

					if(!faxEnabled) {
						placeholder.find(":input").prop('disabled', true);
						placeholder.find(":button").prop('disabled', true);
						console.info("fax is disabled for this oscar instance.");
					}
				}
			}
		});
	}		
};

jQuery(document).ready(function() {
	faxControl.initialize();
});


function getSearchValue(name, url)
{
	if (url == null) { url = window.location.href; }
	name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
	var regexS = "[\\?&]"+name+"=([^&#]*)";
	var regex = new RegExp(regexS);
	var results = regex.exec(url);
	if (results == null) { return ""; }
	else { return results[1]; }
}

function submitFax(save) {
	document.getElementById('fax').value=true;
	var form = jQuery("form").first();

	var saveHolder = jQuery("#saveHolder");
	if (saveHolder == null || saveHolder.size() == 0) {
		form.append("<input id='saveHolder' type='hidden' name='skipSave' value='"+!save+"' >");
	}
	saveHolder = jQuery("#saveHolder");
	saveHolder.val(!save);
	needToConfirm=false;
	if (document.getElementById('Letter') == null)
	{
		form.submit();
	}
	else
	{
		form = $("form[name='RichTextLetter']");
		document.getElementById('Letter').value=editControlContents('edit');
		form.submit();
	}

	document.getElementById('fax').value=false;
}