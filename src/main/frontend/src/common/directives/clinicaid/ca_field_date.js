 angular.module('Common').directive(
 'caFieldDate',

 [
	 '$compile',
	 '$timeout',
	 'fieldHelperService',

	 function(
		 $compile,
		 $timeout,
		 helper
	 )
 {
	 var scope = angular.copy(helper.default_scope);

	 scope.date_picker_id = '@caDatePickerId';
	 scope.orientation = '@caOrientation';
	 scope.use_multi_date = '@caUseMultiDate';
	 scope.container_id = '@caContainerId';
	 scope.no_label = '@caNoLabel';

	 var template_function = function template_function(element, attributes) {
		 if(attributes['caTemplate'] == 'bare')
		 {
			 return 'src/common/directives/clinicaid/ca_field_date_bare.jsp';
		 }
		 return 'src/common/directives/clinicaid/ca_field_date.jsp';
	 };

	 var link_function = function link_function($scope, element, attribute, controller)
	 {
		 // Creating the datepicker changes the value of the ng-model and fires the
		 // ng-change event. This disables the event until the field initialization is complete
		 $scope.initialization_complete = false;
		 $scope.delayed_change_fn = function()
		 {
			 if($scope.initialization_complete === true)
			 {
				 $scope.change_fn();
			 }
		 };

		 helper.merge_title_messages($scope);

		 $scope.datepicker = null;
		 $scope.is_multi_date = false;
		 $scope.CTRL_KEY = 17;
		 $scope.LEFT_SHIFT_KEY = 16;
		 $scope.ctrl_key_down = false;
		 $scope.shift_key_down = false;
		 $scope.is_visible = false;

		 // Default container for datepicker is "body", but can be overridden
		 $scope.container = $("body");
		 if(!$scope.container_id)
		 {
			 $scope.container = $("#" + $scope.container_id);
		 }

		 var buttons_html_with_picker = "<tfoot><tr>" +
				 "<th style='display: table-cell;' ng-class=\"{active: is_multi_date}\" ng-click='toggle_multi()' colspan='2' class='multi'>Multi</th>" +
				 //"<th style='display: table-cell;' colspan='3' class='today'>Today</th>" +
				 "<th style='display: table-cell;' ng-click='close_date_picker()' colspan='3' class='multi'>Close</th>" +
					 //	"class='close'>Close</th></tr></tfoot>";
				 "<th style='display: table-cell;' colspan='2' class='clear'>Clear</th></tr>";

		 var buttons_html = "<tfoot><tr>" +
				 "<th style='display: table-cell;' colspan='4' class='today'>Today</th>" +
				 "<th style='display: table-cell;' colspan='3' class='clear'>Clear</th></tr>";
				 //"<tr class='close'><th style='display: table-cell;' ng-click='close_date_picker()' colspan='7' " +
		 			//	"class='close'>Close</th></tr></tfoot>";

		 $scope.create_datepicker = function create_datepicker()
		 {
			 // Build datepicker. By default put it in the <body> tag so that it is
			 // placed correctly and is always on top. They container can be overridden
			 // though.
			 if($scope.container_id == null)
			 {
				 $scope.datepicker.datepicker({
					 multidate: $scope.is_multi_date,
					 autoclose: !$scope.is_multi_date,
					 todayHighlight: true,
					 todayBtn: 'linked',
					 clearBtn: true,
					 toggleActive: $scope.is_multi_date,
					 orientation: $scope.orientation,
					 format: 'yyyy-mm-dd',
					 showOnFocus: false,
					 keyboardNavigation: false
				 });
			 }
			 else
			 {
				 $scope.datepicker.datepicker({
					 multidate: $scope.is_multi_date,
					 autoclose: !$scope.is_multi_date,
					 todayHighlight: true,
					 todayBtn: 'linked',
					 clearBtn: true,
					 toggleActive: $scope.is_multi_date,
					 orientation: $scope.orientation,
					 format: 'yyyy-mm-dd',
					 container: '#' + $scope.container_id,
					 showOnFocus: false,
					 keyboardNavigation: false
				 });

			 }

			 $scope.datepicker.datepicker().on('show', function ()
			 {
				 $scope.is_visible = true;
			 });

			 $scope.datepicker.datepicker().on('hide', function ()
			 {
				 $scope.is_visible = false;
			 });
		 };

		 $scope.toggle_multi = function toggle_multi(force_true, input_string)
		 {
			 if($scope.use_multi_date == 'true')
			 {
				 $scope.is_multi_date = force_true || !$scope.is_multi_date;
				 $scope.datepicker.datepicker('remove');
				 $scope.create_datepicker();
				 $scope.datepicker.datepicker('show');

				 // Datepicker forces valid dates on the input box when the datepicker opens. This breaks
				 // manual entry when you istart to enter a second date and auto switch to multi date mode.
				 // We put the date string back in after the parse occurs, to account for this
				 if(input_string && input_string.length > 0)
				 {
					 element.find("input").val(input_string);
				 }
			 }
		 };

		 // We assume the client is done picking multiple dates when they release control
		 // Close the datepicker
		 $scope.key_up = function key_up(event)
		 {
			 if ($scope.use_multi_date == 'true')
			 {
				 // Switch to multi-date if a date seperator (comma) is entered
				 if (!$scope.is_multi_date)
				 {
					 var input_value = element.find("input").val();
					 if (input_value.search(",") != -1)
					 {
						 $scope.toggle_multi(true, input_value);
					 }
				 }

				 if (event.keyCode == $scope.CTRL_KEY)
				 {
					 $scope.close_date_picker(event);
					 $scope.ctrl_key_down = false;
				 }
				 else if (event.keyCode == $scope.LEFT_SHIFT_KEY)
				 {
					 $scope.close_date_picker(event);
					 $scope.shift_key_down = false;
				 }
			 }
		 };

		 // Switch to multi-date mode when control or shift is pressed
		 $scope.key_down = function key_down(event)
		 {
			 if ($scope.use_multi_date == 'true')
			 {
				 // Switch to multi-date when ctrl is pressed. For selecting arbitrary dates
				 if ($scope.ctrl_key_down)
				 {
					 return;
				 }

				 if (event.keyCode == $scope.CTRL_KEY)
				 {
					 $scope.ctrl_key_down = true;
					 $scope.toggle_multi(true);
				 }
				 // Switch to multi-date when shift. For selecting date ranges. NOT IMPLEMENTED
				 else if (event.keyCode == $scope.LEFT_SHIFT_KEY)
				 {
					 $scope.shift_key_down = true;
					 $scope.toggle_multi(true);
				 }
			 }
		 };

		 $scope.on_focus = function on_focus(event)
		 {
			 // Prevent toggling datepicker on click and on focus
			 if(!$scope.is_visible)
			 {
				 $scope.open_date_picker(event);
			 }

			 $scope.focus_fn();
		 };

		 $scope.on_click = function on_click(event)
		 {
			 // Toggle datepicker open/closed
			 if(!$scope.is_visible)
			 {
				 $scope.open_date_picker(event);
			 }
		 };

		 $scope.open_date_picker = function open_date_picker(event)
		 {
			 var dates = element.find("input").val().split(',');
			 var new_is_multi_date = false;

			 // Use whatever dates are in the input box when opening datepicker
			 if(dates && dates.length > 1)
			 {
				 new_is_multi_date = true;
			 }

			 $scope.datepicker.datepicker('setDates', dates);

			 // Switch between multi date and single date picker depending on number of dates in
			 // input box
			 if($scope.use_multi_date == 'true' && new_is_multi_date != $scope.is_multi_date)
			 {
				 $scope.toggle_multi();
			 }
			 else
			 {
				 $scope.datepicker.datepicker('show');
			 }
		 };

		 $scope.close_date_picker = function close_date_picker(event)
		 {
			 $scope.datepicker.datepicker('hide');
		 };

		 // wrap focus function the same way as the field_helper
		 $scope.focus_fn = function on_focus()
		 {
			 if(angular.isFunction($scope.on_focus_fn))
			 {
				 // only call the focus function if the field still has focus after a timeout
				 $timeout(function() {
					 if($(element).find(" input:enabled, select:enabled, textarea:enabled ").is(':focus'))
					 {
						 $scope.on_focus_fn();
					 }
					 else
					 {
						 console.log('ignoring on focus function - element is no longer in focus', $scope.name)
					 }
				 });
			 }
		 };

		 $scope.$watch('focus_field', function()
		 {
			 if($scope.focus_field == $scope.name)
			 {
				 var input_box = element.find("input");
				 // Focus and select the input element
				 $timeout(function()
				 {
					 if(!input_box.is(':focus'))
					 {
						 input_box.focus();
					 }
					 input_box.select();
				 });
			 }
		 });

		 // Initial Datepicker setup
		 $timeout( function() {
			 var date_picker_id = attribute.caDatePickerId;
			 if(!date_picker_id)
			 {
				 date_picker_id = 'ca-date-picker';
			 }
			 $scope.datepicker = $('#' + date_picker_id).find('input');
			 $scope.create_datepicker();

			 $scope.datepicker.on('show', function (e)
			 {
				 // Show multi button only if this is a multi-date picker
				 var dropdown = $(".datepicker.datepicker-dropdown");
				 var footer = dropdown.find('tfoot');
				 if ($scope.use_multi_date == 'true')
				 {
					 footer.replaceWith(buttons_html_with_picker);
				 }
				 else
				 {
					 footer.replaceWith(buttons_html);
				 }
				 footer = dropdown.find('tfoot');
				 $compile(footer.contents())($scope);

				 // replace next/prev buttons with custom font icons
				 dropdown.find(".next").html("").addClass("icon-arrow-right");
				 dropdown.find(".prev").html("").addClass("icon-arrow-left");

				 $scope.initialization_complete = true;
			 })
		 });
	 };

	 return {
		 restrict: 'EA',
		 scope: scope,
		 replace: true,
		 templateUrl: template_function,
		 link: link_function,
		 controller: helper.default_controller
	 };

 }]);
