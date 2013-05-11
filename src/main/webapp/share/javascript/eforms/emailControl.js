if (typeof jQuery == "undefined") { alert("The emailControl library requires jQuery. Please ensure that it is loaded first"); }

var emailControlPlaceholder = "<br/>Email Recipients:<br/><div id='emailForm'>Loading email options..</div>";
var emailControlEmailPatientButton     = "<span>&nbsp;</span><input value='Email to Patient' name='EmailPatientButton' id='email_patient_button' type='button' onclick='submitEmailButtonAjax(false, true)'>";
var emailControlEmailButton     = "<span>&nbsp;</span><input value='Email' name='EmailSaveButton' id='email_button' type='button' onclick='submitEmailButtonAjax(false, false)'>";
var emailControlEmailSaveButton = "<span>&nbsp;</span><input value='Submit & Email' name='EmailButton' id='emailSave_button' type='button' onclick='submitEmailButtonAjax(true, false)'>";
var emailControlMemoryInput = "<input value='false' name='emailEForm' id='emailEForm' type='hidden' />";	
var emailControlToEmail = "<input value='' name='toEmail' id='toEmail' type='hidden' />";	
var emailControlToName = "<input value='' name='toName' id='toName' type='hidden' />";	
var emailControl = {
	initialize: function () {
		var placeholder = jQuery("#emailControl");
		if (placeholder == null || placeholder.size() == 0) { 
			if (jQuery(".DoNotPrint").size() > 0) { 
				placeholder = jQuery("<div id='emailControl'>&nbsp;</div>");
				jQuery(".DoNotPrint").append(placeholder);				
			}
			else {
				alert("Missing placeholder please ensure a div with the id emailControl or a div with class DoNotPrint exists on the page."); 
				return;
			}
		}
		
		var demoNo ="";			
		demoNo = getSearchValue("demographic_no");
		if (demoNo == "") { demoNo = getSearchValue("efmdemographic_no", jQuery("form").attr('action')); }
		placeholder.html(emailControlPlaceholder);
		
		$.ajax({
			url:"../eform/efmformemail_form.jsp",
			data:"demographicNo=" + demoNo,
			success: function(data) {
				
				if (data == null || data.trim() == "") {
					placeholder.html("");
					alert("Error loading email control, please contact an administrator.");
				}
				else { 
					placeholder.html(data);					
					var buttonLocation = jQuery("input[name='SubmitButton']");
					if (buttonLocation.size() != 0) { 
						buttonLocation = jQuery(buttonLocation[buttonLocation.size() -1]);
						jQuery(emailControlEmailPatientButton).insertAfter(buttonLocation);
						jQuery(emailControlEmailButton).insertAfter(buttonLocation);
						jQuery(emailControlEmailSaveButton).insertAfter(buttonLocation);
						jQuery(emailControlMemoryInput).insertAfter(buttonLocation);
						jQuery(emailControlToEmail).insertAfter(buttonLocation);
						jQuery(emailControlToName).insertAfter(buttonLocation);
					}
					else {
						buttonLocation = jQuery(".DoNotPrint");
						if (buttonLocation == null || buttonLocation.size() == 0) {			
							buttonLocation = jQuery(jQuery("form")[0]);
						}
						if (buttonLocation != null) {
							buttonLocation.append(jQuery(emailControlEmailPatientButton));
							buttonLocation.append(jQuery(emailControlEmailButton));
							buttonLocation.append(jQuery(emailControlEmailSaveButton));
							buttonLocation.append(jQuery(emailControlMemoryInput));
							buttonLocation.append(jQuery(emailControlToEmail));
							buttonLocation.append(jQuery(emailControlToName));
						}
					}
					if (buttonLocation == null) { alert("Unable to find form or save button please check this is a proper eform."); return; }					
					
				}
			}
		});
	}		
};

jQuery(document).ready(function() {
	emailControl.initialize();
});

function chooseEmail(){
	toName = jQuery("#emailSelect").find(":selected").text();
	toEmail = jQuery("#emailSelect").find(":selected").val();
	
	jQuery('#toName').val(toName);
	jQuery('#toEmail').val(toEmail);
}

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

function submitEmailButtonAjax(save, emailPatient) {
	document.getElementById('emailEForm').value=true;
	
	var saveHolder = jQuery("#saveHolder");
	if (saveHolder == null || saveHolder.size() == 0) {
		jQuery("form").append("<input id='saveHolder' type='hidden' name='skipSave' value='"+!save+"' >");
	}
	saveHolder = jQuery("#saveHolder");
	saveHolder.val(!save);
	needToConfirm=false;
	if (document.getElementById('Letter') == null) {
		if(emailPatient){
			toEmail = jQuery("#patient_email").val();
			jQuery('#toEmail').val(toEmail);
		}
		var form = $("form");
		
		var closeWindowHTML = '<script type="text/javascript">'+
							'function closeWindow(){'+
							'    setTimeout("window.close()",5000);'+
							'    console.log("test");'+
							'}'+
							'</script>';
		resultWindow = window.open('', 'resultWindow', "location=1,status=1,scrollbars=1,resizable=no,width=300,height=100,menubar=no,toolbar=no");
		resultWindow.document.write(closeWindowHTML);
		resultWindow.document.write("Sending email to &lt;"+$('#toEmail').val()+"&gt; ");
		$.ajax({
			 type: "POST",  
			 url: form.attr("action"),  
			 data: form.serialize(),  
			 success: function() {
				 resultWindow.document.write("<div style=\"color:#458B00; font-weight: bold; padding-top: 10px;\">Email successfully sent</div>");
				 resultWindow.document.write("<script type=\"text/javascript\">closeWindow();</script>");
			 },
			 error: function() {
				 resultWindow.document.write("<div>Something went wrong while trying to send the email. Please contact your administrator.</div>");
			 } 
		});
	}
	else {
		var form = $("form[name='RichTextLetter']");
		if (!save) { form.attr("target", "_blank"); }
		document.getElementById('Letter').value=editControlContents('edit');
		
		$.ajax({
			 type: "POST",  
			 url: form.attr("action"),  
			 data: form.serialize(),  
			 success: function() {  
			    alert("Email sent successfully");
			    if (save) { window.close(); }
			 },
			 error: function() {
				 alert("An error occured while attempting to send your email, please contact an administrator.");
			 } 
		});
	}
	document.getElementById('emailEForm').value=false;
}
