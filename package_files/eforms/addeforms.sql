-- Default eforms will be uploaded here

-- DriverReport
INSERT into eform ( form_name, file_name, subject, form_date, form_time, status, patient_independent, form_html )
VALUES ( 'Lab Requisition 2011', 'UpdatedLabReq2011-NoDarkChecks.html', 'LabReq2011', CURDATE(), CURTIME(), 1, 0, '<html>
<head>
<title>Lab Requisition</title>

<!-- CSS Script that removes textarea and textbox borders when printing -->
<style type="text/css" media="print">
.DoNotPrint{
    display:none;
}
.noborder {
    border : 0px;
    background: transparent;
	scrollbar-3dlight-color: transparent;
	scrollbar-3dlight-color: transparent;
	scrollbar-arrow-color: transparent;
	scrollbar-base-color: transparent;
	scrollbar-darkshadow-color: transparent;
	scrollbar-face-color: transparent;
	scrollbar-highlight-color: transparent;
	scrollbar-shadow-color: transparent;
	scrollbar-track-color: transparent;
	overflow: hidden;
}
</style>

<!-------Script to optimize window on loading----------->
<script language="JavaScript">
top.window.moveTo(0,0);
if (document.all) {
top.window.resizeTo(screen.availWidth,screen.availHeight);
}
else if (document.layers||document.getElementById) {
if (top.window.outerHeight<screen.availHeight||top.window.outerWidth<screen.availWidth){
top.window.outerHeight = screen.availHeight;
top.window.outerWidth = 1130;
}
}
</script>
<!----------End optimize window script---------->

<!-----------------script for current time--------->
<SCRIPT language="javascript">

<!-- hide javascript

function StartClock12() {
Time12 = new Date();
Cur12Hour = Time12.getHours();
Cur12Mins = Time12.getMinutes();
The12Time = 0;
The12Time = (Cur12Hour > 12) ? Cur12Hour - 12 : Cur12Hour;
The12Time += ((Cur12Mins < 10) ? \':0\' : \':\') + Cur12Mins;
The12Time += (Cur12Hour > 11) ? \' PM\': \' AM\';
window.status = The12Time;
setTimeout(\'StartClock12()\',1000);
}

// done hiding -->

</SCRIPT> 
<!-------end current time script-------------->

<!-- --Script for pop-up menu------------------- -->
<style type="text/css">
#topbar{
	position:absolute;
	border: 1px solid black;
	padding: 2px;
	background-color: lightyellow;
	width: 220px;
	visibility: hidden;
	z-index: 100;
	font-size: 10;
}
</style>

<!-- ----------Beginning of Darker/LargerCheckBox Script---------------- -->

<script language="javascript">
function formPrint(){
    changeClass();
    window.print();
} 
function changeClass(){
x = document.getElementsByTagName(\'input\')
    for (i=0; i<x.length; i++){
        if (x[i].type == \'checkbox\'){
            x[i].className = \'largerCheckbox\'
        }
    }
}
</script>
<style type="Text/css">
input.largerCheckbox {
    -moz-transform:scale(1.2);         /*scale up image 1.3x - Firefox specific */ 
    -webkit-transform:scale(1.2);      /*Webkit based browser eg Chrome, Safari */ 
    -o-transform:scale(1.2);           /*Opera browser */ 
}
</style>
<style type="text/css" media="print">
input.largerCheckbox { 
    -moz-transform:scale(1.4);         /*scale up image 1.8x - Firefox specific */ 
    -webkit-transform:scale(1.2);      /*Webkit based browser eg Chrome, Safari */ 
    -o-transform:scale(1.2);           /*Opera browser */ 
} 
</style>
<!--[if IE]>
<style type="text/css">
input.largerCheckbox { 
    height: 15px;                     /*30px checkboxes for IE 5 to IE 7 */ 
    width: 15px; 
} 
</style> 
<![endif]--> 
<!-- ----------End of Darker/LargerCheckBox Script---------------- -->


<!-- scripts to confirm closing of window if it hadn\'t been saved yet -->
<script language="javascript">
//keypress events trigger dirty flag
var needToConfirm = false;
document.onkeyup=setDirtyFlag;
function setDirtyFlag(){
        needToConfirm = true;
}
function releaseDirtyFlag(){
        needToConfirm = false; //Call this function if doesn\'t requires an alert.
//this could be called when save button is clicked
}
window.onbeforeunload = confirmExit;
function confirmExit(){
     if (needToConfirm){
         return "You have attempted to leave this page. If you have made any changes to the fields without clicking the Save button, your changes will be lost. Are you sure you want to exit this page?";
     }
}
</script>

<!-- Script to check for change in default payer -->
<script language="javascript">
function checkIt(){
	if (BillToWSBC.checked==false && BillToICBC.checked==false && BillToPatient.checked==false && BillToOther.checked==false)
		{
		BillToMSP.checked = true;
	}
}
</script>

</head>

<body onload="StartClock12(); checkIt();">

<img src="${oscar_image_path}LabReq2011.png" style="position: absolute; left: 12px; top: 16px; width: 850px">

<!-- You can remove ${oscar_image_path} as you develop the form, but make sure you put it back before uploading to OSCAR otherwise the image wouldn\'t show.
<!-- Also note: the image filename IS CASE SENSITIVE INCLUDING THE EXTENSION. It may work otherwise in Windows, but not in OSCAR because it\'s based on a Linux platform -->

<form method="post" action="" name="FormName" id="FormName">

<!-- ----------------------------All textfields/checkboxes/textareas go here...------ -->

<input type="hidden" name="today" id="today" oscarDB=today>
<input type="hidden" name="PatientMD" id="PatientMD" oscarDB=doctor>

<!-- -------------LabLocation------ -->
	<textarea name="LabLocation" class="noborder" wrap="virtual" style="position: absolute; left: 115px; top: 35px; width: 260px; height: 63px; font-family: Arial; font-size: 12px"></textarea>

	<textarea name="LabHours" class="noborder" wrap="virtual" style="position: absolute; left: 375px; top: 50px; width: 200px; height: 38px; font-family: Arial; font-size: 12px"></textarea>

<!-- -------------Guideline Reference: see specific sections for the guideline webpage info------ -->
<!-- -------------Use format below for all references------ -->

        <a href="http://www.bcguidelines.ca/gpac/submenu_diagnostics_lab.html" style="position: absolute; left: 285px; top: 124px; font-family: Arial; font-weight: bold; font-size: 9.2px" target="_blank">Guideline Home Page</a>

<!-- -------------Bill to:------ -->
	<input name="BillToMSP" id="BillToMSP" type="checkbox" style="position: absolute; left: 60px; top: 145px;">

	<input name="BillToICBC" id="BillToICBC" type="checkbox" style="position: absolute; left: 112px; top: 145px;">

	<input name="BillToWSBC" id="BillToWSBC" type="checkbox" style="position: absolute; left: 166px; top: 145px;">

	<input name="BillToPatient" id="BillToPatient" type="checkbox" style="position: absolute; left: 256px; top: 145px;">

	<input name="BillToOther" id="BillToOther" type="checkbox" style="position: absolute; left: 336px; top: 145px;">

	<input name="BillToOtherText" type="text" style="position: absolute; left: 397px; top: 145px;" style="width: 180px; font-family: Arial; font-size: 14px;" class="noborder">

<!-- -------------Patient Demographics------ -->
	<input name="PHN" type="text" oscardb=HIN style="position: absolute; left: 18px; top: 186px; width: 260px; height: 23px; font-family: Arial; font-size: 14px; text-align: left;" class="noborder">

	<input name="ICBC/WSBC/RCMPNumber" type="text" style="position: absolute; left: 288px; top: 186px; width: 290px; height: 23px; font-family: Arial; font-size: 14px; text-align: left;" class="noborder">

	<input name="Surname" type="text" oscarDB=patient_nameL style="position: absolute; left: 18px; top: 220px; width: 260px; font-family: Arial; font-size: 14px;" class="noborder">

	<input name="FirstName" type="text" oscarDB=patient_nameF style="position: absolute; left: 288px; top: 220px; width: 290px; font-family: Arial; font-size: 14px;" class="noborder">

	<input name="DOB" type="text" oscardb=dob style="position: absolute; left: 18px; top: 255px; width: 155px; font-family: Arial; font-size: 14px; text-align: left;" class="noborder">

	<input name="gender" type="text" oscardb=sex style="position: absolute; left: 194px; top: 255px; width: 63px; font-family: Arial; font-size: 14px; text-align: center;" class="noborder">

	<input name="PregnantYes" type="checkbox" style="position: absolute; left: 339px; top: 252px;">

	<input name="PregnantNo" type="checkbox" style="position: absolute; left: 377px; top: 252px;">

	<input name="FastingTime" type="text" style="position: absolute; left: 470px; top: 250px; width: 45px; font-family: Arial; font-size: 16px; text-align: center;" class="noborder">

	<input name="PatientPhoneNumber" type="text" oscardb=phone style="position: absolute; left: 18px; top: 288px; width: 260px; font-family: Arial; font-size: 14px; text-align: left;" class="noborder">

	<input name="ChartNumber" type="text" style="position: absolute; left: 288px; top: 288px; width: 290px; font-family: Arial; font-size: 14px; text-align: left;" class="noborder">

	<input name="Address" type="text" oscardb=addressLine style="position: absolute; left: 18px; top: 319px; width: 560px; font-family: Arial; font-size: 14px; text-align: left;" class="noborder">

	<input name="DiagnosisAndIndications" type="text" style="position: absolute; left: 18px; top: 354px; width: 415px; font-family: Arial; font-size: 14px;" class="noborder">

	<input name="CurrentMedicationsLastDose" type="text" style="position: absolute; left:445px; top: 354px; width: 415; font-family: Arial; font-size: 14px" class="noborder">

<!------------Ordering Physician(s) info--------------->
	<input name="PhysicianName" type="text" oscarDB=current_user style="position: absolute; left: 588px; top: 60px; width: 272px; font-family: Arial; font-size: 12px; font-weight: bold; text-align: left;" class="noborder">

	<textarea name="Clinic label" oscarDB=clinic_label style="position: absolute; left: 588px; top:80px; height: 90px; width: 272px; font-family: Arial; font-size: 12px; font-weight: bold;" class="noborder"></textarea>

	<input name="LocumName" type="text" style="position: absolute; left: 588px; top: 183px; width: 272px; font-family: Arial; font-size: 12px; text-align: left;" class="noborder">

	<input name="MSPNo" type="text" style="position: absolute; left: 588px; top: 217px; width: 272px; font-family: Arial; font-size: 12px; text-align: left;" class="noborder">

	<input name="STATContact" type="text" style="position: absolute; left: 588px; top: 254px; width: 272px; font-family: Arial; font-size: 12px; text-align: left;" class="noborder">

	<input name="CopyTo" type="text" style="position: absolute; left: 588px; top: 288px; width: 272px; font-family: Arial; font-size: 12px; text-align: left;" class="noborder">

<!-----------Hematology-------------------->
	<input name="HematologyProfile" type="checkbox" style="position: absolute; left: 8px; top: 397px;">

	<input name="PTINR" type="checkbox" style="position: absolute; left: 8px; top: 411px;">

        <a href="http://www.bcguidelines.ca/gpac/guideline_warfarin_management.html" style="position: absolute; left: 29px; top: 416px;font-family: Arial; font-weight: bold; font-size: 10px;" target="_blank">PT-INR</a>

	<input name="WarfarinYes" type="checkbox" style="position: absolute; left: 159px; top: 411px;">

	<input name="Ferritin" type="checkbox" style="position: absolute; left: 8px; top: 425px;">

	<input name="IronAndTransferrinSaturation" type="checkbox" style="position: absolute; left: 8px; top: 439px;">

	<input name="SpecialCase" type="checkbox" style="position: absolute; left: 159px; top: 436px;">

	<textarea name="SpecialCaseText" class="noborder" wrap="virtual" style="position: absolute; left: 18px; top:470px; width: 260; height: 36px; font-family: Arial; font-size: 14px"></textarea>

<!-----------Urinalysis/Urine Culture-------->
        <a href="http://www.bcguidelines.ca/gpac/guideline_urinalysis.html"  style="position: absolute; left: 393px; top: 378px; font-family: Arial; font-weight: bold; font-size: 12px;" target="_blank">URINE TESTS</a>

        <a href="http://www.bcguidelines.ca/gpac/guideline_hematuria.html" style="position: absolute; left: 380px; top: 494px; font-family: Arial; font-weight: bold; font-size: 9px;" target="_blank" class="DoNotPrint">microscopic hematuria guideline</a>

	<input name="UrineCulture" type="checkbox" style="position: absolute; left: 280px; top: 397px;">

	<input name="NameOfUrineAbx" type="text" style="position: absolute; left: 300px; top: 413px; width: 278px; height: 23px; font-family: Arial; font-size: 14px; text-align: left;" class="noborder">

	<input name="UrineMacroscopicMicroscopicIfDipstickPositive" type="checkbox" style="position: absolute; left: 280px; top: 430px;">

	<input name="UrineMacroscopicCultureIfPyuriaOrNitrate" type="checkbox" style="position: absolute; left: 280px; top: 445px;">

	<input name="UrineMacroscopicOnly" type="checkbox" style="position: absolute; left: 280px; top: 460px;">

	<input name="UrineMicroscopicOnly" type="checkbox" style="position: absolute; left: 397px; top: 460px;">

	<input name="UrineSpecialCase" type="checkbox" style="position: absolute; left: 333px; top: 475px;">

	<input name="UrinePregnancyTest" type="checkbox" style="position: absolute; left: 280px; top: 490px;">

<!------------Chemistry------------------------->

	<input name="GlucoseFasting" type="checkbox"  style="position: absolute; left: 582px; top: 395px;">

	<input name="GlucoseTimed" type="checkbox"  style="position: absolute; left: 582px; top: 410px;">

	<input name="GlucoseTimedHrPC" type="text" style="position: absolute; left: 655px; top: 410px; width: 30px; height: 22px; font-family: Arial; font-size: 14px; text-align: centre;" class="noborder">

	<input name="GTTGDMScreen" type="checkbox"  style="position: absolute; left: 582px; top: 425px;"
	 onClick="
		document.FormName.PregnantYes.checked=true;
		document.FormName.FastingTime.value=\'10\';
		document.FormName.DiagnosisAndIndications.value += \'Gestational DM screen \';
		document.FormName.PatientInstructions.value +=  \'Nothing to eat or drink, except for water, for 10 hours prior to test. You will be required to remain at the lab for the duration of the test.  Please call and book your appointment.\';">

	<input name="GTTGDMConfirmation" type="checkbox"  style="position: absolute; left: 582px; top: 440px;"
	 onClick="
		document.FormName.PregnantYes.checked=true;
		document.FormName.FastingTime.value=\'10\';
		document.FormName.DiagnosisAndIndications.value += \'Abnormal Gestational DM screen \';
		document.FormName.PatientInstructions.value +=  \'Nothing to eat or drink, except for water, for 10 hours prior to test. You will be required to remain at the lab for the duration of the test.  Please call and book your appointment.\';">

	<input name="GTTnon-Pregnant" type="checkbox"  style="position: absolute; left: 582px; top: 455px;"
	 onClick="
		document.FormName.FastingTime.value=\'10\';
		document.FormName.PatientInstructions.value +=  \'Nothing to eat or drink, except for water, for 10 hours prior to test. You will be required to remain at the lab for the duration of the test.  Please call and book your appointment.\';">

	<input name="A1c" type="checkbox"  style="position: absolute; left: 582px; top: 470px;">

	<input name="ACR" type="checkbox"  style="position: absolute; left: 582px; top: 485px;">


<!-----------Microbiology-------------------------->

<!-----------Routine Culture-------->
	<input name="Antibiotics" type="text" style="position: absolute; left: 105px; top: 547px; width: 173px; height: 22px; font-family: Arial; font-size: 14px; text-align: left;" class="noborder">

	<input name="Throat" type="checkbox"  style="position: absolute; left: 8px; top: 570px;"
	 onClick="
	document.FormName.CollectionTime.value = The12Time;
	document.FormName.CollectionDate.value=document.getElementById(\'today\').value;
	document.FormName.DiagnosisAndIndications.value += \'Sore throat, \';">

        <a href="http://www.bcguidelines.ca/gpac/guideline_throat.html" style="position: absolute; left: 30px; top: 576px; font-family: Arial;  font-size: 10px" target="_blank">Throat</a>

	<input name="Sputum" type="checkbox"  style="position: absolute; left: 77px; top: 570px;">

	<input name="Blood" type="checkbox"  style="position: absolute; left: 140px; top: 570px;">

	<input name="SuperficialWound" type="checkbox"  style="position: absolute; left: 8px; top: 585px;">

	<input name="DeepWound" type="checkbox"  style="position: absolute; left: 77px; top: 585px;">

	<input name="WoundSite" type="text" style="position: absolute; left: 125px; top: 610px; width: 153px; height: 22px; font-family: Arial; font-size: 14px; text-align: left;" class="noborder">

	<input name="Other" type="checkbox"  style="position: absolute; left: 8px; top: 633px;">

	<input name="OtherSpecimen" type="text" style="position: absolute; left: 55px; top: 631px; width: 223px; height: 22px; font-family: Arial; font-size: 14px; text-align: left;" class="noborder">


<!-----------Genital-------->
        <a href="http://www.bcguidelines.ca/gpac/pdf/gen.pdf"  style="position: absolute;  left: 15px; top: 653px; font-family: Arial; font-weight: bold; font-size: 10px;" target="_blank">VAGINITIS</a>

        <a href="http://www.stiresource.com/brochures/pdfs/STI%20Quick%20Reference.pdf" style="position: absolute; left: 80px; top: 653px; font-family: Arial; font-weight: bold; font-size: 9px" target="_blank" class="DoNotPrint">STI Treatment Guidelines</a>

	<input name="VaginitisInitial" type="checkbox" style="position: absolute; left: 8px; top: 665px;"
	onClick="
	document.FormName.CollectionTime.value = The12Time;
	document.FormName.CollectionDate.value=document.getElementById(\'today\').value;
	document.FormName.DiagnosisAndIndications.value += \'Vaginitis, \';">

	<input name="VaginitisChronic" type="checkbox" style="position: absolute; left: 8px; top: 680px;"
	onClick="
	document.FormName.CollectionTime.value = The12Time;
	document.FormName.CollectionDate.value=document.getElementById(\'today\').value;
	document.FormName.DiagnosisAndIndications.value += \'Chronic vaginitis, \';">

	<input name="Trichomonas" type="checkbox"  style="position: absolute; left: 8px; top: 695px;"
	onClick="
	document.FormName.CollectionTime.value = The12Time;
	document.FormName.CollectionDate.value=document.getElementById(\'today\').value;
	document.FormName.DiagnosisAndIndications.value += \'Trichomonas? \';">

	<input name="VaginoAnoRectalGBS" type="checkbox" style="position: absolute; left: 8px; top: 729px;"
	onClick="
	document.FormName.PregnantYes.checked=true;
	document.FormName.CollectionTime.value = The12Time;
	document.FormName.CollectionDate.value=document.getElementById(\'today\').value;
	document.FormName.DiagnosisAndIndications.value += \'GBS screen, \';">

	<input name="PenicillinAllergy" type="checkbox"  style="position: absolute; left: 131px; top: 729px;">

<!-----------Chlamydia (CT) and Gonorrhea (GC)-------->
	<input name="ChlamydiaGC" type="checkbox" style="position: absolute; left: 8px; top: 766px;">

	<input name="CTGCUrethra" type="checkbox" style="position: absolute; left: 97px; top: 777px;"
	onClick="
	document.FormName.CollectionTime.value = The12Time;
	document.FormName.CollectionDate.value=document.getElementById(\'today\').value;">

	<input name="CTGCCervix" type="checkbox" style="position: absolute; left: 155px; top: 777px;"
	onClick="
	document.FormName.CollectionTime.value = The12Time;
	document.FormName.CollectionDate.value=document.getElementById(\'today\').value;">

	<input name="CTGCUrine" type="checkbox" style="position: absolute; left: 206px; top: 777px;">

	<input name="GCOnly" type="checkbox" style="position: absolute; left: 8px; top: 792px;">

	<input name="GCOnlyThroat" type="checkbox" style="position: absolute; left: 97px; top: 792px;"
	onClick="
	document.FormName.CollectionTime.value = The12Time;
	document.FormName.CollectionDate.value=document.getElementById(\'today\').value;">

	<input name="GCOnlyRectal" type="checkbox" style="position: absolute; left: 155px; top: 792px;"
	onClick="
	document.FormName.CollectionTime.value = The12Time;
	document.FormName.CollectionDate.value=document.getElementById(\'today\').value;">

	<input name="GCOnlyOtherSite" type="checkbox" style="position: absolute; left: 97px; top: 807px;">

	<input name="GCOnlyOtherSiteText" type="text" style="position: absolute; left: 155px; top: 807px; width: 123px; height: 24px; font-family: Arial; font-size: 14px; text-align: left;" class="noborder">

<!-----------Stool Specimens-------->
        <a href="http://www.bcguidelines.ca/gpac/guideline_diarrhea.html" style="position: absolute; left: 15px; top: 828px; font-family: Arial; font-weight: bold; font-size: 10px;" target="_blank">STOOL SPECIMENS</a>

	<input name="HxBloodyStools" type="checkbox" style="position: absolute; left: 114px; top: 840px;">

	<input name="CDToxin" type="checkbox" style="position: absolute; left: 8px; top: 855px;">

	<input name="StoolCS" type="checkbox" style="position: absolute; left: 8px; top: 870px;">

	<input name="StoolOP" type="checkbox" style="position: absolute; left: 8px; top: 885px;">

	<input name="StoolOPHighRisk" type="checkbox" style="position: absolute; left: 8px; top: 900px;">

<!-----------Dermatophytes-------->
	<input name="DermatophyteCulture" type="checkbox"  style="position: absolute; left: 8px; top: 930px;">

	<input name="FungusKOHPrep" type="checkbox" style="position: absolute; left: 151px; top: 930px;">

	<input name="SpecimenSkin" type="checkbox"  style="position: absolute; left: 98px; top: 945px;">

	<input name="SpecimenNail" type="checkbox" style="position: absolute; left: 151px; top: 945px;">

	<input name="SpecimenHair" type="checkbox" style="position: absolute; left: 205px; top: 945px;">

	<input name="DermatophyteSite" type="text" style="position: absolute; left: 55px; top: 962px; width: 223px; height: 24px; font-family: Arial; font-size: 14px; text-align: left;" class="noborder">


<!-----------Mycology-------->
	<input name="YeastCulture" type="checkbox" style="position: absolute; left: 8px; top: 993px;">

	<input name="FungusCulture" type="checkbox" style="position: absolute; left: 96px; top: 993px;">

	<input name="YeastFungusSpecimenSite" type="text" style="position: absolute; left: 180px; top: 992px; width: 98px; font-family: Arial; font-size: 14px; text-align: left;" class="noborder">

<!-----------Hepatitis Serology-------->
        <a href="http://www.bcguidelines.ca/gpac/pdf/vihep.pdf" style="position: absolute; left: 285px; top: 536px; font-family: Arial; font-weight: bold; font-size: 9.1px;" target="_blank">HEPATITIS SEROLOGY</a>

	<input name="AcuteViralHepatitis" type="checkbox" style="position: absolute; left: 280px; top: 547px;">

	<input name="ChronicViralHepatitis" type="checkbox" style="position: absolute; left: 280px; top: 601px;">

	<input name="HepatitisAImmuneStatus" type="checkbox" style="position: absolute; left: 280px; top: 658px;">

	<input name="HepatitisBImmuneStatus" type="checkbox" style="position: absolute; left: 280px; top: 671px;">

	<input name="HBsAg" type="checkbox" style="position: absolute; left: 280px; top: 700px;">

	<input name="HIVNominal" type="checkbox" style="position: absolute; left: 280px; top: 766px;">

	<input name="HIVNonNominal" type="checkbox" style="position: absolute; left: 389px; top: 766px;">

<!-----------Lipids-------->
        <a href="http://www.bcguidelines.ca/gpac/guideline_cvd.html#top" style="position: absolute; left: 586px; top: 516px;font-family: Arial; font-weight: bold; font-size: 9.1px;" target="_blank">LIPIDS</a>

	<input name="LipidProfileMSP" type="checkbox" style="position: absolute; left: 582px; top: 558px;"
	 onClick="
		document.FormName.PatientInstructions.value +=  \'Nothing to eat or drink, except for water, for 10 hours prior to test.\';">

	<input name="ApoB" type="checkbox" style="position: absolute; left: 582px; top: 585px;">

	<input name="LipidProfileNon-MSP" type="checkbox" style="position: absolute; left: 582px; top: 609px;"
	 onClick="
		document.FormName.PatientInstructions.value +=  \'Nothing to eat or drink, except for water, for 10 hours prior to test.\';">

<!-----------Thyroid Function-------->
        <a href="http://www.bcguidelines.ca/gpac/guideline_thyroid.html" style="position: absolute; left: 586px; top: 637px;font-family: Arial; font-weight: bold; font-size: 9.1px;" target="_blank">THYROID FUNCTION</a>

	<input name="TSH" type="checkbox" style="position: absolute; left: 582px; top: 682px;">

	<input name="SuspectHyperthyroidism" type="checkbox" style="position: absolute; left: 582px; top: 697px;">

	<input name="MonitorThyroidRx" type="checkbox" style="position: absolute; left: 582px; top: 712px;"
	onClick="
	document.FormName.DiagnosisAndIndications.value += \'Hypothyroid, \';
	document.FormName.CurrentMedicationsLastDose.value +=  \'Thyroxin, \';">

<!-----------Other Chemistry Tests-------->
	<input name="Sodium" type="checkbox"style="position: absolute; left: 582px; top: 750px;">

	<input name="Albumin" type="checkbox" style="position: absolute; left: 651px; top: 750px;">

	<input name="CreatinineGFR" type="checkbox" style="position: absolute; left: 722px; top: 750px;">

	<input name="Potassium" type="checkbox" style="position: absolute; left: 582px; top: 765px;">

	<input name="AlkPhos" type="checkbox" style="position: absolute; left: 651px; top: 765px;">

	<input name="Calcium" type="checkbox" style="position: absolute; left: 722px; top: 765px;">

	<input name="ALT" type="checkbox" style="position: absolute; left: 651px; top: 780px;">

	<input name="CreatineKinase" type="checkbox" style="position: absolute; left: 722px; top: 780px;">

	<input name="Bilirubin" type="checkbox" style="position: absolute; left: 651px; top: 795px;">

	<input name="PSABillable" type="checkbox" style="position: absolute; left: 722px; top: 795px;">

	<input name="GGT" type="checkbox" style="position: absolute; left: 651px; top: 810px;">

	<input name="PSAPatientMustPay" type="checkbox" style="position: absolute; left: 722px; top: 810px;">

	<input name="TProtein" type="checkbox" style="position: absolute; left: 651px; top: 825px;">

<!-----------Other Chemistry Tests-------->
	<input name="ECG" type="checkbox" style="position: absolute; left: 586px; top: 863px;">

	<input name="Holter" type="checkbox" style="position: absolute; left: 636px; top: 863px;">

        <a href="http://www.bcguidelines.ca/gpac/guideline_ambulatory.html" style="position: absolute; left: 658px; top: 867px; font-family: Arial; font-weight: bold; font-size: 10px;" target="_blank">Holter</a>

	<input name="FOB" type="checkbox" style="position: absolute; left: 708px; top: 863px;">

        <a href="http://www.bcguidelines.ca/gpac/pdf/colorectal_det.pdf" style="position: absolute; left: 729px; top: 867px; font-family: Arial; font-weight: bold; font-size: 10px;" target="_blank">Fecal occult blood</a>

<!-----------Additional test/instructions------------------>
	<textarea name="AdditionalTestInstructions" wrap="virtual" style="position: absolute; left: 285px; top: 885px; width: 575px; height: 95px; font-family: Arial; font-size: 16px;" class="noborder"></textarea>

<!----------Signoff----------------------->
	<b style="position: absolute; left: 290px; top: 995px; font-family: Arial; font-size: 14px; text-align: left; font-weight: bold;">"Electronically signed"</b>

	<input name="LabreqDate" type="text" oscardb=today style="position: absolute; left: 650px; top: 995px; width: 210px; height: 22px; font-family: Arial; font-size: 14px; text-align: left;" class="noborder">

<!-----------Collection Detail-------------->
	<input name="CollectionDate" type="text" style="position: absolute; left: 18px; top: 1030px; width: 120px; height: 20px; font-family: Arial; font-size: 12px; text-align: left;" class="noborder">

	<input name="CollectionTime" type="text" style="position: absolute; left: 154px; top: 1030px; width: 120px; height: 20px; font-family: Arial; font-size: 12px; text-align: left;" class="noborder">

	<input name="Phlebotomist" type="text" style="position: absolute; left: 288px; top: 1030px; width: 250px; height: 20px;  font-family: Arial; font-size: 12px; text-align: left;" class="noborder">

	<input name="TelephoneRequisitionReceivedBy" type="text" style="position: absolute; left: 565px; top: 1030px; width: 295px; height: 20px;  font-family: Arial; font-size: 12px; text-align: left;" class="noborder">

<!-----------Patient Instructions-------------->
	<textarea name="PatientInstructions" wrap="virtual" style="position: absolute; left: 18px; top: 1062px; width: 842px; height: 45px; font-family: Arial; font-size: 16px;" class="noborder"></textarea>

<!-- ----------------- End of Text/Textfields/Checkboxes ---------------------------------------- -->

<!-- The submit/print/reset buttons -->
<div class="DoNotPrint" style="position: absolute; left: 18px; top: 1170px;">
	<table><tr><td>
		Subject: <input name="subject" size="40" type="text"> 
		<input value="Submit" name="SubmitButton" id="SubmitButton" type="button" onclick="releaseDirtyFlag();setTimeout(\'document.FormName.submit()\',1000);"> 
		<input value="Reset" name="ResetButton" id="ResetButton" type="button" onclick="document.FormName.reset();"> 
		<input value="Print" name="PrintButton" id="PrintButton" type="button" onclick="formPrint();"> 
		<input value="Print & Submit" name="PrintSubmitButton" id="PrintSubmitButton" type="button" onclick="formPrint();releaseDirtyFlag();setTimeout(\'document.FormName.submit()\',1000);"> 
	</td></tr></table>
</div>
</form>

<!-- -----------Pop-up menu items ------------------------ -->

<div id="LabQuickSelect" class="DoNotPrint" style="position:fixed; left:880px; top:5px; width:210px; background-color:LightYellow; border-style:solid; border-color:black; border-width:1px; font-family: sans-serif; font-size:10;">
<form name="LabQuickSelect">

<!-------------Lab Locations---------------->
<u>LOCATION:</u><br>
<!---insert new-Dropdown  Menu for Lab Locations---------------------->

<form name="menuform">

<select id="LabList">
	<option>Choose a lab location</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 208-3001 Gordon Ave,\nCoquitlam, BC\n(604) 464-1814\nFax: (604) 464-8537.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-5:00; Sat 7:00-1:00\';">Lifelab-Coq\'m-Gordon</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 536 Clarke Road,\nCoquitlam, BC\n(604) 936-7355\nFax: (604) 516-2216.\'; document.FormName.LabHours.value += \'Mon-Fri 7:30-5:00; Sat 7:30-12:00\';">Lifelab-Coq\'m-Burquitlam</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 207-1194 Lansdowne Drive,\nCoquitlam, BC\n(604) 944-1324\nFax: (604) 468-4359.\'; document.FormName.LabHours.value += \'Mon-Fri 7:30-4:00; Sat 7:00-12:00\';">BC Bio-Coq\'m-Lansdowne</option>
			        
	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 106-1015 Austin Ave,\nCoquitlam, BC\n(604) 937-3913\nFax: (604) 937-3849.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-5:00; Sat 7:00-12:00\';">BC Bio-Coq\'m-Austin</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, Suite R-435 North Road (Cariboo Centre), Coquitlam, BC\n(604) 939-7362 Fax: (604) 939-2073.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:30; Sat 7:00-12:00\';">BC Bio-Coq\'m-North Road</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 101-2624 St. Johns St,\nPort Moody, BC\n(604) 931-5644\nFax: (604) 931-1284.\'; document.FormName.LabHours.value += \'Mon-Fri 8:30-12:30; Sat Closed\';">BC Bio-Port Moody-St. John\'s</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 7-2185 Wilson Ave,\nPort Coquitlam, BC\n(604) 944-7754\nFax: (604) 941-0543.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-5:00; Sat 7:00-12:00\';">BC Bio-PoCo Wilson</option>

        <option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 115-1465 Salisbury Ave,\nPort Coquitlam, BC\n(604) 941-4313\nFax: (604) 941-0514.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:00; Sat Closed\';">BC Bio-PoCo Salisbury</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 102-12195 Harris Road,\nPitt Meadows, BC\n(604) 465-7873\nFax: (604) 465-0493.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:00; Sat Closed\';">BC Bio-Pitt Meadows</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 101-11743 - 224 St,\nMaple Ridge, BC\n(604) 467-5141\nFax: (604) 467-3685.\'; document.FormName.LabHours.value += \'Mon-Fri 6:30-5:00; Sat 7:00-12:00\';">BC Bio-Maple Ridge</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 508-625 Fifth Ave,\nNew Westminster, BC\n(604) 526-2831\nFax: (604) 523-3417.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-5:00; Sat 7:00-12:00\';">BC Bio-NW 5th Ave</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 103-301 E. Columbia St,\nNew Westminster, BC\n(604) 522-8941\nFax: (604) 522-9917.\'; document.FormName.LabHours.value += \'Mon-Fri 8:30-4:30; Sat: Closed\';">BC Bio-NW Columbia</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 104-7885 Sixth St,\nBurnaby, BC\n(604) 526-0205\nFax: (604) 526-0209.\'; document.FormName.LabHours.value += \'Mon-Fri 7:30-4:00; Sat Closed\';">BC Bio-Bby Square</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 201-4980 Kingsway (Nelson+Bennett), Burnaby, BC\n(604) 433-6511\nFax: (604) 433-5834.\'; document.FormName.LabHours.value += \'Mon-Fri 6:30-6:00; Sat 7:00-2:00\';">BC Bio-Bby Metrotown</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 206-6411 Nelson Ave,\nBurnaby, BC\n(604) 435-5149\nFax: (604) 431-0479.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-3:00; Sat Closed\';">BC Bio-Bby Nelson</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 203-6542 E. Hastings St,\nBurnaby, BC\n(604) 294-6686\nFax: (604) 294-6652.\'; document.FormName.LabHours.value += \'Mon-Fri 7:30-3:30; Sat 7:00-12:00\';">BC Bio-Bby Kensington Sq</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 302-3965 Kingsway,\nBurnaby, BC\n(604) 439-9642\nFax: (604) 437-1289.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-4:30; Sat 7:00-12:00\';">Lifelab-Bby Central Park</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 324 Gilmore Ave,\nBurnaby, BC\n(604) 298-3933\nFax: (604) 205-7043.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-6:00; Sat 7:00-3:00\';">Lifelab-Bby Hastings</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 104-4515 Harvest Dr,\nDelta, BC\n(604) 946-2144\nFax: (604) 502-1738.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-4:00; Sat 7:00-1:00\';">Lifelab-Delta Harvest Dr Ladner</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 114-1077 - 56th St,\nDelta, BC\n(604) 943-7033\nFax: (604) 502-1104.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:00; Sat Closed\';">Lifelab-Delta 56th Tsawwassen</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 201-8425 - 120th St,\nSurrey, BC\n(604) 591-3304\nFax: (604) 599-3925.\'; document.FormName.LabHours.value += \'Mon-Fri 6:30-6:00; Sat 7:00-2:00\';">BC Bio-Delta 84th</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 122-6345 - 120th St,\nSurrey, BC\n(604) 597-7884\nFax: (604) 543-2971.\'; document.FormName.LabHours.value += \'Mon-Fri 7:30-4:00; Sat 7:00-12:00\';">BC Bio-Delta 63rd</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 102-17760 - 56th Ave,\nSurrey, BC\n(604) 576-6111\nFax: (604) 502-2136.\'; document.FormName.LabHours.value += \'Mon-Fri 6:30-5:00; Sat 7:00-2:00\';">Lifelab-Cloverdale</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 113-7130 - 120th St,\nSurrey, BC\n(604) 543-5280\nFax: (604) 543-3280.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:00; Sat 8:00-1:00\';">Lifelab-Surrey Satnam</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 103-9648 - 128th St,\nSurrey, BC\n(604) 585-7404\nFax\nFax: (604) **********.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:00; Sat Closed\';">Lifelab-Surrey Cedar</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 201-12080 Nordel Way,\nSurrey, BC\n(604) 591-6717\nFax: (604) 502-7598.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-5:00; Sat 7:30-3:00\';">Lifelab-Surrey Nordel</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 101-9656 King George Hwy,\nSurrey, BC\n(604) 588-3494\nFax: (604) 584-1396.\'; document.FormName.LabHours.value += \'Mon-Fri 6:30-5:30; Sat 7:00-12:00\';">BC Bio-Surrey King George+96</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 19-15300 - 105th Ave,\nSurrey, BC\n(604) 581-5711\nFax: (604) 584-5714.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-5:00; Sat Closed\';">BC Bio-Surrey Guildford</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 124-13745 - 72nd Ave,\nSurrey, BC\n(604) 591-8618\nFax: (604) 572-0485.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-4:00; Sat 7:00-2:00\';">BC Bio-Surrey 72nd</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 202-13798 - 94A Ave,\nSurrey, BC\n(604) 589-2226\nFax: (604) 589-2260.\'; document.FormName.LabHours.value += \'Mon-Fri 8:30-4:00; Sat Closed\';">BC Bio-Surrey 94A</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 106-8927 - 152nd St,\nSurrey, BC\n(604) 583-4265\nFax: (604) 583-7253.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-4:30; Sat 7:00-2:00\';">BC Bio-Surrey 152nd</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 202-16088 - 84th Ave,\nSurrey, BC\n(604) 572-4359\nFax: (604) 572-4859.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:00; Sat Closed\';">BC Bio-Surrey 84th</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 112-15252 - 32nd Ave,\nSurrey, BC\n(604) 531-7737\nFax: (604) 589-2226.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:00; Sat Closed\';">BC Bio-S Surrey Morgan Creek</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 105-1656 Martin Dr,\nWhite Rock, BC\n(604) 538-4990.\'; document.FormName.LabHours.value += \'Mon-Fri 6:30-5:00; Sat 7:00-1:00\';">Lifelab-White Rock</option>

        <option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 120-15321 - 16th Ave,\nWhite Rock, BC\n(604) 531-0737 Fax: (604) 531-0751.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-5:00; Sat 7:00-12:00\';">BC Bio-White Rock</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 209-5503 - 206th St,\nLangley, BC\n(604) 534-8671\nFax: (604) 532-3017.\'; document.FormName.LabHours.value += \'Mon-Fri 6:30-5:00; Sat 7:00-12:00\';">BC Bio-Langley Douglas Cresc</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 130-19653 Willowbrook Dr,\nLangley, BC\n(604) 534-8667\nFax: (604) 534-9253.\'; document.FormName.LabHours.value += \'Mon-Fri 7:30-3:30; Sat 7:30-3:30\';">BC Bio-Langley Willowbrook</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 105-20103- 40th Ave,\nLangley, BC\n(604) 533-1617\nFax: (604) 533-1631.\'; document.FormName.LabHours.value += \'Mon-Fri 7:30-3:30; Sat Closed\';">BC Bio-Langley 40th</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 102B-20999 - 88th Ave,\nLangley, BC\n(604) 882-0426\nFax: (604) 882-3910.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-5:00; Sat 7:00-12:00\';">BC Bio-Langley Walnut Grove</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 4305 W. 10th Ave,\nVancouver, BC\n(604) 228-9412\nFax: (604) 228-4902.\'; document.FormName.LabHours.value += \'Mon-Fri 8:30-4:30; Sat 8:00-12:30\';">Lifelab-Van Discovery</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 2061 W. 42nd Ave,\nVancouver, BC\n(604) 263-7742\nFax: (604) 261-5374.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:30; Sat 8:00-1:00\';">Lifelab-Van Kerrisdale</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 112-3540 W. 41st Ave,\nVancouver, BC\n(604) 264-9815\nFax: (604) 263-2965.\'; document.FormName.LabHours.value += \'Mon-Fri 8:30-5:00; Sat Closed\';">Lifelab-Van Dunbar</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 290-2184 W. Broadway,\nVancouver, BC\n(604) 738-7911\nFax: (604) 714-5976.\'; document.FormName.LabHours.value += \'Mon-Fri 7:30-5:00; Sat Closed\';">Lifelab-Van Regent</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 104-888 W. 8th Ave,\nVancouver, BC\n(604) 876-7911\nFax: (604) 708-5645.\'; document.FormName.LabHours.value += \'Mon-Fri 8:30-4:00; Sat Closed\';">Lifelab-Van Laurel</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 701-750 W. Broadway,\nVancouver, BC\n(604) 877-1707\nFax: (604) 871-1549.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-5:00; Sat 7:00-3:00\';">Lifelab-Van Fairmont</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 220-943 W. Broadway,\nVancouver, BC\n(604) 734-1826\nFax: (604) 714-0361.\'; document.FormName.LabHours.value += \'Mon-Fri 9:00-5:00; Sat Closed\';">Lifelab-Van W Broadway</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 410-1338 W. Broadway,\nVancouver, BC\n(604) 731-9166\nFax: (604) 731-3214.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-12:00, 1:00-4:00; Sat Closed\';">BC Bio-Van Broadway Plaza</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 203-190 E. 48th Ave,\nVancouver, BC\n(604) 325-8544\nFax: (604) 301-0469.\'; document.FormName.LabHours.value += \'Mon-Fri 8:30-1:00; Sat Closed\';">BC Bio-Van Main+48th</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 6540 Fraser St,\nVancouver, BC\n(604) 325-4814\nFax: (604) 301-0127.\'; document.FormName.LabHours.value += \'Mon-Fri 7:30-5:00; Sat 7:30-3:30\';">Lifelab-Van South Fraser</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 5786 Victoria Dr,\nVancouver, BC\n(604) 324-0728\nFax: (604) 324-0727.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-4:30; Sat 7:00-3:00\';">Lifelab-Van Victoria Dr</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 340-3150 E. 54th Ave,\nVancouver, BC\n(604) 267-2001\nFax: (604) 433-7509.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:00; Sat Closed\';">Lifelab-Van Champlain Square</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 306-1750 E. 10th Ave,\nVancouver, BC\n(604) 873-2651\nFax: (604) 871-0865.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-5:00; Sat 7:00-12:00\';">BC Bio-Van Commercial</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 408 E. Hastings St,\nVancouver, BC\n(604) 738-7301\nFax: (604) 738-7308.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:00; Sat Closed\';">Lifelab-Van 3 Pillars</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 204-180 Keefer St,\nVancouver, BC\n(604) 685-7473\nFax: (604) 915-7029.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-4:30; Sat 7:00-3:00\';">Lifelab-Van Keefer</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 2736 E. Hastings St,\nVancouver, BC\n(604) 253-1914\nFax: (604) 709-1075.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:00; Sat Closed\';">BC Bio-Van E Hastings</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 8677 Granville St,\nVancouver, BC\n(604) 266-7177\nFax: (604) 261-8571.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:00; Sat Closed\';">Lifelab-Van Marpole</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 215-650 W. 41st Ave,\nVancouver, BC\n(604) 261-1022\nFax: (604) 261-7937.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:00; Sat 7:30-3:30\';">Lifelab-Van Oakridge Mall</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 50-809 W. 41st Ave,\nVancouver, BC\n(604) 263-4912\nFax: (604) 263-4921.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-1:00; Sat Closed\';">BC Bio-Van Willow+41st</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 33-5740 Cambie St,\nVancouver, BC\n(604) 327-2033\nFax: (604) 327-6641.\'; document.FormName.LabHours.value += \'Mon-Fri 7:30-4:00; Sat 7:00-12:00\';">BC Bio-Van Cambie+41st</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 4527 Main St,\nVancouver, BC\n(604) 874-1919\nFax: (604) 875-6247.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-3:30; Sat Closed\';">Lifelab-Van Little Mountain</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 206-1160 Burrard St,\nVancouver, BC\n(604) 689-1012\nFax: (604) 689-2947.\'; document.FormName.LabHours.value += \'Mon-Fri 7:30-5:00; Sat Closed\';">BC Bio-Van St. Paul\'s</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 208-1200 Burrard St,\nVancouver, BC\n(604) 684-3668\nFax: (604) 605-0873.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:00; Sat 8:00-1:00\';">Lifelab-Van St. Paul\'s</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 835-777 Hornby St,\nVancouver, BC\n(604) 682-4811\nFax: (604) 915-9059.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-4:00; Sat Closed\';">Lifelab-Van Georgia</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 163-555 W. 12th Ave,\nVancouver, BC\n(604) 709-6131\nFax: (604) 709-6136.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:00; Sat Closed\';">BC Bio-Van-City Square</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 2-1530 W. 7th Ave,\nVancouver, BC\n(604) 738-0414\nFax: (604) 731-4183.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:00; Sat 8:00-1:00\';">Lifelab-Van Cityview</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 108-3195 Granville St,\nVancouver, BC\n(604) 738-9045\nFax: (604) 714-0375.\'; document.FormName.LabHours.value += \'Mon-Fri 9:00-12:30/1:30-4:30; Sat Closed\';">Lifelab-Van Hycroft</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 136 Davie St,\nVancouver, BC\n(604) 687-4334\nFax: (604) 687-4337.\'; document.FormName.LabHours.value += \'Mon-Fri 7:30-3:00; Sat Closed\';">BC Bio-Van-Yaletown</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 4314 Gallant Ave,\nNorth Vancouver, BC\n(604) 929-1360\nFax: (604) 903-9155.\'; document.FormName.LabHours.value += \'Mon-Fri 7:30-12:00; Sat Closed\';">Lifelab-N. Van Deep Cove</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 215-1916 Lonsdale Ave,\nNorth Vancouver, BC\n(604) 980-3621\nFax: (604) 904-2318.\'; document.FormName.LabHours.value += \'Mon-Fri 6:30-5:00; Sat 7:00-3:00\';">Lifelab-N. Van-Lonsdale</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 209-1200 Lynn Valley Rd,\nNorth Vancouver, BC\n(604) 903-4940\nFax: (604) 980-4270.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:00; Sat Closed\';">Lifelab-N. Van-Lynn Valley</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 105-575 - 16th St,\nWest Vancouver, BC\n(604) 903-4920\nFax: (604) 921-4652.\'; document.FormName.LabHours.value += \'Mon-Fri 7:30-5:00; Sat 7:30-3:30\';">Lifelab-W. Van-Hollyburn</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 115-2419 Bellevue Ave,\nWest Vancouver, BC\n(604) 925-2811\nFax: (604) 925-5179.\'; document.FormName.LabHours.value += \'Mon-Fri 7:30-3:30; Sat Closed\';">Lifelab-W. Van-Dundarave</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS, 103-7343 Hurd St,\nMission, BC\n(604) 826-7197\nFax: (604) 820-2735.\'; document.FormName.LabHours.value += \'Mon-Fri 7:30-3:00; Sat Closed\';">BC Bio-Mission</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 17-6451 Buswell St,\nRichmond, BC\n(604) 273-6511\nFax: (604) ********.\'; document.FormName.LabHours.value += \'Mon-Fri 7:00-5:00; Sat 7:00-3:00\';">Lifelab-Richmond-Buswell</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 172-6180 Blundell Rd,\nRichmond, BC\n(604) 713-3130\nFax: (604) ********.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-5:00; Sat Closed\';">Lifelab-Richmond-#2 Road</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 104-3811 Chatham Rd,\nRichmond, BC\n(604) 271-1712\nFax: (604) ********.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:00; Sat Closed\';">Lifelab-Richmond-Steveston</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 107-6051 Gilbert Rd,\nRichmond, BC\n(604) 278-5412\nFax: (604) ********.\'; document.FormName.LabHours.value += \'Mon-Fri 8:00-4:00; Sat Closed\';">Lifelab-Richmond-Crestwood</option>

	<option onClick="document.FormName.LabLocation.value += \'LIFELABS, 200-5791 No. 3 Rd,\nRichmond, BC\n(604) 278-6516\nFax: (604) ********.\'; document.FormName.LabHours.value += \'Mon-Fri 6:30-5:00; Sat 7:00-3:00\';">Lifelab-Richmond-#3 Road</option>

	<option onClick="document.FormName.LabLocation.value +=\'Chilliwack General Hospital,\n45600 Menholm Rd, Chilliwack, BC\n(604) 795-4141 Ext. 614108\'; document.FormName.LabHours.value += \'Mon-Fri 7:30-4:00\';">Chilliwack General Hospital</option>

	<option onClick="document.FormName.LabLocation.value += \'Sardis Outpatient Lab,\n#5 - 6014 Vedder Rd, Chilliwack, BC\n(604) 824-9627\'; document.FormName.LabHours.value += \'Mon-Fri 6:30-5:30; Sat Closed\';">Sardis Outpatient Lab</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS Chilliwack,\n201-9200 Mary St, Chilliwack, BC\n(604) 792-4607\'; document.FormName.LabHours.value += \'Mon-Fri 6:30-5:00 Sat 7:00-12:00\';">BC Bio-Chilliwack</option>

	<option onClick="document.FormName.LabLocation.value += \'BC BIO LABS Aggasiz,\n1 - 7069 Cheam Ave, Aggasiz, BC\n(604) 796-8523 \'; document.FormName.LabHours.value += \'Mon-Fri 7:30-1:00; Sat Closed\';">BC Bio-Agassiz</option>

</select>

<!-------------Copy to Patient----------------------->
        <input type="checkbox" name="PatientCopy"
	onClick="document.FormName.CopyTo.value +=  \'Patient\';
        ">Copy to Patient

        <input type="checkbox" name="Locum"
	onClick="
		document.FormName.LocumName.value=document.getElementById(\'PatientMD\').value;
		">Locum Order
<br>

<!-------------Fasting Instructions---------------->
	<input type="checkbox" name="FastingInfo"
	onClick="
		document.FormName.PatientInstructions.value +=  \'Nothing to eat or drink, except for water, for 10 hours prior to test.\';
		document.FormName.FastingTime.value=\'10\';
	">Fasting Instructions

<!-------------Time Stamp---------------->
	<input type="checkbox" name="TimeStamp"
	onClick="
	document.FormName.CollectionTime.value = The12Time;
	document.FormName.CollectionDate.value=document.getElementById(\'today\').value;
	">Time Stamp<br>

<!-------------CHF---------------->
<a href="http://www.bcguidelines.ca/gpac/guideline_heart_failure_care.html" target="_blank">CHF:</a><br>
	<input type="checkbox" name="CHFBaseline"
	 onClick="
		document.FormName.DiagnosisAndIndications.value += \'CHF, \';
		document.FormName.FastingTime.value=\'10\';
		document.FormName.HematologyProfile.checked=true;
		document.FormName.UrineMacroscopicMicroscopicIfDipstickPositive.checked=true;
		document.FormName.GlucoseFasting.checked=true;
		document.FormName.TSH.checked=true;
		document.FormName.Sodium.checked=true;
		document.FormName.Potassium.checked=true;
		document.FormName.CreatinineGFR.checked=true;
		document.FormName.ALT.checked=true;
		document.FormName.Albumin.checked=true;
		document.FormName.ECG.checked=true;
		document.FormName.PatientInstructions.value +=  \'Nothing to eat or drink, except for water, for 10 hours prior to test.\';
	">Baseline

	<input type="checkbox" name="CHFFollowUp"
	 onClick="
		document.FormName.DiagnosisAndIndications.value += \'CHF, \';
		document.FormName.Sodium.checked=true;
		document.FormName.Potassium.checked=true;
		document.FormName.CreatinineGFR.checked=true;
	">Follow-up<br>

	<input type="checkbox" name="CHFFollowUpStandingQ3months"
	 onClick="
		document.FormName.DiagnosisAndIndications.value += \'CHF, \';
		document.FormName.Sodium.checked=true;
		document.FormName.Potassium.checked=true;
		document.FormName.CreatinineGFR.checked=true;
		document.FormName.AdditionalTestInstructions.value += \'Repeat q3monthly for 2 years, \';
	">Standing Orders Q3/12<br>


<!----------CKD-------------------->
<a href="http://www.bcguidelines.ca/gpac/guideline_ckd.html" target="_blank">CKD:</a><br>
	<input type="checkbox" name="CKDAnnual"
	 onClick="
		document.FormName.DiagnosisAndIndications.value += \'Chronic Kidney Disease, \';
		document.FormName.HematologyProfile.checked=true;
		document.FormName.A1c.checked=true;
		document.FormName.FastingTime.value=\'10\';
		document.FormName.ACR.checked=true;
		document.FormName.LipidProfileMSP.checked=true;
		document.FormName.Sodium.checked=true;
		document.FormName.Potassium.checked=true;
		document.FormName.CreatinineGFR.checked=true;
		document.FormName.Calcium.checked=true;
		document.FormName.Albumin.checked=true;
		document.FormName.IronAndTransferrinSaturation.checked=true;
		document.FormName.AdditionalTestInstructions.value += \'Phosphorus, iPTH, \';
		document.FormName.PatientInstructions.value +=  \'Nothing to eat or drink, except for water, for 10 hours prior to test.\';
	">Annual

	<input type="checkbox" name="CKDQ6months"
	 onClick="
		document.FormName.DiagnosisAndIndications.value += \'Chronic Kidney Disease, \';
		document.FormName.A1c.checked=true;
		document.FormName.ACR.checked=true;
		document.FormName.CreatinineGFR.checked=true;
		document.FormName.AdditionalTestInstructions.value += \'Standing Order:\n A1C q3monthly for 2 years;\n Creatinine, ACR q6monthly for 2 years, \';
	">Standing Order<br>

<!------DM--------------------->
<a href="http://www.bcguidelines.ca/gpac/guideline_diabetes.html" target="_blank">DM:</a><br>
	<input type="checkbox" name="DMAnnual"
	 onClick="
		document.FormName.DiagnosisAndIndications.value += \'Diabetes Mellitus, \';
		document.FormName.A1c.checked=true;
		document.FormName.ACR.checked=true;
		document.FormName.FastingTime.value=\'10\';
		document.FormName.GlucoseFasting.checked=true;
		document.FormName.Sodium.checked=true;
		document.FormName.Potassium.checked=true;
		document.FormName.CreatinineGFR.checked=true;
		document.FormName.LipidProfileMSP.checked=true;
		document.FormName.AdditionalTestInstructions.value += \'Simultaneous fasting glucose meter/lab comparison, \';
		document.FormName.PatientInstructions.value +=  \'Nothing to eat or drink, except for water, for 10 hours prior to test. Please bring your glucometer for calibration\';
	">Annual

	<input type="checkbox" name="DMAnnualStanding"
	 onClick="
		document.FormName.DiagnosisAndIndications.value += \'Diabetes Mellitus, \';
		document.FormName.A1c.checked=true;
		document.FormName.ACR.checked=true;
		document.FormName.FastingTime.value=\'10\';
		document.FormName.GlucoseFasting.checked=true;
		document.FormName.Sodium.checked=true;
		document.FormName.Potassium.checked=true;
		document.FormName.CreatinineGFR.checked=true;
		document.FormName.LipidProfileMSP.checked=true;
		document.FormName.AdditionalTestInstructions.value += \'Simultaneous fasting glucose meter/lab comparison \n \n Standing Order: (repeat all q1year), \';
		document.FormName.PatientInstructions.value +=  \'Nothing to eat or drink, except for water, for 10 hours prior to test. Please bring your glucometer for calibration\';
	">Standing Order<br>

	<input type="checkbox" name="DMFollowup"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Diabetes Mellitus, \';
		document.FormName.A1c.checked=true;
	">A1c

	<input type="checkbox" name="DMStandingQ3months"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Diabetes Mellitus, \';
		document.FormName.A1c.checked=true;
		document.FormName.AdditionalTestInstructions.value += \'Standing Order: repeat q3months for 2 years, \';
	">A1c - Q3/12<br>

<!--------Dyslipidemia--------------->
<u>DYSLIPIDEMIA:</u><br>
	<input type="checkbox" name="CholesterolFBS"
	 onClick="
		document.FormName.DiagnosisAndIndications.value += \'Dyslipidemia, \';
		document.FormName.FastingTime.value=\'10\';
		document.FormName.GlucoseFasting.checked=true;
		document.FormName.ALT.checked=true;
		document.FormName.CreatineKinase.checked=true;
		document.FormName.LipidProfileMSP.checked=true;
		document.FormName.PatientInstructions.value +=  \'Nothing to eat or drink, except for water, for 10 hours prior to test.\';
	">Cholesterol & FBS<br>

	<input type="checkbox" name="DyslipidemiaOnStatin"
	 onClick="
		document.FormName.DiagnosisAndIndications.value += \'Dyslipidemia, \';
		document.FormName.CurrentMedicationsLastDose.value += \'Statin \';
		document.FormName.FastingTime.value=\'10\';
		document.FormName.GlucoseFasting.checked=true;
		document.FormName.LipidProfileMSP.checked=true;
		document.FormName.ALT.checked=true;
		document.FormName.CreatineKinase.checked=true;
		document.FormName.PatientInstructions.value +=  \'Nothing to eat or drink, except for water, for 10 hours prior to test.\';
	">Dyslipidemia Follow-up: On Statin<br>

<!--------HTN-------------------->
<a href="http://www.bcguidelines.ca/gpac/guideline_hypertension.html" target="_blank">HYPERTENSION:</a>
	<input type="checkbox" name="HypertensionAnnual"
	 onClick="
		document.FormName.DiagnosisAndIndications.value += \'Hypertension, \';
		document.FormName.FastingTime.value=\'10\';
		document.FormName.ACR.checked=true;
		document.FormName.HematologyProfile.checked=true;
		document.FormName.GlucoseFasting.checked=true;
		document.FormName.ALT.checked=true;
		document.FormName.CreatineKinase.checked=true;
		document.FormName.LipidProfileMSP.checked=true;
		document.FormName.Sodium.checked=true;
		document.FormName.Potassium.checked=true;
		document.FormName.CreatinineGFR.checked=true;
		document.FormName.PatientInstructions.value +=  \'Nothing to eat or drink, except for water, for 10 hours prior to test.\';
	">Annual<br>

<!-------Other-------------------->
<u>OTHERS:</u><br>
	<input type="checkbox" name="Prenatal"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Pregnant, rule out asymptomatic bacteriuria\';
		document.FormName.HematologyProfile.checked=true;
		document.FormName.TSH.checked=true;
		document.FormName.HBsAg.checked=true;
		document.FormName.UrineMacroscopicMicroscopicIfDipstickPositive.checked=true;
		document.FormName.UrineCulture.checked=true;
		document.FormName.HIVNominal.checked=true;
		document.FormName.PregnantYes.checked=true;
		document.FormName.AdditionalTestInstructions.value += \'Rubella IgG, Syphilis screen \';
	">First Prenatal Labs<br>

	<input type="checkbox" name="RhNegative"
	onClick="
		document.FormName.PregnantYes.checked=true;
		document.FormName.AdditionalTestInstructions.value += \'Rh antibodies \';
	">Rh negative<br>

	<input type="checkbox" name="Autoimmune"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'?autoimmune problem, \';
		document.FormName.AdditionalTestInstructions.value=document.FormName.AdditionalTestInstructions.value+\'ANA, RF, \';
	">Autoimmune
<a href="http://www.bcguidelines.ca/gpac/guideline_ana_testing.html" target="_blank">ANA, </a>
<a href="http://www.bcguidelines.ca/gpac/guideline_ra.html" target="_blank">RF </a><br>

	<input type="checkbox" name="Celiac"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Query Celiac disease, \';
		document.FormName.AdditionalTestInstructions.value += \'anti-TTG IgA for celiac disease, \';
	"><font style="cursor:pointer; color:blue;" onclick="
		document.getElementById(\'SupplementalInfo\').style.visibility=\'visible\';
		document.AdditionalInformation.AdditionalInfo.value +=  \'IgA anti-TTG is generally considered to be the test of choice to screen patients for celiac disease (see Physicians Newsletter April 2002) because of its sensitivity (>90-96%) and specificity (>95%). Selective IgA deficiency, which is more common in celiac disease patients than the general population (1 in 40 vs. 1 in 400), may lead to false negative IgA anti-TTG tests. Many, but not all, IgA deficient patients can be identified using the optical density of the IgA anti-TTG result such that low optical density results suggest the possibility of IgA deficiency. BC Biomedical Laboratories will continue with the practice of reflexing an IgA quantitation on samples with a low optical density anti-TTG result. However, since this protocol will only identify about 90% of IgA deficient patients, physicians should be aware that some IgA deficient celiac patients may still be missed. The IgA quantitation will not be billed to MSP as the new billing rule does not allow submission of simultaneous claims for both tests collected together.\n\nReference: BC Bio Physicians Newsletter Volume 12, Issue 3 \n\n\';
		">Celiac disease</font><br>

	<input type="checkbox" name="CTDSerology"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Connective tissue disease, \';
		document.FormName.AdditionalTestInstructions.value += \' RA factor, ANA, ENA, CRP, Anti-CCP \';
		document.FormName.HematologyProfile.checked=true;
        ">Connective Tissue Disease Serology<br>

	<input type="checkbox" name="Dementia"
	 onClick="
		document.FormName.DiagnosisAndIndications.value +=\'Dementia, \';
		document.FormName.FastingTime.value=\'10\';
		document.FormName.HematologyProfile.checked=true;
		document.FormName.GlucoseFasting.checked=true;
		document.FormName.TSH.checked=true;
		document.FormName.Sodium.checked=true;
		document.FormName.Potassium.checked=true;
		document.FormName.CreatinineGFR.checked=true;
		document.FormName.ALT.checked=true;
		document.FormName.GGT.checked=true;
                document.FormName.UrineMacroscopicMicroscopicIfDipstickPositive.checked=true;
		document.FormName.UrineMacroscopicCultureIfPyuriaOrNitrate.checked=true;
		document.FormName.AdditionalTestInstructions.value += \'ANA, B12, \';
		document.FormName.PatientInstructions.value +=  \'Nothing to eat or drink, except for water, for 10 hours prior to test.\';
	">Dementia<br>

<input type="checkbox" name="Eating disorder workup"
	 onClick="
		document.FormName.DiagnosisAndIndications.value += \'Eating disorder, \';
		document.FormName.FastingTime.value=\'10\';
		document.FormName.HematologyProfile.checked=true;
		document.FormName.GlucoseFasting.checked=true;
		document.FormName.LipidProfileMSP.checked=true;
		document.FormName.TSH.checked=true;
		document.FormName.Ferritin.checked=true;
		document.FormName.UrineMacroscopicMicroscopicIfDipstickPositive.checked=true;
		document.FormName.UrineMacroscopicCultureIfPyuriaOrNitrate.checked=true;
		document.FormName.Sodium.checked=true;
		document.FormName.Potassium.checked=true;
		document.FormName.CreatinineGFR.checked=true;
		document.FormName.Calcium.checked=true;
		document.FormName.ALT.checked=true;
		document.FormName.GGT.checked=true;
		document.FormName.AlkPhos.checked=true;
		document.FormName.Bilirubin.checked=true;
		document.FormName.Albumin.checked=true;
		document.FormName.TProtein.checked=true;
		document.FormName.ECG.checked=true;
		document.FormName.AdditionalTestInstructions.value += \'Mg, PO4,\n \';
              	document.FormName.ProtocolTextbox.value +=  \'PATIENT INSTRUCTIONS:  Nothing to eat or drink, except for water, for 10 hours prior to test.\';
	">Eating disorder workup<br>

	<input type="checkbox" name="Fatigue"
	 onClick="
		document.FormName.DiagnosisAndIndications.value += \'Fatigue, \';
		document.FormName.FastingTime.value=\'10\';
		document.FormName.HematologyProfile.checked=true;
		document.FormName.GlucoseFasting.checked=true;
		document.FormName.TSH.checked=true;
		document.FormName.Ferritin.checked=true;
		document.FormName.Sodium.checked=true;
		document.FormName.Potassium.checked=true;
		document.FormName.CreatinineGFR.checked=true;
		document.FormName.ALT.checked=true;
		document.FormName.GGT.checked=true;
		document.FormName.UrineMacroscopicMicroscopicIfDipstickPositive.checked=true;
		document.FormName.UrineMacroscopicCultureIfPyuriaOrNitrate.checked=true;
		document.FormName.AdditionalTestInstructions.value += \'CRP, \';
		document.FormName.PatientInstructions.value += \'Nothing to eat or drink, except for water, for 10 hours prior to test. You will be asked to provide a urine sample.\';
	">Fatigue<br>

<input type="checkbox" name="Gyn Ca"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'?Gyne Ca, \';
		document.FormName.AdditionalTestInstructions.value += \'CA125, CEA, CA19-9, CA15-3, \';
	">Gyn Cancer

<br>
	<input type="checkbox" name="HP Breath Test"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Query peptic ulcer disease, \';
		document.FormName.AdditionalTestInstructions.value += \'HP Breath Test\';
		document.FormName.FastingTime.value=\'4\';
		document.FormName.PatientInstructions.value += \'Do not eat or drink anything (except water) for 4 hours before the test. Testing may take up to 40 minutes.\';
	">HP Breath Test/Urea Breath Test<br>

	<u>INFERTILITY:</u>

	<input type="checkbox" name="Infertility-Female"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Female infertility, \';
		document.FormName.HematologyProfile.checked=true;
	        document.FormName.TSH.checked=true;
		document.FormName.HBsAg.checked=true;
		document.FormName.HIVNominal.checked=true;
		document.FormName.AdditionalTestInstructions.value += \' Cycle day 3 FSH, LH, DHEAS, Testosterone, Estradiol, PRL,\n Rubella, Blood group, Rh and Antibody Screen,\n Hep C, VDRL \';
		document.FormName.PatientInstructions.value += \'Do blood work in the 3rd day of your cycle/period.\';
        ">Female

	<input type="checkbox" name="Infertility-Male"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Male infertility, \';
                document.FormName.HematologyProfile.checked=true;
		document.FormName.HIVNominal.checked=true;
		document.FormName.TSH.checked=true;
		document.FormName.AdditionalTestInstructions.value += \'FSH, LH, Prolactin, Testosterone, \n  Semenalysis, \n Karyotype\';
       	">Male<br>

	<input type="checkbox" name="INRStanding"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'On Warfarin, \';
		document.FormName.PTINR.checked=true;
		document.FormName.WarfarinYes.checked=true;
		document.FormName.AdditionalTestInstructions.value += \'Standing Order: INR PRN for 2 years \';
	">INR - Standing Order<br>

	<input type="checkbox" name="LFT"
	onClick="
		document.FormName.ALT.checked=true;
		document.FormName.GGT.checked=true;
		document.FormName.AlkPhos.checked=true;
		document.FormName.Bilirubin.checked=true;
		document.FormName.Albumin.checked=true;
		document.FormName.TProtein.checked=true;
	">LFTs<br>

	<u>ELEVATED LFTs</u>
	<input type="checkbox" name="Acute Elevated LFT"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Acute Elevated LFT\';
                document.FormName.HematologyProfile.checked=true;
                document.FormName.PTINR.checked=true;
		document.FormName.ALT.checked=true;
		document.FormName.GGT.checked=true;
		document.FormName.AlkPhos.checked=true;
		document.FormName.Bilirubin.checked=true;
		document.FormName.Albumin.checked=true;
		document.FormName.TProtein.checked=true;
		document.FormName.AcuteViralHepatitis.checked=true;
	">Acute 

	<input type="checkbox" name="Chronic Elevated LFT"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Chronic Elevated LFT\';
               	document.FormName.FastingTime.value=\'10\';
                document.FormName.GlucoseFasting.checked=true;
		document.FormName.LipidProfileMSP.checked=true;
		document.FormName.Ferritin.checked=true;
		document.FormName.ChronicViralHepatitis.checked=true;
		document.FormName.AdditionalTestInstructions.value=document.FormName.AdditionalTestInstructions.value+= \'ANA, anti-mitochondrial AB, ceruloplasmin, alpha-1 antitrypsin \';
		document.FormName.ProtocolTextbox.value +=  \'PATIENT INSTRUCTIONS:  Nothing to eat or drink, except for water, for 10 hours prior to test.\';
	">Chronic <br>

	<input type="checkbox" name="Chronic Hepatitis B Follow-up"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Chronic Hepatitis B\';
		document.FormName.AdditionalTestInstructions.value=document.FormName.AdditionalTestInstructions.value+= \' AFP, ALT   \n Register for Q 3 months for 2 years\';
	">Chronic Hep B follow-up<br>

	<u>OSTEOPOROSIS:</u>
	<input type="checkbox" name="FemaleOsteoporosis"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Osteoporosis \';
		document.FormName.HematologyProfile.checked=true;
	        document.FormName.TSH.checked=true;
		document.FormName.Calcium.checked=true;
		document.FormName.AlkPhos.checked=true;
		document.FormName.AdditionalTestInstructions.value += \' Phosphate, \n serum protein electrophoresis, \';
        ">Female 

	<input type="checkbox" name="MaleOsteoporosis"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Male osteoporosis \';
	        document.FormName.TSH.checked=true;
		document.FormName.HematologyProfile.checked=true;
		document.FormName.Sodium.checked=true;
		document.FormName.Potassium.checked=true;
		document.FormName.CreatinineGFR.checked=true;
		document.FormName.Calcium.checked=true;
		document.FormName.AlkPhos.checked=true;
		document.FormName.AdditionalTestInstructions.value += \' Phosphate, 25-Hydroxyvitamin D, total and bio-available testosterone, iPTH, urinary calcium excretion \';
		document.FormName.PatientInstructions.value += \'Do blood work in the morning, before 10:00 a.m.\';
        ">Male<br>

	<input type="checkbox" name="PCOS workup"
	 onClick="
		document.FormName.DiagnosisAndIndications.value += \'?PCOS \';
		document.FormName.HematologyProfile.checked=true;
		document.FormName.TSH.checked=true;
		document.FormName.AdditionalTestInstructions.value += \' FSH, LH, Prolactin, \n Total testosterone, DHEAS\';
	">PCOS workup<br>

	<input type="checkbox" name="RenalFunction"
	onClick="
		document.FormName.Sodium.checked=true;
		document.FormName.Potassium.checked=true;
		document.FormName.CreatinineGFR.checked=true;
	">Renal Function<br>

	<input type="checkbox" name="Stool"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Diarrhea \';
		document.FormName.StoolCS.checked=true;
		document.FormName.StoolOP.checked=true;
	">Stool O+P, C+S<br>

	<input type="checkbox" name="Thrombosis screen"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Thrombosis Screen, \';
		document.FormName.HematologyProfile.checked=true;
		document.FormName.PTINR.checked=true;
		document.FormName.TSH.checked=true;
		document.FormName.AdditionalTestInstructions.value += \'factor 5 leiden, protein C, protein S, \n antithrombin 3, DRVVT \';
	">Thrombosis screen<br>

	<input type="checkbox" name="TSH Standing order"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Hypothyroidism, \';               
		document.FormName.MonitorThyroidRx.checked=true;
                document.FormName.AdditionalTestInstructions.value=document.FormName.AdditionalTestInstructions.value+= \'Standing Order: TSH PRN for 2 years\';
	">TSH Standing Order<br>

	<input type="checkbox" name="Urine C&S"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Urinary Tract Infection, \';               
		document.FormName.UrineCulture.checked=true;
	">Urine C&S<br>

	<u>VAGINITIS:</u>
	<input type="checkbox" name="VaginitisInitial"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Vaginal discharge \';
		document.FormName.VaginitisInitial.checked=true;
	">Initial

	<input type="checkbox" name="VaginitisRecurrent"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Vaginal discharge-Recurrent/Chronic \';
		document.FormName.VaginitisChronic.checked=true;
	">Recurrent<br>

	<input type="checkbox" name="VagDischargeCTGC"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Vaginal discharge \';
		document.FormName.ChlamydiaGC.checked=true;
		document.FormName.CTGCCervix.checked=true;
	">Vaginitis CT+GC

	<input type="checkbox" name="VagDischargeTrichomonas"
	onClick="
		document.FormName.Trichomonas.checked=true;
	">Trichomonas<br>

	<input type="checkbox" name="RenalFunctionsRadiology"
	onClick="
		document.FormName.DiagnosisAndIndications.value += \'Check renal function in preparation for IV contrast \';
                document.FormName.CopyTo.value=document.FormName.CopyTo.value+\'RCH Radiology Department \';
		document.FormName.CreatinineGFR.checked=true;
		document.FormName.PatientInstructions.value += \' Cr/eGFR within 90 days pre-procedure and repeated 48 hrs post procedure if required. \';
	">Mucomyst Protocol Lab Tests<br><br>

<font style="cursor:pointer; color:blue;" onclick="document.getElementById(\'TherapeuticMonitoring\').style.visibility=\'visible\'; document.getElementById(\'AdditionalMonitoring\').style.visibility=\'visible\' "><u>THERAPEUTIC MONITORING</u></font><br><br>

	<input value="Reset" name="reset" type="reset">
	<input value="Close" name="Close" type="button" onClick="javascript: closebar(); return false">

</form>
</div>
<!--  -----------Pop-up end----------------------- -->

<!-- -----------Therapeutic Monitoring Items------------------------ -->

<div id="TherapeuticMonitoring" class="DoNotPrint" style="position:fixed; visibility:hidden; left:730px; top:5px; width:150px; height:300px; border-style:solid; border-color:black; border-width:1px; background-color:LightYellow; font-family: sans-serif; font-size:10;">
<form name="MonitoringSelect">

<u>Methotrexate:</u><br>

	<input type="checkbox" name="MethotrexateBaseline"
	 onClick="
		document.FormName.DiagnosisAndIndications.value += \'Methotrexate baseline and monitoring, \';
		document.FormName.HematologyProfile.checked=true;
		document.FormName.CreatinineGFR.checked=true;
		document.FormName.ALT.checked=true;
		document.FormName.AlkPhos.checked=true;
		document.FormName.Bilirubin.checked=true;
		document.FormName.Albumin.checked=true;
		document.FormName.PTINR.checked=true;
		document.FormName.AdditionalTestInstructions.value += \'Standing order:\n CBC, AST, eGFR\n Repeat every 4-8 weeks for 2 years \';
		document.OptionalMonitoring.OptionalTests.value +=  \'Do baseline CXR\nConsider HepB/C serology in high risk patients\nConsider contraception requirements\nCheck adequate folate intake: 1 mg per day or 7 mg per week  \';
			">Baseline<br>

	<input type="checkbox" name="MethotrexateOngoing"
	 onClick="
		document.FormName.DiagnosisAndIndications.value += \'Methotrexate monitoring, \';
		document.FormName.AdditionalTestInstructions.value +=  \'Standing order:\n CBC, AST, eGFR\n Repeat every 4-8 weeks for 2 years \';
		document.OptionalMonitoring.OptionalTests.value +=  \'Do baseline CXR\nConsider HepB/C serology in high risk patients\nConsider contraception requirements\nCheck adequate folate intake: 1 mg per day or 7 mg per week \';
			">Ongoing<br>

<input type="button" value="close" onclick="document.getElementById(\'TherapeuticMonitoring\').style.visibility = \'hidden\';document.getElementById(\'AdditionalMonitoring\').style.visibility = \'hidden\'; ">
<input value="Reset" name="reset" type="reset">

</form>
</div>

<div id="AdditionalMonitoring" class="DoNotPrint" style="position:fixed; visibility:hidden; left:330px; top:5px; width:400px; height:100px; border-style:solid; border-color:black; border-width:1px; background-color:LightYellow; font-family: sans-serif; font-size:10;">
<form name="OptionalMonitoring">
<input value="Reset" name="reset" type="reset">
<textarea name="OptionalTests" id="OptionalTests" class="noborder" wrap="virtual" style="height: 100px; width: 400px;background-color:LightYellow;  font-family: Arial; font-size: 10px;"></textarea>
</div>
</form>

<!-- ---------------------- Add Supplemental Information Here -------------------------- -->

<div id="SupplementalInfo" class="DoNotPrint" style="position:fixed; visibility:hidden; left:330px; top:5px; height: 200px; width: 350px; border-style:solid; border-color:black; border-width:1px; background-color:LightYellow; font-family: sans-serif; font-size:10;">
<form name="AdditionalInformation">
<input type="button" value="close" onclick="document.getElementById(\'SupplementalInfo\').style.visibility = \'hidden\';">
<input value="Reset" name="reset" type="reset">
<textarea name="AdditionalInfo" id="AdditionalInfo" class="noborder" wrap="virtual" style="height: 200px; width: 350px; background-color:LightYellow;  font-family: Arial; font-size: 10px;"></textarea>


</div></body></html>');
