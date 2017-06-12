angular.module('Common.Directives').directive('draggable', [
	function()
	{
		console.log('DRAGGABLE DIRECTIVE LOADED');

		return {
			restrict: 'A',
			link: function postLink(scope, elem, attrs)
			{
				elem.draggable(
				{
					axis: "x",
					containment: "#main-body"
				});
			}
		};
	}
]);