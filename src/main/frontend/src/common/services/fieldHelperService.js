'use strict';

angular.module('Common.Services').factory(
	'fieldHelperService',
	[
		'$timeout',

		function(
			$timeout
		)
		{
			var helper = {};

			helper.default_scope = {

				form_group_class: '@caFormGroupClass',
				label_size: '@caLabelSize',
				input_size: '@caInputSize',
				text_length: '@caTextLength',
				hide_label_colon: '@caTitleNoColon',
				template: '@caTemplate',

				name: '@caName',

				title: '@caTitle',
				hint: '@caHint',

				tab_index: '@caTabindex',

				error_message: '@caError',
				warning_message: '@caWarning',

				depends_on_field: '@caDependsOnField',
				depends_on_value: '@caDependsOnValue',

				model: '=caModel',
				disabled: '=caDisabled',
				hide: '=caHide',
				focus_field: '=caFocusField',

				on_focus_fn: '&caFocus',
				change_fn: '&caChange',
				blur_fn: '&caBlur'
			};

			helper.resolve_template = function(attributes, base_default_template)
			{
				// if tag has a ca-template on it, add it to the template path
				if(Juno.Common.Util.exists(attributes['caTemplate']))
				{
					return base_default_template + '_' + attributes['caTemplate'] + '.jsp';
				}
				return base_default_template + '.jsp';
			};

			helper.merge_title_messages = function merge_title_messages($scope)
			{
				// Merge error and warning records so they both can be displayed in the hover over
				$scope.title_array = [];
				if(Juno.Common.Util.exists($scope.error_message))
				{
					$scope.title_array.push($scope.error_message);
				}
				if(Juno.Common.Util.exists($scope.warning_message))
				{
					$scope.title_array.push($scope.warning_message);
				}

				if($scope.title_array.length == 0 && Juno.Common.Util.exists($scope.hint))
				{
					$scope.title_array.push($scope.hint);
				}
				$scope.title_string = $scope.title_array.join('|');
			};

			helper.default_link_function = function default_link_function(
				$scope, element, attribute, controller)
			{
				helper.merge_title_messages($scope);
				$scope.focus_fn = function on_focus()
				{
					if(angular.isFunction($scope.on_focus_fn))
					{
						// only call the focus function if the field still has focus after a timeout
						$timeout(function() {
							if(($(element).is("input:enabled, select:enabled, textarea:enabled") &&
								$(element).is(':focus')) ||
								$(element).find(" input:enabled, select:enabled, textarea:enabled ").is(':focus'))
							{
								$scope.on_focus_fn();
							}
							else
							{
								console.log('ignoring on focus function - element is no longer in focus', $scope.name)
							}
						});
					}
				};

				// watch the focus field: when it matches the name, focus the element
				$scope.$watch('focus_field', function()
				{
					if(Juno.Common.Util.exists($scope.name) &&
						$scope.focus_field == $scope.name)
					{
						// Focus and select the input element
						$timeout(function()
						{
							if($(element).is("input:enabled, select:enabled, textarea:enabled"))
							{
								$(element).focus().select();
							}
							else
							{
								$(element).find(
									"input:enabled, select:enabled, textarea:enabled").first().focus().select();
							}
						});
					}
				});

				// listen for focusCaFocusField event: when it matches, focus the element
				$scope.$on('focusCaFocusField', function(e, focus)
				{
					if(Juno.Common.Util.exists($scope.name) && $scope.name == focus)
					{
						if($(element).is("input:enabled, select:enabled, textarea:enabled"))
						{
							$(element).focus().select();
						}
						else
						{
							$(element).find(
								"input:enabled, select:enabled, textarea:enabled").first().focus().select();
						}
					}
				});
			};

			helper.default_controller = ['$scope', '$element', '$attrs', function default_controller($scope, $element, $attrs)
			{
				// These field directives have a built-in handling of the focus field,
				// so this tells the caFocusField directive to ignore them.
				this.ignore_focus_field = function ignore_focus_field()
				{
					return true;
				}
			}];

			return helper;
		}]);
