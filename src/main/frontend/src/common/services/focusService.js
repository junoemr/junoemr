'use strict';

angular.module('Common.Services').factory(
	'focusService',

	[
		'$timeout',
		'$window',

		function (
			$timeout, $window
		)
		{
			var focus = {};

			// focuses element
			focus.element = function element(selector)
			{
				$timeout(function()
				{
					$(selector).focus();
				});
			};

			focus.focusRef = function element(ref)
			{
				$timeout(function()
				{
					ref.focus();
				});
			};

			// focuses first visible and enabled form input
			// if selector is provided, only looks within that selector
			focus.first_form_input = function first_form_element(selector)
			{
				if(!selector)
				{
					selector = "form:visible:first";
				}

				$timeout(function()
				{
					$(selector + " input:enabled, " +
						selector + " select:enabled, " +
						selector + " textarea:enabled ").first().focus();
				});
			};

			// focuses first 'has-error' form input and scrolls to alert
			// if opts['selector'] is provided, only looks within that selector
			// if opts['scroll_offset'] is provided, used in first_alert
			focus.first_error_input = function first_error_input(opts)
			{
				if(!angular.isObject(opts))
				{
					opts = {};
				}
				var selector = opts['selector'] || '';
				$timeout(function()
				{
					$(selector + " .has-error:first input:enabled, " +
						selector + " .has-error:first select:enabled, " +
						selector + " .has-error:first textarea:enabled ").first().focus();
					focus.first_alert(opts);
				});
			};

			// scrolls to the first visible alert (not actually a focus)
			// if modal is open, will just scroll to top of modal
			// if opts['selector'] is provided, only looks within that selector
			// if opts['scroll_offset'] is provided, scrolls to that offset above alert
			focus.first_alert = function first_alert(opts)
			{
				if(!angular.isObject(opts))
				{
					opts = {};
				}
				var selector = opts['selector'] || '';
				var scroll_offset = opts['scroll_offset'];

				if(!scroll_offset || scroll_offset < 0)
				{
					scroll_offset = 10;
				}

				$timeout(function()
				{
					if($('body').hasClass('modal-open'))
					{
						// there's a modal open - scroll to the top
						$('.modal').animate({ scrollTop: 0 });
					}
					else
					{
						// no modal open - scroll to just above the first alert
						$('html, body').animate({
							scrollTop: $(selector + " .alert:visible:first").offset().top - scroll_offset
						}, 500);
					}
				});
			};

			return focus;

		}]);
