angular.module('Common').directive(
	'caFieldCurrencyFilter',
	[
		'$filter',
		function ($filter)
		{
			var link_function = function link_function(scope, element, attribute, ngModelController)
			{
				ngModelController.$parsers.push(
					function fromUser(text)
					{
						if (text == null)
						{
							text = "0";
						}

						var tempValue = text.replace(/[$,]/g, '');
						if (tempValue.trim() == "")
						{
							tempValue = "0";
						}
						var newValue = parseFloat(tempValue).toFixed(4);
						if (isNaN(newValue))
						{
							newValue = null;
						}
						return newValue;
					});

				ngModelController.$formatters.unshift(
					function toUser(text)
					{
						if (text == null)
						{
							return null;
						}

						var currencyFilter = $filter('myCurrencyFilter');
						var newValue = currencyFilter(text);

						return newValue;
					});
			};

			return {
				require: 'ngModel',
				restrict: 'EAC',
				link: link_function
			};

		}
	]
);
