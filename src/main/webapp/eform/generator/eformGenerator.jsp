<%--

    Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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

    This software was written for
    CloudPractice Inc.
    Victoria, British Columbia
    Canada

--%>
<html>
<!-- Eform Generator 0.1.02 -->
<!-- Author: Robert Martin -->
<head>
	<META http-equiv='Content-Type' content='text/html; charset=UTF-8'>
	<title>Oscar E-form Generator</title>
	<!-- import jQuery -->
	<script src="<%= request.getContextPath() %>/share/javascript/jquery/jquery-2.2.4.min.js"></script>
	<!-- import jQuery UI-->
	<script src="<%= request.getContextPath() %>/share/javascript/jquery/jquery-ui-1.12.0.min.js"></script>
	<!-- import jQuery Theme (ui stylesheet) -->
	<link href= "<%= request.getContextPath() %>/share/javascript/jquery/jquery-ui-1.12.0/themes/vader/jquery-ui.min.css" rel="stylesheet">

	<!-- javascript file for the signature pads -- optional -->
	<script src="<%= request.getContextPath() %>/share/javascript/eforms/signature_pad.min.js"></script>

	<script>
		/* Hack to load jquery requirements from jquery site when run outside of oscar */
		var runStandaloneVersion = false;
		if (!window.jQuery) {
			document.write("\x3cscript src='https://code.jquery.com/jquery-2.2.4.min.js'\x3e\x3c\/script\x3e");
			document.write("\x3cscript src='https://code.jquery.com/ui/1.12.0/jquery-ui.min.js'\x3e\x3c\/script\x3e");
			document.write("\x3clink href='https://code.jquery.com/ui/1.12.0/themes/vader/jquery-ui.min.css' rel='stylesheet' \x3e");
			/* local javascript file for the signature pads */
			document.write("\x3cscript src='signature_pad.min.js'\x3e\x3c\/script\x3e");
			runStandaloneVersion = true;
		}
		console.info("Run as standalone version: " + runStandaloneVersion);
	</script>

	<style>
		body {
			margin: 10px;
			background: #d7d7d7;
		}

		#main_container {
			display: flex;
			flex-direction: row;
			flex-wrap: nowrap;
			width: 100%
		}

		#eform_container {
			height: 100%;
			min-height: 100%;
			max-height: calc(100vh - 20px); /*subtract body margin space*/
			overflow: auto;
			width: auto;
			padding: 0 25px 0 0;
		}

		#eform_view_wrapper {
			height: 100%;
		}

		/* pop-in grab bar for resizing eform viewport/controls */
		#eform_view_wrapper > .ui-resizable-e {
			background-color: transparent;
			transition: 2s;
		}

		#eform_view_wrapper > .ui-resizable-e:hover {
			background-color: #f19901;
			transition-delay: 1s;
			transition-duration: 2s;
		}

		@media screen {
			.page_container {
				margin: 0 0 10px 0 !important;
				/* shadows */
				-moz-box-shadow: 3px 3px 5px 0 #a0a0a0;
				-webkit-box-shadow: 3px 3px 5px 0 #a0a0a0;
				box-shadow: 3px 3px 5px 0 #a0a0a0;
			}
		}

		.flexV {
			display: flex;
			flex-direction: column;
		}

		.flexH {
			display: flex;
			flex-direction: row;
		}

		#control {
			padding: 0 10px 0 10px;
			width: auto;
			flex: 1;
			overflow: auto;
			max-height: calc(100vh - 20px); /*subtract body margin space*/
		}

		#control_menu_1-page_setup fieldset {
			flex: 1;
		}

		.gen-control-menu {
			box-sizing: border-box;
		}

		.gen-control-menu label {
			display: inline-block;
			width: 200px;
			text-align: right;
			padding: 2px 5px 2px 5px;
		}

		.gen-control-menu input[type=text] {
			display: inline-block;
			padding: 2px 5px 2px 5px;
			margin: 2px;
			width: calc(100% - 220px); /* 100% - label width + padding */
		}

		.gen-control-menu input[type=checkbox] {
			width: 16px;
			height: 16px;
		}

		/* hack: need to ensure that disabled resizables don't show their handles sometimes. */
		.ui-resizable-disabled .ui-resizable-handle, .ui-resizable-autohide .ui-resizable-handle {
			display: none !important;
		}

		.gen-trash_frame {
			border: dashed #d9534f;
			padding: 3px;
			box-sizing: border-box;
			text-align: center;
			float: left;
			width: 35%;
			min-width: 150px;
			height: 100%;
			max-height: 215px;
			min-height: 215px;
		}

		.gen-trashHover {
			background: rgba(217, 83, 79, 0.75);
		}

		.gen-stitch_frame {
			padding: 10px;
			margin: 10px;
			background: #8198c3;
			color: #fff;
			line-height: 1.3em;
			border: 2px dashed #fff;
			border-radius: 10px;
			box-shadow: 0 0 0 4px #8198c3, 2px 1px 6px 4px rgba(255, 255, 255, 0.5);
		}

		.gen-draggable {
			cursor: default;
		}

		.gen-selectOverflow {
			max-height: 200px;
		}

		.divHighlight {
			background-color: #bcd5eb !important;
			outline: 2px solid #5166bb !important;
		}

		.selectedHighlight {
			outline: 2px solid #0bbb00 !important;
		}

		.gen-snapGuide, .gen-snapLine {
			position: absolute;
			background: transparent;
			border: 0 solid;
			top: 0;
			left: 0;
		}

		.handle {
			position: absolute;
			width: 7px;
			height: 7px;
			top: 0;
			left: 0;
			padding: 0;
			z-index: 90;
		}

		.gen-snapLine.vertical {
			border-right: 1px solid red;
			width: 0;
			height: 100%;
			left: 100%;
			z-index: 90;
		}

		.gen-snapLine.vertical > .handle {
			margin: 0 0 0 -3px;
			height: 100%;
			cursor: e-resize;
		}

		.gen-snapLine.vertical > .ruler {
			background: linear-gradient(to bottom, rgba(255, 0, 0, 0.50) 1px, transparent 0px);
			background-size: 100% 10px;
		}

		.gen-snapLine.horizontal {
			border-bottom: 1px solid red;
			width: 100%;
			height: 0;
			top: 100%;
			z-index: 90;
		}

		.gen-snapLine.horizontal > .handle {
			margin: -3px 0 0 0;
			width: 100%;
			cursor: s-resize;
		}

		.gen-snapLine.horizontal > .ruler {
			background: linear-gradient(to left, rgba(255, 0, 0, 0.50) 1px, transparent 0px);
			background-size: 10px 100%;
		}

		/* only used by eform generator draggables */
		.gen-widget {
			position: relative;
			z-index: 1;
		}

		.gen-widget .inputOverride {
			position: absolute;
			width: 100%;
			height: 100%;
			z-index: 85;
		}

		.gen-widget .inputOverride[disabled] {
			z-index: -1;
		}

		/* highlight inputs that are tagged for various auto-fills on startup */
		input[type="checkbox"][class*="gender_precheck_"] {
			outline: 1px solid rgba(245, 190, 255, 1.0) !important;
		}

		[class*="gender_precheck_"] {
			background-color: rgba(245, 190, 255, 1.0) !important;
		}

		[oscardb] {
			background-color: rgba(245, 190, 255, 0.50) !important;
		}
	</style>

	<style id="eform_style" class="toSource">
		/* base style for pages */
		.page_container {
			position: relative;
			background: #ffffff;
			float: left;
			border: solid 0;
			margin: 0;
		}

		/* div containing form input elements */
		.input_elements {
			position: absolute;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
		}

		/* base styling for input wrapper divs */
		.gen-widget {
			display: inline-block;
			text-align: left;
			vertical-align: top;
			background: transparent;
			border: 0;
		}

		/* style wrapped input elements */
		.gen-widget input, textarea {
			position: absolute;
			display: inline-block;
			text-align: left;
			font-weight: normal;
			font-size: 12px;
			font-family: 'Helvetica', 'Arial', sans-serif;
			background: transparent;
			color: #000000;
			border: 1px solid #d2d2d2;
			padding: 0;
			width: 100%;
			height: 100%;
			z-index: 10;
			margin: 0;
		}

		/* define xbox styling */
		.gen-xBox input {
			background: #f3f3f3;
			text-align: center;
			font-weight: bold;
			font-size: 9px;
			border: 1px solid black;
			cursor: pointer;
		}
		.gen-xBox input:focus {
			outline: none;
			color: transparent;
			text-shadow: 0 0 0 #000;
		}
		/* define custom checkbox styling */
		.gen-checkbox input {
			background: #f3f3f3;
			text-align: center;
			font-weight: bold;
			font-size: 9px;
			border: 1px solid black;
			cursor: pointer;
		}
		.gen-checkbox input:focus {
			outline: none;
			color: transparent;
			text-shadow: 0 0 0 #000;
		}

		/* define other */
		.noborder {
			border-color: transparent !important;
		}

		/* print only styling */
		@media print {
			.DoNotPrint {
				display: none;
			}

			.noborderPrint {
				border-color: transparent !important;
			}

			.page_container {
				page-break-after: always;
			}
		}

		/* define label styling (only used by generated labels) */
		.label-style_1 {
			color: #000000;
			font-size: 12px;
			font-weight: normal;
			font-family: Verdana, Arial, sans-serif;
		}
	</style>
	<style id="eform_style_shapes" class="toSource">
		/* define shape styling
		 * can be safely removed if shapes are not used */
		.circle {
			border-radius: 50%;
			width: 100%;
			height: 100%;
			background: #FFFFFF;
			border: 1px solid black;
		}

		.square {
			border-radius: 0;
			background: #FFFFFF;
			border: 1px solid black;
		}

		.square-rounded {
			border-radius: 15%;
			background: #FFFFFF;
			border: 1px solid black;
		}
	</style>
	<style id="eform_style_signature" class="toSource">
		/* signature pad styling */
		.signaturePad {
			background-color: #efefef;
			opacity: 0.8;
		}

		.signaturePad canvas {
			position: absolute;
			width: 100%;
			height: 100%;
		}

		.signaturePad .canvas_frame {
			position: relative;
			width: 100%;
			height: 100%;
		}

		.signaturePad .clearBtn {
			position: relative;
			float: right;
			padding: 1px;
			line-height: 1em;
			font-size: 14px;
			font-family: monospace;
			text-align: center;
		}

		.signaturePad .clearBtn:active {
			color: red;
		}

		.signaturePad .signature_image {
			display: inline-block;
			position: absolute;
		}

		/* print only styling */
		@media print {
			.signaturePad {
				background-color: transparent;
				opacity: 1;
			}

			.signaturePad canvas { /* always hide canvas for printing. print image instead */
				display: none !important;
			}

			.signature_image { /* always show signature image when printing */
				display: inline-block !important;
			}
		}
	</style>

	<script>
		/** CONSTANTS */
		var CONFIRM_PAGE_REMOVE_TITLE = "Confirm Page Removal";
		var CONFIRM_PAGE_REMOVE_MESSAGE = "You are about the remove a page from the eform. " +
			"This will delete any work on the page and cannot be undone. Are you sure you want to delete the page?";

		var OSCAR_SAVE_MESSAGE_NEW = "Save As New Eform";
		var OSCAR_SAVE_MESSAGE_UPDATE = "Update Eform";

		// make sure .page_container css matches this when modifying
		var eFormPageWidthPortrait = 850;//850
		var eFormPageHeightPortrait = 1100;//1100
		var eFormPageWidthLandscape = 1100;
		var eFormPageHeightLandscape = 800;

		var defaultTextBoxWidth = 256;
		var defaultTextBoxHeight = 16;
		var defaultCheckboxSize = 12;
		var defaultShapeSize = 64;

		var checkboxSizeRange = [1, 128];
		var textBoxSizeRange = [1, 1024];

		var inFirefox = (navigator.userAgent.search("Firefox") >= 0);
		var inChrome = (navigator.userAgent.search("Chrome") >= 0);
		var eFormViewMinWidth = 375;//px
		var eFormViewPadding = 25;//px

		var defaultIncludeFaxControl = true;
		var defaultEnableSnapGuides = true;
		var defaultShowRuler = false;
		var defaultMenuOpenIndex = 1; // control menu accordion tab index

		/* define the base names for generated elements. */
		var baseWidgetName = "gen_widgetId";
		var baseInputName = "gen_inputId";
		var baseBackImageName = "gen_backgroundImage";
		var baseImageWidgetName = "gen_dragImageTemplate";
		var baseSignatureDataName = "gen_signatureData";
		var basePageName = "page_";

		var XBOX_INPUT_SELECTOR = ".xBox,.utf8Check";
		var TEXT_INPUT_SELECTOR = ":input[type=text]:not("+XBOX_INPUT_SELECTOR+"), textarea";
		var CHEK_INPUT_SELECTOR = ":input[type=checkbox]";
		var GENDER_PRECHECK_CLASS_SELECTOR = "[class*=gender_precheck_]";
		var OSCAR_DISPLAY_IMG_SRC = "<%= request.getContextPath() %>/eform/displayImage.do?imagefile=";
		var OSCAR_EFORM_ENTITY_URL = "<%= request.getContextPath() %>/ws/rs/eform/";
		var OSCAR_EFORM_SEARCH_URL = "<%= request.getContextPath() %>/ws/rs/eforms/";

		/** GLOBAL VARIABLES */
		var eformName = "Untitled eForm";
		var textBordersVisibleState = 1;
		var xboxBordersVisibleState = 0;
		var orientationIndex = 0; //portrait 0 vs landscape 1 vs custom 2

		var checkboxSize = defaultCheckboxSize;
		var textBoxWidth = defaultTextBoxWidth;
		var textBoxHeight = defaultTextBoxHeight;
		var eFormPageWidth = eFormPageWidthPortrait;
		var eFormPageHeight = eFormPageHeightPortrait;
		var enableElementHighlights = false;
		var dragAndDropEnabled = false;

		// stores the current eform id. 0 if new eform.
		var eFormFid = 0;

		// store the list of images on the oscar server so only one load is needed.
		var eFormImageList = [];
		/** oscar db tags hardcoded for standalone. list update date: 2018-10-02 */
		var oscarDatabaseTags = [
		    "today", "time", "appt_date", "appt_start_time", "appt_end_time", "appt_location",
            "next_appt_date", "next_appt_time", "nextf_appt_date", "next_appt_location", "current_form_id", "current_form_data_id",
            "current_user", "current_user_fname_lname", "current_user_ohip_no", "current_user_specialty", "current_user_specialty_code",
			"current_user_cpsid", "current_user_id", "current_user_signature", "current_logged_in_user", "current_logged_in_user_address",
			"current_logged_in_user_fax", "current_logged_in_user_work_phone", "current_logged_in_user_roles", "current_logged_in_user_type",
			"current_logged_in_user_id", "current_user_takno", "current_logged_in_user_takno", "patient_name", "first_last_name",
			"patient_nameL", "patient_nameF", "patient_alias", "patient_id", "label", "address", "addressline", "address_street_number_and_name",
			"province", "city", "postal", "dob", "dobc", "dobc2", "dobc3", "dob_year", "dob_month", "dob_day", "NameAddress",
			"hin", "hinc", "hinversion", "hc_type", "hc_renew_date", "chartno", "phone", "phone2", "cell", "phone_extension",
			"phone2_extension", "age", "age_in_months", "ageComplex", "ageComplex2", "sex", "sin", "licensed_producer_by_demographic",
			"licensed_producer2_by_demographic", "licensed_producer_address_name", "licensed_producer_address_name_list",
			"licensed_producer_full_address", "licensed_producer_full_address_list", "multisite_name_list", "multisite_fax_list",
			"multisite_phone_list", "multisite_full_address_list", "multisite_address_list", "multisite_city_list", "multisite_province_list",
			"multisite_postal_list", "medical_history", "other_medications_history", "social_family_history", "ongoingconcerns",
			"reminders", "risk_factors", "family_history", "risk_factors_json", "family_history_json", "dxregistry", "OHIPdxCode",
			"allergies_des", "allergies_des_no_archived", "recent_rx", "today_rx", "druglist_generic", "druglist_trade",
			"druglist_line", "latest_echart_note", "onGTPAL", "onEDB", "bcGTPAL", "bcEDD", "doctor", "doctor_provider_no",
			"doctor_ohip_no", "doctor_specialty_code", "doctor_cpsid", "appt_provider_cpsid", "appt_provider_specialty",
			"doctor_title", "provider_name", "provider_name_first_init", "provider_specialty", "doctor_work_phone", "doctor_fax",
			"doctor_signature", "appt_provider_name", "appt_provider_ohip_no", "appt_provider_id", "appt_no", "referral_name",
			"referral_address", "referral_phone", "referral_fax", "referral_no", "bc_referral_name", "bc_referral_address",
			"bc_referral_phone", "bc_referral_fax", "bc_referral_no", "clinic_name", "clinic_phone", "clinic_fax", "clinic_label",
			"clinic_addressLine", "clinic_addressLineFull", "clinic_address", "clinic_city", "clinic_province", "clinic_postal",
			"clinic_multi_phone", "clinic_multi_fax", "_eform_values_first", "_eform_values_last", "_eform_values_first_all_json",
			"_eform_values_last_all_json", "_eform_values_count", "_eform_values_countname", "_eform_values_count_ref",
			"_eform_values_countname_ref", "_eform_values_count_refname", "_eform_values_countname_refname", "_other_id",
            "dtap_immunization_date", "flu_immunization_date", "fobt_immunization_date", "mammogram_immunization_date",
			"pap_immunization_date", "cytology_no", "guardian_label", "guardian_label2", "email", "service_date", "practitioner",
			"ref_doctor", "fee_total", "payment_total", "refund_total", "balance_owing", "bill_item_number", "bill_item_description",
			"bill_item_service_code", "bill_item_qty", "bill_item_dx", "bill_item_amount", "urine_tox_test_json", "methadone_induction_assessment_json",
            "who_measurements", "family_doctor_name", "family_doctor_last_name", "family_doctor_address", "family_doctor_phone",
			"family_doctor_fax", "family_doctor_no", "bc_family_doctor_name", "bc_family_doctor_address", "bc_family_doctor_phone",
			"bc_family_doctor_fax", "bc_family_doctor_no", "roster_status"
        ];

		var $globalSelectedElement = null;
		var $mouseTargetElement = null;
		var currentMousePos = {x: -1, y: -1};
		var signaturePadLoaded = false;

		/** returns the input string with all non alpha-numeric characters removed */
		function stripSpecialChars(string) {
			return string.replace(/[^a-z0-9\s]/gi, '');
		}
		function destroy_gen_widgets($elementSelector) {
			$elementSelector.find(".gen-resizable").resizable("destroy").removeClass("gen-resizable").addClass("gen-resize-destroyed");
			$elementSelector.find(".gen-draggable").draggable("destroy").removeClass("gen-draggable").addClass("gen-draggable-destroyed");
			$elementSelector.find(".gen-droppable").droppable("destroy").removeClass("gen-droppable").addClass("gen-droppable-destroyed");
		}
		function undestroy_gen_widgets($elementSelector) {
			$elementSelector.find(".gen-draggable-destroyed").each(function () {
				makeDraggable($(this), false, ".gen-layer1, .gen-layer2, .gen-layer3");
				$(this).removeClass("gen-draggable-destroyed");
			});
			$elementSelector.find(".gen-resize-destroyed").each(function () {
				makeResizable($(this));
				$(this).removeClass("gen-resize-destroyed");
			});
			$elementSelector.find(".gen-droppable-destroyed").each(function () {
				makeDroppable($(this), "divHighlight", ".gen-layer2, .gen-layer3", true);
				$(this).removeClass("gen-droppable-destroyed");
			});
		}
		function getOscarDBTags() {
			var dbTagList = null;
			if(runStandaloneVersion) {
				dbTagList = oscarDatabaseTags;
			}
			else {
				$.ajax
				({
					type: "GET",
					url: OSCAR_EFORM_SEARCH_URL + 'databaseTags',
					dataType: 'json',
					async: false,
					success: function (data) {
						var status = data.status;
						if(status === "SUCCESS") {
							dbTagList = data.body;
						}
					},
					failure: function(data) {
						console.error(data);
					}
				});
			}
			return dbTagList;
		}
		function addOscarImagePath(string) {
			if(!runStandaloneVersion) {
				// remove the oscar loading path
				// have to do escape for regex because of changing context path
				var regexFixed = OSCAR_DISPLAY_IMG_SRC.replace(/\//g, "\\/").replace(/\./g, "\\.").replace(/\?/g, "\\?");
				string = string.replace(new RegExp(regexFixed, "g"), "\${oscar_image_path}");
			}
			else {
				string = string.replace(/(img.*?)src\s*=\s*(\"|\')(?!data:image\/png;base64,)/gi, "$1src=$2\${oscar_image_path}");
			}
			return string;
		}
		function removeOscarImagePath(string) {
			if(!runStandaloneVersion) {
				var regexFixed = OSCAR_DISPLAY_IMG_SRC.replace(/\//g, "\\/").replace(/\./g, "\\.").replace(/\?/g, "\\?");
				return string.replace(/\\$(%7B|\{)oscar_image_path(%7D|\})/gi, OSCAR_DISPLAY_IMG_SRC);
			}
			else {
				return string.replace(/\$(%7B|\{)oscar_image_path(%7D|\})/gi, '');
			}
		}
		/** add or remove the current element from the DOM if it doesn't match the given state */
		function toggleElement($root, $element, state) {
			var attached = $.contains(document, $element.get(0));
			if (!state && attached) {
				$element.detach();
			}
			else if (state && !attached) {
				$root.append($element);
			}
		}
		function addHiddenElements(include_fax) {
			var $patientGender = $("#PatientGender");
			var $inputForm = $("#inputForm");
			var count = $inputForm.find(GENDER_PRECHECK_CLASS_SELECTOR).length;
			if ($patientGender.get(0) == null) {
				$patientGender = $("<input>", {
					type: "hidden",
					id: "PatientGender",
					name: "PatientGender"
				}).attr('oscarDB', 'sex');
			}
			toggleElement($inputForm, $patientGender, (count > 0));

			var $faxControl = $("#faxControl");
			if ($faxControl.get(0) == null) {
				$faxControl = $("<div>", {
					id: "faxControl"
				})
			}
			toggleElement($("#BottomButtons"), $faxControl, include_fax);
		}
		/** this function writes input values into the html, to ensure html/jQuery value synchronization */
		function write_values_to_html() {
			var $input_elem = $(".input_elements");
			// preserve checkbox checked state, since jquery does not modify html
			$input_elem.find("input[type=checkbox]").each(function (index) {
				if ($(this).is(':checked')) {
					$(this).attr('checked', "checked");
				}
				else {
					$(this).removeAttr("checked");
				}
			});
			// preserve text info, since jquery does not modify html
			$input_elem.find("input[type=text]").each(function (index) {
				if ($(this).val() && $(this).val().length > 0) {
					$(this).attr("value", $(this).val());
				}
				else {
					$(this).removeAttr("value");
				}
			});
			$input_elem.find("textarea").each(function (index) {
				if ($(this).val() && $(this).val().length > 0) {
					$(this).text($(this).val());
				}
				else {
					$(this).text("");
				}
			});
		}
		function generate_eform_source_html(escapeHtml, include_fax) {
			write_values_to_html();

			var $input_elements = $(".input_elements");
			var $eform_container = $("#eform_container");
			var $signature_data = $eform_container.find(".signature_data");
			if ($globalSelectedElement) $globalSelectedElement.removeClass("selectedHighlight");
			$globalSelectedElement = null;
			destroy_gen_widgets($input_elements);
			addHiddenElements(include_fax);
			// detatch elements so they are not added to generated code
			var detached = [];
			$(".gen-snapGuide,.inputOverride,.signature_image").each(function () {
				var $override = $(this);
				var $parent = $override.parent();
				toggleElement($parent, $override, false);
				detached.push([$parent, $override]);
			});

			var source = "<html><head>";
			source += "\<META http-equiv='Content-Type' content='text/html; charset=UTF-8'\>";
			source += "<title>" + eformName + "</title>";
			source += "\<script src='\$\{oscar_javascript_path\}jquery/jquery-2.2.4.min.js'\>\<\/script\>";
			source += "\<script\>window.jQuery || document.write(\"\\x3cscript src='https://code.jquery.com/jquery-2.2.4.min.js'\\x3e\\x3c\\/script\\x3e\");\<\/script\>";

			if (include_fax) {
				source += "\<script src='\$\{oscar_javascript_path\}eforms/faxControl.js'\>\<\/script\>";
			}
			if ($signature_data.length > 0) {
				source += "\<script src='\$\{oscar_javascript_path\}eforms/signature_pad.min.js'\>\<\/script\>";
			}
			source += "<style>";
			var baseStyle = document.getElementById('eform_style');
			var shapeStyles = document.getElementById('eform_style_shapes');
			var signatureStyles = document.getElementById('eform_style_signature');
			var script = document.getElementById('eform_script');
			var signature_script = document.getElementById('signature_script');
			var htmlElements = document.getElementById('eform_container');

			//source += document.getElementsByTagName('style')[0].innerHTML;
			source += baseStyle.innerHTML;
			if ($eform_container.find(".circle,.square-rounded,.square").length > 0) {
				source += shapeStyles.innerHTML;
			}
			if ($eform_container.find(".signaturePad").length > 0) {
				source += signatureStyles.innerHTML;
			}
			source += "</style><script>" + script.innerHTML;
			if ($eform_container.find(".signaturePad").length > 0) {
				source += signature_script.innerHTML;
			}
			source += "\<\/script></head><body onload='onEformLoad();'><div id='eform_container'" +
				"style='max-width: " + eFormPageWidth + "px'>";
			source += htmlElements.innerHTML;
			source += "</div></body></html>";
			source = addOscarImagePath(source);
			source = source.replace(/>\s*</g, ">\n<");
			//now we need to escape the html special chars
			if (escapeHtml) {
				source = source.replace(/</g, "&lt;").replace(/>/g, "&gt;");
				//now we add <pre> tags to preserve whitespace
				source = "<pre>" + source + "</pre>";
			}
			undestroy_gen_widgets($input_elements);
			dragAndDropEnable(false);
			//toggleElement($container, $grid, true);
			for (var i = 0; i < detached.length; i++) {
				toggleElement(detached[i][0], detached[i][1], true);
			}
			return source;
		}
		function showSource(include_fax) {
			var source = generate_eform_source_html(true, include_fax);
			//now open the window and set the source as the content
			var sourceWindow = window.open('', 'Source of page', 'height=800,width=800,scrollbars=1,resizable=1');
			sourceWindow.document.write(source);
			sourceWindow.document.title = "eForm Source";
			sourceWindow.document.close(); //close the document for writing, not the window
			//give source window focus
			if (window.focus) sourceWindow.focus();
		}
		function download(text, name, type) {
			var a = document.createElement("a");
			var file = new Blob([text], {type: type});
			a.href = URL.createObjectURL(file);
			a.download = name;
			// create mouse event for initiating the download
			var event = document.createEvent("MouseEvents");
			event.initMouseEvent(
				"click", true, false, window, 0, 0, 0, 0, 0
				, false, false, false, false, 0, null
			);
			a.dispatchEvent(event);
		}
		function downloadSource(include_fax) {
			var name = stripSpecialChars(eformName).replace(/\s/g, "_") + '.html';
			return download(generate_eform_source_html(false, include_fax), name, 'text/html');
		}
		/** Save the eform html as a new eform */
		function saveToOscarEforms(include_fax) {
			var eformCode = generate_eform_source_html(false, include_fax);
			var url = OSCAR_EFORM_ENTITY_URL + ((eFormFid > 0)? eFormFid + "/": "") + "json";
			var type = (eFormFid > 0)? "PUT" : "POST";

			$.ajax
                ({
                    type: type,
                    url: url,
                    contentType: "application/json; charset=utf-8",
                    dataType: 'json',
                    async: false,
                    data: JSON.stringify({ "id": eFormFid, "formName": eformName, "formHtml" : eformCode }),
                    success: function (data) {
                        console.info(data);
                        var status = data.status;
						if(status === "SUCCESS") {
							alert("EForm Save Successful!");
							setEformId(data.body.id);
						}
						else {
							alert(data.error);
						}
                    },
                    failure: function(data) {
                        console.error(data);
                        alert("EForm save failure!");
                    }
                });
		}

		/** make the given element draggable */
		function makeDraggable($element, cloneable, stackClasses) {
			$element.draggable({
				appendTo: "body",
				revert: "invalid",
				revertDuration: 500,
				stack: stackClasses,
				scroll: false,
				snap: ".gen-snapLine:visible",
				snapMode: "inner",
				snapTolerance: 10
			});
			if (cloneable) {
				$element.draggable("option", "helper", "clone");
				$element.addClass("gen-cloneable");
			}
			$element.addClass("gen-draggable");
		}
		/** make the given element resizable */
		function makeResizable($element) {
			$element.resizable({
				aspectRatio: ($element.find(':checkbox').length > 0),
				containment: "#inputForm"
			});
			$element.resizable("disable");
			$element.addClass("gen-resizable");
		}
		/** make the given element accept draggable elements */
		function makeDroppable($element, hoverClasses, acceptClasses, greedy) {
			$element.droppable({
				accept: acceptClasses,
				hoverClass: hoverClasses,
				greedy: greedy,
				drop: function (event, ui) {
					dropOnForm(ui, $(this));
				}
			});
			$element.addClass("gen-droppable");
		}
		function makeSignatureCanvas($element) {

			var $canvasFrame = $element.children(".canvas_frame");
			var $clearBtn = $element.children(".clearBtn");
			var canvas = $canvasFrame.children("canvas").get(0);
			var $data = $canvasFrame.children(".signature_data");
			var src = $data.val();
			var $img = $("<img>", {
				src: src,
				class: "signature_image"
			});
			if (src && src.length > 0) {
				$img.appendTo($canvasFrame);
			}

			if (signaturePadLoaded) {
				$img.hide();
				console.info("loading editable signature pads");
				var updateSlaveSignature = function (src_canvas, dest_canvas) {
					// write to the destination with image scaling
					var dest_context = dest_canvas.getContext("2d");
					dest_context.clearRect(0, 0, dest_canvas.width, dest_canvas.height);
					dest_context.drawImage(src_canvas, 0, 0, dest_canvas.width, dest_canvas.height);
				};
				var setCanvasSize = function () {
					canvas.width = $element.width();
					canvas.height = $element.height();
					$element.trigger("signatureChange");
				};
				// initialize the signature pad
				var signPad = new SignaturePad(canvas, {
					minWidth: 2,
					maxWidth: 4,
					onEnd: function () {
						$element.trigger("signatureChange");
					}
				});
				// load the image data to the canvas ofter initialization
				if (src != null && src != "") {
					signPad.fromDataURL(src);
				}
				else {
					setCanvasSize();
				}
				// so that the signature image resizes correctly in generator
				$element.on("resize", setCanvasSize);

				// define a custom update trigger action. this allows the eform to store the signature.
				$element.on("signatureChange", function () {
					$data.val(signPad.toDataURL());
					$img.prop('src', signPad.toDataURL());
					if ($element.attr('slaveSigPad')) {
						var $slavePad = $("#" + $element.attr('slaveSigPad')); // get slave pad by id
						updateSlaveSignature(canvas, $slavePad.find("canvas").get(0));
						$slavePad.trigger("signatureChange"); // be careful of infinite loops
					}
					return false;
				});
				// init the clear button
				$clearBtn.on('click', function () {
					signPad.clear();
					$element.trigger("signatureChange");
					return false;
				});
			}
			// not using the canvas, show signature as an image instead.
			else {
				$img.show();
			}
		}

		function createBasicDraggableDiv(widgetId, width, height, customClasses) {
			return $("<div>", {
				id: widgetId,
				class: "gen-widget " + customClasses,
				width: width + "px",
				height: height + "px"
			});
		}
		function createInputOverrideDiv() {
			return $("<div>", {
				class: "inputOverride"
			});
		}
		function addDraggableInputType($parent, widgetId, type, width, height, customClasses) {
			var $widget = createBasicDraggableDiv(widgetId, width, height, customClasses + " gen-layer3");

			if (type === "textarea") {
				$widget.append($("<textarea>", {
					css: {resize: 'none'},
					class: 'gen_input'
				}));
			}
			else {
				$widget.append($("<input>", {
					type: type,
					class: 'gen_input'
				}));
			}
			$widget.append(createInputOverrideDiv());
			$parent.append($widget);

			makeDraggable($widget, true, ".gen-layer1, .gen-layer2, .gen-layer3");
			return $widget;
		}

		function addDraggableShape($parent, widgetId, width, height, customClasses) {
			var $widget = createBasicDraggableDiv(widgetId, width, height, customClasses + " gen-layer2");
			$parent.append($widget);
			makeDraggable($widget, true, ".gen-layer1, .gen-layer2");
			makeDroppable($widget, "divHighlight", ".gen-layer2, .gen-layer3", true);
			return $widget;
		}
		function addDraggableLabel($parent, widgetId, text, customClasses) {
			var $widget = $("<label>", {
				id: widgetId,
				class: "gen-widget gen-layer3 ui-widget-content " + customClasses,
				text: text
			});
			$parent.append($widget);
			makeDraggable($widget, true, ".gen-layer1, .gen-layer2, .gen-layer3");

			return $widget;
		}
		function addDraggableImage($parent, widgetId, width, height, src, customClasses) {
			var $widget = createBasicDraggableDiv(widgetId, width, height, customClasses + " gen-layer3");
			$widget.append($("<img>", {
				src: src,
				width: "100%",
				height: "100%"
			}));
			$parent.append($widget);
			makeDraggable($widget, true, ".gen-layer1, .gen-layer2");

			return $widget;
		}
		function addDraggableSignaturePad($parent, widgetId, width, height, customClasses) {
			var $widget = createBasicDraggableDiv(widgetId, width, height, customClasses + " gen-layer3");

			var $canvas = $("<canvas>");
			var $canvasData = $("<input>", {
				type: "hidden",
				class: "signature_data"
			});
			var $clearBtn = $("<button>", {
				type: "button",
				text: "clear",
				class: "clearBtn DoNotPrint"
			});
			var $flex = $("<div>", {class: "canvas_frame"});
			$flex.append($canvas).append($canvasData);
			$widget.append($flex).append($clearBtn);
			$flex.append(createInputOverrideDiv());
			$parent.append($widget);
			makeDraggable($widget, true, ".gen-layer1, .gen-layer2");
			return $widget;
		}

		/** generate a unique id for new input elements. */
		function getUniqueId(baseId) {
			var i = 1;
			// if you max this out your eForm is too big anyways
			while (i < 99999) {
				var returnId = baseId + i;

				if (!document.getElementById(returnId)) {
					return returnId;
				}
				i++;
			}
			console.error("unique ID generation failed");
			return undefined;
		}
		/** create a copy of the draggable element at the given position
		 * the clone will not be re-cloneable, and will be given a generated name/id */
		function cloneDraggableAt($newParent, position, $toClone) {

			var $newDraggable = $toClone.clone();
			var id = getUniqueId(baseWidgetName);
			$newDraggable.attr("id", id);
			//$newDraggable.attr("name", id);
			$newDraggable.removeClass("gen-cloneable");
			$newDraggable.appendTo($newParent);
			$newDraggable.css(position);
			// clone is made draggable with all options except the helper.
			$newDraggable.draggable($toClone.draggable("option"));
			$newDraggable.draggable("option", "helper", false);

			// inputs must all have id's and names to work in oscar
			$newDraggable.children(":input").each(function () {
				var id = getUniqueId(baseInputName);
				$(this).attr({
					id: id,
					name: id
				});
			});
			// init signature pads when cloning
			if ($newDraggable.hasClass("signaturePad")) {
				// inputs must all have id's and names to work in oscar
				$newDraggable.find(".signature_data").each(function () {
					var id = getUniqueId(baseSignatureDataName);
					$(this).attr({
						id: id,
						name: id
					});
				});
				makeSignatureCanvas($newDraggable);
			}
			setNoborderStyle($newDraggable.find(XBOX_INPUT_SELECTOR), xboxBordersVisibleState);
			setNoborderStyle($newDraggable.find(TEXT_INPUT_SELECTOR), textBordersVisibleState);
			return $newDraggable;
		}
		/** recursively clone widget elements and attach them to the parent selector */
		function cloneWidgetAt($newParent, position, $toClone) {
			var isResizable = $toClone.data('uiResizable');
			var $childWidgets = $toClone.children(".gen-widget");
			// remove resizable to prevent errors
			if (isResizable) {
				$toClone.resizable("destroy").removeClass("gen-resizable");
			}
			// clone the element
			var $clone = cloneDraggableAt($newParent, position, $toClone);
			//remove duplicated children elements (they won't be draggable etc)
			$clone.children(".gen-widget").remove();
			// clone all child widgets and attach them to the new clone
			$childWidgets.each(function () {
				var xPos = $(this).position().left;
				var yPos = $(this).position().top;
				var pos = {top: yPos, left: xPos, position: "absolute"};
				cloneWidgetAt($clone, pos, $(this));
			});
			if (isResizable) {
				makeResizable($toClone);//re-enable the resizable
			}
			makeResizable($clone);//make clone resizable
			if ($toClone.data('uiDroppable')) {
				$clone.droppable($toClone.droppable("option"));
			}
			return $clone;
		}

		/** drop a draggable object onto a droppable object, and clone/update the draggable parent */
		function dropOnForm(ui, $new_parent) {
			var $draggable = $(ui.draggable);
			var $old_parent = $draggable.parent();

			if ($draggable.hasClass("gen-cloneable")) {
				var xPos = ui.helper.offset().left - $new_parent.offset().left;
				var yPos = ui.helper.offset().top - $new_parent.offset().top;
				var pos = {top: yPos, left: xPos, position: "absolute"};
				cloneWidgetAt($new_parent, pos, $draggable);
			}
			else if (!($new_parent.is($old_parent))) {
				var xPos = $old_parent.offset().left + ui.helper.position().left - $new_parent.offset().left;
				var yPos = $old_parent.offset().top + ui.helper.position().top - $new_parent.offset().top;
				var pos = {top: yPos, left: xPos, position: "absolute"};
				$draggable.appendTo($new_parent).css(pos);

			}
		}
		/** set up a simple fixed size frame div */
		function createTrashFrame() {
			return $("<div>", {
				class: "gen-trash_frame"
			});
		}
		/** set up a simple frame div */
		function createStitchFrame() {
			return $("<div>", {
				class: "gen-stitch_frame"
			});
		}
		/** set up a drop down menu with the items from the options Array */
		function addSelectMenu($rootElement, menuId, label, optionsArr, valuesArr) {
			var $select = $("<select>", {
				id: menuId
			});
			for (var i = 0; i < optionsArr.length; i++) {
				$option = $("<option>").html(optionsArr[i]);
				if(valuesArr) {
					$option.attr('value',valuesArr[i])
				}
				$select.append($option);
			}
			$rootElement.append($("<label>", {
				for: menuId,
				text: label
			}));
			$rootElement.append($select);
			$select.selectmenu().selectmenu("menuWidget").addClass("gen-selectOverflow");
			return $select;
		}

		/** set up a spinner html with the given id, label, and value
		 * call the spinner() initializer after this method */
		function createSpinnerElem(spinnerId, label, value) {
			return $("<p>")
				.append($("<label>", {
					for: spinnerId,
					text: label
				}))
				.append($("<input>", {
					id: spinnerId,
					name: spinnerId,
					value: value
				}));
		}
		/** set up tabs html with the given id and tab names.
		 * call the tabs() initializer after this method */
		function addTabs($rootElement, tabBaseId, tabNames) {

			var $root = $("<div>", {id: tabBaseId});
			var $ul = $("<ul>").appendTo($root);
			var tabs = [];

			for (var i = 0; i < tabNames.length; i++) {
				$ul.append($("<li>")
					.append($("<a>", {
						href: "#" + tabBaseId + "-" + i,
						text: tabNames[i]
					})));
			}
			for (i = 0; i < tabNames.length; i++) {
				var newTab = $("<div>", {
					id: tabBaseId + "-" + i
				}).appendTo($root);
				tabs.push(newTab);
			}
			$rootElement.append($root);
			$root.tabs();
			return tabs;
		}

		/** creates a labeled fieldset */
		function createFieldset(id, legend) {
			var $fieldset = $("<fieldset>", {id: id});
			if (legend != null) {
				$fieldset.append($("<legend>", {
					text: legend
				}));
			}
			return $fieldset;
		}
		/** set up radio control group, allowing for multiple
		 * options with only one selected at a time */
		function addRadioGroup($rootElement, baseId, legendName, optionNames) {
			var $fieldset = createFieldset(baseId, legendName);
			var opts = [];

			for (var i = 0; i < optionNames.length; i++) {
				var id = "#" + baseId + "-" + i;
				var $label = $("<label>", {
					for: id,
					text: optionNames[i]
				});
				var $input = $("<input>", {
					id: id,
					type: 'radio',
					name: baseId + '-radio',
					value: i
				});
				$fieldset.append($label).append($input);
				opts.push($input);
			}
			$rootElement.append($fieldset);
			$(opts).checkboxradio({icon: false});
			$fieldset.controlgroup();

			return $fieldset;
		}
		/** create a confirmation dialogue box element
		 *  call jquery dialog constructor on the returned div element selector */
		function createConfirmationDialogueElements(title, message) {
			return $("<div>", {
				title: title,
				class: "gen-alert"
			}).append($("<p>", {
				text: message
			}));
		}

		function loadEformData(data) {
			//TODO find a way to merge functions and css stylesheets
			// import custom style elements
			/*var imported_style = $($.parseHTML(data)).filter('style').text();
			 $("#eform_style").html($("#eform_style").html() + imported_style);*/
			// import custom script elements
			/*var imported_script = $($.parseHTML(data)).filter('script').text();
			 $("#eform_script").html($("#eform_script").html() + imported_script);*/

			// remove oscar image paths in incoming data
			data = removeOscarImagePath(data);
			// import the eform name
			eformName = $($.parseHTML(data)).filter('title').text();
			$("#eformNameInput").val(eformName);

			var $div = $(data);
			var imported_form = $div.find("#inputForm").html();

			var $inputForm = $("#inputForm");
			$inputForm.html(imported_form);

			// TODO -- combine with generic makeDraggables and addNewPage
			var $input_elements = $(".input_elements");
			var $pages = $(".page_container");
			$pages.droppable({
				accept: ".gen-layer2, .gen-layer3",
				drop: function (event, ui) {
					dropOnForm(ui, $(this).find(".input_elements"));
				}
			});
			$("#pagesControlGroup").find(".page_control_item").remove();
			$pages.each(function () { //add the grid to loaded pages
				$("#pagesControlGroup").append(createPageControlDiv($(this)));
				addSnapGuidesTo($(this));
			});
			$pages.find(XBOX_INPUT_SELECTOR).parent().append(createInputOverrideDiv());
			$pages.find(CHEK_INPUT_SELECTOR).parent().append(createInputOverrideDiv());
			$pages.find(TEXT_INPUT_SELECTOR).parent().append(createInputOverrideDiv());
			$pages.find(".signature_data").parent().append(createInputOverrideDiv());
			$pages.find(".signaturePad").each(function () {
				makeSignatureCanvas($(this));
			});

			setNoborderStyle($pages.find(XBOX_INPUT_SELECTOR), xboxBordersVisibleState);
			setNoborderStyle($pages.find(TEXT_INPUT_SELECTOR), textBordersVisibleState);

			undestroy_gen_widgets($input_elements);
			dragAndDropEnable(false);

			var pageW = eFormPageWidth;
			var pageH = eFormPageHeight;
			$pages.each(function () {
				pageW = $(this).width();
				pageH = $(this).height();
			});
			$("#gen-setPageWidth").val(pageW);
			$("#gen-setPageHeight").val(pageH);
			//console.info(pageW, pageH);
			var index = 2;
			if (pageW === eFormPageWidthPortrait && pageH === eFormPageHeightPortrait) {
				index = 0;
			}
			else if (pageW === eFormPageWidthLandscape && pageH === eFormPageHeightLandscape) {
				index = 1;
			}
			setPageOrientation(index);
			return true;
		}

		function init_form_load($element) {

		if(!runStandaloneVersion) {
			$.ajax
                ({
                    // populate the eform list from the server
                    type: "GET",
                    url: OSCAR_EFORM_SEARCH_URL,
                    dataType: 'json',
                    async: true,
                    success: function (data) {

                        var status = data.status;
						if(status === "SUCCESS") {
	                        var options = [""];
	                        var values = [0];
	                        var selectedId = 0;
	                        for(var i=0; i<data.body.length; i++) {
	                            options.push(data.body[i].formName);
	                            values.push(data.body[i].id);
	                        }

							var $root = $("<div>", {class: "page_control_item"}).appendTo($element);
	                        var $eFormSelect = addSelectMenu($root, "eFormSelect", "Select EForm", options, values);
	                        $eFormSelect.selectmenu();
	                        var $loadButton = $("<button>", {
                                text: "Load Selected EForm"
                            }).button().click(function (event) {
                                if(selectedId > 0) {
                                    // load the selected eform from the html by id
                                    $.ajax
                                        ({
						                    type: "GET",
						                    url: OSCAR_EFORM_ENTITY_URL + selectedId,
						                    dataType: 'json',
						                    async: true,
						                    success: function (data) {
						                        var status = data.status;
												if(status === "SUCCESS") {
							                        // setup the generator with the existing eform data
							                        loadEformData(data.body.formHtml);
							                        setEformId(data.body.id);
							                        console.info("EForm Loaded from Server");
						                        }
						                        else {
						                            alert(data.error);
						                        }
						                    }
										});
								}
                                event.preventDefault();
                            }).appendTo($root);

	                        $eFormSelect.on("selectmenuchange", ( function (event, data) {
	                            selectedId = data.item.value;
	                        }));
                        }
                    },
                    failure: function(data) {
                        console.error(data);
                    }
                });
		}
		else {
			$element.append($("<input>", {
					type: "file",
					accept: ".html"
				})
					.change(function () {
						if (this.files && this.files[0]) {
							var reader = new FileReader();
							reader.onload = function (e) {
								$.get(e.target.result, function (data) {
									loadEformData(data);
									console.info("EForm Loaded from File.");
								})
							};
							reader.readAsDataURL(this.files[0]);
						}
					})
			).append($("<div>").append(
				$("<label>").text("Note: any custom scripts or styles in the loaded form will not be preserved")
			));
			}
		}
		function addBackgroundImage($parentElement, srcString) {
			var id = getUniqueId(baseBackImageName);
			var $img = $("<img>", {
				id: id,
				class: "gen-layer1"
			}).prependTo($parentElement);
			if (srcString) {
				$img.attr('src', srcString);
			}
			return $img;
		}
		function createPageControlDiv($pageDiv) {

			var $img = $pageDiv.children("img");
			var $root = $("<div>", {class: "page_control_item"});

			var $fileSelector;

			if(runStandaloneVersion) {
				$fileSelector = $("<input>", {
					type: "file",
					accept: ".png"
				}).change(function () {
					if (this.files && this.files[0]) {
						var reader = new FileReader();
						var $fileInput = $(this);
						reader.onload = function (e) {
							var src = $fileInput.val().replace(/C:\\fakepath\\/i, '');
							if ($img == null || $img.length <= 0) {
								$img = addBackgroundImage($pageDiv, src);
							}
							else {
								$img.attr('src', src);
							}
							$img.on('load', function () {
								var css;
								var ratio = $(this).width() / $(this).height();
								var pratio = (eFormPageWidth / eFormPageHeight);
								if (ratio < pratio) css = {width: 'auto', height: '100%'};
								else css = {width: '100%', height: 'auto'};
								$(this).css(css);
							});
						};
						reader.readAsDataURL(this.files[0]);
					}
				});
			}

			var $clearButton = $("<button>", {
				text: "Clear"
			}).button().click(function (event) {
				if ($img != null) {
					$img.remove();
					$img = null;
				}
				event.preventDefault();
			});

			var $removePageButton = $("<button>", {
				text: "Remove Page"
			}).button({
				icon: "ui-icon-circle-minus",
				showLabel: false
			}).click(function (event) {
				var $confirm = createConfirmationDialogueElements(CONFIRM_PAGE_REMOVE_TITLE, CONFIRM_PAGE_REMOVE_MESSAGE);
				$confirm.dialog({
					resizable: false,
					height: "auto",
					width: 400,
					modal: true,
					buttons: {
						"Delete": function () {
							$pageDiv.remove();
							$root.remove();
							$(this).dialog("close");
						},
						"Cancel": function () {
							$(this).dialog("close");
						}
					},
					close: function () {
						$(this).remove();
					}
				});
				event.preventDefault();
			});

			$root.append($removePageButton).append($clearButton);
			if(!runStandaloneVersion) {

                var options = [""];
                for(var i=0; i<eFormImageList.length; i++) {
                    options.push(eFormImageList[i]);
                }

                $fileSelector = addSelectMenu($root, "imageSelect", "Select Background Image", options);
                $fileSelector.selectmenu();

                $fileSelector.on("selectmenuchange", ( function (event, data) {
                    var src = OSCAR_DISPLAY_IMG_SRC + $fileSelector.val();
                    if($fileSelector.val().length < 1) {return;}
					if ($img == null || $img.length <= 0) {
						$img = addBackgroundImage($pageDiv, src);
					}
					else {
						$img.attr('src', src);
					}
					$img.on('load', function () {
						var css;
						var ratio = $(this).width() / $(this).height();
						var pratio = (eFormPageWidth / eFormPageHeight);
						if (ratio < pratio) css = {width: 'auto', height: '100%'};
						else css = {width: '100%', height: 'auto'};
						$(this).css(css);
					});

                }));
            }
			else {
				$root.append($fileSelector);
			}
			return $root;
		}
		function setPageDimensions(newWidth, newHeight) {
			eFormPageWidth = newWidth;
			eFormPageHeight = newHeight;
			$(".page_container").css({width: eFormPageWidth, height: eFormPageHeight});
			$("#eform_container").css({'max-width': eFormPageWidth + 'px'});

			var $wrapper = $("#eform_view_wrapper");
			var maxWidth = eFormPageWidth + eFormViewPadding;
			//console.info(eFormPageWidth, eFormViewPadding, maxWidth);
			$wrapper.resizable("option", "maxWidth", maxWidth);
			if ($wrapper.width() > maxWidth) {
				$wrapper.width(maxWidth);
			}
		}
		function setEformId(id) {
			var asInt = parseInt(id);
			var $saveBtn = $("#saveToOscarButton");
			if(Number.isInteger(asInt) && asInt > 0) {
				eFormFid = asInt;
				$saveBtn.button('option', 'label', OSCAR_SAVE_MESSAGE_UPDATE);
			}
			else {
				eFormFid = 0;
				$saveBtn.button('option', 'label', OSCAR_SAVE_MESSAGE_NEW);
			}
		}
		function setPageOrientation(newIndex) {

			var $custWidth = $("#gen-setPageWidth");
			var $custHeight = $("#gen-setPageHeight");
			var $defaultShow = $("#gen-orientationLabel");

			switch (newIndex) {
				case 2: {
					$custWidth.parent().show();
					$custHeight.parent().show();
					$defaultShow.hide();
					setPageDimensions(parseInt($custWidth.val(), 10), parseInt($custHeight.val()), 10);
					orientationIndex = 2;
					break;
				}
				case 1: {
					$custWidth.parent().hide();
					$custHeight.parent().hide();
					$defaultShow.show();
					setPageDimensions(eFormPageWidthLandscape, eFormPageHeightLandscape);
					orientationIndex = 1;
					break;
				}
				default: {
					$custWidth.parent().hide();
					$custHeight.parent().hide();
					$defaultShow.show();
					setPageDimensions(eFormPageWidthPortrait, eFormPageHeightPortrait);
					orientationIndex = 0;
					break;
				}
			}
			$("#gen-orientation").find("input").filter("[value='" + orientationIndex + "']").prop("checked", true).button("refresh");
			$("#gen-orientation").find("input").checkboxradio("refresh");
		}
		function init_setup_controls($element) {

			if(!runStandaloneVersion) {
                $.ajax
                    ({
                        type: "GET",
                        url: OSCAR_EFORM_SEARCH_URL + 'images',
                        dataType: 'json',
                        async: false,
                        success: function (data) {
	                        var status = data.status;
							if(status === "SUCCESS") {
                                eFormImageList = data.body;
                            }
                        },
                        failure: function(data) {
                            console.error(data);
                        }
                    });
            }


			var $pagesControlgroup = createFieldset("pagesControlGroup", "Pages");
			var $addPageButtonControlGroup = createFieldset("addPagesControlGroup", null);
			var $addPageButton = $("<button>", {
				text: "Add Page"
			}).button({
				icon: "ui-icon-circle-plus"
				//showLabel: false
			}).click(function (event) {
				var $newPage = createNewPage();
				$pagesControlgroup.append(createPageControlDiv($newPage));
				event.preventDefault();
			});

			var $custDimensionX = $("<div>").append($("<label>", {
				text: "Width:",
				for: "gen-setPageWidth"
			})).append($("<input>", {
				id: "gen-setPageWidth",
				type: "text",
				value: eFormPageWidth
			}));
			var $custDimensionY = $("<div>").append($("<label>", {
				text: "Height:",
				for: "gen-setPageHeight"
			})).append($("<input>", {
				id: "gen-setPageHeight",
				type: "text",
				value: eFormPageHeight
			}));
			var $dimensionInputs = $("<div>", {
				class: "flexH"
			}).append($custDimensionX).append($custDimensionY);

			var labels = ["Portrait", "Landscape", "Custom"];
			var $orinetationRadioGroup = addRadioGroup($element, "gen-orientation", "Orientation", labels);
			$orinetationRadioGroup.on("change", function (e) {
				var value = parseInt($(e.target).val());
				setPageOrientation(value);
			});
			$element.append($dimensionInputs);
			// set inital value index
			setPageOrientation(orientationIndex);

			$element.append($addPageButtonControlGroup.append($addPageButton));
			$addPageButtonControlGroup.append($pagesControlgroup);
			$("<div>").append($("<label>", {
				id: "gen-orientationLabel",
				text: "Page Dimensions: " + eFormPageWidthPortrait + "x" + eFormPageHeightPortrait +
				" (Landscape: " + eFormPageWidthLandscape + "x" + eFormPageHeightLandscape + ")"
			})).appendTo($dimensionInputs);

			return $pagesControlgroup;
		}

		/** tab 0 setup */
		function initCheckboxTemplateTab($tab) {
			var $options_menu0 = $("<div>", {
				class: "gen-control-menu"
			});
			var $dragFrame00 = createStitchFrame();
			var $dragFrame01 = createStitchFrame();
			var $checkBoxTemplate = addDraggableInputType($dragFrame00, "checkBoxTemplate", "checkbox", checkboxSize, checkboxSize, "");
			var $xBoxTemplate = addDraggableInputType($dragFrame01, "xBoxTemplate", "text", checkboxSize, checkboxSize, "gen-xBox");
			var $utf8CheckBoxTemplate = addDraggableInputType($dragFrame00, "utf8CheckBoxTemplate", "text", checkboxSize, checkboxSize, "gen-checkbox");
			var $checkBoxTemplateInput = $checkBoxTemplate.find(":input");
			var $xBoxTemplateInput = $xBoxTemplate.find(":input");
			$xBoxTemplateInput.addClass("xBox").attr('autocomplete', 'off');
			var $utf8CheckboxTemplateInput = $utf8CheckBoxTemplate.find(":input");
			$utf8CheckboxTemplateInput.addClass("utf8Check").attr('autocomplete', 'off');
			var $checkboxSizeSpinner = createSpinnerElem("checkboxSizeSpinner", "Template Size:", checkboxSize);
			var changeTemplateSize = function (event, ui) {
				checkboxSize = this.value;
				let fontSize = Math.ceil(checkboxSize * 0.75);
				$checkBoxTemplate.css({width: checkboxSize, height: checkboxSize});
				$xBoxTemplate.css({width: checkboxSize, height: checkboxSize});
				$xBoxTemplate.find(XBOX_INPUT_SELECTOR).css({'font-size': fontSize + 'px'});
				$utf8CheckBoxTemplate.css({width: checkboxSize, height: checkboxSize});
				$utf8CheckBoxTemplate.find(XBOX_INPUT_SELECTOR).css({'font-size': fontSize + 'px'});
			};
			$checkboxSizeSpinner.find(":input").spinner({
				min: checkboxSizeRange[0],
				max: checkboxSizeRange[1],
				stop: changeTemplateSize,
				spin: changeTemplateSize
			});
			var $checkByGenderChkbox = $("<input>", {
				id: "gen-precheckByGender",
				type: "checkbox",
				checked: false
			});
			var $preCheckCheckbox = $("<input>", {
				id: "gen-precheckCheckboxId",
				type: "checkbox",
				checked: false,
				change: function (event, ui) {
					var $xbox = $xBoxTemplate.find(":input");
					var $chkbox = $checkBoxTemplate.find(":input");
					var $utf8box = $utf8CheckBoxTemplate.find(":input");
					$xbox.val($xbox.val() === 'X' ? '' : 'X');
					$utf8box.val($utf8box.val() === '\u2713' ? '' : '\u2713');
					$chkbox.prop('checked', !($chkbox.is(':checked')));
					if ($(this).is(':checked') && $checkByGenderChkbox.is(':checked')) {
						$checkByGenderChkbox.prop('checked', false);
						$checkByGenderChkbox.change();
					}
				}
			});
			$tab.append($options_menu0);
			$tab.append($("<label>", {for: $dragFrame00.attr('id'), text: "\u2713 Box"})).append($dragFrame00);
			$tab.append($("<label>", {for: $dragFrame01.attr('id'), text: "X Box"})).append($dragFrame01);
			$options_menu0.append($checkboxSizeSpinner);
			$options_menu0.append($("<div>").append($("<label>", {
				text: "Pre-Check:",
				for: "gen-precheckCheckboxId"
			})).append($preCheckCheckbox));

			$options_menu0.append($("<div>").append($("<label>", {
				for: "gen-precheckByGender",
				text: "Pre-Check by Gender"
			})).append($checkByGenderChkbox));
			var $div01 = $("<div>").appendTo($options_menu0);
			var $gender_select = addSelectMenu($div01, "genderSelect0", "Check When", ["M", "F", "T", "O", "U"]);
			$gender_select.selectmenu("disable");
			var removeGenderPrecheckClasses = function (index, curclass) {
				return (curclass.match(/(^|\s)gender_precheck_\S+/g) || []).join(' ');
			};
			$gender_select.on("selectmenuchange", ( function (event, data) {
				$checkBoxTemplateInput.removeClass(removeGenderPrecheckClasses).addClass("gender_precheck_" + $gender_select.val());
				$xBoxTemplateInput.removeClass(removeGenderPrecheckClasses).addClass("gender_precheck_" + $gender_select.val());
				$utf8CheckboxTemplateInput.removeClass(removeGenderPrecheckClasses).addClass("gender_precheck_" + $gender_select.val());
			}));
			$checkByGenderChkbox.on('change', function () {
				if ($(this).is(':checked')) {
					$gender_select.selectmenu("enable");
					$checkBoxTemplateInput.removeClass(removeGenderPrecheckClasses).addClass("gender_precheck_" + $gender_select.val());
					$xBoxTemplateInput.removeClass(removeGenderPrecheckClasses).addClass("gender_precheck_" + $gender_select.val());
					$utf8CheckboxTemplateInput.removeClass(removeGenderPrecheckClasses).addClass("gender_precheck_" + $gender_select.val());
					if ($preCheckCheckbox.is(':checked')) {
						$preCheckCheckbox.prop('checked', false);
						$preCheckCheckbox.change();
					}
				}
				else {
					$gender_select.selectmenu("disable");
					$checkBoxTemplateInput.removeClass(removeGenderPrecheckClasses);
					$xBoxTemplateInput.removeClass(removeGenderPrecheckClasses);
					$utf8CheckboxTemplateInput.removeClass(removeGenderPrecheckClasses);
				}
			});
		}
		/** tab 1 setup */
		function initTextboxTemplateTab($tab) {
			var $options_menu1 = $("<div>", {
				class: "gen-control-menu"
			});

			var $dragFrame10 = createStitchFrame().attr('id', 'dragframe1');
			var $dragFrame11 = createStitchFrame().attr('id', 'dragframe11');
			var $textBoxTemplate = addDraggableInputType($dragFrame10, "textBoxTemplate", "text", textBoxWidth, textBoxHeight, "");
			var $textAreaTemplate = addDraggableInputType($dragFrame11, "textAreaTemplate", "textarea", textBoxWidth, textBoxHeight, "");
			var $textSizeSpinnerW = createSpinnerElem("textSizeSpinnerW", "Template Width:", textBoxWidth);
			var onTextSizeSpinnerW = function (event, ui) {
				textBoxWidth = this.value;
				$textBoxTemplate.css({width: textBoxWidth});
				$textAreaTemplate.css({width: textBoxWidth});
			};
			var onTextSizeSpinnerH = function (event, ui) {
				textBoxHeight = this.value;
				$textBoxTemplate.css({height: textBoxHeight});
				$textAreaTemplate.css({height: textBoxHeight});
			};
			$textSizeSpinnerW.find(":input").spinner({
				min: textBoxSizeRange[0],
				max: textBoxSizeRange[1],
				stop: onTextSizeSpinnerW,
				spin: onTextSizeSpinnerW
			});
			var $textSizeSpinnerH = createSpinnerElem("textSizeSpinnerH", "Template Height:", textBoxHeight);
			$textSizeSpinnerH.find(":input").spinner({
				min: textBoxSizeRange[0],
				max: textBoxSizeRange[1],
				stop: onTextSizeSpinnerH,
				spin: onTextSizeSpinnerH
			});

			$tab.append($options_menu1);
			$tab.append($("<label>", {for: $dragFrame10.attr('id'), text: "Single Line Input"})).append($dragFrame10);
			$tab.append($("<label>", {for: $dragFrame11.attr('id'), text: "Multi Line Input"})).append($dragFrame11);

			$options_menu1.append($textSizeSpinnerW)
				.append($textSizeSpinnerH)
				.append($("<div>").append($("<label>", {
					text: "Prefilled Text:",
					for: "gen-textBoxDefaultTextId"
				})).append($("<input>", {
					id: "gen-textBoxDefaultTextId",
					type: "text",
					value: "",
					change: function (event, ui) {
						$textBoxTemplate.find(":input").val($(this).val());
						$textAreaTemplate.find("textarea").text($(this).val());
					}
				}))).append($("<div>").append($("<label>", {
				text: "Placeholder:",
				for: "gen-textBoxPlaceholderId"
			})).append($("<input>", {
				id: "gen-textBoxPlaceholderId",
				type: "text",
				value: "",
				change: function (event, ui) {
					$textBoxTemplate.find(":input").attr('placeholder', ($(this).val()));
					$textAreaTemplate.find("textarea").attr('placeholder', ($(this).val()));
				}
			})));

			/* set up oscar database tag selection */
			var $oscarDbCheckbox = $("<input>", {
				id: "toggleGOscarDbCheckbox",
				type: "checkbox"
			});
			$("<div>").append($("<label>", {
				text: "Use Database Tag:",
				for: "toggleGOscarDbCheckbox"
			})).append($oscarDbCheckbox).appendTo($options_menu1);

			var $div1 = $("<div>").appendTo($options_menu1);
			var $db_tag_select = addSelectMenu($div1, "oscarDbTagSelect", "Select Tag:", getOscarDBTags());
			$db_tag_select.selectmenu("disable");
			$db_tag_select.on("selectmenuchange", ( function (event, data) {
				$textBoxTemplate.find(":input").attr('oscarDB', $db_tag_select.val());
				$textAreaTemplate.find("textarea").attr('oscarDB', $db_tag_select.val());
			}));
			$oscarDbCheckbox.on('change', function (event, ui) {
				if ($(this).is(':checked')) {
					$db_tag_select.selectmenu("enable");
					$textBoxTemplate.find(":input").attr('oscarDB', $db_tag_select.val());
					$textAreaTemplate.find("textarea").attr('oscarDB', $db_tag_select.val());
				}
				else {
					$db_tag_select.selectmenu("disable");
					$textBoxTemplate.find(":input").removeAttr('oscarDB');
					$textAreaTemplate.find("textarea").removeAttr('oscarDB');
				}
			});
		}
		/** tab 2 setup */
		function initLabelTemplateTab($tab) {
			var $options_menu2 = $("<div>", {
				class: "gen-control-menu"
			});
			var $dragFrame2 = createStitchFrame();
			var $labelTemplate = addDraggableLabel($dragFrame2, "textlabelTemplate", "sample text", "label-style_1");

			$tab.append($options_menu2);
			$tab.append($dragFrame2);

			$options_menu2.append($("<label>", {
				text: "Label Text:",
				for: "gen-textLabelValueId"
			})).append($("<input>", {
				id: "gen-textLabelValueId",
				type: "text",
				value: "sample text",
				change: function (event, ui) {
					$labelTemplate.text($(this).val());
				}
			}));
		}
		/** tab 3 setup */
		function initShapeTemplateTab($tab) {
			var $dragFrame1 = createStitchFrame();
			addDraggableShape($dragFrame1, "square", defaultShapeSize, defaultShapeSize, "square");
			addDraggableShape($dragFrame1, "square-rounded", defaultShapeSize, defaultShapeSize, "square-rounded");
			addDraggableShape($dragFrame1, "circle", defaultShapeSize, defaultShapeSize, "circle");
			$tab.append($dragFrame1);
		}
		/** tab 4 setup */
		function initImageTemplateTab($tab) {

			var $dragFrame2 = createStitchFrame();

			if(!runStandaloneVersion) {
                var options = [""];
                for(var i=0; i<eFormImageList.length; i++) {
                    options.push(eFormImageList[i]);
                }

                var $widget = null;

                var $fileSelector = addSelectMenu($tab, "imageSelect2", "Select Image", options);
                $fileSelector.selectmenu();
                $tab.append($dragFrame2);

                $fileSelector.on("selectmenuchange", ( function (event, data) {
                    var src = OSCAR_DISPLAY_IMG_SRC + $fileSelector.val();
                    if($fileSelector.val().length < 1) {return;}

					// remove the old widget
                    if($widget) {$widget.remove();}
                    // create a fake element to load the image (need to get the attributes height/width)
                    var $img = $("<img>", {
                        src: src,
                        hidden: "hidden"
                    }).appendTo($dragFrame2);

					$img.on('load', function () {
                        $widget = addDraggableImage($dragFrame2, getUniqueId(baseImageWidgetName), $(this).width(), $(this).height(), src, "");
                        $img.remove();//remove the fake, not needed now
                    });

                }));
            }
			else {

				$tab.append($("<span>", {
					text: "Images must be in same folder as the generator",
					css: {flex: 1}
				}));

				var $fileSelector2 = $("<input>", {
					type: "file",
					accept: ".png"
				}).change(function () {
					if (this.files && this.files[0]) {
						var reader = new FileReader();
						var $fileInput = $(this);
						reader.onload = function (readerEvt) {
							var img = new Image();
							img.src = readerEvt.target.result;
							var src = $fileInput.val().replace(/C:\\fakepath\\/i, '');
							img.onload = function () {
								console.log(img.width, img.height);
								addDraggableImage($dragFrame2, getUniqueId(baseImageWidgetName), img.width, img.height, src, "");
							}
						};
						reader.readAsDataURL(this.files[0]);

					}
				}).appendTo($tab);
			}
		}
		/** signature tab init */
		function initSignatureTemplateTab($tab) {
			if (!signaturePadLoaded) {
				$tab.append($("<span>", {text: "Missing External Signature Source Code File"}));
				return;
			}
			var $dragFrame1 = createStitchFrame();
			addDraggableSignaturePad($dragFrame1, "signaturePad", 255, 50, "signaturePad");
			$tab.append($dragFrame1);
		}
		function init_input_controls($element) {

			var tabNames = ["Checkbox", "Text Box", "Label", "Shapes", "Images", "Signature"];
			var $tabs = addTabs($element, "control_menu_1-placement-tabs", tabNames);
			/* tab 0 -- Checkbox */
			initCheckboxTemplateTab($tabs[0]);
			/* tab 1 -- Text Box */
			initTextboxTemplateTab($tabs[1]);
			/* tab 2 -- Labels */
			initLabelTemplateTab($tabs[2]);
			/* tab 3 -- Shapes */
			initShapeTemplateTab($tabs[3]);
			/* tab 4 -- Images */
			initImageTemplateTab($tabs[4]);
			/* tab 5 -- Signaure Pad */
			initSignatureTemplateTab($tabs[5]);

			for (var i = 0; i < $tabs.length; i++) {
				$tabs[i].addClass("flexV");
			}

			/* input common footer */
			var $footer = createFieldset("form-building-controls", "Controls")
				.append($("<div>")).append($("<label>", {
					text: "* Hold Alt to enable Draggable Resize"
				}))
				.append($("<div>")).append($("<label>", {
					text: "* Hold Shift when resizing to maintain aspect ratio"
				}))
				.append($("<div>")).append($("<label>", {
					text: "* Use Ctrl+C while mousing over an existing widget, then make copies using Ctrl+V"
				}));
			var $control_fieldset = createFieldset("grid-guide_options", "Guide Options");
			var $trash_box = createTrashFrame();
			$trash_box.append($("<label>").text("Trash"));
			$trash_box.droppable({
				accept: ".ui-draggable",
				hoverClass: "gen-trashHover",
				drop: function (event, ui) {
					var ele = $(ui.draggable);
					if (!ele.hasClass("gen-cloneable")) {
						ui.draggable.remove();
					}
				}
			});
			//$trash_box.appendTo($rootElement);
			var $guideRulerEnabled = $("<div>")
				.append($("<label>", {
					text: "Show Ruler Marks",
					for: "toggleRuler"
				}))
				.append($("<input>", {
					id: "toggleRuler",
					type: "checkbox",
					checked: defaultShowRuler,
					css: {width: "16px", height: "16px"}
				}).change(function () {
					$(".gen-snapGuide").find(".handle").toggleClass("ruler", $(this).is(':checked'));
				}));
			$(".gen-snapGuide").find(".handle").toggleClass("ruler", defaultShowRuler);//initialize
			var $guideToggleCheckbox = $("<div>")
				.append($("<label>", {
					text: "Enable Snap-to Guides",
					for: "toggleSnapGuides"
				}))
				.append($("<input>", {
					id: "toggleSnapGuides",
					type: "checkbox",
					checked: defaultEnableSnapGuides,
					css: {width: "16px", height: "16px"}
				}).change(function () {
					$(".gen-snapGuide").toggle($(this).is(':checked'));
				}));
			$control_fieldset.append($guideToggleCheckbox).append($guideRulerEnabled);

			var $hbox = $("<div>").append($trash_box).append($control_fieldset).append($footer);
			$element.append($hbox);
		}
		function setNoborderStyle($selector, value) {
			if ($selector == null) return false;
			switch (value) {
				case 2:
					$selector.toggleClass("noborder", true).toggleClass("noborderPrint", false);
					break;
				case 1:
					$selector.toggleClass("noborder", false).toggleClass("noborderPrint", true);
					break;
				default:// always visible
					$selector.toggleClass("noborder", false).toggleClass("noborderPrint", false);
					break;
			}
		}
		function init_style_controls($element) {

			var visibilityLables = ["Always Visible", "Invisible on Print", "Always Invisible"];
			var $textHideOptions = addRadioGroup($element, "gen-text-border-radio", "Text Borders", visibilityLables);
			$textHideOptions.on("change", function (e) {
				var value = parseInt($(e.target).val());
				var $selector = $(".input_elements").find(TEXT_INPUT_SELECTOR);
				textBordersVisibleState = value;
				setNoborderStyle($selector, value);
			});
			// set inital value index
			$textHideOptions.find("input").filter("[value='" + textBordersVisibleState + "']").prop("checked", true).button("refresh");

			var $xboxHideOptions = addRadioGroup($element, "gen-xbox-border-radio", "xBox Borders", visibilityLables);
			$xboxHideOptions.on("change", function (e) {
				var value = parseInt($(e.target).val());
				var $selector = $(".input_elements").find(XBOX_INPUT_SELECTOR);
				xboxBordersVisibleState = value;
				setNoborderStyle($selector, value);
			});
			// set inital value index
			$xboxHideOptions.find("input").filter("[value='" + xboxBordersVisibleState + "']").prop("checked", true).button("refresh");
		}
		function init_finalize_controls($element) {
			var $options_menu = $("<div>", {
				class: "gen-control-menu"
			});
			var $toggleFaxControls = $("<input>", {
				id: "toggleFaxControls",
				type: "checkbox",
				css: {width: "16px", height: "16px"},
				checked: defaultIncludeFaxControl
			});
			$options_menu.append($("<div>").append($("<label>", {
					text: "Eform Name",
					for: "eformNameInput"
				})).append($("<input>", {
					id: "eformNameInput",
					type: "text",
					value: eformName,
					change: function (event, ui) {
						eformName = $(this).val();
					}
				}))
			).append($("<div>").append($("<label>", {
					text: "Include Fax Controls",
					for: "toggleFaxControls"
				})).append($toggleFaxControls)
			);
			$element.append($options_menu);
			$element.append($('<button>', {
				id: "showSource",
				text: "View Eform Source",
				click: function (event) {
					showSource($toggleFaxControls.is(':checked'));
					event.preventDefault();
				}
			}).button({icon: "ui-icon-newwin"}))
				.append($('<button>', {
					id: "downloadSource",
					text: "Download As File",
					click: function (event) {
						downloadSource($toggleFaxControls.is(':checked'));
						event.preventDefault();
					}
				}).button({icon: "ui-icon-document"}))
				.append($('<button>', {
					id: "printPreview",
					text: "Print A Preview",
					click: function (event) {
						onEformPrint();
						event.preventDefault();
					}
				}).button({icon: "ui-icon-print"}));
			
			// button for saving directly to oscar eforms
			if(!runStandaloneVersion) {
				$element.append($('<button>', {
					id: "saveToOscarButton",
					text: OSCAR_SAVE_MESSAGE_NEW,
					click: function (event) {
						saveToOscarEforms($toggleFaxControls.is(':checked'));
						event.preventDefault();
					}
				}).button({icon: "ui-icon-disk"}));
			}
		}
		function addSnapGuidesTo($element) {
			var $vertSnapBox = $("<div>", {
				class: "gen-snapGuide",
				height: "100%"
			}).append($("<div>", {
				class: "gen-snapLine vertical"
			}).append($("<p>", {
				class: "handle" //ruler
			}).disableSelection())).appendTo($element);
			var $horzSnapBox = $("<div>", {
				class: "gen-snapGuide",
				width: "100%"
			}).append($("<div>", {
				class: "gen-snapLine horizontal"
			}).append($("<p>", {
				class: "handle" //ruler
			}).disableSelection())).appendTo($element);
			$vertSnapBox.draggable({
				containment: $element,
				handle: "p"
			});
			$horzSnapBox.draggable({
				containment: $element,
				handle: "p"
			});
			$vertSnapBox.toggle(defaultEnableSnapGuides);
			$horzSnapBox.toggle(defaultEnableSnapGuides);
		}
		/** create and return html for a new eform page */
		function createNewPageDiv() {
			return $("<div>", {
				id: getUniqueId(basePageName),
				class: "page_container",
				css: {width: eFormPageWidth, height: eFormPageHeight}
			}).append($("<div>", {
				class: "input_elements"
			}));
		}
		/** add a new page to the eform */
		function createNewPage() {
			var $newpage = createNewPageDiv();
			//TODO better way of appending pages to the form (that won't rely on BottomButtons placement)
			$newpage.insertBefore($("#BottomButtons"));

			$newpage.droppable({
				accept: ".gen-layer2, .gen-layer3",
				drop: function (event, ui) {
					dropOnForm(ui, $(this).find(".input_elements"));
				}
			});
			addSnapGuidesTo($newpage);
			return $newpage;
		}

		function dragAndDropEnable(enable) {
			if (enable) {
				$('.gen-draggable').draggable("enable");
				$('.inputOverride').attr("disabled", false);
			}
			else {
				$('.gen-draggable').draggable("disable");
				$('.inputOverride').attr("disabled", true);
				//make xBoxes work in generator (have to unbind and rebind click events)
				var $input_elements = $(".input_elements");
				$input_elements.find(XBOX_INPUT_SELECTOR).unbind("click");
				initXBoxes($input_elements);
				initUTF8Checkboxes($input_elements);
			}
			dragAndDropEnabled = enable;
		}

		function onKeyUp(e) {

			if (!e.altKey) {
				$('.gen-resizable').resizable("disable");
			}
			if (!e.shiftKey) {
				enableElementHighlights = false;
			}
		}
		function onKeyDown(e) {
			if (e.altKey) {
				$('.gen-resizable').resizable("enable");
				e.preventDefault();
			}
			if (e.shiftKey && !e.altKey) {
				enableElementHighlights = true;
			}

			if (e.ctrlKey && e.which == 67) { //Ctrl C
				var $widget;
				if ($mouseTargetElement.hasClass("gen-widget")) {
					$widget = $mouseTargetElement;
				}
				else {
					$widget = $mouseTargetElement.parent(".gen-widget");
				}
				if ($globalSelectedElement) {
					$globalSelectedElement.removeClass("selectedHighlight");
				}
				if ($widget.length > 0) {
					$globalSelectedElement = $widget;
					$globalSelectedElement.addClass("selectedHighlight");
				}
				else {
					$globalSelectedElement = null;
				}
			}
			else if (dragAndDropEnabled && $globalSelectedElement && $mouseTargetElement && e.ctrlKey && e.which == 86) { //Ctrl V
				// paste an element to the closest input_elements div to mouse target
				var $appendTo;
				if ($mouseTargetElement.hasClass("input_elements")) {
					$appendTo = $mouseTargetElement;
				}
				else {
					$appendTo = $mouseTargetElement.parents(".input_elements");
				}
				if ($appendTo.length > 0) {
					var relativeXPosition = (currentMousePos.x - $appendTo.offset().left);
					var relativeYPosition = (currentMousePos.y - $appendTo.offset().top);
					var pos = {top: relativeYPosition, left: relativeXPosition, position: "absolute"};

					var $clone = cloneWidgetAt($appendTo, pos, $globalSelectedElement);
					$clone.removeClass("selectedHighlight");
				}
			}
			if ($mouseTargetElement && e.which == 46) { //DEL key
				var $toDelete;
				if ($mouseTargetElement.hasClass("gen-widget")) {
					$toDelete = $mouseTargetElement;
				}
				else if ($mouseTargetElement.parents(".gen-widget").length > 0) {
					$toDelete = $mouseTargetElement.parents(".gen-widget");
				}
				if ($toDelete) {
					if ($globalSelectedElement && ($toDelete.is($globalSelectedElement)
						|| $.contains($toDelete.get(0), $globalSelectedElement.get(0)))) {
						$globalSelectedElement.removeClass("selectedHighlight");
						$globalSelectedElement = null;
					}
					$toDelete.remove();
					$mouseTargetElement = null;
				}
			}
		}

		function onContainerMouseMove(e) {

			var elem = e.target || null;
			if ($mouseTargetElement != null) {
				$mouseTargetElement.removeClass("divHighlight");
			}
			if (elem != null) {
				$mouseTargetElement = $(elem);
				if (enableElementHighlights) {
					$mouseTargetElement.addClass("divHighlight");
				}
			}
		}

		function init() {
			/* browser check */
			if (!(inFirefox || inChrome)) {
				$("#main_container").html("This page only works when loaded in Mozilla Firefox or Google Chrome.");
				return false;
			}
			signaturePadLoaded = (typeof SignaturePad !== 'undefined');
			console.info("signature loaded: " + signaturePadLoaded);

			/* set up element positioning */
			var $eform_container = $("#eform_container");
			$("#eform_view_wrapper").resizable({
				handles: "e",
				minWidth: eFormViewMinWidth,
				maxWidth: eFormPageWidthLandscape + 25
			});

			var $page1 = createNewPage();
			//add_background_image($image_frame);
			init_form_load($('#control_menu_1-load'));
			var $pageControl = init_setup_controls($('#control_menu_1-page_setup'));
			$pageControl.append(createPageControlDiv($page1));
			init_input_controls($('#control_menu_1-placement'));
			init_style_controls($('#control_menu_1-stylize'));
			init_finalize_controls($('#control_menu_1-finalize'));
			//init_control_info_bar($('#control'), $eform_container);

			$("#control_menu_1").accordion({
				heightStyle: "content",
				collapsible: true,
				active: defaultMenuOpenIndex,
				activate: function (event, ui) {
					dragAndDropEnable(ui.newPanel.hasClass("gen-allow_drag"));
				}
			});

			$(document).keydown(onKeyDown);
			$(document).keyup(onKeyUp);

			/* In the generator we don't want to print/submit like a normal e-form
			 * so we override the functions here to disable/change their actions within the e-form generator! */
			onEformSubmit = function () {
				alert('This would submit the eform');
				return false;
			};
			onEformPrint = function () {
				write_values_to_html();
				var divToPrint = document.getElementById('eform_container');
				var style1 = document.getElementById('eform_style').innerHTML;
				var style2 = document.getElementById('eform_style_shapes').innerHTML;
				var style3 = document.getElementById('eform_style_signature').innerHTML;
				var newWin = window.open('', 'Print-Window');
				newWin.document.open();
				var htmlPrint = '<html><head><title>' + eformName + '</title><style>' + style1 + style2 + style3 +
					'</style></head><body onload="window.print()">' + divToPrint.innerHTML + '</body></html>';
				newWin.document.write(htmlPrint);
				newWin.document.close();
				var timeout = 1;
				if (inFirefox) {
					timeout = 1000;
				}
				newWin.setTimeout(function () {
					newWin.close();
				}, timeout);
			};
			onEformPrintSubmit = function () {
				onEformPrint();
				alert('This would submit the eform');
				return false;
			};
			$eform_container.mousemove(onContainerMouseMove);
			$(document).mousemove(function (event) {
				currentMousePos.x = event.pageX;
				currentMousePos.y = event.pageY;
			});
		}
	</script>
	<script id="signature_script" class="toSource">
		/** this function is run on page load to make signature pads work. */
		$(function () {
			$(".signaturePad").each(function () {
				var $this = $(this);
				var $canvasFrame = $this.children(".canvas_frame");
				var $clearBtn = $this.children(".clearBtn");
				var canvas = $canvasFrame.children("canvas").get(0);
				var $data = $canvasFrame.children(".signature_data");
				var src = $data.val();
				// the image is needed even when signature pads are loaded for printing/faxing
				var $img = $("<img>", {
					src: src,
					class: "signature_image"
				});
				if (src && src.length > 0) {
					$img.appendTo($canvasFrame);
				}

				// if signature pad loaded correctly and eform viewed on screen
				/* NOTE: media type does not currently work in wkhtmltopdf
				 See https://github.com/wkhtmltopdf/wkhtmltopdf/issues/1737 */
				if (typeof SignaturePad !== 'undefined' && window.matchMedia("screen").matches) {
					console.info("editable signature pad initializing ");
					$img.hide();
					var updateSlaveSignature = function (src_canvas, dest_canvas) {
						// write to the destination with image scaling
						var dest_context = dest_canvas.getContext("2d");
						dest_context.clearRect(0, 0, dest_canvas.width, dest_canvas.height);
						dest_context.drawImage(src_canvas, 0, 0, dest_canvas.width, dest_canvas.height);
					};
					// initialize the signature pad
					var signPad = new SignaturePad(canvas, {
						minWidth: 1,
						maxWidth: 2,
						onEnd: function () {
							$this.trigger("signatureChange");
						}
					});
					// load the image data to the canvas ofter initialization
					if (src != null && src != "") {
						signPad.fromDataURL(src);
					}
					// define a custom update trigger action. this allows the eform to store the signature.
					$this.on("signatureChange", function () {
						$data.val(signPad.toDataURL());
						$img.prop('src', signPad.toDataURL());
						if ($this.attr('slaveSigPad')) {
							var $slavePad = $("#" + $this.attr('slaveSigPad')); // get slave pad by id
							updateSlaveSignature(canvas, $slavePad.find("canvas").get(0));
							$slavePad.trigger("signatureChange"); // be careful of infinite loops
						}
						return false;
					});
					// init the clear button
					$clearBtn.on('click', function () {
						signPad.clear();
						$this.trigger("signatureChange");
						return false;
					});
				}
				// not using the canvas, show signature as an image instead.
				else {
					console.info("static signature pad initializing");
					$img.show();
				}
			});
		});
	</script>
	<script id="eform_script" class="toSource">
		var needToConfirm = false;
		/** call this to prevent closing the window without a confirmation */
		function setDirtyFlag() {
			needToConfirm = true;
		}
		/** call this to prevent the exit confirmation popup when closing the window */
		function releaseDirtyFlag() {
			needToConfirm = false;
		}
		/** call this function on page load to prevent the window from closing
		 * without confirmation when unsaved changes have been made */
		function initConfirmClose() {
			window.addEventListener("beforeunload", function (event) {
				if (needToConfirm) {
					var confirmationMessage = "You have attempted to leave this page. " +
						"If you have made any changes to the fields without clicking the Submit button, " +
						"your changes will be lost. Are you sure you want to exit this page?";
					event.returnValue = confirmationMessage;
					return confirmationMessage;
				}
			});
			$("input").change(function (event) {
				setDirtyFlag();
			});
		}
		/** this function fixes images paths in the eform to allow image use outside of oscar.
		 *  This is useful when developing the eform, but can be removed for final versions */
		function replaceOscarImagePathsWhenLocal($selector) {
			var strLoc = window.location.href.toLowerCase();
			if (strLoc.indexOf("https") == -1) {
				$selector.find("img").not(".signature_data").each(function () {
					this.src = this.src.replace(/\$\%7Boscar_image_path\%7D/, '');
				});
			}
		}
		/** initializes custom x-box input functionality.
		 *  should be called once on eform load */
		function initXBoxes($selector) {
			$selector.find(".xBox").click(function () {
				$(this).val($(this).val() === 'X' ? '' : 'X');
			}).keypress(function (event) {
				// any key press except tab will constitute a value change to the checkbox
				if (event.which != 0) {
					$(this).click();
					return false;
				}
			});
		}
		/** initializes custom checkboxes input functionality using utf-8 character.
		 *  should be called once on eform load */
		function initUTF8Checkboxes($selector) {
			$selector.find(".utf8Check").click(function () {
				$(this).val($(this).val() === '\u2713' ? '' : '\u2713');
			}).keypress(function (event) {
				// any key press except tab will constitute a value change to the checkbox
				if (event.which != 0) {
					$(this).click();
					return false;
				}
			});
		}
		/** pre-check checkboxes and xboxes based on patient gender */
		function initPrecheckedCheckboxes($selector) {
			var $patientGender = $("#PatientGender");
			if ($patientGender) {
				var filter = ".gender_precheck_" + $patientGender.val();
				$selector.find("input[type=checkbox]").filter(filter).prop('checked', true);
				$selector.find(".xBox").filter(filter).val('X');
				$selector.find(".utf8Check").filter(filter).val('\u2713');
			}
		}
		/** This function is called when the eform has been loaded */
		function onEformLoad() {
			var $input_elements = $(".input_elements");
			replaceOscarImagePathsWhenLocal($("#eform_container"));
			initXBoxes($input_elements);
			initUTF8Checkboxes($input_elements);
			initPrecheckedCheckboxes($input_elements);
			initConfirmClose();
		}
		/** This function is called when the print button is clicked */
		function onEformPrint() {
			window.print();
		}
		/** This function is called when the eform submit button is clicked */
		function onEformSubmit() {
			releaseDirtyFlag();
			document.forms[0].submit();
		}
		/** This function is called when the eform print & submit button is clicked */
		function onEformPrintSubmit() {
			onEformPrint();
			releaseDirtyFlag();
			setTimeout('document.forms[0].submit()', 2000);
		}
	</script>
</head>
<body onload="init();">
<div id="main_container">
	<div id="eform_view_wrapper">
		<div id="eform_container" class="toSource">
			<form id="inputForm" action="">
				<div class="DoNotPrint" id="BottomButtons">
					<!-- Form Control Buttons -->
					<label for="subject">Subject:</label>
					<input name="subject" id="subject" size="40" type="text">
					<input value="Submit" name="SubmitButton" id="SubmitButton" type="button" onclick="onEformSubmit();">
					<input value="Print" name="PrintButton" id="PrintButton" type="button" onclick="onEformPrint();">
					<input value="Print & Submit" name="PrintSubmitButton" id="PrintSubmitButton" type="button" onclick="onEformPrintSubmit();">
				</div>
			</form>
		</div>
	</div>
	<div id="control">
		<div id="control_menu_1">
			<h3>Load Existing E-form</h3>
			<div id="control_menu_1-load" class="flexV"></div>
			<h3>Page Setup</h3>
			<div id="control_menu_1-page_setup" class="flexV"></div>
			<h3>Form Building</h3>
			<div id="control_menu_1-placement" class="flexV gen-allow_drag"></div>
			<h3>Form Stylization</h3>
			<div id="control_menu_1-stylize"></div>
			<h3>Finalize</h3>
			<div id="control_menu_1-finalize"></div>
		</div>
	</div>
</div>
</body>
</html>