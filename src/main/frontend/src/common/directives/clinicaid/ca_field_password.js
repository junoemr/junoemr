// alias to ca_field_text with hideText = true
angular.module('Common').directive(
		'caFieldPassword',
		[
			'fieldHelperService',
			function(helper)
			{
				return helper.textInputDirective(true);
			}
]);

