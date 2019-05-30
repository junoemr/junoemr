angular.module('Common').directive(
	'compileHtml',
	[ '$compile',
	function($compile)
	{
		// this directive is like ng-bind-html but it will angular-compile the content
		return function(scope, element, attrs)
		{
			scope.$watch(
				function(scope)
				{
					return scope.$eval(attrs.compileHtml);
				},
				function(value)
				{
					element.jsp(value);
					$compile(element.contents())(scope);
				}
			)
		};
	}
]);
