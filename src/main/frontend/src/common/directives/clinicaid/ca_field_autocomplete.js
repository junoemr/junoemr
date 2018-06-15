angular.module('Common').directive(
	'caFieldAutocomplete',

	[
		'$q',
		'globalStateService',
		'$timeout',
		'fieldHelperService',
		'autoCompleteService',
		'resultsService',
		'focusService',

	function($q, global_state, $timeout, helper, autocomplete, results_factory, focus)
{
	var min_length_zero_types = [
		'yes_no_boolean',
		'gender',
		'schedule',
		'schedule_type',
		'schedule_template',
		'schedule_event_status',
		'user_roles',
		'user_type',
		'province',
		'country',
		'custom_tax',
		'alberta_location_code',
		'alberta_default_time_role_modifier',
		'service_modifier',
		'ontario_office_code',
		'ontario_service_location_indicator',
		'bc_service_location_code',
		'bc_referral_code',
		'sask_clinic',
		'sask_oop_province',
		'sask_mode',
		'sask_location_of_service',
		'sask_location_of_service_short',
		'sask_corporation_indicator',
		'sask_submission_type',
		'sask_submission_file',
		'provider' ];

	var scope = angular.copy(helper.default_scope);

	scope.placeholder = '@caPlaceholder';
	scope.input_group_class = '@caInputGroupClass';

	// must be a valid generic_ref type
	scope.autocomplete_type = '@caAutocompleteType';

	// Extra params to pass in the REST call
	scope.params = '=caParams';
	scope.params_fn = '&caParamsFn';

	// the on-select function to call, and if applicable, additional data to pass into it
	scope.on_select_fn = '&caOnSelect';
	scope.on_select_data = '=caOnSelectData';

	// the clear function to call, and if applicable, data to pass into it
	scope.on_clear_fn = '&caClear';
	scope.on_clear_data = '=caClearData';

	// the create function to call
	// create option will only appear if function is provided and create_if is met (if also provided)
	// and there is no selected autocomplete value
	scope.on_create_fn = '&caCreate';
	scope.create_if = '=caCreateIf';

	// the modify function to call
	// modify option will only appear if function is provided and modify_if is met (if also provided)
	// and there is a selected autocomplete value
	scope.on_modify_fn = '&caModify';
	scope.modify_if = '=caModifyIf';
	scope.create_on_not_found = '=?caCreateOnNotFound';
	scope.editable = '=?caEditable';

	// List of records to exclude from the results. EG These items are already linked
	// Must be an array of pojo models
	scope.ca_exclude_list = '=caExcludeList';

	var template_function = function template_function(element, attributes) {
		return helper.resolve_template(attributes, 'code/common/directives/ca_field_autocomplete');
	};

	var autocomplete_link_function = function autocomplete_link_function(
		$scope, element, attribute, controller)
	{
		$scope.autocomplete = autocomplete;
		$scope.search_field = null;

		if(!angular.isDefined($scope.model))
		{
			$scope.model = null;
		}

		if(!Juno.Common.Util.exists($scope.create_on_not_found))
		{
			$scope.create_on_not_found = false;
		}

		if(!Juno.Common.Util.exists($scope.editable))
		{
			$scope.editable = false;
		}

		// ensure autocomplete type is either a listed dao or defined in GenericRef
		if(Object.keys($scope.autocomplete.dao_map).indexOf(
					$scope.autocomplete_type) == -1 &&
				Object.keys(Clinicaid.Model.GenericRef.REF_DEFINITIONS).indexOf(
					$scope.autocomplete_type) == -1)
		{
			console.log("invalid autocomplete type:", $scope.autocomplete_type,
				"valid values: (" ,
					Object.keys($scope.autocomplete.dao_map),
					Object.keys(Clinicaid.Model.GenericRef.REF_DEFINITIONS));
			return;
		}

		// default min-length is 1 except for some types
		$scope.autocomplete_min_length =
			(min_length_zero_types.indexOf($scope.autocomplete_type) != -1) ? 0 : 1;

		// use the passed-in placeholder or default to 'Search...'
		$scope.input_placeholder = $scope.placeholder;
		if(!angular.isDefined($scope.input_placeholder))
		{
			$scope.input_placeholder = 'Search...';
		}

		// clear function:
		// call the passed-in function with the passed-in data
		$scope.clear_autocomplete_search = function clear_autocomplete_search()
		{
			$scope.search_field = null;
		};

		$scope.clear_autocomplete_model = function clear_autocomplete_model(focus_input)
		{
			$scope.model = null;
			if(angular.isFunction($scope.on_clear_fn()))
			{
				$scope.on_clear_fn()($scope.on_clear_data);
			}

			if(focus_input)
			{
				focus.element(element.find('input'));
			}
		};

		// select function:
		// call the passed-in function with the autocomplete and passed-in data
		$scope.on_select = function on_select($item, $model, $label, $event)
		{
			$scope.model = $item;
			$scope.change_fn();
			if(angular.isFunction($scope.on_select_fn()))
			{
				$scope.on_select_fn()($item, $model, $label, $event, $scope.on_select_data);
			}
		};

		$scope.create_on_not_found_fn = function create_on_not_found_fn(temp_model)
		{
			if(Juno.Common.Util.exists(temp_model))
			{
				$scope.autocomplete.create_on_not_found(
						temp_model, $scope.autocomplete_type, true).then(
						function success(results)
						{
							$scope.on_select(results.data, results.data, null, null, $scope.on_select_data);
							if (angular.isFunction($scope.change_fn))
							{
								$scope.change_fn();
							}
						}, function error(errors)
						{
							console.log('error:', errors);
						}
				);
			}
		};

		$scope.on_change = function on_change()
		{
			if(!$scope.editable)
			{
				$scope.clear_autocomplete_model();
				return;
			}

			var temp_model = $scope.autocomplete.build_model_object(
					$scope.autocomplete_type, $scope.search_field);

			$scope.on_select(temp_model, temp_model, null, null, $scope.on_select_data);

			if ($scope.create_on_not_found)
			{
				$scope.create_on_not_found_fn(temp_model);
			}
		};

		// blur function
		$scope.on_blur = function on_blur()
		{
			// this is for the case when the user does a search and then blurs the field:
			// if the value is null, call the clear function (since the select function doesn't fire)
			if($scope.model == null)
			{
				$scope.clear_autocomplete_model();
				$scope.clear_autocomplete_search();

				// only run the change function if the model existed when the autocomplete was last focused
				if(Juno.Common.Util.exists($scope.model_on_focus) &&
					angular.isFunction($scope.change_fn))
				{
					$scope.change_fn();
				}
			}

			// call the passed-in function, if provided
			if (angular.isFunction($scope.blur_fn))
			{
				$scope.blur_fn();
			}
		};

		// on focus
		$scope.on_focus = function on_focus()
		{
			// make a copy of the current model (needed for comparison on_blur)
			$scope.model_on_focus = angular.copy($scope.model);

			// call the passed-in function, if provided
			if(angular.isFunction($scope.focus_fn))
			{
				$scope.focus_fn();
			}
		};

		// create function: call the passed-in function, if provided
		$scope.on_create = null;
		if(angular.isFunction($scope.on_create_fn()))
		{
			$scope.on_create = function on_create()
			{
				$scope.on_create_fn()();
			};
		}

		// modify function: call the passed-in function, if provided
		$scope.on_modify = null;
		if(angular.isFunction($scope.on_modify_fn()))
		{
			$scope.on_modify = function on_modify()
			{
				$scope.on_modify_fn()();
			};
		}
		$scope.show_modify_if = (typeof $scope.modify_if === 'undefined' || $scope.modify_if);

		// autocomplete search: invoke configured function
		$scope.autocomplete_items = function autocomplete_items($viewValue)
		{
			var deferred = $q.defer();
			var params = $scope.params ? $scope.params : {};
			if(angular.isFunction($scope.params_fn))
			{
				// If the params_fn definition has arguments, $scope.params_fn() with return the results.
				// If it doesnt have armuments it returns a reference to the funciton which still needs
				// to be executed to get the results
				var params_check = $scope.params_fn();
				if(angular.isFunction(params_check))
				{
					params_check = params_check();
				}
				angular.extend(params, params_check);
			}

			autocomplete.get_items($scope.autocomplete_type, $viewValue, params).then(
					function success(results)
					{
						var out_items = results.data;
						if(Juno.Common.Util.exists($scope.ca_exclude_list))
						{
							out_items = $scope.prune_items(out_items);
						}
						deferred.resolve(out_items);
					}, function error(errors)
					{
						deferred.reject(errors)
					}
			);
			return deferred.promise;
		};

		$scope.prune_items = function prune_items(items)
		{
			var out_items = [];
			for(var i = 0; i < items.length; i++)
			{
				var in_list = false;
				for(var ii = 0; ii < $scope.ca_exclude_list.length; ii++)
				{
					var exclude_item = $scope.ca_exclude_list[ii];
					if(typeof(exclude_item) == 'string')
					{
						if(items[i].data[items[i].value_field] == exclude_item)
						{
							in_list = true;
						}
					} else
					{
						if(items[i].data[items[i].value_field] ==
								exclude_item.data[$scope.ca_exclude_list[ii].value_field])
						{
							in_list = true;
						}
					}
				}
				if(!in_list)
				{
					out_items.push(items[i]);
				}
			}
			return out_items;
		};

		// autocomplete format: format the selection for display in the box
		$scope.autocomplete_input_formatter = function autocomplete_input_formatter($model)
		{
			return $model != null ? $model.autocomplete_input_formatted : null;
		};

		// show the create button?
		$scope.show_create = function show_create()
		{
			return $scope.on_create && !$scope.model &&
				(typeof $scope.create_if === 'undefined' || $scope.create_if);
		};

		// show the modify button?
		$scope.show_modify = function show_modify()
		{
			return $scope.on_modify && $scope.model && !$scope.model.dummy_record &&
				(typeof $scope.modify_if === 'undefined' || $scope.modify_if);
		};

		// show the buttons?
		$scope.show_buttons = function show_buttons()
		{
			return !$scope.disabled && ($scope.model || $scope.show_create());
		};

		// required for initialization of the search field value:
		// the directive is created before the model is populated
		$scope.$watch('model', function(new_value, old_value)
		{
			$scope.search_field = $scope.model;
		});

		// run the default field link function
		helper.default_link_function($scope, element, attribute, controller);
	};

	return {
		restrict: 'E',
		scope: scope,
		templateUrl: template_function,
		replace: true,
		link: autocomplete_link_function,
		controller: helper.default_controller
	};

}]);
