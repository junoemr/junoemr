angular.module('Common.Directives').directive('resizable', [
	function()
	{
		console.log('RESIZABLE DIRECTIVE LOADED');

		return {
			restrict: 'A',
			scope:
			{
				callback: '&onResize'
			},
			link: function postLink(scope, elem, attrs)
			{

				console.log('RESIZABLE DIRECTIVE HEIGHT ', $(".note-editor-textarea").height());
				elem.resizable(
				{
					handles: 'n, e, w, nw, ne',
					minHeight: 140 + $(".note-editor-textarea").height(),
					maxHeight: window.innerHeight,
					minWidth: 490,
					containment: "#main-body"
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
					// console.log('RIGHT-PANE WIDTH = ', $("#right-pane").width());
					// console.log('MAX HEIGHT = ', window.innerHeight);
					$(".note-editor-textarea").css('height', elem.height() - 140);
				});
			}
		};
	}
]);