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
					minWidth: 490,
					maxHeight: window.innerHeight / 1.5,
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
					$(".note-editor-textarea").css('height', elem.height() - 140);
				});
			}
		};
	}
]);