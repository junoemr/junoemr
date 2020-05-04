angular.module('Common').directive(
	'caFieldText',

	[
		'fieldHelperService',

	function(
		helper
	)
{
	return helper.textInputDirective(false);
}]);

