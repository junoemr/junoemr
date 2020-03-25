angular.module('Common').directive(
	'caFieldText',

	[
		'fieldHelperService',

	function(
		helper
	)
{
	console.log("create modal caFieldText");
	return helper.textInputDirective(false);
}]);

