
require('jquery-ui/ui/widgets/resizable');

angular.module('Common.Directives').directive('resizable', [
	function()
	{
		return {
			restrict: 'A',
			scope:
			{
				callback: '&onResize'
			},
			link: function postLink(scope, elem, attrs)
			{
				elem.resizable(
				{
					handles: 'n, nw, ne',
					minHeight: 250,
					minWidth: 510,
					maxWidth: window.innerWidth,
					maxHeight: window.innerHeight / 1.5,
					// containment: "#main-body"
				});
				elem.on('resizestop', function(evt, ui)
				{
					if (scope.callback)
					{
						scope.callback();
					}

				});
				elem.on('resize', function()
				{
					$(".note-editor-textarea").css('height', elem.height() - 145 - $(".note-editor-issues").height());
				});

				// Workaround, need to either replace JqueryUI or find a better way to account for dynamic changes to resizable div size
				// Doesn't seem to be an easy way to dynamically set minHeight appropriately
				scope.$on('noteIssueAdded', function()
				{
					var newHeight = $('#note-editor').height() + 33;
					$('#note-editor').height(newHeight);
				});

				// Workaround, need to either replace JqueryUI or find a better way to account for dynamic changes to resizable div size
				// Doesn't seem to be an easy way to dynamically set minHeight appropriately
				scope.$on('noteIssueRemoved', function()
				{
					var newHeight = $('#note-editor').height() - 33;
					$('#note-editor').height(newHeight);
				});


			}
		};
	}
]);