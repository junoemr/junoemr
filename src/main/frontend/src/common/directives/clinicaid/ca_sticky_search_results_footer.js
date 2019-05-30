angular.module('Common').directive(
	'caStickySearchResultsFooter',

	[ '$document', '$timeout',

	function($document, $timeout)
{
	var scope = {
		eventName: '@caEventName'
	};

	var link_function = function link_function($scope, element, attribute, controller)
	{
		var searchResultsRecordsElement =
			angular.element(element[0].querySelector('.search-results-records'));

		var scrollbarWrapperElement = angular.element(element[0].querySelector('.search-results-scrollbar'));
		var scrollbarElement = angular.element(scrollbarWrapperElement[0].querySelector('.scrollbar'));

		var tableWrapperElement = angular.element(element[0].querySelector('.stickied-table-wrapper'));
		var tableElement = angular.element(tableWrapperElement[0].querySelector('table'));

		var footerElement = angular.element(element[0].querySelector('.search-results-footer'));
		var searchResultsElement = angular.element(element[0].querySelector('.search-results-records'));

		var contentWrapperElement = angular.element($document.find('#center-panel-wrapper'));
		var layoutSidebarElement = angular.element($document.find('#layout-sidebar'));

		// connect the dummy scrollbar in the footer with the table scrollbar
		scrollbarWrapperElement.scroll(function()
		{
			tableWrapperElement.scrollLeft(scrollbarWrapperElement.scrollLeft());
		});
		tableWrapperElement.scroll(function()
		{
			scrollbarWrapperElement.scrollLeft(tableWrapperElement.scrollLeft());
		});

		// resize and position the footer when the specified event is fired
		$scope.$on($scope.eventName, function()
		{
			$timeout($scope.update_search_results_footer);
		});

		$scope.update_search_results_footer = function update_search_results_footer()
		{
			// for a fixed footer, main content must be longer than the sidebar,
			// and the search results must extend beyond the bottom of the screen
			var fixedFooter = (contentWrapperElement.height() >= layoutSidebarElement.height()) &&
				searchResultsElement.offset().top + searchResultsElement.height() > $(window).height();

			if(fixedFooter)
			{
				// fixed footer at bottom of screen, with scrollbar
				footerElement.addClass('fixed').removeClass('static');

				footerElement.css('width', searchResultsRecordsElement.width());

				scrollbarWrapperElement.width(tableWrapperElement.width());
				scrollbarElement.width(tableElement.width());

				if(scrollbarWrapperElement.width() < scrollbarElement.width())
				{
					tableWrapperElement.css('padding-bottom', footerElement.height() - 15);
				}
				else
				{
					tableWrapperElement.css('padding-bottom', footerElement.height());
				}
			}
			else
			{
				// static footer below search results
				footerElement.addClass('static').removeClass('fixed');
				footerElement.css('width', 'initial');
				tableWrapperElement.css('padding-bottom', 0);
			}
		};
	};

	return {
		restrict: 'A',
		scope: scope,
		link: link_function
	};

}]);

